package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Immunization.ImmunizationPerformerComponent;
import org.hl7.fhir.r4.model.Immunization.ImmunizationProtocolAppliedComponent;
import org.hl7.fhir.r4.model.Immunization.ImmunizationStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.service.ImmunizationService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ImmunizationResourceProvider implements IResourceProvider {
	
	public static final String RESOURCE_TYPE = "Immunization";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    ImmunizationService service;

    public ImmunizationResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (ImmunizationService) context.getBean("immunizationService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Immunization.class;
	}
	
 	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Immunization/1/_history/4
	 * @param theId : Id of the Immunization
	 * @return : Object of Immunization information
	 */
	@Read(version=true)
    public Immunization readOrVread(@IdParam IdType theId) {
		String id;
		DafImmunization dafImmunization;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		if (theId.hasVersionIdPart()) {
		   // this is a vread  
		   dafImmunization = service.getImmunizationByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       dafImmunization = service.getImmunizationById(id);
		}
		return createImmunizationObject(dafImmunization);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=Immunization.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Immunization/1/_history
	 * @param theId : ID of the Immunization
	 * @return : List of Immunizations
	 */
	@History()
    public List<Immunization> getImmunizationHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafImmunization> dafImmunizationList = service.getImmunizationHistoryById(id);
        
        List<Immunization> immunizationList = new ArrayList<Immunization>();
        for (DafImmunization dafImmunization : dafImmunizationList) {
        	immunizationList.add(createImmunizationObject(dafImmunization));
        }
        
        return immunizationList;
	}
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theDate
	 * @param thePerformer
	 * @param theReaction
	 * @param theLotNumber 
	 * @param theStatusReason
	 * @param theReasonCode
	 * @param theManufacturer
	 * @param theTargetDisease
	 * @param thePatient
	 * @param theSeries
	 * @param theVaccineCode
	 * @param theReasonReference
	 * @param theLocation
	 * @param theStatus
	 * @param theReactionDate
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
        @OptionalParam(name = Immunization.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "Business identifier")
        @OptionalParam(name = Immunization.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "Vaccination  (non)-Administration Date")
        @OptionalParam(name = Immunization.SP_DATE)
        DateRangeParam theDate,
        
        @Description(shortDefinition = "The practitioner or organization who played a role in the vaccination")
        @OptionalParam(name = Immunization.SP_PERFORMER)
        ReferenceAndListParam thePerformer,
        
        @Description(shortDefinition = "Additional information on reaction")
        @OptionalParam(name = Immunization.SP_REACTION)
        ReferenceAndListParam theReaction,
        
        @Description(shortDefinition = "Vaccine Lot Number")
        @OptionalParam(name = Immunization.SP_LOT_NUMBER)
        StringAndListParam theLotNumber,
        
        @Description(shortDefinition = "Reason why the vaccine was not administered")
        @OptionalParam(name = Immunization.SP_STATUS_REASON)
        TokenAndListParam theStatusReason,
        
        @Description(shortDefinition = "Reason why the vaccine was administered")
        @OptionalParam(name = Immunization.SP_REASON_CODE)
        TokenAndListParam theReasonCode,
        
        @Description(shortDefinition = "Vaccine Manufacturer")
        @OptionalParam(name = Immunization.SP_MANUFACTURER)
        ReferenceAndListParam theManufacturer,
        
        @Description(shortDefinition = "The target disease the dose is being administered against")
        @OptionalParam(name = Immunization.SP_TARGET_DISEASE)
        TokenAndListParam theTargetDisease,
        
        @Description(shortDefinition = "The patient for the vaccination record")
        @OptionalParam(name = Immunization.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "The series being followed by the provider")
        @OptionalParam(name = Immunization.SP_SERIES)
        StringAndListParam theSeries,
        
        @Description(shortDefinition = "Vaccine Product Administered")
        @OptionalParam(name = Immunization.SP_VACCINE_CODE)
        TokenAndListParam theVaccineCode,
        
        @Description(shortDefinition = "Why immunization occurred")
        @OptionalParam(name = Immunization.SP_REASON_REFERENCE)
        ReferenceAndListParam theReasonReference,
        
        @Description(shortDefinition="The service delivery location or facility in which the vaccine was / was to be administered")
        @OptionalParam(name=Immunization.SP_LOCATION)
        ReferenceAndListParam theLocation,
        
        @Description(shortDefinition = "Immunization event status")
        @OptionalParam(name = Immunization.SP_STATUS)
        TokenAndListParam theStatus,
        
        @Description(shortDefinition = "Why immunization occurred")
        @OptionalParam(name = Immunization.SP_REACTION_DATE)
        DateRangeParam theReactionDate,
        
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(Immunization.SP_RES_ID, theId);
        paramMap.add(Immunization.SP_IDENTIFIER, theIdentifier);
        paramMap.add(Immunization.SP_DATE, theDate);
        paramMap.add(Immunization.SP_PERFORMER, thePerformer);
        paramMap.add(Immunization.SP_REACTION, theReaction);
        paramMap.add(Immunization.SP_LOT_NUMBER, theLotNumber);
        paramMap.add(Immunization.SP_STATUS_REASON, theStatusReason);
        paramMap.add(Immunization.SP_REASON_CODE, theReasonCode);
        paramMap.add(Immunization.SP_MANUFACTURER, theManufacturer);
        paramMap.add(Immunization.SP_TARGET_DISEASE, theTargetDisease);
        paramMap.add(Immunization.SP_PATIENT, thePatient);
        paramMap.add(Immunization.SP_SERIES, theSeries);
        paramMap.add(Immunization.SP_VACCINE_CODE, theVaccineCode);
        paramMap.add(Immunization.SP_REASON_REFERENCE, theReasonReference);
        paramMap.add(Immunization.SP_LOCATION, theLocation);
        paramMap.add(Immunization.SP_STATUS, theStatus);
        paramMap.add(Immunization.SP_REACTION_DATE, theReactionDate);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<DafImmunization> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> immunizationList = new ArrayList<IBaseResource>();
				List<String> ids = new ArrayList<String>();
                for(DafImmunization dafImmunization : results){
					Immunization immunization = createImmunizationObject(dafImmunization);
					immunizationList.add(immunization);
					ids.add(((IdType)immunization.getIdElement()).getResourceType()+"/"+((IdType)immunization.getIdElement()).getIdPart());
				}

				if(theRevIncludes.size() >0 ){
					immunizationList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
				}
                return immunizationList;
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
     * This method converts DafDocumentReference object to DocumentReference object
     * @param dafImmunization : DafDocumentReference Immunization object
     * @return : DocumentReference Immunization object
     */
    private Immunization createImmunizationObject(DafImmunization dafImmunization) {
        Immunization immunization = new Immunization();
        JSONObject immunizationJSON = new JSONObject(dafImmunization.getData());

        // Set version
        if(!(immunizationJSON.isNull("meta"))) {
        	if(!(immunizationJSON.getJSONObject("meta").isNull("versionId"))) {
        		immunization.setId(new IdType(RESOURCE_TYPE, immunizationJSON.getString("id") + "", immunizationJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				immunization.setId(new IdType(RESOURCE_TYPE, immunizationJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
        	immunization.setId(new IdType(RESOURCE_TYPE, immunizationJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(immunizationJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = immunizationJSON.getJSONArray("identifier");
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
        	immunization.setIdentifier(identifiers);
        }
        
        //set status
      	if (!immunizationJSON.isNull("status")) {
      		immunization.setStatus(ImmunizationStatus.fromCode(immunizationJSON.getString("status")));
      	}
      	
      	//set primarySource
      	if (!immunizationJSON.isNull("primarySource")) {
			immunization.setPrimarySource(immunizationJSON.getBoolean("primarySource"));
		}
      	
      //set location
      	if (!immunizationJSON.isNull("location")) {
			JSONObject locationJSON = immunizationJSON.getJSONObject("location");
			Reference theLocation = new Reference();
			if (!locationJSON.isNull("reference")) {
				theLocation.setReference(locationJSON.getString("reference"));
			}
			if (!locationJSON.isNull("display")) {
				theLocation.setDisplay(locationJSON.getString("display"));
			}
			if (!locationJSON.isNull("type")) {
				theLocation.setType(locationJSON.getString("type"));
			}
			immunization.setLocation(theLocation);
		}
      	//set status reason
      	if (!immunizationJSON.isNull("statusReason")) {
			JSONObject statusReasonJSON = immunizationJSON.getJSONObject("statusReason");
			CodeableConcept statusReason = new CodeableConcept();
			
			if (!statusReasonJSON.isNull("coding")) {
				JSONArray codingJSON = statusReasonJSON.getJSONArray("coding");
				List<Coding> codingList = new ArrayList<Coding>();
				int noOfCodings = codingJSON.length();
				
				for (int j = 0; j < noOfCodings ; j++) {
					Coding theCoding = new Coding();
					
					if (!codingJSON.getJSONObject(j).isNull("system")) {
						theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
					}
					if (!codingJSON.getJSONObject(j).isNull("code")) {
						theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
						
					}
					if (!codingJSON.getJSONObject(j).isNull("display")) {
						theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
						
					}
					codingList.add(theCoding);
				}
				statusReason.setCoding(codingList);
			}
			immunization.setStatusReason(statusReason);
		}
      	
      	//set vaccinecode
      	if (!immunizationJSON.isNull("vaccineCode")) {
      		JSONObject vaccineCodeJSON = immunizationJSON.getJSONObject("vaccineCode");
      		CodeableConcept vaccineCode = new CodeableConcept();
      		if (!vaccineCodeJSON.isNull("coding")) {
      			JSONArray codingJSON = vaccineCodeJSON.getJSONArray("coding");
				List<Coding> codingList = new ArrayList<Coding>();
				int noOfCodings = codingJSON.length();
				
				for (int j = 0; j < noOfCodings; j++) {
					Coding theCoding = new Coding();
					
					if (!codingJSON.getJSONObject(j).isNull("system")) {
						theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
					}
					if (!codingJSON.getJSONObject(j).isNull("code")) {
						theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
						
					}
					if (!codingJSON.getJSONObject(j).isNull("display")) {
						theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
					}
					codingList.add(theCoding);
				}
				vaccineCode.setCoding(codingList);
				
			}
      		if (!vaccineCodeJSON.isNull("text")) {
				vaccineCode.setText(vaccineCodeJSON.getString("text"));
			}
			immunization.setVaccineCode(vaccineCode);
		}
      	
      	//set Patient
      	if (!immunizationJSON.isNull("patient")) {
			JSONObject patientJSON = immunizationJSON.getJSONObject("patient");
			Reference thePatient =new Reference();
			if (!patientJSON.isNull("reference")) {
				thePatient.setReference(patientJSON.getString("reference"));
			}
			if (!patientJSON.isNull("display")) {
				thePatient.setDisplay(patientJSON.getString("display"));
			}
			if (!patientJSON.isNull("type")) {
				thePatient.setType(patientJSON.getString("type"));
			}
			immunization.setPatient(thePatient);
		}
      	
      	//set occurrenceDateTime
      	if (!immunizationJSON.isNull("occurrenceDateTime")) {
      		DateTimeType dateTimeType= new DateTimeType();
      		Date occurrenceDateTime = CommonUtil.convertStringToDate(immunizationJSON.getString("occurrenceDateTime"));
      		dateTimeType.setValue(occurrenceDateTime);
      		immunization.setOccurrence(dateTimeType);
		}
      	
      	//occurrenceString
      	if (!immunizationJSON.isNull("occurrenceString")) {
      		StringType stringType = new StringType();
      		String string = immunizationJSON.getString("occurrenceString");
      		stringType.setValue(string);
      		immunization.setOccurrence(stringType);
			
		}
        
      	
        //set manufacturer
      	if (!immunizationJSON.isNull("manufacturer")) {
      		JSONObject manufacturerJSON = immunizationJSON.getJSONObject("manufacturer");
      		Reference theManufacturer = new Reference();
			if (!manufacturerJSON.isNull("reference")) {
				theManufacturer.setReference(manufacturerJSON.getString("reference"));
			}
			if (!manufacturerJSON.isNull("display")) {
				theManufacturer.setDisplay(manufacturerJSON.getString("display"));
			}
			if (!manufacturerJSON.isNull("type")) {
				theManufacturer.setType(manufacturerJSON.getString("type"));
			}
			immunization.setManufacturer(theManufacturer);
		}
		
        //set lotnumber
        if (!immunizationJSON.isNull("lotNumber")) {
        	immunization.setLotNumber(immunizationJSON.getString("lotNumber"));
		}
        
        //set expirationDate
        if (!immunizationJSON.isNull("expirationDate")) {
			Date expirationDate = CommonUtil.convertStringToDate(immunizationJSON.getString("expirationDate"));
			immunization.setExpirationDate(expirationDate);
		}
        
        //set performer
        if (!immunizationJSON.isNull("performer")) {
        	JSONArray performerJSON = immunizationJSON.getJSONArray("performer");
			List<ImmunizationPerformerComponent> performerList = new ArrayList<ImmunizationPerformerComponent>();
			int noOfPerformers = performerJSON.length();
			
			for (int i = 0; i < noOfPerformers; i++) {
				ImmunizationPerformerComponent thePerformer = new ImmunizationPerformerComponent();
				
				if (!performerJSON.getJSONObject(i).isNull("function")) {
					JSONObject functionJSON = performerJSON.getJSONObject(i).getJSONObject("function");
					CodeableConcept theFunction = new CodeableConcept();
					
					if (!functionJSON.isNull("coding")) {
						JSONArray codingJSON = functionJSON.getJSONArray("coding");
						List<Coding> codingList = new ArrayList<Coding>();
						int noOfCodings = codingJSON.length();
						
						for (int j = 0; j < noOfCodings; j++) {
							Coding theCoding = new Coding();
							
							if (!codingJSON.getJSONObject(j).isNull("system")) {
								theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
							}
							if (!codingJSON.getJSONObject(j).isNull("code")) {
								theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
							}
							codingList.add(theCoding);
						}
						theFunction.setCoding(codingList);
					}
					thePerformer.setFunction(theFunction);
				}
					
				if (!performerJSON.getJSONObject(i).isNull("actor")) {
					JSONObject actorJSON = performerJSON.getJSONObject(i).getJSONObject("actor");
					Reference theActor = new Reference();
					if (!actorJSON.isNull("reference")) {
							theActor.setReference(actorJSON.getString("reference"));
					}
					thePerformer.setActor(theActor);
				}
				performerList.add(thePerformer);
			}
			immunization.setPerformer(performerList);
		}
        
        //set reasoncode
        if (!immunizationJSON.isNull("reasonCode")) {
			JSONArray reasonCodeJSON = immunizationJSON.getJSONArray("reasonCode");
			List<CodeableConcept> reasonCodeList =  new ArrayList<CodeableConcept>();
			int noOfReasonCodes = reasonCodeJSON.length();
			
			for (int i = 0; i < noOfReasonCodes; i++) {
				CodeableConcept theReasonCode = new CodeableConcept();
				if (!reasonCodeJSON.getJSONObject(i).isNull("coding")) {
					 JSONArray codingJSON = reasonCodeJSON.getJSONObject(i).getJSONArray("coding");
					 List<Coding> codingList = new ArrayList<Coding>();
					 int noOfCodings = codingJSON.length();
					 
					 for (int j = 0; j < noOfCodings; j++) {
						 Coding theCoding = new Coding();
						 if (!codingJSON.getJSONObject(j).isNull("system")) {
							 theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
						}
						 if (!codingJSON.getJSONObject(j).isNull("code")) {
							theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
						}
						 codingList.add(theCoding);
					}
					 theReasonCode.setCoding(codingList);
				}
				reasonCodeList.add(theReasonCode);
			}
			immunization.setReasonCode(reasonCodeList);
		}
        //set series and targetDisease
        if (!immunizationJSON.isNull("protocolApplied")) {
			JSONArray protocolAppliedJSON = immunizationJSON.getJSONArray("protocolApplied");
			List<ImmunizationProtocolAppliedComponent>  protocolAppliedList = new ArrayList<ImmunizationProtocolAppliedComponent>();
			int noOfProtocolsApplied = protocolAppliedJSON.length();
			for (int i = 0; i < noOfProtocolsApplied; i++) {
				ImmunizationProtocolAppliedComponent theProtocolApplied = new ImmunizationProtocolAppliedComponent();
				
				if (!protocolAppliedJSON.getJSONObject(i).isNull("series")) {
					theProtocolApplied.setSeries(protocolAppliedJSON.getJSONObject(i).getString("series"));
				}
				if (!protocolAppliedJSON.getJSONObject(i).isNull("targetDisease")) {
					JSONArray targetDiseaseJSON = protocolAppliedJSON.getJSONObject(i).getJSONArray("targetDisease");
					List<CodeableConcept> targetDiseaseList = new ArrayList<CodeableConcept>();
					int noOfTargetDiseases = targetDiseaseJSON.length();
					
					for (int j = 0; j < noOfTargetDiseases; j++) {
						CodeableConcept theTargetDisease = new CodeableConcept();
						
						if (!targetDiseaseJSON.getJSONObject(j).isNull("coding")) {
							 JSONArray codingJSON = targetDiseaseJSON.getJSONObject(j).getJSONArray("coding");
							 List<Coding> codingList = new ArrayList<Coding>();
							 int noOfCodings = codingJSON.length();
							 for (int k = 0; k < noOfCodings; k++) {
								Coding theCoding = new Coding();
								
								if (!codingJSON.getJSONObject(k).isNull("system")) {
									theCoding.setSystem(codingJSON.getJSONObject(k).getString("system"));
								}
								if (!codingJSON.getJSONObject(k).isNull("code")) {
									theCoding.setCode(codingJSON.getJSONObject(k).getString("code"));
								}
								codingList.add(theCoding);
							}
							 theTargetDisease.setCoding(codingList);
						}
						targetDiseaseList.add(theTargetDisease);
					}
					theProtocolApplied.setTargetDisease(targetDiseaseList);
				}
								 
				if (!protocolAppliedJSON.getJSONObject(i).isNull("doseNumberPositiveInt")) {
					PositiveIntType theDoseNumberPositiveInt = new PositiveIntType();
					theDoseNumberPositiveInt.setValue(protocolAppliedJSON.getJSONObject(i).getInt("doseNumberPositiveInt"));
					theProtocolApplied.setDoseNumber(theDoseNumberPositiveInt);
				}
				protocolAppliedList.add(theProtocolApplied);
			}
			immunization.setProtocolApplied(protocolAppliedList);
		}
        
        //set reportOrigin
        if (!immunizationJSON.isNull("reportOrigin")) {
		JSONObject codeJSON = immunizationJSON.getJSONObject("reportOrigin");
		CodeableConcept theCode = new CodeableConcept();
		if (!codeJSON.isNull("coding")) {
			JSONArray codingJSON = codeJSON.getJSONArray("coding");
			List<Coding> codingLists = new ArrayList<Coding>();
			int noOfCodings = codingJSON.length();
			for (int i = 0; i < noOfCodings; i++) {
				Coding theCoding = new Coding();
				if (!codingJSON.getJSONObject(i).isNull("system")) {
					theCoding.setSystem(codingJSON.getJSONObject(i).getString("system"));
				}
				if (!codingJSON.getJSONObject(i).isNull("code")) {
					theCoding.setCode(codingJSON.getJSONObject(i).getString("code"));
				}
				if (!codingJSON.getJSONObject(i).isNull("display")) {
					theCoding.setDisplay(codingJSON.getJSONObject(i).getString("display"));
				}
				if (!codingJSON.getJSONObject(i).isNull("userSelected")) {
					theCoding.setUserSelected(codingJSON.getJSONObject(i).getBoolean("userSelected"));
				}
				codingLists.add(theCoding);
			}
			theCode.setCoding(codingLists);
		}
		if (!codeJSON.isNull("text")) {
			theCode.setText(codeJSON.getString("text"));
		}
		immunization.setReportOrigin(theCode);
	}
		return immunization;
      }
}
