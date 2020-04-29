package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafDiagnosticReport;
import org.sitenv.spring.service.DiagnosticReportService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.*;

public class DiagnosticReportResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "DiagnosticReport";
	public static final String VERSION_ID = "1.0";
	AbstractApplicationContext context;
	DiagnosticReportService service;

	public DiagnosticReportResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		service = (DiagnosticReportService) context.getBean("diagnosticReportService");
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return DiagnosticReport.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * 
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/DiagnosticReport/1/_history/4
	 * 
	 * @param theId : Id of the DiagnosticReport
	 * @return : Object of DiagnosticReport information
	 */
	@Read(version = true)
	public DiagnosticReport readOrVread(@IdParam IdType theId) {
		String id;
		DafDiagnosticReport dafDiagnosticReport;
		try {
			id = theId.getIdPart();
		} catch (NumberFormatException e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown
			 * resource
			 */
			throw new ResourceNotFoundException(theId);
		}
		if (theId.hasVersionIdPart()) {
			// this is a vread
			dafDiagnosticReport = service.getDiagnosticReportByVersionId(id, theId.getVersionIdPart());

		} else {

			dafDiagnosticReport = service.getDiagnosticReportById(id);
		}

		return createDiagnosticReportObject(dafDiagnosticReport);
	}

	/**
	 * The history operation retrieves a historical collection of all versions of a
	 * single resource (instance history). History methods must be annotated with
	 * the @History annotation.It supports Instance History method.
	 * "type=DiagnosticReport.class". Instance level (history of a specific resource
	 * instance by type and ID) The method must have a parameter annotated with
	 * the @IdParam annotation, indicating the ID of the resource for which to
	 * return history. Example URL to invoke this method: http://<server
	 * name>/<context>/fhir/DiagnosticReport/1/_history
	 * 
	 * @param theId : ID of the DiagnosticReport
	 * @return : List of DiagnosticReport's
	 */
	@History()
	public List<DiagnosticReport> getDiagnosticReportHistoryById(@IdParam IdType theId) {

		String id;
		try {
			id = theId.getIdPart();
		} catch (NumberFormatException e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown
			 * resource
			 */
			throw new ResourceNotFoundException(theId);
		}

		List<DafDiagnosticReport> dafDiagnosticReportList = service.getDiagnosticReportHistoryById(id);
		List<DiagnosticReport> diagnosticReportList = new ArrayList<DiagnosticReport>();
		for (DafDiagnosticReport dafDiagnosticReport : dafDiagnosticReportList) {
			diagnosticReportList.add(createDiagnosticReportObject(dafDiagnosticReport));
		}
		return diagnosticReportList;
	}

	/**
	 * The "@Search" annotation indicates that this method supports the search
	 * operation. You may have many different method annotated with this annotation,
	 * to support many different search criteria. The search operation returns a
	 * bundle with zero-to-many resources of a given type, matching a given set of
	 * parameters.
	 * 
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theCode
	 * @param theSubject
	 * @param theCategory
	 * @param theConclusion
	 * @param thePerformer
	 * @param theResult
	 * @param theBasedOn
	 * @param theSpecimen
	 * @param theDate
	 * @param theIncludes
	 * @param theRevIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(
		javax.servlet.http.HttpServletRequest theServletRequest,

		@Description(shortDefinition = "The resource identity")
        @OptionalParam(name = DiagnosticReport.SP_RES_ID)
		StringAndListParam theId,

		@Description(shortDefinition = "An DiagnosticReport  identifier")
		@OptionalParam(name = DiagnosticReport.SP_IDENTIFIER) 
		TokenAndListParam theIdentifier,

		@Description(shortDefinition = "The status of the diagnostic report")
		@OptionalParam(name = DiagnosticReport.SP_STATUS)
		TokenAndListParam theStatus,

		@Description(shortDefinition = "A code or name that describes this diagnostic report")
		@OptionalParam(name = DiagnosticReport.SP_CODE) 
		TokenAndListParam theCode,

		@Description(shortDefinition = "The subject of the report")
		@OptionalParam(name = DiagnosticReport.SP_SUBJECT) 
		ReferenceAndListParam theSubject,

		@Description(shortDefinition = "The healthcare event which this DiagnosticReport is about")
		@OptionalParam(name = DiagnosticReport.SP_ENCOUNTER) 
		ReferenceAndListParam theEncounter,

		@Description(shortDefinition = "This is used for searching, sorting and display purposes.")
		@OptionalParam(name = DiagnosticReport.SP_CATEGORY) 
		TokenAndListParam theCategory,

		@Description(shortDefinition = "Concise and clinically contextualized summary conclusion of the diagnostic report")
		@OptionalParam(name = DiagnosticReport.SP_CONCLUSION)
		TokenAndListParam theConclusion,

		@Description(shortDefinition = "The diagnostic service that is responsible for issuing the report")
		@OptionalParam(name = DiagnosticReport.SP_PERFORMER) 
		ReferenceAndListParam thePerformer,

		@Description(shortDefinition = "Observations that are part of this diagnostic report") 
		@OptionalParam(name = DiagnosticReport.SP_RESULT) 
		ReferenceAndListParam theResult,

		@Description(shortDefinition = "Details concerning a service requested") 
		@OptionalParam(name = DiagnosticReport.SP_BASED_ON) 
		ReferenceAndListParam theBasedOn,

		@Description(shortDefinition = "Details about the specimens on which this diagnostic report is based")
		@OptionalParam(name = DiagnosticReport.SP_SPECIMEN) 
		ReferenceAndListParam theSpecimen,

		@Description(shortDefinition = "The practitioner or organization that is responsible for the report's conclusions and interpretations")
		@OptionalParam(name = DiagnosticReport.SP_RESULTS_INTERPRETER) 
		ReferenceAndListParam theInterpreter,

		@Description(shortDefinition = "The date and time that this version of the report was made available to providers") 
		@OptionalParam(name = DiagnosticReport.SP_ISSUED)
		DateAndListParam theIssued,

		@Description(shortDefinition = "The time or time-period the observed values are related to") 
		@OptionalParam(name = DiagnosticReport.SP_DATE)
		DateAndListParam theDate,
		
		@Description(shortDefinition = "The subject of the report if a patient")
		@OptionalParam(name = DiagnosticReport.SP_PATIENT) 
		ReferenceAndListParam thePatient,

		@IncludeParam(allow = {"*"})
		Set<Include> theIncludes,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,

		@Sort SortSpec theSort,
		@Count Integer theCount) {

		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(DiagnosticReport.SP_RES_ID, theId);
		paramMap.add(DiagnosticReport.SP_IDENTIFIER, theIdentifier);
		paramMap.add(DiagnosticReport.SP_STATUS, theStatus);
		paramMap.add(DiagnosticReport.SP_CODE, theCode);
		paramMap.add(DiagnosticReport.SP_ENCOUNTER, theEncounter);
		paramMap.add(DiagnosticReport.SP_SUBJECT, theSubject);
		paramMap.add(DiagnosticReport.SP_CATEGORY, theCategory);
		paramMap.add(DiagnosticReport.SP_CONCLUSION, theConclusion);
		paramMap.add(DiagnosticReport.SP_PERFORMER, thePerformer);
		paramMap.add(DiagnosticReport.SP_RESULT, theResult);
		paramMap.add(DiagnosticReport.SP_BASED_ON, theBasedOn);
		paramMap.add(DiagnosticReport.SP_SPECIMEN, theSpecimen);
		paramMap.add(DiagnosticReport.SP_RESULTS_INTERPRETER, theInterpreter);
		paramMap.add(DiagnosticReport.SP_DATE, theDate);
		paramMap.add(DiagnosticReport.SP_ISSUED, theIssued);
		paramMap.add(DiagnosticReport.SP_PATIENT, thePatient);
		paramMap.setSort(theSort);
		paramMap.setCount(theCount);

		final List<DafDiagnosticReport> results = service.search(paramMap);

		return new IBundleProvider() {
			final InstantDt published = InstantDt.withCurrentTime();

			@Override
			public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
				List<IBaseResource> diagnosticReportList = new ArrayList<IBaseResource>();
				List<String> ids = new ArrayList<String>();
				for (DafDiagnosticReport dafDiagnosticReport : results) {
					DiagnosticReport diagnosticReport = createDiagnosticReportObject(dafDiagnosticReport);
					diagnosticReportList.add(diagnosticReport);
					ids.add(((IdType)diagnosticReport.getIdElement()).getResourceType()+"/"+((IdType)diagnosticReport.getIdElement()).getIdPart());
				}
				if(theRevIncludes.size() >0 ){
					diagnosticReportList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
				}
				return diagnosticReportList;
			}

			@Override
			public Integer size() {
				return results.size();
			}

			@Override
			public InstantDt getPublished() {
				return published;
			}

			@Override
			public Integer preferredPageSize() {
				return null;
			}

			@Override
			public String getUuid() {
				return null;
			}
		};
	}
	
	/**
     * The create  operation saves a new resource to the server, 
     * allowing the server to give that resource an ID and version ID.
     * Create methods must be annotated with the @Create annotation, 
     * and have a single parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Create methods must return an object of type MethodOutcome . 
     * This object contains the identity of the created resource.
     * Example URL to invoke this method (this would be invoked using an HTTP POST, 
     * with the resource in the POST body): http://<server name>/<context>/fhir/Questionnaire
     * @param theDiagnosticReport
     * @return
     */
	@Create
	public MethodOutcome createDiagnosticReport(@ResourceParam DiagnosticReport theDiagnosticReport) {	

		// Save this Questionnaire to the database...
		DafDiagnosticReport dafDiagnosticReport = service.createDiagnosticReport(theDiagnosticReport);

		// This method returns a MethodOutcome object which contains
		// the ID (composed of the type Patient, the logical ID 3746, and the
		// version ID 1)
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafDiagnosticReport.getId().toString()));

		return retVal;
	}
	

	/**
	 * This method converts DafDocumentReference object to DocumentReference object
	 */
	private DiagnosticReport createDiagnosticReportObject(DafDiagnosticReport dafDiagnosticReport) {

		DiagnosticReport diagnosticReport = new DiagnosticReport();
		JSONObject diagnosticReportJSON = new JSONObject(dafDiagnosticReport.getData());

		// Set version
		if (!(diagnosticReportJSON.isNull("meta"))) {
			if (!(diagnosticReportJSON.getJSONObject("meta").isNull("versionId"))) {
				diagnosticReport.setId(new IdType(RESOURCE_TYPE, diagnosticReportJSON.getString("id") + "",
						diagnosticReportJSON.getJSONObject("meta").getString("versionId")));
			}else {
				diagnosticReport.setId(new IdType(RESOURCE_TYPE, diagnosticReportJSON.getString("id") + "", VERSION_ID));
			}
		} else {
			diagnosticReport.setId(new IdType(RESOURCE_TYPE, diagnosticReportJSON.getString("id") + "", VERSION_ID));
		}

		// set code
		if (!(diagnosticReportJSON.isNull("code"))) {
			JSONObject codeJSON = diagnosticReportJSON.getJSONObject("code");
			CodeableConcept theCode = new CodeableConcept();

			if (!(codeJSON.isNull("coding"))) {
				JSONArray codingJSON = codeJSON.getJSONArray("coding");
				int noOfCoding = codingJSON.length();
				List<Coding> codingDtList = new ArrayList<Coding>();
				for (int i = 0; i < noOfCoding; i++) {
					Coding theCoding = new Coding();
					if (!(codingJSON.getJSONObject(i).isNull("system"))) {
						theCoding.setSystem(codingJSON.getJSONObject(i).getString("system"));
					}
					if (!(codingJSON.getJSONObject(i).isNull("code"))) {
						theCoding.setCode(codingJSON.getJSONObject(i).getString("code"));
					}
					if (!(codingJSON.getJSONObject(i).isNull("display"))) {
						theCoding.setDisplay(codingJSON.getJSONObject(i).getString("display"));
					}
					codingDtList.add(theCoding);
				}
				theCode.setCoding(codingDtList);
			}
			diagnosticReport.setCode(theCode);
		}

		// set subject
		if (!(diagnosticReportJSON.isNull("subject"))) {
			Reference theSubject = new Reference();
			if (!(diagnosticReportJSON.getJSONObject("subject").isNull("reference"))) {
				theSubject.setReference(diagnosticReportJSON.getJSONObject("subject").getString("reference"));
			}
			if (!(diagnosticReportJSON.getJSONObject("subject").isNull("display"))) {
				theSubject.setDisplay(diagnosticReportJSON.getJSONObject("subject").getString("display"));
			}
			if (!(diagnosticReportJSON.getJSONObject("subject").isNull("type"))) {
				theSubject.setType(diagnosticReportJSON.getJSONObject("subject").getString("type"));
			}
			diagnosticReport.setSubject(theSubject);
		}

		// set encounter
		if (!(diagnosticReportJSON.isNull("encounter"))) {
			Reference theEncounter = new Reference();
			if (!(diagnosticReportJSON.getJSONObject("encounter").isNull("reference"))) {
				theEncounter.setReference(diagnosticReportJSON.getJSONObject("encounter").getString("reference"));
			}
			if (!(diagnosticReportJSON.getJSONObject("encounter").isNull("display"))) {
				theEncounter.setDisplay(diagnosticReportJSON.getJSONObject("encounter").getString("display"));
			}
			if (!(diagnosticReportJSON.getJSONObject("encounter").isNull("type"))) {
				theEncounter.setType(diagnosticReportJSON.getJSONObject("encounter").getString("type"));
			}
			diagnosticReport.setEncounter(theEncounter);
		}

		// set conclusion
		if (!(diagnosticReportJSON.isNull("conclusion"))) {
			diagnosticReport.setConclusion(diagnosticReportJSON.getString("conclusion"));
		}

		// Set status
		if (!(diagnosticReportJSON.isNull("status"))) {
			diagnosticReport.setStatus(
					DiagnosticReport.DiagnosticReportStatus.fromCode(diagnosticReportJSON.getString("status")));
		}

		// set performer
		if (!(diagnosticReportJSON.isNull("performer"))) {
			JSONArray performerJSON = diagnosticReportJSON.getJSONArray("performer");
			int noOfPerformer = performerJSON.length();
			List<Reference> performerDtList = new ArrayList<Reference>();
			for (int i = 0; i < noOfPerformer; i++) {
				Reference thePerformer = new Reference();
				if (!(performerJSON.getJSONObject(i).isNull("reference"))) {
					thePerformer.setReference(performerJSON.getJSONObject(i).getString("reference"));
				}
				if (!(performerJSON.getJSONObject(i).isNull("display"))) {
					thePerformer.setDisplay(performerJSON.getJSONObject(i).getString("display"));
				}
				if (!(performerJSON.getJSONObject(i).isNull("type"))) {
					thePerformer.setType(performerJSON.getJSONObject(i).getString("type"));
				}
				performerDtList.add(thePerformer);
			}
			diagnosticReport.setPerformer(performerDtList);
		}

		// set result
		if (!(diagnosticReportJSON.isNull("result"))) {
			JSONArray resultJSON = diagnosticReportJSON.getJSONArray("result");
			int noOfResult = resultJSON.length();
			List<Reference> resultDtList = new ArrayList<Reference>();
			for (int i = 0; i < noOfResult; i++) {
				Reference theResult = new Reference();
				if (!(resultJSON.getJSONObject(i).isNull("reference"))) {
					theResult.setReference(resultJSON.getJSONObject(i).getString("reference"));
				}
				if (!(resultJSON.getJSONObject(i).isNull("display"))) {
					theResult.setDisplay(resultJSON.getJSONObject(i).getString("display"));
				}
				if (!(resultJSON.getJSONObject(i).isNull("type"))) {
					theResult.setType(resultJSON.getJSONObject(i).getString("type"));
				}

				resultDtList.add(theResult);
			}
			diagnosticReport.setResult(resultDtList);
		}

		// set category
				if (!(diagnosticReportJSON.isNull("category"))) {
					JSONArray categoryJSON = diagnosticReportJSON.getJSONArray("category");
					int noOfCategaries = categoryJSON.length();
					List<CodeableConcept> categoryList = new ArrayList<CodeableConcept>();
					for (int c = 0; c < noOfCategaries; c++) {
						CodeableConcept theCategory = new CodeableConcept();
						if (!(categoryJSON.getJSONObject(c).isNull("coding"))) {
							JSONArray categoryCodingJSON = categoryJSON.getJSONObject(c).getJSONArray("coding");
							int noOfCategoryCoding = categoryCodingJSON.length();
							List<Coding> codingList = new ArrayList<Coding>();
							for (int j = 0; j < noOfCategoryCoding; j++) {
								Coding categoryCoding = new Coding();
								if (!(categoryCodingJSON.getJSONObject(j).isNull("system"))) {
									categoryCoding.setSystem(categoryCodingJSON.getJSONObject(j).getString("system"));
								}
								if (!(categoryCodingJSON.getJSONObject(j).isNull("version"))) {
									categoryCoding.setVersion(categoryCodingJSON.getJSONObject(j).getString("version"));
								}
								if (!(categoryCodingJSON.getJSONObject(j).isNull("code"))) {
									categoryCoding.setCode(categoryCodingJSON.getJSONObject(j).getString("code"));
								}
								if (!(categoryCodingJSON.getJSONObject(j).isNull("display"))) {
									categoryCoding.setDisplay(categoryCodingJSON.getJSONObject(j).getString("display"));
								}
								if (!(categoryCodingJSON.getJSONObject(j).isNull("userSelected"))) {
									categoryCoding.setUserSelected(categoryCodingJSON.getJSONObject(j).getBoolean("userSelected"));
								}
								codingList.add(categoryCoding);
							}
							theCategory.setCoding(codingList);
						}
						if (!(categoryJSON.getJSONObject(c).isNull("text"))) {
							theCategory.setText(categoryJSON.getJSONObject(c).getString("text"));
						}
						categoryList.add(theCategory);
					}
					diagnosticReport.setCategory(categoryList);
				}
		// set basedOn
		if (!(diagnosticReportJSON.isNull("basedOn"))) {
			JSONArray basedOnJSON = diagnosticReportJSON.getJSONArray("basedOn");
			int noOfBasedOn = basedOnJSON.length();
			List<Reference> basedOnDtList = new ArrayList<Reference>();
			for (int i = 0; i < noOfBasedOn; i++) {
				Reference theBasedOn = new Reference();
				if (!(basedOnJSON.getJSONObject(i).isNull("reference"))) {
					theBasedOn.setReference(basedOnJSON.getJSONObject(i).getString("reference"));
				}
				if (!(basedOnJSON.getJSONObject(i).isNull("display"))) {
					theBasedOn.setDisplay(basedOnJSON.getJSONObject(i).getString("display"));
				}
				if (!(basedOnJSON.getJSONObject(i).isNull("type"))) {
					theBasedOn.setType(basedOnJSON.getJSONObject(i).getString("type"));
				}
				basedOnDtList.add(theBasedOn);
			}
			diagnosticReport.setBasedOn(basedOnDtList);
		}

		// set specimen
		if (!(diagnosticReportJSON.isNull("specimen"))) {
			JSONArray specimenJSON = diagnosticReportJSON.getJSONArray("specimen");
			int noOfSpecimens = specimenJSON.length();
			List<Reference> specimenDtList = new ArrayList<Reference>();
			Reference theSpecimen = new Reference();
			for (int i = 0; i < noOfSpecimens; i++) {
				if (!(specimenJSON.getJSONObject(i).isNull("reference"))) {
					theSpecimen.setReference(specimenJSON.getJSONObject(i).getString("reference"));
				}
				if (!(specimenJSON.getJSONObject(i).isNull("display"))) {
					theSpecimen.setDisplay(specimenJSON.getJSONObject(i).getString("display"));
				}
				if (!(specimenJSON.getJSONObject(i).isNull("type"))) {
					theSpecimen.setType(specimenJSON.getJSONObject(i).getString("type"));
				}
				specimenDtList.add(theSpecimen);
			}
			diagnosticReport.setSpecimen(specimenDtList);
		}

		 //Set identifier
        if(!(diagnosticReportJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = diagnosticReportJSON.getJSONArray("identifier");
        	int noOfIdentifiers = identifierJSON.length();
        	List<Identifier> identifiers = new ArrayList<Identifier>();
        	for(int i = 0; i < noOfIdentifiers; i++) {
            	Identifier theIdentifier = new Identifier();
        		if(!(identifierJSON.getJSONObject(i).isNull("use"))) {
                	theIdentifier.setUse(Identifier.IdentifierUse.fromCode(identifierJSON.getJSONObject(i).getString("use")));	
            	}
        		
        		if(!(identifierJSON.getJSONObject(i).isNull("type"))) {
        			CodeableConcept theType = new CodeableConcept();
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("type").isNull("coding"))) {
            			JSONArray typeCodingJSON = identifierJSON.getJSONObject(i).getJSONObject("type").getJSONArray("coding");
            			int noOfCodings = typeCodingJSON.length();
            			List<Coding> theCodingList = new ArrayList<Coding>();
            			for(int j = 0; j < noOfCodings; j++) {
            				Coding theCoding = new Coding();
                			if(!(typeCodingJSON.getJSONObject(j).isNull("system"))) {
                				theCoding.setSystem(typeCodingJSON.getJSONObject(j).getString("system"));
                			}
                			if(!(typeCodingJSON.getJSONObject(j).isNull("code"))) {
                				theCoding.setCode(typeCodingJSON.getJSONObject(j).getString("code"));
                			}
                			if(!(typeCodingJSON.getJSONObject(j).isNull("display"))) {
                				theCoding.setDisplay(typeCodingJSON.getJSONObject(j).getString("display"));
                			}
                			theCodingList.add(theCoding);
            			}
                    	
            			theType.setCoding(theCodingList);
            		}
                	theIdentifier.setType(theType);
            	}
        		
        		if(!(identifierJSON.getJSONObject(i).isNull("system"))) {
                	theIdentifier.setSystem(identifierJSON.getJSONObject(i).getString("system"));
            	}
            	
            	if(!(identifierJSON.getJSONObject(i).isNull("value"))) {
                	theIdentifier.setValue(identifierJSON.getJSONObject(i).getString("value"));
            	}
            	
            	if(!(identifierJSON.getJSONObject(i).isNull("period"))) {
            		Period thePeriod = new Period();
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("period").isNull("start"))) {
                        Date startDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject(i).getJSONObject("period").getString("start"));
                        thePeriod.setStart(startDate);
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("period").isNull("end"))) {
                        Date endDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject(i).getJSONObject("period").getString("end"));
                        thePeriod.setStart(endDate);
            		}
                    theIdentifier.setPeriod(thePeriod);
            	}

            	if(!(identifierJSON.getJSONObject(i).isNull("assigner"))) {
        			Reference theAssigner = new Reference(); 
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("display"))) {
                        theAssigner.setDisplay(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("display"));
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("reference"))) {
                        theAssigner.setReference(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("reference"));
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("type"))) {
                        theAssigner.setType(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("type"));
            		}
                    theIdentifier.setAssigner(theAssigner);
            	}
         
            	identifiers.add(theIdentifier);
        	}
        	diagnosticReport.setIdentifier(identifiers);
        }

		// set issued
		if (!(diagnosticReportJSON.isNull("issued"))) {
			String dateInStr = (String) diagnosticReportJSON.get("issued");
			Date dateOfIssued = CommonUtil.convertStringToDate(dateInStr);
			diagnosticReport.setIssued(dateOfIssued);
		}

		// set effectiveDateTime
		if (!(diagnosticReportJSON.isNull("effectiveDateTime"))) {
			DateTimeType theEffectiveDateTime = new DateTimeType();
        	Date theDate = CommonUtil.convertStringToDate(diagnosticReportJSON.getString("effectiveDateTime"));
        	theEffectiveDateTime.setValue(theDate);
        	diagnosticReport.setEffective(theEffectiveDateTime);
		}

		//set presentedForm
		if (!(diagnosticReportJSON.isNull("presentedForm"))) {
			JSONArray presentedFormJSON = diagnosticReportJSON.getJSONArray("presentedForm");
			List<Attachment> attachmentList = new ArrayList<Attachment>();
			for (int i = 0; i < presentedFormJSON.length(); i++) {
				Attachment theAttachment = new Attachment();
				if(!(presentedFormJSON.getJSONObject(i).isNull("contentType"))){
					theAttachment.setContentType(presentedFormJSON.getJSONObject(i).getString("contentType"));
				}
				if(!(presentedFormJSON.getJSONObject(i).isNull("data"))){
					theAttachment.setData(Base64.getDecoder().decode(presentedFormJSON.getJSONObject(i).getString("data")));
				}
				attachmentList.add(theAttachment);
			}
			diagnosticReport.setPresentedForm(attachmentList);

		}

			return diagnosticReport;
	}
}
