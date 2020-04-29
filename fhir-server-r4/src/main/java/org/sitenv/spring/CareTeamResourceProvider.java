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
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.CareTeam.CareTeamParticipantComponent;
import org.hl7.fhir.r4.model.CareTeam.CareTeamStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.service.CareTeamService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CareTeamResourceProvider implements IResourceProvider {
	
	public static final String RESOURCE_TYPE = "CareTeam";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    CareTeamService service;
  
    
    public CareTeamResourceProvider() {
	    context = new AnnotationConfigApplicationContext(AppConfig.class);
	    service = (CareTeamService) context.getBean("careTeamService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return CareTeam.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/CareTeam/1/_history/4
	 * @param theId : Id of the careTeam
	 * @return : Object of careTeam information
	 */	
	@Read(version=true)
    public CareTeam readOrVread(@IdParam IdType theId) {
		String id;
		DafCareTeam dafCareTeam;
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
			dafCareTeam = service.getCareTeamByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       dafCareTeam = service.getCareTeamById(id);
		}
		return createCareTeamObject(dafCareTeam);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=CareTeam.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/CareTeam/1/_history
	 * @param theId : ID of the CareTeam
	 * @return : List of CareTeam's
	 */
	@History()
    public List<CareTeam> getCareTeamHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafCareTeam> dafCareTeamList = service.getCareTeamHistoryById(id);
        
        List<CareTeam> careTeamList = new ArrayList<CareTeam>();
        for (DafCareTeam dafCareTeam : dafCareTeamList) {
        	careTeamList.add(createCareTeamObject(dafCareTeam));
        }
        
        return careTeamList;
	}
	

	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theDate
	 * @param theIdentifier
	 * @param thePatient
	 * @param theSubject
	 * @param theEncounter
	 * @param theCategory
	 * @param theParticipant
	 * @param theStatus
	 * @param theIncludes
	 * @param theRevIncludes
	 * @return
	 */
	@Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "Time period team covers")
        @OptionalParam(name = CareTeam.SP_DATE)
        DateAndListParam theDate,

        @Description(shortDefinition = "The ID of the resource")
        @OptionalParam(name = CareTeam.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "External Ids for this team")
        @OptionalParam(name = CareTeam.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "Who care team is for")
        @OptionalParam(name = CareTeam.SP_SUBJECT)
        StringAndListParam theSubject,
        
        @Description(shortDefinition = "Who care team is for")
        @OptionalParam(name = CareTeam.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "Encounter or episode associated with CareTeam")
        @OptionalParam(name = CareTeam.SP_ENCOUNTER)
        StringAndListParam theEncounter,

        @Description(shortDefinition = "Type of team")
        @OptionalParam(name = CareTeam.SP_CATEGORY)
        StringAndListParam theCategory,

        @Description(shortDefinition = "Who is involved")
        @OptionalParam(name = CareTeam.SP_PARTICIPANT)
        StringAndListParam theParticipant,
        
        @Description(shortDefinition="proposed | active | suspended | inactive | entered-in-error")
		@OptionalParam(name = CareTeam.SP_STATUS)
		TokenAndListParam theStatus, 

        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,

		@Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
		
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(CareTeam.SP_DATE, theDate);
        paramMap.add(CareTeam.SP_RES_ID, theId);
        paramMap.add(CareTeam.SP_IDENTIFIER, theIdentifier);
        paramMap.add(CareTeam.SP_SUBJECT, theSubject);
        paramMap.add(CareTeam.SP_PATIENT, thePatient);
        paramMap.add(CareTeam.SP_ENCOUNTER, theEncounter);
        paramMap.add(CareTeam.SP_CATEGORY, theCategory);
        paramMap.add(CareTeam.SP_PARTICIPANT, theParticipant);
        paramMap.add(CareTeam.SP_STATUS, theStatus);
        	            
        final List<DafCareTeam> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> careTeamList = new ArrayList<IBaseResource>();
				List<String> ids = new ArrayList<String>();
				for(DafCareTeam dafCareTeam : results){
					CareTeam careTeam = createCareTeamObject(dafCareTeam);
					careTeamList.add(careTeam);
					ids.add(((IdType)careTeam.getIdElement()).getResourceType()+"/"+((IdType)careTeam.getIdElement()).getIdPart());
                }
				if(theRevIncludes.size() >0 ){
					careTeamList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
				}
				return careTeamList;
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
	  * @param dafCareTeam : DafDocumentReference careTeam object
	  * @return : DocumentReference CareTeam object
	  */
	private CareTeam createCareTeamObject(DafCareTeam dafCareTeam) {
		CareTeam careTeam=new CareTeam();
		JSONObject careTeamJSONObj = new JSONObject(dafCareTeam.getData());
		
		// Set version
	    if(!careTeamJSONObj.isNull("meta")) {
	    	if(!(careTeamJSONObj.getJSONObject("meta").isNull("versionId"))) {
	    		careTeam.setId(new IdType(RESOURCE_TYPE, careTeamJSONObj.getString("id") + "", careTeamJSONObj.getJSONObject("meta").getString("versionId")));
	    	}else {
				careTeam.setId(new IdType(RESOURCE_TYPE, careTeamJSONObj.getString("id") + "", VERSION_ID));
			}
	    }
	    else {
	    	careTeam.setId(new IdType(RESOURCE_TYPE, careTeamJSONObj.getString("id") + "", VERSION_ID));
	    }
	    
	   //set identifier
	    if(!careTeamJSONObj.isNull("identifier")) {
	    	JSONArray identifierJSON=careTeamJSONObj.getJSONArray("identifier");
	    	int noOfIdentifiers=identifierJSON.length();
	    	List<Identifier> identifiers=new ArrayList<Identifier>();		    	
	    	for(int i=0;i<noOfIdentifiers;i++) {
		    	Identifier theIdentifier=new Identifier();
	    		if(!(identifierJSON.getJSONObject(i).isNull("value"))) {
	            	theIdentifier.setValue(identifierJSON.getJSONObject(i).getString("value"));
	        	}
	    		if(!(identifierJSON.getJSONObject(i).isNull("use"))) {
                	theIdentifier.setUse(Identifier.IdentifierUse.fromCode(identifierJSON.getJSONObject(i).getString("use")));	
            	}
        		
        		if(!(identifierJSON.getJSONObject(i).isNull("system"))) {
                	theIdentifier.setSystem(identifierJSON.getJSONObject(i).getString("system"));
            	}
        		if(!(identifierJSON.getJSONObject(i).isNull("period"))) {
            		Period identifierPeriod = new Period();
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("period").isNull("start"))) {
                        Date identifierSDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject(i).getJSONObject("period").getString("start"));
                        identifierPeriod.setStart(identifierSDate);
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("period").isNull("end"))) {
                        Date identifierSDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject(i).getJSONObject("period").getString("end"));
                        identifierPeriod.setEnd(identifierSDate);
            		}
                    theIdentifier.setPeriod(identifierPeriod);
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
	    	careTeam.setIdentifier(identifiers);
	    }
	  	    
	    //set active
	    if(!careTeamJSONObj.isNull("status")) {
	    	careTeam.setStatus(CareTeamStatus.fromCode(careTeamJSONObj.getString("status")));
	    }
	    
	    //set name
	    if(!careTeamJSONObj.isNull("name")) {
	    	careTeam.setName(careTeamJSONObj.getString("name"));
	    }
		
	    //set encounter
	    if(!careTeamJSONObj.isNull("encounter")) {
	    	Reference  theEncounter = new Reference();	    	
	    	if(!(careTeamJSONObj.getJSONObject("encounter").isNull("reference"))) {
	    		theEncounter.setReference(careTeamJSONObj.getJSONObject("encounter").getString("reference"));    		
	    	}
	    	if(!(careTeamJSONObj.getJSONObject("encounter").isNull("display"))) {
	    		theEncounter.setDisplay(careTeamJSONObj.getJSONObject("encounter").getString("display"));    		
	    	}
	    	if(!(careTeamJSONObj.getJSONObject("encounter").isNull("type"))) {
	    		theEncounter.setType(careTeamJSONObj.getJSONObject("encounter").getString("type"));    		
	    	}
	    	careTeam.setEncounter(theEncounter);    
	    }
		
	    //set period
	    if(!careTeamJSONObj.isNull("period")) {
    		Period thePeriod=new Period();
    		if(!(careTeamJSONObj.getJSONObject("period").isNull("start"))) {
	    		Date careteamDate=CommonUtil.convertStringToDate(careTeamJSONObj.getJSONObject("period").getString("start"));
	    		thePeriod.setStart(careteamDate);  
	    	}  
	    	if(!(careTeamJSONObj.getJSONObject("period").isNull("end"))) {
	    		Date careteamDate=CommonUtil.convertStringToDate(careTeamJSONObj.getJSONObject("period").getString("end"));
	    		thePeriod.setEnd(careteamDate);  
	    	}   
	    	careTeam.setPeriod(thePeriod);
	    }
	    
	    //set category
	    if(!careTeamJSONObj.isNull("category")) {
	    	JSONArray categoryJSON=careTeamJSONObj.getJSONArray("category");
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
				if(!(categoryJSON.getJSONObject(c).isNull("text"))) {
					theCategory.setText(categoryJSON.getJSONObject(c).getString("text"));
    			}
				categoryList.add(theCategory);
			}
			careTeam.setCategory(categoryList);
		}
   
	    //set subject
	    if(!careTeamJSONObj.isNull("subject")) {
	    	Reference  theSubject = new Reference();	    	
	    	if(!(careTeamJSONObj.getJSONObject("subject").isNull("reference"))) {
	    		theSubject.setReference(careTeamJSONObj.getJSONObject("subject").getString("reference"));    		
	    	}
	    	
	    	if(!(careTeamJSONObj.getJSONObject("subject").isNull("display"))) {
	    		theSubject.setDisplay(careTeamJSONObj.getJSONObject("subject").getString("display"));    		
	    	}
	    	
	    	if(!(careTeamJSONObj.getJSONObject("subject").isNull("type"))) {
	    		theSubject.setType(careTeamJSONObj.getJSONObject("subject").getString("type"));    		
	    	}
	    	 careTeam.setSubject(theSubject);    
	    }
	    
	    //set participant
	    if(!careTeamJSONObj.isNull("participant")){
	    	JSONArray participantJSON=careTeamJSONObj.getJSONArray("participant");
	    	int noOfparticipants=participantJSON.length();
	    	List<CareTeamParticipantComponent> participantList = new ArrayList<CareTeamParticipantComponent>();
	    	for(int i = 0; i < noOfparticipants; i++) {
	    		CareTeamParticipantComponent participantComponent = new CareTeamParticipantComponent();
	    		if (!(participantJSON.getJSONObject(i).isNull("role"))) {
					JSONArray participantRoleJSON = participantJSON.getJSONObject(i).getJSONArray("role");
					int noOfparticipantRole = participantRoleJSON.length();
					List<CodeableConcept> roleList = new ArrayList<CodeableConcept>();		
					for(int j = 0; j < noOfparticipantRole; j++) {
						CodeableConcept participantRole = new CodeableConcept();
  						if(!(participantRoleJSON.getJSONObject(j).isNull("text"))) {
  							participantRole.setText(participantRoleJSON.getJSONObject(j).getString("text"));
  						}
						roleList.add(participantRole);
    	            }
					participantComponent.setRole(roleList);
	    		}  		
	    		if (!(participantJSON.getJSONObject(i).isNull("member"))) {    
	    			JSONObject memberJSON=participantJSON.getJSONObject(i).getJSONObject("member");
	    			Reference  theMember = new Reference(); 	
	    	    	if(!(memberJSON.isNull("reference"))){
	    	    		theMember.setReference(memberJSON.getString("reference"));    		
	    	    	}
	    	    	if(!(memberJSON.isNull("display"))) {
	    	    		theMember.setDisplay(memberJSON.getString("display"));    		
	    	    	}
	    	    	if(!(memberJSON.isNull("type"))) {
	    	    		theMember.setType(memberJSON.getString("type"));    		
	    	    	}
	    	    	participantComponent.setMember(theMember);  
	    		}
	    		
	
	    		if (!(participantJSON.getJSONObject(i).isNull("onBehalfOf"))) {
	    			JSONObject onBehalfOfJSON = participantJSON.getJSONObject(i).getJSONObject("onBehalfOf");
	    			Reference theOnBehalfOf = new Reference();
	    	    	
	    	    	if(!(onBehalfOfJSON.isNull("reference"))) {
	    	    		theOnBehalfOf.setReference(onBehalfOfJSON.getString("reference"));    		
	    	    	}
	    	    	if(!(onBehalfOfJSON.isNull("display"))) {
	    	    		theOnBehalfOf.setDisplay(onBehalfOfJSON.getString("display"));    		
	    	    	}
	    	    	participantComponent.setOnBehalfOf(theOnBehalfOf);
	    		}
	    		
	    		 if(!(participantJSON.getJSONObject(i).isNull("period"))) {
	    			JSONObject periodJSON = participantJSON.getJSONObject(i).getJSONObject("period");
	     	    	Period participantPeriod=new Period();
	     	    	if(!(periodJSON.isNull("end"))) {
	     	    		Date participantDate=CommonUtil.convertStringToDate(periodJSON.getString("end"));
	     	    		participantPeriod.setEnd(participantDate);  
	     	    	}    
	     	    	participantComponent.setPeriod(participantPeriod);
	     	    }
	    		participantList.add(participantComponent);
	    	}    	
	    	careTeam.setParticipant(participantList);	
	    }
	
	    //set managingOrganisation
	    if(!careTeamJSONObj.isNull("managingOrganization")) {
	    	JSONArray managingJSON=careTeamJSONObj.getJSONArray("managingOrganization");
	    	int noOfManagings=managingJSON.length();
	    	List<Reference> managingOrganizationList = new ArrayList<>();
	    	for(int m = 0 ; m < noOfManagings ; m++) {
		    	Reference  theManagingOrganization = new Reference();
	    		if(!(managingJSON.getJSONObject(m).isNull("reference"))) {
	    			theManagingOrganization.setReference(managingJSON.getJSONObject(m).getString("reference"));
	    		}
	    		managingOrganizationList.add(theManagingOrganization);
	    	}
	    	careTeam.setManagingOrganization(managingOrganizationList);
	  	}	    
	    
	   //set reasonCode
	   if(!careTeamJSONObj.isNull("reasonCode")) {
	    	JSONArray reasonCodeJSON=careTeamJSONObj.getJSONArray("reasonCode");
	    	int noOfreasonCode=reasonCodeJSON.length();
	    	List<CodeableConcept> reasonCodeList = new ArrayList<CodeableConcept>();					
			for( int c = 0; c < noOfreasonCode ; c++) {
				CodeableConcept theReasonCode = new CodeableConcept();
				if (!(reasonCodeJSON.getJSONObject(c).isNull("coding"))) {
					JSONArray codingJSON = reasonCodeJSON.getJSONObject(c).getJSONArray("coding");;
					int noOfCodings = codingJSON.length();
					List<Coding> codingList = new ArrayList<Coding>();					
					for(int j = 0; j < noOfCodings; j++) {
						Coding reasonCoding = new Coding();
						
						if(!(codingJSON.getJSONObject(j).isNull("system"))) {
							reasonCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
						}
						if(!(codingJSON.getJSONObject(j).isNull("display"))) {
							reasonCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
						}
						if(!(codingJSON.getJSONObject(j).isNull("code"))) {
							reasonCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
						}
						codingList.add(reasonCoding);
					}
	  				theReasonCode.setCoding(codingList);
	  			}
				reasonCodeList.add(theReasonCode);
			}
			careTeam.setReasonCode(reasonCodeList);
	    }
		return careTeam;
	}
}













