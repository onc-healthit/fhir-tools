package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.service.PractitionerService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PractitionerResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Practitioner";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    PractitionerService service;

    public PractitionerResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (PractitionerService) context.getBean("practitionerService");
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Practitioner.class;
	}
	
	/**
	 * The "@Read" annotation indicate
	 * s that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Practitioner/1/_history/4
	 * @param theId: ID of practitioner
	 * @return : Practitioner object
	 */
	@Read(version=true)
    public Practitioner readOrVread(@IdParam IdType theId) {
		String id;
		DafPractitioner dafPractitioner;
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
			dafPractitioner = service.getPractitionerByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			dafPractitioner = service.getPractitionerById(id);
		}
		
		return createPractitionerObject(dafPractitioner);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=Practitioner.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Practitioner/1/_history
	 * @param theId: ID of the practitioner
	 * @return : List of Practitioner's
	 */
	@History()
    public List<Practitioner> getPractitionerHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafPractitioner> dafPractitionerList = service.getPractitionerHistoryById(id);

        List<Practitioner> practitionerList = new ArrayList<Practitioner>();
        for (DafPractitioner dafPractitioner : dafPractitionerList) {
        	practitionerList.add(createPractitionerObject(dafPractitioner));
        }
        
        return practitionerList;
	}
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theName
	 * @param theFamily
	 * @param theGiven
	 * @param theTelecom
	 * @param theAddress
	 * @param theAddressCity
	 * @param theAddressState
	 * @param theAddressPostalcode
	 * @param theAddressCountry
	 * @param theGender
	 * @param theActive
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
            @OptionalParam(name = Practitioner.SP_RES_ID)
            TokenAndListParam theId,

            @Description(shortDefinition = "A Practitioner identifier")
            @OptionalParam(name = Practitioner.SP_IDENTIFIER)
            TokenAndListParam theIdentifier,

            @Description(shortDefinition = "A portion of either family or given name of the Practitioner")
            @OptionalParam(name = Practitioner.SP_NAME)
            StringAndListParam theName,

            @Description(shortDefinition = "A portion of the family name")
            @OptionalParam(name = Practitioner.SP_FAMILY)
            StringAndListParam theFamily,

            @Description(shortDefinition = "A portion of the given name of the Practitioner")
            @OptionalParam(name = Practitioner.SP_GIVEN)
            StringAndListParam theGiven,

            @Description(shortDefinition = "One of the languages that the practitioner can communicate with")
            @OptionalParam(name = Practitioner.SP_COMMUNICATION)
            TokenAndListParam theCommunication,

            @Description(shortDefinition = "The value in any kind of telecom details of the Practitioner")
            @OptionalParam(name = Practitioner.SP_TELECOM)
            StringAndListParam theTelecom,

            @Description(shortDefinition = "An address in any kind of address/part of the Practitioner")
            @OptionalParam(name = Practitioner.SP_ADDRESS)
            StringAndListParam theAddress,
            
            @Description(shortDefinition="A city specified in an address")
			@OptionalParam(name = Practitioner.SP_ADDRESS_CITY)
			StringAndListParam theAddressCity, 
  
			@Description(shortDefinition="A state specified in an address")
			@OptionalParam(name = Practitioner.SP_ADDRESS_STATE)
			StringAndListParam theAddressState, 
  
			@Description(shortDefinition="A postalCode specified in an address")
			@OptionalParam(name = Practitioner.SP_ADDRESS_POSTALCODE)
			StringAndListParam theAddressPostalcode, 
  
			@Description(shortDefinition="A country specified in an address")
			@OptionalParam(name = Practitioner.SP_ADDRESS_COUNTRY)
			StringAndListParam theAddressCountry, 

            @Description(shortDefinition = "Gender of the Practitioner")
            @OptionalParam(name = Practitioner.SP_GENDER)
            TokenAndListParam theGender,

            @Description(shortDefinition = "A value in an email contact.Practitioner.telecom.where(system='email')")
            @OptionalParam(name = Practitioner.SP_EMAIL)
            StringAndListParam theEmail,

            @Description(shortDefinition = "A use code specified in an address.Practitioner.address.use")
            @OptionalParam(name = Practitioner.SP_ADDRESS_USE)
            TokenAndListParam theAddressUse,

            @Description(shortDefinition = "Whether the Practitioner record is active")
            @OptionalParam(name = Practitioner.SP_ACTIVE)
            TokenAndListParam theActive,

            @Description(shortDefinition = "A value in a phone contact. Path: Practitioner.telecom(system=phone)")
            @OptionalParam(name = Practitioner.SP_PHONE)
            StringAndListParam thePhone,

            @IncludeParam(allow = {"*"})
            Set<Include> theIncludes,

			@IncludeParam(reverse=true, allow= {"*"})
			Set<Include> theRevIncludes,

            @Sort
            SortSpec theSort,

            @Count
            Integer theCount) {

	            SearchParameterMap paramMap = new SearchParameterMap();
	            paramMap.add(Practitioner.SP_RES_ID, theId);
	            paramMap.add(Practitioner.SP_IDENTIFIER, theIdentifier);
	            paramMap.add(Practitioner.SP_NAME, theName);
	            paramMap.add(Practitioner.SP_FAMILY, theFamily);
	            paramMap.add(Practitioner.SP_GIVEN, theGiven);
	            paramMap.add(Practitioner.SP_COMMUNICATION, theCommunication);
	            paramMap.add(Practitioner.SP_TELECOM, theTelecom);
	            paramMap.add(Practitioner.SP_ADDRESS, theAddress);
	            paramMap.add(Practitioner.SP_ADDRESS_CITY, theAddressCity);
	            paramMap.add(Practitioner.SP_ADDRESS_STATE, theAddressState);
	            paramMap.add(Practitioner.SP_ADDRESS_POSTALCODE, theAddressPostalcode);
	            paramMap.add(Practitioner.SP_ADDRESS_COUNTRY, theAddressCountry);
	            paramMap.add(Practitioner.SP_GENDER, theGender);
	            paramMap.add(Practitioner.SP_EMAIL, theEmail);
	            paramMap.add(Practitioner.SP_ADDRESS_USE, theAddressUse);
	            paramMap.add(Practitioner.SP_ACTIVE, theActive);
	            paramMap.add(Practitioner.SP_PHONE, thePhone);
	            paramMap.setIncludes(theIncludes);
	            paramMap.setSort(theSort);
	            paramMap.setCount(theCount);
	            
	            final List<DafPractitioner> results = service.search(paramMap);
	
	            return new IBundleProvider() {
	                final InstantDt published = InstantDt.withCurrentTime();
	                @Override
	                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
	                    List<IBaseResource> practitionerList = new ArrayList<IBaseResource>();
						List<String> ids = new ArrayList<String>();
						for(DafPractitioner dafPractitioner : results){
							Practitioner practitioner = createPractitionerObject(dafPractitioner);
							practitionerList.add(practitioner);
							ids.add(((IdType)practitioner.getIdElement()).getResourceType()+"/"+((IdType)practitioner.getIdElement()).getIdPart());
						}
						if(theRevIncludes.size() >0 ){
							practitionerList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
						}

	                    return practitionerList;
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
     */
	private Practitioner createPractitionerObject(DafPractitioner dafPractitioner) {
		Practitioner practitioner = new Practitioner();
        JSONObject practitionerJSON = new JSONObject(dafPractitioner.getData());

        // Set version
        if(!(practitionerJSON.isNull("meta"))) {
        	if(!(practitionerJSON.getJSONObject("meta").isNull("versionId"))) {
                practitioner.setId(new IdType(RESOURCE_TYPE, practitionerJSON.getString("id") + "", practitionerJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				practitioner.setId(new IdType(RESOURCE_TYPE, practitionerJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
            practitioner.setId(new IdType(RESOURCE_TYPE, practitionerJSON.getString("id") + "", VERSION_ID));
        }
        
      //Set identifier
        if(!(practitionerJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = practitionerJSON.getJSONArray("identifier");
        	int noOfIdentifiers = identifierJSON.length();
        	List<Identifier> identifiers = new ArrayList<Identifier>();
        	for(int i = 0; i < noOfIdentifiers; i++) {
            	Identifier theIdentifier = new Identifier();
        		if(!(identifierJSON.getJSONObject(i).isNull("use"))) {
                	theIdentifier.setUse(Identifier.IdentifierUse.fromCode(identifierJSON.getJSONObject(i).getString("use")));	
            	}
        		
        		if(!(identifierJSON.getJSONObject(i).isNull("type"))) {
        			CodeableConcept identifierCodeableConcept = new CodeableConcept();
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("type").isNull("coding"))) {
            			JSONArray iCodingJSON = identifierJSON.getJSONObject(i).getJSONObject("type").getJSONArray("coding");
            			int noOfCodings = iCodingJSON.length();
            			List<Coding> theCodingList = new ArrayList<Coding>();
            			for(int j = 0; j < noOfCodings; j++) {
            				Coding identifierCoding = new Coding();
                			if(!(iCodingJSON.getJSONObject(j).isNull("system"))) {
                				identifierCoding.setSystem(iCodingJSON.getJSONObject(j).getString("system"));
                			}
                			if(!(iCodingJSON.getJSONObject(j).isNull("code"))) {
                				identifierCoding.setCode(iCodingJSON.getJSONObject(j).getString("code"));
                			}
                			if(!(iCodingJSON.getJSONObject(j).isNull("display"))) {
                				identifierCoding.setDisplay(iCodingJSON.getJSONObject(j).getString("display"));
                			}
                			theCodingList.add(identifierCoding);
            			}
                    	
            			identifierCodeableConcept.setCoding(theCodingList);
            		}
                	theIdentifier.setType(identifierCodeableConcept);
            	}
        		
        		if(!(identifierJSON.getJSONObject(i).isNull("system"))) {
                	theIdentifier.setSystem(identifierJSON.getJSONObject(i).getString("system"));
            	}
            	
            	if(!(identifierJSON.getJSONObject(i).isNull("value"))) {
                	theIdentifier.setValue(identifierJSON.getJSONObject(i).getString("value"));
            	}
            	
            	if(!(identifierJSON.getJSONObject(i).isNull("period"))) {
            		Period identifierPeriod = new Period();
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("period").isNull("start"))) {
                        Date identifierSDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject(i).getJSONObject("period").getString("start"));
                        identifierPeriod.setStart(identifierSDate);
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("period").isNull("end"))) {
                        Date identifierEDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject(i).getJSONObject("period").getString("end"));
                        identifierPeriod.setStart(identifierEDate);
            		}
                    theIdentifier.setPeriod(identifierPeriod);
            	}

            	if(!(identifierJSON.getJSONObject(i).isNull("assigner"))) {
        			Reference identifierReference = new Reference(); 
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("display"))) {
                        identifierReference.setDisplay(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("display"));
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("reference"))) {
                        identifierReference.setReference(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("reference"));
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("type"))) {
                        identifierReference.setType(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("type"));
            		}
                    theIdentifier.setAssigner(identifierReference);
            	}
         
            	identifiers.add(theIdentifier);
        	}
        	practitioner.setIdentifier(identifiers);
        }
        
        //set active
        if(!(practitionerJSON.isNull("active"))) {
        	practitioner.setActive(practitionerJSON.getBoolean("active"));
        }
         
        // Set name
        if(!(practitionerJSON.isNull("name"))) {
        	JSONArray nameJSON = practitionerJSON.getJSONArray("name");
        	int numberOfNameUSe = nameJSON.length();
        	List<HumanName> nameList = new ArrayList<HumanName>();
        	
        	for(int i = 0; i < numberOfNameUSe; i++) {
        		HumanName name = new HumanName();
        		if(!(nameJSON.getJSONObject(i).isNull("use"))) {
            		name.setUse(HumanName.NameUse.fromCode((nameJSON.getJSONObject(i).getString("use"))));
        		}
        		
        		if(!(nameJSON.getJSONObject(i).isNull("text"))) {
            		name.setText(nameJSON.getJSONObject(i).getString("text"));
            	}
        
            	if(!(nameJSON.getJSONObject(i).isNull("family"))) {
            		name.setFamily(nameJSON.getJSONObject(i).getString("family"));
            	}

        		if(!(nameJSON.getJSONObject(i).isNull("given"))) {
            		List<StringType> givenList = new ArrayList<StringType>();
            		JSONArray givenNames = nameJSON.getJSONObject(i).getJSONArray("given");
            		for(int j = 0; j < givenNames.length(); j++) {
            			String givenName = givenNames.getString(j) ;
    				   	StringType theGiven = new StringType();
    				   	theGiven.setValue(givenName);
    				   	givenList.add(theGiven);
            		}
            		name.setGiven(givenList); 
        		}
        		
        		if(!(nameJSON.getJSONObject(i).isNull("prefix"))) {
            		List<StringType> prefixList = new ArrayList<StringType>();
            		JSONArray prefixNames = nameJSON.getJSONObject(i).getJSONArray("prefix");
            		for(int p = 0; p < prefixNames.length(); p++) {
            			String prefixName = prefixNames.getString(p) ;
    				   	StringType thePrefix = new StringType();
    				   	thePrefix.setValue(prefixName);
    				   	prefixList.add(thePrefix);
            		}
            		name.setPrefix(prefixList); 
        		}
        		
        		if(!(nameJSON.getJSONObject(i).isNull("suffix"))) {
            		List<StringType> suffixList = new ArrayList<StringType>();
            		JSONArray suffixNames = nameJSON.getJSONObject(i).getJSONArray("suffix");
            		for(int s = 0; s < suffixNames.length(); s++) {
            			String suffixName = suffixNames.getString(s) ;
    				   	StringType theSuffix = new StringType();
    				   	theSuffix.setValue(suffixName);
    				   	suffixList.add(theSuffix);
            		}
            		name.setSuffix(suffixList); 
        		}
        		nameList.add(name);
        	}      
        	practitioner.setName(nameList);
        }
        
        //Set address
        if(!(practitionerJSON.isNull("address"))) {
        	JSONArray addressJSON = practitionerJSON.getJSONArray("address");
        	int noOfAddresses = addressJSON.length();
        	List<Address> addressDtList = new ArrayList<Address>();
            
            for(int i = 0; i < noOfAddresses; i++) {
                Address theAddress = new Address();

            	if(!(addressJSON.getJSONObject(i).isNull("use"))) {
                    theAddress.setUse(Address.AddressUse.fromCode(addressJSON.getJSONObject(i).getString("use")));
                }
                
                if(!(addressJSON.getJSONObject(i).isNull("type"))) {
                    theAddress.setType(Address.AddressType.fromCode(addressJSON.getJSONObject(i).getString("type")));
                }
                
                if(!(addressJSON.getJSONObject(i).isNull("line"))) {
                	List<StringType> addressLines = new ArrayList<StringType>();
                    StringType addressLine = new StringType();
                    addressLine.setValue(addressJSON.getJSONObject(i).getJSONArray("line").toString());
                    addressLines.add(addressLine);
                    theAddress.setLine(addressLines);
                }
                
                if(!(addressJSON.getJSONObject(i).isNull("text"))) {
			       	theAddress.setText(addressJSON.getJSONObject(i).getString("text"));
                }
                if(!(addressJSON.getJSONObject(i).isNull("city"))) {
		       		theAddress.setCity(addressJSON.getJSONObject(i).getString("city"));
                }
                if(!(addressJSON.getJSONObject(i).isNull("district"))) {
		           theAddress.setDistrict(addressJSON.getJSONObject(i).getString("district"));
                }
                if(!(addressJSON.getJSONObject(i).isNull("state"))) {
		           theAddress.setState(addressJSON.getJSONObject(i).getString("state"));
                }
                if(!(addressJSON.getJSONObject(i).isNull("postalCode"))) {
		           theAddress.setPostalCode(addressJSON.getJSONObject(i).getString("postalCode"));
		
                }
                if(!(addressJSON.getJSONObject(i).isNull("country"))) {
		           theAddress.setCountry(addressJSON.getJSONObject(i).getString("country"));
		
                }
                if(!(addressJSON.getJSONObject(i).isNull("period"))) {
		            Period addressPeriod = new Period();
			       	if(!(addressJSON.getJSONObject(i).getJSONObject("period").isNull("start"))) {
			               Date startPeriod = CommonUtil.convertStringToDate(addressJSON.getJSONObject(i).getJSONObject("period").getString("start"));
			               addressPeriod.setStart(startPeriod);  
			       	}
			    	if(!(addressJSON.getJSONObject(i).getJSONObject("period").isNull("end"))) {
			               Date endPeriod = CommonUtil.convertStringToDate(addressJSON.getJSONObject(i).getJSONObject("period").getString("end"));
			               addressPeriod.setEnd(endPeriod);  
			       	}
			       	theAddress.setPeriod(addressPeriod);
                }
                addressDtList.add(theAddress);	
            }
            practitioner.setAddress(addressDtList);
        }
        
        //Set gender 
        if(!(practitionerJSON.isNull("gender"))) {
        	practitioner.setGender(Enumerations.AdministrativeGender.fromCode(practitionerJSON.getString("gender")));
        }
        
        //Set birth date
  		if(!(practitionerJSON.isNull("birthDate"))) {
  			String dateInString = (String) practitionerJSON.get("birthDate");
  			Date dateOfBirth = CommonUtil.convertStringToDate(dateInString);
  			practitioner.setBirthDate(dateOfBirth);
  		}
  		
  		//Set telecom
        if(!(practitionerJSON.isNull("telecom"))) {
        	JSONArray telecomJSON = practitionerJSON.getJSONArray("telecom");
        	int numberOfTelecoms = telecomJSON.length();
        	List<ContactPoint> contactPointDtList = new ArrayList<ContactPoint>();
        	
        	for(int i = 0; i < numberOfTelecoms; i++) {
            	ContactPoint phoneContact = new ContactPoint();
            	if(!(telecomJSON.getJSONObject(i).isNull("system"))) {
                    phoneContact.setSystem(ContactPoint.ContactPointSystem.fromCode(telecomJSON.getJSONObject(i).getString("system")));
            	}
            	if(!(telecomJSON.getJSONObject(i).isNull("use"))) {
                    phoneContact.setUse(ContactPoint.ContactPointUse.fromCode(telecomJSON.getJSONObject(i).getString("use")));
            	}
            	
            	if(!(telecomJSON.getJSONObject(i).isNull("value"))) {
                    phoneContact.setValue(telecomJSON.getJSONObject(i).getString("value"));
            	}
            	
            	if(!(telecomJSON.getJSONObject(i).isNull("rank"))) {
                    phoneContact.setRank(telecomJSON.getJSONObject(i).getInt("rank"));
            	}
            	
            	if(!(telecomJSON.getJSONObject(i).isNull("period"))) {
    				Period contactPeriod = new Period();
            		if(!(telecomJSON.getJSONObject(i).getJSONObject("period").isNull("end"))) {
        				Date contactEDate = CommonUtil.convertStringToDateYear(telecomJSON.getJSONObject(i).getJSONObject("period").getString("end"));
        				contactPeriod.setEnd(contactEDate);
            		}
            		if(!(telecomJSON.getJSONObject(i).getJSONObject("period").isNull("start"))) {
        				Date contactSDate = CommonUtil.convertStringToDateYear(telecomJSON.getJSONObject(i).getJSONObject("period").getString("start"));
        				contactPeriod.setStart(contactSDate);
            		}
    				phoneContact.setPeriod(contactPeriod);
            	}
            	
                contactPointDtList.add(phoneContact);
        	}
        	practitioner.setTelecom(contactPointDtList);
        }	
        
        //Set communication
        if(!(practitionerJSON.isNull("communication"))) {
        	JSONArray communicationJSON = practitionerJSON.getJSONArray("communication");
        	int noOfCommunications = communicationJSON.length();
        	List<CodeableConcept> communicationList = new ArrayList<CodeableConcept>();
        	
        	for(int i = 0; i < noOfCommunications; i++) {
            	CodeableConcept theCommunication = new CodeableConcept();
        		if(!(communicationJSON.getJSONObject(i).isNull("coding"))) {
        			JSONArray communicationCodingJSON = communicationJSON.getJSONObject(i).getJSONArray("coding");
        			int noOfcommunicationCodings = communicationCodingJSON.length();
        			List<Coding> communicationCodingList = new ArrayList<Coding>();
        			for(int j = 0; j < noOfcommunicationCodings; j++) {
            			Coding communicationCoding = new Coding();

        				if(!(communicationCodingJSON.getJSONObject(j).isNull("system"))) {
        					communicationCoding.setSystem(communicationCodingJSON.getJSONObject(j).getString("system"));
        				}
        				if(!(communicationCodingJSON.getJSONObject(j).isNull("code"))) {
        					communicationCoding.setCode(communicationCodingJSON.getJSONObject(j).getString("code"));
        				}
        				if(!(communicationCodingJSON.getJSONObject(j).isNull("display"))) {
        					communicationCoding.setDisplay(communicationCodingJSON.getJSONObject(j).getString("display"));
        				}
        				communicationCodingList.add(communicationCoding);
        			}
        			theCommunication.setCoding(communicationCodingList);
        		}
        		if(!(communicationJSON.getJSONObject(i).isNull("text"))) {
        			theCommunication.setText((communicationJSON.getJSONObject(i).getString("text")));
        		}
            	communicationList.add(theCommunication);
        	}
        	practitioner.setCommunication(communicationList);
        }
        
        //Set qualification
        if(!(practitionerJSON.isNull("qualification"))) {
        	JSONArray qualificationJSON = practitionerJSON.getJSONArray("qualification");
        	int noOfQualifications = qualificationJSON.length();
        	List<PractitionerQualificationComponent> qualificationList = new ArrayList<PractitionerQualificationComponent>();
        	
        	for(int q = 0; q < noOfQualifications; q++) {
            	PractitionerQualificationComponent theQualification = new PractitionerQualificationComponent();
            	//Set identifier
        		if(!(qualificationJSON.getJSONObject(q).isNull("identifier"))) {
        			JSONArray qualificationIdentifiersJSON = qualificationJSON.getJSONObject(q).getJSONArray("identifier");
        			int noOfQualificationIdentifiers = qualificationIdentifiersJSON.length();
        			
        			List<Identifier> qualificationIdentifiers = new ArrayList<Identifier>();
                	
                	for(int j = 0; j < noOfQualificationIdentifiers; j++) {
                    	Identifier theQualificationIdentifier = new Identifier();
                		if(!(qualificationIdentifiersJSON.getJSONObject(j).isNull("use"))) {
                			theQualificationIdentifier.setUse(Identifier.IdentifierUse.fromCode((qualificationIdentifiersJSON.getJSONObject(j).getString("use"))));
                		}
                		if(!(qualificationIdentifiersJSON.getJSONObject(j).isNull("type"))) {
                			CodeableConcept qualificationCodeableConcept = new CodeableConcept();
                			if(!(qualificationIdentifiersJSON.getJSONObject(j).getJSONObject("type").isNull("text"))) {
                				qualificationCodeableConcept.setText(qualificationIdentifiersJSON.getJSONObject(j).getJSONObject("type").getString("text"));
                			}
                			theQualificationIdentifier.setType(qualificationCodeableConcept);
                		}
                		if(!(qualificationIdentifiersJSON.getJSONObject(j).isNull("system"))) {
                			theQualificationIdentifier.setSystem(qualificationIdentifiersJSON.getJSONObject(j).getString("system"));
                		}
                		if(!(qualificationIdentifiersJSON.getJSONObject(j).isNull("value"))) {
                			theQualificationIdentifier.setValue(qualificationIdentifiersJSON.getJSONObject(j).getString("value"));
                		}
                		qualificationIdentifiers.add(theQualificationIdentifier);
                	}
                	theQualification.setIdentifier(qualificationIdentifiers);
        		}
        		
        		//Set code
            	if(!(qualificationJSON.getJSONObject(q).isNull("code"))) {
            		CodeableConcept qualificationCode = new CodeableConcept();

            		if(!(qualificationJSON.getJSONObject(q).getJSONObject("code").isNull("coding"))) {
            			JSONArray codingJSON = qualificationJSON.getJSONObject(q).getJSONObject("code").getJSONArray("coding");
            			int noOfQualificationCoding = codingJSON.length();
            			List<Coding> qualificationCodings = new ArrayList<Coding>();
            			
            			for(int i = 0; i < noOfQualificationCoding; i++) {
                			Coding qualificationCoding = new Coding();
            				
                			if(!(codingJSON.getJSONObject(i).isNull("system"))) {
            					qualificationCoding.setSystem(codingJSON.getJSONObject(i).getString("system"));
            				}
            				if(!(codingJSON.getJSONObject(i).isNull("code"))) {
            					qualificationCoding.setCode(codingJSON.getJSONObject(i).getString("code"));
            				}
            				if(!(codingJSON.getJSONObject(i).isNull("display"))) {
            					qualificationCoding.setDisplay(codingJSON.getJSONObject(i).getString("display"));
            				}
            				qualificationCodings.add(qualificationCoding);
            			}
            			qualificationCode.setCoding(qualificationCodings);
            		}
            		if(!(qualificationJSON.getJSONObject(q).getJSONObject("code").isNull("text"))) {
            			qualificationCode.setText(qualificationJSON.getJSONObject(q).getJSONObject("code").getString("text"));
            		}
            		theQualification.setCode(qualificationCode);
            	}
            	
            	//Set period
            	if(!(qualificationJSON.getJSONObject(q).isNull("period"))) {
            		Period qualificationPeriod = new Period();
            		if(!(qualificationJSON.getJSONObject(q).getJSONObject("period").isNull("start"))) {
                        Date qualificationSDate = CommonUtil.convertStringToDateYear(qualificationJSON.getJSONObject(q).getJSONObject("period").getString("start"));
                        qualificationPeriod.setStart(qualificationSDate);
            		}
            		if(!(qualificationJSON.getJSONObject(q).getJSONObject("period").isNull("end"))) {
                        Date qualificationEDate = CommonUtil.convertStringToDateYear(qualificationJSON.getJSONObject(q).getJSONObject("period").getString("end"));
                        qualificationPeriod.setEnd(qualificationEDate);
            		}
            		theQualification.setPeriod(qualificationPeriod);
            	}
            	
            	//Set issuer
            	if(!(qualificationJSON.getJSONObject(q).isNull("issuer"))) {
            		Reference qualificationIssuer = new Reference();
            		if(!(qualificationJSON.getJSONObject(q).getJSONObject("issuer").isNull("display"))) {
            			qualificationIssuer.setDisplay(qualificationJSON.getJSONObject(q).getJSONObject("issuer").getString("display"));
            		}
            		if(!(qualificationJSON.getJSONObject(q).getJSONObject("issuer").isNull("type"))) {
            			qualificationIssuer.setType(qualificationJSON.getJSONObject(q).getJSONObject("issuer").getString("type"));
            		}
            		if(!(qualificationJSON.getJSONObject(q).getJSONObject("issuer").isNull("reference"))) {
            			qualificationIssuer.setReference(qualificationJSON.getJSONObject(q).getJSONObject("issuer").getString("reference"));
            		}
            		theQualification.setIssuer(qualificationIssuer);
            	}
            	qualificationList.add(theQualification);
        	}
        	practitioner.setQualification(qualificationList);
        }
        return practitioner;
	} 

}
