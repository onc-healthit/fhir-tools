package org.sitenv.spring;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.sitenv.spring.model.AllergyIntoleranceResource;
import org.sitenv.spring.model.ClaimResource;
import org.sitenv.spring.model.ConditionResource;
import org.sitenv.spring.model.EncounterResource;
import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.MedicationStatementResource;
import org.sitenv.spring.model.ObservationResource;
import org.sitenv.spring.model.PatientResource;
import org.sitenv.spring.service.AllergyIntoleranceResourceService;
import org.sitenv.spring.service.ClaimResourceService;
import org.sitenv.spring.service.ConditionResourceService;
import org.sitenv.spring.service.EncounterResourceService;
import org.sitenv.spring.service.ExtractionTaskService;
import org.sitenv.spring.service.MedicationStatementResourceService;
import org.sitenv.spring.service.ObservationResourceService;
import org.sitenv.spring.service.PatientResourceService;
import org.sitenv.spring.util.CommonUtil;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.resource.Claim;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Controller
@RequestMapping("/extract")
@PropertySource(value = { "classpath:application.properties" })
public class ExtractionController {
	
	@Autowired
	private ExtractionTaskService etService;

	@Autowired
	private PatientResourceService patientService;

	@Autowired
	private AllergyIntoleranceResourceService allergyService;

	@Autowired
	private ConditionResourceService conditionService;

	@Autowired
	private MedicationStatementResourceService medStatementService;

	@Autowired
	private EncounterResourceService encounterService;

	@Autowired
	private ClaimResourceService claimService;

	@Autowired
	private ObservationResourceService observationService;
	
	@Autowired
    private Environment environment;


	@Scheduled(cron = "*/5 * * * * ?")
	public void schedularTaskToExtractData() throws Exception {
		System.out.println("Schedular checking for pending requests...");
		List<ExtractionTask> etList = etService.getExtractionTasksByProcessFlag(false);
		for (ExtractionTask et : etList) {
			
			String accessToken = null;
			if(et.getAuthenticationMode()!=null && et.getAuthenticationMode().equalsIgnoreCase("secure")) {
	        	accessToken = getAccessToken(null);
	        }
			// Request to the Content Location
			if(et.getContentLocation()!=null)
				pollRequestToContentLocation(et,accessToken);

		}

	}

