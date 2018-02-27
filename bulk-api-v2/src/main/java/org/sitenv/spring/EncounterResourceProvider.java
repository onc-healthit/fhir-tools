package org.sitenv.spring;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.tools.ant.taskdefs.Length;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.jpa.internal.metamodel.SingularAttributeImpl.Identifier;
import org.hibernate.metamodel.source.annotations.entity.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.neo4j.cypher.internal.compiler.v2_0.functions.Id;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.model.DafEncounter;
import org.sitenv.spring.model.DafMedicationOrder;
import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.query.ConditionSearchCriteria;
import org.sitenv.spring.query.EncounterSearchCriteria;
import org.sitenv.spring.service.AllergyIntoleranceService;

import org.sitenv.spring.service.EncounterService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.DurationDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt.Repeat;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder.DispenseRequest;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder.DosageInstruction;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.Encounter.Hospitalization;
import ca.uhn.fhir.model.dstu2.resource.Encounter.Location;
import ca.uhn.fhir.model.dstu2.resource.Encounter.Participant;
import ca.uhn.fhir.model.dstu2.resource.Encounter.StatusHistory;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.valueset.AdmitSourceEnum;
import ca.uhn.fhir.model.dstu2.valueset.AllergyIntoleranceStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.CarePlanStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterClassEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.UnitsOfTimeEnum;
import ca.uhn.fhir.model.primitive.BoundCodeDt;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringOrListParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Component
@Scope("request")
public class EncounterResourceProvider<dischargeDispositionEnum, DischargeDispositionEnum> implements IResourceProvider {
	  public static final String RESOURCE_TYPE = "Encounter";
	    public static final String VERSION_ID = "2.0";
	    AbstractApplicationContext context;
	    EncounterService service;
	
	    
	    
	    @Override
	    public Class<Encounter> getResourceType() {
	        return Encounter.class;
	    }
	    
