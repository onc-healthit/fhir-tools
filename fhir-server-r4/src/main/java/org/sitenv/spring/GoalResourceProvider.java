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
import org.hl7.fhir.r4.model.Goal.GoalTargetComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafGoal;
import org.sitenv.spring.service.GoalService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class GoalResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Goal";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    GoalService service;
    
    public GoalResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (GoalService) context.getBean("goalService");
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Goal.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Goal/1/_history/3.0
	 * @param theId : Id of the Goal
	 * @return : Object of Goal information
	 */
	@Read(version=true)
    public Goal readOrVread(@IdParam IdType theId) {
		String id;
		DafGoal dafGoal;
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
			dafGoal = service.getGoalByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			dafGoal = service.getGoalById(id);
		}
		return createGoalObject(dafGoal);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=Goal.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Goal/1/_history
	 * @param theId : ID of the Goal
	 * @return : List of Goal's
	 */
	@History()
    public List<Goal> getGoalHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafGoal> dafGoalList = service.getGoalHistoryById(id);
        
        List<Goal> goalList = new ArrayList<Goal>();
        for (DafGoal dafGoal : dafGoalList) {
        	goalList.add(createGoalObject(dafGoal));
        }
        
        return goalList;
	}
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theTargetDate
	 * @param theCategory
	 * @param theStartDate
	 * @param theAchievementStatus
	 * @param theLifecycleStatus
	 * @param thePatient
	 * @param theSubject
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
        @OptionalParam(name = Goal.SP_RES_ID)
        TokenAndListParam theId,

        @Description(shortDefinition = "A goal identifier")
        @OptionalParam(name = Goal.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "Reach goal on or before")
        @OptionalParam(name = Goal.SP_TARGET_DATE)
        DateRangeParam theTargetDate,
        
        @Description(shortDefinition = "E.g. Treatment, dietary, behavioral, etc.")
        @OptionalParam(name = Goal.SP_CATEGORY)
        TokenAndListParam theCategory,
        
        @Description(shortDefinition = "When goal pursuit begins")
        @OptionalParam(name = Goal.SP_START_DATE)
        DateRangeParam theStartDate,
        
        @Description(shortDefinition = "in-progress | improving | worsening | no-change | achieved | sustaining | not-achieved | no-progress | not-attainable")
        @OptionalParam(name = Goal.SP_ACHIEVEMENT_STATUS)
        TokenAndListParam theAchievementStatus,
        
        @Description(shortDefinition = "proposed | planned | accepted | active | on-hold | completed | cancelled | entered-in-error | rejected")
        @OptionalParam(name = Goal.SP_LIFECYCLE_STATUS)
        TokenAndListParam theLifecycleStatus,
  
        @Description(shortDefinition = "Who this goal is intended for")
        @OptionalParam(name = Goal.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "Who this goal is intended for")
        @OptionalParam(name = Goal.SP_SUBJECT)
        ReferenceAndListParam theSubject,

        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(Goal.SP_RES_ID, theId);
            paramMap.add(Goal.SP_IDENTIFIER, theIdentifier);
            paramMap.add(Goal.SP_TARGET_DATE, theTargetDate);
            paramMap.add(Goal.SP_CATEGORY, theCategory);
            paramMap.add(Goal.SP_START_DATE, theStartDate);
            paramMap.add(Goal.SP_ACHIEVEMENT_STATUS, theAchievementStatus);
            paramMap.add(Goal.SP_LIFECYCLE_STATUS, theLifecycleStatus);
            paramMap.add(Goal.SP_SUBJECT, theSubject);
            paramMap.add(Goal.SP_PATIENT, thePatient);

            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<DafGoal> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> goalList = new ArrayList<IBaseResource>();
					List<String> ids = new ArrayList<String>();
                    for(DafGoal dafGoal : results){
						Goal goal= createGoalObject(dafGoal);
						goalList.add(goal);
						ids.add(((IdType)goal.getIdElement()).getResourceType()+"/"+((IdType)goal.getIdElement()).getIdPart());
					}

					if(theRevIncludes.size() >0 ){
						goalList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
					}

					return goalList;
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
     * @param dafGoal : DafDocumentReference goal object
     * @return : DocumentReference goal object
     */
    private Goal createGoalObject(DafGoal dafGoal) {
    	Goal goal = new Goal();
        JSONObject goalJSON = new JSONObject(dafGoal.getData());

        // Set version
        if(!(goalJSON.isNull("meta"))) {
        	if(!(goalJSON.getJSONObject("meta").isNull("versionId"))) {
                goal.setId(new IdType(RESOURCE_TYPE, goalJSON.getString("id") + "", goalJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				goal.setId(new IdType(RESOURCE_TYPE, goalJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
            goal.setId(new IdType(RESOURCE_TYPE, goalJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(goalJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = goalJSON.getJSONArray("identifier");
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
        	goal.setIdentifier(identifiers);
        }

        //Set outcomeReference
        if(!(goalJSON.isNull("outcomeReference"))) {
        	JSONArray outcomeReferenceJSON = goalJSON.getJSONArray("outcomeReference");
        	int noOfOutcomeReference = outcomeReferenceJSON.length();
        	List<Reference> outcomeReferenceList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfOutcomeReference; i++) {
        		Reference theOutcomeReference = new Reference();
        		if(!outcomeReferenceJSON.getJSONObject(i).isNull("display")) {
            		theOutcomeReference.setDisplay(outcomeReferenceJSON.getJSONObject(i).getString("display"));
        		}
        		if(!outcomeReferenceJSON.getJSONObject(i).isNull("type")) {
            		theOutcomeReference.setType(outcomeReferenceJSON.getJSONObject(i).getString("type"));
        		}
        		if(!outcomeReferenceJSON.getJSONObject(i).isNull("reference")) {
            		theOutcomeReference.setReference(outcomeReferenceJSON.getJSONObject(i).getString("reference"));
        		}
        		outcomeReferenceList.add(theOutcomeReference);
        	}
        	goal.setOutcomeReference(outcomeReferenceList);
        }

        //Set lifecycleStatus
        if(!(goalJSON.isNull("lifecycleStatus"))) {
        	goal.setLifecycleStatus(Goal.GoalLifecycleStatus.fromCode(goalJSON.getString("lifecycleStatus")));
        }
        //Set statusReason
        if(!(goalJSON.isNull("statusReason"))) {
        	goal.setStatusReason(goalJSON.getString("statusReason"));
        }
        //Set category
        if(!(goalJSON.isNull("category"))) {
        	JSONArray categoryJSON = goalJSON.getJSONArray("category");
        	int noOfcategory = categoryJSON.length();
        	List<CodeableConcept> categoryList = new ArrayList<CodeableConcept>();
        	for(int i = 0; i < noOfcategory; i++) {
        		CodeableConcept theCategory = new CodeableConcept();
        		if(!(categoryJSON.getJSONObject(i).isNull("coding"))) {
					JSONArray codingJSON = categoryJSON.getJSONObject(i).getJSONArray("coding");
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
        		if(!categoryJSON.getJSONObject(i).isNull("text")) {
            		theCategory.setText(categoryJSON.getJSONObject(i).getString("text"));
        		}
        		categoryList.add(theCategory);
        	}
        	goal.setCategory(categoryList);
        }
        //Set achievementStatus
        if(!(goalJSON.isNull("achievementStatus"))) {
        	JSONObject achievementStatusJSON = goalJSON.getJSONObject("achievementStatus");
         	CodeableConcept theAchievementStatus = new CodeableConcept();
    		if(!(achievementStatusJSON.isNull("coding"))) {
				JSONArray codingJSON = achievementStatusJSON.getJSONArray("coding");
				int noOfACodings = codingJSON.length();
				List<Coding> codingList = new ArrayList<Coding>();
				for(int j = 0; j < noOfACodings; j++) {
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
				theAchievementStatus.setCoding(codingList);
			}
    		if(!achievementStatusJSON.isNull("text")) {
        		theAchievementStatus.setText(achievementStatusJSON.getString("text"));
    		}
    		goal.setAchievementStatus(theAchievementStatus);
        }
        //Set priority
        if(!(goalJSON.isNull("priority"))) {
        	JSONObject priorityJSON = goalJSON.getJSONObject("priority");
         	CodeableConcept thePriority = new CodeableConcept();
    		if(!(priorityJSON.isNull("coding"))) {
				JSONArray codingJSON = priorityJSON.getJSONArray("coding");
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
				thePriority.setCoding(codingList);
			}
    		if(!priorityJSON.isNull("text")) {
        		thePriority.setText(priorityJSON.getString("text"));
    		}
    		goal.setPriority(thePriority);
        }
        
        //Set description
        if(!(goalJSON.isNull("description"))) {
        	JSONObject descriptionJSON = goalJSON.getJSONObject("description");
         	CodeableConcept theDescription = new CodeableConcept();
    		if(!(descriptionJSON.isNull("coding"))) {
				JSONArray codingJSON = descriptionJSON.getJSONArray("coding");
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
				theDescription.setCoding(codingList);
			}
    		if(!descriptionJSON.isNull("text")) {
        		theDescription.setText(descriptionJSON.getString("text"));
    		}
    		goal.setDescription(theDescription);
        }
        //Set target
        if(!(goalJSON.isNull("target"))) {
        	JSONArray targetJSON = goalJSON.getJSONArray("target");
        	int noOfTargets = targetJSON.length();
        	List<GoalTargetComponent> targetList = new ArrayList<GoalTargetComponent>();
        	for(int t = 0; t < noOfTargets; t++) {
        		GoalTargetComponent theTarget = new GoalTargetComponent();
        		if(!targetJSON.getJSONObject(t).isNull("measure")) {
        			JSONObject measureJSON = targetJSON.getJSONObject(t).getJSONObject("measure");
        			CodeableConcept theMeasure = new CodeableConcept();
        			if(!(measureJSON.isNull("coding"))) {
        				JSONArray codingJSON = measureJSON.getJSONArray("coding");
        				int noOfTCodings = codingJSON.length();
        				List<Coding> codingList = new ArrayList<Coding>();
        				for(int m = 0; m < noOfTCodings; m++) {
        					Coding theCoding  = new Coding();
        					if(!codingJSON.getJSONObject(m).isNull("system")) {
        						theCoding.setSystem(codingJSON.getJSONObject(m).getString("system"));
        					}
        					if(!codingJSON.getJSONObject(m).isNull("code")) {
        						theCoding.setCode(codingJSON.getJSONObject(m).getString("code"));
        					}
        					if(!codingJSON.getJSONObject(m).isNull("display")) {
        						theCoding.setDisplay(codingJSON.getJSONObject(m).getString("display"));
        					}
        					codingList.add(theCoding);
        				}
        				theMeasure.setCoding(codingList);
        			}
        			theTarget.setMeasure(theMeasure);
        		}
        		
        		if(!targetJSON.getJSONObject(t).isNull("detailRange")) {
        			JSONObject detailRangeJSON = targetJSON.getJSONObject(t).getJSONObject("detailRange");
        			Range theDetailRange = new Range();
            		if(!detailRangeJSON.isNull("low")) {
            			JSONObject lowJSON = detailRangeJSON.getJSONObject("low");
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
                		theDetailRange.setLow(theLow);
            		}
            		if(!detailRangeJSON.isNull("high")) {
            			JSONObject highJSON = detailRangeJSON.getJSONObject("high");
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
                		theDetailRange.setHigh(theHigh);
            		}
            		theTarget.setDetail(theDetailRange);
        		}
        		 
                //Set dueDate
                if(!(targetJSON.getJSONObject(t).isNull("dueDate"))) {
                	DateType theDueDate = new DateType();
                	Date dueDate = CommonUtil.convertStringToDate(targetJSON.getJSONObject(t).getString("dueDate"));
                	theDueDate.setValue(dueDate);
                	theTarget.setDue(theDueDate);
                }
        		targetList.add(theTarget);
        	}
        	goal.setTarget(targetList);
        }
        //Set subject
        if(!(goalJSON.isNull("subject"))) {
        	JSONObject subjectJSON = goalJSON.getJSONObject("subject");
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
        	goal.setSubject(theSubject);
        }
        
        //Set statusDate
        if(!(goalJSON.isNull("statusDate"))) {
        	Date statusDate = CommonUtil.convertStringToDate(goalJSON.getString("statusDate"));
        	goal.setStatusDate(statusDate);
        }
       
        //Set startDate
        if(!(goalJSON.isNull("startDate"))) {
        	DateType theStartDate = new DateType();
        	Date startDate = CommonUtil.convertStringToDate(goalJSON.getString("startDate"));
        	theStartDate.setValue(startDate);
        	goal.setStart(theStartDate);
        }

        //Set addresses
        if(!(goalJSON.isNull("addresses"))) {
        	JSONArray addressesJSON = goalJSON.getJSONArray("addresses");
        	int noOfAddresses = addressesJSON.length();
        	List<Reference> addressesList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfAddresses; i++) {
        		Reference theAddresses = new Reference();
        		if(!addressesJSON.getJSONObject(i).isNull("display")) {
            		theAddresses.setDisplay(addressesJSON.getJSONObject(i).getString("display"));
        		}
        		if(!addressesJSON.getJSONObject(i).isNull("type")) {
            		theAddresses.setType(addressesJSON.getJSONObject(i).getString("type"));
        		}
        		if(!addressesJSON.getJSONObject(i).isNull("reference")) {
            		theAddresses.setReference(addressesJSON.getJSONObject(i).getString("reference"));
        		}
        		addressesList.add(theAddresses);
        	}
        	goal.setAddresses(addressesList);
        }

        //Set note
        if(!(goalJSON.isNull("note"))) {
        	JSONArray noteJSON = goalJSON.getJSONArray("note");
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
        	goal.setNote(noteList);
        }
        
        //Set outcomeCode
        if(!(goalJSON.isNull("outcomeCode"))) {
        	JSONArray outcomeCodeJSON = goalJSON.getJSONArray("outcomeCode");
        	int noOfOutcomeCode = outcomeCodeJSON.length();
        	List<CodeableConcept> outcomeCodeList = new ArrayList<CodeableConcept>();
        	for(int i = 0; i < noOfOutcomeCode; i++) {
        		CodeableConcept theOutcomeCode = new CodeableConcept();
        		if(!(outcomeCodeJSON.getJSONObject(i).isNull("coding"))) {
					JSONArray codingJSON = outcomeCodeJSON.getJSONObject(i).getJSONArray("coding");
					int noOfOCodings = codingJSON.length();
					List<Coding> codingList = new ArrayList<Coding>();
					for(int j = 0; j < noOfOCodings; j++) {
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
					theOutcomeCode.setCoding(codingList);
				}
        		if(!outcomeCodeJSON.getJSONObject(i).isNull("text")) {
            		theOutcomeCode.setText(outcomeCodeJSON.getJSONObject(i).getString("text"));
        		}
        		outcomeCodeList.add(theOutcomeCode);
        	}
        	goal.setOutcomeCode(outcomeCodeList);
        }
        return goal;
    }
}