	public void pollRequestToContentLocation(ExtractionTask et, String accessToken)
			throws ClientProtocolException, IOException, ParseException {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet bulkRequest = new HttpGet(et.getContentLocation());

			// add request header
			bulkRequest.addHeader("Prefer", "respond-async");
			
			 if (accessToken != null) {
				 bulkRequest.addHeader("Authorization", "bearer " + accessToken); 
			 }
			 
			HttpResponse bulkResponse = client.execute(bulkRequest);

			BufferedReader rd = new BufferedReader(new InputStreamReader(bulkResponse.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			Header linksHeader = bulkResponse.getFirstHeader("Link");

			if (linksHeader != null) {
				et.setLinks(linksHeader.getValue());
				et.setResponseBody(result.toString());
				et.setProcessFlag(true);
				// et.setStatus("Completed");
				etService.saveOrUpdate(et);
				// Download Bulk Data files
				downloadBulkDataFiles(et);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloadBulkDataFiles(ExtractionTask et) throws MalformedURLException, IOException, ParseException {

		String links = et.getLinks();

		if (links != null) {
			String[] linksArray = links.replaceAll("[<>]", "").split(",");
			String contextPath = System.getProperty("catalina.base");
			String fileDir = contextPath + "/Extraction Data/" + et.getExtractionTaskId() + "/";
			if (!new File(fileDir).exists()) {
				new File(fileDir).mkdirs();
			}
			for (String link : linksArray) {
				String[] splitArray = link.split("/");
				String fileName = splitArray[splitArray.length - 1];
				File file = new File(fileDir + fileName);

				FileUtils.copyURLToFile(new URL(link), file);
				System.out.println(fileDir + fileName + " downloaded...!");

				String resourceName = getResourceNameWithLink(link);
				// Read the file and load data to the mart
				if (file.exists() && file.length() > 0) {
					readFilesAndLoadDataMart(file, resourceName, et);
				} else {
					System.out.println(file.getName() + " is empty...!");
				}
			}
		}
	}

	public void readFilesAndLoadDataMart(File file, String resourceName, ExtractionTask et) {
		try {
		FhirContext ctx = FhirContext.forDstu2();
		 List<String> allLines =
		 Files.readAllLines(Paths.get(file.getAbsolutePath()));
		 for (String line : allLines) {
		/*JSONParser parser = new JSONParser();
		JSONArray json = (JSONArray) parser.parse(new FileReader(file));
		Iterator<JSONObject> iterator = json.iterator();
		while (iterator.hasNext()) {
			JSONObject line = (JSONObject) iterator.next();*/
			try {
				switch (resourceName) {
				case "Patient":
					// Convert string to HAPI Patient
					Patient patient = (Patient) ctx.newJsonParser().parseResource(line.toString());

					// Check for the duplicates before persist/save(with given,family,gender,
					// birthdate)
					List<PatientResource> duplicates = patientService.findDuplicatesBeforePersist(patient, et);
					PatientResource patientResource = new PatientResource();
					patientResource.setExtractionTaskId(et.getExtractionTaskId());
					patientResource.setActualPatientId(patient.getId().getIdPart());
					if (patient.getName().size() > 0) {
						patientResource.setFirstName(patient.getName().get(0).getFamilyAsSingleString());
						patientResource.setLastName(patient.getName().get(0).getGivenAsSingleString());
					}
					patientResource.setData(line.toString());
					if (duplicates.size() == 0) {
						patientResource.setInternalPatientId("P-" + CommonUtil.generateRandomString(8));
					} else {
						patientResource.setInternalPatientId(duplicates.get(0).getInternalPatientId());
					}
					patientService.saveOrUpdate(patientResource);
					System.out.println("patient saved....!");
					break;

				case "AllergyIntolerance":

					AllergyIntolerance allergy = (AllergyIntolerance) ctx.newJsonParser()
							.parseResource(line.toString());
					List<AllergyIntoleranceResource> duplicateAllergies = allergyService
							.findDuplicatesBeforePersist(allergy, et);
					List<PatientResource> patientList = patientService
							.getPatientsByActualId(allergy.getPatient().getReference().getIdPart());
					
					AllergyIntoleranceResource ai = new AllergyIntoleranceResource();
					ai.setExtractionTaskId(et.getExtractionTaskId());
					ai.setActualAllergyId(allergy.getId().getIdPart());
					ai.setInternalPatientId(patientList.get(0).getInternalPatientId());
					ai.setData(line.toString());
					if (duplicateAllergies.size() == 0) {
						ai.setInternalAllergyId("AI-" + CommonUtil.generateRandomString(8));
					} else {
						ai.setInternalAllergyId(duplicateAllergies.get(0).getInternalAllergyId());
					}
					allergyService.saveOrUpdate(ai);

					break;

				case "Condition":
					Condition condition = (Condition) ctx.newJsonParser().parseResource(line.toString());
					List<ConditionResource> duplicateConditions = conditionService
							.findDuplicatesBeforePersist(condition, et);
					List<PatientResource> patientCondList = patientService
							.getPatientsByActualId(condition.getPatient().getReference().getIdPart());
					ConditionResource conditionResource = new ConditionResource();
					conditionResource.setExtractionTaskId(et.getExtractionTaskId());
					conditionResource.setActualConditionId(condition.getId().getIdPart());
					conditionResource.setInternalConditionId("C-" + CommonUtil.generateRandomString(8));
					conditionResource.setData(line.toString());
					if (duplicateConditions.size() == 0) {
						conditionResource.setInternalPatientId(patientCondList.get(0).getInternalPatientId());
					} else {
						conditionResource.setInternalPatientId(patientCondList.get(0).getInternalPatientId());
					}
					conditionService.saveOrUpdate(conditionResource);

					break;

				case "MedicationStatement":
					MedicationStatement ms = (MedicationStatement) ctx.newJsonParser().parseResource(line.toString());
					List<MedicationStatementResource> duplicateMedStatements = medStatementService
							.findDuplicatesBeforePersist(ms, et);
					List<PatientResource> patientMSList = patientService
							.getPatientsByActualId(ms.getPatient().getReference().getIdPart());
					MedicationStatementResource msResource = new MedicationStatementResource();
					msResource.setExtractionTaskId(et.getExtractionTaskId());
					msResource.setActualMedStatementId(ms.getId().getIdPart());
					msResource.setInternalMedStatementId("MS-" + CommonUtil.generateRandomString(8));
					msResource.setData(line.toString());
					if (duplicateMedStatements.size() == 0) {
						msResource.setInternalPatientId(patientMSList.get(0).getInternalPatientId());
					} else {
						msResource.setInternalPatientId(patientMSList.get(0).getInternalPatientId());
					}
					medStatementService.saveOrUpdate(msResource);

					break;

				case "Encounter":
					Encounter encounter = (Encounter) ctx.newJsonParser().parseResource(line.toString());
					List<EncounterResource> duplicateEncounters = encounterService
							.findDuplicatesBeforePersist(encounter, et);
					List<PatientResource> patientEncountersList = patientService
							.getPatientsByActualId(encounter.getPatient().getReference().getIdPart());
					EncounterResource encounterResource = new EncounterResource();
					encounterResource.setExtractionTaskId(et.getExtractionTaskId());
					encounterResource.setActualEncounterId(encounter.getId().getIdPart());
					encounterResource.setInternalEncounterId("E-" + CommonUtil.generateRandomString(8));
					encounterResource.setData(line.toString());
					if (duplicateEncounters.size() == 0) {
						encounterResource.setInternalPatientId(patientEncountersList.get(0).getInternalPatientId());
					} else {
						encounterResource.setInternalPatientId(patientEncountersList.get(0).getInternalPatientId());
					}
					encounterService.saveOrUpdate(encounterResource);

					break;

				case "Claim":
					Claim claim = (Claim) ctx.newJsonParser().parseResource(line.toString());
					List<ClaimResource> duplicateClaims = claimService.findDuplicatesBeforePersist(claim, et);
					List<PatientResource> patientClaimsList = patientService
							.getPatientsByActualId(claim.getPatient().getReference().getIdPart());
					ClaimResource claimResource = new ClaimResource();
					claimResource.setExtractionTaskId(et.getExtractionTaskId());
					claimResource.setActualClaimId(claim.getId().getIdPart());
					claimResource.setInternalClaimId("CL-" + CommonUtil.generateRandomString(8));
					claimResource.setData(line.toString());
					if (duplicateClaims.size() == 0) {
						claimResource.setInternalPatientId(patientClaimsList.get(0).getInternalPatientId());
					} else {
						claimResource.setInternalPatientId(patientClaimsList.get(0).getInternalPatientId());
					}
					claimService.saveOrUpdate(claimResource);

					break;

				case "Observation":
					try {
					Observation observation = (Observation) ctx.newJsonParser().parseResource(line.toString());
					// List<ObservationResource> duplicateObservations =
					// observationService.findDuplicatesBeforePersist(encounter, et);
					List<PatientResource> patientObservationsList = patientService
							.getPatientsByActualId(observation.getSubject().getReference().getIdPart());
					// if(duplicateEncounters.size()==0) {
					ObservationResource observationResource = new ObservationResource();
					observationResource.setExtractionTaskId(et.getExtractionTaskId());
					observationResource.setActualObservationId(observation.getId().getIdPart());
					observationResource.setInternalPatientId(patientObservationsList.get(0).getInternalPatientId());
					observationResource.setInternalObservationId("E-" + CommonUtil.generateRandomString(8));
					if (observation.getCategory() != null && observation.getCategory().getCoding() != null)
						observationResource.setCategory(observation.getCategory().getCoding().get(0).getCode());

					if (observation.getCode() != null && observation.getCode().getCoding() != null)
						observationResource.setCode(observation.getCode().getCoding().get(0).getCode());

					observationResource.setData(line.toString());
					observationService.saveOrUpdate(observationResource);
					// }
					}catch(Exception e) {
						System.out.println("error : "+e.getMessage());
						e.printStackTrace();
					}
					break;

				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		et.setStatus("Completed");
		etService.saveOrUpdate(et);
		
	}catch(Exception e) {
		e.printStackTrace();
	}
	}

	public String getResourceNameWithLink(String link) {

		String resourceName = null;

		if (link.contains("Patient"))
			resourceName = "Patient";
		else if (link.contains("AllergyIntolerance"))
			resourceName = "AllergyIntolerance";
		else if (link.contains("CarePlan"))
			resourceName = "CarePlan";
		else if (link.contains("Claim"))
			resourceName = "Claim";
		else if (link.contains("Condition"))
			resourceName = "Condition";
		else if (link.contains("Device"))
			resourceName = "Device";
		else if (link.contains("DiagnosticReport"))
			resourceName = "DiagnosticReport";
		else if (link.contains("DocumentReference"))
			resourceName = "DocumentReference";
		else if (link.contains("Encounter"))
			resourceName = "Encounter";
		else if (link.contains("Goal"))
			resourceName = "Goal";
		else if (link.contains("Immunization"))
			resourceName = "Immunization";
		else if (link.contains("Location"))
			resourceName = "location";
		else if (link.contains("MedicationAdministration"))
			resourceName = "MedicationAdministration";
		else if (link.contains("MedicationDispense"))
			resourceName = "MedicationDispense";
		else if (link.contains("MedicationOrder"))
			resourceName = "MedicationOrder";
		else if (link.contains("MedicationStatement"))
			resourceName = "MedicationStatement";
		else if (link.contains("Medication"))
			resourceName = "Medication";
		else if (link.contains("Observation"))
			resourceName = "Observation";
		else if (link.contains("Organization"))
			resourceName = "Organization";
		else if (link.contains("Procedure"))
			resourceName = "Procedure";
		else
			resourceName = "Unknown Resource";

		return resourceName;
	}
	
	  public String getAccessToken(HttpServletRequest request) throws Exception {
	    	try {
	    	String accessToken = null;
	    	String aud = environment.getRequiredProperty("aud");
	    	
	    	String client_assertion = getCompactJWS();
	        String json = "grant_type=client_credentials&scope=system/:resourceType.(read|write|*)&client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer&client_assertion="+client_assertion;
	        StringEntity entity = new StringEntity(json,ContentType.DEFAULT_TEXT);
	        
	        HttpClient httpClient = HttpClientBuilder.create().build();
	        HttpPost requestPost = new HttpPost(aud);
	        requestPost.setEntity(entity);
	        
	        HttpResponse response = httpClient.execute(requestPost);
	        if(response.getStatusLine().getStatusCode()==200) {
	        BufferedReader rd = new BufferedReader(
                  new InputStreamReader(response.getEntity().getContent()));

	        StringBuffer result = new StringBuffer();
	        String line;
	        while ((line = rd.readLine()) != null) {
	        	result.append(line);
	        }	
	        
	        JSONObject payLoad = new JSONObject(result.toString());
          
          accessToken = payLoad.getString("access_token");
          if(request!=null) {
          HttpSession httpSession = request.getSession();
          HashMap<String , String> sessionMap = new HashMap<String, String>();
          sessionMap.put("access_token", accessToken);
          httpSession.setAttribute("access_token", accessToken);
          }
	        }
	        
			return accessToken;
	    	}catch(Exception e) {
	    		System.out.println("error :"+e.getMessage());
	    		e.printStackTrace();
	    	}
	    	return null;
	    	
	    }
	    
	    public String getCompactJWS() throws Exception {
	    	
	    	String issuer = environment.getProperty("iss");
	    	String keypath = environment.getProperty("keypath");
	    	String subject = environment.getProperty("sub");
	    	String aud = environment.getProperty("aud");
	    	
	    	
	    	File f = new File(keypath);
	    	DataInputStream dis = new DataInputStream(new FileInputStream(f));
	    	byte[] keyBytes = new byte[(int)f.length()];
	    	dis.readFully(keyBytes);
	    	dis.close();
	    	PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
	    	Key key = KeyFactory.getInstance("RSA").generatePrivate(spec);
	    	String compactJws = Jwts.builder()
	    			  .setSubject(subject)
	    			  .setIssuedAt(new Date())
	    			  .setIssuer(issuer)
	    			  .setAudience(aud)
	    			  .signWith(SignatureAlgorithm.RS256, key)
	    			  .compact();
	    	
			return compactJws;
	    	
	    }

}
