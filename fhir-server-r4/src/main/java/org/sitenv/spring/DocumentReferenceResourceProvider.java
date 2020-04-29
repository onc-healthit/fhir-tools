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
import org.hl7.fhir.r4.model.DocumentReference.*;
import org.hl7.fhir.r4.model.Enumerations.DocumentReferenceStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafDocumentReference;
import org.sitenv.spring.service.DocumentReferenceService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.*;

public class DocumentReferenceResourceProvider implements IResourceProvider {
	
	public static final String RESOURCE_TYPE = "DocumentReference";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    DocumentReferenceService service;

    public DocumentReferenceResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (DocumentReferenceService) context.getBean("DocumentReferenceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return DocumentReference.class;
	}
	
 	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/DocumentReference/1/_history/4
	 * @param theId : Id of the DocumentReference
	 * @return : Object of DocumentReference information
	 */
	@Read(version=true)
    public DocumentReference readOrVread(@IdParam IdType theId) {
		String id;
		DafDocumentReference dafDocumentReference;
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
		   dafDocumentReference = service.getDocumentReferenceByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       dafDocumentReference = service.getDocumentReferenceById(id);
		}
		return createDocumentReferenceObject(dafDocumentReference);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=DocumentReference.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/DocumentReference/1/_history
	 * @param theId : ID of the DocumentReference
	 * @return : List of DocumentReferences
	 */
	@History()
    public List<DocumentReference> getDocumentReferenceHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafDocumentReference> dafDocumentReferenceList = service.getDocumentReferenceHistoryById(id);
        
