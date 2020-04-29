package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceEligibilityComponent;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceNotAvailableComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafHealthcareService;
import org.sitenv.spring.service.HealthcareServiceService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class HealthcareServiceResourceProvider implements IResourceProvider {
	
	public static final String RESOURCE_TYPE = "HealthcareService";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    HealthcareServiceService service;
    
    public HealthcareServiceResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (HealthcareServiceService) context.getBean("healthcareServiceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return HealthcareService.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/HealthcareService/1/_history/3.0
	 * @param theId : Id of the HealthcareService
	 * @return : Object of HealthcareService information
	 */
	@Read(version=true)
    public HealthcareService readOrVread(@IdParam IdType theId) {
		String id;
		DafHealthcareService dafHealthcareService;
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
			dafHealthcareService = service.getHealthcareServiceByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			dafHealthcareService = service.getHealthcareServiceById(id);
		}
		return createHealthcareServiceObject(dafHealthcareService);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=HealthcareService.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/HealthcareService/1/_history
	 * @param theId : ID of the HealthcareService
	 * @return : List of HealthcareService's
	 */
	@History()
    public List<HealthcareService> getHealthcareServiceHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafHealthcareService> dafHealthcareServiceList = service.getHealthcareServiceHistoryById(id);
        
        List<HealthcareService> healthcareServiceList = new ArrayList<HealthcareService>();
        for (DafHealthcareService dafHealthcareService : dafHealthcareServiceList) {
        	healthcareServiceList.add(createHealthcareServiceObject(dafHealthcareService));
        }
        
        return healthcareServiceList;
	}

	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theName
	 * @param theEndpoint
	 * @param theOrganization
	 * @param theTelecom
	 * @param theLocation
	 * @param theCategory
	 * @param theCharacteristic
	 * @param theActive
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
    @Search()
    public IBundleProvider search(
    		javax.servlet.http.HttpServletRequest theServletRequest,

            @Description(shortDefinition = "The resource identity")
            @OptionalParam(name = HealthcareService.SP_RES_ID)
            TokenAndListParam theId,

            @Description(shortDefinition = "External identifiers for this item")
            @OptionalParam(name = HealthcareService.SP_IDENTIFIER)
            TokenAndListParam theIdentifier,

            @Description(shortDefinition = "A portion of the Healthcare service name")
            @OptionalParam(name = HealthcareService.SP_NAME)
            StringAndListParam theName,

            @Description(shortDefinition = "Technical endpoints providing access to services operated for the location")
            @OptionalParam(name = HealthcareService.SP_ENDPOINT)
            ReferenceAndListParam theEndpoint,

            @Description(shortDefinition = "The organization that provides this Healthcare Service")
            @OptionalParam(name = HealthcareService.SP_ORGANIZATION)
            ReferenceAndListParam theOrganization,

            @Description(shortDefinition = "Contacts related to the healthcare service")
            @OptionalParam(name = "telecom")
            StringAndListParam theTelecom,

            @Description(shortDefinition = "The location of the Healthcare Service")
            @OptionalParam(name = HealthcareService.SP_LOCATION)
            ReferenceAndListParam theLocation,
            
            @Description(shortDefinition="Service Category of the Healthcare Service")
			@OptionalParam(name = HealthcareService.SP_SERVICE_CATEGORY)
			TokenAndListParam theCategory, 

  			@Description(shortDefinition="One of the HealthcareService's characteristics")
			@OptionalParam(name = HealthcareService.SP_CHARACTERISTIC)
            TokenAndListParam theCharacteristic, 

            @Description(shortDefinition = "The Healthcare Service is currently marked as active")
            @OptionalParam(name = HealthcareService.SP_ACTIVE)
            TokenAndListParam theActive,
            
            @Description(shortDefinition = "The type of service provided by this healthcare service")
            @OptionalParam(name = HealthcareService.SP_SERVICE_TYPE)
            TokenAndListParam theServiceType,
            
            @Description(shortDefinition = "Location(s) service is intended for/available to")
            @OptionalParam(name = HealthcareService.SP_COVERAGE_AREA)
            ReferenceAndListParam theCoverageArea,
            
            @Description(shortDefinition = "The specialty of the service provided by this healthcare service")
            @OptionalParam(name = HealthcareService.SP_SPECIALTY)
            TokenAndListParam theSpecialty,
 
            @IncludeParam(allow = {"*"})
            Set<Include> theIncludes,

            @Sort
            SortSpec theSort,

            @Count
            Integer theCount) {

            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(HealthcareService.SP_RES_ID, theId);
            paramMap.add(HealthcareService.SP_IDENTIFIER, theIdentifier);
            paramMap.add(HealthcareService.SP_NAME, theName);
            paramMap.add(HealthcareService.SP_ORGANIZATION, theOrganization);
            paramMap.add(HealthcareService.SP_ENDPOINT, theEndpoint);
            paramMap.add("telecom", theTelecom);
            paramMap.add(HealthcareService.SP_LOCATION, theLocation);
            paramMap.add(HealthcareService.SP_CHARACTERISTIC, theCharacteristic);
            paramMap.add(HealthcareService.SP_ACTIVE, theActive);
            paramMap.add(HealthcareService.SP_SERVICE_TYPE, theServiceType);
            paramMap.add(HealthcareService.SP_SERVICE_CATEGORY, theCategory);
            paramMap.add(HealthcareService.SP_COVERAGE_AREA, theCoverageArea);
            paramMap.add(HealthcareService.SP_SPECIALTY, theSpecialty);
            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<DafHealthcareService> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> healthcareServiceList = new ArrayList<IBaseResource>();
                    for(DafHealthcareService dafHealthcareService : results){
                    	healthcareServiceList.add(createHealthcareServiceObject(dafHealthcareService));
                    }
                    return healthcareServiceList;
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
     * @param dafHealthcareService : DafDocumentReference HealthcareService object
     * @return : DocumentReference HealthcareService object
     */
    private HealthcareService createHealthcareServiceObject(DafHealthcareService dafHealthcareService) {
    	HealthcareService healthcareService = new HealthcareService();
        JSONObject healthcareServiceJSON = new JSONObject(dafHealthcareService.getData());

        // Set version
        if(!(healthcareServiceJSON.isNull("meta"))) {
        	if(!(healthcareServiceJSON.getJSONObject("meta").isNull("versionId"))) {
                healthcareService.setId(new IdType(RESOURCE_TYPE, healthcareServiceJSON.getString("id") + "", healthcareServiceJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				healthcareService.setId(new IdType(RESOURCE_TYPE, healthcareServiceJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
            healthcareService.setId(new IdType(RESOURCE_TYPE, healthcareServiceJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(healthcareServiceJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = healthcareServiceJSON.getJSONArray("identifier");
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
        	healthcareService.setIdentifier(identifiers);
        }
        
        //set active
        if(!(healthcareServiceJSON.isNull("active"))) {
            healthcareService.setActive(healthcareServiceJSON.getBoolean("active"));
        }
        
        //set providedBy
        if(!(healthcareServiceJSON.isNull("providedBy"))) {
        	JSONObject providedByJSON = healthcareServiceJSON.getJSONObject("providedBy");
        	Reference theProvidedBy = new Reference();
        	if(!(providedByJSON.isNull("reference"))) {
            	theProvidedBy.setReference(providedByJSON.getString("reference"));
        	}
        	if(!(providedByJSON.isNull("display"))) {
            	theProvidedBy.setDisplay(providedByJSON.getString("display"));
        	}
        	if(!(providedByJSON.isNull("type"))) {
            	theProvidedBy.setType(providedByJSON.getString("type"));
        	} 
        	healthcareService.setProvidedBy(theProvidedBy);
        }
        
        //Set category
        if(!(healthcareServiceJSON.isNull("category"))) {
	    	JSONArray categoryJSON = healthcareServiceJSON.getJSONArray("category");
	    	int noOfCategories = categoryJSON.length();
	    	List<CodeableConcept> categoryList = new ArrayList<CodeableConcept>();
	    	for(int i = 0; i < noOfCategories; i++) {
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
				
				if(!(categoryJSON.getJSONObject(i).isNull("text"))) {
					theCategory.setText(categoryJSON.getJSONObject(i).getString("text"));
				}
				categoryList.add(theCategory);
			}
			healthcareService.setCategory(categoryList);
        }
	    
        //Set type
        if(!(healthcareServiceJSON.isNull("type"))) {
	    	JSONArray typeJSON = healthcareServiceJSON.getJSONArray("type");
	    	int noOfTypes = typeJSON.length();
	    	List<CodeableConcept> typeList = new ArrayList<CodeableConcept>();		
			for(int i = 0; i < noOfTypes; i++) {
				CodeableConcept theType = new CodeableConcept();
				if(!(typeJSON.getJSONObject(i).isNull("coding"))) {	
					JSONArray codingJSON = typeJSON.getJSONObject(i).getJSONArray("coding");
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
					theType.setCoding(codingList);
				}
				if(!(typeJSON.getJSONObject(i).isNull("text"))) {
					theType.setText(typeJSON.getJSONObject(i).getString("text"));
				}	
				typeList.add(theType);
			}
			healthcareService.setType(typeList);
        }
        
        //Set specialty
        if(!(healthcareServiceJSON.isNull("specialty"))) {
	    	JSONArray specialtyJSON = healthcareServiceJSON.getJSONArray("specialty");
	    	int noOfspecialty = specialtyJSON.length();
	    	List<CodeableConcept> specialtyList = new ArrayList<CodeableConcept>();
			for(int i = 0; i < noOfspecialty; i++) {
				CodeableConcept theSpecialty = new CodeableConcept();
				if(!(specialtyJSON.getJSONObject(i).isNull("coding"))) {
					
					JSONArray codingJSON = specialtyJSON.getJSONObject(i).getJSONArray("coding");
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
					theSpecialty.setCoding(codingList);
				}
				specialtyList.add(theSpecialty);
			}
			healthcareService.setSpecialty(specialtyList);
        }
        
        //Set location
        if(!(healthcareServiceJSON.isNull("location"))) {
        	JSONArray locationJSON = healthcareServiceJSON.getJSONArray("location"); 
        	int noOfLocations = locationJSON.length();
        	List<Reference> locationList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfLocations; i++) {
        		Reference theLocation = new Reference();
        		if(!(locationJSON.getJSONObject(i).isNull("reference"))) {
        			theLocation.setReference(locationJSON.getJSONObject(i).getString("reference"));
        		}
        		if(!(locationJSON.getJSONObject(i).isNull("display"))) {
        			theLocation.setDisplay(locationJSON.getJSONObject(i).getString("display"));
        		}
        		if(!(locationJSON.getJSONObject(i).isNull("type"))) {
        			theLocation.setType(locationJSON.getJSONObject(i).getString("type"));
        		}
        		locationList.add(theLocation);
        	}
        	healthcareService.setLocation(locationList);
        }
        
        //Set name
        if(!(healthcareServiceJSON.isNull("name"))) {
        	healthcareService.setName(healthcareServiceJSON.getString("name"));
        }
        
        //Set comment
        if(!(healthcareServiceJSON.isNull("comment"))) {
        	healthcareService.setComment(healthcareServiceJSON.getString("comment"));
        }
        
        //Set extraDetails
        if(!(healthcareServiceJSON.isNull("extraDetails"))) {
        	healthcareService.setExtraDetails(healthcareServiceJSON.getString("extraDetails"));
        }
        
        //Set telecom
        if(!(healthcareServiceJSON.isNull("telecom"))) {
        	JSONArray telecomJSON = healthcareServiceJSON.getJSONArray("telecom");
        	int noOfTelecoms = telecomJSON.length();
        	List<ContactPoint> contactPointDtList = new ArrayList<ContactPoint>();
        	for(int t = 0; t < noOfTelecoms; t++) {
            	ContactPoint phoneContact = new ContactPoint();
            	if(!(telecomJSON.getJSONObject(t).isNull("system"))) {
                    phoneContact.setSystem(ContactPoint.ContactPointSystem.fromCode(telecomJSON.getJSONObject(t).getString("system")));
            	}
            	if(!(telecomJSON.getJSONObject(t).isNull("use"))) {
                    phoneContact.setUse(ContactPoint.ContactPointUse.fromCode(telecomJSON.getJSONObject(t).getString("use")));
            	}
            	
            	if(!(telecomJSON.getJSONObject(t).isNull("value"))) {
                    phoneContact.setValue(telecomJSON.getJSONObject(t).getString("value"));
            	}
            	
            	if(!(telecomJSON.getJSONObject(t).isNull("rank"))) {
                    phoneContact.setRank(telecomJSON.getJSONObject(t).getInt("rank"));
            	}
            	
            	if(!(telecomJSON.getJSONObject(t).isNull("period"))) {
    				Period contactPeriod = new Period();
            		if(!(telecomJSON.getJSONObject(t).getJSONObject("period").isNull("end"))) {
        				Date contactEDate = CommonUtil.convertStringToDateYear(telecomJSON.getJSONObject(t).getJSONObject("period").getString("end"));
        				contactPeriod.setEnd(contactEDate);
            		}
    				phoneContact.setPeriod(contactPeriod);
            	}
            	
                contactPointDtList.add(phoneContact);
        	}
           healthcareService.setTelecom(contactPointDtList);
        }
        
        //Set coverageArea
        if(!(healthcareServiceJSON.isNull("coverageArea"))) {
        	JSONArray coverageAreaJSON = healthcareServiceJSON.getJSONArray("coverageArea"); 
        	int noOfcoverageAreas = coverageAreaJSON.length();
        	List<Reference> coverageAreaList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfcoverageAreas; i++) {
        		Reference theCoverageArea = new Reference();
        		if(!(coverageAreaJSON.getJSONObject(i).isNull("reference"))) {
        			theCoverageArea.setReference(coverageAreaJSON.getJSONObject(i).getString("reference"));
        		}
        		if(!(coverageAreaJSON.getJSONObject(i).isNull("type"))) {
        			theCoverageArea.setType(coverageAreaJSON.getJSONObject(i).getString("type"));
        		}
        		if(!(coverageAreaJSON.getJSONObject(i).isNull("display"))) {
        			theCoverageArea.setDisplay(coverageAreaJSON.getJSONObject(i).getString("display"));
        		}
        		coverageAreaList.add(theCoverageArea);
        	}
        	healthcareService.setCoverageArea(coverageAreaList);
        }
        
        //Set serviceProvisionCode
        if(!(healthcareServiceJSON.isNull("serviceProvisionCode"))) {
        	JSONArray serviceProvisionCodeJSON = healthcareServiceJSON.getJSONArray("serviceProvisionCode");
	    	int noOfserviceProvisionCode = serviceProvisionCodeJSON.length();
	    	List<CodeableConcept> serviceProvisionCodeList = new ArrayList<CodeableConcept>();
			
			for(int i = 0; i < noOfserviceProvisionCode; i++) {
				CodeableConcept theServiceProvisionCode = new CodeableConcept();

				if(!(serviceProvisionCodeJSON.getJSONObject(i).isNull("coding"))) {
					
					JSONArray codingJSON = serviceProvisionCodeJSON.getJSONObject(i).getJSONArray("coding");
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
					theServiceProvisionCode.setCoding(codingList);
				}
				serviceProvisionCodeList.add(theServiceProvisionCode);
			}
			healthcareService.setServiceProvisionCode(serviceProvisionCodeList);
        }
        
        //Set eligibility
        if(!(healthcareServiceJSON.isNull("eligibility"))) {
        	JSONArray eligibilityJSON = healthcareServiceJSON.getJSONArray("eligibility");
        	int noOfEligibilities = eligibilityJSON.length();
        	List<HealthcareServiceEligibilityComponent> eligibilityList = new ArrayList<HealthcareServiceEligibilityComponent>();
        	
        	for(int i = 0; i < noOfEligibilities; i++) {
        		HealthcareServiceEligibilityComponent theEligibility = new HealthcareServiceEligibilityComponent();
        		if(!(eligibilityJSON.getJSONObject(i).isNull("code"))) {
        			JSONObject codeJSON = eligibilityJSON.getJSONObject(i).getJSONObject("code");
        			CodeableConcept theCode = new CodeableConcept();
        			if(!(codeJSON.isNull("coding"))) {
        				JSONArray codingJSON = codeJSON.getJSONArray("coding");
        				int noOfCodings = codingJSON.length();
        				List<Coding> codingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfCodings; j++) {
        					Coding theCoding = new Coding();
        					if(!(codingJSON.getJSONObject(j).isNull("display"))) {
        						theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
        					}
        					if(!(codingJSON.getJSONObject(j).isNull("system"))) {
        						theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(codingJSON.getJSONObject(j).isNull("code"))) {
        						theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(codingJSON.getJSONObject(j).isNull("version"))) {
        						theCoding.setVersion(codingJSON.getJSONObject(j).getString("version"));
        					}
        					codingList.add(theCoding);
        				}
        				theCode.setCoding(codingList);
        			}
        			theEligibility.setCode(theCode);
        		}
        		eligibilityList.add(theEligibility);
        	}
        	healthcareService.setEligibility(eligibilityList);
        }
        //Set program
        if(!(healthcareServiceJSON.isNull("program"))) {
        	JSONArray programJSON = healthcareServiceJSON.getJSONArray("program");
	    	int noOfPrograms = programJSON.length();
	    	List<CodeableConcept> programList = new ArrayList<CodeableConcept>();
	    	for(int i = 0; i < noOfPrograms; i++) {
				CodeableConcept theProgram = new CodeableConcept();
				if(!(programJSON.getJSONObject(i).isNull("coding"))) {
					JSONArray codingJSON = programJSON.getJSONObject(i).getJSONArray("coding");
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
					theProgram.setCoding(codingList);
				}
				if(!(programJSON.getJSONObject(i).isNull("text"))) {
					theProgram.setText(programJSON.getJSONObject(i).getString("text"));
				}
				programList.add(theProgram);
			}
			healthcareService.setProgram(programList);
        }
        
        //Set characteristic
        if(!(healthcareServiceJSON.isNull("characteristic"))) {
        	JSONArray characteristicJSON = healthcareServiceJSON.getJSONArray("characteristic");
	    	int noOfCharacteristics = characteristicJSON.length();
	    	List<CodeableConcept> characteristicList = new ArrayList<CodeableConcept>();
			for(int i = 0; i < noOfCharacteristics; i++) {
				CodeableConcept theCharacteristic = new CodeableConcept();
				if(!(characteristicJSON.getJSONObject(i).isNull("coding"))) {
					
					JSONArray codingJSON = characteristicJSON.getJSONObject(i).getJSONArray("coding");
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
					theCharacteristic.setCoding(codingList);
				}
				characteristicList.add(theCharacteristic);
			}
			healthcareService.setCharacteristic(characteristicList);
        }
        
        //Set referralMethod
        if(!(healthcareServiceJSON.isNull("referralMethod"))) {
        	JSONArray referralMethodJSON = healthcareServiceJSON.getJSONArray("referralMethod");
	    	int noOfReferralMethods = referralMethodJSON.length();
	    	List<CodeableConcept> referralMethodList = new ArrayList<CodeableConcept>();
			
			for(int i = 0; i < noOfReferralMethods; i++) {
				CodeableConcept theReferralMethod = new CodeableConcept();

				if(!(referralMethodJSON.getJSONObject(i).isNull("coding"))) {
					
					JSONArray codingJSON = referralMethodJSON.getJSONObject(i).getJSONArray("coding");
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
					theReferralMethod.setCoding(codingList);
				}
				
				if(!(referralMethodJSON.getJSONObject(i).isNull("text"))) {
					theReferralMethod.setText(referralMethodJSON.getJSONObject(i).getString("text"));
				}
				referralMethodList.add(theReferralMethod);
			}
			healthcareService.setReferralMethod(referralMethodList);
        }
        
        //Set appointmentRequired
        if(!(healthcareServiceJSON.isNull("appointmentRequired"))) {
            healthcareService.setAppointmentRequired(healthcareServiceJSON.getBoolean("appointmentRequired"));
        }
        
        //Set availableTime
        if(!(healthcareServiceJSON.isNull("availableTime"))) {
        	JSONArray availableTimeJSON = healthcareServiceJSON.getJSONArray("availableTime");
        	int noOfavailableTimes = availableTimeJSON.length();
        	List<HealthcareServiceAvailableTimeComponent> availableTimeList = new ArrayList<HealthcareServiceAvailableTimeComponent>();
        	for(int i = 0; i < noOfavailableTimes; i++) {
        		HealthcareServiceAvailableTimeComponent theAvailableTime = new HealthcareServiceAvailableTimeComponent();
        		if(!(availableTimeJSON.getJSONObject(i).isNull("daysOfWeek"))) {
     
        		}
        		if(!(availableTimeJSON.getJSONObject(i).isNull("allDay"))) {
        			theAvailableTime.setAllDay(availableTimeJSON.getJSONObject(i).getBoolean("allDay"));
        		}
        		if(!(availableTimeJSON.getJSONObject(i).isNull("availableStartTime"))) {
        			theAvailableTime.setAvailableStartTime(availableTimeJSON.getJSONObject(i).getString("availableStartTime"));
        		}
        		if(!(availableTimeJSON.getJSONObject(i).isNull("availableEndTime"))) {
        			theAvailableTime.setAvailableEndTime(availableTimeJSON.getJSONObject(i).getString("availableEndTime"));
        		}
        		availableTimeList.add(theAvailableTime);
        	}
        	healthcareService.setAvailableTime(availableTimeList);
        }
        
        //Set notAvailable
        if(!(healthcareServiceJSON.isNull("notAvailable"))) {
        	JSONArray notAvailableTimeJSON = healthcareServiceJSON.getJSONArray("notAvailable");
        	int noOfNotAavailableTimes = notAvailableTimeJSON.length();
        	List<HealthcareServiceNotAvailableComponent> notAvailableTimeList = new ArrayList<HealthcareServiceNotAvailableComponent>();
        	for(int i = 0; i < noOfNotAavailableTimes; i++) {
        		HealthcareServiceNotAvailableComponent theNotAvailableTime = new HealthcareServiceNotAvailableComponent();
        		if(!(notAvailableTimeJSON.getJSONObject(i).isNull("description"))) {
        			theNotAvailableTime.setDescription(notAvailableTimeJSON.getJSONObject(i).getString("description"));
        		}
        		if(!(notAvailableTimeJSON.getJSONObject(i).isNull("during"))) {
        			Period theDuring = new Period();
        			if(!(notAvailableTimeJSON.getJSONObject(i).getJSONObject("during").isNull("start"))) {
        				Date start = CommonUtil.convertStringToDate(notAvailableTimeJSON.getJSONObject(i).getJSONObject("during").getString("start"));
        				theDuring.setEnd(start);
            		}
        			if(!(notAvailableTimeJSON.getJSONObject(i).getJSONObject("during").isNull("end"))) {
        				Date end = CommonUtil.convertStringToDate(notAvailableTimeJSON.getJSONObject(i).getJSONObject("during").getString("end"));
        				theDuring.setEnd(end);
            		}
        			theNotAvailableTime.setDuring(theDuring);
        		}
        		notAvailableTimeList.add(theNotAvailableTime);
        	}
        	healthcareService.setNotAvailable(notAvailableTimeList);
        }
        
        //Set availabilityExceptions
        if(!(healthcareServiceJSON.isNull("availabilityExceptions"))) {
        	healthcareService.setAvailabilityExceptions(healthcareServiceJSON.getString("availabilityExceptions"));
        }
        
        //Set endpoint
        if(!(healthcareServiceJSON.isNull("endpoint"))) {
        	JSONArray endPointJSON = healthcareServiceJSON.getJSONArray("endpoint");
        	int noOfEndPoints = endPointJSON.length();
        	List<Reference> endPointList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfEndPoints; i++) {
            	Reference theEndPoint = new Reference();
        		if(!(endPointJSON.getJSONObject(i).isNull("reference"))) {
        			theEndPoint.setReference(endPointJSON.getJSONObject(i).getString("reference"));
        		}
        		if(!(endPointJSON.getJSONObject(i).isNull("type"))) {
        			theEndPoint.setType(endPointJSON.getJSONObject(i).getString("type"));
        		}
        		if(!(endPointJSON.getJSONObject(i).isNull("display"))) {
        			theEndPoint.setDisplay(endPointJSON.getJSONObject(i).getString("display"));
        		}
        		endPointList.add(theEndPoint);
        	}
        	healthcareService.setEndpoint(endPointList);
        }
        return healthcareService;
    }
}
