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
import org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationDosageComponent;
import org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationPerformerComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedicationAdministration;
import org.sitenv.spring.service.MedicationAdministrationService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MedicationAdministrationResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "MedicationAdministration";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    MedicationAdministrationService service;
    
    public MedicationAdministrationResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (MedicationAdministrationService) context.getBean("medicationAdministrationService");
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return MedicationAdministration.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationAdministration/1/_history/3.0
	 * @param theId : Id of the MedicationAdministration
	 * @return : Object of MedicationAdministration information
	 */
	@Read(version=true)
    public MedicationAdministration readOrVread(@IdParam IdType theId) {
		String id;
		DafMedicationAdministration dafMedicationAdministration;
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
			dafMedicationAdministration = service.getMedicationAdministrationByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			dafMedicationAdministration = service.getMedicationAdministrationById(id);
		}
		return createMedicationAdministrationObject(dafMedicationAdministration);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=MedicationAdministration.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationAdministration/1/_history
	 * @param theId : ID of the MedicationAdministration
	 * @return : List of MedicationAdministration's
	 */
	@History()
    public List<MedicationAdministration> getMedicationAdministrationHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafMedicationAdministration> dafMedicationAdministrationList = service.getMedicationAdministrationHistoryById(id);
        
        List<MedicationAdministration> medicationAdministrationList = new ArrayList<MedicationAdministration>();
        for (DafMedicationAdministration dafMedicationAdministration : dafMedicationAdministrationList) {
        	medicationAdministrationList.add(createMedicationAdministrationObject(dafMedicationAdministration));
        }
        
        return medicationAdministrationList;
	}
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theDevice
	 * @param theReasonNotGiven
	 * @param theContext
	 * @param theEffectiveTime
	 * @param thePatient
	 * @param theReasonGiven
	 * @param theMedication
	 * @param theSubject
	 * @param thePerformer
	 * @param theRequest
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = MedicationAdministration.SP_RES_ID)
        TokenAndListParam theId,

        @Description(shortDefinition = "A MedicationAdministration identifier")
        @OptionalParam(name = MedicationAdministration.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "MedicationAdministration event status (for example one of active/paused/completed/nullified)")
        @OptionalParam(name = MedicationAdministration.SP_STATUS)
        TokenAndListParam theStatus,
        
        @Description(shortDefinition = "Return administrations with this administration device identity")
        @OptionalParam(name = MedicationAdministration.SP_DEVICE)
        ReferenceAndListParam theDevice,
        
        @Description(shortDefinition = "Reasons for not administering the medication")
        @OptionalParam(name = MedicationAdministration.SP_REASON_NOT_GIVEN)
        TokenAndListParam theReasonNotGiven,
        
        @Description(shortDefinition = "Return administrations that share this encounter or episode of care")
        @OptionalParam(name = MedicationAdministration.SP_CONTEXT)
        ReferenceAndListParam theContext,
        
        @Description(shortDefinition = "Date administration happened (or did not happen)")
        @OptionalParam(name = MedicationAdministration.SP_EFFECTIVE_TIME)
        DateRangeParam theEffectiveTime,
        
        @Description(shortDefinition = "The identity of a patient to list administrations  for")
        @OptionalParam(name = MedicationAdministration.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "Reasons for not administering the medication")
        @OptionalParam(name = MedicationAdministration.SP_REASON_GIVEN)
        TokenAndListParam theReasonGiven,
        
        @Description(shortDefinition = "Return administrations of this medication resource")
        @OptionalParam(name = MedicationAdministration.SP_MEDICATION)
        ReferenceAndListParam theMedication,
        
        @Description(shortDefinition = "The identity of the individual or group to list administrations for")
        @OptionalParam(name = MedicationAdministration.SP_SUBJECT)
        ReferenceAndListParam theSubject,
        
        @Description(shortDefinition = "The identity of the individual who administered the medication")
        @OptionalParam(name = MedicationAdministration.SP_PERFORMER)
        ReferenceAndListParam thePerformer,
        
        @Description(shortDefinition = "The identity of a request to list administrations from")
        @OptionalParam(name = MedicationAdministration.SP_REQUEST)
        ReferenceAndListParam theRequest,
        
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(MedicationAdministration.SP_RES_ID, theId);
            paramMap.add(MedicationAdministration.SP_IDENTIFIER, theIdentifier);
            paramMap.add(MedicationAdministration.SP_STATUS, theStatus);
            paramMap.add(MedicationAdministration.SP_DEVICE, theDevice);
            paramMap.add(MedicationAdministration.SP_REASON_NOT_GIVEN, theReasonNotGiven);
            paramMap.add(MedicationAdministration.SP_CONTEXT, theContext);
            paramMap.add(MedicationAdministration.SP_EFFECTIVE_TIME, theEffectiveTime);
            paramMap.add(MedicationAdministration.SP_PATIENT, thePatient);
            paramMap.add(MedicationAdministration.SP_REASON_GIVEN, theReasonGiven);
            paramMap.add(MedicationAdministration.SP_MEDICATION, theMedication);
            paramMap.add(MedicationAdministration.SP_SUBJECT, theSubject);
            paramMap.add(MedicationAdministration.SP_PERFORMER, thePerformer);
            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<DafMedicationAdministration> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> medicationAdministrationList = new ArrayList<IBaseResource>();
                    for(DafMedicationAdministration dafMedicationAdministration : results){
                    	medicationAdministrationList.add(createMedicationAdministrationObject(dafMedicationAdministration));
                    }
                    return medicationAdministrationList;
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
     * @param dafMedicationAdministration : DafDocumentReference MedicationAdministration object
     * @return : DocumentReference MedicationAdministration object
     */
    private MedicationAdministration createMedicationAdministrationObject(DafMedicationAdministration dafMedicationAdministration) {
    	MedicationAdministration medicationAdministration = new MedicationAdministration();
        JSONObject medicationadministrationJSON = new JSONObject(dafMedicationAdministration.getData());

        // Set version
        if(!(medicationadministrationJSON.isNull("meta"))) {
        	if(!(medicationadministrationJSON.getJSONObject("meta").isNull("versionId"))) {
                medicationAdministration.setId(new IdType(RESOURCE_TYPE, medicationadministrationJSON.getString("id") + "", medicationadministrationJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				medicationAdministration.setId(new IdType(RESOURCE_TYPE, medicationadministrationJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
            medicationAdministration.setId(new IdType(RESOURCE_TYPE, medicationadministrationJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(medicationadministrationJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = medicationadministrationJSON.getJSONArray("identifier");
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
        	medicationAdministration.setIdentifier(identifiers);
        }

        //Set medicationCodeableConcept
       	if(!(medicationadministrationJSON.isNull("medicationCodeableConcept"))) {
    		JSONObject medicationCodeableConceptJSON = medicationadministrationJSON.getJSONObject("medicationCodeableConcept");
    		CodeableConcept theMedicationCodeableConcept = new CodeableConcept();
    		if(!medicationCodeableConceptJSON.isNull("coding")) {
    			JSONArray codingJSON = medicationCodeableConceptJSON.getJSONArray("coding");
    			int noOfCodings = codingJSON.length();
    			List<Coding> codingList = new ArrayList<Coding>();
    			for(int s = 0; s < noOfCodings; s++) {
    				Coding theCoding = new Coding();
    				if(!codingJSON.getJSONObject(s).isNull("system")) {
    					theCoding.setSystem(codingJSON.getJSONObject(s).getString("system"));
    				}
    				if(!codingJSON.getJSONObject(s).isNull("code")) {
    					theCoding.setCode(codingJSON.getJSONObject(s).getString("code"));
    				}
    				if(!codingJSON.getJSONObject(s).isNull("display")) {
    					theCoding.setDisplay(codingJSON.getJSONObject(s).getString("display"));
    				}
    				codingList.add(theCoding);
    			}
    			theMedicationCodeableConcept.setCoding(codingList);
    		}
    		if(!medicationCodeableConceptJSON.isNull("text")) {
    			theMedicationCodeableConcept.setText(medicationCodeableConceptJSON.getString("text"));
    		}
    		medicationAdministration.setMedication(theMedicationCodeableConcept);
    	}
        //Set partOf
        if(!(medicationadministrationJSON.isNull("partOf"))) {
        	JSONArray partOfJSON = medicationadministrationJSON.getJSONArray("partOf");
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
        	medicationAdministration.setPartOf(partOfList);
        }
        //Set supportingInformation
        if(!(medicationadministrationJSON.isNull("supportingInformation"))) {
        	JSONArray supportingInformationJSON = medicationadministrationJSON.getJSONArray("supportingInformation");
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
        	medicationAdministration.setSupportingInformation(supportingInformationList);
        }
        //set status
        if(!(medicationadministrationJSON.isNull("status"))) {
            medicationAdministration.setStatus(medicationadministrationJSON.getString("status"));
        }
        //Set statusReason
        if(!(medicationadministrationJSON.isNull("statusReason"))) {
        	JSONArray statusReasonJSON = medicationadministrationJSON.getJSONArray("statusReason");
        	int noOfStatus = statusReasonJSON.length();
        	List<CodeableConcept> statusReasonList = new ArrayList<CodeableConcept>();
        	for(int s = 0; s < noOfStatus; s++) {
        		CodeableConcept theStatusReason = new CodeableConcept();
        		if(!statusReasonJSON.getJSONObject(s).isNull("coding")) {
        			JSONArray sCodingJSON = statusReasonJSON.getJSONObject(s).getJSONArray("coding");
        			int noOfSCodings = sCodingJSON.length();
        			List<Coding> sCodingList = new ArrayList<Coding>();
        			for(int c = 0; c < noOfSCodings; c++) {
        				Coding theSCoding = new Coding();
        				if(!sCodingJSON.getJSONObject(c).isNull("system")) {
        					theSCoding.setSystem(sCodingJSON.getJSONObject(c).getString("system"));
        				}
        				if(!sCodingJSON.getJSONObject(c).isNull("code")) {
        					theSCoding.setCode(sCodingJSON.getJSONObject(c).getString("code"));
        				}
        				if(!sCodingJSON.getJSONObject(c).isNull("display")) {
        					theSCoding.setDisplay(sCodingJSON.getJSONObject(c).getString("display"));
        				}
        				sCodingList.add(theSCoding);
        			}
        			theStatusReason.setCoding(sCodingList);
        		}
        		if(!statusReasonJSON.getJSONObject(s).isNull("text")) {
        			theStatusReason.setText(statusReasonJSON.getJSONObject(s).getString("text"));
        		}
        		statusReasonList.add(theStatusReason);
        	}
        	medicationAdministration.setStatusReason(statusReasonList);
        }
        //Set category
        if(!(medicationadministrationJSON.isNull("category"))) {
        	JSONObject categoryJSON = medicationadministrationJSON.getJSONObject("category");
    		CodeableConcept theCategory = new CodeableConcept();
    		if(!(categoryJSON.isNull("coding"))) {
				JSONArray codingJSON = categoryJSON.getJSONArray("coding");
				int noOfCodings = codingJSON.length();
				List<Coding> codingList = new ArrayList<Coding>();
				for(int j = 0; j < noOfCodings; j++) {
					Coding theCoding = new Coding();

					if(!(codingJSON.getJSONObject(j).isNull("system"))) {
						theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
					}
					if(!(codingJSON.getJSONObject(j).isNull("code"))) {
						theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
					}
					if(!(codingJSON.getJSONObject(j).isNull("display"))) {
						theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
					}
					codingList.add(theCoding);
				}
				theCategory.setCoding(codingList);
			}
    		if(!categoryJSON.isNull("text")) {
        		theCategory.setText(categoryJSON.getString("text"));
    		}
        	medicationAdministration.setCategory(theCategory);
        }
        
        //Set medicationReference
        if(!(medicationadministrationJSON.isNull("medicationReference"))) {
        	JSONObject medicationReferenceJSON = medicationadministrationJSON.getJSONObject("medicationReference");
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
        	medicationAdministration.setMedication(theMedicationReference);
        }
        
		//Set subject
        if(!(medicationadministrationJSON.isNull("subject"))) {
        	JSONObject subjectJSON = medicationadministrationJSON.getJSONObject("subject");
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
        	medicationAdministration.setSubject(theSubject);
        }
        
        //Set context
        if(!(medicationadministrationJSON.isNull("context"))) {
        	JSONObject contextJSON = medicationadministrationJSON.getJSONObject("context");
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
        	medicationAdministration.setContext(theContext);
        }
        
        //Set effectivePeriod
        if(!(medicationadministrationJSON.isNull("effectivePeriod"))) {
        	Period thePeriod = new Period();
        	if(!(medicationadministrationJSON.getJSONObject("effectivePeriod").isNull("start"))) {
        		Date start = CommonUtil.convertStringToDate(medicationadministrationJSON.getJSONObject("effectivePeriod").getString("start"));
        		thePeriod.setStart(start);
        	}
        	if(!(medicationadministrationJSON.getJSONObject("effectivePeriod").isNull("end"))) {
        		Date end = CommonUtil.convertStringToDate(medicationadministrationJSON.getJSONObject("effectivePeriod").getString("end"));
        		thePeriod.setEnd(end);
        	}
        	medicationAdministration.setEffective(thePeriod);
        }
        //Set performer
        if(!(medicationadministrationJSON.isNull("performer"))) {
        	JSONArray performerJSON = medicationadministrationJSON.getJSONArray("performer");
        	int noOfPerformers = performerJSON.length();
        	List<MedicationAdministrationPerformerComponent> performerList = new ArrayList<MedicationAdministrationPerformerComponent>();
        	for(int p = 0; p < noOfPerformers; p++) {
            	MedicationAdministrationPerformerComponent thePerformer = new MedicationAdministrationPerformerComponent();
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
        				JSONArray codingJSON = functionJSON.getJSONArray("coding");
        				int noOfCodings = codingJSON.length();
        				List<Coding> codingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfCodings; j++) {
        					Coding theCoding = new Coding();

        					if(!(codingJSON.getJSONObject(j).isNull("system"))) {
        						theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(codingJSON.getJSONObject(j).isNull("code"))) {
        						theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(codingJSON.getJSONObject(j).isNull("display"))) {
        						theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
        					}
        					codingList.add(theCoding);
        				}
        				theFunction.setCoding(codingList);
        			}
            		if(!performerJSON.getJSONObject(p).isNull("text")) {
            			theFunction.setText(performerJSON.getJSONObject(p).getString("text"));
            		}
            		thePerformer.setFunction(theFunction);
            	}
            	performerList.add(thePerformer);
        	}
        	medicationAdministration.setPerformer(performerList);
        }
        //Set reasonCode
        if(!(medicationadministrationJSON.isNull("reasonCode"))) {
        	JSONArray reasonCodeJSON = medicationadministrationJSON.getJSONArray("reasonCode");
        	int noOfReasons = reasonCodeJSON.length();
        	List<CodeableConcept> reasonCodeList = new ArrayList<CodeableConcept>();
        	for(int r = 0; r < noOfReasons; r++) {
        		CodeableConcept theReasonCode = new CodeableConcept();
        		if(!reasonCodeJSON.getJSONObject(r).isNull("coding")) {
        			JSONArray codingJSON = reasonCodeJSON.getJSONObject(r).getJSONArray("coding");
        			int noOfCodings = codingJSON.length();
        			List<Coding> codingList = new ArrayList<Coding>();
        			for(int c = 0; c < noOfCodings; c++) {
        				Coding theCoding = new Coding();
        				if(!codingJSON.getJSONObject(c).isNull("system")) {
        					theCoding.setSystem(codingJSON.getJSONObject(c).getString("system"));
        				}
        				if(!codingJSON.getJSONObject(c).isNull("code")) {
        					theCoding.setCode(codingJSON.getJSONObject(c).getString("code"));
        				}
        				if(!codingJSON.getJSONObject(c).isNull("display")) {
        					theCoding.setDisplay(codingJSON.getJSONObject(c).getString("display"));
        				}
        				codingList.add(theCoding);
        			}
        			theReasonCode.setCoding(codingList);
        		}
        		if(!reasonCodeJSON.getJSONObject(r).isNull("text")) {
        			theReasonCode.setText(reasonCodeJSON.getJSONObject(r).getString("text"));
        		}
        		reasonCodeList.add(theReasonCode);
        	}
        	medicationAdministration.setReasonCode(reasonCodeList);
        }
        //Set reasonReference
        if(!(medicationadministrationJSON.isNull("reasonReference"))) {
        	JSONArray reasonReferenceJSON = medicationadministrationJSON.getJSONArray("reasonReference");
        	int noOfReasonReference = reasonReferenceJSON.length();
        	List<Reference> reasonReferenceList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfReasonReference; i++) {
        		Reference theReasonReference = new Reference();
        		if(!reasonReferenceJSON.getJSONObject(i).isNull("display")) {
            		theReasonReference.setDisplay(reasonReferenceJSON.getJSONObject(i).getString("display"));
        		}
        		if(!reasonReferenceJSON.getJSONObject(i).isNull("type")) {
            		theReasonReference.setType(reasonReferenceJSON.getJSONObject(i).getString("type"));
        		}
        		if(!reasonReferenceJSON.getJSONObject(i).isNull("reference")) {
            		theReasonReference.setReference(reasonReferenceJSON.getJSONObject(i).getString("reference"));
        		}
        		reasonReferenceList.add(theReasonReference);
        	}
        	medicationAdministration.setReasonReference(reasonReferenceList);
        }
        //Set request
        if(!(medicationadministrationJSON.isNull("request"))) {
        	JSONObject requestJSON = medicationadministrationJSON.getJSONObject("request");
        	Reference theRequest = new Reference();
        	if(!requestJSON.isNull("reference")) {
        		theRequest.setReference(requestJSON.getString("reference"));
        	}
        	if(!requestJSON.isNull("display")) {
        		theRequest.setDisplay(requestJSON.getString("display"));
        	}
        	if(!requestJSON.isNull("type")) {
        		theRequest.setType(requestJSON.getString("type"));
        	}
        	medicationAdministration.setRequest(theRequest);
        }
        //Set dosage
        if(!(medicationadministrationJSON.isNull("dosage"))) {
        	JSONObject dosageJSON = medicationadministrationJSON.getJSONObject("dosage");
        	MedicationAdministrationDosageComponent theDosage = new MedicationAdministrationDosageComponent();
        	if(!(dosageJSON.isNull("text"))) {
        		theDosage.setText(dosageJSON.getString("text"));
        	}
        	if(!(dosageJSON.isNull("site"))) {
        		JSONObject siteJSON = dosageJSON.getJSONObject("site");
        		CodeableConcept theSite = new CodeableConcept();
        		if(!siteJSON.isNull("coding")) {
        			JSONArray codingJSON = siteJSON.getJSONArray("coding");
        			int noOfCodings = codingJSON.length();
        			List<Coding> codingList = new ArrayList<Coding>();
        			for(int s = 0; s < noOfCodings; s++) {
        				Coding theCoding = new Coding();
        				if(!codingJSON.getJSONObject(s).isNull("system")) {
        					theCoding.setSystem(codingJSON.getJSONObject(s).getString("system"));
        				}
        				if(!codingJSON.getJSONObject(s).isNull("code")) {
        					theCoding.setCode(codingJSON.getJSONObject(s).getString("code"));
        				}
        				if(!codingJSON.getJSONObject(s).isNull("display")) {
        					theCoding.setDisplay(codingJSON.getJSONObject(s).getString("display"));
        				}
        				codingList.add(theCoding);
        			}
        			theSite.setCoding(codingList);
        		}
        		if(!siteJSON.isNull("text")) {
        			theSite.setText(siteJSON.getString("text"));
        		}
        		theDosage.setSite(theSite);
        	}
        	if(!(dosageJSON.isNull("route"))) {
        		JSONObject routeJSON = dosageJSON.getJSONObject("route");
        		CodeableConcept theRoute = new CodeableConcept();
        		if(!routeJSON.isNull("coding")) {
        			JSONArray codingJSON = routeJSON.getJSONArray("coding");
        			int noOfcodings = codingJSON.length();
        			List<Coding> codingList = new ArrayList<Coding>();
        			for(int s = 0; s < noOfcodings; s++) {
        				Coding theCoding = new Coding();
        				if(!codingJSON.getJSONObject(s).isNull("system")) {
        					theCoding.setSystem(codingJSON.getJSONObject(s).getString("system"));
        				}
        				if(!codingJSON.getJSONObject(s).isNull("code")) {
        					theCoding.setCode(codingJSON.getJSONObject(s).getString("code"));
        				}
        				if(!codingJSON.getJSONObject(s).isNull("display")) {
        					theCoding.setDisplay(codingJSON.getJSONObject(s).getString("display"));
        				}
        				codingList.add(theCoding);
        			}
        			theRoute.setCoding(codingList);
        		}
        		if(!routeJSON.isNull("text")) {
        			theRoute.setText(routeJSON.getString("text"));
        		}
        		theDosage.setRoute(theRoute);
        	}
        	if(!(dosageJSON.isNull("method"))) {
        		JSONObject methodJSON = dosageJSON.getJSONObject("method");
        		CodeableConcept theMethod = new CodeableConcept();
        		if(!methodJSON.isNull("coding")) {
        			JSONArray codingJSON = methodJSON.getJSONArray("coding");
        			int noOfMCoding = codingJSON.length();
        			List<Coding> mCodingList = new ArrayList<Coding>();
        			for(int s = 0; s < noOfMCoding; s++) {
        				Coding theMCoding = new Coding();
        				if(!codingJSON.getJSONObject(s).isNull("system")) {
        					theMCoding.setSystem(codingJSON.getJSONObject(s).getString("system"));
        				}
        				if(!codingJSON.getJSONObject(s).isNull("code")) {
        					theMCoding.setCode(codingJSON.getJSONObject(s).getString("code"));
        				}
        				if(!codingJSON.getJSONObject(s).isNull("display")) {
        					theMCoding.setDisplay(codingJSON.getJSONObject(s).getString("display"));
        				}
        				mCodingList.add(theMCoding);
        			}
        			theMethod.setCoding(mCodingList);
        		}
        		if(!methodJSON.isNull("text")) {
        			theMethod.setText(methodJSON.getString("text"));
        		}
        		theDosage.setMethod(theMethod);
        	}
        	//Set dose
        	if(!(dosageJSON.isNull("dose"))) {
        		JSONObject doseJSON = dosageJSON.getJSONObject("dose");
        		SimpleQuantity theDose = new SimpleQuantity();
        		if(!(doseJSON.isNull("value"))) {
        			theDose.setValue(doseJSON.getLong("value"));
        		}
        		if(!(doseJSON.isNull("unit"))) {
        			theDose.setUnit(doseJSON.getString("unit"));
        		}
        		if(!(doseJSON.isNull("system"))) {
        			theDose.setSystem(doseJSON.getString("system"));
        		}
        		if(!(doseJSON.isNull("code"))) {
        			theDose.setCode(doseJSON.getString("code"));
        		}
        		theDosage.setDose(theDose);
        	}
        	
        	//Set rateRatio
        	if(!(dosageJSON.isNull("rateRatio"))) {
        		JSONObject rateRatioJSON = dosageJSON.getJSONObject("rateRatio");
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
        		theDosage.setRate(theRateRatio);
        	}
        	//Set rateQuantity
        	if(!(dosageJSON.isNull("rateQuantity"))) {
        		JSONObject rateQuantityJSON = dosageJSON.getJSONObject("rateQuantity");
        		SimpleQuantity theRateQuantity = new SimpleQuantity();
        		if(!(rateQuantityJSON.isNull("value"))) {
        			theRateQuantity.setValue(rateQuantityJSON.getLong("value"));
        		}
        		if(!(rateQuantityJSON.isNull("unit"))) {
        			theRateQuantity.setUnit(rateQuantityJSON.getString("unit"));
        		}
        		if(!(rateQuantityJSON.isNull("system"))) {
        			theRateQuantity.setSystem(rateQuantityJSON.getString("system"));
        		}
        		if(!(rateQuantityJSON.isNull("code"))) {
        			theRateQuantity.setCode(rateQuantityJSON.getString("code"));
        		}
        		theDosage.setRate(theRateQuantity);
        	}
        	medicationAdministration.setDosage(theDosage);
        }
        //Set eventHistory
        if(!(medicationadministrationJSON.isNull("eventHistory"))) {
        	JSONArray eventHistoryJSON = medicationadministrationJSON.getJSONArray("eventHistory");
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
        	medicationAdministration.setEventHistory(eventHistoryList);
        }
        //Set note
        if(!(medicationadministrationJSON.isNull("note"))) {
        	JSONArray noteJSON = medicationadministrationJSON.getJSONArray("note");
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
        	medicationAdministration.setNote(noteList);
        }
        //Set device
        if(!(medicationadministrationJSON.isNull("device"))) {
        	JSONArray deviceJSON = medicationadministrationJSON.getJSONArray("device");
        	int noOfDevices = deviceJSON.length();
        	List<Reference> deviceList = new ArrayList<Reference>();
        	for(int b = 0; b < noOfDevices; b++) {
        		Reference theDevice = new Reference();
        		if(!(deviceJSON.getJSONObject(b).isNull("display"))) {
        			theDevice.setDisplay(deviceJSON.getJSONObject(b).getString("display"));
        		}
        		if(!(deviceJSON.getJSONObject(b).isNull("type"))) {
        			theDevice.setType(deviceJSON.getJSONObject(b).getString("type"));
        		}
        		if(!(deviceJSON.getJSONObject(b).isNull("reference"))) {
        			theDevice.setReference(deviceJSON.getJSONObject(b).getString("reference"));
        		}
        		deviceList.add(theDevice);
        	}
        	medicationAdministration.setDevice(deviceList);
        }
        return medicationAdministration;
    }
}