        List<DocumentReference> DocumentReferenceList = new ArrayList<DocumentReference>();
        for (DafDocumentReference dafDocumentReference : dafDocumentReferenceList) {
        	DocumentReferenceList.add(createDocumentReferenceObject(dafDocumentReference));
        }
        return DocumentReferenceList;
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
	 * @param theCategory
	 * @param theStatus
	 * @param thePeriod
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
        @OptionalParam(name = DocumentReference.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "A DocumentReference identifier")
        @OptionalParam(name = DocumentReference.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
       
        @Description(shortDefinition = "When this document reference was created")
        @OptionalParam(name = DocumentReference.SP_DATE)
        DateRangeParam theDate,
        
        @Description(shortDefinition = "Kind of document (LOINC if possible)")
        @OptionalParam(name = DocumentReference.SP_TYPE)
        TokenAndListParam theType,
         
        @Description(shortDefinition = "Who/what is the subject of the document")
        @OptionalParam(name = DocumentReference.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "Categorization of document")
        @OptionalParam(name = DocumentReference.SP_CATEGORY)
        TokenAndListParam theCategory,
        
        @Description(shortDefinition = "current | superseded | entered-in-error")
        @OptionalParam(name = DocumentReference.SP_STATUS)
        TokenAndListParam theStatus,
        
        @Description(shortDefinition = "Time of service that is being documented")
        @OptionalParam(name = DocumentReference.SP_PERIOD)
        DateRangeParam thePeriod,

        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
    	
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(DocumentReference.SP_RES_ID, theId);
        paramMap.add(DocumentReference.SP_IDENTIFIER, theIdentifier);
        paramMap.add(DocumentReference.SP_DATE, theDate);
        paramMap.add(DocumentReference.SP_TYPE, theType);
        paramMap.add(DocumentReference.SP_PATIENT, thePatient);
        paramMap.add(DocumentReference.SP_CATEGORY, theCategory);
        paramMap.add(DocumentReference.SP_STATUS, theStatus);
        paramMap.add(DocumentReference.SP_PERIOD, thePeriod);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<DafDocumentReference> results = service.search(paramMap);

        return new IBundleProvider() {
        	final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> documentReferenceList = new ArrayList<IBaseResource>();
				List<String> ids = new ArrayList<String>();
                for(DafDocumentReference dafDocumentReference : results){
					DocumentReference documentReference = createDocumentReferenceObject(dafDocumentReference);
					documentReferenceList.add(documentReference);
					ids.add(((IdType)documentReference.getIdElement()).getResourceType()+"/"+((IdType)documentReference.getIdElement()).getIdPart());
                }
				if(theRevIncludes.size() >0 ){
					documentReferenceList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
				}
				return documentReferenceList;
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
     * @param dafDocumentReference : DafDocumentReference DocumentReference object
     * @return : DocumentReference DocumentReference object
     */
    private DocumentReference createDocumentReferenceObject(DafDocumentReference dafDocumentReference) {
        DocumentReference documentReference = new DocumentReference();
        JSONObject documentReferenceJSON = new JSONObject(dafDocumentReference.getData());

        // Set version
        if(!(documentReferenceJSON.isNull("meta"))) {
        	if(!(documentReferenceJSON.getJSONObject("meta").isNull("versionId"))) {
        		documentReference.setId(new IdType(RESOURCE_TYPE, documentReferenceJSON.getString("id") + "", documentReferenceJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				documentReference.setId(new IdType(RESOURCE_TYPE, documentReferenceJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
        	documentReference.setId(new IdType(RESOURCE_TYPE, documentReferenceJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(documentReferenceJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = documentReferenceJSON.getJSONArray("identifier");
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
        	documentReference.setIdentifier(identifiers);
        }
        //set subject
        if (!documentReferenceJSON.isNull("subject")) {
        	JSONObject subjectJSON = documentReferenceJSON.getJSONObject("subject");
        	Reference subject = new Reference();
        	
        	if (!subjectJSON.isNull("reference")) {
				subject.setReference(subjectJSON.getString("reference"));
			}
        	if (!subjectJSON.isNull("display")) {
				subject.setDisplay(subjectJSON.getString("display"));
			}
        	documentReference.setSubject(subject);
		}
       
     // set category
     		if (!documentReferenceJSON.isNull("category")) {
     			JSONArray categoryJSON = documentReferenceJSON.getJSONArray("category");
     			List<CodeableConcept> categoryList = new ArrayList<CodeableConcept>();
     			int noOfCategories = categoryJSON.length();
     			for (int i = 0; i < noOfCategories; i++) {
     				CodeableConcept theCategory = new CodeableConcept();
     				if (!categoryJSON.getJSONObject(i).isNull("coding")) {
     					JSONArray codingJSON = categoryJSON.getJSONObject(i).getJSONArray("coding");
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
     					theCategory.setCoding(codingList);
     				}

     				if (!categoryJSON.getJSONObject(i).isNull("text")) {
     					theCategory.setText(categoryJSON.getJSONObject(i).getString("text"));
     				}
     				categoryList.add(theCategory);
     			}
     			documentReference.setCategory(categoryList);
     		}
     		
     		//Set Type
    		if (!documentReferenceJSON.isNull("type")) {
    			JSONObject typeJSON = documentReferenceJSON.getJSONObject("type");
    			CodeableConcept theType = new CodeableConcept();

    			if (!(typeJSON.isNull("coding"))) {
    				JSONArray physicalTypeCoding = typeJSON.getJSONArray("coding");
    				int noOfphysicalTypes = physicalTypeCoding.length();
    				List<Coding> physicalTypeCodingList = new ArrayList<Coding>();

    				for (int i = 0; i < noOfphysicalTypes; i++) {
    					Coding physicalCoding = new Coding();
    					if (!(physicalTypeCoding.getJSONObject(i).isNull("system"))) {
    						physicalCoding.setSystem(physicalTypeCoding.getJSONObject(i).getString("system"));
    					}
    					if (!(physicalTypeCoding.getJSONObject(i).isNull("code"))) {
    						physicalCoding.setCode(physicalTypeCoding.getJSONObject(i).getString("code"));
    					}
    					if (!(physicalTypeCoding.getJSONObject(i).isNull("display"))) {
    						physicalCoding.setDisplay(physicalTypeCoding.getJSONObject(i).getString("display"));
    					}
    					physicalTypeCodingList.add(physicalCoding);
    				}
    				theType.setCoding(physicalTypeCodingList);
    			}
    			documentReference.setType(theType);
    		}
    		
    		 // set securityLabel
     		if (!documentReferenceJSON.isNull("securityLabel")) {
     			JSONArray categoryJSON = documentReferenceJSON.getJSONArray("securityLabel");
     			List<CodeableConcept> securityLabelList = new ArrayList<CodeableConcept>();
     			int noOfSecurityLabels = categoryJSON.length();
     			for (int i = 0; i < noOfSecurityLabels; i++) {
     				CodeableConcept theSecurityLabel = new CodeableConcept();
     				if (!categoryJSON.getJSONObject(i).isNull("coding")) {
     					JSONArray codingJSON = categoryJSON.getJSONObject(i).getJSONArray("coding");
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
     					theSecurityLabel.setCoding(codingList);
     				}

     				if (!categoryJSON.getJSONObject(i).isNull("text")) {
     					theSecurityLabel.setText(categoryJSON.getJSONObject(i).getString("text"));
     				}
     				securityLabelList.add(theSecurityLabel);
     			}
     			documentReference.setSecurityLabel(securityLabelList);
     		}
     		
     		//set date
          	if (!documentReferenceJSON.isNull("date")) {
          		Date date = CommonUtil.convertStringToDate(documentReferenceJSON.getString("date"));
          		documentReference.setDate(date);
    		}
          	
          	//set context
          	if (!documentReferenceJSON.isNull("context")) {
				JSONObject contextJSON = documentReferenceJSON.getJSONObject("context");
				DocumentReferenceContextComponent theContext = new DocumentReferenceContextComponent();
				
				if (!contextJSON.isNull("encounter")) {
					JSONArray encounterJSON = contextJSON.getJSONArray("encounter");
					List<Reference> encounterList = new ArrayList<Reference>();
					int noOfEncounter = encounterJSON.length();
					
					for (int i = 0; i < noOfEncounter; i++) {
						Reference theEncounter = new Reference();
					if (!encounterJSON.getJSONObject(i).isNull("reference")) {
						theEncounter.setReference(encounterJSON.getJSONObject(i).getString("reference"));
					}
					if (!encounterJSON.getJSONObject(i).isNull("type")) {
						theEncounter.setType(encounterJSON.getJSONObject(i).getString("type"));
					}
					if (!encounterJSON.getJSONObject(i).isNull("display")) {
						theEncounter.setDisplay(encounterJSON.getJSONObject(i).getString("display"));
					}
					encounterList.add(theEncounter);
				}
				theContext.setEncounter(encounterList);
				}
				if (!contextJSON.isNull("event")) {
					JSONArray eventJSON = contextJSON.getJSONArray("event");
					List<CodeableConcept> eventList = new ArrayList<CodeableConcept>();
					int noOfEvents = eventJSON.length();
					for (int i = 0; i < noOfEvents; i++) {
						CodeableConcept theEvent = new CodeableConcept();
					
						if (!eventJSON.getJSONObject(i).isNull("coding")) {
							JSONArray codingJSON = eventJSON.getJSONObject(i).getJSONArray("coding");
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
							theEvent.setCoding(codingList);
						}
						if (!eventJSON.getJSONObject(i).isNull("text")) {
							theEvent.setText(eventJSON.getJSONObject(i).getString("text"));
						}
						eventList.add(theEvent);
					}
					theContext.setEvent(eventList);
				}
				if (!contextJSON.isNull("sourcePatientInfo")) {
					JSONObject sourcePatientInfoJSON = contextJSON.getJSONObject("sourcePatientInfo");
					Reference theReference = new Reference();
					if (!sourcePatientInfoJSON.isNull("reference")) {
						theReference.setReference(sourcePatientInfoJSON.getString("reference"));
					}
					if (!sourcePatientInfoJSON.isNull("type")) {
						theReference.setType(sourcePatientInfoJSON.getString("type"));
					}
					if (!sourcePatientInfoJSON.isNull("display")) {
						theReference.setDisplay(sourcePatientInfoJSON.getString("display"));
					}
					theContext.setSourcePatientInfo(theReference);
				}
				
				if (!contextJSON.isNull("period")) {
					JSONObject periodJSON = contextJSON.getJSONObject("period");
					Period thePeriod = new Period();
					if (!periodJSON.isNull("start")) {
						thePeriod.setStart(CommonUtil.convertStringToDate(periodJSON.getString("start")));
					}
					if (!periodJSON.isNull("end")) {
						thePeriod.setEnd(CommonUtil.convertStringToDate(periodJSON.getString("end")));
					}
					theContext.setPeriod(thePeriod);
				}
				
				if (!contextJSON.isNull("facilityType")) {
					JSONObject facilityTypeJSON = contextJSON.getJSONObject("facilityType");
					CodeableConcept theFacilityType = new CodeableConcept();
					
					if (!facilityTypeJSON.isNull("coding")) {
						JSONArray codingJSON = facilityTypeJSON.getJSONArray("coding");
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
						theFacilityType.setCoding(codingList);
					}
					if (!facilityTypeJSON.isNull("text")) {
						theFacilityType.setText(facilityTypeJSON.getString("text"));
					}
					theContext.setFacilityType(theFacilityType);
				}
				
				if (!contextJSON.isNull("practiceSetting")) {
					JSONObject practiceSettingJSON = contextJSON.getJSONObject("practiceSetting");
					CodeableConcept thePracticeSetting = new CodeableConcept();
					
					if (!practiceSettingJSON.isNull("coding")) {
						JSONArray codingJSON = practiceSettingJSON.getJSONArray("coding");
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
						thePracticeSetting.setCoding(codingList);
					}
					if (!practiceSettingJSON.isNull("text")) {
						thePracticeSetting.setText(practiceSettingJSON.getString("text"));
					}
					theContext.setPracticeSetting(thePracticeSetting);
				}
				if (!contextJSON.isNull("related")) {
					JSONArray relatedJSON = contextJSON.getJSONArray("related");
					List<Reference> relatedrList = new ArrayList<Reference>();
					int noOfEncounter = relatedJSON.length();
					
					for (int i = 0; i < noOfEncounter; i++) {
						Reference theRelated = new Reference();
					if (!relatedJSON.getJSONObject(i).isNull("reference")) {
						theRelated.setReference(relatedJSON.getJSONObject(i).getString("reference"));
					}
					if (!relatedJSON.getJSONObject(i).isNull("type")) {
						theRelated.setType(relatedJSON.getJSONObject(i).getString("type"));
					}
					if (!relatedJSON.getJSONObject(i).isNull("identifier")) {
						JSONObject identifierJSON = relatedJSON.getJSONObject(i).getJSONObject("identifier");
						Identifier theIdentifier = new Identifier();
						if (!identifierJSON.isNull("system")) {
							theIdentifier.setSystem(identifierJSON.getString("system"));
						}
						if (!identifierJSON.isNull("value")) {
							theIdentifier.setValue(identifierJSON.getString("value"));
						}
						theRelated.setIdentifier(theIdentifier);
					}
					if (!relatedJSON.getJSONObject(i).isNull("display")) {
						theRelated.setDisplay(relatedJSON.getJSONObject(i).getString("display"));
					}
					relatedrList.add(theRelated);
				}
				theContext.setRelated(relatedrList);
				}
				documentReference.setContext(theContext);
			}
          	
		//set author
          	if (!documentReferenceJSON.isNull("author")) {
				JSONArray authorJSON = documentReferenceJSON.getJSONArray("author");
				List<Reference> authorList = new ArrayList<Reference>();
				int noOfAuthors = authorJSON.length();
				
				for (int k = 0; k < noOfAuthors; k++) {
					Reference theReference  = new Reference();
					
					if (!authorJSON.getJSONObject(k).isNull("reference")) {
						theReference.setReference(authorJSON.getJSONObject(k).getString("reference"));
					}
					if (!authorJSON.getJSONObject(k).isNull("type")) {
						theReference.setType(authorJSON.getJSONObject(k).getString("type"));
					}
					if (!authorJSON.getJSONObject(k).isNull("display")) {
						theReference.setDisplay(authorJSON.getJSONObject(k).getString("display"));
					}
					authorList.add(theReference);
				}
				documentReference.setAuthor(authorList);
			}
          	
          //set authenticator
          	if (!documentReferenceJSON.isNull("authenticator")) {
				JSONObject authenticatorJSON = documentReferenceJSON.getJSONObject("authenticator");
				
					Reference theReference  = new Reference();
					
					if (!authenticatorJSON.isNull("reference")) {
						theReference.setReference(authenticatorJSON.getString("reference"));
					}
					if (!authenticatorJSON.isNull("type")) {
						theReference.setType(authenticatorJSON.getString("type"));
					}
					if (!authenticatorJSON.isNull("display")) {
						theReference.setDisplay(authenticatorJSON.getString("display"));
					}
					documentReference.setAuthenticator(theReference);
				}
				
          	 //set custodian
          	if (!documentReferenceJSON.isNull("custodian")) {
				JSONObject custodianJSON = documentReferenceJSON.getJSONObject("custodian");
				
					Reference theReference  = new Reference();
					
					if (!custodianJSON.isNull("reference")) {
						theReference.setReference(custodianJSON.getString("reference"));
					}
					if (!custodianJSON.isNull("type")) {
						theReference.setType(custodianJSON.getString("type"));
					}
					if (!custodianJSON.isNull("display")) {
						theReference.setDisplay(custodianJSON.getString("display"));
					}
					documentReference.setCustodian(theReference);
				}
				 	
         // set relatesTo
     		if (!documentReferenceJSON.isNull("relatesTo")) {
     			JSONArray relatesToJSON = documentReferenceJSON.getJSONArray("relatesTo");
     			List<DocumentReferenceRelatesToComponent> relatesToList = new ArrayList<DocumentReferenceRelatesToComponent>();
     			int noOfRelatesTo = relatesToJSON.length();
     			
     			for (int i = 0; i < noOfRelatesTo; i++) {
     				DocumentReferenceRelatesToComponent theRelatesToComponent = new DocumentReferenceRelatesToComponent();
     				
     				if (!relatesToJSON.getJSONObject(i).isNull("code")) {
     					theRelatesToComponent.setCode(DocumentRelationshipType.fromCode(relatesToJSON.getJSONObject(i).getString("code")));
     				}
     				if (!relatesToJSON.getJSONObject(i).isNull("target")) {
     					JSONObject targetJSON = relatesToJSON.getJSONObject(i).getJSONObject("target");
     					
     					Reference theReference  = new Reference();
    					
    					if (!targetJSON.isNull("reference")) {
    						theReference.setReference(targetJSON.getString("reference"));
    					}
    					if (!targetJSON.isNull("type")) {
    						theReference.setType(targetJSON.getString("type"));
    					}
    					if (!targetJSON.isNull("display")) {
    						theReference.setDisplay(targetJSON.getString("display"));
    					}
    					
    					theRelatesToComponent.setTarget(theReference);
     				}
     				relatesToList.add(theRelatesToComponent);
     			}
     			documentReference.setRelatesTo(relatesToList);
     		}
     		
     		//description
     		if (!documentReferenceJSON.isNull("description")) {
				documentReference.setDescription(documentReferenceJSON.getString("description"));
			}
     		
     		//set content
     		if (!documentReferenceJSON.isNull("content")) {
     			JSONArray contentJSON = documentReferenceJSON.getJSONArray("content");
				List<DocumentReferenceContentComponent> contentList  = new ArrayList<DocumentReference.DocumentReferenceContentComponent>();
				int noOfContents = contentJSON.length();
				
				for (int i = 0; i < noOfContents; i++) {
					DocumentReferenceContentComponent theContent = new DocumentReferenceContentComponent();
					if (!contentJSON.getJSONObject(i).isNull("attachment")) {
						JSONObject attachmentJSON = contentJSON.getJSONObject(i).getJSONObject("attachment");
						Attachment theAttachment = new Attachment();
						if (!attachmentJSON.isNull("contentType")) {
							theAttachment.setContentType(attachmentJSON.getString("contentType"));
						}

						if (!attachmentJSON.isNull("data")) {
							theAttachment.setData(Base64.getDecoder().decode(attachmentJSON.getString("data")));
						}

						if (!attachmentJSON.isNull("language")) {
							theAttachment.setLanguage(attachmentJSON.getString("language"));
						}
						if (!attachmentJSON.isNull("url")) {
							theAttachment.setUrl(attachmentJSON.getString("url"));
						}
						if (!attachmentJSON.isNull("size")) {
							theAttachment.setSize(attachmentJSON.getInt("size"));
						}
					/*
					 * if (!attachmentJSON.isNull("hash")) {
					 * theAttachment.setHash(attachmentJSON.getString("hash")); }
					 */
						if (!attachmentJSON.isNull("title")) {
							theAttachment.setTitle(attachmentJSON.getString("title"));
						}
						if (!attachmentJSON.isNull("creation")) {
							theAttachment.setCreation(CommonUtil.convertStringToDate(attachmentJSON.getString("creation")));
						}
						theContent.setAttachment(theAttachment);
					}
					if (!contentJSON.getJSONObject(i).isNull("format")) {
     					JSONObject formatJSON = contentJSON.getJSONObject(i).getJSONObject("format");
     					
     						Coding theFormat = new Coding();

     						if (!formatJSON.isNull("system")) {
     							theFormat.setSystem(formatJSON.getString("system"));
     						}
     						if (!formatJSON.isNull("code")) {
     							theFormat.setCode(formatJSON.getString("code"));
     						}
     						if (!formatJSON.isNull("display")) {
     							theFormat.setDisplay(formatJSON.getString("display"));
     						}
     						theContent.setFormat(theFormat);
     					}
						contentList.add(theContent);
					}
					documentReference.setContent(contentList);
     		}
     		//set docStatus
     		if (!documentReferenceJSON.isNull("docStatus")) {
				documentReference.setDocStatus(ReferredDocumentStatus.fromCode(documentReferenceJSON.getString("docStatus")));
			}
     		
     		//set status
     		if (!documentReferenceJSON.isNull("status")) {
     			documentReference.setStatus(DocumentReferenceStatus.fromCode(documentReferenceJSON.getString("status"))); }
		     
        return documentReference;
    }
}
