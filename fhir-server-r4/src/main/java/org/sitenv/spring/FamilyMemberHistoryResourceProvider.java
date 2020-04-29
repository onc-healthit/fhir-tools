package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.UriAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.FamilyMemberHistory.FamilyMemberHistoryConditionComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafFamilyMemberHistory;
import org.sitenv.spring.service.FamilyMemberHistoryService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class FamilyMemberHistoryResourceProvider implements IResourceProvider {
	

	public static final String RESOURCE_TYPE = "FamilyMemberHistory";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    FamilyMemberHistoryService service;
    
    public FamilyMemberHistoryResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (FamilyMemberHistoryService) context.getBean("familyMemberHistoryService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return FamilyMemberHistory.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/FamilyMemberHistory/1/_history/4
	 * @param theId : Id of the FamilyMemberHistory
	 * @return : Object of FamilyMemberHistory information
	 */
	@Read(version=true)
    public FamilyMemberHistory readOrVread(@IdParam IdType theId) {
		String id;
		DafFamilyMemberHistory dafFamilyMemberHistory;
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
		   dafFamilyMemberHistory = service.getFamilyMemberHistoryByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       dafFamilyMemberHistory = service.getFamilyMemberHistoryById(id);
		}
		return createFamilyMemberHistoryObject(dafFamilyMemberHistory);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=FamilyMemberHistory.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/FamilyMemberHistory/1/_history
	 * @param theId : ID of the FamilyMemberHistory
	 * @return : List of FamilyMemberHistory's
	 */
	@History()
    public List<FamilyMemberHistory> getFamilyMemberHistoryHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafFamilyMemberHistory> dafFamilyMemberHistoryList = service.getFamilyMemberHistoryHistoryById(id);
        
        List<FamilyMemberHistory> familyMemberHistoryList = new ArrayList<FamilyMemberHistory>();
        for (DafFamilyMemberHistory dafFamilyMemberHistory : dafFamilyMemberHistoryList) {
        	familyMemberHistoryList.add(createFamilyMemberHistoryObject(dafFamilyMemberHistory));
        }
        
        return familyMemberHistoryList;
	}
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theRelationship
	 * @param theInstantiatesUri
	 * @param theSex
	 * @param thePatient
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
        @OptionalParam(name = FamilyMemberHistory.SP_RES_ID)
        TokenAndListParam theId,

        @Description(shortDefinition = "A familymember identifier")
        @OptionalParam(name = FamilyMemberHistory.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "partial | completed | entered-in-error | health-unknown")
        @OptionalParam(name = FamilyMemberHistory.SP_STATUS)
        TokenAndListParam theStatus,
        
        @Description(shortDefinition = "A search by a relationship type")
        @OptionalParam(name = FamilyMemberHistory.SP_RELATIONSHIP)
        TokenAndListParam theRelationship,
        
        @Description(shortDefinition = "Instantiates external protocol or definition")
        @OptionalParam(name = FamilyMemberHistory.SP_INSTANTIATES_URI)
        UriAndListParam theInstantiatesUri,
        
        @Description(shortDefinition = "A search by a sex code of a family member")
        @OptionalParam(name = FamilyMemberHistory.SP_GENDER)
        TokenAndListParam theSex,
        
        @Description(shortDefinition = "The identity of a subject to list family member history items for")
        @OptionalParam(name = FamilyMemberHistory.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "A search by a condition code")
        @OptionalParam(name = FamilyMemberHistory.SP_CODE)
        TokenAndListParam theCode,
        
        
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
		
	        SearchParameterMap paramMap = new SearchParameterMap();
	        paramMap.add(FamilyMemberHistory.SP_RES_ID, theId);
	        paramMap.add(FamilyMemberHistory.SP_IDENTIFIER, theIdentifier);
	        paramMap.add(FamilyMemberHistory.SP_STATUS, theStatus);
	        paramMap.add(FamilyMemberHistory.SP_RELATIONSHIP, theRelationship);
	        paramMap.add(FamilyMemberHistory.SP_INSTANTIATES_URI, theInstantiatesUri);
	        paramMap.add(FamilyMemberHistory.SP_GENDER, theSex);
	        paramMap.add(FamilyMemberHistory.SP_PATIENT, thePatient);
	        paramMap.add(FamilyMemberHistory.SP_CODE, theCode);
	        paramMap.setIncludes(theIncludes);
	        paramMap.setSort(theSort);
	        paramMap.setCount(theCount);
	        
	        final List<DafFamilyMemberHistory> results = service.search(paramMap);
	
	        return new IBundleProvider() {
	            final InstantDt published = InstantDt.withCurrentTime();
	            @Override
	            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
	                List<IBaseResource> familyMemberHistoryList = new ArrayList<IBaseResource>();
	                for(DafFamilyMemberHistory dafFamilyMemberHistory : results){
	                	familyMemberHistoryList.add(createFamilyMemberHistoryObject(dafFamilyMemberHistory));
	                }
	                return familyMemberHistoryList;
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
     * @param dafFamilyMemberHistory : DafDocumentReference familymemberhistory object
     * @return : DocumentReference familymemberhistory object
     */
    private FamilyMemberHistory createFamilyMemberHistoryObject(DafFamilyMemberHistory dafFamilyMemberHistory) {
    	FamilyMemberHistory familyMemberHistory = new FamilyMemberHistory();
        JSONObject familyMemberHistoryJSON = new JSONObject(dafFamilyMemberHistory.getData());

        // Set version
        if(!(familyMemberHistoryJSON.isNull("meta"))) {
        	if(!(familyMemberHistoryJSON.getJSONObject("meta").isNull("versionId"))) {
                familyMemberHistory.setId(new IdType(RESOURCE_TYPE, familyMemberHistoryJSON.getString("id") + "", familyMemberHistoryJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				familyMemberHistory.setId(new IdType(RESOURCE_TYPE, familyMemberHistoryJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
            familyMemberHistory.setId(new IdType(RESOURCE_TYPE, familyMemberHistoryJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(familyMemberHistoryJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = familyMemberHistoryJSON.getJSONArray("identifier");
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
        	familyMemberHistory.setIdentifier(identifiers);
        }
        
        //Set instantiatesUri
        if(!(familyMemberHistoryJSON.isNull("instantiatesUri"))) {
        	List<UriType> uriList = new ArrayList<UriType>();
        	JSONArray theUris = familyMemberHistoryJSON.getJSONArray("instantiatesUri");
        	int noOfUris = theUris.length();
    		for(int i = 0; i < noOfUris; i++) {
    			String uriName = theUris.getString(i) ;
    			UriType uriType = new UriType();
    			uriType.setValue(uriName);
			   	uriList.add(uriType);
    		}
    		familyMemberHistory.setInstantiatesUri(uriList);
        }
        
        //set status
        if(!(familyMemberHistoryJSON.isNull("status"))) {
            familyMemberHistory.setStatus(FamilyMemberHistory.FamilyHistoryStatus.fromCode(familyMemberHistoryJSON.getString("status")));
        }
         
        //Set name
        if(!(familyMemberHistoryJSON.isNull("name"))) {
        	familyMemberHistory.setName(familyMemberHistoryJSON.getString("name"));
        }
        
        //Set date
        if(!(familyMemberHistoryJSON.isNull("date"))) {
        	Date theDate = CommonUtil.convertStringToDate(familyMemberHistoryJSON.getString("date"));
        	familyMemberHistory.setDate(theDate);
        }
        
		//Set deceasedBoolean
		if(!(familyMemberHistoryJSON.isNull("deceasedBoolean"))) {
			BooleanType isDeceased = new BooleanType();
			isDeceased.setValue(familyMemberHistoryJSON.getBoolean("deceasedBoolean"));
			familyMemberHistory.setDeceased(isDeceased);
		}
		
		//Set estimatedAge
		if(!(familyMemberHistoryJSON.isNull("estimatedAge"))) {
			familyMemberHistory.setEstimatedAge(familyMemberHistoryJSON.getBoolean("estimatedAge"));
		}
				
		//Set patient
        if(!(familyMemberHistoryJSON.isNull("patient"))) {
        	JSONObject patientJSON = familyMemberHistoryJSON.getJSONObject("patient");
        	Reference thePatient = new Reference();
        	if(!patientJSON.isNull("reference")) {
        		thePatient.setReference(patientJSON.getString("reference"));
        	}
        	if(!patientJSON.isNull("display")) {
        		thePatient.setDisplay(patientJSON.getString("display"));
        	}
        	if(!patientJSON.isNull("type")) {
        		thePatient.setType(patientJSON.getString("type"));
        	}
        	familyMemberHistory.setPatient(thePatient);
        }
        
        //Set relationship
        if(!(familyMemberHistoryJSON.isNull("relationship"))) {
        	JSONObject relationshipJSON = familyMemberHistoryJSON.getJSONObject("relationship");
    		CodeableConcept theRelationship = new CodeableConcept();
    		if(!(relationshipJSON.isNull("coding"))) {
				JSONArray codingJSON = relationshipJSON.getJSONArray("coding");
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
				theRelationship.setCoding(codingList);
			}
    		if(!relationshipJSON.isNull("text")) {
        		theRelationship.setText(relationshipJSON.getString("text"));
    		}
        	familyMemberHistory.setRelationship(theRelationship);
        }
        
        //Set sex
        if(!(familyMemberHistoryJSON.isNull("sex"))) {
        	JSONObject sexJSON = familyMemberHistoryJSON.getJSONObject("sex");
    		CodeableConcept theSex = new CodeableConcept();
    		if(!(sexJSON.isNull("coding"))) {
				JSONArray codingJSON = sexJSON.getJSONArray("coding");
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
				theSex.setCoding(codingList);
			}
    		if(!sexJSON.isNull("text")) {
        		theSex.setText(sexJSON.getString("text"));
    		}
        	familyMemberHistory.setSex(theSex);
        }
        
        //Set dataAbsentReason
        if(!(familyMemberHistoryJSON.isNull("dataAbsentReason"))) {
        	JSONObject dataAbsentReasonJSON = familyMemberHistoryJSON.getJSONObject("dataAbsentReason");
    		CodeableConcept theDataAbsentReason = new CodeableConcept();
    		if(!(dataAbsentReasonJSON.isNull("coding"))) {
				JSONArray codingJSON = dataAbsentReasonJSON.getJSONArray("coding");
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
				theDataAbsentReason.setCoding(codingList);
			}
    		if(!dataAbsentReasonJSON.isNull("text")) {
        		theDataAbsentReason.setText(dataAbsentReasonJSON.getString("text"));
    		}
        	familyMemberHistory.setDataAbsentReason(theDataAbsentReason);
        }
        
        //Set bornPeriod
		if(!(familyMemberHistoryJSON.isNull("bornPeriod"))) {
			Period theBornPeriod = new Period();
			if(!(familyMemberHistoryJSON.getJSONObject("bornPeriod").isNull("start"))) {
        		Date bornPeriodStart = CommonUtil.convertStringToDate(familyMemberHistoryJSON.getJSONObject("bornPeriod").getString("start"));
        		theBornPeriod.setStart(bornPeriodStart);
			}
			if(!(familyMemberHistoryJSON.getJSONObject("bornPeriod").isNull("end"))) {
        		Date bornPeriodEnd = CommonUtil.convertStringToDate(familyMemberHistoryJSON.getJSONObject("bornPeriod").getString("end"));
        		theBornPeriod.setEnd(bornPeriodEnd);
			}
			familyMemberHistory.setBorn(theBornPeriod);
		}
        //Set condition
        if(!(familyMemberHistoryJSON.isNull("condition"))) {
			JSONArray conditionJSON = familyMemberHistoryJSON.getJSONArray("condition");
			int noOfConditions = conditionJSON.length();
			List<FamilyMemberHistoryConditionComponent> conditionList = new ArrayList<FamilyMemberHistoryConditionComponent>();
			for(int i = 0; i < noOfConditions; i++) {
				FamilyMemberHistoryConditionComponent theCondition = new FamilyMemberHistoryConditionComponent();
				//Set code
    			if(!(conditionJSON.getJSONObject(i).isNull("code"))) {
    				JSONObject conditionCodeJSON = conditionJSON.getJSONObject(i).getJSONObject("code");
    				CodeableConcept theConditionCode = new CodeableConcept();
    				if(!(conditionCodeJSON.isNull("coding"))) {
    					JSONArray conditionCodingJSON = conditionCodeJSON.getJSONArray("coding");
    					int noOfConditionCodings = conditionCodingJSON.length(); 
    					List<Coding> conditionCodingList = new ArrayList<Coding>();
    					for(int n = 0; n < noOfConditionCodings; n++) {
    						Coding theConditionCoding = new Coding();
    						if(!(conditionCodingJSON.getJSONObject(n).isNull("system"))) {
    							theConditionCoding.setSystem(conditionCodingJSON.getJSONObject(n).getString("system"));
    						}
    						if(!(conditionCodingJSON.getJSONObject(n).isNull("code"))) {
    							theConditionCoding.setCode(conditionCodingJSON.getJSONObject(n).getString("code"));
    						}
    						if(!(conditionCodingJSON.getJSONObject(n).isNull("display"))) {
    							theConditionCoding.setDisplay(conditionCodingJSON.getJSONObject(n).getString("display"));
    						}
    						conditionCodingList.add(theConditionCoding);
    					}
    					theConditionCode.setCoding(conditionCodingList);
    				}
    				if(!(conditionCodeJSON.isNull("text"))) {
    					theConditionCode.setText(conditionCodeJSON.getString("text"));
    				}
    				theCondition.setCode(theConditionCode);
    			}
    			//Set contributedToDeath
    			if(!(conditionJSON.getJSONObject(i).isNull("contributedToDeath"))) {
    				theCondition.setContributedToDeath(conditionJSON.getJSONObject(i).getBoolean("contributedToDeath"));
    			}
    			//Set onsetAge
    			if(!(conditionJSON.getJSONObject(i).isNull("onsetAge"))) {
    				JSONObject onSetAgeJSON = conditionJSON.getJSONObject(i).getJSONObject("onsetAge");
    				Age theOnSetAge = new Age();
    				if(!(onSetAgeJSON.isNull("value"))) {
    					theOnSetAge.setValue(onSetAgeJSON.getLong("value"));
    				}
    				if(!(onSetAgeJSON.isNull("unit"))) {
    					theOnSetAge.setUnit(onSetAgeJSON.getString("unit"));
    				}
    				if(!(onSetAgeJSON.isNull("system"))) {
    					theOnSetAge.setSystem(onSetAgeJSON.getString("system"));
    				}
    				if(!(onSetAgeJSON.isNull("code"))) {
    					theOnSetAge.setCode(onSetAgeJSON.getString("code"));
    				}
    				theCondition.setOnset(theOnSetAge);
    			}
    			
    			//Set note
    	        if(!(conditionJSON.getJSONObject(i).isNull("note"))) {
    	        	JSONArray noteJSON = conditionJSON.getJSONObject(i).getJSONArray("note");
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
    	        	theCondition.setNote(noteList);
    	        }
    			conditionList.add(theCondition);
			}
			familyMemberHistory.setCondition(conditionList);
        }
		
        //Set note
        if(!(familyMemberHistoryJSON.isNull("note"))) {
        	JSONArray noteJSON = familyMemberHistoryJSON.getJSONArray("note");
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
        	familyMemberHistory.setNote(noteList);
        }
        return familyMemberHistory;
    }
}
