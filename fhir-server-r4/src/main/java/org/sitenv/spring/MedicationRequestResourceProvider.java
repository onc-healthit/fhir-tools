package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestComponent;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent;
import org.hl7.fhir.r4.model.Timing.TimingRepeatComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.service.MedicationRequestService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MedicationRequestResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "MedicationRequest";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    MedicationRequestService service;

    public MedicationRequestResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (MedicationRequestService) context.getBean("medicationRequestService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return MedicationRequest.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationRequest/1/_history/4
	 * @param theId : Id of the MedicationRequest
	 * @return : Object of MedicationRequest information
	 */
	@Read(version=true)
    public MedicationRequest readOrVread(@IdParam IdType theId) {
		String id;
		
		DafMedicationRequest dafMedicationRequest;
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
		   dafMedicationRequest = service.getMedicationRequestByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       dafMedicationRequest = service.getMedicationRequestById(id);
		}
		return createMedicationRequestObject(dafMedicationRequest);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=MedicationRequest.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationRequest/1/_history
	 * @param theId : ID of the patient
	 * @return : List of MedicationRequest
	 */
	@History()
    public List<MedicationRequest> getMedicationRequestHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafMedicationRequest> dafMedicationRequestList = service.getMedicationRequestHistoryById(id);
        
        List<MedicationRequest> medicationRequestList = new ArrayList<MedicationRequest>();
        for (DafMedicationRequest dafMedicationRequest : dafMedicationRequestList) {
        	medicationRequestList.add(createMedicationRequestObject(dafMedicationRequest));
        }
        
        return medicationRequestList;
	}

	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theSubject
	 * @param theStatus
	 * @param theIntent
	 * @param theCategory
	 * @param theContext
	 * @param thePriority
	 * @param thePerformer
	 * @param thePerformerType
	 * @param theAuthoredOn
	 * @param theIncludes
	 * @param theRevIncludes
	 * @return
	 */
    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = MedicationRequest.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "A medicationRequest identifier")
        @OptionalParam(name = MedicationRequest.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "The identity of a patient to list orders  for")
        @OptionalParam(name = MedicationRequest.SP_SUBJECT)
        StringAndListParam theSubject,

        @Description(shortDefinition = "Status of the prescription")
        @OptionalParam(name = MedicationRequest.SP_STATUS)
        StringAndListParam theStatus,

        @Description(shortDefinition = "Returns prescriptions with different intents")
        @OptionalParam(name = MedicationRequest.SP_INTENT)
        TokenAndListParam theIntent,

        @Description(shortDefinition = "Returns prescriptions with different categories")
        @OptionalParam(name = MedicationRequest.SP_CATEGORY)
        StringAndListParam theCategory,

        @Description(shortDefinition = "Return prescriptions with this encounter or episode of care identifier")
        @OptionalParam(name = MedicationRequest.SP_ENCOUNTER)
        StringAndListParam theContext,

        @Description(shortDefinition = "Returns prescriptions with different priorities")
        @OptionalParam(name = MedicationRequest.SP_PRIORITY)
        TokenAndListParam thePriority,
        
        @Description(shortDefinition="Returns prescriptions prescribed by this prescriber")
		@OptionalParam(name = MedicationRequest.SP_REQUESTER)
		ReferenceAndListParam theRequester, 
  
		@Description(shortDefinition="Returns requests for a specific type of performer")
		@OptionalParam(name = MedicationRequest.SP_INTENDED_PERFORMERTYPE)
        TokenAndListParam thePerformerType, 
  
		@Description(shortDefinition="Returns requests for a specific type of performer")
		@OptionalParam(name = MedicationRequest.SP_INTENDED_PERFORMER)
        ReferenceAndListParam thePerformer, 

        @Description(shortDefinition = "Return prescriptions written on this date")
        @OptionalParam(name = MedicationRequest.SP_AUTHOREDON)
        DateAndListParam theAuthoredOn,
        
        @Description(shortDefinition="Returns prescriptions for a specific patient")
        @OptionalParam(name = MedicationRequest.SP_PATIENT)
        ReferenceAndListParam thePatient,
        

        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,

		@Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(MedicationRequest.SP_RES_ID, theId);
            paramMap.add(MedicationRequest.SP_IDENTIFIER, theIdentifier);
            paramMap.add(MedicationRequest.SP_SUBJECT, theSubject);
            paramMap.add(MedicationRequest.SP_STATUS, theStatus);
            paramMap.add(MedicationRequest.SP_INTENT, theIntent);
            paramMap.add(MedicationRequest.SP_CATEGORY, theCategory);
            paramMap.add(MedicationRequest.SP_ENCOUNTER, theContext);
            paramMap.add(MedicationRequest.SP_PRIORITY, thePriority);
            paramMap.add(MedicationRequest.SP_REQUESTER, theRequester);
            paramMap.add(MedicationRequest.SP_INTENDED_PERFORMERTYPE, thePerformerType);
            paramMap.add(MedicationRequest.SP_INTENDED_PERFORMER, thePerformer);
            paramMap.add(MedicationRequest.SP_AUTHOREDON, theAuthoredOn);
            paramMap.add(MedicationRequest.SP_PATIENT, thePatient);
            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<DafMedicationRequest> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> medicationRequestList = new ArrayList<IBaseResource>();
					List<String> medicationRequesIDs = new ArrayList<String>();
					List<String> medicationIds = new ArrayList<String>();
                    for(DafMedicationRequest dafMedicationRequest : results){
						MedicationRequest medicationRequest = createMedicationRequestObject(dafMedicationRequest);
						medicationRequestList.add(medicationRequest);
						medicationRequesIDs.add(((IdType)medicationRequest.getIdElement()).getResourceType()+"/"+((IdType)medicationRequest.getIdElement()).getIdPart());

						if((medicationRequest.getMedication() instanceof Reference)) {
							medicationIds.add(StringUtils.remove(((Reference) medicationRequest.getMedication()).getReference(),"Medication/"));
						}
                    }
					if(theIncludes.size() >0 && medicationIds.size() > 0){
						medicationRequestList.addAll(new MedicationResourceProvider().getMedicationByResourceId(medicationIds));
					}

                    if(theRevIncludes.size() >0 ){
                        medicationRequestList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(medicationRequesIDs));
                    }

                    return medicationRequestList;
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
    
    
	private MedicationRequest createMedicationRequestObject(DafMedicationRequest dafMedicationRequest) {
		MedicationRequest medicationRequest = new MedicationRequest();
	    JSONObject medicationRequestJSON = new JSONObject(dafMedicationRequest.getData());
	    
	    //Set version
	    if(!(medicationRequestJSON.isNull("meta"))) {
	    	if(!(medicationRequestJSON.getJSONObject("meta").isNull("versionId"))) {
	    		medicationRequest.setId(new IdType(RESOURCE_TYPE, medicationRequestJSON.getString("id") + "", medicationRequestJSON.getJSONObject("meta").getString("versionId")));
	    	}else {
				medicationRequest.setId(new IdType(RESOURCE_TYPE, medicationRequestJSON.getString("id") + "", VERSION_ID));
			}
	    }
	    else {
	    	medicationRequest.setId(new IdType(RESOURCE_TYPE, medicationRequestJSON.getString("id") + "", VERSION_ID));
	    }
	        
	    //Set identifier
        if(!(medicationRequestJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = medicationRequestJSON.getJSONArray("identifier");
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
        	medicationRequest.setIdentifier(identifiers);
        }
	    
	    //set status
	    if(!(medicationRequestJSON.isNull("status"))) {
	    	medicationRequest.setStatus(MedicationRequest.MedicationRequestStatus.fromCode(medicationRequestJSON.getString("status")));
	    }
	    
	    //set statusReason
		if(!(medicationRequestJSON.isNull("statusReason"))) {
			JSONObject statusReasonJSON = medicationRequestJSON.getJSONObject("statusReason");
			CodeableConcept theStatusReason=new CodeableConcept();
			
			if(!(statusReasonJSON.isNull("coding"))) {
				JSONArray reasonCodingJSON = statusReasonJSON.getJSONArray("coding");
				int noOfreasonCode = reasonCodingJSON.length();
				List<Coding> reasonCodingList = new ArrayList<Coding>();
				for(int p = 0; p < noOfreasonCode; p++) {
					Coding theReasonCoding = new Coding();
					if(!(reasonCodingJSON.getJSONObject(p).isNull("system"))) {
						theReasonCoding.setSystem(reasonCodingJSON.getJSONObject(p).getString("system"));
					}
					if(!(reasonCodingJSON.getJSONObject(p).isNull("code"))) {
						theReasonCoding.setCode(reasonCodingJSON.getJSONObject(p).getString("code"));
					}
					if(!(reasonCodingJSON.getJSONObject(p).isNull("display"))) {
						theReasonCoding.setDisplay(reasonCodingJSON.getJSONObject(p).getString("display"));
					}
					reasonCodingList.add(theReasonCoding);
				}
				theStatusReason.setCoding(reasonCodingList);
			}
			if(!(statusReasonJSON.isNull("text"))) {
				theStatusReason.setText(statusReasonJSON.getString("text"));
			}
			medicationRequest.setStatusReason(theStatusReason);
		}
	  		
		if(!(medicationRequestJSON.isNull("intent"))) {
			medicationRequest.setIntent(MedicationRequest.MedicationRequestIntent.fromCode(medicationRequestJSON.getString("intent")));
		}
	  		
		//set category
		if(!(medicationRequestJSON.isNull("category"))) {
			JSONArray categoryJSON=medicationRequestJSON.getJSONArray("category");
			int noOfCategaries=categoryJSON.length();
			List<CodeableConcept> categoryList=new ArrayList<CodeableConcept>();
				
			for( int c = 0; c < noOfCategaries ; c++) {
				CodeableConcept theCategory = new CodeableConcept();
		
				if (!(categoryJSON.getJSONObject(c).isNull("coding"))) {
					JSONArray categoryCodingJSON = categoryJSON.getJSONObject(c).getJSONArray("coding");
					int noOfCategoryCoding = categoryCodingJSON.length();
					List<Coding> codingList = new ArrayList<Coding>();
	  					
					for(int j = 0; j < noOfCategoryCoding; j++) {
							Coding theCoding = new Coding();
	  						
	  						if(!(categoryCodingJSON.getJSONObject(j).isNull("system"))) {
	  							theCoding.setSystem(categoryCodingJSON.getJSONObject(j).getString("system"));
	  						}
	  						if(!(categoryCodingJSON.getJSONObject(j).isNull("display"))) {
	  							theCoding.setDisplay(categoryCodingJSON.getJSONObject(j).getString("display"));
	  						}
	  						if(!(categoryCodingJSON.getJSONObject(j).isNull("code"))) {
	  							theCoding.setCode(categoryCodingJSON.getJSONObject(j).getString("code"));
	  						}
	  						codingList.add(theCoding);
	  				}
					theCategory.setCoding(codingList);
				}
				categoryList.add(theCategory);
			}
			medicationRequest.setCategory(categoryList);
		}
	    
		//set subject
	    if(!(medicationRequestJSON.isNull("subject"))) {
	    	Reference  theSubject = new Reference();
	    	
	    	if(!(medicationRequestJSON.getJSONObject("subject").isNull("reference"))) {
	    		theSubject.setReference(medicationRequestJSON.getJSONObject("subject").getString("reference"));    		
	    	}
	    	
	    	if(!(medicationRequestJSON.getJSONObject("subject").isNull("display"))) {
	    		theSubject.setDisplay(medicationRequestJSON.getJSONObject("subject").getString("display"));    		
	    	}
	    	
	    	if(!(medicationRequestJSON.getJSONObject("subject").isNull("type"))) {
	    		theSubject.setType(medicationRequestJSON.getJSONObject("subject").getString("type"));    		
	    	}
	    	medicationRequest.setSubject(theSubject);    
	    }
	  
	    
       	//Set supportingInformation
        if(!(medicationRequestJSON.isNull("supportingInformation"))) {
        	JSONArray supportingInformationJSON = medicationRequestJSON.getJSONArray("supportingInformation");
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
        	medicationRequest.setSupportingInformation(supportingInformationList);
        }
 
	    //set encounter
	    if(!(medicationRequestJSON.isNull("encounter"))) {
	    	Reference  theEncounter = new Reference();
	    	
	    	if(!(medicationRequestJSON.getJSONObject("encounter").isNull("reference"))) {
	    		theEncounter.setReference(medicationRequestJSON.getJSONObject("encounter").getString("reference"));    		
	    	}
	    	if(!(medicationRequestJSON.getJSONObject("encounter").isNull("display"))) {
	    		theEncounter.setDisplay(medicationRequestJSON.getJSONObject("encounter").getString("display"));    		
	    	}
	    	if(!(medicationRequestJSON.getJSONObject("encounter").isNull("type"))) {
	    		theEncounter.setType(medicationRequestJSON.getJSONObject("encounter").getString("type"));    		
	    	}
	    	medicationRequest.setEncounter(theEncounter); 
	    }
	    
	    //set reasonCode
  		if(!(medicationRequestJSON.isNull("reasonCode"))) {
  			JSONArray reasonCodeJSON = medicationRequestJSON.getJSONArray("reasonCode");
  			int noOfreasonCodes=reasonCodeJSON.length();
  			List<CodeableConcept> reasonCodeList = new ArrayList<CodeableConcept>();
  			
  			for(int i = 0; i < noOfreasonCodes ; i++) {
  	  			CodeableConcept theReason=new CodeableConcept();
  				if(!(reasonCodeJSON.getJSONObject(i).isNull("coding"))) {
  					JSONArray reasonCodingJSON = reasonCodeJSON.getJSONObject(i).getJSONArray("coding");
  					int noOfreasonCode = reasonCodingJSON.length();
  					List<Coding> reasonCodingList = new ArrayList<Coding>();
  					for(int p = 0; p < noOfreasonCode; p++) {
  	  					Coding theReasonCoding = new Coding();
  						if(!(reasonCodingJSON.getJSONObject(p).isNull("system"))) {
  							theReasonCoding.setSystem(reasonCodingJSON.getJSONObject(p).getString("system"));
  						}
  						if(!(reasonCodingJSON.getJSONObject(p).isNull("code"))) {
  							theReasonCoding.setCode(reasonCodingJSON.getJSONObject(p).getString("code"));
  						}
  						if(!(reasonCodingJSON.getJSONObject(p).isNull("display"))) {
  							theReasonCoding.setDisplay(reasonCodingJSON.getJSONObject(p).getString("display"));
  						}
  						reasonCodingList.add(theReasonCoding);
  					}
  					theReason.setCoding(reasonCodingList);
  				}
  				reasonCodeList.add(theReason);
  			}
  			medicationRequest.setReasonCode(reasonCodeList);
  		}
	  		
	  	//set basedOn
	    if(!(medicationRequestJSON.isNull("basedOn"))) {
	    	JSONArray basedOnJSON=medicationRequestJSON.getJSONArray("basedOn");
	    	int noOfbased=basedOnJSON.length();
	    	List<Reference> basedOnList=new ArrayList<Reference>();
	    	
	    	for(int i = 0; i < noOfbased ; i++) {
		    	Reference  theBasedOn = new Reference();
		    	if(!(basedOnJSON.getJSONObject(i).isNull("reference"))) {
		    		theBasedOn.setReference(basedOnJSON.getJSONObject(i).getString("reference"));    		
		    	}
		    	if(!(basedOnJSON.getJSONObject(i).isNull("type"))) {
		    		theBasedOn.setType(basedOnJSON.getJSONObject(i).getString("type"));    		
		    	}
		    	if(!(basedOnJSON.getJSONObject(i).isNull("display"))) {
		    		theBasedOn.setDisplay(basedOnJSON.getJSONObject(i).getString("display"));    		
		    	}
		    	basedOnList.add(theBasedOn);
	    	}
	    	medicationRequest.setBasedOn(basedOnList); 
	    }
	  		
	  	//set note
	    if(!(medicationRequestJSON.isNull("note"))) {
	    	JSONArray noteJSON=medicationRequestJSON.getJSONArray("note");
	    	int noOfNotes=noteJSON.length();
	    	List<Annotation> noteList=new ArrayList<Annotation>();
	    	
	    	for(int i = 0 ; i < noOfNotes ; i++) {
	    		Annotation theNote=new Annotation();
	    		if(!(noteJSON.getJSONObject(i).isNull("text"))) {
	    			theNote.setText(noteJSON.getJSONObject(i).getString("text"));
	    		}
	    		noteList.add(theNote);
	    	}
	    	medicationRequest.setNote(noteList);
	    }
		    
	    //Set instantiatesUri
        if(!(medicationRequestJSON.isNull("instantiatesUri"))) {
        	List<UriType> uriList = new ArrayList<UriType>();
        	JSONArray theUris = medicationRequestJSON.getJSONArray("instantiatesUri");
        	int noOfUris = theUris.length();
    		for(int i = 0; i < noOfUris; i++) {
    			String uriName = theUris.getString(i) ;
    			UriType uriType = new UriType();
    			uriType.setValue(uriName);
			   	uriList.add(uriType);
    		}
    		medicationRequest.setInstantiatesUri(uriList);
        }
	        
	    //Set dosageInstruction
        if(!(medicationRequestJSON.isNull("dosageInstruction"))) {
        	JSONArray dosageInstructionJSON = medicationRequestJSON.getJSONArray("dosageInstruction");
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
        				List<Coding> theAsCodingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfAsCodings; j++) {
        					Coding theAsCoding = new Coding();
        					if(!(asCodingJSON.getJSONObject(j).isNull("system"))) {
        						theAsCoding.setSystem(asCodingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(asCodingJSON.getJSONObject(j).isNull("code"))) {
        						theAsCoding.setCode(asCodingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(asCodingJSON.getJSONObject(j).isNull("display"))) {
        						theAsCoding.setDisplay(asCodingJSON.getJSONObject(j).getString("display"));
        					}
        					theAsCodingList.add(theAsCoding);
        				}
        				theAsNeededCodeableConcept.setCoding(theAsCodingList);
        			}
        			if(!(dosageInstructionJSON.getJSONObject(i).isNull("text"))) {
        				theAsNeededCodeableConcept.setText(dosageInstructionJSON.getJSONObject(i).getString("text"));
        			}
        			theDosage.setAsNeeded(theAsNeededCodeableConcept);
        		}
        		//Set additionalInstruction
                if(!(dosageInstructionJSON.getJSONObject(i).isNull("additionalInstruction"))) {
                	JSONArray additionalInstructionJSON = dosageInstructionJSON.getJSONObject(i).getJSONArray("additionalInstruction");
                	int noOfAdditionalInfo = additionalInstructionJSON.length();
                	List<CodeableConcept> additionalInstructionList = new ArrayList<CodeableConcept>();
                	for(int a = 0; a < noOfAdditionalInfo; a++) {
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
        			List<DosageDoseAndRateComponent> dosageDoseAndRateList = new ArrayList<DosageDoseAndRateComponent>();
        			for(int k = 0; k < noOfDoseAndRates; k++) {
        				DosageDoseAndRateComponent theDosageDoseAndRate = new DosageDoseAndRateComponent();
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
        					theDosageDoseAndRate.setType(theTType);
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
                    		theDosageDoseAndRate.setDose(theDoseQuantity);
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
                    		theDosageDoseAndRate.setRate(theRateRatio);
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
                    		theDosageDoseAndRate.setDose(theDoseRange);
        				}
        				dosageDoseAndRateList.add(theDosageDoseAndRate);
        			}
        			theDosage.setDoseAndRate(dosageDoseAndRateList);
        		}
        		dosageList.add(theDosage);
        	}
        	medicationRequest.setDosageInstruction(dosageList);
        }
	        
        //Set authoredOn
		if(!(medicationRequestJSON.isNull("authoredOn"))) {
			String dateInStr = (String) medicationRequestJSON.get("authoredOn");
			Date dateOfauthoredOn = CommonUtil.convertStringToDate(dateInStr);
			medicationRequest.setAuthoredOn(dateOfauthoredOn);
		}
	        
		//set requester
		if(!(medicationRequestJSON.isNull("requester"))) {
	    	Reference  theRequester = new Reference();
	    	
	    	if(!(medicationRequestJSON.getJSONObject("requester").isNull("reference"))) {
	    		theRequester.setReference(medicationRequestJSON.getJSONObject("requester").getString("reference"));    		
	    	}
	    	if(!(medicationRequestJSON.getJSONObject("requester").isNull("display"))) {
	    		theRequester.setDisplay(medicationRequestJSON.getJSONObject("requester").getString("display"));    		
	    	}
	    	medicationRequest.setRequester(theRequester); 
	    }
			
		//set performer
		if(!(medicationRequestJSON.isNull("performer"))) {
	    	Reference  thePerformer = new Reference();
	    	
	    	if(!(medicationRequestJSON.getJSONObject("performer").isNull("reference"))) {
	    		thePerformer.setReference(medicationRequestJSON.getJSONObject("performer").getString("reference"));    		
	    	}
	    	if(!(medicationRequestJSON.getJSONObject("performer").isNull("display"))) {
	    		thePerformer.setDisplay(medicationRequestJSON.getJSONObject("performer").getString("display"));    		
	    	}
	    	medicationRequest.setPerformer(thePerformer); 
	    }
			
		//set performerType
		if(!(medicationRequestJSON.isNull("performerType"))) {
			JSONObject performerTypeJSONObj = medicationRequestJSON.getJSONObject("performerType");
			CodeableConcept thePerformerType=new CodeableConcept();
			
			if(!(performerTypeJSONObj.isNull("coding"))) {
				JSONArray performerCodingJSON = performerTypeJSONObj.getJSONArray("coding");
				int noOfreasonCode = performerCodingJSON.length();
				List<Coding> performerTypeList = new ArrayList<Coding>();
				for(int p = 0; p < noOfreasonCode; p++) {
					Coding thePerformerTypeCoding = new Coding();
					if(!(performerCodingJSON.getJSONObject(p).isNull("system"))) {
						thePerformerTypeCoding.setSystem(performerCodingJSON.getJSONObject(p).getString("system"));
					}
					if(!(performerCodingJSON.getJSONObject(p).isNull("code"))) {
						thePerformerTypeCoding.setCode(performerCodingJSON.getJSONObject(p).getString("code"));
					}
					if(!(performerCodingJSON.getJSONObject(p).isNull("display"))) {
						thePerformerTypeCoding.setDisplay(performerCodingJSON.getJSONObject(p).getString("display"));
					}
					performerTypeList.add(thePerformerTypeCoding);
				}
				thePerformerType.setCoding(performerTypeList);
			}
			medicationRequest.setPerformerType(thePerformerType);
		}
			
		//set recorder
		if(!(medicationRequestJSON.isNull("recorder"))) {
	    	Reference  theRecorder = new Reference();
	    	
	    	if(!(medicationRequestJSON.getJSONObject("recorder").isNull("reference"))) {
	    		theRecorder.setReference(medicationRequestJSON.getJSONObject("recorder").getString("reference"));    		
	    	}
	    	if(!(medicationRequestJSON.getJSONObject("recorder").isNull("display"))) {
	    		theRecorder.setDisplay(medicationRequestJSON.getJSONObject("recorder").getString("display"));    		
	    	}
	    	if(!(medicationRequestJSON.getJSONObject("recorder").isNull("type"))) {
	    		theRecorder.setType(medicationRequestJSON.getJSONObject("recorder").getString("type"));    		
	    	}
	    	medicationRequest.setRecorder(theRecorder); 
	    }
			
		//set insurance
	    if(!(medicationRequestJSON.isNull("insurance"))) {
	    	JSONArray insuranceJSON=medicationRequestJSON.getJSONArray("insurance");
	    	int noOfinsurance=insuranceJSON.length();
	    	List<Reference> insuranceList=new ArrayList<Reference>();
	    	
	    	for(int i = 0 ; i < noOfinsurance ; i++) {
	    		Reference theInsurance=new Reference();
	    		if(!(insuranceJSON.getJSONObject(i).isNull("reference"))) {
	    			theInsurance.setReference(insuranceJSON.getJSONObject(i).getString("reference"));
	    		}
	    		insuranceList.add(theInsurance);
	    	}
	    	medicationRequest.setInsurance(insuranceList);
	    }
		    
	    //set substitution
	    if(!(medicationRequestJSON.isNull("substitution"))) {
	    	MedicationRequestSubstitutionComponent theSubstitution=new MedicationRequestSubstitutionComponent();
	    	JSONObject substitutionJSON = medicationRequestJSON.getJSONObject("substitution");
	    	
	    	if(!(substitutionJSON.isNull("allowedBoolean"))) {
	    		BooleanType theAllowed = new BooleanType();
	    		theAllowed.setValue(substitutionJSON.getBoolean("allowedBoolean"));
	    		theSubstitution.setAllowed(theAllowed);		
	    	}
	    	if(!(substitutionJSON.isNull("reason"))) {
	    		JSONObject reasonJSON=substitutionJSON.getJSONObject("reason");
	    		CodeableConcept theReason=new CodeableConcept();
				
				if(!(reasonJSON.isNull("coding"))) {
					JSONArray reasonCodingJSON = reasonJSON.getJSONArray("coding");
					int noOfreasonCode = reasonCodingJSON.length();
					List<Coding> reasonList = new ArrayList<Coding>();
					
					for(int p = 0; p < noOfreasonCode; p++) {
						Coding theReasonCoding = new Coding();
						if(!(reasonCodingJSON.getJSONObject(p).isNull("system"))) {
							theReasonCoding.setSystem(reasonCodingJSON.getJSONObject(p).getString("system"));
						}
						if(!(reasonCodingJSON.getJSONObject(p).isNull("code"))) {
							theReasonCoding.setCode(reasonCodingJSON.getJSONObject(p).getString("code"));
						}
						if(!(reasonCodingJSON.getJSONObject(p).isNull("display"))) {
							theReasonCoding.setDisplay(reasonCodingJSON.getJSONObject(p).getString("display"));
						}
						reasonList.add(theReasonCoding);
					}
					theReason.setCoding(reasonList);
				}
				if(!(reasonJSON.isNull("text"))) {
					theReason.setText(reasonJSON.getString("text"));
				}
				theSubstitution.setReason(theReason);
	    	}
	    	medicationRequest.setSubstitution(theSubstitution); 
	    }
	    
	    //set dispenseRequest
	    if(!(medicationRequestJSON.isNull("dispenseRequest"))) {
			JSONObject dispenseRequestJSONObj = medicationRequestJSON.getJSONObject("dispenseRequest");
			MedicationRequestDispenseRequestComponent theDispenseRequest= new MedicationRequestDispenseRequestComponent();
			
			if(!(dispenseRequestJSONObj.isNull("validityPeriod"))) {
				Period thevalidityPeriod=new Period();
				JSONObject validityPeriodJSON=dispenseRequestJSONObj.getJSONObject("validityPeriod");
				
				
				if(!(validityPeriodJSON.isNull("start"))) {
					Date startDate = CommonUtil.convertStringToDate(validityPeriodJSON.getString("start"));
					thevalidityPeriod.setStart(startDate);
				}
				if(!(validityPeriodJSON.isNull("end"))) {
					Date endDate = CommonUtil.convertStringToDate(validityPeriodJSON.getString("end"));
					thevalidityPeriod.setEnd(endDate);
				}
				theDispenseRequest.setValidityPeriod(thevalidityPeriod);
			}
			
			if(!(dispenseRequestJSONObj.isNull("numberOfRepeatsAllowed"))){
				theDispenseRequest.setNumberOfRepeatsAllowed(dispenseRequestJSONObj.getInt("numberOfRepeatsAllowed"));
			}
			
        	if(!(dispenseRequestJSONObj.isNull("quantity"))) {
        		JSONObject quantityJSON = dispenseRequestJSONObj.getJSONObject("quantity");
        		SimpleQuantity thequantity = new SimpleQuantity();
        		if(!(quantityJSON.isNull("value"))) {
        			thequantity.setValue(quantityJSON.getLong("value"));
        		}
        		if(!(quantityJSON.isNull("unit"))) {
        			thequantity.setUnit(quantityJSON.getString("unit"));
        		}
        		if(!(quantityJSON.isNull("system"))) {
        			thequantity.setSystem(quantityJSON.getString("system"));
        		}
        		if(!(quantityJSON.isNull("code"))) {
        			thequantity.setCode(quantityJSON.getString("code"));
        		}
        		theDispenseRequest.setQuantity(thequantity);
        	}
        	
        	if(!(dispenseRequestJSONObj.isNull("expectedSupplyDuration"))) {
        		JSONObject expectedSupplyDuration = dispenseRequestJSONObj.getJSONObject("expectedSupplyDuration");
        		Duration theExpectedSupplyDuration = new Duration();
        		if(!(expectedSupplyDuration.isNull("value"))) {
        			theExpectedSupplyDuration.setValue(expectedSupplyDuration.getLong("value"));
        		}
        		if(!(expectedSupplyDuration.isNull("unit"))) {
        			theExpectedSupplyDuration.setUnit(expectedSupplyDuration.getString("unit"));
        		}
        		if(!(expectedSupplyDuration.isNull("system"))) {
        			theExpectedSupplyDuration.setSystem(expectedSupplyDuration.getString("system"));
        		}
        		if(!(expectedSupplyDuration.isNull("code"))) {
        			theExpectedSupplyDuration.setCode(expectedSupplyDuration.getString("code"));
        		}
        		theDispenseRequest.setExpectedSupplyDuration(theExpectedSupplyDuration);
        	}
        	
        	if(!(dispenseRequestJSONObj.isNull("performer"))) {
		    	Reference  thePerformer = new Reference();
		    	JSONObject performerJSON = dispenseRequestJSONObj.getJSONObject("performer");
		    	if(!(performerJSON.isNull("reference"))) {
		    		thePerformer.setReference(performerJSON.getString("reference"));   
		    	}
		    	if(!(performerJSON.isNull("display"))) {
		    		thePerformer.setDisplay(performerJSON.getString("display"));    		
		    	}
		    	theDispenseRequest.setPerformer(thePerformer); 
		    }
			medicationRequest.setDispenseRequest(theDispenseRequest);
	    }
		    
		//set detectedIssue
	    if(!(medicationRequestJSON.isNull("detectedIssue"))) {
	    	JSONArray detectedIssueJSON=medicationRequestJSON.getJSONArray("detectedIssue");
	    	int noOfinsurance=detectedIssueJSON.length();
	    	List<Reference> detectedIssueList=new ArrayList<Reference>();
	    	
	    	for(int i = 0 ; i < noOfinsurance ; i++) {
		    	Reference thedetectedIssue=new Reference();
	    		if(!(detectedIssueJSON.getJSONObject(i).isNull("reference"))) {
	    			thedetectedIssue.setReference(detectedIssueJSON.getJSONObject(i).getString("reference"));
	    		}
	    		if(!(detectedIssueJSON.getJSONObject(i).isNull("display"))) {
	    			thedetectedIssue.setDisplay(detectedIssueJSON.getJSONObject(i).getString("display"));
	    		}
	    		detectedIssueList.add(thedetectedIssue);
	    	}
	    	medicationRequest.setDetectedIssue(detectedIssueList);
	    }
		    
	    //set eventHistory
	    if(!(medicationRequestJSON.isNull("eventHistory"))) {
	    	JSONArray eventHistoryJSON=medicationRequestJSON.getJSONArray("eventHistory");
	    	int noOfinsurance=eventHistoryJSON.length();
	    	List<Reference> eventHistoryList=new ArrayList<Reference>();
	    	
	    	for(int i = 0 ; i < noOfinsurance ; i++) {
		    	Reference theEventHistory=new Reference();
	    		if(!(eventHistoryJSON.getJSONObject(i).isNull("reference"))) {
	    			theEventHistory.setReference(eventHistoryJSON.getJSONObject(i).getString("reference"));
	    		}
	    		if(!(eventHistoryJSON.getJSONObject(i).isNull("display"))) {
	    			theEventHistory.setDisplay(eventHistoryJSON.getJSONObject(i).getString("display"));
	    		}
	    		eventHistoryList.add(theEventHistory);
	    	}
	    	medicationRequest.setEventHistory(eventHistoryList);
	    }
		    
	    //set priorPrescription
		if(!(medicationRequestJSON.isNull("priorPrescription"))) {
	    	Reference  thePriorPrescription = new Reference();
	    	
	    	if(!(medicationRequestJSON.getJSONObject("priorPrescription").isNull("reference"))) {
	    		thePriorPrescription.setReference(medicationRequestJSON.getJSONObject("priorPrescription").getString("reference"));    		
	    	}
	    	if(!(medicationRequestJSON.getJSONObject("priorPrescription").isNull("display"))) {
	    		thePriorPrescription.setDisplay(medicationRequestJSON.getJSONObject("priorPrescription").getString("display"));    		
	    	}
	    	medicationRequest.setPriorPrescription(thePriorPrescription); 
	    }
		
		//set medicationReference
		if(!(medicationRequestJSON.isNull("medicationReference"))) {
	    	Reference  theMedicationReference = new Reference();
	    	
	    	if(!(medicationRequestJSON.getJSONObject("medicationReference").isNull("reference"))) {
	    		theMedicationReference.setReference(medicationRequestJSON.getJSONObject("medicationReference").getString("reference"));    		
	    	}
	    	if(!(medicationRequestJSON.getJSONObject("medicationReference").isNull("display"))) {
	    		theMedicationReference.setDisplay(medicationRequestJSON.getJSONObject("medicationReference").getString("display"));    		
	    	}
	    	if(!(medicationRequestJSON.getJSONObject("medicationReference").isNull("type"))) {
	    		theMedicationReference.setType(medicationRequestJSON.getJSONObject("medicationReference").getString("type"));    		
	    	}
	    	medicationRequest.setMedication(theMedicationReference);
	    }
			
		// Set priority
		if (!(medicationRequestJSON.isNull("priority"))) {
			medicationRequest.setPriority(MedicationRequest.MedicationRequestPriority.fromCode(medicationRequestJSON.getString("priority")));
		}

		//Set Reported
		if(!(medicationRequestJSON.isNull("reported"))) {
			Reference reported = new Reference();
			reported.setReference(medicationRequestJSON.getJSONObject("reported").getString("reference"));
			medicationRequest.setReported(reported);
		}

			//Set medicationCodeableConcept
       	if(!(medicationRequestJSON.isNull("medicationCodeableConcept"))) {
    		JSONObject medicationCodeableConceptJSON = medicationRequestJSON.getJSONObject("medicationCodeableConcept");
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
    		medicationRequest.setMedication(theMedicationCodeableConcept);
    	}
		return medicationRequest;
	}
}
