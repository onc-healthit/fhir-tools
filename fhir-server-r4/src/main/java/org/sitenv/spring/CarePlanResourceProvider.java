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
import ca.uhn.fhir.rest.param.UriAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.CarePlan.CarePlanActivityComponent;
import org.hl7.fhir.r4.model.CarePlan.CarePlanActivityDetailComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.service.CarePlanService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CarePlanResourceProvider implements IResourceProvider {
	
	public static final String RESOURCE_TYPE = "CarePlan";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    CarePlanService service;
    
    public CarePlanResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (CarePlanService) context.getBean("carePlanService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return CarePlan.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/CarePlan/1/_history/3.0
	 * @param theId : Id of the CarePlan
	 * @return : Object of CarePlan information
	 */
	@Read(version=true)
    public CarePlan readOrVread(@IdParam IdType theId) {
		String id;
		DafCarePlan dafCarePlan;
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
			dafCarePlan = service.getCarePlanByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			dafCarePlan = service.getCarePlanById(id);
		}
		return createCarePlanObject(dafCarePlan);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=CarePlan.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/CarePlan/1/_history
	 * @param theId : ID of the CarePlan
	 * @return : List of CarePlan's
	 */
	@History()
    public List<CarePlan> getCarePlanHistoryById( @IdParam IdType theId) {

		String id;
		try {
			id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafCarePlan> dafCarePlanList = service.getCarePlanHistoryById(id);
        
        List<CarePlan> carePlanList = new ArrayList<CarePlan>();
        for (DafCarePlan dafCarePlan : dafCarePlanList) {
        	carePlanList.add(createCarePlanObject(dafCarePlan));
        }
        
        return carePlanList;
	}
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theDate
	 * @param theCareTeam
	 * @param thePerformer
	 * @param theGoal
	 * @param theSubject
	 * @param theReplaces
	 * @param thePartOf
	 * @param theIntent
	 * @param theCondition
	 * @param theBasedOn
	 * @param thePatient
	 * @param theInstantiatesUri
	 * @param theStatus
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
        @OptionalParam(name = CarePlan.SP_RES_ID)
        TokenAndListParam theId,

        @Description(shortDefinition = "A careplan identifier")
        @OptionalParam(name = CarePlan.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "Time period plan covers")
        @OptionalParam(name = CarePlan.SP_DATE)
        DateRangeParam theDate,

        @Description(shortDefinition = "Who's involved in plan?")
        @OptionalParam(name = CarePlan.SP_CARE_TEAM, targetTypes = {CareTeam.class})
        ReferenceAndListParam theCareTeam,

        @Description(shortDefinition = "Matches if the practitioner is listed as a performer in any of the \"simple\" activities")
        @OptionalParam(name = CarePlan.SP_PERFORMER, 
        targetTypes = {CareTeam.class, Device.class, HealthcareService.class, Organization.class, Patient.class, Practitioner.class} )
        ReferenceAndListParam thePerformer,

        @Description(shortDefinition = "Desired outcome of plan")
        @OptionalParam(name = CarePlan.SP_GOAL)
        ReferenceAndListParam theGoal,

        @Description(shortDefinition = "Who the care plan is for")
        @OptionalParam(name = CarePlan.SP_SUBJECT)
        ReferenceAndListParam theSubject,

        @Description(shortDefinition = "CarePlan replaced by this CarePlan")
        @OptionalParam(name = CarePlan.SP_REPLACES)
        ReferenceAndListParam theReplaces,
        
		@Description(shortDefinition="Part of referenced CarePlan")
		@OptionalParam(name = CarePlan.SP_PART_OF, targetTypes={CarePlan.class })
        ReferenceAndListParam thePartOf, 

		@Description(shortDefinition="proposal | plan | order | option")
		@OptionalParam(name = CarePlan.SP_INTENT)
		TokenAndListParam theIntent, 

        @Description(shortDefinition = "Health issues this plan addresses")
        @OptionalParam(name = CarePlan.SP_CONDITION)
        ReferenceAndListParam theCondition,

        @Description(shortDefinition = "Fulfills CarePlan")
        @OptionalParam(name = CarePlan.SP_BASED_ON)
        ReferenceAndListParam theBasedOn,

        @Description(shortDefinition = "Who the care plan is for")
        @OptionalParam(name = CarePlan.SP_PATIENT)
        ReferenceAndListParam thePatient,
 
        @Description(shortDefinition = "Instantiates external protocol or definition")
        @OptionalParam(name = CarePlan.SP_INSTANTIATES_URI)
        UriAndListParam theInstantiatesUri,
        
        @Description(shortDefinition = "draft | active | suspended | completed | entered-in-error | cancelled | unknown")
        @OptionalParam(name = CarePlan.SP_STATUS)
        TokenAndListParam theStatus,
        
        @Description(shortDefinition="Type of plan")
		@OptionalParam(name = CarePlan.SP_CATEGORY)
		TokenAndListParam theCategory, 
        
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
		
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(CarePlan.SP_RES_ID, theId);
        paramMap.add(CarePlan.SP_IDENTIFIER, theIdentifier);
        paramMap.add(CarePlan.SP_DATE, theDate);
        paramMap.add(CarePlan.SP_CARE_TEAM, theCareTeam);
        paramMap.add(CarePlan.SP_PERFORMER, thePerformer);
        paramMap.add(CarePlan.SP_GOAL, theGoal);
        paramMap.add(CarePlan.SP_SUBJECT, theSubject);
        paramMap.add(CarePlan.SP_REPLACES, theReplaces);
        paramMap.add(CarePlan.SP_PART_OF, thePartOf);
        paramMap.add(CarePlan.SP_INTENT, theIntent);
        paramMap.add(CarePlan.SP_CONDITION, theCondition);
        paramMap.add(CarePlan.SP_BASED_ON, theBasedOn);
        paramMap.add(CarePlan.SP_PATIENT, thePatient);
        paramMap.add(CarePlan.SP_INSTANTIATES_URI, theInstantiatesUri);
        paramMap.add(CarePlan.SP_CONDITION, theCondition);
        paramMap.add(CarePlan.SP_STATUS, theStatus);
        paramMap.add(CarePlan.SP_CATEGORY, theCategory);
        
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<DafCarePlan> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
			public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
				List<IBaseResource> carePlanList = new ArrayList<IBaseResource>();
				List<String> ids = new ArrayList<String>();
				for(DafCarePlan dafCarePlan : results){
					CarePlan carePlan = createCarePlanObject(dafCarePlan);
					carePlanList.add(carePlan);
					ids.add(((IdType)carePlan.getIdElement()).getResourceType()+"/"+((IdType)carePlan.getIdElement()).getIdPart());
				}
				if(theRevIncludes.size() >0 ){
					carePlanList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
				}
				return carePlanList;
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
     * @param dafCarePlan : DafDocumentReference careplan object
     * @return : DocumentReference careplan object
     */
    private CarePlan createCarePlanObject(DafCarePlan dafCarePlan) {
    	CarePlan carePlan = new CarePlan();
        JSONObject carePlanJSON = new JSONObject(dafCarePlan.getData());

        // Set version
        if(!(carePlanJSON.isNull("meta"))) {
        	if(!(carePlanJSON.getJSONObject("meta").isNull("versionId"))) {
                carePlan.setId(new IdType(RESOURCE_TYPE, carePlanJSON.getString("id") + "", carePlanJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				carePlan.setId(new IdType(RESOURCE_TYPE, carePlanJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
            carePlan.setId(new IdType(RESOURCE_TYPE, carePlanJSON.getString("id") + "", VERSION_ID));
        }
        
        //Set text
        if(!(carePlanJSON.isNull("text"))) {
        	Narrative theText = new Narrative();
        	theText.setStatus(Narrative.NarrativeStatus.fromCode(carePlanJSON.getJSONObject("text").getString("status")));    	
        	theText.setDivAsString(carePlanJSON.getJSONObject("text").getString("div"));
        	carePlan.setText(theText);
        }

        //Set identifier
        if(!(carePlanJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = carePlanJSON.getJSONArray("identifier");
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
        	carePlan.setIdentifier(identifiers);
        }
        
        //set instantiatesCanonical
        if (!carePlanJSON.isNull("instantiatesCanonical")) {
			JSONArray  instantiatesCanonicalJSON = carePlanJSON.getJSONArray("instantiatesCanonical");
			List<CanonicalType> instantiatesCanonicalList =new ArrayList<CanonicalType>();
			int noOfCanonocals = instantiatesCanonicalJSON.length();
			
			for(int i = 0; i < noOfCanonocals; i++) {
				CanonicalType instantiatesCanonical = new CanonicalType();
				instantiatesCanonical.setValue(instantiatesCanonicalJSON.getString(i));
				instantiatesCanonicalList.add(instantiatesCanonical);
			}
			carePlan.setInstantiatesCanonical(instantiatesCanonicalList);
		}
        
        //Set instantiatesUri
        if(!(carePlanJSON.isNull("instantiatesUri"))) {
        	List<UriType> uriList = new ArrayList<UriType>();
        	JSONArray theUris = carePlanJSON.getJSONArray("instantiatesUri");
        	int noOfUris = theUris.length();
    		for(int i = 0; i < noOfUris; i++) {
    			String uriName = theUris.getString(i) ;
    			UriType uriType = new UriType();
    			uriType.setValue(uriName);
			   	uriList.add(uriType);
    		}
        	carePlan.setInstantiatesUri(uriList);
        }
        
        //Set basedOn
        if(!(carePlanJSON.isNull("basedOn"))) {
        	JSONArray basedOnJSON = carePlanJSON.getJSONArray("basedOn");
        	int noOfBasedOn = basedOnJSON.length();
        	List<Reference> basedOnList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfBasedOn; i++) {
        		Reference theBasedOn = new Reference();
        		if(!basedOnJSON.getJSONObject(i).isNull("display")) {
            		theBasedOn.setDisplay(basedOnJSON.getJSONObject(i).getString("display"));
        		}
        		if(!basedOnJSON.getJSONObject(i).isNull("type")) {
            		theBasedOn.setType(basedOnJSON.getJSONObject(i).getString("type"));
        		}
        		if(!basedOnJSON.getJSONObject(i).isNull("reference")) {
            		theBasedOn.setReference(basedOnJSON.getJSONObject(i).getString("reference"));
        		}
        		basedOnList.add(theBasedOn);
        	}
        	carePlan.setBasedOn(basedOnList);
        }
        
        //Set replaces
        if(!(carePlanJSON.isNull("replaces"))) {
        	JSONArray replacesJSON = carePlanJSON.getJSONArray("replaces");
        	int noOfReplaces = replacesJSON.length();
        	List<Reference> replacesList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfReplaces; i++) {
        		Reference theReplaces = new Reference();
        		if(!replacesJSON.getJSONObject(i).isNull("display")) {
            		theReplaces.setDisplay(replacesJSON.getJSONObject(i).getString("display"));
        		}
        		if(!replacesJSON.getJSONObject(i).isNull("type")) {
            		theReplaces.setType(replacesJSON.getJSONObject(i).getString("type"));
        		}
        		if(!replacesJSON.getJSONObject(i).isNull("reference")) {
            		theReplaces.setReference(replacesJSON.getJSONObject(i).getString("reference"));
        		}
        		replacesList.add(theReplaces);
        	}
        	carePlan.setReplaces(replacesList);
        }
        
        //Set partOf
        if(!(carePlanJSON.isNull("partOf"))) {
        	JSONArray partOfJSON = carePlanJSON.getJSONArray("partOf");
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
        	carePlan.setPartOf(partOfList);
        }
        
        //Set status
        if(!(carePlanJSON.isNull("status"))) {
        	carePlan.setStatus(CarePlan.CarePlanStatus.fromCode(carePlanJSON.getString("status")));
        }
        
        //Set intent
        if(!(carePlanJSON.isNull("intent"))) {
        	carePlan.setIntent(CarePlan.CarePlanIntent.fromCode(carePlanJSON.getString("intent")));
        }
        
        //Set category
        if(!(carePlanJSON.isNull("category"))) {
        	JSONArray categoryJSON = carePlanJSON.getJSONArray("category");
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
        	carePlan.setCategory(categoryList);
        }
        
        //Set title
        if(!(carePlanJSON.isNull("title"))) {
        	carePlan.setTitle(carePlanJSON.getString("title"));
        }
        
        //Set description
        if(!(carePlanJSON.isNull("description"))) {
        	carePlan.setDescription(carePlanJSON.getString("description"));
        }
        
        //Set subject
        if(!(carePlanJSON.isNull("subject"))) {
        	JSONObject subjectJSON = carePlanJSON.getJSONObject("subject");
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
        	carePlan.setSubject(theSubject);
        }
        
        //Set period
        if(!(carePlanJSON.isNull("period"))) {
        	Period thePeriod = new Period();
        	if(!(carePlanJSON.getJSONObject("period").isNull("start"))) {
        		Date start = CommonUtil.convertStringToDate(carePlanJSON.getJSONObject("period").getString("start"));
        		thePeriod.setStart(start);
        	}
        	if(!(carePlanJSON.getJSONObject("period").isNull("end"))) {
        		Date end = CommonUtil.convertStringToDate(carePlanJSON.getJSONObject("period").getString("end"));
        		thePeriod.setEnd(end);
        	}
        	carePlan.setPeriod(thePeriod);
        }
        
        //Set created
        if(!(carePlanJSON.isNull("created"))) {
        	Date theCreated = CommonUtil.convertStringToDate(carePlanJSON.getString("created"));
        	carePlan.setCreated(theCreated);
        }
        
        //Set author
        if(!(carePlanJSON.isNull("author"))) {
        	JSONObject authorJSON = carePlanJSON.getJSONObject("author");
        	Reference theAuthor = new Reference();
        	if(!authorJSON.isNull("reference")) {
        		theAuthor.setReference(authorJSON.getString("reference"));
        	}
        	if(!authorJSON.isNull("display")) {
        		theAuthor.setDisplay(authorJSON.getString("display"));
        	}
        	if(!authorJSON.isNull("type")) {
        		theAuthor.setType(authorJSON.getString("type"));
        	}
        	carePlan.setAuthor(theAuthor);
        }

        //Set careTeam
        if(!(carePlanJSON.isNull("careTeam"))) {
        	JSONArray careTeamJSON = carePlanJSON.getJSONArray("careTeam");
        	int noOfCareTeam = careTeamJSON.length();
        	List<Reference> careTeamList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfCareTeam; i++) {
        		Reference theCareTeam = new Reference();
        		if(!careTeamJSON.getJSONObject(i).isNull("display")) {
            		theCareTeam.setDisplay(careTeamJSON.getJSONObject(i).getString("display"));
        		}
        		if(!careTeamJSON.getJSONObject(i).isNull("type")) {
            		theCareTeam.setType(careTeamJSON.getJSONObject(i).getString("type"));
        		}
        		if(!careTeamJSON.getJSONObject(i).isNull("reference")) {
            		theCareTeam.setReference(careTeamJSON.getJSONObject(i).getString("reference"));
        		}
        		careTeamList.add(theCareTeam);
        	}
        	carePlan.setCareTeam(careTeamList);
        }
        
        //Set addresses
        if(!(carePlanJSON.isNull("addresses"))) {
        	JSONArray addressesJSON = carePlanJSON.getJSONArray("addresses");
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
        	carePlan.setAddresses(addressesList);
        }
        
        //Set goal
        if(!(carePlanJSON.isNull("goal"))) {
        	JSONArray goalJSON = carePlanJSON.getJSONArray("goal");
        	int noOfGoal = goalJSON.length();
        	List<Reference> goalList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfGoal; i++) {
        		Reference theGoal = new Reference();
        		if(!goalJSON.getJSONObject(i).isNull("display")) {
            		theGoal.setDisplay(goalJSON.getJSONObject(i).getString("display"));
        		}
        		if(!goalJSON.getJSONObject(i).isNull("type")) {
            		theGoal.setType(goalJSON.getJSONObject(i).getString("type"));
        		}
        		if(!goalJSON.getJSONObject(i).isNull("reference")) {
            		theGoal.setReference(goalJSON.getJSONObject(i).getString("reference"));
        		}
        		goalList.add(theGoal);
        	}
        	carePlan.setGoal(goalList);
        }
        
        //Set activity
        if(!(carePlanJSON.isNull("activity"))) {
        	JSONArray activityJSON = carePlanJSON.getJSONArray("activity");
        	int noOfActivity = activityJSON.length();
        	List<CarePlanActivityComponent> activityList = new ArrayList<CarePlanActivityComponent>();
        	for(int i = 0; i < noOfActivity; i++) {
        		CarePlanActivityComponent theActivity = new CarePlanActivityComponent();
        		//Set outcomeCodeableConcept
        		if(!(activityJSON.getJSONObject(i).isNull("outcomeCodeableConcept"))) {
        			JSONArray outcomeJSON = activityJSON.getJSONObject(i).getJSONArray("outcomeCodeableConcept");
        			int noOfOutcome = outcomeJSON.length(); 
        			List<CodeableConcept> outcomeList = new ArrayList<CodeableConcept>();
        			for(int j = 0; j < noOfOutcome; j++) {
        				CodeableConcept theOutcome = new CodeableConcept();
        				if(!(outcomeJSON.getJSONObject(j).isNull("coding"))) {
        					JSONArray codingJSON = outcomeJSON.getJSONObject(j).getJSONArray("coding");
        					int noOfCodings = codingJSON.length();
        					List<Coding> codingList = new ArrayList<Coding>();
        					for(int k = 0; k < noOfCodings;k++) {
        						Coding theCoding = new Coding();
        						if(!(codingJSON.getJSONObject(k).isNull("system"))) {
        							theCoding.setSystem(codingJSON.getJSONObject(k).getString("system"));
        						}
        						if(!(codingJSON.getJSONObject(k).isNull("code"))) {
        							theCoding.setCode(codingJSON.getJSONObject(k).getString("code"));
        						}
        						if(!(codingJSON.getJSONObject(k).isNull("display"))) {
        							theCoding.setDisplay(codingJSON.getJSONObject(k).getString("display"));
        						}
        						codingList.add(theCoding);
        					}
        					theOutcome.setCoding(codingList);
        				}
        				if(!(outcomeJSON.getJSONObject(j).isNull("text"))) {
        					theOutcome.setText(outcomeJSON.getJSONObject(j).getString("text"));
        				}
        				outcomeList.add(theOutcome);
        			}
        			theActivity.setOutcomeCodeableConcept(outcomeList);
        		}
        		
        		//Set progress
        		if(!(activityJSON.getJSONObject(i).isNull("progress"))) {
        			JSONArray progressJSON = activityJSON.getJSONObject(i).getJSONArray("progress");
        			int noOfProgresses = progressJSON.length();
        			List<Annotation> progressList = new ArrayList<Annotation>();
        			for(int a = 0; a < noOfProgresses; a++) {
        				Annotation theProgress = new Annotation();
        				if(!(progressJSON.getJSONObject(a).isNull("time"))) {
        	        		Date progressTime = CommonUtil.convertStringToDate(progressJSON.getJSONObject(a).getString("time"));
        					theProgress.setTime(progressTime);
        				}
        				if(!(progressJSON.getJSONObject(a).isNull("text"))) {
        					theProgress.setText(progressJSON.getJSONObject(a).getString("text"));
        				}
        				progressList.add(theProgress);
        			}
        			theActivity.setProgress(progressList);
        		}
        		
        		//Set outcomeReference
        		if(!(activityJSON.getJSONObject(i).isNull("outcomeReference"))) {
        			JSONArray outcomeRefJSON = activityJSON.getJSONObject(i).getJSONArray("outcomeReference");
        			int noOfOutcomeRef = outcomeRefJSON.length();
        			List<Reference> outcomeReferenceList = new ArrayList<Reference>();
        			for(int m = 0; m < noOfOutcomeRef; m++) {
        				Reference theOutcomeReference = new Reference();
        				if(!(outcomeRefJSON.getJSONObject(m).isNull("reference"))) {
        					theOutcomeReference.setReference(outcomeRefJSON.getJSONObject(m).getString("reference"));
        				}
        				if(!(outcomeRefJSON.getJSONObject(m).isNull("type"))) {
        					theOutcomeReference.setType(outcomeRefJSON.getJSONObject(m).getString("type"));
        				}
        				if(!(outcomeRefJSON.getJSONObject(m).isNull("display"))) {
        					theOutcomeReference.setType(outcomeRefJSON.getJSONObject(m).getString("display"));
        				}
        				outcomeReferenceList.add(theOutcomeReference);
        			} 
        			theActivity.setOutcomeReference(outcomeReferenceList);
        		}
        		
        		//Set detail
        		if(!(activityJSON.getJSONObject(i).isNull("detail"))) {
        			JSONObject detailJSON = activityJSON.getJSONObject(i).getJSONObject("detail");
        			CarePlanActivityDetailComponent theDetail = new CarePlanActivityDetailComponent();
        			
        			//Set kind
        	        if(!(detailJSON.isNull("kind"))) {
        	        	theDetail.setKind(CarePlan.CarePlanActivityKind.fromCode(detailJSON.getString("kind")));
        	        }
        	        
        			//Set code
        			if(!(detailJSON.isNull("code"))) {
        				JSONObject detailCodeJSON = detailJSON.getJSONObject("code");
        				CodeableConcept theDetailCode = new CodeableConcept();
        				if(!(detailCodeJSON.isNull("coding"))) {
        					JSONArray detailCodingJSON = detailCodeJSON.getJSONArray("coding");
        					int noOfDetailCodings = detailCodingJSON.length(); 
        					List<Coding> detailCodingList = new ArrayList<Coding>();
        					for(int n = 0; n < noOfDetailCodings; n++) {
        						Coding theDetailCoding = new Coding();
        						if(!(detailCodingJSON.getJSONObject(n).isNull("system"))) {
        							theDetailCoding.setSystem(detailCodingJSON.getJSONObject(n).getString("system"));
        						}
        						if(!(detailCodingJSON.getJSONObject(n).isNull("code"))) {
        							theDetailCoding.setCode(detailCodingJSON.getJSONObject(n).getString("code"));
        						}
        						if(!(detailCodingJSON.getJSONObject(n).isNull("display"))) {
        							theDetailCoding.setDisplay(detailCodingJSON.getJSONObject(n).getString("display"));
        						}
        						detailCodingList.add(theDetailCoding);
        					}
        					theDetailCode.setCoding(detailCodingList);
        				}
        				if(!(detailCodeJSON.isNull("text"))) {
        					theDetailCode.setText(detailCodeJSON.getString("text"));
        				}
        				theDetail.setCode(theDetailCode);
        			}
        			
        			// set reasonCode
        	 		if (!detailJSON.isNull("reasonCode")) {
        	 			JSONArray categoryJSON = detailJSON.getJSONArray("reasonCode");
        	 			List<CodeableConcept> reasonCodeList = new ArrayList<CodeableConcept>();
        	 			int noOfCategories = categoryJSON.length();
        	 			for (int r = 0; r < noOfCategories; r++) {
        	 				CodeableConcept theReasonCode = new CodeableConcept();
        	 				if (!categoryJSON.getJSONObject(r).isNull("coding")) {
        	 					JSONArray codingJSON = categoryJSON.getJSONObject(r).getJSONArray("coding");
        	 					List<Coding> codingList = new ArrayList<Coding>();
        	 					for (int j = 0; j < codingJSON.length(); j++) {
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
        	 					theReasonCode.setCoding(codingList);
        	 				}

        	 				if (!categoryJSON.getJSONObject(r).isNull("text")) {
        	 					theReasonCode.setText(categoryJSON.getJSONObject(r).getString("text"));
        	 				}
        	 				reasonCodeList.add(theReasonCode);
        	 			}
        	 			carePlan.setCategory(reasonCodeList);
        	 		}
        	 		
        			//Set goal
        	        if(!(detailJSON.isNull("goal"))) {
        	        	JSONArray dtGoalJSON = carePlanJSON.getJSONArray("goal");
        	        	int noOfDtGoal = dtGoalJSON.length();
        	        	List<Reference> dtGoalList = new ArrayList<Reference>();
        	        	for(int g = 0; g < noOfDtGoal; g++) {
        	        		Reference theDtGoal = new Reference();
        	        		if(!dtGoalJSON.getJSONObject(g).isNull("display")) {
        	            		theDtGoal.setDisplay(dtGoalJSON.getJSONObject(g).getString("display"));
        	        		}
        	        		if(!dtGoalJSON.getJSONObject(g).isNull("type")) {
        	            		theDtGoal.setType(dtGoalJSON.getJSONObject(g).getString("type"));
        	        		}
        	        		if(!dtGoalJSON.getJSONObject(g).isNull("reference")) {
        	            		theDtGoal.setReference(dtGoalJSON.getJSONObject(g).getString("reference"));
        	        		}
        	        		dtGoalList.add(theDtGoal);
        	        	}
        	        	theDetail.setGoal(dtGoalList);
        	        }
        	        
        			//Set status
        			if(!(detailJSON.isNull("status"))) {
        				String detailStatus = detailJSON.getString("status");
        				theDetail.setStatus(CarePlan.CarePlanActivityStatus.fromCode(detailStatus));
        			}
        			
        			//Set statusReason
        			if(!(detailJSON.isNull("statusReason"))) {
        				JSONObject statusReasonJSON = detailJSON.getJSONObject("statusReason");
        				CodeableConcept theStatusReason = new CodeableConcept();
        				if(!(statusReasonJSON.isNull("coding"))) {
        					JSONArray codingJSON = statusReasonJSON.getJSONArray("coding");
        					int noOfCodings = codingJSON.length();
        					List<Coding> codingList = new ArrayList<Coding>();
        					for(int p = 0; p < noOfCodings; p++) {
        						Coding theCoding = new Coding();
        						if(!(codingJSON.getJSONObject(p).isNull("system"))) {
        							theCoding.setSystem(codingJSON.getJSONObject(p).getString("system"));
        						}
        						if(!(codingJSON.getJSONObject(p).isNull("code"))) {
        							theCoding.setCode(codingJSON.getJSONObject(p).getString("code"));
        						}
        						if(!(codingJSON.getJSONObject(p).isNull("display"))) {
        							theCoding.setDisplay(codingJSON.getJSONObject(p).getString("display"));
        						}
        						codingList.add(theCoding);
        					}
        					theStatusReason.setCoding(codingList);
        				}
        				if(!(statusReasonJSON.isNull("text"))) {
        					theStatusReason.setText(statusReasonJSON.getString("text"));
        				}
        				theDetail.setStatusReason(theStatusReason);
        			}
        			
        			//Set doNotPerform
        			if(!(detailJSON.isNull("doNotPerform"))) {
        				theDetail.setDoNotPerform(detailJSON.getBoolean("doNotPerform"));
        			}
        			
        			//Set scheduledPeriod
        			if(!(detailJSON.isNull("scheduledPeriod"))) {
        				Period theScheduledPeriod = new Period();
        				if(!(detailJSON.getJSONObject("scheduledPeriod").isNull("start"))) {
        	        		Date scheduledPeriodStart = CommonUtil.convertStringToDate(detailJSON.getJSONObject("scheduledPeriod").getString("start"));
        	        		theScheduledPeriod.setStart(scheduledPeriodStart);
        				}
        				if(!(detailJSON.getJSONObject("scheduledPeriod").isNull("end"))) {
        	        		Date scheduledPeriodEnd = CommonUtil.convertStringToDate(detailJSON.getJSONObject("scheduledPeriod").getString("end"));
        	        		theScheduledPeriod.setEnd(scheduledPeriodEnd);
        				}
        				theDetail.setScheduled(theScheduledPeriod);
        			}
        			
        			//Set productReference
          			if(!(detailJSON.isNull("productReference"))) {
        				Reference theProductReference = new Reference();
        				if(!(detailJSON.getJSONObject("productReference").isNull("display"))) {
        					theProductReference.setDisplay(detailJSON.getJSONObject("productReference").getString("display"));
        				}
        				if(!(detailJSON.getJSONObject("productReference").isNull("reference"))) {
        					theProductReference.setReference(detailJSON.getJSONObject("productReference").getString("reference"));
        				}
        				if(!(detailJSON.getJSONObject("productReference").isNull("type"))) {
        					theProductReference.setType(detailJSON.getJSONObject("productReference").getString("type"));
        				}
        				theDetail.setProduct(theProductReference);
        			}
          			
          			//Set productCodeableConcept
        			if(!(detailJSON.isNull("productCodeableConcept"))) {
        				JSONObject productCodeableConceptJSON = detailJSON.getJSONObject("productCodeableConcept");
        				CodeableConcept theProductCodeableConcept = new CodeableConcept();
        				if(!(productCodeableConceptJSON.isNull("coding"))) {
        					JSONArray codingJSON = productCodeableConceptJSON.getJSONArray("coding");
        					int noOfCodings = codingJSON.length();
        					List<Coding> codingList = new ArrayList<Coding>();
        					for(int p = 0; p < noOfCodings; p++) {
        						Coding theCoding = new Coding();
        						if(!(codingJSON.getJSONObject(p).isNull("system"))) {
        							theCoding.setSystem(codingJSON.getJSONObject(p).getString("system"));
        						}
        						if(!(codingJSON.getJSONObject(p).isNull("code"))) {
        							theCoding.setCode(codingJSON.getJSONObject(p).getString("code"));
        						}
        						if(!(codingJSON.getJSONObject(p).isNull("display"))) {
        							theCoding.setDisplay(codingJSON.getJSONObject(p).getString("display"));
        						}
        						codingList.add(theCoding);
        					}
        					theProductCodeableConcept.setCoding(codingList);
        				}
        				if(!(productCodeableConceptJSON.isNull("text"))) {
        					theProductCodeableConcept.setText(productCodeableConceptJSON.getString("text"));
        				}
        				theDetail.setProduct(theProductCodeableConcept);
        			}
          			
          			//Set dailyAmount
          			if(!(detailJSON.isNull("dailyAmount"))) {
          				JSONObject dailyAmtJSON = detailJSON.getJSONObject("dailyAmount");
          				SimpleQuantity theDailyAmt = new SimpleQuantity();
          				if(!(dailyAmtJSON.isNull("value"))) {
          					theDailyAmt.setValue(dailyAmtJSON.getLong("value"));
          				}
          				if(!(dailyAmtJSON.isNull("code"))) {
          					theDailyAmt.setCode(dailyAmtJSON.getString("code"));
          				}
          				if(!(dailyAmtJSON.isNull("unit"))) {
          					theDailyAmt.setUnit(dailyAmtJSON.getString("unit"));
          				}
          				if(!(dailyAmtJSON.isNull("system"))) {
          					theDailyAmt.setSystem(dailyAmtJSON.getString("system"));
          				}
          				theDetail.setDailyAmount(theDailyAmt);
          			}
          			
        			//Set description
        	        if(!(detailJSON.isNull("description"))) {
        	        	theDetail.setDescription(detailJSON.getString("description"));
        	        }
        	        
        			//Set location
        			if(!(detailJSON.isNull("location"))) {
        				Reference theLocation = new Reference();
        				if(!(detailJSON.getJSONObject("location").isNull("display"))) {
        					theLocation.setDisplay(detailJSON.getJSONObject("location").getString("display"));
        				}
        				if(!(detailJSON.getJSONObject("location").isNull("reference"))) {
        					theLocation.setReference(detailJSON.getJSONObject("location").getString("reference"));
        				}
        				if(!(detailJSON.getJSONObject("location").isNull("type"))) {
        					theLocation.setType(detailJSON.getJSONObject("location").getString("type"));
        				}
        				theDetail.setLocation(theLocation);
        			}
        			
        			//Set performer
        			if(!(detailJSON.isNull("performer"))) {
        				JSONArray detailPerformerJSON = detailJSON.getJSONArray("performer");
        				int noOfDtPerformers = detailPerformerJSON.length();
        				List<Reference> dtPerformerList = new ArrayList<Reference>();
        				for(int z = 0; z < noOfDtPerformers; z++) {
        					Reference theDtPerformer = new Reference();
        					if(!(detailPerformerJSON.getJSONObject(z).isNull("reference"))) {
        						theDtPerformer.setReference(detailPerformerJSON.getJSONObject(z).getString("reference"));
        					}
        					if(!(detailPerformerJSON.getJSONObject(z).isNull("display"))) {
        						theDtPerformer.setDisplay(detailPerformerJSON.getJSONObject(z).getString("display"));
        					}
        					if(!(detailPerformerJSON.getJSONObject(z).isNull("type"))) {
        						theDtPerformer.setType(detailPerformerJSON.getJSONObject(z).getString("type"));
        					}
        					dtPerformerList.add(theDtPerformer);
        				}
        				theDetail.setPerformer(dtPerformerList);
        			}
        			theActivity.setDetail(theDetail);
        		}
        		activityList.add(theActivity);
        	}
        	carePlan.setActivity(activityList);
        }
        
        //Set note
        if(!(carePlanJSON.isNull("note"))) {
        	JSONArray noteJSON = carePlanJSON.getJSONArray("note");
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
        	carePlan.setNote(noteList);
        }
        return carePlan;
    }
}
