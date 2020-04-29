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
import org.hl7.fhir.r4.model.AllergyIntolerance.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.service.AllergyIntoleranceService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class AllergyIntoleranceResourceProvider implements IResourceProvider {
	
	public static final String RESOURCE_TYPE = "AllergyIntolerance";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    AllergyIntoleranceService service;

    public AllergyIntoleranceResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (AllergyIntoleranceService) context.getBean("AllergyIntoleranceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return AllergyIntolerance.class;
	}
	
 	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/AllergyIntolerance/1/_history/4
	 * @param theId : Id of the AllergyIntolerance
	 * @return : Object of AllergyIntolerance information
	 */
	@Read(version=true)
    public AllergyIntolerance readOrVread(@IdParam IdType theId) {
		String id;
		DafAllergyIntolerance dafAllergyIntolerance;
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
		   dafAllergyIntolerance = service.getAllergyIntoleranceByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       dafAllergyIntolerance = service.getAllergyIntoleranceById(id);
		}
		return createAllergyIntoleranceObject(dafAllergyIntolerance);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=AllergyIntolerance.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/AllergyIntolerance/1/_history
	 * @param theId : ID of the AllergyIntolerance
	 * @return : List of AllergyIntolerances
	 */
	@History()
    public List<AllergyIntolerance> getAllergyIntoleranceHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafAllergyIntolerance> dafAllergyIntoleranceList = service.getAllergyIntoleranceHistoryById(id);
        
        List<AllergyIntolerance> allergyIntoleranceList = new ArrayList<AllergyIntolerance>();
        for (DafAllergyIntolerance dafAllergyIntolerance : dafAllergyIntoleranceList) {
        	allergyIntoleranceList.add(createAllergyIntoleranceObject(dafAllergyIntolerance));
        }
        return allergyIntoleranceList;
	}
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theDate
	 * @param theSeverity
	 * @param theManifestation
	 * @param theRecorder
	 * @param theCode
	 * @param theVerificationStatus
	 * @param theCriticality
	 * @param theClinicalStatus
	 * @param theType
	 * @param theOnset
	 * @param theRoute
	 * @param theAsserter
	 * @param thePatient
	 * @param theCategory
	 * @param theLastDate
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */

    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = AllergyIntolerance.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "A AllergyIntolerance identifier")
        @OptionalParam(name = AllergyIntolerance.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "mild | moderate | severe (of event as a whole)")
        @OptionalParam(name = AllergyIntolerance.SP_SEVERITY)
        TokenAndListParam theSeverity,

        @Description(shortDefinition = "Date first version of the resource instance was recorded")
        @OptionalParam(name = AllergyIntolerance.SP_DATE)
        DateRangeParam theDate,
        
        @Description(shortDefinition = "Clinical symptoms/signs associated with the Event")
        @OptionalParam(name = AllergyIntolerance.SP_MANIFESTATION)
        TokenAndListParam theManifestation,
        
        @Description(shortDefinition = "Who recorded the sensitivity")
        @OptionalParam(name = AllergyIntolerance.SP_RECORDER)
        ReferenceAndListParam theRecorder,
        
        @Description(shortDefinition = "Code that identifies the allergy or intolerance")
        @OptionalParam(name = AllergyIntolerance.SP_CODE)
        TokenAndListParam theCode,
        
        @Description(shortDefinition = "unconfirmed | confirmed | refuted | entered-in-error")
        @OptionalParam(name = AllergyIntolerance.SP_VERIFICATION_STATUS)
        TokenAndListParam theVerificationStatus,
        
        @Description(shortDefinition = "low | high | unable-to-assess")
        @OptionalParam(name = AllergyIntolerance.SP_CRITICALITY)
        TokenAndListParam theCriticality,
        
        @Description(shortDefinition = "active | inactive | resolved")
        @OptionalParam(name = AllergyIntolerance.SP_CLINICAL_STATUS)
        TokenAndListParam theClinicalStatus,
        
        @Description(shortDefinition = "allergy | intolerance - Underlying mechanism (if known)")
        @OptionalParam(name = AllergyIntolerance.SP_TYPE)
        TokenAndListParam theType,
        
        @Description(shortDefinition = "Date(/time) when manifestations showed")
        @OptionalParam(name = AllergyIntolerance.SP_ONSET)
        DateRangeParam theOnset,
        
        @Description(shortDefinition = "How the subject was exposed to the substance")
        @OptionalParam(name = AllergyIntolerance.SP_ROUTE)
        TokenAndListParam theRoute,
        
        @Description(shortDefinition = "Source of the information about the allergy")
        @OptionalParam(name = AllergyIntolerance.SP_ASSERTER)
        ReferenceAndListParam theAsserter,
        
        @Description(shortDefinition = "Who the sensitivity is for")
        @OptionalParam(name = AllergyIntolerance.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "food | medication | environment | biologic")
        @OptionalParam(name = AllergyIntolerance.SP_CATEGORY)
        TokenAndListParam theCategory,
        
        @Description(shortDefinition = "Date(/time) of last known occurrence of a reaction")
        @OptionalParam(name = AllergyIntolerance.SP_LAST_DATE)
        DateRangeParam theLastDate,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,

		@IncludeParam(allow= {"*"})
		Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
    	
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(AllergyIntolerance.SP_RES_ID, theId);
        paramMap.add(AllergyIntolerance.SP_IDENTIFIER, theIdentifier);
        paramMap.add(AllergyIntolerance.SP_SEVERITY, theSeverity);
        paramMap.add(AllergyIntolerance.SP_DATE, theDate);
        paramMap.add(AllergyIntolerance.SP_MANIFESTATION, theManifestation);
        paramMap.add(AllergyIntolerance.SP_RECORDER, theRecorder);
        paramMap.add(AllergyIntolerance.SP_CODE, theCode);
        paramMap.add(AllergyIntolerance.SP_VERIFICATION_STATUS, theVerificationStatus);
        paramMap.add(AllergyIntolerance.SP_CRITICALITY, theCriticality);
        paramMap.add(AllergyIntolerance.SP_CLINICAL_STATUS, theClinicalStatus);
        paramMap.add(AllergyIntolerance.SP_TYPE, theType);
        paramMap.add(AllergyIntolerance.SP_ONSET, theOnset);
        paramMap.add(AllergyIntolerance.SP_ROUTE, theRoute);
        paramMap.add(AllergyIntolerance.SP_ASSERTER, theAsserter);
        paramMap.add(AllergyIntolerance.SP_PATIENT, thePatient);
        paramMap.add(AllergyIntolerance.SP_CATEGORY, theCategory);
        paramMap.add(AllergyIntolerance.SP_LAST_DATE, theLastDate);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<DafAllergyIntolerance> results = service.search(paramMap);

        return new IBundleProvider() {
        	final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> allergyIntoleranceList = new ArrayList<IBaseResource>();
				List<String> ids = new ArrayList<String>();
				for(DafAllergyIntolerance dafAllergyIntolerance : results){
					AllergyIntolerance allergyIntolerance = createAllergyIntoleranceObject(dafAllergyIntolerance);
                	allergyIntoleranceList.add(allergyIntolerance);
					ids.add(((IdType)allergyIntolerance.getIdElement()).getResourceType()+"/"+((IdType)allergyIntolerance.getIdElement()).getIdPart());
				}
				if(theRevIncludes.size() >0 ){
					allergyIntoleranceList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
				}
                return allergyIntoleranceList;
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
     * @param dafAllergyIntolerance : DafDocumentReference AllergyIntolerance object
     * @return : DocumentReference AllergyIntolerance object
     */
    private AllergyIntolerance createAllergyIntoleranceObject(DafAllergyIntolerance dafAllergyIntolerance) {
        AllergyIntolerance allergyIntolerance = new AllergyIntolerance();
        JSONObject allergyIntoleranceJSON = new JSONObject(dafAllergyIntolerance.getData());

        // Set version
        if(!(allergyIntoleranceJSON.isNull("meta"))) {
        	if(!(allergyIntoleranceJSON.getJSONObject("meta").isNull("versionId"))) {
        		allergyIntolerance.setId(new IdType(RESOURCE_TYPE, allergyIntoleranceJSON.getString("id") + "", allergyIntoleranceJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				allergyIntolerance.setId(new IdType(RESOURCE_TYPE, allergyIntoleranceJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
        	allergyIntolerance.setId(new IdType(RESOURCE_TYPE, allergyIntoleranceJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(allergyIntoleranceJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = allergyIntoleranceJSON.getJSONArray("identifier");
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
        	allergyIntolerance.setIdentifier(identifiers);
        }
        //set clinicalStatus
        if (!allergyIntoleranceJSON.isNull("clinicalStatus")) {
			JSONObject clinicalStatusJSON = allergyIntoleranceJSON.getJSONObject("clinicalStatus");
			CodeableConcept theClinicalStatus = new CodeableConcept();
			
			if (!clinicalStatusJSON.isNull("coding")) {
				JSONArray codingJSON = clinicalStatusJSON.getJSONArray("coding");
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
				theClinicalStatus.setCoding(codingList);
			}
			if (!clinicalStatusJSON.isNull("text")) {
				theClinicalStatus.setText(clinicalStatusJSON.getString("text"));
			}
			allergyIntolerance.setClinicalStatus(theClinicalStatus);
		}
        
        //set verificationStatus
        if (!allergyIntoleranceJSON.isNull("verificationStatus")) {
			JSONObject verificationStatusJSON = allergyIntoleranceJSON.getJSONObject("verificationStatus");
			CodeableConcept theVerificationStatus = new CodeableConcept();
			
			if (!verificationStatusJSON.isNull("coding")) {
				JSONArray codingJSON = verificationStatusJSON.getJSONArray("coding");
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
				theVerificationStatus.setCoding(codingList);
			}
			if (!verificationStatusJSON.isNull("text")) {
				theVerificationStatus.setText(verificationStatusJSON.getString("text"));
			}
			allergyIntolerance.setVerificationStatus(theVerificationStatus);
		}
        
        //set type
        if (!allergyIntoleranceJSON.isNull("type")) {
        	allergyIntolerance.setType(AllergyIntoleranceType.fromCode(allergyIntoleranceJSON.getString("type")));
		}
        
        //set category
        if (!allergyIntoleranceJSON.isNull("category")) {
			JSONArray categoryJSON = allergyIntoleranceJSON.getJSONArray("category");
			int noOfCategory = categoryJSON.length();
			for (int i = 0; i < noOfCategory; i++) {
				allergyIntolerance.addCategory(AllergyIntoleranceCategory.fromCode(categoryJSON.getString(i)));
			}
		}
        
        //set Criticality
        if (!allergyIntoleranceJSON.isNull("criticality")) {
			allergyIntolerance.setCriticality(AllergyIntoleranceCriticality.fromCode(allergyIntoleranceJSON.getString("criticality")));
		}
        
        //set reaction
        if (!allergyIntoleranceJSON.isNull("reaction")) {
			JSONArray reactionJSON = allergyIntoleranceJSON.getJSONArray("reaction");
			List<AllergyIntoleranceReactionComponent> reactionList = new ArrayList<AllergyIntoleranceReactionComponent>();
			int noOfReactions = reactionJSON.length();		
			for (int k = 0; k < noOfReactions; k++) {
				AllergyIntoleranceReactionComponent theReaction = new AllergyIntoleranceReactionComponent();
				
				if (!reactionJSON.getJSONObject(k).isNull("onset")) {
					//theReaction.setOnset(CommonUtil.convertStringToDate(reactionJSON.getJSONObject(k).getString("onset")));
				}
				
				 if (!reactionJSON.getJSONObject(k).isNull("substance")) {
						JSONObject substanceJSON = reactionJSON.getJSONObject(k).getJSONObject("substance");
						CodeableConcept substance = new CodeableConcept();
						
						if (!substanceJSON.isNull("coding")) {
							JSONArray codingJSON = substanceJSON.getJSONArray("coding");
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
							substance.setCoding(codingList);
						}
						if (!substanceJSON.isNull("text")) {
							substance.setText(substanceJSON.getString("text"));
						}
						theReaction.setSubstance(substance);
				 }
				 
				 if (!reactionJSON.getJSONObject(k).isNull("manifestation")) {
						JSONArray manifestationJSON = reactionJSON.getJSONObject(k).getJSONArray("manifestation");
						List<CodeableConcept> manifestationList = new ArrayList<CodeableConcept>();
						int noOfManifestation = manifestationJSON.length();
						
						for (int i = 0; i < noOfManifestation; i++) {
							CodeableConcept theManifestation = new CodeableConcept();
							
							if (!manifestationJSON.getJSONObject(i).isNull("coding")) {
								JSONArray codingJSON = manifestationJSON.getJSONObject(i).getJSONArray("coding");
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
								theManifestation.setCoding(codingList);
							}
							manifestationList.add(theManifestation);
						}
						theReaction.setManifestation(manifestationList);
					}
				 if (!reactionJSON.getJSONObject(k).isNull("severity")) {
						theReaction.setSeverity(AllergyIntoleranceSeverity.fromCode(reactionJSON.getJSONObject(k).getString("severity")));
				 }
				 reactionList.add(theReaction);
			}
			allergyIntolerance.setReaction(reactionList);       
        }
        //set recorder
        if (!allergyIntoleranceJSON.isNull("recorder")) {
			JSONObject recorderJSON = allergyIntoleranceJSON.getJSONObject("recorder");
			Reference theRecorder = new Reference();
			
			if (!recorderJSON.isNull("reference")) {
				theRecorder.setReference(recorderJSON.getString("reference"));
			}
			if (!recorderJSON.isNull("display")) {
				theRecorder.setDisplay(recorderJSON.getString("display"));
			}
			allergyIntolerance.setRecorder(theRecorder);
		}
        
       //set lastOccurrence
        if (!allergyIntoleranceJSON.isNull("lastOccurrence")) {
      		Date theLastOccurrence = CommonUtil.convertStringToDateYear(allergyIntoleranceJSON.getString("lastOccurrence"));
      		allergyIntolerance.setLastOccurrence(theLastOccurrence);
		}
        
        //set code
        if (!allergyIntoleranceJSON.isNull("code")) {
			JSONObject codeJSON = allergyIntoleranceJSON.getJSONObject("code");
			CodeableConcept theCode = new CodeableConcept();
			
			if (!codeJSON.isNull("coding")) {
				JSONArray codingJSON = codeJSON.getJSONArray("coding");
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
				theCode.setCoding(codingList);
			}
			if (!codeJSON.isNull("text")) {
				theCode.setText(codeJSON.getString("text"));
			}
			allergyIntolerance.setCode(theCode);
		}
        
        //set Patient
      	if (!allergyIntoleranceJSON.isNull("patient")) {
			JSONObject patientJSON = allergyIntoleranceJSON.getJSONObject("patient");
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
			allergyIntolerance.setPatient(thePatient);
		}
      	
      	//set date
      	if (!allergyIntoleranceJSON.isNull("recordedDate")) {
      		allergyIntolerance.setRecordedDate(CommonUtil.convertStringToDate(allergyIntoleranceJSON.getString("recordedDate")));
		}
       
      	//set asserter
      	if (!allergyIntoleranceJSON.isNull("asserter")) {
			JSONObject asserterJSON = allergyIntoleranceJSON.getJSONObject("asserter");
			Reference theAsserter =new Reference();
			if (!asserterJSON.isNull("reference")) {
				theAsserter.setReference(asserterJSON.getString("reference"));
			}
			if (!asserterJSON.isNull("display")) {
				theAsserter.setDisplay(asserterJSON.getString("display"));
			}
			allergyIntolerance.setAsserter(theAsserter);
		}     
        return allergyIntolerance;
    }
}
