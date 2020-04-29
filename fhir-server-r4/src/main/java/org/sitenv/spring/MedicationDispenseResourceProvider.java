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
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent;
import org.hl7.fhir.r4.model.MedicationDispense.MedicationDispensePerformerComponent;
import org.hl7.fhir.r4.model.Timing.TimingRepeatComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedicationDispense;
import org.sitenv.spring.service.MedicationDispenseService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MedicationDispenseResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "MedicationDispense";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    MedicationDispenseService service;
    
    public MedicationDispenseResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (MedicationDispenseService) context.getBean("medicationDispenseService");
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return MedicationDispense.class;
	}
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationDispense/1/_history/3.0
	 * @param theId : Id of the MedicationDispense
	 * @return : Object of MedicationDispense information
	 */
	@Read(version=true)
    public MedicationDispense readOrVread(@IdParam IdType theId) {
		String id;
		DafMedicationDispense dafMedicationDispense;
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
			dafMedicationDispense = service.getMedicationDispenseByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			dafMedicationDispense = service.getMedicationDispenseById(id);
		}
		return createMedicationDispenseObject(dafMedicationDispense);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=MedicationDispense.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationDispense/1/_history
	 * @param theId : ID of the MedicationDispense
	 * @return : List of MedicationDispense's
	 */
	@History()
    public List<MedicationDispense> getMedicationDispenseHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafMedicationDispense> dafMedicationDispenseList = service.getMedicationDispenseHistoryById(id);
        
        List<MedicationDispense> medicationDispenseList = new ArrayList<MedicationDispense>();
        for (DafMedicationDispense dafMedicationDispense : dafMedicationDispenseList) {
        	medicationDispenseList.add(createMedicationDispenseObject(dafMedicationDispense));
        }
        
        return medicationDispenseList;
	}
	/**
	 *  The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theContext
	 * @param thePatient
	 * @param thePrescription
	 * @param theMedication
	 * @param theWhenPrepared
	 * @param theWhenHandedOver
	 * @param theType
	 * @param theDestination
	 * @param theReceiver
	 * @param theCode
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = MedicationDispense.SP_RES_ID)
        TokenAndListParam theId,

        @Description(shortDefinition = "A MedicationAdministration identifier")
        @OptionalParam(name = MedicationDispense.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "Returns dispenses with a specified dispense status")
        @OptionalParam(name = MedicationDispense.SP_STATUS)
        TokenAndListParam theStatus,
        
        @Description(shortDefinition = "Returns dispenses with a specific context (episode or episode of care)")
        @OptionalParam(name = MedicationDispense.SP_CONTEXT)
        ReferenceAndListParam theContext,
        
        @Description(shortDefinition = "The identity of a patient to list dispenses  for")
        @OptionalParam(name = MedicationDispense.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "The identity of a prescription to list dispenses from")
        @OptionalParam(name = MedicationDispense.SP_PRESCRIPTION)
        ReferenceAndListParam thePrescription,
        
        @Description(shortDefinition = "Returns dispenses of this medicine resource")
        @OptionalParam(name = MedicationDispense.SP_MEDICATION)
        ReferenceAndListParam theMedication,
        
        @Description(shortDefinition = "Returns dispenses prepared on this date")
        @OptionalParam(name = MedicationDispense.SP_WHENPREPARED)
        DateRangeParam theWhenPrepared,
        
        @Description(shortDefinition = "Returns dispenses handed over on this date")
        @OptionalParam(name = MedicationDispense.SP_WHENHANDEDOVER)
        DateRangeParam theWhenHandedOver,
        
        @Description(shortDefinition = "Returns dispenses of a specific type")
        @OptionalParam(name = MedicationDispense.SP_TYPE)
        TokenAndListParam theType,
        
        @Description(shortDefinition = "Returns dispenses that should be sent to a specific destination")
        @OptionalParam(name = MedicationDispense.SP_DESTINATION)
        ReferenceAndListParam theDestination,
        
        @Description(shortDefinition = "The identity of a receiver to list dispenses for")
        @OptionalParam(name = MedicationDispense.SP_RECEIVER)
        ReferenceAndListParam theReceiver,
        
        @Description(shortDefinition = "Returns dispenses of this medicine code")
        @OptionalParam(name = MedicationDispense.SP_CODE)
        TokenAndListParam theCode,


        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(MedicationDispense.SP_RES_ID, theId);
            paramMap.add(MedicationDispense.SP_IDENTIFIER, theIdentifier);
            paramMap.add(MedicationDispense.SP_STATUS, theStatus);
            paramMap.add(MedicationDispense.SP_CONTEXT, theContext);
            paramMap.add(MedicationDispense.SP_PATIENT, thePatient);
            paramMap.add(MedicationDispense.SP_PRESCRIPTION, thePatient);
            paramMap.add(MedicationDispense.SP_WHENPREPARED, theWhenPrepared);
            paramMap.add(MedicationDispense.SP_WHENHANDEDOVER, theWhenHandedOver);
            paramMap.add(MedicationDispense.SP_TYPE, theType);
            paramMap.add(MedicationDispense.SP_MEDICATION, theMedication);
            paramMap.add(MedicationDispense.SP_DESTINATION, theDestination);
            paramMap.add(MedicationDispense.SP_RECEIVER, theReceiver);
            paramMap.add(MedicationDispense.SP_CODE, theCode);
            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<DafMedicationDispense> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> medicationDispenseList = new ArrayList<IBaseResource>();
                    for(DafMedicationDispense dafMedicationDispense : results){
                    	medicationDispenseList.add(createMedicationDispenseObject(dafMedicationDispense));
                    }
                    return medicationDispenseList;
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
     * @param dafMedicationDispense : DafDocumentReference MedicationDispense object
     * @return : DocumentReference MedicationDispense object
     */
    private MedicationDispense createMedicationDispenseObject(DafMedicationDispense dafMedicationDispense) {
    	MedicationDispense medicationDispense = new MedicationDispense();
        JSONObject medicationDispenseJSON = new JSONObject(dafMedicationDispense.getData());

        // Set version
        if(!(medicationDispenseJSON.isNull("meta"))) {
        	if(!(medicationDispenseJSON.getJSONObject("meta").isNull("versionId"))) {
                medicationDispense.setId(new IdType(RESOURCE_TYPE, medicationDispenseJSON.getString("id") + "", medicationDispenseJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				medicationDispense.setId(new IdType(RESOURCE_TYPE, medicationDispenseJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
            medicationDispense.setId(new IdType(RESOURCE_TYPE, medicationDispenseJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(medicationDispenseJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = medicationDispenseJSON.getJSONArray("identifier");
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
        	medicationDispense.setIdentifier(identifiers);
        }
        
        //set status
        if(!(medicationDispenseJSON.isNull("status"))) {
        	medicationDispense.setStatus(medicationDispenseJSON.getString("status"));
        }

        //Set subject
        if(!(medicationDispenseJSON.isNull("subject"))) {
        	JSONObject subjectJSON = medicationDispenseJSON.getJSONObject("subject");
        	Reference theSubject = new Reference();
        	if(!subjectJSON.isNull("reference")) {
        		theSubject.setReference(subjectJSON.getString("reference"));
        	}
        	if(!subjectJSON.isNull("display")) {
        		theSubject.setDisplay(subjectJSON.getString("display"));
        	}
        	if(!subjectJSON.isNull("type")) {
        		theSubject.setType(subjectJSON.getString("type"));
        	}
        	medicationDispense.setSubject(theSubject);
        }
        //Set context
        if(!(medicationDispenseJSON.isNull("context"))) {
        	JSONObject contextJSON = medicationDispenseJSON.getJSONObject("context");
        	Reference theContext = new Reference();
        	if(!contextJSON.isNull("reference")) {
        		theContext.setReference(contextJSON.getString("reference"));
        	}
        	if(!contextJSON.isNull("display")) {
        		theContext.setDisplay(contextJSON.getString("display"));
        	}
        	if(!contextJSON.isNull("type")) {
        		theContext.setType(contextJSON.getString("type"));
        	}
        	medicationDispense.setContext(theContext);
        }
        
        //Set performer
        if(!(medicationDispenseJSON.isNull("performer"))) {
        	JSONArray performerJSON = medicationDispenseJSON.getJSONArray("performer");
        	int noOfPerformers = performerJSON.length();
        	List<MedicationDispensePerformerComponent> performerList = new ArrayList<MedicationDispensePerformerComponent>();
        	for(int p = 0; p < noOfPerformers; p++) {
        		MedicationDispensePerformerComponent thePerformer = new MedicationDispensePerformerComponent();
            	if(!performerJSON.getJSONObject(p).isNull("actor")) {
            		JSONObject actorJSON = performerJSON.getJSONObject(p).getJSONObject("actor");
            		Reference theActor = new Reference();
            		if(!actorJSON.isNull("reference")) {
            			theActor.setReference(actorJSON.getString("reference"));
            		}
            		if(!actorJSON.isNull("display")) {
            			theActor.setDisplay(actorJSON.getString("display"));
            		}
            		if(!actorJSON.isNull("type")) {
            			theActor.setType(actorJSON.getString("type"));
            		}
            		thePerformer.setActor(theActor);
            	}
            	if(!performerJSON.getJSONObject(p).isNull("function")) {
            		JSONObject functionJSON = performerJSON.getJSONObject(p).getJSONObject("function");
            		CodeableConcept theFunction = new CodeableConcept();
            		if(!(functionJSON.isNull("coding"))) {
        				JSONArray fCodingJSON = functionJSON.getJSONArray("coding");
        				int noOfFCodings = fCodingJSON.length();
        				List<Coding> fCodingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfFCodings; j++) {
        					Coding theFCoding = new Coding();

        					if(!(fCodingJSON.getJSONObject(j).isNull("system"))) {
        						theFCoding.setSystem(fCodingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(fCodingJSON.getJSONObject(j).isNull("code"))) {
        						theFCoding.setCode(fCodingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(fCodingJSON.getJSONObject(j).isNull("display"))) {
        						theFCoding.setDisplay(fCodingJSON.getJSONObject(j).getString("display"));
        					}
        					fCodingList.add(theFCoding);
        				}
        				theFunction.setCoding(fCodingList);
        			}
            		if(!performerJSON.getJSONObject(p).isNull("text")) {
            			theFunction.setText(performerJSON.getJSONObject(p).getString("text"));
            		}
            		thePerformer.setFunction(theFunction);
            	}
            	performerList.add(thePerformer);
        	}
        	medicationDispense.setPerformer(performerList);
        }
        
        //Set partOf
        if(!(medicationDispenseJSON.isNull("partOf"))) {
        	JSONArray partOfJSON = medicationDispenseJSON.getJSONArray("partOf");
        	int noOfPartOf = partOfJSON.length();
        	List<Reference> partOfList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfPartOf; i++) {
        		Reference thePartOf = new Reference();
        		if(!partOfJSON.getJSONObject(i).isNull("display")) {
            		thePartOf.setDisplay(partOfJSON.getJSONObject(i).getString("display"));
        		}
        		if(!partOfJSON.getJSONObject(i).isNull("type")) {
            		thePartOf.setType(partOfJSON.getJSONObject(i).getString("type"));
        		}
        		if(!partOfJSON.getJSONObject(i).isNull("reference")) {
            		thePartOf.setReference(partOfJSON.getJSONObject(i).getString("reference"));
        		}
        		partOfList.add(thePartOf);
        	}
        	medicationDispense.setPartOf(partOfList);
        }
        
        //Set medicationCodeableConcept
       	if(!(medicationDispenseJSON.isNull("medicationCodeableConcept"))) {
    		JSONObject medicationCodeableConceptJSON = medicationDispenseJSON.getJSONObject("medicationCodeableConcept");
    		CodeableConcept theMedicationCodeableConcept = new CodeableConcept();
    		if(!medicationCodeableConceptJSON.isNull("coding")) {
    			JSONArray mCodingJSON = medicationCodeableConceptJSON.getJSONArray("coding");
    			int noOfMCoding = mCodingJSON.length();
    			List<Coding> mCodingList = new ArrayList<Coding>();
    			for(int s = 0; s < noOfMCoding; s++) {
    				Coding theMCoding = new Coding();
    				if(!mCodingJSON.getJSONObject(s).isNull("system")) {
    					theMCoding.setSystem(mCodingJSON.getJSONObject(s).getString("system"));
    				}
    				if(!mCodingJSON.getJSONObject(s).isNull("code")) {
    					theMCoding.setCode(mCodingJSON.getJSONObject(s).getString("code"));
    				}
    				if(!mCodingJSON.getJSONObject(s).isNull("display")) {
    					theMCoding.setDisplay(mCodingJSON.getJSONObject(s).getString("display"));
    				}
    				mCodingList.add(theMCoding);
    			}
    			theMedicationCodeableConcept.setCoding(mCodingList);
    		}
    		if(!medicationCodeableConceptJSON.isNull("text")) {
    			theMedicationCodeableConcept.setText(medicationCodeableConceptJSON.getString("text"));
    		}
    		medicationDispense.setMedication(theMedicationCodeableConcept);
    	}
        
        //Set medicationReference
        if(!(medicationDispenseJSON.isNull("medicationReference"))) {
        	JSONObject medicationReferenceJSON = medicationDispenseJSON.getJSONObject("medicationReference");
        	Reference theMedicationReference = new Reference();
        	if(!medicationReferenceJSON.isNull("reference")) {
        		theMedicationReference.setReference(medicationReferenceJSON.getString("reference"));
        	}
        	if(!medicationReferenceJSON.isNull("display")) {
        		theMedicationReference.setDisplay(medicationReferenceJSON.getString("display"));
        	}
        	if(!medicationReferenceJSON.isNull("type")) {
        		theMedicationReference.setType(medicationReferenceJSON.getString("type"));
        	}
        	medicationDispense.setMedication(theMedicationReference);
        }
        //Set location
        if(!(medicationDispenseJSON.isNull("location"))) {
        	JSONObject locationJSON = medicationDispenseJSON.getJSONObject("location");
        	Reference theLocation = new Reference();
        	if(!locationJSON.isNull("reference")) {
        		theLocation.setReference(locationJSON.getString("reference"));
        	}
        	if(!locationJSON.isNull("display")) {
        		theLocation.setDisplay(locationJSON.getString("display"));
        	}
        	if(!locationJSON.isNull("type")) {
        		theLocation.setType(locationJSON.getString("type"));
        	}
        	medicationDispense.setLocation(theLocation);
        }
        //Set destination
        if(!(medicationDispenseJSON.isNull("destination"))) {
        	JSONObject destinationJSON = medicationDispenseJSON.getJSONObject("destination");
        	Reference theDestination = new Reference();
        	if(!destinationJSON.isNull("reference")) {
        		theDestination.setReference(destinationJSON.getString("reference"));
        	}
        	if(!destinationJSON.isNull("display")) {
        		theDestination.setDisplay(destinationJSON.getString("display"));
        	}
        	if(!destinationJSON.isNull("type")) {
        		theDestination.setType(destinationJSON.getString("type"));
        	}
        	medicationDispense.setDestination(theDestination);
        }
        //Set context
        if(!(medicationDispenseJSON.isNull("context"))) {
        	JSONObject contextJSON = medicationDispenseJSON.getJSONObject("context");
        	Reference theContext = new Reference();
        	if(!contextJSON.isNull("reference")) {
        		theContext.setReference(contextJSON.getString("reference"));
        	}
        	if(!contextJSON.isNull("display")) {
        		theContext.setDisplay(contextJSON.getString("display"));
        	}
        	if(!contextJSON.isNull("type")) {
        		theContext.setType(contextJSON.getString("type"));
        	}
        	medicationDispense.setContext(theContext);
        }
       	//Set supportingInformation
        if(!(medicationDispenseJSON.isNull("supportingInformation"))) {
        	JSONArray supportingInformationJSON = medicationDispenseJSON.getJSONArray("supportingInformation");
        	int noOfSupportInfo = supportingInformationJSON.length();
        	List<Reference> supportingInformationList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfSupportInfo; i++) {
        		Reference theSupportingInformation = new Reference();
        		if(!supportingInformationJSON.getJSONObject(i).isNull("display")) {
            		theSupportingInformation.setDisplay(supportingInformationJSON.getJSONObject(i).getString("display"));
        		}
        		if(!supportingInformationJSON.getJSONObject(i).isNull("type")) {
            		theSupportingInformation.setType(supportingInformationJSON.getJSONObject(i).getString("type"));
        		}
        		if(!supportingInformationJSON.getJSONObject(i).isNull("reference")) {
            		theSupportingInformation.setReference(supportingInformationJSON.getJSONObject(i).getString("reference"));
        		}
        		supportingInformationList.add(theSupportingInformation);
        	}
        	medicationDispense.setSupportingInformation(supportingInformationList);
        }
    	//Set receiver
        if(!(medicationDispenseJSON.isNull("receiver"))) {
        	JSONArray receiverJSON = medicationDispenseJSON.getJSONArray("receiver");
        	int noOfReceiver = receiverJSON.length();
        	List<Reference> receiverList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfReceiver; i++) {
        		Reference theReceiver = new Reference();
        		if(!receiverJSON.getJSONObject(i).isNull("display")) {
            		theReceiver.setDisplay(receiverJSON.getJSONObject(i).getString("display"));
        		}
        		if(!receiverJSON.getJSONObject(i).isNull("type")) {
            		theReceiver.setType(receiverJSON.getJSONObject(i).getString("type"));
        		}
        		if(!receiverJSON.getJSONObject(i).isNull("reference")) {
            		theReceiver.setReference(receiverJSON.getJSONObject(i).getString("reference"));
        		}
        		receiverList.add(theReceiver);
        	}
        	medicationDispense.setReceiver(receiverList);
        }
        //Set detectedIssue
        if(!(medicationDispenseJSON.isNull("detectedIssue"))) {
        	JSONArray detectedIssueJSON = medicationDispenseJSON.getJSONArray("detectedIssue");
        	int noOfDetectedIssue = detectedIssueJSON.length();
        	List<Reference> detectedIssueList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfDetectedIssue; i++) {
        		Reference theDetectedIssue = new Reference();
        		if(!detectedIssueJSON.getJSONObject(i).isNull("display")) {
            		theDetectedIssue.setDisplay(detectedIssueJSON.getJSONObject(i).getString("display"));
        		}
        		if(!detectedIssueJSON.getJSONObject(i).isNull("type")) {
            		theDetectedIssue.setType(detectedIssueJSON.getJSONObject(i).getString("type"));
        		}
        		if(!detectedIssueJSON.getJSONObject(i).isNull("reference")) {
            		theDetectedIssue.setReference(detectedIssueJSON.getJSONObject(i).getString("reference"));
        		}
        		detectedIssueList.add(theDetectedIssue);
        	}
        	medicationDispense.setDetectedIssue(detectedIssueList);
        }
        //Set eventHistory
        if(!(medicationDispenseJSON.isNull("eventHistory"))) {
        	JSONArray eventHistoryJSON = medicationDispenseJSON.getJSONArray("eventHistory");
        	int noOfEventHistory = eventHistoryJSON.length();
        	List<Reference> eventHistoryList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfEventHistory; i++) {
        		Reference theEventHistory = new Reference();
        		if(!eventHistoryJSON.getJSONObject(i).isNull("display")) {
            		theEventHistory.setDisplay(eventHistoryJSON.getJSONObject(i).getString("display"));
        		}
        		if(!eventHistoryJSON.getJSONObject(i).isNull("type")) {
            		theEventHistory.setType(eventHistoryJSON.getJSONObject(i).getString("type"));
        		}
        		if(!eventHistoryJSON.getJSONObject(i).isNull("reference")) {
            		theEventHistory.setReference(eventHistoryJSON.getJSONObject(i).getString("reference"));
        		}
        		eventHistoryList.add(theEventHistory);
        	}
        	medicationDispense.setEventHistory(eventHistoryList);
        }
        //Set category
        if(!(medicationDispenseJSON.isNull("category"))) {
        	JSONObject categoryJSON = medicationDispenseJSON.getJSONObject("category");
    		CodeableConcept theCategory = new CodeableConcept();
    		if(!(categoryJSON.isNull("coding"))) {
				JSONArray cCodingJSON = categoryJSON.getJSONArray("coding");
				int noOfCCodings = cCodingJSON.length();
				List<Coding> cCodingList = new ArrayList<Coding>();
				for(int j = 0; j < noOfCCodings; j++) {
					Coding theCCoding = new Coding();

					if(!(cCodingJSON.getJSONObject(j).isNull("system"))) {
						theCCoding.setSystem(cCodingJSON.getJSONObject(j).getString("system"));
					}
					if(!(cCodingJSON.getJSONObject(j).isNull("code"))) {
						theCCoding.setCode(cCodingJSON.getJSONObject(j).getString("code"));
					}
					if(!(cCodingJSON.getJSONObject(j).isNull("display"))) {
						theCCoding.setDisplay(cCodingJSON.getJSONObject(j).getString("display"));
					}
					cCodingList.add(theCCoding);
				}
				theCategory.setCoding(cCodingList);
			}
    		if(!categoryJSON.isNull("text")) {
        		theCategory.setText(categoryJSON.getString("text"));
    		}
    		medicationDispense.setCategory(theCategory);
        }
        //Set type
        if(!(medicationDispenseJSON.isNull("type"))) {
        	JSONObject typeJSON = medicationDispenseJSON.getJSONObject("type");
    		CodeableConcept theType = new CodeableConcept();
    		if(!(typeJSON.isNull("coding"))) {
				JSONArray tCodingJSON = typeJSON.getJSONArray("coding");
				int noOfTCodings = tCodingJSON.length();
				List<Coding> tCodingList = new ArrayList<Coding>();
				for(int j = 0; j < noOfTCodings; j++) {
					Coding theTCoding = new Coding();

					if(!(tCodingJSON.getJSONObject(j).isNull("system"))) {
						theTCoding.setSystem(tCodingJSON.getJSONObject(j).getString("system"));
					}
					if(!(tCodingJSON.getJSONObject(j).isNull("code"))) {
						theTCoding.setCode(tCodingJSON.getJSONObject(j).getString("code"));
					}
					if(!(tCodingJSON.getJSONObject(j).isNull("display"))) {
						theTCoding.setDisplay(tCodingJSON.getJSONObject(j).getString("display"));
					}
					tCodingList.add(theTCoding);
				}
				theType.setCoding(tCodingList);
			}
    		if(!typeJSON.isNull("text")) {
        		theType.setText(typeJSON.getString("text"));
    		}
    		medicationDispense.setType(theType);
        }
        //Set quantity
    	if(!(medicationDispenseJSON.isNull("quantity"))) {
    		JSONObject quantityJSON = medicationDispenseJSON.getJSONObject("quantity");
    		SimpleQuantity theQuantity = new SimpleQuantity();
    		if(!(quantityJSON.isNull("value"))) {
    			theQuantity.setValue(quantityJSON.getLong("value"));
    		}
    		if(!(quantityJSON.isNull("unit"))) {
    			theQuantity.setUnit(quantityJSON.getString("unit"));
    		}
    		if(!(quantityJSON.isNull("system"))) {
    			theQuantity.setSystem(quantityJSON.getString("system"));
    		}
    		if(!(quantityJSON.isNull("code"))) {
    			theQuantity.setCode(quantityJSON.getString("code"));
    		}
    		medicationDispense.setQuantity(theQuantity);
    	}
    	//Set whenPrepared
        if(!(medicationDispenseJSON.isNull("whenPrepared"))) {
        	Date whenPreparedDate = CommonUtil.convertStringToDate(medicationDispenseJSON.getString("whenPrepared"));
        	medicationDispense.setWhenPrepared(whenPreparedDate);
        }
        //Set whenHandedOver
        if(!(medicationDispenseJSON.isNull("whenHandedOver"))) {
        	Date whenHandedOverDate = CommonUtil.convertStringToDate(medicationDispenseJSON.getString("whenHandedOver"));
        	medicationDispense.setWhenHandedOver(whenHandedOverDate);
        }
    	//Set daysSupply
    	if(!(medicationDispenseJSON.isNull("daysSupply"))) {
    		JSONObject daysSupplyJSON = medicationDispenseJSON.getJSONObject("daysSupply");
    		SimpleQuantity theDaysSupply = new SimpleQuantity();
    		if(!(daysSupplyJSON.isNull("value"))) {
    			theDaysSupply.setValue(daysSupplyJSON.getLong("value"));
    		}
    		if(!(daysSupplyJSON.isNull("unit"))) {
    			theDaysSupply.setUnit(daysSupplyJSON.getString("unit"));
    		}
    		if(!(daysSupplyJSON.isNull("system"))) {
    			theDaysSupply.setSystem(daysSupplyJSON.getString("system"));
    		}
    		if(!(daysSupplyJSON.isNull("code"))) {
    			theDaysSupply.setCode(daysSupplyJSON.getString("code"));
    		}
    		medicationDispense.setDaysSupply(theDaysSupply);
    	}
    	//Set authorizingPrescription
        if(!(medicationDispenseJSON.isNull("authorizingPrescription"))) {
        	JSONArray authorizingPrescriptionJSON = medicationDispenseJSON.getJSONArray("authorizingPrescription");
        	int noOfAuthorizingPrescription = authorizingPrescriptionJSON.length();
        	List<Reference> authorizingPrescriptionList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfAuthorizingPrescription; i++) {
        		Reference theAuthorizingPrescription = new Reference();
        		if(!authorizingPrescriptionJSON.getJSONObject(i).isNull("display")) {
            		theAuthorizingPrescription.setDisplay(authorizingPrescriptionJSON.getJSONObject(i).getString("display"));
        		}
        		if(!authorizingPrescriptionJSON.getJSONObject(i).isNull("type")) {
            		theAuthorizingPrescription.setType(authorizingPrescriptionJSON.getJSONObject(i).getString("type"));
        		}
        		if(!authorizingPrescriptionJSON.getJSONObject(i).isNull("reference")) {
            		theAuthorizingPrescription.setReference(authorizingPrescriptionJSON.getJSONObject(i).getString("reference"));
        		}
        		authorizingPrescriptionList.add(theAuthorizingPrescription);
        	}
        	medicationDispense.setAuthorizingPrescription(authorizingPrescriptionList);
        }
        //Set dosageInstruction
        if(!(medicationDispenseJSON.isNull("dosageInstruction"))) {
        	JSONArray dosageInstructionJSON = medicationDispenseJSON.getJSONArray("dosageInstruction");
        	List<Dosage> dosageList = new ArrayList<Dosage>();
        	int noOfDosageInstructions = dosageInstructionJSON.length();
        	
        	for(int i = 0; i < noOfDosageInstructions; i++) {
        		Dosage theDosage = new Dosage();
        		if(!(dosageInstructionJSON.getJSONObject(i).isNull("sequence"))) {
        			theDosage.setSequence(dosageInstructionJSON.getJSONObject(i).getInt("sequence"));
        		}
        		if(!(dosageInstructionJSON.getJSONObject(i).isNull("text"))) {
        			theDosage.setText(dosageInstructionJSON.getJSONObject(i).getString("text"));
        		}
        		if(!(dosageInstructionJSON.getJSONObject(i).isNull("asNeededBoolean"))) {
        			BooleanType theBoolean = new BooleanType();
        			theBoolean.setValue(dosageInstructionJSON.getJSONObject(i).getBoolean("asNeededBoolean"));
        			theDosage.setAsNeeded(theBoolean);
        		}
        		//Set maxDosePerAdministration
            	if(!(dosageInstructionJSON.getJSONObject(i).isNull("maxDosePerAdministration"))) {
            		JSONObject maxDosePerAdministrationJSON = dosageInstructionJSON.getJSONObject(i).getJSONObject("maxDosePerAdministration");
            		SimpleQuantity theMaxDosePerAdministration = new SimpleQuantity();
            		if(!(maxDosePerAdministrationJSON.isNull("value"))) {
            			theMaxDosePerAdministration.setValue(maxDosePerAdministrationJSON.getLong("value"));
            		}
            		if(!(maxDosePerAdministrationJSON.isNull("unit"))) {
            			theMaxDosePerAdministration.setUnit(maxDosePerAdministrationJSON.getString("unit"));
            		}
            		if(!(maxDosePerAdministrationJSON.isNull("system"))) {
            			theMaxDosePerAdministration.setSystem(maxDosePerAdministrationJSON.getString("system"));
            		}
            		if(!(maxDosePerAdministrationJSON.isNull("code"))) {
            			theMaxDosePerAdministration.setCode(maxDosePerAdministrationJSON.getString("code"));
            		}
            		theDosage.setMaxDosePerAdministration(theMaxDosePerAdministration);
            	}
            	//Set maxDosePerLifetime
            	if(!(dosageInstructionJSON.getJSONObject(i).isNull("maxDosePerLifetime"))) {
            		JSONObject maxDosePerLifetimeJSON = dosageInstructionJSON.getJSONObject(i).getJSONObject("maxDosePerLifetime");
            		SimpleQuantity theMaxDosePerLifeTime = new SimpleQuantity();
            		if(!(maxDosePerLifetimeJSON.isNull("value"))) {
            			theMaxDosePerLifeTime.setValue(maxDosePerLifetimeJSON.getLong("value"));
            		}
            		if(!(maxDosePerLifetimeJSON.isNull("unit"))) {
            			theMaxDosePerLifeTime.setUnit(maxDosePerLifetimeJSON.getString("unit"));
            		}
            		if(!(maxDosePerLifetimeJSON.isNull("system"))) {
            			theMaxDosePerLifeTime.setSystem(maxDosePerLifetimeJSON.getString("system"));
            		}
            		if(!(maxDosePerLifetimeJSON.isNull("code"))) {
            			theMaxDosePerLifeTime.setCode(maxDosePerLifetimeJSON.getString("code"));
            		}
            		theDosage.setMaxDosePerLifetime(theMaxDosePerLifeTime);
            	}
            	//Set maxDosePerPeriod
            	if(!(dosageInstructionJSON.getJSONObject(i).isNull("maxDosePerPeriod"))) {
            		JSONObject maxDosePerPeriodJSON = dosageInstructionJSON.getJSONObject(i).getJSONObject("maxDosePerPeriod");
            		Ratio theMaxDosePerPeriod = new Ratio();
            		if(!maxDosePerPeriodJSON.isNull("numerator")) {
            			JSONObject numeratorJSON = maxDosePerPeriodJSON.getJSONObject("numerator");
            			Quantity theNumerator = new Quantity();
            			if(!(numeratorJSON.isNull("value"))) {
            				theNumerator.setValue(numeratorJSON.getLong("value"));
                		}
                	 	if(!(numeratorJSON.isNull("system"))) {
                	 		theNumerator.setSystem(numeratorJSON.getString("system"));
                		}
                		if(!(numeratorJSON.isNull("code"))) {
                			theNumerator.setCode(numeratorJSON.getString("code"));
                		}
                		theMaxDosePerPeriod.setNumerator(theNumerator);
            		}
            		if(!maxDosePerPeriodJSON.isNull("denominator")) {
            			JSONObject denominatorJSON = maxDosePerPeriodJSON.getJSONObject("denominator");
            			Quantity theDenominator = new Quantity();
            			if(!(denominatorJSON.isNull("value"))) {
            				theDenominator.setValue(denominatorJSON.getLong("value"));
                		}
                	 	if(!(denominatorJSON.isNull("system"))) {
                	 		theDenominator.setSystem(denominatorJSON.getString("system"));
                		}
                		if(!(denominatorJSON.isNull("code"))) {
                			theDenominator.setCode(denominatorJSON.getString("code"));
                		}
                		theMaxDosePerPeriod.setDenominator(theDenominator);
            		}
            		theDosage.setMaxDosePerPeriod(theMaxDosePerPeriod);
            	}
        		if(!(dosageInstructionJSON.getJSONObject(i).isNull("timing"))) {
        			Timing theTiming = new Timing();
        			JSONObject timingJSON = dosageInstructionJSON.getJSONObject(i).getJSONObject("timing");
        			if(!timingJSON.isNull("repeat")) {
        				TimingRepeatComponent theRepeat = new TimingRepeatComponent();
        				JSONObject repeatJSON = timingJSON.getJSONObject("repeat");
        				if(!(repeatJSON.isNull("frequency"))) {
        					theRepeat.setFrequency(repeatJSON.getInt("frequency"));
        				}
        				if(!(repeatJSON.isNull("period"))) {
        					theRepeat.setPeriod(repeatJSON.getInt("period"));
        				}
        				if(!(repeatJSON.isNull("periodUnit"))) {
        					theRepeat.setPeriodUnit(Timing.UnitsOfTime.fromCode(repeatJSON.getString("periodUnit")));
        				}
        				if(!(repeatJSON.isNull("periodMax"))) {
        					theRepeat.setPeriodMax(repeatJSON.getLong("periodMax"));
        				}
        				if(!(repeatJSON.isNull("duration"))) {
        					theRepeat.setDuration(repeatJSON.getLong("duration"));
        				}
        				if(!(repeatJSON.isNull("durationUnit"))) {
        					theRepeat.setDurationUnit(Timing.UnitsOfTime.fromCode(repeatJSON.getString("durationUnit")));
        				}
        				if(!(repeatJSON.isNull("boundsPeriod"))) {
        					JSONObject boundsPeriodJSON = repeatJSON.getJSONObject("boundsPeriod");
        					Period theBoundsPeriod = new Period();
        					if(!(boundsPeriodJSON.isNull("start"))) {
        		        		Date start = CommonUtil.convertStringToDate(boundsPeriodJSON.getString("start"));
        		        		theBoundsPeriod.setStart(start);
        		        	}
        		        	if(!(boundsPeriodJSON.isNull("end"))) {
        		        		Date end = CommonUtil.convertStringToDate(boundsPeriodJSON.getString("end"));
        		        		theBoundsPeriod.setEnd(end);
        		        	}
        		        	theRepeat.setBounds(theBoundsPeriod);
        				}
        				theTiming.setRepeat(theRepeat);
        			}
        			theDosage.setTiming(theTiming);
        		}
        		if(!(dosageInstructionJSON.getJSONObject(i).isNull("route"))) {
        			JSONObject routeJSON = dosageInstructionJSON.getJSONObject(i).getJSONObject("route");
        			CodeableConcept theRoute = new CodeableConcept();
        			if(!(routeJSON.isNull("coding"))) {
        				JSONArray rCodingJSON = routeJSON.getJSONArray("coding");
        				int noOfRCodings = rCodingJSON.length();
        				List<Coding> theRCodingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfRCodings; j++) {
        					Coding theRCoding = new Coding();
        					if(!(rCodingJSON.getJSONObject(j).isNull("system"))) {
        						theRCoding.setSystem(rCodingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(rCodingJSON.getJSONObject(j).isNull("code"))) {
        						theRCoding.setCode(rCodingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(rCodingJSON.getJSONObject(j).isNull("display"))) {
        						theRCoding.setDisplay(rCodingJSON.getJSONObject(j).getString("display"));
        					}
        					theRCodingList.add(theRCoding);
        				}
        				theRoute.setCoding(theRCodingList);
        			}
        			if(!(dosageInstructionJSON.getJSONObject(i).isNull("text"))) {
        				theRoute.setText(dosageInstructionJSON.getJSONObject(i).getString("text"));
        			}
        			theDosage.setRoute(theRoute);
        		}
        		if(!(dosageInstructionJSON.getJSONObject(i).isNull("method"))) {
        			JSONObject methodJSON = dosageInstructionJSON.getJSONObject(i).getJSONObject("method");
        			CodeableConcept theMethod = new CodeableConcept();
        			if(!(methodJSON.isNull("coding"))) {
        				JSONArray mCodingJSON = methodJSON.getJSONArray("coding");
        				int noOfMCodings = mCodingJSON.length();
        				List<Coding> theMCodingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfMCodings; j++) {
        					Coding theMCoding = new Coding();
        					if(!(mCodingJSON.getJSONObject(j).isNull("system"))) {
        						theMCoding.setSystem(mCodingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(mCodingJSON.getJSONObject(j).isNull("code"))) {
        						theMCoding.setCode(mCodingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(mCodingJSON.getJSONObject(j).isNull("display"))) {
        						theMCoding.setDisplay(mCodingJSON.getJSONObject(j).getString("display"));
        					}
        					theMCodingList.add(theMCoding);
        				}
        				theMethod.setCoding(theMCodingList);
        			}
        			if(!(dosageInstructionJSON.getJSONObject(i).isNull("text"))) {
        				theMethod.setText(dosageInstructionJSON.getJSONObject(i).getString("text"));
        			}
        			theDosage.setMethod(theMethod);
        		}
        		if(!(dosageInstructionJSON.getJSONObject(i).isNull("site"))) {
        			JSONObject siteJSON = dosageInstructionJSON.getJSONObject(i).getJSONObject("site");
        			CodeableConcept theSite = new CodeableConcept();
        			if(!(siteJSON.isNull("coding"))) {
        				JSONArray sCodingJSON = siteJSON.getJSONArray("coding");
        				int noOfSCodings = sCodingJSON.length();
        				List<Coding> theSCodingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfSCodings; j++) {
        					Coding theSCoding = new Coding();
        					if(!(sCodingJSON.getJSONObject(j).isNull("system"))) {
        						theSCoding.setSystem(sCodingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(sCodingJSON.getJSONObject(j).isNull("code"))) {
        						theSCoding.setCode(sCodingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(sCodingJSON.getJSONObject(j).isNull("display"))) {
        						theSCoding.setDisplay(sCodingJSON.getJSONObject(j).getString("display"));
        					}
        					theSCodingList.add(theSCoding);
        				}
        				theSite.setCoding(theSCodingList);
        			}
        			if(!(dosageInstructionJSON.getJSONObject(i).isNull("text"))) {
        				theSite.setText(dosageInstructionJSON.getJSONObject(i).getString("text"));
        			}
        			theDosage.setSite(theSite);
        		}
        		if(!(dosageInstructionJSON.getJSONObject(i).isNull("asNeededCodeableConcept"))) {
        			JSONObject asNeededCodeableConceptJSON = dosageInstructionJSON.getJSONObject(i).getJSONObject("asNeededCodeableConcept");
        			CodeableConcept theAsNeededCodeableConcept = new CodeableConcept();
        			if(!(asNeededCodeableConceptJSON.isNull("coding"))) {
        				JSONArray asCodingJSON = asNeededCodeableConceptJSON.getJSONArray("coding");
        				int noOfAsCodings = asCodingJSON.length();
        				List<Coding> codingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfAsCodings; j++) {
        					Coding theCoding = new Coding();
        					if(!(asCodingJSON.getJSONObject(j).isNull("system"))) {
        						theCoding.setSystem(asCodingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(asCodingJSON.getJSONObject(j).isNull("code"))) {
        						theCoding.setCode(asCodingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(asCodingJSON.getJSONObject(j).isNull("display"))) {
        						theCoding.setDisplay(asCodingJSON.getJSONObject(j).getString("display"));
        					}
        					codingList.add(theCoding);
        				}
        				theAsNeededCodeableConcept.setCoding(codingList);
        			}
        			if(!(dosageInstructionJSON.getJSONObject(i).isNull("text"))) {
        				theAsNeededCodeableConcept.setText(dosageInstructionJSON.getJSONObject(i).getString("text"));
        			}
        			theDosage.setAsNeeded(theAsNeededCodeableConcept);
        		}
        		//Set additionalInstruction
                if(!(dosageInstructionJSON.getJSONObject(i).isNull("additionalInstruction"))) {
                	JSONArray additionalInstructionJSON = dosageInstructionJSON.getJSONObject(i).getJSONArray("additionalInstruction");
                	int noOfAdditionalInstructions = additionalInstructionJSON.length();
                	List<CodeableConcept> additionalInstructionList = new ArrayList<CodeableConcept>();
                	for(int a = 0; a < noOfAdditionalInstructions; a++) {
                		CodeableConcept theAdditionalInstruction = new CodeableConcept();
                		if(!(additionalInstructionJSON.getJSONObject(a).isNull("coding"))) {
            				JSONArray cCodingJSON = additionalInstructionJSON.getJSONObject(a).getJSONArray("coding");
            				int noOfCCodings = cCodingJSON.length();
            				List<Coding> cCodingList = new ArrayList<Coding>();
            				for(int j = 0; j < noOfCCodings; j++) {
            					Coding theCCoding = new Coding();

            					if(!(cCodingJSON.getJSONObject(j).isNull("system"))) {
            						theCCoding.setSystem(cCodingJSON.getJSONObject(j).getString("system"));
            					}
            					if(!(cCodingJSON.getJSONObject(j).isNull("code"))) {
            						theCCoding.setCode(cCodingJSON.getJSONObject(j).getString("code"));
            					}
            					if(!(cCodingJSON.getJSONObject(j).isNull("display"))) {
            						theCCoding.setDisplay(cCodingJSON.getJSONObject(j).getString("display"));
            					}
            					cCodingList.add(theCCoding);
            				}
            				theAdditionalInstruction.setCoding(cCodingList);
            			}
                		if(!additionalInstructionJSON.getJSONObject(a).isNull("text")) {
                    		theAdditionalInstruction.setText(additionalInstructionJSON.getJSONObject(a).getString("text"));
                		}
                		additionalInstructionList.add(theAdditionalInstruction);
                	}
            		theDosage.setAdditionalInstruction(additionalInstructionList);
                }
        		//Set doseAndRate
        		if(!(dosageInstructionJSON.getJSONObject(i).isNull("doseAndRate"))) {
        			JSONArray doseAndRateJSON = dosageInstructionJSON.getJSONObject(i).getJSONArray("doseAndRate"); 
        			int noOfDoseAndRates = doseAndRateJSON.length(); 
        			List<DosageDoseAndRateComponent> doseAndRateList = new ArrayList<DosageDoseAndRateComponent>();
        			for(int k = 0; k < noOfDoseAndRates; k++) {
        				DosageDoseAndRateComponent theDoseAndRate = new DosageDoseAndRateComponent();
        				if(!(doseAndRateJSON.getJSONObject(k).isNull("type"))) {
        					JSONObject typeJSON = doseAndRateJSON.getJSONObject(k).getJSONObject("type");
            				CodeableConcept theTType = new CodeableConcept();
        					if(!(typeJSON.isNull("coding"))) {
        						JSONArray tCodingJSON = typeJSON.getJSONArray("coding");
                				int noOfTCodings = tCodingJSON.length();
                				List<Coding> tCodingList = new ArrayList<Coding>(); 
                				for(int l = 0; l < noOfTCodings; l++) {
                					Coding theTCoding = new Coding();
                					if(!(tCodingJSON.getJSONObject(l).isNull("system"))) {
                						theTCoding.setSystem(tCodingJSON.getJSONObject(l).getString("system"));
                					}
                					if(!(tCodingJSON.getJSONObject(l).isNull("code"))) {
                						theTCoding.setCode(tCodingJSON.getJSONObject(l).getString("code"));
                					}
                					if(!(tCodingJSON.getJSONObject(l).isNull("display"))) {
                						theTCoding.setDisplay(tCodingJSON.getJSONObject(l).getString("display"));
                					}
                					if(!(tCodingJSON.getJSONObject(l).isNull("version"))) {
                						theTCoding.setVersion(tCodingJSON.getJSONObject(l).getString("version"));
                					}
                					if(!(tCodingJSON.getJSONObject(l).isNull("userSelected"))) {
                						theTCoding.setUserSelected(tCodingJSON.getJSONObject(l).getBoolean("userSelected"));
                					}
                					tCodingList.add(theTCoding);
                				}
                				theTType.setCoding(tCodingList);
        					} 
        					theDoseAndRate.setType(theTType);
            			}
        				if(!(doseAndRateJSON.getJSONObject(k).isNull("doseQuantity"))) {
        					JSONObject doseQuantityJSON = doseAndRateJSON.getJSONObject(k).getJSONObject("doseQuantity");
                    		SimpleQuantity theDoseQuantity = new SimpleQuantity();
                    		if(!(doseQuantityJSON.isNull("value"))) {
                    			theDoseQuantity.setValue(doseQuantityJSON.getLong("value"));
                    		}
                    		if(!(doseQuantityJSON.isNull("unit"))) {
                    			theDoseQuantity.setUnit(doseQuantityJSON.getString("unit"));
                    		}
                    		if(!(doseQuantityJSON.isNull("system"))) {
                    			theDoseQuantity.setSystem(doseQuantityJSON.getString("system"));
                    		}
                    		if(!(doseQuantityJSON.isNull("code"))) {
                    			theDoseQuantity.setCode(doseQuantityJSON.getString("code"));
                    		}
                    		theDoseAndRate.setDose(theDoseQuantity);
        				}
        				if(!(doseAndRateJSON.getJSONObject(k).isNull("rateRatio"))) {
        					JSONObject rateRatioJSON = doseAndRateJSON.getJSONObject(k).getJSONObject("rateRatio");
                    		Ratio theRateRatio = new Ratio();
                    		if(!rateRatioJSON.isNull("numerator")) {
                    			JSONObject numeratorJSON = rateRatioJSON.getJSONObject("numerator");
                    			Quantity theNumerator = new Quantity();
                    			if(!(numeratorJSON.isNull("value"))) {
                    				theNumerator.setValue(numeratorJSON.getLong("value"));
                        		}
                        	 	if(!(numeratorJSON.isNull("system"))) {
                        	 		theNumerator.setSystem(numeratorJSON.getString("system"));
                        		}
                        		if(!(numeratorJSON.isNull("code"))) {
                        			theNumerator.setCode(numeratorJSON.getString("code"));
                        		}
                        		theRateRatio.setNumerator(theNumerator);
                    		}
                    		if(!rateRatioJSON.isNull("denominator")) {
                    			JSONObject denominatorJSON = rateRatioJSON.getJSONObject("denominator");
                    			Quantity theDenominator = new Quantity();
                    			if(!(denominatorJSON.isNull("value"))) {
                    				theDenominator.setValue(denominatorJSON.getLong("value"));
                        		}
                        	 	if(!(denominatorJSON.isNull("system"))) {
                        	 		theDenominator.setSystem(denominatorJSON.getString("system"));
                        		}
                        		if(!(denominatorJSON.isNull("code"))) {
                        			theDenominator.setCode(denominatorJSON.getString("code"));
                        		}
                        		theRateRatio.setDenominator(theDenominator);
                    		}
                    		theDoseAndRate.setRate(theRateRatio);
        				}
        				if(!(doseAndRateJSON.getJSONObject(k).isNull("doseRange"))) {
        					JSONObject doseRangeJSON = doseAndRateJSON.getJSONObject(k).getJSONObject("doseRange");
                    		Range theDoseRange = new Range();
                    		if(!doseRangeJSON.isNull("low")) {
                    			JSONObject lowJSON = doseRangeJSON.getJSONObject("low");
                    			SimpleQuantity theLow = new SimpleQuantity();
                    			if(!(lowJSON.isNull("value"))) {
                    				theLow.setValue(lowJSON.getLong("value"));
                        		}
                    			if(!(lowJSON.isNull("unit"))) {
                    				theLow.setUnit(lowJSON.getString("unit"));
                        		}
                        	 	if(!(lowJSON.isNull("system"))) {
                        	 		theLow.setSystem(lowJSON.getString("system"));
                        		}
                        		if(!(lowJSON.isNull("code"))) {
                        			theLow.setCode(lowJSON.getString("code"));
                        		}
                        		theDoseRange.setLow(theLow);
                    		}
                    		if(!doseRangeJSON.isNull("high")) {
                    			JSONObject highJSON = doseRangeJSON.getJSONObject("high");
                    			SimpleQuantity theHigh = new SimpleQuantity();
                    			if(!(highJSON.isNull("value"))) {
                    				theHigh.setValue(highJSON.getLong("value"));
                        		}
                    			if(!(highJSON.isNull("unit"))) {
                        	 		theHigh.setUnit(highJSON.getString("unit"));
                        		}
                        	 	if(!(highJSON.isNull("system"))) {
                        	 		theHigh.setSystem(highJSON.getString("system"));
                        		}
                        		if(!(highJSON.isNull("code"))) {
                        			theHigh.setCode(highJSON.getString("code"));
                        		}
                        		theDoseRange.setHigh(theHigh);
                    		}
                    		theDoseAndRate.setDose(theDoseRange);
        				}
        				doseAndRateList.add(theDoseAndRate);
        			}
        			theDosage.setDoseAndRate(doseAndRateList);
        		}
        		dosageList.add(theDosage);
        	}
        	medicationDispense.setDosageInstruction(dosageList);
        }
        //Set note
        if(!(medicationDispenseJSON.isNull("note"))) {
        	JSONArray noteJSON = medicationDispenseJSON.getJSONArray("note");
        	int noOfNotes = noteJSON.length();
        	List<Annotation> noteList = new ArrayList<Annotation>();
        	for(int b = 0; b < noOfNotes; b++) {
        		Annotation theNote = new Annotation();
        		if(!(noteJSON.getJSONObject(b).isNull("text"))) {
        			theNote.setText(noteJSON.getJSONObject(b).getString("text"));
        		}
        		if(!(noteJSON.getJSONObject(b).isNull("time"))) {
        			Date noteTime = CommonUtil.convertStringToDate(noteJSON.getJSONObject(b).getString("time"));
        			theNote.setTime(noteTime);
        		}
        		noteList.add(theNote);
        	}
        	medicationDispense.setNote(noteList);
        }
        return medicationDispense;
    }
}
