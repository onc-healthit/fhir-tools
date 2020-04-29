package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Patient.ContactComponent;
import org.hl7.fhir.r4.model.Patient.PatientCommunicationComponent;
import org.hl7.fhir.r4.model.Patient.PatientLinkComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.parboiled.common.StringUtils;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafPatient;
import org.sitenv.spring.service.PatientService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PatientResourceProvider implements IResourceProvider {
	
	public static final String RESOURCE_TYPE = "Patient";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    PatientService service;

    public PatientResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (PatientService) context.getBean("patientService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Patient.class;
	}
	
 	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Patient/1/_history/4
	 * @param theId : Id of the patient
	 * @return : Object of patient information
	 */
	@Read(version=true)
    public Patient readOrVread(@IdParam IdType theId) {
		String id;
		DafPatient dafPatient;
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
		   dafPatient = service.getPatientByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       dafPatient = service.getPatientById(id);
		}
		return createPatientObject(dafPatient);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=Patient.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Patient/1/_history
	 * @param theId : ID of the patient
	 * @return : List of patient's
	 */
	@History()
    public List<Patient> getPatientHistoryById( @IdParam IdType theId) {

		String id;
		try {
			id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafPatient> dafPatientList = service.getPatientHistoryById(id);
        
        List<Patient> patientList = new ArrayList<Patient>();
        for (DafPatient dafPatient : dafPatientList) {
            patientList.add(createPatientObject(dafPatient));
        }
        
        return patientList;
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
	 * @param theOrganization
	 * @param theTelecom
	 * @param theAddress
	 * @param theAddressCity
	 * @param theAddressState
	 * @param theAddressPostalcode
	 * @param theAddressCountry
	 * @param theGender
	 * @param theLanguage
	 * @param theBirthdate
	 * @param theActive
	 * @param theLink
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
        @OptionalParam(name = Patient.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "A patient identifier")
        @OptionalParam(name = Patient.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "A portion of either family or given name of the patient")
        @OptionalParam(name = Patient.SP_NAME)
        StringAndListParam theName,


        @Description(shortDefinition = "A portion of the family name of the patient")
        @OptionalParam(name = Patient.SP_FAMILY)
        StringAndListParam theFamily,

        @Description(shortDefinition = "A portion of the given name of the patient")
        @OptionalParam(name = Patient.SP_GIVEN)
        StringAndListParam theGiven,

        @Description(shortDefinition = "The organization that is the custodian of the patient record")
        @OptionalParam(name = Patient.SP_ORGANIZATION)
        StringAndListParam theOrganization,

        @Description(shortDefinition = "The value in any kind of telecom details of the patient")
        @OptionalParam(name = Patient.SP_TELECOM)
        StringAndListParam theTelecom,

        @Description(shortDefinition = "An address in any kind of address/part of the patient")
        @OptionalParam(name = Patient.SP_ADDRESS)
        StringAndListParam theAddress,
        
        @Description(shortDefinition="A city specified in an address")
		@OptionalParam(name = Patient.SP_ADDRESS_CITY)
		StringAndListParam theAddressCity,

		@Description(shortDefinition="A state specified in an address")
		@OptionalParam(name = Patient.SP_ADDRESS_STATE)
		StringAndListParam theAddressState,

		@Description(shortDefinition="A postalCode specified in an address")
		@OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE)
		StringAndListParam theAddressPostalcode,
  
			@Description(shortDefinition="A country specified in an address")
		@OptionalParam(name = Patient.SP_ADDRESS_COUNTRY)
		StringAndListParam theAddressCountry, 

        @Description(shortDefinition = "Gender of the patient")
        @OptionalParam(name = Patient.SP_GENDER)
        TokenAndListParam theGender,

        @Description(shortDefinition = "Language code (irrespective of use value)")
        @OptionalParam(name = Patient.SP_LANGUAGE)
        StringAndListParam theLanguage,

        @Description(shortDefinition = "The patient's date of birth")
        @OptionalParam(name = Patient.SP_BIRTHDATE)
        DateRangeParam theBirthdate,

        @Description(shortDefinition = "Whether the patient record is active")
        @OptionalParam(name = Patient.SP_ACTIVE)
        TokenAndListParam theActive,
        
        @Description(shortDefinition = "A use code specified in an address.Patient.address.use")
        @OptionalParam(name = Patient.SP_ADDRESS_USE)
        TokenAndListParam theAddressUse,

        @Description(shortDefinition = "All patients linked to the given patient")
        @OptionalParam(name = Patient.SP_LINK, targetTypes = {Patient.class})
        ReferenceAndListParam theLink,
        
        @Description(shortDefinition = "This patient has been marked as deceased, or as a death date entered")
        @OptionalParam(name = Patient.SP_DECEASED)
        TokenAndListParam theDeceased,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,

		@IncludeParam(allow= {"*"})
		Set<Include> theIncludes
	)

    {
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(Patient.SP_RES_ID, theId);
        paramMap.add(Patient.SP_IDENTIFIER, theIdentifier);
        paramMap.add(Patient.SP_NAME, theName);
        paramMap.add(Patient.SP_FAMILY, theFamily);
        paramMap.add(Patient.SP_GIVEN, theGiven);
        paramMap.add(Patient.SP_ORGANIZATION, theOrganization);
        paramMap.add(Patient.SP_TELECOM, theTelecom);
        paramMap.add(Patient.SP_ADDRESS, theAddress);
        paramMap.add(Patient.SP_ADDRESS_CITY, theAddressCity);
        paramMap.add(Patient.SP_ADDRESS_STATE, theAddressState);
        paramMap.add(Patient.SP_ADDRESS_POSTALCODE, theAddressPostalcode);
        paramMap.add(Patient.SP_ADDRESS_COUNTRY, theAddressCountry);
        paramMap.add(Patient.SP_GENDER, theGender);
        paramMap.add(Patient.SP_LANGUAGE, theLanguage);
        paramMap.add(Patient.SP_BIRTHDATE, theBirthdate);
        paramMap.add(Patient.SP_ACTIVE, theActive);
        paramMap.add(Patient.SP_LINK, theLink);
        paramMap.setIncludes(theIncludes);
		paramMap.setRevIncludes(theRevIncludes);

        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<DafPatient> results = service.search(paramMap);

		//ca.uhn.fhir.rest.server.IBundleProvider retVal = getDao().search(paramMap);
        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
            	List<IBaseResource> patientList = new ArrayList<IBaseResource>();
            	List<String> patientIDs = new ArrayList<String>();
                for(DafPatient dafPatient : results){
                    Patient patient = createPatientObject(dafPatient);
                	patientList.add(patient);
					patientIDs.add(((IdType)patient.getIdElement()).getResourceType()+"/"+((IdType)patient.getIdElement()).getIdPart());
				}
				if(theRevIncludes.size() >0 ){
					patientList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(patientIDs));
				}

                return patientList;
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

    /**
     * This method converts DafDocumentReference object to DocumentReference object
     * @param dafPatient : DafDocumentReference patient object
     * @return : DocumentReference patient object
     */
    private Patient createPatientObject(DafPatient dafPatient) {
        Patient patient = new Patient();
        JSONObject patientJSONObj = new JSONObject(dafPatient.getData());

        // Set version
        if(!(patientJSONObj.isNull("meta"))) {
        	if(!(patientJSONObj.getJSONObject("meta").isNull("versionId"))) {
                patient.setId(new IdType(RESOURCE_TYPE, patientJSONObj.getString("id") + "", patientJSONObj.getJSONObject("meta").getString("versionId")));
        	}else{
				patient.setId(new IdType(RESOURCE_TYPE, patientJSONObj.getString("id") + "", VERSION_ID));
			}
        }
        else {
            patient.setId(new IdType(RESOURCE_TYPE, patientJSONObj.getString("id") + "", VERSION_ID));
        }

		// set extensions
		if (!(patientJSONObj.isNull("extension"))) {
			JSONArray externalExtensionJSON = patientJSONObj.getJSONArray("extension");
			int noOfExternalExtensions = externalExtensionJSON.length();
			List<Extension> extension = new ArrayList<>();
			for (int i = 0; i < noOfExternalExtensions; i++) {
				Extension theExtension = new Extension();
				if (!(externalExtensionJSON.getJSONObject(i).isNull("url"))) {
					theExtension.setUrl((externalExtensionJSON.getJSONObject(i).getString("url")));
				}

				if (!(externalExtensionJSON.getJSONObject(i).isNull("valueCode"))) {
					CodeType theCoding = new CodeType();
					theCoding.setValue(externalExtensionJSON.getJSONObject(i).getString("valueCode"));
					theExtension.setValue(theCoding);
				}

				if (!(externalExtensionJSON.getJSONObject(i).isNull("valueString"))) {
					StringType valueCode = new StringType(externalExtensionJSON.getJSONObject(i).getString("valueString"));
					theExtension.setValue(valueCode);
				}


				if (!(externalExtensionJSON.getJSONObject(i).isNull("extension"))) {
					JSONArray iExtensionJSON = externalExtensionJSON.getJSONObject(i).getJSONArray("extension");
					int noOfExtensions = iExtensionJSON.length();
					List<Extension> childList = new ArrayList<Extension>();
					for (int k = 0; k < noOfExtensions; k++) {
						Extension childExt = new Extension();
						if (!(iExtensionJSON.getJSONObject(k).isNull("url"))) {
							childExt.setUrl((iExtensionJSON.getJSONObject(k).getString("url")));
						}

						if (!(iExtensionJSON.getJSONObject(k).isNull("valueString"))) {
							StringType value = new StringType(iExtensionJSON.getJSONObject(k).getString("valueString"));
							childExt.setValue(value);
						}
						if (!(iExtensionJSON.getJSONObject(k).isNull("valueCoding"))) {
							Coding theCoding = new Coding();
							if (!(iExtensionJSON.getJSONObject(k).getJSONObject("valueCoding").isNull("code"))) {
								theCoding.setCode(iExtensionJSON.getJSONObject(k).getJSONObject("valueCoding").getString("code"));
							}

							if (!(iExtensionJSON.getJSONObject(k).getJSONObject("valueCoding").isNull("system"))) {
								theCoding.setSystem(iExtensionJSON.getJSONObject(k).getJSONObject("valueCoding").getString("system"));
							}
							if (!(iExtensionJSON.getJSONObject(k).getJSONObject("valueCoding").isNull("display"))) {
								theCoding.setDisplay(iExtensionJSON.getJSONObject(k).getJSONObject("valueCoding").getString("display"));
							}
							childExt.setValue(theCoding);
						}
						childList.add(childExt);
					}
					theExtension.setExtension(childList);
				}
				extension.add(theExtension);
			}
			patient.setExtension(extension);
		}
        
        
        //Set identifier
        if(!(patientJSONObj.isNull("identifier"))) {
        	JSONArray identifierJSON = patientJSONObj.getJSONArray("identifier");
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
        	patient.setIdentifier(identifiers);
        }
        
        //set active
        if(!(patientJSONObj.isNull("active"))) {
            patient.setActive(patientJSONObj.getBoolean("active"));
        }



        // Set name
        if(!(patientJSONObj.isNull("name"))) {
        	JSONArray nameJSON = patientJSONObj.getJSONArray("name");
        	int numberOfNameUSe = nameJSON.length();
        	List<HumanName> nameList = new ArrayList<HumanName>();
        	
        	for(int n = 0; n < numberOfNameUSe; n++) {
        		HumanName name = new HumanName();
        		if(!(nameJSON.getJSONObject(n).isNull("use"))) {
            		name.setUse(HumanName.NameUse.fromCode((nameJSON.getJSONObject(n).getString("use"))));
        		}
        		
        		if(!(nameJSON.getJSONObject(n).isNull("text"))) {
            		name.setText(nameJSON.getJSONObject(n).getString("text"));
            	}
        
            	if(!(nameJSON.getJSONObject(n).isNull("family"))) {
            		name.setFamily(nameJSON.getJSONObject(n).getString("family"));
            	}

        		if(!(nameJSON.getJSONObject(n).isNull("given"))) {
            		List<StringType> givenList = new ArrayList<StringType>();
            		JSONArray givenNames = nameJSON.getJSONObject(n).getJSONArray("given");
            		for(int g = 0; g < givenNames.length(); g++) {

            			String givenName = givenNames.getString(g) ;
    				   	StringType givenStringType = new StringType();
    				   	givenStringType.setValue(givenName);
    				   	givenList.add(givenStringType);


            		}
            		name.setGiven(givenList); 
        		}


        		if(!(nameJSON.getJSONObject(n).isNull("prefix"))) {
            		List<StringType> prefixList = new ArrayList<StringType>();
            		JSONArray prefixNames = nameJSON.getJSONObject(n).getJSONArray("prefix");
            		for(int p = 0; p < prefixNames.length(); p++) {
            			String prefixName = prefixNames.getString(p) ;
    				   	StringType prefixStringType = new StringType();
    				   	prefixStringType.setValue(prefixName);
    				   	prefixList.add(prefixStringType);
            		}
            		name.setPrefix(prefixList); 
        		}
        		
        		if(!(nameJSON.getJSONObject(n).isNull("suffix"))) {
            		List<StringType> suffixList = new ArrayList<StringType>();
            		JSONArray suffixNames = nameJSON.getJSONObject(n).getJSONArray("suffix");
            		for(int s = 0; s < suffixNames.length(); s++) {
            			String suffixName = suffixNames.getString(s) ;
    				   	StringType suffixStringType = new StringType();
    				   	suffixStringType.setValue(suffixName);
    				   	suffixList.add(suffixStringType);
            		}
            		name.setSuffix(suffixList);
        		}
        		nameList.add(name);
        	}      
            patient.setName(nameList);
        }

        //Set telecom
        if(!(patientJSONObj.isNull("telecom"))) {
        	JSONArray telecomJSON = patientJSONObj.getJSONArray("telecom");
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
           patient.setTelecom(contactPointDtList);
        }

        //Set gender
        if(!(patientJSONObj.isNull("gender"))) {
        	patient.setGender(Enumerations.AdministrativeGender.fromCode(patientJSONObj.getString("gender")));
        }
       
        //Set birth date
		if(!(patientJSONObj.isNull("birthDate"))) {
			String dateInStr = (String) patientJSONObj.get("birthDate");
			Date dateOfBirth = CommonUtil.convertStringToDate(dateInStr);
			patient.setBirthDate(dateOfBirth);
		}
		
		//Set deceasedBoolean
		if(!(patientJSONObj.isNull("deceasedBoolean"))) {
			BooleanType isDeceased = new BooleanType();
			isDeceased.setValue(patientJSONObj.getBoolean("deceasedBoolean"));
			patient.setDeceased(isDeceased);
		}
		
        //Set address
        if(!(patientJSONObj.isNull("address"))) {
        	JSONArray addressJSON = patientJSONObj.getJSONArray("address");
        	int noOfAddresses = addressJSON.length();
        	List<Address> addressDtList = new ArrayList<Address>();
            Address theAddress = new Address();
            
            for(int i = 0; i < noOfAddresses; i++) {
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
			               Date addressDatePeriod = CommonUtil.convertStringToDate(addressJSON.getJSONObject(i).getJSONObject("period").getString("start"));
			               addressPeriod.setStart(addressDatePeriod);
			       	}
		            theAddress.setPeriod(addressPeriod);
               }

            }
            addressDtList.add(theAddress);	
            patient.setAddress(addressDtList);
        }
        
        //Set communication
  		if (!(patientJSONObj.isNull("communication"))) {
  			JSONArray communicationJSON = patientJSONObj.getJSONArray("communication");
  			int noOfCommunications = communicationJSON.length();
  			List<PatientCommunicationComponent> communicationList = new ArrayList<PatientCommunicationComponent>();
  			for(int i = 0; i < noOfCommunications; i++) {
  	  			PatientCommunicationComponent theCommunication = new PatientCommunicationComponent();
  				if (!(communicationJSON.getJSONObject(i).isNull("language"))) {
  		  			CodeableConcept theCommunicationLanguage = new CodeableConcept();
  	  				JSONObject languageJSON = communicationJSON.getJSONObject(i).getJSONObject("language");
  	  				if (!(languageJSON.isNull("coding"))) {
  	  					JSONArray communicationCodingJSON = languageJSON.getJSONArray("coding");
  	  					int noOfCommunicationCoding = communicationCodingJSON.length();
	  					List<Coding> codingList = new ArrayList<Coding>();

  	  					for(int j = 0; j < noOfCommunicationCoding; j++) {
  	  						Coding theCoding = new Coding();
  	  						
  	  						if(!(communicationCodingJSON.getJSONObject(j).isNull("system"))) {
  	  							theCoding.setSystem(communicationCodingJSON.getJSONObject(j).getString("system"));
  	  						}
  	  						if(!(communicationCodingJSON.getJSONObject(j).isNull("display"))) {
  	  	  						theCoding.setDisplay(communicationCodingJSON.getJSONObject(j).getString("display"));
  	  						}
  	  						if(!(communicationCodingJSON.getJSONObject(j).isNull("code"))) {
	  	  						theCoding.setCode(communicationCodingJSON.getJSONObject(j).getString("code"));
	  						}
  	  						codingList.add(theCoding);
  	  					}
  	  					theCommunicationLanguage.setCoding(codingList);
  	  				}
  	  				theCommunication.setLanguage(theCommunicationLanguage);
  	  			}
  				if (!(communicationJSON.getJSONObject(i).isNull("preferred"))) {
  					theCommunication.setPreferred(communicationJSON.getJSONObject(i).getBoolean("preferred"));
  				}
  	  			communicationList.add(theCommunication);
  			}
  			patient.setCommunication(communicationList);
  		}
  		
        //Set multipleBirthBoolean
  		if(!(patientJSONObj.isNull("multipleBirthBoolean"))) {
  			BooleanType multipleBirth = new BooleanType();
  	      	multipleBirth.setValue(patientJSONObj.getBoolean("multipleBirthBoolean"));
  	      	patient.setMultipleBirth(multipleBirth);
  		}
      	
      	//Set contact
      	if (!(patientJSONObj.isNull("contact"))) {
  			JSONArray contactJSON = patientJSONObj.getJSONArray("contact");
  			int noOfContacts = contactJSON.length();
  			List<ContactComponent> contactComponentList = new ArrayList<ContactComponent>();
  			
  			for(int j = 0; j < noOfContacts; j++) {
  	  			ContactComponent theContactComponent = new ContactComponent();
  				if(!(contactJSON.getJSONObject(j).isNull("relationship"))) {
  					JSONArray contactRelationJSON = contactJSON.getJSONObject(j).getJSONArray("relationship");
	  	  			int noOfRelations = contactRelationJSON.length();
		  			List<CodeableConcept> relCodeableCodeList = new ArrayList<CodeableConcept>();		  			
	  	  			for(int k = 0; k < noOfRelations; k++) {
		  	  			CodeableConcept relCodeableConcept = new CodeableConcept();
	  	  				if(!(contactRelationJSON.getJSONObject(k).isNull("coding"))) {
	  	  					JSONArray relationCodingJSON = contactRelationJSON.getJSONObject(k).getJSONArray("coding");
	  	  					int noOfRelationCodings = relationCodingJSON.length();
	  			  			List<Coding> relationCodingList = new ArrayList<Coding>();
	  	  					for(int l = 0; l < noOfRelationCodings; l++) {
	  	  						Coding relationCoding = new Coding();
	  	  						if(!(relationCodingJSON.getJSONObject(l).isNull("system"))) {
		  	  		  				relationCoding.setSystem(relationCodingJSON.getJSONObject(l).getString("system"));
	  	  						}	
			  	  		  			
		  	  		  			if(!(relationCodingJSON.getJSONObject(l).isNull("code"))) {
		  	  		  				relationCoding.setCode(relationCodingJSON.getJSONObject(l).getString("code"));
		  	  		  			}
			  	  		  		if(!(relationCodingJSON.getJSONObject(l).isNull("display"))) {
		  	  		  				relationCoding.setDisplay(relationCodingJSON.getJSONObject(l).getString("display"));
		  	  		  			}
		  	  		  			relationCodingList.add(relationCoding);
	  	  					}
	  	  					relCodeableConcept.setCoding(relationCodingList);
	  	  				}
		  	  			if(!(contactRelationJSON.getJSONObject(k).isNull("text"))) {
		  	  				relCodeableConcept.setText(contactRelationJSON.getJSONObject(k).getString("text"));
		  	  			}
  	  					relCodeableCodeList.add(relCodeableConcept);
	  	  			}
	  	  			theContactComponent.setRelationship(relCodeableCodeList);
	  	  		}
  				
  				//Set organization
  				if(!(contactJSON.getJSONObject(j).isNull("organization"))) {
  					Reference contactOrganization = new Reference();
  					if(!(contactJSON.getJSONObject(j).getJSONObject("organization").isNull("reference"))) {
  						contactOrganization.setReference(contactJSON.getJSONObject(j).getJSONObject("organization").getString("reference"));
  					}
  					if(!(contactJSON.getJSONObject(j).getJSONObject("organization").isNull("display"))) {
  						contactOrganization.setDisplay(contactJSON.getJSONObject(j).getJSONObject("organization").getString("display"));
  					}
  					theContactComponent.setOrganization(contactOrganization);
  				}
  				
  				//Set contact name
  				if(!(contactJSON.getJSONObject(j).isNull("name"))) {
  					JSONObject patientNameJSON = contactJSON.getJSONObject(j).getJSONObject("name");
  		            HumanName contactName = new HumanName();
  		            
  		            if(!(patientNameJSON.isNull("use"))) {
		            	contactName.setUse(HumanName.NameUse.fromCode(patientNameJSON.getString("use")));
		            }
  		          
  		            if(!(patientNameJSON.isNull("family"))) {
  		            	contactName.setFamily(patientNameJSON.getString("family"));
  		            }
  		            
  		            if(!(patientNameJSON.isNull("text"))) {
		            	contactName.setText(patientNameJSON.getString("text"));
		            }
  		            if(!(patientNameJSON.isNull("given"))) {
  		            	List<StringType> contactGivenList = new ArrayList<StringType>();
  	            		JSONArray contactGivenNames = patientNameJSON.getJSONArray("given");
  	            		for(int i = 0; i < contactGivenNames.length(); i++) {
  	            			String contactGivenName = contactGivenNames.getString(i) ;
  	    				   	StringType contactGivenStrType = new StringType();
  	    				   	contactGivenStrType.setValue(contactGivenName);
  	    				   	contactGivenList.add(contactGivenStrType);
  	            		}
  	            		contactName.setGiven(contactGivenList);
  		            }
  		            theContactComponent.setName(contactName);
  				}
  				
  				//Set contact telecom
  				if(!(contactJSON.getJSONObject(j).isNull("telecom"))) {
  					JSONArray contactTelecomJSON = contactJSON.getJSONObject(j).getJSONArray("telecom");
  					int noOfTelecoms = contactTelecomJSON.length();
  					List<ContactPoint> contactTelecomPointDtList = new ArrayList<ContactPoint>();
  					
  					for(int i = 0; i < noOfTelecoms; i++) {
  	  					ContactPoint contactTelecomPoint = new ContactPoint();
  						if(!(contactTelecomJSON.getJSONObject(i).isNull("system"))) {
  	  						contactTelecomPoint.setSystem(ContactPoint.ContactPointSystem.fromCode(contactTelecomJSON.getJSONObject(i).getString("system")));
  						}
  						if(!(contactTelecomJSON.getJSONObject(i).isNull("value"))) {
  							contactTelecomPoint.setValue(contactTelecomJSON.getJSONObject(i).getString("value"));  
  						}
  						if(!(contactTelecomJSON.getJSONObject(i).isNull("use"))) {
  							contactTelecomPoint.setUse(ContactPoint.ContactPointUse.fromCode(contactTelecomJSON.getJSONObject(i).getString("use")));  
  						}
  						contactTelecomPointDtList.add(contactTelecomPoint);
  					}
  					theContactComponent.setTelecom(contactTelecomPointDtList);
  				}
  	  			contactComponentList.add(theContactComponent);
  			}
  			patient.setContact(contactComponentList);
      	}
  			
        //Set maritalStatus
        if(!(patientJSONObj.isNull("maritalStatus"))) {
        	JSONObject maritalStatusJSON = patientJSONObj.getJSONObject("maritalStatus");        	
			CodeableConcept theMaritalStatus = new CodeableConcept();
			if(!(maritalStatusJSON.isNull("coding"))) {
    			JSONArray maritalStatusCoding = maritalStatusJSON.getJSONArray("coding");
    			int noOfMaritalStatus = maritalStatusCoding.length();
    			List<Coding> maritalCodingList = new ArrayList<Coding>();
    			for(int i = 0; i < noOfMaritalStatus; i++) {
                	Coding maritalCoding = new Coding();
        			if(!(maritalStatusCoding.getJSONObject(i).isNull("system"))) {
        				maritalCoding.setSystem(maritalStatusCoding.getJSONObject(i).getString("system"));
        			}
        			if(!(maritalStatusCoding.getJSONObject(i).isNull("code"))) {
        				maritalCoding.setCode(maritalStatusCoding.getJSONObject(i).getString("code"));
        			}
        			if(!(maritalStatusCoding.getJSONObject(i).isNull("display"))) {
        				maritalCoding.setDisplay(maritalStatusCoding.getJSONObject(i).getString("display"));
        			}
        			maritalCodingList.add(maritalCoding);
    			}
    			theMaritalStatus.setCoding(maritalCodingList);
    		}
    		
    		if(!(maritalStatusJSON.isNull("text"))) {
    			theMaritalStatus.setText(maritalStatusJSON.getString("text"));
    		}
    		patient.setMaritalStatus(theMaritalStatus);
    	}
      
        //Set managingOrganization
        if(!(patientJSONObj.isNull("managingOrganization"))) {
            Reference managingOrgRef = new Reference();
            if(!(patientJSONObj.getJSONObject("managingOrganization").isNull("reference"))) {
                managingOrgRef.setReference(patientJSONObj.getJSONObject("managingOrganization").getString("reference"));
            }
            if(!(patientJSONObj.getJSONObject("managingOrganization").isNull("display"))) {
                managingOrgRef.setDisplay(patientJSONObj.getJSONObject("managingOrganization").getString("display"));
            }
            if(!(patientJSONObj.getJSONObject("managingOrganization").isNull("type"))) {
                managingOrgRef.setType(patientJSONObj.getJSONObject("managingOrganization").getString("type"));
            }
     	    patient.setManagingOrganization(managingOrgRef);
        }
        
        //Set link
        if(!(patientJSONObj.isNull("link"))) {
        	List<PatientLinkComponent> linksList = new ArrayList<PatientLinkComponent>();
        	JSONArray linkJSON = patientJSONObj.getJSONArray("link");
        	int noOfLinks = linkJSON.length();
        	for(int i = 0; i < noOfLinks; i++) {
            	PatientLinkComponent theLink = new PatientLinkComponent();
        		if(!(linkJSON.getJSONObject(i).isNull("other"))) {
        			Reference linkReference = new Reference();
        			if(!(linkJSON.getJSONObject(i).getJSONObject("other").isNull("reference"))) {
        				linkReference.setReference(linkJSON.getJSONObject(i).getJSONObject("other").getString("reference"));
        			}        				
        			theLink.setOther(linkReference);
        		}
        		if(!(linkJSON.getJSONObject(i).isNull("type"))) {
        			theLink.setType(Patient.LinkType.fromCode((linkJSON.getJSONObject(i).getString("type"))));
        		}
        		linksList.add(theLink);
        	}
        	patient.setLink(linksList);
        }



		return patient;
    }
}
