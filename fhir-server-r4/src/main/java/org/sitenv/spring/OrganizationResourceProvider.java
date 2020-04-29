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
import org.hl7.fhir.r4.model.Organization.OrganizationContactComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafOrganization;
import org.sitenv.spring.service.OrganizationService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class OrganizationResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Organization";
	public static final String VERSION_ID = "1.0";
	AbstractApplicationContext context;
	OrganizationService service;

	public OrganizationResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		service = (OrganizationService) context.getBean("organizationService");
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Organization.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Organization/1/_history/4
	 * 
	 * @param theId : Id of the organization
	 * @return : Object of organization information
	 */
	@Read(version = true)
	public Organization readOrVread(@IdParam IdType theId) {
		String id;
		DafOrganization dafOrganization;
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
			dafOrganization = service.getOrganizationByVersionId(id, theId.getVersionIdPart());

		} else {
			dafOrganization = service.getOrganizationById(id);
		}
		return createOrganizationObject(dafOrganization);
	}

	/**
	 * The history operation retrieves a historical collection of all versions of a
	 * single resource (instance history). History methods must be annotated with
	 * the @History annotation.It supports Instance History method.
	 * "type=Organization.class". Instance level (history of a specific resource instance
	 * by type and ID) The method must have a parameter annotated with the @IdParam
	 * annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Organization/1/_history
	 * 
	 * @param theId : ID of the organization
	 * @return : List of organization's
	 */
	@History()
	public List<Organization> getOrganizationHistoryById(@IdParam IdType theId) {

		String id;
		try {
			id = theId.getIdPart();
		} catch (NumberFormatException e) {
			// If we can't parse the ID as a long, it's not valid so this is an unknown
			// resource
			throw new ResourceNotFoundException(theId);
		}
		List<DafOrganization> dafOrganizationList = service.getOrganizationHistoryById(id);

		List<Organization> organizationList = new ArrayList<Organization>();
		for (DafOrganization dafOrganization : dafOrganizationList) {
			organizationList.add(createOrganizationObject(dafOrganization));
		}

		return organizationList;
	}

	/**
	 * The "@Search" annotation indicates that this method supports the search
	 * operation. You may have many different method annotated with this annotation,
	 * to support many different search criteria. The search operation returns a
	 * bundle with zero-to-many resources of a given type, matching a given set of
	 * parameters.
	 * 
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theAddress
	 * @param theActive
	 * @param theAddressCity
	 * @param theAddressState
	 * @param theAddressCountry
	 * @param theType
	 * @param theEndPoint
	 * @param theName
	 * @param theTelecom
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
	    @OptionalParam(name = Organization.SP_RES_ID)
	    StringAndListParam theId,
	
	    @Description(shortDefinition = "An Organization  identifier")
	    @OptionalParam(name = Organization.SP_IDENTIFIER)
	    TokenAndListParam theIdentifier,
	    
	    @Description(shortDefinition = "The organization of which this organization forms a part")
	    @OptionalParam(name = Organization.SP_PARTOF)
	    ReferenceAndListParam thePartOf,
	    
	    @Description(shortDefinition = "Visiting or addresses for the contact")
	    @OptionalParam(name = Organization.SP_ADDRESS)
	    StringAndListParam theAddress,
	    
	    @Description(shortDefinition = "Visiting or state addresses for the contact")
	    @OptionalParam(name = Organization.SP_ADDRESS_STATE)
	    StringAndListParam theAddressState,
	    
	    @Description(shortDefinition = "Whether the organization record is active")
	    @OptionalParam(name = Organization.SP_ACTIVE)
	    TokenAndListParam theActive,
	    
	    @Description(shortDefinition = "The kind(s) of organization that this is.")
	    @OptionalParam(name = Organization.SP_TYPE)
	    TokenAndListParam theType,
	    
	    @Description(shortDefinition = "Visiting or postal addresses for the contact")
	    @OptionalParam(name = Organization.SP_ADDRESS_POSTALCODE)
	    StringAndListParam thePostalCode,
	    
	    @Description(shortDefinition = "Visiting or country addresses for the contact")
	    @OptionalParam(name = Organization.SP_ADDRESS_COUNTRY)
	    StringAndListParam theAddressCountry,
	    
	    @Description(shortDefinition = "Technical endpoints providing access to services operated for the organization")
	    @OptionalParam(name = Organization.SP_ENDPOINT)
	    ReferenceAndListParam theEndPoint,
	  
	    @Description(shortDefinition = "Visiting or  addresses for the contact")
	    @OptionalParam(name = Organization.SP_ADDRESS_USE)
	    TokenAndListParam theAddressUse,
	    
	    @Description(shortDefinition = "An Organization  name")
	    @OptionalParam(name = Organization.SP_NAME)
	    StringAndListParam theName,
	    
	    @Description(shortDefinition = "A city specified in an address")
	    @OptionalParam(name = Organization.SP_ADDRESS_CITY)
	    StringAndListParam theAddressCity,
	    
	    @Description(shortDefinition = "The value in any kind of telecom details of the organization")
	    @OptionalParam(name = "telecom")
	    StringAndListParam theTelecom,
	    
	    @IncludeParam(allow = {"*"})
	    Set<Include> theIncludes,

		@IncludeParam(reverse=true, allow = {"*"})
		Set<Include> theRevIncludes,


		@Sort
	    SortSpec theSort,
	
	    @Count
	    Integer theCount) {

			SearchParameterMap paramMap = new SearchParameterMap();
			paramMap.add(Organization.SP_RES_ID, theId);
			paramMap.add(Organization.SP_IDENTIFIER, theIdentifier);
			paramMap.add(Organization.SP_PARTOF, thePartOf);
			paramMap.add(Organization.SP_ADDRESS, theAddress);
			paramMap.add(Organization.SP_ADDRESS_STATE, theAddressState);
			paramMap.add(Organization.SP_ACTIVE, theActive);
			paramMap.add(Organization.SP_TYPE, theType);
			paramMap.add(Organization.SP_ADDRESS_CITY, theAddressCity);
			paramMap.add(Organization.SP_NAME, theName);
			paramMap.add(Organization.SP_ADDRESS_POSTALCODE, thePostalCode);
			paramMap.add(Organization.SP_ADDRESS_COUNTRY, theAddressCountry);
			paramMap.add(Organization.SP_ENDPOINT, theEndPoint);
			paramMap.add(Organization.SP_ADDRESS_USE, theAddressUse);
			paramMap.add("telecom", theTelecom);
			paramMap.setIncludes(theIncludes);
			paramMap.setSort(theSort);
			paramMap.setCount(theCount);

			final List<DafOrganization> results = service.search(paramMap);

			return new IBundleProvider() {
				final InstantDt published = InstantDt.withCurrentTime();

				@Override
				public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
					List<IBaseResource> organizationList = new ArrayList<IBaseResource>();
					List<String> ids = new ArrayList<String>();
					for (DafOrganization dafOrganization : results) {
						Organization organization = createOrganizationObject(dafOrganization);
						organizationList.add(organization);
						ids.add(((IdType)organization.getIdElement()).getResourceType()+"/"+((IdType)organization.getIdElement()).getIdPart());
					}

					if(theRevIncludes.size() >0 ){
						organizationList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
					}


					return organizationList;
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
	 * 
	 * This method converts DafDocumentReference object to DocumentReference object
	 */
	private Organization createOrganizationObject(DafOrganization dafOrganization) {
		Organization organization = new Organization();
		JSONObject organizationJsonObj = new JSONObject(dafOrganization.getData());

		// set versionId
		if (!(organizationJsonObj.isNull("meta"))) {
			if (!(organizationJsonObj.getJSONObject("meta").isNull("versionId"))) {
				organization.setId(new IdType(RESOURCE_TYPE, organizationJsonObj.getString("id") + "",
						organizationJsonObj.getJSONObject("meta").getString("versionId")));
			}else {
				organization.setId(new IdType(RESOURCE_TYPE, organizationJsonObj.getString("id") + "", VERSION_ID));
			}
		} else {
			organization.setId(new IdType(RESOURCE_TYPE, organizationJsonObj.getString("id") + "", VERSION_ID));
		}

		// set name
		if (!(organizationJsonObj.isNull("name"))) {
			organization.setName(organizationJsonObj.getString("name"));
		}

		// set active
		if (!(organizationJsonObj.isNull("active"))) {
			organization.setActive(organizationJsonObj.getBoolean("active"));
		}

		// set alias
		if (!(organizationJsonObj.isNull("alias"))) {
			List<StringType> aliasDtList = new ArrayList<StringType>();
			JSONArray aliasJSON = organizationJsonObj.getJSONArray("alias");
			int noOfAlias = aliasJSON.length();
			for (int i = 0; i < noOfAlias; i++) {
				String alias = aliasJSON.getString(i);
				StringType theAlias = new StringType();
				theAlias.setValue(alias);
				aliasDtList.add(theAlias);
			}
			organization.setAlias(aliasDtList);
		}

		// set telecom
		if (!(organizationJsonObj.isNull("telecom"))) {
			JSONArray telecomJSON = organizationJsonObj.getJSONArray("telecom");
			int numberOfTelecoms = organizationJsonObj.getJSONArray("telecom").length();
			List<ContactPoint> contactPointDtList = new ArrayList<ContactPoint>();

			for (int i = 0; i < numberOfTelecoms; i++) {
				ContactPoint phoneContact = new ContactPoint();
				if (!(telecomJSON.getJSONObject(i).isNull("system"))) {
					phoneContact.setSystem(ContactPoint.ContactPointSystem
							.fromCode(telecomJSON.getJSONObject(i).getString("system")));
				}
				if (!(telecomJSON.getJSONObject(i).isNull("use"))) {
					phoneContact.setUse(ContactPoint.ContactPointUse
							.fromCode(telecomJSON.getJSONObject(i).getString("use")));
				}

				if (!(telecomJSON.getJSONObject(i).isNull("value"))) {
					phoneContact.setValue(telecomJSON.getJSONObject(i).getString("value"));
				}
				contactPointDtList.add(phoneContact);
			}
			organization.setTelecom(contactPointDtList);
		}

		 //Set identifier
        if(!(organizationJsonObj.isNull("identifier"))) {
        	JSONArray identifierJSON = organizationJsonObj.getJSONArray("identifier");
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
        	organization.setIdentifier(identifiers);
        }

		// set address
		if (!(organizationJsonObj.isNull("address"))) {
			JSONArray addressJSON = organizationJsonObj.getJSONArray("address");
			int noOfAddresses = addressJSON.length();
			List<Address> addressDtList = new ArrayList<Address>();
			for (int i = 0; i < noOfAddresses; i++) {
				Address theAddress = new Address();
				if (!(addressJSON.getJSONObject(i).isNull("use"))) {
					theAddress.setUse(
							Address.AddressUse.fromCode(addressJSON.getJSONObject(i).getString("use")));
				}

				if (!(addressJSON.getJSONObject(i).isNull("line"))) {
					List<StringType> addressLines = new ArrayList<StringType>();
					StringType addressLine = new StringType();
					addressLine.setValue(addressJSON.getJSONObject(i).getJSONArray("line").toString());
					addressLines.add(addressLine);
					theAddress.setLine(addressLines);
				}

				if (!(addressJSON.getJSONObject(i).isNull("city"))) {
					theAddress.setCity(addressJSON.getJSONObject(i).getString("city"));
				}
				if (!(addressJSON.getJSONObject(i).isNull("state"))) {
					theAddress.setState(addressJSON.getJSONObject(i).getString("state"));
				}
				if (!(addressJSON.getJSONObject(i).isNull("postalCode"))) {
					theAddress.setPostalCode(addressJSON.getJSONObject(i).getString("postalCode"));

				}
				if (!(addressJSON.getJSONObject(i).isNull("country"))) {
					theAddress.setCountry(addressJSON.getJSONObject(i).getString("country"));

				}
				addressDtList.add(theAddress);
			}
			organization.setAddress(addressDtList);
		}

		// set PartOf
		if (!(organizationJsonObj.isNull("partOf"))) {
			Reference thePartOf = new Reference();
			if (!(organizationJsonObj.getJSONObject("partOf").isNull("reference"))) {
				thePartOf.setReference(organizationJsonObj.getJSONObject("partOf").getString("reference"));
			}
			if (!(organizationJsonObj.getJSONObject("partOf").isNull("display"))) {
				thePartOf.setDisplay(organizationJsonObj.getJSONObject("partOf").getString("display"));
			}
			if (!(organizationJsonObj.getJSONObject("partOf").isNull("type"))) {
				thePartOf.setType(organizationJsonObj.getJSONObject("partOf").getString("type"));
			}
			organization.setPartOf(thePartOf);
		}

		// set endPoint
		if (!(organizationJsonObj.isNull("endpoint"))) {
			JSONArray endPointJSON = organizationJsonObj.getJSONArray("endpoint");
			int noOfEndPoint = endPointJSON.length();
			List<Reference> endPointDtList = new ArrayList<Reference>();
			for (int i = 0; i < noOfEndPoint; i++) {
				Reference theEndPoint = new Reference();
				if (!(endPointJSON.getJSONObject(i).isNull("reference"))) {
					theEndPoint.setReference(endPointJSON.getJSONObject(i).getString("reference"));
				}
				if (!(endPointJSON.getJSONObject(i).isNull("display"))) {
					theEndPoint.setDisplay(endPointJSON.getJSONObject(i).getString("display"));
				}
				endPointDtList.add(theEndPoint);
			}
			organization.setEndpoint(endPointDtList);
		}

		// set type
		if (!(organizationJsonObj.isNull("type"))) {
			JSONArray typeJson = organizationJsonObj.getJSONArray("type");
			int noOfType = typeJson.length();
			List<CodeableConcept> typeDtList = new ArrayList<>();
			for (int j = 0; j < noOfType; j++) {
				CodeableConcept theType = new CodeableConcept();
				if (!(typeJson.getJSONObject(j).isNull("coding"))) {
					JSONArray typeCodingJSON = typeJson.getJSONObject(j).getJSONArray("coding");
					int noOfCoding = typeCodingJSON.length();
					List<Coding> codingDtList = new ArrayList<>();
					for (int i = 0; i < noOfCoding; i++) {
						Coding theCoding = new Coding();
						if (!(typeCodingJSON.getJSONObject(i).isNull("system"))) {
							theCoding.setSystem(typeCodingJSON.getJSONObject(i).getString("system"));
						}
						if (!(typeCodingJSON.getJSONObject(i).isNull("code"))) {
							theCoding.setCode(typeCodingJSON.getJSONObject(i).getString("code"));
						}
						if (!(typeCodingJSON.getJSONObject(i).isNull("display"))) {
							theCoding.setDisplay(typeCodingJSON.getJSONObject(i).getString("display"));
						}
						codingDtList.add(theCoding);
					}
					theType.setCoding(codingDtList);
				}
				typeDtList.add(theType);
			}
			organization.setType(typeDtList);
		}

		// set contact
		if (!(organizationJsonObj.isNull("contact"))) {
			JSONArray contactJSON = organizationJsonObj.getJSONArray("contact");
			int noOfContacts = contactJSON.length();
			List<OrganizationContactComponent> contactDtList = new ArrayList<>();
			for (int k = 0; k < noOfContacts; k++) {
				OrganizationContactComponent theContact = new OrganizationContactComponent();
				if (!(contactJSON.getJSONObject(k).isNull("address"))) {
					Address theAddress = new Address();

					if (!(contactJSON.getJSONObject(k).getJSONObject("address").isNull("postalCode"))) {
						theAddress.setPostalCode(contactJSON.getJSONObject(k).getJSONObject("address")
								.getString("postalCode"));
					}
					if (!(contactJSON.getJSONObject(k).getJSONObject("address").isNull("city"))) {
						theAddress.setCity(
								contactJSON.getJSONObject(k).getJSONObject("address").getString("city"));
					}
					if (!(contactJSON.getJSONObject(k).getJSONObject("address").isNull("country"))) {
						theAddress.setCountry(
								contactJSON.getJSONObject(k).getJSONObject("address").getString("country"));
					}

					if (!(contactJSON.getJSONObject(k).getJSONObject("address").isNull("line"))) {
						List<StringType> contactLineList = new ArrayList<>();
						JSONArray addressLineJSON = contactJSON.getJSONObject(k).getJSONObject("address").getJSONArray("line");
						for (int j = 0; j < addressLineJSON.length(); j++) {
							String contactLineAddress = addressLineJSON.getString(j);
							StringType theContactLine = new StringType();
							theContactLine.setValue(contactLineAddress);
							contactLineList.add(theContactLine);
						}
						theAddress.setLine(contactLineList);
					}
					theContact.setAddress(theAddress);
				}

				// set contact purpose
				if (!(contactJSON.getJSONObject(k).isNull("purpose"))) {
					CodeableConcept thePurpose = new CodeableConcept();

					if (!(contactJSON.getJSONObject(k).getJSONObject("purpose").isNull("coding"))) {
						JSONArray codingPurpose = contactJSON.getJSONObject(k).getJSONObject("purpose")
								.getJSONArray("coding");
						int noOfCoding = codingPurpose.length();
						List<Coding> purposeDtList = new ArrayList<Coding>();

						for (int i = 0; i < noOfCoding; i++) {
							Coding theCoding = new Coding();
							if (!(codingPurpose.getJSONObject(i).isNull("system"))) {
								theCoding.setSystem(codingPurpose.getJSONObject(i).getString("system"));
							}
							if (codingPurpose.getJSONObject(i).isNull("code")) {
								theCoding.setCode(codingPurpose.getJSONObject(i).getString("code"));
							}
							purposeDtList.add(theCoding);
						}
						thePurpose.setCoding(purposeDtList);
					}
					theContact.setPurpose(thePurpose);
				}

				// set name
				if (!(contactJSON.getJSONObject(k).isNull("name"))) {
					HumanName theName = new HumanName();

					if (!(contactJSON.getJSONObject(k).getJSONObject("name").isNull("use"))) {
						theName.setUse(HumanName.NameUse.fromCode(
								contactJSON.getJSONObject(k).getJSONObject("name").getString("use")));
					}
					if (!(contactJSON.getJSONObject(k).getJSONObject("name").isNull("text"))) {
						theName.setText(
								contactJSON.getJSONObject(k).getJSONObject("name").getString("text"));
					}
					if (!(contactJSON.getJSONObject(k).getJSONObject("name").isNull("family"))) {
						theName.setFamily(
								contactJSON.getJSONObject(k).getJSONObject("name").getString("family"));
					}
					if (!(contactJSON.getJSONObject(k).getJSONObject("name").isNull("given"))) {
						List<StringType> contactGivenList = new ArrayList<>();
						JSONArray contactGivenNamesJSON = contactJSON.getJSONObject(k).getJSONObject("name")
								.getJSONArray("given");
						for (int g = 0; g < contactGivenNamesJSON.length(); g++) {
							String givenName = contactGivenNamesJSON.getString(g);
							StringType theGiven = new StringType();
							theGiven.setValue(givenName);
							contactGivenList.add(theGiven);
						}
						theName.setGiven(contactGivenList);
					}

					if (!(contactJSON.getJSONObject(k).getJSONObject("name").isNull("prefix"))) {
						List<StringType> prefixList = new ArrayList<>();
						JSONArray contactNameJSON = contactJSON.getJSONObject(k).getJSONObject("name")
								.getJSONArray("prefix");
						for (int i = 0; i < contactNameJSON.length(); i++) {
							String prefix = contactNameJSON.getString(i);
							StringType thePrefix = new StringType();
							thePrefix.setValue(prefix);
							prefixList.add(thePrefix);
						}
						theName.setPrefix(prefixList);
					}
					theContact.setName(theName);
				}

				// set contact telecom
				if (!(contactJSON.getJSONObject(k).isNull("telecom"))) {
					JSONArray contactTelecomJson = contactJSON.getJSONObject(k).getJSONArray("telecom");
					int noOfTelecom = contactTelecomJson.length();
					List<ContactPoint> telecomDtList = new ArrayList<ContactPoint>();
					for (int i = 0; i < noOfTelecom; i++) {
						ContactPoint theContactPoint = new ContactPoint();
						if (!(contactTelecomJson.getJSONObject(i).isNull("system"))) {
							theContactPoint.setSystem(ContactPoint.ContactPointSystem
									.fromCode(contactTelecomJson.getJSONObject(i).getString("system")));
						}
						if (!(contactTelecomJson.getJSONObject(i).isNull("value"))) {
							theContactPoint.setValue(contactTelecomJson.getJSONObject(i).getString("value"));
						}
						if (!(contactTelecomJson.getJSONObject(i).isNull("use"))) {
							theContactPoint.setUse(ContactPoint.ContactPointUse
									.fromCode(contactTelecomJson.getJSONObject(i).getString("use")));
						}
						telecomDtList.add(theContactPoint);
					}
					theContact.setTelecom(telecomDtList);
				}
				contactDtList.add(theContact);
			}
			organization.setContact(contactDtList);
		}
		return organization;
	}
}