	    public EncounterResourceProvider() {
	        context = new AnnotationConfigApplicationContext(AppConfig.class);
	        service = (EncounterService) context.getBean("encounterResourceService");
	    }
	    
	    
	    @Read()
	    public Encounter getEncounterResourceById(@IdParam IdDt theId) {

	    	DafEncounter dafEncounter = service.getEncounterResourceById(theId.getIdPartAsLong().intValue());

	    	Encounter encounter = createEncounterObject(dafEncounter);

	        return encounter;
	    }

	    
	    @Search()
	    public List<Encounter> searchByPatient(@RequiredParam(name = MedicationOrder.SP_PATIENT) ReferenceParam thePatient,
	                                                 @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
	        String patientId = thePatient.getIdPart();
	        
	        List<DafEncounter> dafMedOrderList = service.getEncounterrByPatient(patientId);

	        List<Encounter> medOrderList = new ArrayList<Encounter>();

	        for (DafEncounter dafMedOrder : dafMedOrderList) {
	            medOrderList.add(createEncounterObject(dafMedOrder));
	        }
	        return medOrderList;
	    }
	 
	   
	    @Search
	    public List<Encounter> getAllEncounter(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount){
	    	try {
	    	List<DafEncounter> dafEncounterList = service.getAllEncounter();

	         List<Encounter> encounterList = new ArrayList<Encounter>();

	         for (DafEncounter dafEncounter : dafEncounterList) {
	        	 encounterList.add(createEncounterObject(dafEncounter));
	         }

	         return encounterList;
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
			return null;
	    	
	    }
	    
	    @Update
	    public MethodOutcome updateEncounter(@IdParam IdDt theId, @ResourceParam Encounter theEncounter)throws JsonGenerationException, JsonMappingException, IOException {
	    	
	    	DafEncounter de = convertEncountertoDafEncounter(theEncounter);
	    	MethodOutcome retVal = new MethodOutcome();
	    	de.setId(theId.getIdPartAsLong().intValue());
	    	DafEncounter updateEn = service.updateEncounter(de);
	    	IdDt updatedId = new IdDt("Encounter", Integer.toString(updateEn.getId()));
	         retVal.setId(updatedId);
	    	System.out.println(updateEn.getId());
	    	return retVal;
	     }

	    
	    @Create
	    public MethodOutcome saveEncounter(@ResourceParam Encounter  en) throws JsonGenerationException, JsonMappingException, IOException {
	    	
	    	 DafEncounter de = convertEncountertoDafEncounter(en);
	         MethodOutcome retVal = new MethodOutcome();
	         DafEncounter persistedEn = service.saveEncounter(de);
	         System.out.println(persistedEn.getId());
	         return retVal;
	    	 }
	    
	    
	    public DafEncounter convertEncountertoDafEncounter(Encounter en) throws JsonGenerationException, JsonMappingException, IOException {
			
	    
	    	  DafEncounter de = new DafEncounter();
	    	  ObjectMapper mapperObj = new ObjectMapper();
	    	
	    	   // Identifier
	    	  HashMap<String,String> hm=new HashMap<String,String>();  
	    	  if(en.getIdentifier() != null && en.getIdentifier().get(0).getUse() !=null) {
	    	  hm.put("use",en.getIdentifier().get(0).getUse());
	    	  }
	    	  if(en.getIdentifier() != null && en.getIdentifier().get(0).getSystem() !=null) {
	    	  hm.put("system",en.getIdentifier().get(0).getSystem());
	    	  }
	    	  if(en.getIdentifier() != null && en.getIdentifier().get(0).getValue()!=null) {
	    	  hm.put("value",en.getIdentifier().get(0).getValue() );
	    	  }
	    	  String dafIdentifier = mapperObj.writeValueAsString(hm);
	    	  de.setIdentifier(dafIdentifier);
	    	  
	    	
	    	   //class
	    	 if(en.getClass() != null) {
	    	 de.setClasses(en.getClassElement());
	    	 }
	    	
	    	  
	    	  //status
	    	if(en.getStatus() != null) {
			de.setStatus(en.getStatus());
			 }
			
			  //Patient
	    	 if(en.getPatient() != null) {
			 de.setPatient(Integer.parseInt(en.getPatient().getReference().getIdPart()));
	         }
	       
	         //participant
	        if(en.getParticipant() != null) {
	        de.setParticipant(Integer.parseInt(en.getParticipant().get(0).getIndividual().getReference().getIdPart()));
		     }
			      
			  //Type
	        HashMap<String, String> type = new HashMap<String,String>();
			  if(en.getType() != null && en.getType().get(0).getCoding().get(0).getCode()!=null) {
			type.put("code",en.getType().get(0).getCoding().get(0).getCode());
			  }
			  if(en.getType() != null && en.getType().get(0).getCoding().get(0).getSystem()!=null) {
			type.put("system",en.getType().get(0).getCoding().get(0).getSystem());
			  }
			  if(en.getType() != null && en.getType().get(0).getCoding().get(0).getDisplay()!=null) {
			type.put("display",en.getType().get(0).getCoding().get(0).getDisplay());
			  }
			  if(en.getType() != null && en.getType().get(0).getText()!=null) {
			type.put("text", en.getType().get(0).getText());
			  }
			
		   String dafType =  mapperObj.writeValueAsString(type);
		   de.setType(dafType);
	    	  
			
		    //priority
	       HashMap<String, String> priority = new HashMap<String,String>();
		   if(en.getPriority()!= null && en.getPriority().getCoding().get(0).getCode()!=null) {
		   priority.put("code",en.getPriority().getCoding().get(0).getCode());
		   }
		   if(en.getPriority()!= null && en.getPriority().getCoding().get(0).getSystem()!=null) {
		   priority.put("system",en.getPriority().getCoding().get(0).getSystem());
		   }
		   if(en.getPriority()!= null && en.getPriority().getCoding().get(0).getDisplay()!=null) {
		   priority.put("display",en.getPriority().getCoding().get(0).getDisplay());
		   }
		   
		   String dafpriority =  mapperObj.writeValueAsString(priority);
		   de.setPriority(dafpriority);
	    	 
			
		  //period
	     HashMap<String, String> period = new HashMap<String,String>();
		 Date date = new Date();
         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
         if(en.getPeriod() != null && en.getPeriod().getStart()!=null ) {
         period.put("start",dateFormat.format(en.getPeriod().getStart()));
          }
         if(en.getPeriod() != null && en.getPeriod().getEnd()!=null ) {
	     period.put("end",dateFormat.format(en.getPeriod().getEnd()));
          }
             
	     String dafperiod =  mapperObj.writeValueAsString(period);
		 de.setShPeriod(dafperiod);
	    	  
			
		 //length
	    HashMap<String, String> length = new HashMap<String,String>();
		if(en.getLength()!= null && en.getLength().getCode()!=null) {
		length.put("code",en.getLength().getCode());
		 }
		if(en.getLength()!= null && en.getLength().getValue().toPlainString()!=null) {
		length.put("value",en.getLength().getValue().toPlainString());
		}
		if(en.getLength()!= null && en.getLength().getUnit()!=null) {
		length.put("unit",en.getLength().getUnit());
		}
		if(en.getLength()!= null && en.getLength().getSystem()!=null) {
		length.put("system",en.getLength().getSystem());
		}
				 
		String daflength =  mapperObj.writeValueAsString(length);
		 de.setLength(daflength);
	    	 
	    	  
	      //Reason
	     HashMap<String, String> reason = new HashMap<String,String>();
	      if(en.getReason()!= null && en.getReason().get(0).getCoding().get(0).getCode()!=null) {
	       reason.put("code",en.getReason().get(0).getCoding().get(0).getCode());
	       }
	       if(en.getReason()!= null && en.getReason().get(0).getCoding().get(0).getSystem()!=null) {
	       reason.put("system",en.getReason().get(0).getCoding().get(0).getSystem());
	       }
	       if(en.getReason()!= null && en.getReason().get(0).getCoding().get(0).getDisplay()!=null) {
	       reason.put("display",en.getReason().get(0).getCoding().get(0).getDisplay());
	       }
	       if(en.getReason()!= null && en.getReason().get(0).getText()!=null) {
	       reason.put("text", en.getReason().get(0).getText());
	       }
			
	       String dafreason =  mapperObj.writeValueAsString(reason);
		   de.setReason(dafreason); 
	    	  
			
			   //preAdmissionIdentifier
	    	  HashMap<String, String> preAdmission = new HashMap<String,String>();
			  if(en.getHospitalization()!= null && en.getHospitalization().getPreAdmissionIdentifier().getUse()!=null) {
			  preAdmission.put("use",en.getHospitalization().getPreAdmissionIdentifier().getUse());
			  }
			  if(en.getHospitalization()!= null && en.getHospitalization().getPreAdmissionIdentifier().getSystem()!=null) {
			  preAdmission.put("system",en.getHospitalization().getPreAdmissionIdentifier().getSystem());
			  }
			  if(en.getHospitalization()!= null && en.getHospitalization().getPreAdmissionIdentifier().getValue()!=null) {
			  preAdmission.put("value",en.getHospitalization().getPreAdmissionIdentifier().getValue());
			  }
		   
		        String dafpreAdmission =  mapperObj.writeValueAsString(preAdmission);
			    de.setHospPreAdmissionIdentifier(dafpreAdmission); 		  
	    	  
			  
			   //admitSource
	    	 HashMap<String, String> admitSource = new HashMap<String,String>();
			  if(en.getHospitalization()!= null && en.getHospitalization().getAdmitSource().getCoding().get(0).getCode()!=null ) {
			  admitSource.put("code",en.getHospitalization().getAdmitSource().getCoding().get(0).getCode());
			  }
			  if(en.getHospitalization()!= null && en.getHospitalization().getAdmitSource().getCoding().get(0).getSystem()!=null ) {
			  admitSource.put("system",en.getHospitalization().getAdmitSource().getCoding().get(0).getSystem());
			  }
			  if(en.getHospitalization()!= null && en.getHospitalization().getAdmitSource().getCoding().get(0).getDisplay()!=null ) {
			  admitSource.put("display",en.getHospitalization().getAdmitSource().getCoding().get(0).getDisplay());
			  }
		   
		        String dafadmitSource =  mapperObj.writeValueAsString(admitSource);
			    de.setHospAdmitSource(dafadmitSource); 		  
	    	  
			  
			   //reAdmission
	          HashMap<String, String> reAdmission = new HashMap<String,String>();
			  if(en.getHospitalization()!= null && en.getHospitalization().getReAdmission().getCoding().get(0).getDisplay()!=null) {
			  reAdmission.put("display",en.getHospitalization().getReAdmission().getCoding().get(0).getDisplay());
			  }
		     String dafreAdmission =  mapperObj.writeValueAsString(reAdmission);
		     de.setHospReAdmission(dafreAdmission);
	    	 
			  
			   //dietPreference
	    	  HashMap<String, String> dietPreference = new HashMap<String,String>();
			  if(en.getHospitalization()!= null && en.getHospitalization().getDietPreference().get(0).getCoding().get(0).getCode()!=null ) {
			  dietPreference.put("code",en.getHospitalization().getDietPreference().get(0).getCoding().get(0).getCode());
			  }
			  if(en.getHospitalization()!= null && en.getHospitalization().getDietPreference().get(0).getCoding().get(0).getSystem()!=null ) {
			  dietPreference.put("system",en.getHospitalization().getDietPreference().get(0).getCoding().get(0).getSystem());
			  }
			  if(en.getHospitalization()!= null && en.getHospitalization().getDietPreference().get(0).getCoding().get(0).getDisplay()!=null ) {
			  dietPreference.put("display",en.getHospitalization().getDietPreference().get(0).getCoding().get(0).getDisplay());
			  }
		   
		        String dafdietPreference =  mapperObj.writeValueAsString(dietPreference);
			    de.setHospDietPreference(dafdietPreference); 	  
	    	 
			  
			   //dischargeDisposition 
	          HashMap<String, String> discharge = new HashMap<String,String>();
			  if(en.getHospitalization()!= null &&  en.getHospitalization().getDischargeDisposition().getCoding().get(0).getCode() !=null) {
			  discharge.put("code",en.getHospitalization().getDischargeDisposition().getCoding().get(0).getCode());
			  }
			  if(en.getHospitalization()!= null &&  en.getHospitalization().getDischargeDisposition().getCoding().get(0).getSystem() !=null) {
			  discharge.put("system",en.getHospitalization().getDischargeDisposition().getCoding().get(0).getSystem());
			  }
			  if(en.getHospitalization()!= null &&  en.getHospitalization().getDischargeDisposition().getCoding().get(0).getDisplay() !=null) {
			  discharge.put("display",en.getHospitalization().getDischargeDisposition().getCoding().get(0).getDisplay());
			  }
		   
		       String dafdischarge =  mapperObj.writeValueAsString(discharge);
			   de.setHospDischargeDisposition(dafdischarge);	
	    	
				  
             //Location
	       if(en.getLocation()!= null) {
		   de.setLocation(Integer.parseInt(en.getLocation().get(0).getLocation().getReference().getIdPart()));
	        }
		
		   //serviceProvider
	     if(en.getServiceProvider()!= null) { 
		 de.setServiceProvider(Integer.parseInt(en.getServiceProvider().getReference().getIdPart()));
	     }
			
		return de;
		
	      }
	    
    
	    private Encounter createEncounterObject(DafEncounter dafEncounter) {
	    	try {
	    	
	    	Encounter encounter = new Encounter();
	    	
	    	// Set Version
	    	encounter.setId(new IdDt(RESOURCE_TYPE, dafEncounter.getId() + "", VERSION_ID));
	    	
	    	//Set identifier
	       Map<String, String> enIdentifier = HapiUtils.convertToJsonMap(dafEncounter.getIdentifier());
	        List<IdentifierDt> identifier = new ArrayList<IdentifierDt>();
	        IdentifierDt identifierDt = new IdentifierDt();
	        System.out.println(enIdentifier.get("use"));
	        identifierDt.setUse(IdentifierUseEnum.valueOf(enIdentifier.get("use").toUpperCase()));
	        identifierDt.setSystem(enIdentifier.get("system"));
	        identifierDt.setValue(enIdentifier.get("value"));
	        identifier.add(identifierDt);
	        encounter.setIdentifier(identifier);
	    	
	         //set patient
	    	ResourceReferenceDt patientResource = new ResourceReferenceDt();
	        String theId = "Patient/" + Integer.toString(dafEncounter.getPatient());
	        patientResource.setReference(theId);
	       // patientResource.setDisplay("display");
	        encounter.setPatient(patientResource);
	        
	    	
	        //Set Status
	        encounter.setStatus(EncounterStateEnum.valueOf(dafEncounter.getStatus().toUpperCase().trim()));
	        
	 
	       
	    	
	        //Set Period 
	   	   Map<String, String> enPeriod = HapiUtils.convertToJsonMap(dafEncounter.getShPeriod());
		  //PeriodDt period = new ArrayList<PeriodDt>();
		   PeriodDt period = new PeriodDt();
		   period.setStart(new DateTimeDt(enPeriod.get("start")));
		   period.setEnd(new DateTimeDt(enPeriod.get("end")));
          // period.add(period);
           encounter.setPeriod(period);
	    	
           //Set Class
	       encounter.setClassElement(EncounterClassEnum.valueOf(dafEncounter.getClasses().toUpperCase()));
	    	
          //Set Type
	      Map<String, String> enType = HapiUtils.convertToJsonMap(dafEncounter.getType());
          List<CodeableConceptDt> typeList = new ArrayList<CodeableConceptDt>();
          CodeableConceptDt typecode = new CodeableConceptDt();
          CodingDt typecoding = new CodingDt();
          typecoding.setCode(enType.get("code"));
          typecoding.setDisplay(enType.get("display"));
          typecoding.setSystem(enType.get("system"));
          typecode.addCoding(typecoding);
          typecode.setText(enType.get("text"));
          typeList.add(typecode);
          encounter.setType(typeList);
	    	
         //Set Priority
	    Map<String, String> medCodeableConcept = HapiUtils.convertToJsonMap(dafEncounter.getPriority());
        CodeableConceptDt classCodeDt = new CodeableConceptDt();
        CodingDt classCodingDt = new CodingDt();
        classCodingDt.setSystem(medCodeableConcept.get("system").trim());
        classCodingDt.setCode(medCodeableConcept.get("code").trim());
        classCodingDt.setDisplay(medCodeableConcept.get("display").trim());
        classCodeDt.addCoding(classCodingDt);
        encounter.setPriority(classCodeDt);
	    	
         //set reason
	    Map<String, String> reasonNP = HapiUtils.convertToJsonMap(dafEncounter.getReason());
		List<CodeableConceptDt> theReasonNP= new ArrayList<CodeableConceptDt>();
		CodeableConceptDt rnpCC= new CodeableConceptDt();
		CodingDt rnpCoding=new CodingDt();
		rnpCoding.setSystem(reasonNP.get("system"));
		rnpCoding.setCode(reasonNP.get("code"));
		rnpCoding.setDisplay(reasonNP.get("display"));
		//bodysiteCC.addCoding(rnpCoding);
	    rnpCC.setText(reasonNP.get("text"));
		rnpCC.addCoding(rnpCoding);
		theReasonNP.add(rnpCC);
		encounter.setReason(theReasonNP);
		
	    //set Participant
	    List<Participant> participants = new  ArrayList<Participant>();
		 Participant participant= new Participant();
		 ResourceReferenceDt individual = new ResourceReferenceDt();
		 String practitionerId = "Practitioner/" + Integer.toString(dafEncounter.getParticipant());
		 individual.setReference(practitionerId);
		// individual.setDisplay("display");
		 participant.setIndividual(individual );
		 participants.add(participant);
	     encounter.setParticipant(participants);
	    	
          //set  length
	     Map<String, String> lengthRequest = HapiUtils.convertToJsonMap(dafEncounter.getLength());
	  	 //DurationDt classCodeDt1 = new DurationDt();
	       DurationDt simpleQuantityDt = new DurationDt();
	       simpleQuantityDt.setCode(lengthRequest.get("code"));
	       simpleQuantityDt.setValue(Long.parseLong(lengthRequest.get("value")));
	       simpleQuantityDt.setUnit(lengthRequest.get("unit"));
	       simpleQuantityDt.setSystem(lengthRequest.get("system"));
	      //classCodeDt.setDose(simpleQuantityDt);
	     // classCodeDt1.add( simpleQuantityDt);
	        encounter.setLength(simpleQuantityDt);
		
	    // set hospitalization
	    Map<String, String> hospAdmitSource = HapiUtils.convertToJsonMap(dafEncounter.getHospAdmitSource());
	      Hospitalization hospitalization = new Hospitalization();
	        
	        // set preAdmissionIdentifier
	        Map<String, String> preAdmissionIdentifier = HapiUtils.convertToJsonMap(dafEncounter.getHospPreAdmissionIdentifier());
	        List<IdentifierDt> preidentifier = new ArrayList<IdentifierDt>();
	        IdentifierDt preadmList = new IdentifierDt();
	        preadmList.setUse(IdentifierUseEnum.valueOf(preAdmissionIdentifier.get("use").toUpperCase().trim()));
	        preadmList.setSystem(preAdmissionIdentifier.get("system"));
	        preadmList.setValue(preAdmissionIdentifier.get("value"));
	        preidentifier.add(preadmList);
			hospitalization.setPreAdmissionIdentifier(preadmList );
	        
	         // set admitSource
	       BoundCodeableConceptDt<AdmitSourceEnum> admitSourceList = new BoundCodeableConceptDt<>();
	         CodingDt admitSource = new CodingDt();
	        admitSource.setCode(hospAdmitSource.get("code"));
	        admitSource.setSystem(hospAdmitSource.get("system"));
	        admitSource.setDisplay(hospAdmitSource.get("display"));
			admitSourceList.addCoding(admitSource );
			hospitalization.setAdmitSource(admitSourceList);
	        
			//set reAdmission
	      Map<String, String> reAdmission = HapiUtils.convertToJsonMap(dafEncounter.getHospReAdmission());
			CodeableConceptDt readmissionList = new CodeableConceptDt();
			CodingDt readmission = new CodingDt();
			readmission.setDisplay(reAdmission.get("display"));
			readmissionList.addCoding(readmission );
			hospitalization.setReAdmission(readmissionList );
	        
			//set dietPreference
	         Map<String, String> dietPreference = HapiUtils.convertToJsonMap(dafEncounter.getHospDietPreference());
		      List<CodeableConceptDt> dietList =  new ArrayList<CodeableConceptDt>();
		      CodeableConceptDt dietapp= new CodeableConceptDt();
			  CodingDt diet = new CodingDt();
			  diet.setSystem(dietPreference.get("system"));
			  diet.setCode(dietPreference.get("code"));
			  diet.setDisplay(dietPreference.get("display"));
			  dietapp.addCoding(diet);
			  dietList.add(dietapp);
			  hospitalization.setDietPreference(dietList);
	        
		   // set dischargeDisposition 
	       Map<String, String> hospDischarge = HapiUtils.convertToJsonMap(dafEncounter.getHospDischargeDisposition());
	        CodeableConceptDt dischargeList = new CodeableConceptDt();
			CodingDt discharge = new CodingDt();
			discharge.setSystem(hospDischarge.get("system"));
			discharge.setCode(hospDischarge.get("code"));
			discharge.setDisplay(hospDischarge.get("display"));
			dischargeList.addCoding(discharge );
			hospitalization.setDischargeDisposition(dischargeList);
		 	encounter.setHospitalization(hospitalization );
	       	        
          //set location
	      List<Location> locationList =  new ArrayList<Location>();
          Location locations = new Location();
          ResourceReferenceDt location = new ResourceReferenceDt();
          String locationId = "Location/" + Integer.toString(dafEncounter.getLocation());
          location.setReference(locationId);
         //location.setDisplay("Client's home");
         locations.setLocation(location);
         locationList.add(locations);
         encounter.setLocation(locationList);
	       
	      //set serviceProvider
	     ResourceReferenceDt serviceProvider = new ResourceReferenceDt();
	     String theId1 = "Organization/" + Integer.toString(dafEncounter.getServiceProvider());
	     serviceProvider.setReference(theId1);
	     //serviceProvider.setDisplay("display");
	     encounter.setServiceProvider(serviceProvider);
	      
	     return encounter;
	    }catch(Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }
	 }

}
