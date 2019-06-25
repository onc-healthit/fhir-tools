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
import org.hl7.fhir.r4.model.Encounter.DiagnosisComponent;
import org.hl7.fhir.r4.model.Encounter.EncounterHospitalizationComponent;
import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafEncounter;
import org.sitenv.spring.service.EncounterService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class EncounterResourceProvider implements IResourceProvider {
	
	public static final String RESOURCE_TYPE = "Encounter";
    public static final String VERSION_ID = "4.0";
    AbstractApplicationContext context;
    EncounterService service;

    public EncounterResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (EncounterService) context.getBean("EncounterService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Encounter.class;
	}
	
 	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Encounter/1/_history/4
	 * @param theId : Id of the Encounter
	 * @return : Object of Encounter information
	 */
	@Read(version=true)
    public Encounter readOrVread(@IdParam IdType theId) {
		int id;
		DafEncounter dafEncounter;
		try {
		    id = theId.getIdPartAsLong().intValue();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		if (theId.hasVersionIdPart()) {
		   // this is a vread  
		   dafEncounter = service.getEncounterByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       dafEncounter = service.getEncounterById(id);
		}
		return createEncounterObject(dafEncounter);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=Encounter.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Encounter/1/_history
	 * @param theId : ID of the Encounter
	 * @return : List of Encounters
	 */
	@History()
    public List<Encounter> getEncounterHistoryById( @IdParam IdType theId) {

		int id;
		try {
		    id = theId.getIdPartAsLong().intValue();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafEncounter> dafEncounterList = service.getEncounterHistoryById(id);
        
        List<Encounter> encounterList = new ArrayList<Encounter>();
        for (DafEncounter dafEncounter : dafEncounterList) {
        	encounterList.add(createEncounterObject(dafEncounter));
        }
        return encounterList;
	}
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theDate
	 * @param theType
	 * @param thePatient
	 * @param theStatus
	 * @param theClass
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */

    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = Encounter.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "Identifier(s) by which this encounter is knownr")
        @OptionalParam(name = Encounter.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "A date within the period the Encounter lasted")
        @OptionalParam(name = Encounter.SP_DATE)
        DateRangeParam theDate,
        
        @Description(shortDefinition = "Specific type of encounter")
        @OptionalParam(name = Encounter.SP_TYPE)
        TokenAndListParam theType,
        
        @Description(shortDefinition = "The patient or group present at the encounter")
        @OptionalParam(name = Encounter.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "Classification of patient encounter")
        @OptionalParam(name = Encounter.SP_CLASS)
        TokenAndListParam theClass,
        
        @Description(shortDefinition = "planned | arrived | triaged | in-progress | onleave | finished | cancelled +")
        @OptionalParam(name = Encounter.SP_STATUS)
        TokenAndListParam theStatus,
      
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
    	
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(Encounter.SP_RES_ID, theId);
        paramMap.add(Encounter.SP_IDENTIFIER, theIdentifier);
        paramMap.add(Encounter.SP_DATE, theDate);
        paramMap.add(Encounter.SP_TYPE, theType);
        paramMap.add(Encounter.SP_PATIENT, thePatient);
        paramMap.add(Encounter.SP_CLASS, theClass);
        paramMap.add(Encounter.SP_STATUS, theStatus);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<DafEncounter> results = service.search(paramMap);

        return new IBundleProvider() {
        	final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> encounterList = new ArrayList<IBaseResource>();
                for(DafEncounter dafEncounter : results){
                	encounterList.add(createEncounterObject(dafEncounter));
                }
                return encounterList;
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
     * This method converts DafEncounter object to Encounter object
     * @param dafEncounter : DafEncounter Encounter object
     * @return : Encounter Encounter object
     */
    private Encounter createEncounterObject(DafEncounter dafEncounter) {
        Encounter encounter = new Encounter();
        JSONObject encounterJSON = new JSONObject(dafEncounter.getData());

        // Set version
        if(!(encounterJSON.isNull("meta"))) {
        	if(!(encounterJSON.getJSONObject("meta").isNull("versionId"))) {
        		encounter.setId(new IdType(RESOURCE_TYPE, encounterJSON.getString("id") + "", encounterJSON.getJSONObject("meta").getString("versionId")));
        	}
        }
        else {
        	encounter.setId(new IdType(RESOURCE_TYPE, encounterJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(encounterJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = encounterJSON.getJSONArray("identifier");
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
                        thePeriod.setEnd(endDate);
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
        	encounter.setIdentifier(identifiers);
        }
        //set subject
        if (!encounterJSON.isNull("subject")) {
        	JSONObject subjectJSON = encounterJSON.getJSONObject("subject");
        	Reference subject = new Reference();
        	
        	if (!subjectJSON.isNull("reference")) {
				subject.setReference(subjectJSON.getString("reference"));
			}
        	if (!subjectJSON.isNull("display")) {
				subject.setDisplay(subjectJSON.getString("display"));
			}
        	encounter.setSubject(subject);
		}
       
    		
        //set status
        if(!encounterJSON.isNull("status")) {
        	encounter.setStatus(EncounterStatus.fromCode(encounterJSON.getString("status")));
        }
        
        //set type
        if (!encounterJSON.isNull("type")) { 
        	  JSONArray typeJSON = encounterJSON.getJSONArray("type"); 
			  List<CodeableConcept> typeLists =new ArrayList<CodeableConcept>(); 
			  int noOfTypes = typeJSON.length();
		  
			  for (int i = 0; i < noOfTypes; i++) { 
				  CodeableConcept theType = new CodeableConcept();
		  		  if (!typeJSON.getJSONObject(i).isNull("coding")) { 
				  JSONArray codingJSON = typeJSON.getJSONObject(i).getJSONArray("coding"); 
				  List<Coding> codingList =new ArrayList<Coding>(); 
				  int noOfCodings = codingJSON.length();
			  
				  for (int j = 0; j < noOfCodings; j++) { 
					  Coding theCoding = new Coding(); 
					  if(!codingJSON.getJSONObject(j).isNull("system")) {
						  theCoding.setSystem(codingJSON.getJSONObject(j).getString("system")); 
					  } 
					  if(!codingJSON.getJSONObject(j).isNull("code")) {
						  theCoding.setCode(codingJSON.getJSONObject(j).getString("code")); 
					  } 
					  if(!codingJSON.getJSONObject(j).isNull("display")) {
						  theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display")); 
					  }
					  codingList.add(theCoding); 
				  } 
				  theType.setCoding(codingList); 
			  }
			  typeLists.add(theType); 
		  } 
			  encounter.setType(typeLists); 
		}
        
        //set priority
        if (!encounterJSON.isNull("priority")) { 
        	  JSONObject priorityJSON = encounterJSON.getJSONObject("priority"); 
			  CodeableConcept thePriority = new CodeableConcept (); 
		  
			
		  		  if (!priorityJSON.isNull("coding")) { 
				  JSONArray codingJSON = priorityJSON.getJSONArray("coding"); 
				  List<Coding> codingList =new ArrayList<Coding>(); 
				  int noOfCodings = codingJSON.length();
			  
				  for (int j = 0; j < noOfCodings; j++) { 
					  Coding theCoding = new Coding(); 
					  if(!codingJSON.getJSONObject(j).isNull("system")) {
						  theCoding.setSystem(codingJSON.getJSONObject(j).getString("system")); 
					  } 
					  if(!codingJSON.getJSONObject(j).isNull("code")) {
						  theCoding.setCode(codingJSON.getJSONObject(j).getString("code")); 
					  } 
					  if(!codingJSON.getJSONObject(j).isNull("display")) {
						  theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display")); 
					  }
					  codingList.add(theCoding); 
				  } 
				  thePriority.setCoding(codingList); 
			  }
		  		
			  encounter.setPriority(thePriority); 
		}
      //set class
        if(!encounterJSON.isNull("class")) {
        	JSONObject classJSON = encounterJSON.getJSONObject("class");
        	Coding theCoding = new Coding();
        	
        	if (!classJSON.isNull("system")) {
				theCoding.setSystem(classJSON.getString("system"));
			}
        	if (!classJSON.isNull("version")) {
				theCoding.setVersion(classJSON.getString("version"));
			}
        	if (!classJSON.isNull("code")) {
				theCoding.setCode(classJSON.getString("code"));
			}
        	if (!classJSON.isNull("display")) {
			    theCoding.setDisplay(classJSON.getString("display"));
			}
        	if (!classJSON.isNull("userSelected")) {
				theCoding.setUserSelected(classJSON.getBoolean("userSelected"));
			}
        	encounter.setClass_(theCoding);
        }
        
        //set length
        if (!encounterJSON.isNull("length")) {
			JSONObject lengthJSON = encounterJSON.getJSONObject("length");
			Duration theDuration = new Duration();
			
			if (!lengthJSON.isNull("value")) { 
				theDuration.setValue(lengthJSON.getDouble("value"));
			}
			if (!lengthJSON.isNull("unit")) {
				theDuration.setUnit(lengthJSON.getString("unit"));
			}
			if (!lengthJSON.isNull("system")) {
				theDuration.setSystem(lengthJSON.getString("system"));
			}
			if (!lengthJSON.isNull("code")) {
				theDuration.setCode(lengthJSON.getString("code"));
			}
			encounter.setLength(theDuration);
		}
        
        // set serviceProvider
        if (!encounterJSON.isNull("serviceProvider")) {
			JSONObject serviceProviderJSON = encounterJSON.getJSONObject("serviceProvider");
			Reference theReference = new Reference();
			
			if (!serviceProviderJSON.isNull("reference")) {
				theReference.setReference(serviceProviderJSON.getString("reference"));
			}
			if (!serviceProviderJSON.isNull("type")) {
				theReference.setType(serviceProviderJSON.getString("type"));
			}
			if (!serviceProviderJSON.isNull("display")) {
				theReference.setDisplay(serviceProviderJSON.getString("display"));
			}
			encounter.setServiceProvider(theReference);
		}
        
        //set reasonCode
        if (!encounterJSON.isNull("reasonCode")) {
			JSONArray reasonCodeJSON = encounterJSON.getJSONArray("reasonCode");
			List<CodeableConcept> reasonCodeList = new ArrayList<CodeableConcept>();
			int noOfReasonCodes = reasonCodeJSON.length();
			
			for (int i = 0; i < noOfReasonCodes; i++) {
				CodeableConcept theReasonCode = new CodeableConcept();
		
				 if (!reasonCodeJSON.getJSONObject(i).isNull("coding")) { 
					  JSONArray codingJSON = reasonCodeJSON.getJSONObject(i).getJSONArray("coding"); 
					  List<Coding> codingList =new ArrayList<Coding>(); 
					  int noOfCodings = codingJSON.length();
				  
					  for (int j = 0; j < noOfCodings; j++) { 
						  Coding theCoding = new Coding(); 
						  if(!codingJSON.getJSONObject(j).isNull("system")) {
							  theCoding.setSystem(codingJSON.getJSONObject(j).getString("system")); 
						  } 
						  if(!codingJSON.getJSONObject(j).isNull("code")) {
							  theCoding.setCode(codingJSON.getJSONObject(j).getString("code")); 
						  } 
						  if(!codingJSON.getJSONObject(j).isNull("display")) {
							  theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display")); 
						  }
						  codingList.add(theCoding); 
					  } 
					  theReasonCode.setCoding(codingList); 
				  }
				 if (!reasonCodeJSON.getJSONObject(i).isNull("text")) {
					 theReasonCode.setText(reasonCodeJSON.getJSONObject(i).getString("text"));
					
				}
				 reasonCodeList.add(theReasonCode);
					
			}
			encounter.setReasonCode(reasonCodeList);
        }
			
        // set diagnosis
        if (!encounterJSON.isNull("diagnosis")) {
        	JSONArray diagnosisJSON = encounterJSON.getJSONArray("diagnosis");
        	List<DiagnosisComponent> diagnosisList = new ArrayList<Encounter.DiagnosisComponent>();
        	int noOfDiagnosis = diagnosisJSON.length();
			
        	for (int i = 0; i < noOfDiagnosis; i++) {
				DiagnosisComponent diagnosisComponent = new DiagnosisComponent();
				
				if (!diagnosisJSON.getJSONObject(i).isNull("condition")) {
					Reference theReference  = new Reference();
					
					JSONObject conditionJSON = diagnosisJSON.getJSONObject(i).getJSONObject("condition");
						
					if (!conditionJSON.isNull("reference")) {
						theReference.setReference(conditionJSON.getString("reference"));
					}
					if (!conditionJSON.isNull("type")) {
						theReference.setType(conditionJSON.getString("type"));
					}
					if (!conditionJSON.isNull("display")) {
						theReference.setDisplay(conditionJSON.getString("display"));
					}
					diagnosisComponent.setCondition(theReference);
				}
				if (!diagnosisJSON.getJSONObject(i).isNull("use")) {
					JSONObject useJSON = diagnosisJSON.getJSONObject(i).getJSONObject("use");
					 CodeableConcept theUse = new CodeableConcept (); 
					  
						
			  		  if (!useJSON.isNull("coding")) { 
					  JSONArray codingJSON = useJSON.getJSONArray("coding"); 
					  List<Coding> codingList =new ArrayList<Coding>(); 
					  int noOfCodings = codingJSON.length();
				  
					  for (int j = 0; j < noOfCodings; j++) { 
						  Coding theCoding = new Coding(); 
						  if(!codingJSON.getJSONObject(j).isNull("system")) {
							  theCoding.setSystem(codingJSON.getJSONObject(j).getString("system")); 
						  } 
						  if(!codingJSON.getJSONObject(j).isNull("code")) {
							  theCoding.setCode(codingJSON.getJSONObject(j).getString("code")); 
						  } 
						  if(!codingJSON.getJSONObject(j).isNull("display")) {
							  theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display")); 
						  }
						  codingList.add(theCoding); 
					  } 
					  theUse.setCoding(codingList); 
				  }
			  		 diagnosisComponent.setUse(theUse);
				}
				
				if (!diagnosisJSON.getJSONObject(i).isNull("rank")) {
					diagnosisComponent.setRank(diagnosisJSON.getJSONObject(i).getInt("rank"));
				}
				diagnosisList.add(diagnosisComponent);
			}
        	encounter.setDiagnosis(diagnosisList);
		}
			
        //set hospitalization
        if (!encounterJSON.isNull("hospitalization")) {
			JSONObject hospitalizationJSON = encounterJSON.getJSONObject("hospitalization");
			EncounterHospitalizationComponent theHospitalization = new EncounterHospitalizationComponent();
			
			if(!(encounterJSON.isNull("preAdmissionIdentifier"))) {
	        	JSONObject identifierJSON = encounterJSON.getJSONObject("preAdmissionIdentifier");
	        	
	            	Identifier theIdentifier = new Identifier();
	        		if(!(identifierJSON.isNull("use"))) {
	                	theIdentifier.setUse(Identifier.IdentifierUse.fromCode(identifierJSON.getString("use")));	
	            	}
	        		
	        		if(!(identifierJSON.isNull("type"))) {
	        			CodeableConcept theType = new CodeableConcept();
	            		if(!(identifierJSON.getJSONObject("type").isNull("coding"))) {
	            			JSONArray typeCodingJSON = identifierJSON.getJSONObject("type").getJSONArray("coding");
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
	        		
	        		if(!(identifierJSON.isNull("system"))) {
	                	theIdentifier.setSystem(identifierJSON.getString("system"));
	            	}
	            	
	            	if(!(identifierJSON.isNull("value"))) {
	                	theIdentifier.setValue(identifierJSON.getString("value"));
	            	}
	            	theHospitalization.setPreAdmissionIdentifier(theIdentifier);
	    			
	        	}
	        	
			
			if (!hospitalizationJSON.isNull("origin")) {
				JSONObject originJSON = hospitalizationJSON.getJSONObject("origin");
				Reference theOrigin = new Reference();
				
				if (!originJSON.isNull("reference")) {
					theOrigin.setReference(originJSON.getString("reference"));
				}
				if (!originJSON.isNull("type")) {
					theOrigin.setType(originJSON.getString("type"));
				}
				if (!originJSON.isNull("display")) {
					theOrigin.setDisplay(originJSON.getString("display"));
				}
				theHospitalization.setOrigin(theOrigin);
			}
			
			if (!hospitalizationJSON.isNull("admitSource")) {
				JSONObject admitSourceJSON = hospitalizationJSON.getJSONObject("admitSource");
				 CodeableConcept theAdmitSource = new CodeableConcept (); 
				  if (!admitSourceJSON.isNull("coding")) { 
				  JSONArray codingJSON = admitSourceJSON.getJSONArray("coding"); 
				  List<Coding> codingList =new ArrayList<Coding>(); 
				  int noOfCodings = codingJSON.length();
			  
				  for (int j = 0; j < noOfCodings; j++) { 
					  Coding theCoding = new Coding(); 
					  if(!codingJSON.getJSONObject(j).isNull("system")) {
						  theCoding.setSystem(codingJSON.getJSONObject(j).getString("system")); 
					  } 
					  if(!codingJSON.getJSONObject(j).isNull("code")) {
						  theCoding.setCode(codingJSON.getJSONObject(j).getString("code")); 
					  } 
					  if(!codingJSON.getJSONObject(j).isNull("display")) {
						  theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display")); 
					  }
					  codingList.add(theCoding); 
				  } 
				  theAdmitSource.setCoding(codingList); 
				  }
				  if (!admitSourceJSON.isNull("text")) { 
					  theAdmitSource.setText(hospitalizationJSON.getString("text"));
				  }
				  theHospitalization.setAdmitSource(theAdmitSource);
			}
			
			if (!hospitalizationJSON.isNull("reAdmission")) {
				JSONObject reAdmissionJSON = hospitalizationJSON.getJSONObject("reAdmission");
				 CodeableConcept theReAdmissione = new CodeableConcept (); 
				  if (!reAdmissionJSON.isNull("coding")) { 
				  JSONArray codingJSON = reAdmissionJSON.getJSONArray("coding"); 
				  List<Coding> codingList =new ArrayList<Coding>(); 
				  int noOfCodings = codingJSON.length();
			  
				  for (int j = 0; j < noOfCodings; j++) { 
					  Coding theCoding = new Coding(); 
					  if(!codingJSON.getJSONObject(j).isNull("system")) {
						  theCoding.setSystem(codingJSON.getJSONObject(j).getString("system")); 
					  } 
					  if(!codingJSON.getJSONObject(j).isNull("code")) {
						  theCoding.setCode(codingJSON.getJSONObject(j).getString("code")); 
					  } 
					  if(!codingJSON.getJSONObject(j).isNull("display")) {
						  theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display")); 
					  }
					  codingList.add(theCoding); 
				  } 
				  theReAdmissione.setCoding(codingList); 
				  }
				  if (!reAdmissionJSON.isNull("text")) { 
					  theReAdmissione.setText(hospitalizationJSON.getString("text"));
				  }
				  theHospitalization.setReAdmission(theReAdmissione);
			}
			
			if (!hospitalizationJSON.isNull("destination")) {
				JSONObject originJSON = hospitalizationJSON.getJSONObject("destination");
				Reference theOrigin = new Reference();
				
				if (!originJSON.isNull("reference")) {
					theOrigin.setReference(originJSON.getString("reference"));
				}
				if (!originJSON.isNull("type")) {
					theOrigin.setType(originJSON.getString("type"));
				}
				if (!originJSON.isNull("display")) {
					theOrigin.setDisplay(originJSON.getString("display"));
				}
				theHospitalization.setDestination(theOrigin);
			}
			
			if (!encounterJSON.isNull("dietPreference")) { 
	        	  JSONArray dietPreferenceJSON = encounterJSON.getJSONArray("dietPreference"); 
				  List<CodeableConcept> dietPreferenceLists =new ArrayList<CodeableConcept>(); 
				  int noOfTypes = dietPreferenceJSON.length();
			  
				  for (int i = 0; i < noOfTypes; i++) { 
					  CodeableConcept theDietPreference = new CodeableConcept();
			  		  if (!dietPreferenceJSON.getJSONObject(i).isNull("coding")) { 
					  JSONArray codingJSON = dietPreferenceJSON.getJSONObject(i).getJSONArray("coding"); 
					  List<Coding> codingList =new ArrayList<Coding>(); 
					  int noOfCodings = codingJSON.length();
				  
					  for (int j = 0; j < noOfCodings; j++) { 
						  Coding theCoding = new Coding(); 
						  if(!codingJSON.getJSONObject(j).isNull("system")) {
							  theCoding.setSystem(codingJSON.getJSONObject(j).getString("system")); 
						  } 
						  if(!codingJSON.getJSONObject(j).isNull("code")) {
							  theCoding.setCode(codingJSON.getJSONObject(j).getString("code")); 
						  } 
						  if(!codingJSON.getJSONObject(j).isNull("display")) {
							  theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display")); 
						  }
						  codingList.add(theCoding); 
					  } 
					  theDietPreference.setCoding(codingList); 
				  }
			  		dietPreferenceLists.add(theDietPreference); 
			  } 
				theHospitalization.setDietPreference(dietPreferenceLists);
			}
			
			if (!encounterJSON.isNull("specialCourtesy")) { 
	        	  JSONArray specialCourtesyJSON = encounterJSON.getJSONArray("specialCourtesy"); 
				  List<CodeableConcept> specialCourtesyLists =new ArrayList<CodeableConcept>(); 
				  int noOfTypes = specialCourtesyJSON.length();
			  
				  for (int i = 0; i < noOfTypes; i++) { 
					  CodeableConcept theSpecialCourtesy = new CodeableConcept();
			  		  if (!specialCourtesyJSON.getJSONObject(i).isNull("coding")) { 
					  JSONArray codingJSON = specialCourtesyJSON.getJSONObject(i).getJSONArray("coding"); 
					  List<Coding> codingList =new ArrayList<Coding>(); 
					  int noOfCodings = codingJSON.length();
				  
					  for (int j = 0; j < noOfCodings; j++) { 
						  Coding theCoding = new Coding(); 
						  if(!codingJSON.getJSONObject(j).isNull("system")) {
							  theCoding.setSystem(codingJSON.getJSONObject(j).getString("system")); 
						  } 
						  if(!codingJSON.getJSONObject(j).isNull("code")) {
							  theCoding.setCode(codingJSON.getJSONObject(j).getString("code")); 
						  } 
						  if(!codingJSON.getJSONObject(j).isNull("display")) {
							  theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display")); 
						  }
						  codingList.add(theCoding); 
					  } 
					  theSpecialCourtesy.setCoding(codingList); 
				  }
			  		specialCourtesyLists.add(theSpecialCourtesy); 
			  } 
				theHospitalization.setSpecialCourtesy(specialCourtesyLists);
			}
			
			
			if (!encounterJSON.isNull("specialArrangement")) { 
	        	  JSONArray specialArrangementJSON = encounterJSON.getJSONArray("specialArrangement"); 
				  List<CodeableConcept> specialArrangementLists =new ArrayList<CodeableConcept>(); 
				  int noOfTypes = specialArrangementJSON.length();
			  
				  for (int i = 0; i < noOfTypes; i++) { 
					  CodeableConcept thespecialArrangement = new CodeableConcept();
			  		  if (!specialArrangementJSON.getJSONObject(i).isNull("coding")) { 
					  JSONArray codingJSON = specialArrangementJSON.getJSONObject(i).getJSONArray("coding"); 
					  List<Coding> codingList =new ArrayList<Coding>(); 
					  int noOfCodings = codingJSON.length();
				  
					  for (int j = 0; j < noOfCodings; j++) { 
						  Coding theCoding = new Coding(); 
						  if(!codingJSON.getJSONObject(j).isNull("system")) {
							  theCoding.setSystem(codingJSON.getJSONObject(j).getString("system")); 
						  } 
						  if(!codingJSON.getJSONObject(j).isNull("code")) {
							  theCoding.setCode(codingJSON.getJSONObject(j).getString("code")); 
						  } 
						  if(!codingJSON.getJSONObject(j).isNull("display")) {
							  theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display")); 
						  }
						  codingList.add(theCoding); 
					  } 
					  thespecialArrangement.setCoding(codingList); 
				  }
			  		specialArrangementLists.add(thespecialArrangement); 
			  } 
				theHospitalization.setSpecialCourtesy(specialArrangementLists);
			 
			}
			
			if (!hospitalizationJSON.isNull("dischargeDisposition")) {
				JSONObject dischargeDispositionJSON = hospitalizationJSON.getJSONObject("dischargeDisposition");
				 CodeableConcept theDischargeDisposition = new CodeableConcept (); 
				  if (!dischargeDispositionJSON.isNull("coding")) { 
				  JSONArray codingJSON = dischargeDispositionJSON.getJSONArray("coding"); 
				  List<Coding> codingList =new ArrayList<Coding>(); 
				  int noOfCodings = codingJSON.length();
			  
				  for (int j = 0; j < noOfCodings; j++) { 
					  Coding theCoding = new Coding(); 
					  if(!codingJSON.getJSONObject(j).isNull("system")) {
						  theCoding.setSystem(codingJSON.getJSONObject(j).getString("system")); 
					  } 
					  if(!codingJSON.getJSONObject(j).isNull("code")) {
						  theCoding.setCode(codingJSON.getJSONObject(j).getString("code")); 
					  } 
					  if(!codingJSON.getJSONObject(j).isNull("display")) {
						  theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display")); 
					  }
					  codingList.add(theCoding); 
				  } 
				  theDischargeDisposition.setCoding(codingList); 
				  }
				  if (!dischargeDispositionJSON.isNull("text")) { 
					  theDischargeDisposition.setText(hospitalizationJSON.getString("text"));
				  }
				  theHospitalization.setAdmitSource(theDischargeDisposition);
			}
			
			
			encounter.setHospitalization(theHospitalization);
        }
		//set period	
        if (!encounterJSON.isNull("period")) {
			JSONObject periodJSON = encounterJSON.getJSONObject("period");
			Period thePeriod = new Period();
			if (!periodJSON.isNull("start")) {
				thePeriod.setStart(CommonUtil.convertStringToDate(periodJSON.getString("start")));
			}
			if (!periodJSON.isNull("end")) {
				thePeriod.setEnd(CommonUtil.convertStringToDate(periodJSON.getString("end")));
			}
			encounter.setPeriod(thePeriod);
		}
     	
           
        return encounter;
    }
}
