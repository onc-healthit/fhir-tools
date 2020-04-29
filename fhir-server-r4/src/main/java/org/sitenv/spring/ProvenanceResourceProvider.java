package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Provenance.ProvenanceAgentComponent;
import org.hl7.fhir.r4.model.Provenance.ProvenanceEntityComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafProvenance;
import org.sitenv.spring.service.ProvenanceService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ProvenanceResourceProvider implements IResourceProvider {
	
	public static final String RESOURCE_TYPE = "Provenance";
	public static final String VERSION_ID = "1.0";
	AbstractApplicationContext context;
	ProvenanceService service;
	
	public ProvenanceResourceProvider() {
		context=new AnnotationConfigApplicationContext(AppConfig.class);
		service=(ProvenanceService) context.getBean("provenanceService");
	}
	
	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		
		return Provenance.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Provenance/1/_history/4
	 * 
	 * @param theId : Id of the Provenance
	 * @return : Object of Provenance information
	 */
	@Read(version = true)
	public Provenance readOrVread(@IdParam IdType theId) {
		String id;
		DafProvenance dafProvenance;
		try {
			id = theId.getIdPart();
		} catch (NumberFormatException e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown
			 * resource
			 */
			throw new ResourceNotFoundException(theId);
		}
		if (theId.hasVersionIdPart()) {
			// this is a vread
			dafProvenance = service.getProvenanceByVersionId(id, theId.getVersionIdPart());

		} else {

			dafProvenance = service.getProvenanceById(id);
		}

		return createProvenanceObject(dafProvenance);
	}
	
	
	/**
	 * The history operation retrieves a historical collection of all versions of a
	 * single resource (instance history). History methods must be annotated with
	 * the @History annotation.It supports Instance History method.
	 * "type=Condition.class". Instance level (history of a specific resource instance
	 * by type and ID) The method must have a parameter annotated with the @IdParam
	 * annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server
	 * name>/<context>/fhir/Provenance/1/_history
	 * 
	 * @param theId : ID of the Provenance
	 * @return : List of Provenance's
	 */
	@History()
	public List<Provenance> getProvenanceHistoryById(@IdParam IdType theId) {

		String id;
		try {
			id = theId.getIdPart();
		} catch (NumberFormatException e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown
			 * resource
			 */
			throw new ResourceNotFoundException(theId);
		}

		List<DafProvenance> dafProvenanceList = service.getProvenanceHistoryById(id);
		List<Provenance> provenanceList = new ArrayList<Provenance>();
		for (DafProvenance dafProvenance : dafProvenanceList) {
			provenanceList.add(createProvenanceObject(dafProvenance));
		}
		return provenanceList;
	}


	public List<Provenance> getProvenanceByResourceId(List<String> resourceID) {

		List<DafProvenance> results = service.getProvenanceByResourceId(resourceID);
		List<Provenance> provenanceList = new ArrayList<Provenance>();

		for (DafProvenance dafProvenance : results) {
			provenanceList.add(createProvenanceObject(dafProvenance));
		}
		return provenanceList;
	}


	@Search()
	public IBundleProvider search(
			javax.servlet.http.HttpServletRequest theServletRequest,
			
			@Description(shortDefinition = "The resource identity")
	        @OptionalParam(name = Provenance.SP_RES_ID) 
	        StringAndListParam theId,
	         
	         @Description(shortDefinition = "Target Reference(s) (usually version specific)")
			 @OptionalParam(name = Provenance.SP_TARGET)
			 ReferenceAndListParam theTarget,
			 
			 @Description(shortDefinition = "Who participated")
			 @OptionalParam(name = Provenance.SP_AGENT)
			 ReferenceAndListParam theAgent,
			 
			 @Description(shortDefinition = "How the agent participated")
			 @OptionalParam(name = Provenance.SP_AGENT_TYPE)
			 ReferenceAndListParam theAgentType,
			 
			 @Description(shortDefinition = "What the agents role was")
			 @OptionalParam(name = Provenance.SP_AGENT_ROLE)
			 ReferenceAndListParam theAgentRole,
			 
			 @Description(shortDefinition = "When the activity was recorded / updated")
			 @OptionalParam(name = Provenance.SP_RECORDED)
			 ReferenceAndListParam theRecorded,
			 
			 @Description(shortDefinition = "Where the activity occurred, if relevant")
			 @OptionalParam(name = Provenance.SP_LOCATION)
			 ReferenceAndListParam theLocation,
			 
			 @Description(shortDefinition = "Indication of the reason the entity signed the object(s)")
			 @OptionalParam(name = Provenance.SP_SIGNATURE_TYPE)
			 ReferenceAndListParam theSignatureType,
			 
			 @Description(shortDefinition = "Identity of entity")
			 @OptionalParam(name = Provenance.SP_ENTITY)
			 ReferenceAndListParam theEntity,
			 
			 @Description(shortDefinition = "Target Reference(s) (usually version specific)")
			 @OptionalParam(name = Provenance.SP_PATIENT)
			 ReferenceAndListParam thePatient,
			 
			@Sort SortSpec theSort,
			
			@Count Integer theCount
			
	         
	         ) {

		
		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(Provenance.SP_RES_ID, theId);
		paramMap.add(Provenance.SP_TARGET, theTarget);
		paramMap.add(Provenance.SP_AGENT, theAgent);
		paramMap.add(Provenance.SP_AGENT_TYPE, theAgentType);
		paramMap.add(Provenance.SP_AGENT_ROLE, theAgentRole);
		paramMap.add(Provenance.SP_RECORDED, theRecorded);
		paramMap.add(Provenance.SP_LOCATION, theLocation);
		paramMap.add(Provenance.SP_SIGNATURE_TYPE, theSignatureType);
		paramMap.add(Provenance.SP_ENTITY, theEntity);
		paramMap.add(Provenance.SP_PATIENT, thePatient);
		paramMap.setSort(theSort);
		paramMap.setCount(theCount);

        final List<DafProvenance> results = service.search(paramMap);
		return new IBundleProvider() {
			final InstantDt published = InstantDt.withCurrentTime();

			@Override
			public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
				List<IBaseResource> provenanceList = new ArrayList<IBaseResource>();
				for (DafProvenance dafProvenance : results) {
					provenanceList.add(createProvenanceObject(dafProvenance));
				}
				return provenanceList;
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
				// TODO Auto-generated method stub
				return null;
			}
		};
	
		
	}
	
	
	private Provenance createProvenanceObject(DafProvenance dafProvenance) {

		Provenance provenance = new Provenance();
		JSONObject provenanceJSON = new JSONObject(dafProvenance.getData());

		// Set version
        if(!(provenanceJSON.isNull("meta"))) {
        	if(!(provenanceJSON.getJSONObject("meta").isNull("versionId"))) {
        		provenance.setId(new IdType(RESOURCE_TYPE, provenanceJSON.getString("id") + "", provenanceJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				provenance.setId(new IdType(RESOURCE_TYPE, provenanceJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
        	provenance.setId(new IdType(RESOURCE_TYPE, provenanceJSON.getString("id") + "", VERSION_ID));
        }

		//Set target
        if(!(provenanceJSON.isNull("target"))) {
        	JSONArray targetJSON = provenanceJSON.getJSONArray("target");
        	int noOfTarget = targetJSON.length();
        	List<Reference> targetList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfTarget; i++) {
        		Reference theTarget = new Reference();
        		if(!targetJSON.getJSONObject(i).isNull("display")) {
            		theTarget.setDisplay(targetJSON.getJSONObject(i).getString("display"));
        		}
        		if(!targetJSON.getJSONObject(i).isNull("type")) {
            		theTarget.setType(targetJSON.getJSONObject(i).getString("type"));
        		}
        		if(!targetJSON.getJSONObject(i).isNull("reference")) {
            		theTarget.setReference(targetJSON.getJSONObject(i).getString("reference"));
        		}
        		targetList.add(theTarget);
        	}
        	provenance.setTarget(targetList);
        }
        
        //Set occurredPeriod
        if(!(provenanceJSON.isNull("occurredPeriod"))) {
    		Period occurredPeriod = new Period();
    		if(!(provenanceJSON.getJSONObject("occurredPeriod").isNull("start"))) {
                Date identifierSDate = CommonUtil.convertStringToDate(provenanceJSON.getJSONObject("occurredPeriod").getString("start"));
                occurredPeriod.setStart(identifierSDate);
    		}
    		if(!(provenanceJSON.getJSONObject("occurredPeriod").isNull("end"))) {
                Date identifierEDate = CommonUtil.convertStringToDate(provenanceJSON.getJSONObject("occurredPeriod").getString("end"));
                occurredPeriod.setStart(identifierEDate);
    		}
    		provenance.setOccurred(occurredPeriod);
    	}
        
        //Set occurredDateTime
        if(!(provenanceJSON.isNull("occurredDateTime"))) {
        	DateTimeType theOccurredDateTime= new DateTimeType();
        	Date occurredDateTime = CommonUtil.convertStringToDate(provenanceJSON.getString("occurredDateTime"));
        	theOccurredDateTime.setValue(occurredDateTime);
        	provenance.setOccurred(theOccurredDateTime);
        }
        
        //Set recorded
        if(!(provenanceJSON.isNull("recorded"))) {
        	String recordedInStr = (String) provenanceJSON.get("recorded");
			Date recordedDate = CommonUtil.convertStringToDate(recordedInStr);
        	provenance.setRecorded(recordedDate);
        }
        
        //Set policy
        if(!(provenanceJSON.isNull("policy"))) {
        	List<UriType> policyList = new ArrayList<UriType>();
        	JSONArray thePolicy = provenanceJSON.getJSONArray("policy");
        	int noOfPolicy = thePolicy.length();
    		for(int i = 0; i < noOfPolicy; i++) {
    			String policyName = thePolicy.getString(i) ;
    			UriType policyType = new UriType();
    			policyType.setValue(policyName);
			   	policyList.add(policyType);
    		}
        	provenance.setPolicy(policyList);
        }
        
        //Set location
        if(!(provenanceJSON.isNull("location"))) {
        	Reference theLocation = new Reference();
    		if(!(provenanceJSON.getJSONObject("location").isNull("display"))) {
    			theLocation.setDisplay(provenanceJSON.getJSONObject("location").getString("display"));
    		}
    		if(!(provenanceJSON.getJSONObject("location").isNull("type"))) {
    			theLocation.setType(provenanceJSON.getJSONObject("location").getString("type"));
    		}
    		if(!(provenanceJSON.getJSONObject("location").isNull("reference"))) {
    			theLocation.setReference(provenanceJSON.getJSONObject("location").getString("reference"));
    		}
        	provenance.setLocation(theLocation);
        }
        
        //Set reason
        if(!(provenanceJSON.isNull("reason"))) {
        	JSONArray reasonJSON = provenanceJSON.getJSONArray("reason");
        	int noOfReasons = reasonJSON.length();
        	List<CodeableConcept> reasonList = new ArrayList<CodeableConcept>();
        	
        	for(int i = 0; i < noOfReasons; i++) {
        		CodeableConcept theReason = new CodeableConcept();
        		if(!(reasonJSON.getJSONObject(i).isNull("coding"))) {
    				JSONArray codingJSON = reasonJSON.getJSONObject(i).getJSONArray("coding");
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
    				theReason.setCoding(codingList);
    			}
        		if(!reasonJSON.getJSONObject(i).isNull("text")) {
            		theReason.setText(reasonJSON.getJSONObject(i).getString("text"));
        		}
        		reasonList.add(theReason);
        	}
    		provenance.setReason(reasonList);
        }
        
        //Set activity
        if(!(provenanceJSON.isNull("activity"))) {
        	CodeableConcept theActivity = new CodeableConcept();
    		if(!(provenanceJSON.getJSONObject("activity").isNull("coding"))) {
    			JSONArray codingJSON = provenanceJSON.getJSONObject("activity").getJSONArray("coding");
    			int noOfActivity = codingJSON.length();
    			List<Coding> codingList = new ArrayList<Coding>();
    			
    			for(int i = 0; i < noOfActivity; i++) {
        			Coding theCoding = new Coding();
    				
        			if(!(codingJSON.getJSONObject(i).isNull("system"))) {
    					theCoding.setSystem(codingJSON.getJSONObject(i).getString("system"));
    				}
    				if(!(codingJSON.getJSONObject(i).isNull("code"))) {
    					theCoding.setCode(codingJSON.getJSONObject(i).getString("code"));
    				}
    				if(!(codingJSON.getJSONObject(i).isNull("display"))) {
    					theCoding.setDisplay(codingJSON.getJSONObject(i).getString("display"));
    				}
    				codingList.add(theCoding);
    			}
    			theActivity.setCoding(codingList);
    		}
    		if(!(provenanceJSON.getJSONObject("activity").isNull("text"))) {
    			theActivity.setText(provenanceJSON.getJSONObject("activity").getString("text"));
    		}
        }
        
        //Set agent
        if(!(provenanceJSON.isNull("agent"))) {
        	JSONArray agentJSON =  provenanceJSON.getJSONArray("agent");
        	List<ProvenanceAgentComponent> agentList = new ArrayList<ProvenanceAgentComponent>(); 
        	int noOfAgents = agentJSON.length();
        	for(int i = 0; i < noOfAgents; i++) {
        		ProvenanceAgentComponent theAgent = new ProvenanceAgentComponent();
        		//Set agent type
        		if(!(agentJSON.getJSONObject(i).isNull("type"))) {
        			JSONObject typeJSON = agentJSON.getJSONObject(i).getJSONObject("type");
        			CodeableConcept theType = new CodeableConcept();
            		if(!(typeJSON.isNull("coding"))) {
            			JSONArray codingJSON = typeJSON.getJSONArray("coding");
            			int noOfType = codingJSON.length();
            			List<Coding> codingList = new ArrayList<Coding>();
            			
            			for(int j = 0; j < noOfType; j++) {
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
            			theType.setCoding(codingList);
            		}
            		if(!(typeJSON.isNull("text"))) {
            			theType.setText(typeJSON.getString("text"));
            		}
            		theAgent.setType(theType);
        		}
        		
        		//Set role
        		if(!(agentJSON.getJSONObject(i).isNull("role"))) {
        			JSONArray roleJSON = agentJSON.getJSONObject(i).getJSONArray("role");
        			List<CodeableConcept> roleList = new ArrayList<CodeableConcept>();
        			int noOfRoles = roleJSON.length();
        			
        			for(int k = 0; k < noOfRoles; k++) {
        				CodeableConcept theRole = new CodeableConcept();
                		if(!(roleJSON.getJSONObject(i).isNull("coding"))) {
            				JSONArray codingJSON = roleJSON.getJSONObject(i).getJSONArray("coding");
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
            				theRole.setCoding(codingList);
            			}
                		if(!roleJSON.getJSONObject(i).isNull("text")) {
                    		theRole.setText(roleJSON.getJSONObject(i).getString("text"));
                		}
                		roleList.add(theRole);
        			}
        			theAgent.setRole(roleList);
        		}
        		
        		//Set who
        		if(!(agentJSON.getJSONObject(i).isNull("who"))) {
        			JSONObject whoJSON = agentJSON.getJSONObject(i).getJSONObject("who");
        			Reference who = new Reference();
            		if(!(whoJSON.isNull("display"))) {
            			who.setDisplay(whoJSON.getString("display"));
            		}
            		if(!(whoJSON.isNull("type"))) {
            			who.setType(whoJSON.getString("type"));
            		}
            		if(!(whoJSON.isNull("reference"))) {
            			who.setReference(whoJSON.getString("reference"));
            		}
            		theAgent.setWho(who);
        		}
        		
        		//Set onBehalfOf
        		if(!(agentJSON.getJSONObject(i).isNull("onBehalfOf"))) {
        			JSONObject onBehalfOfJSON = agentJSON.getJSONObject(i).getJSONObject("onBehalfOf");
        			Reference onBehalfOf = new Reference();
            		if(!(onBehalfOfJSON.isNull("display"))) {
            			onBehalfOf.setDisplay(onBehalfOfJSON.getString("display"));
            		}
            		if(!(onBehalfOfJSON.isNull("type"))) {
            			onBehalfOf.setType(onBehalfOfJSON.getString("type"));
            		}
            		if(!(onBehalfOfJSON.isNull("reference"))) {
            			onBehalfOf.setReference(onBehalfOfJSON.getString("reference"));
            		}
            		theAgent.setOnBehalfOf(onBehalfOf);
        		}
        		agentList.add(theAgent);
        	}
        	provenance.setAgent(agentList);
        }
        
        //Set entity
        if(!(provenanceJSON.isNull("entity"))) {
        	JSONArray entityJSON = provenanceJSON.getJSONArray("entity");
        	List<ProvenanceEntityComponent> entityList = new ArrayList<ProvenanceEntityComponent>();
        	int noOfEntity = entityJSON.length();
        	
        	for(int i = 0; i < noOfEntity; i++) {
        		ProvenanceEntityComponent theEntity = new ProvenanceEntityComponent();
        		//Set role
        		if(!(entityJSON.getJSONObject(i).isNull("role"))) {        			
        			theEntity.setRole(Provenance.ProvenanceEntityRole.fromCode(entityJSON.getJSONObject(i).getString("role")));
        		}
        		
        		//Set what
        		if(!(entityJSON.getJSONObject(i).isNull("what"))) {
        			JSONObject whatJSON = entityJSON.getJSONObject(i).getJSONObject("what");
        			Reference what = new Reference();
            		if(!(whatJSON.isNull("display"))) {
            			what.setDisplay(whatJSON.getString("display"));
            		}
            		if(!(whatJSON.isNull("type"))) {
            			what.setType(whatJSON.getString("type"));
            		}
            		if(!(whatJSON.isNull("reference"))) {
            			what.setReference(whatJSON.getString("reference"));
            		}
            		if(!(whatJSON.isNull("identifier"))) {
            			Identifier theIdentifier = new Identifier();
            			if(!(whatJSON.getJSONObject("identifier").isNull("use"))) {
            				theIdentifier.setUse(Identifier.IdentifierUse.fromCode(whatJSON.getJSONObject("identifier").getString("use")));
            			}
            			if(!(whatJSON.getJSONObject("identifier").isNull("type"))) {
            				JSONObject typeJSON = whatJSON.getJSONObject("identifier").getJSONObject("type");
            				CodeableConcept theCodeableConcept = new CodeableConcept();
            				if(!(typeJSON.isNull("coding"))) {
            					JSONArray codingJSON = typeJSON.getJSONArray("coding");
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
                    				if(!(codingJSON.getJSONObject(j).isNull("version"))) {
                    					theCoding.setVersion(codingJSON.getJSONObject(j).getString("version"));
                    				}
                    				if(!(codingJSON.getJSONObject(j).isNull("userSelected"))) {
                    					theCoding.setUserSelected(codingJSON.getJSONObject(j).getBoolean("userSelected"));
                    				}
                    				codingList.add(theCoding);
            					}
            					theCodeableConcept.setCoding(codingList);
            				}
            				if(!(typeJSON.isNull("text"))) {
            					theCodeableConcept.setText(typeJSON.getString("text"));
            				}
            				theIdentifier.setType(theCodeableConcept);
            			}
            			if(!(whatJSON.getJSONObject("identifier").isNull("system"))) {
            				theIdentifier.setSystem(whatJSON.getJSONObject("identifier").getString("system"));
            			}
            			if(!(whatJSON.getJSONObject("identifier").isNull("value"))) {
            				theIdentifier.setValue(whatJSON.getJSONObject("identifier").getString("value"));
            			}
            			if(!(whatJSON.getJSONObject("identifier").isNull("period"))) {
            				JSONObject periodJSON = whatJSON.getJSONObject("identifier").getJSONObject("period");
            				Period thePeriod = new Period();
            				if(!(periodJSON.isNull("start"))) {
                				Date contactSDate = CommonUtil.convertStringToDateYear(periodJSON.getString("start"));
                				thePeriod.setEnd(contactSDate);
                    		}
            				if(!(periodJSON.isNull("end"))) {
                				Date contactEDate = CommonUtil.convertStringToDateYear(periodJSON.getString("end"));
                				thePeriod.setEnd(contactEDate);
                    		}
            				theIdentifier.setPeriod(thePeriod);
            			}
            			if(!(whatJSON.getJSONObject("identifier").isNull("assigner"))) {
            				JSONObject assignerJSON = whatJSON.getJSONObject("identifier").getJSONObject("assigner");
            				Reference theAssigner = new Reference();
            				if(!(assignerJSON.isNull("display"))) {
            					theAssigner.setDisplay(provenanceJSON.getJSONObject("location").getString("display"));
            	    		}
            	    		if(!(assignerJSON.isNull("type"))) {
            	    			theAssigner.setType(provenanceJSON.getJSONObject("location").getString("type"));
            	    		}
            	    		if(!(assignerJSON.isNull("reference"))) {
            	    			theAssigner.setReference(provenanceJSON.getJSONObject("location").getString("reference"));
            	    		}
            	    		theIdentifier.setAssigner(theAssigner);
            			}
            			what.setIdentifier(theIdentifier);
            		}
            		theEntity.setWhat(what);
        		}
        		entityList.add(theEntity);
        	}
        	provenance.setEntity(entityList);
        }
        
        //Set signature
        if(!(provenanceJSON.isNull("signature"))) {
        	JSONArray signatureJSON = provenanceJSON.getJSONArray("signature");
        	List<Signature> signatureList = new ArrayList<Signature>();
        	int noOfSignatures = signatureJSON.length();
        	
        	for(int i = 0; i < noOfSignatures; i++) {
        		Signature theSignature = new Signature();
        		//Set type
        		if(!(signatureJSON.getJSONObject(i).isNull("type"))) {
        			JSONArray typeJSON = signatureJSON.getJSONObject(i).getJSONArray("type");
        			List<Coding> typeList = new ArrayList<Coding>();
        			int noOfType = typeJSON.length();
        			for(int j = 0; j < noOfType; j++) {
        				Coding theCoding = new Coding();
        				if(!(typeJSON.getJSONObject(j).isNull("system"))) {
        					theCoding.setSystem(typeJSON.getJSONObject(j).getString("system"));
        				}
        				if(!(typeJSON.getJSONObject(j).isNull("code"))) {
        					theCoding.setCode(typeJSON.getJSONObject(j).getString("code"));
        				}
        				if(!(typeJSON.getJSONObject(j).isNull("display"))) {
        					theCoding.setDisplay(typeJSON.getJSONObject(j).getString("display"));
        				}
        				if(!(typeJSON.getJSONObject(j).isNull("version"))) {
        					theCoding.setVersion(typeJSON.getJSONObject(j).getString("version"));
        				}
        				if(!(typeJSON.getJSONObject(j).isNull("userSelected"))) {
        					theCoding.setUserSelected(typeJSON.getJSONObject(j).getBoolean("userSelected"));
        				}
        				typeList.add(theCoding);
        			}
        			theSignature.setType(typeList);
        		}
        		
        		//Set when 
        		if(!signatureJSON.getJSONObject(i).isNull("when")) {
        			String whenInStr = (String) signatureJSON.getJSONObject(i).get("when");
        			Date whenDate = CommonUtil.convertStringToDate(whenInStr);
        			theSignature.setWhen(whenDate);
        		}
        		
        		//Set who
        		if(!signatureJSON.getJSONObject(i).isNull("who")) {
        			JSONObject whoJSON = signatureJSON.getJSONObject(i).getJSONObject("who");
        			Reference who = new Reference();
            		if(!(whoJSON.isNull("display"))) {
            			who.setDisplay(whoJSON.getString("display"));
            		}
            		if(!(whoJSON.isNull("type"))) {
            			who.setType(whoJSON.getString("type"));
            		}
            		if(!(whoJSON.isNull("reference"))) {
            			who.setReference(whoJSON.getString("reference"));
            		}
            		theSignature.setWho(who);
        		}
        		
        		//Set onBehalfOf
        		if(!signatureJSON.getJSONObject(i).isNull("onBehalfOf")) {
        			JSONObject onBehalfOfJSON = signatureJSON.getJSONObject(i).getJSONObject("onBehalfOf");
        			Reference onBehalfOf = new Reference();
            		if(!(onBehalfOfJSON.isNull("display"))) {
            			onBehalfOf.setDisplay(onBehalfOfJSON.getString("display"));
            		}
            		if(!(onBehalfOfJSON.isNull("type"))) {
            			onBehalfOf.setType(onBehalfOfJSON.getString("type"));
            		}
            		if(!(onBehalfOfJSON.isNull("reference"))) {
            			onBehalfOf.setReference(onBehalfOfJSON.getString("reference"));
            		}
            		theSignature.setOnBehalfOf(onBehalfOf);
        		}
        		
        		//Set targetFormat
        		if(!signatureJSON.getJSONObject(i).isNull("targetFormat")) {
        			theSignature.setTargetFormat(signatureJSON.getJSONObject(i).getString("targetFormat"));
        		}
        		
        		//Set sigFormat
        		if(!signatureJSON.getJSONObject(i).isNull("sigFormat")) {
        			theSignature.setSigFormat(signatureJSON.getJSONObject(i).getString("sigFormat"));
        		}
            	signatureList.add(theSignature);
        	}
        	provenance.setSignature(signatureList);
        }
		return provenance;
	
	}

}
