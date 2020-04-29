package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.SpecialAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Location.LocationPositionComponent;
import org.hl7.fhir.r4.model.Location.LocationStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.service.LocationService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

//import ca.uhn.fhir.rest.api.server.IBundleProvider;

public class LocationResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Location";
	public static final String VERSION_ID = "1.0";
	AbstractApplicationContext context;
	LocationService service;

	public LocationResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		service = (LocationService) context.getBean("locationService");
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Location.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Location/1/_history/2
	 * @param theId : Id of the location
	 * @return : Object of location information
	 */
	
	@Read(version = true)
	public Location readOrVread(@IdParam IdType theId) {
		String id;
		DafLocation dafLocation;
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
			dafLocation = service.getLocationByVersionId(id, theId.getVersionIdPart());

		} else {
			// this is a read
			dafLocation = service.getLocationById(id);
		}
		return createLocationObject(dafLocation);
	}

	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=Location.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Location/1/_history
	 * @param theId : ID of the location
	 * @return : List of locations
	 */
	@History()
	public List<Location> getLocationHistoryById(@IdParam IdType theId) {

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
		List<DafLocation> dafLocationList = service.getLocationHistoryById(id);

		List<Location> LocationList = new ArrayList<Location>();
		for (DafLocation dafLocation : dafLocationList) {
			LocationList.add(createLocationObject(dafLocation));
		}
		return LocationList;
	}

	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theName
	 * @param theAddress
	 * @param theAddressCity
	 * @param theAddressState
	 * @param theAddressPostalCode
	 * @param theAddressCountry 
	 * @param theOrganization
	 * @param thePartOf
	 * @param theOperationalStatus
	 * @param theType
	 * @param theEndpoint
	 * @param theAddressUse
	 * @param theNear
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
        
        @Description(shortDefinition="The ID of the resource")
        @OptionalParam(name=Location.SP_RES_ID)
        StringAndListParam theId,
        
		@Description(shortDefinition="An identifier for the location")
        @OptionalParam(name=Location.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition="A portion of the location's name or alias")
        @OptionalParam(name=Location.SP_NAME)
        StringAndListParam theName,
        
		@Description(shortDefinition="A location of which this location is a part")
        @OptionalParam(name=Location.SP_PARTOF)
        ReferenceAndListParam thePartOf,
        
		@Description(shortDefinition="A (part of the) address of the location")
        @OptionalParam(name=Location.SP_ADDRESS)
        StringAndListParam theAddress,
        
		@Description(shortDefinition="A state specified in an address")
        @OptionalParam(name=Location.SP_ADDRESS_STATE)
        StringAndListParam theAddressState,
        
		@Description(shortDefinition="Searches for locations (typically bed/room) that have an operational status (e.g. contaminated, housekeeping)")
        @OptionalParam(name=Location.SP_OPERATIONAL_STATUS)
        TokenAndListParam theOperationalStatus,
        
		@Description(shortDefinition="A code for the type of location")
        @OptionalParam(name=Location.SP_TYPE)
        TokenAndListParam theType,
  
		@Description(shortDefinition="A postal code specified in an address")
        @OptionalParam(name=Location.SP_ADDRESS_POSTALCODE)
        StringAndListParam theAddressPostalCode,

		@Description(shortDefinition="A country specified in an address")
        @OptionalParam(name=Location.SP_ADDRESS_COUNTRY)
        StringAndListParam theAddressCountry,
        
        @Description(shortDefinition="Technical endpoints providing access to services operated for the location")
        @OptionalParam(name=Location.SP_ENDPOINT)
        ReferenceAndListParam theEndpoint,
        
        @Description(shortDefinition="Searches for locations that are managed by the provided organization")
        @OptionalParam(name=Location.SP_ORGANIZATION)
        ReferenceAndListParam theOrganization,
        
        @Description(shortDefinition="A use code specified in an address")
        @OptionalParam(name=Location.SP_ADDRESS_USE)
        TokenAndListParam theAddressUse,
        
        @Description(shortDefinition="Search for locations where the location.position is near to, or within a specified distance of, the provided coordinates expressed as [latitude]|[longitude]|[distance]|[units] (using the WGS84 datum, see notes).\r\n" + 
        		"If the units are omitted, then kms should be assumed. If the distance is omitted, then the server can use its own discression as to what distances should be considered near (and units are irrelevant)\r\n" + 
        		"\r\n" + 
        		"Servers may search using various techniques that might have differing accuracies, depending on implementation efficiency.\r\n" + 
        		"\r\n" + 
        		"Requires the near-distance parameter to be provided also")
        @OptionalParam(name=Location.SP_NEAR)
        SpecialAndListParam theNear,
   
        @Description(shortDefinition="A city specified in an address")
        @OptionalParam(name=Location.SP_ADDRESS_CITY)
        StringAndListParam theAddressCity,
    
        @Description(shortDefinition="Searches for locations with a specific kind of status")
        @OptionalParam(name=Location.SP_STATUS)
        TokenAndListParam theStatus,
        
        @IncludeParam(allow = { "*"})
        Set<Include> theIncludes,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
		
			SearchParameterMap searchParameterMap = new  SearchParameterMap();
			searchParameterMap.add(Location.SP_RES_ID,theId );
			searchParameterMap.add(Location.SP_IDENTIFIER,theIdentifier );
			searchParameterMap.add(Location.SP_NAME,theName );
			searchParameterMap.add(Location.SP_ADDRESS,theAddress );
			searchParameterMap.add(Location.SP_ADDRESS_CITY,theAddressCity );
			searchParameterMap.add(Location.SP_ADDRESS_COUNTRY,theAddressCountry );
			searchParameterMap.add(Location.SP_ADDRESS_POSTALCODE,theAddressPostalCode );
			searchParameterMap.add(Location.SP_ADDRESS_STATE,theAddressState );
			searchParameterMap.add(Location.SP_ADDRESS_USE,theAddressUse );
			searchParameterMap.add(Location.SP_ENDPOINT,theEndpoint );
			searchParameterMap.add(Location.SP_NEAR,theNear );
			searchParameterMap.add(Location.SP_ORGANIZATION,theOrganization );
			searchParameterMap.add(Location.SP_STATUS,theStatus );
			searchParameterMap.add(Location.SP_PARTOF,thePartOf);
			searchParameterMap.add(Location.SP_TYPE,theType );
			searchParameterMap.add(Location.SP_OPERATIONAL_STATUS,theOperationalStatus );
			searchParameterMap.setIncludes(theIncludes);
			searchParameterMap.setSort(theSort);
			searchParameterMap.setCount(theCount);
			
			final List<DafLocation> results = service.search(searchParameterMap);
			 return new IBundleProvider() {
	             final InstantDt published = InstantDt.withCurrentTime();
	             @Override
	             public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
	                 List<IBaseResource> locationList = new ArrayList<IBaseResource>();
					 List<String> ids = new ArrayList<String>();
					 for(DafLocation dafLocation : results){
						 Location location = createLocationObject(dafLocation);
						 locationList.add(location);
						 ids.add(((IdType)location.getIdElement()).getResourceType()+"/"+((IdType)location.getIdElement()).getIdPart());
	                 }
					 if(theRevIncludes.size() >0 ){
						 locationList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
					 }
					 return locationList;
	             }
				@Override
				public InstantDt getPublished() {
					return published;
				}
				@Override
				public String getUuid() {
					return null;
				}
				@Override
				public Integer preferredPageSize() {
					return null;
				}
				@Override
				public Integer size() {
					return results.size();
				}
			 
			 };
	}
	

    /**
     * This method converts DafDocumentReference object to DocumentReference object
     * @param dafLocation : DafDocumentReference location object
     * @return : DocumentReference location object
     */
	private Location createLocationObject(DafLocation dafLocation) {

		Location location = new Location();
		JSONObject locationJSON = new JSONObject(dafLocation.getData());

		// Set version
		if (!locationJSON.isNull("meta")) {
			if (!(locationJSON.getJSONObject("meta").isNull("versionId"))) {
				location.setId(new IdType(RESOURCE_TYPE, locationJSON.getString("id") + "",
						locationJSON.getJSONObject("meta").getString("versionId")));
			}else {
				location.setId(new IdType(RESOURCE_TYPE, locationJSON.getString("id") + "", VERSION_ID));
			}
		} else {
			location.setId(new IdType(RESOURCE_TYPE, locationJSON.getString("id") + "", VERSION_ID));
		}
		
		 //Set identifier
        if(!locationJSON.isNull("identifier")) {
        	JSONArray identifierJSON = locationJSON.getJSONArray("identifier");
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
        	location.setIdentifier(identifiers);
        }
		
		//Set PhysicalType
		if (!(locationJSON.isNull("physicalType"))) {
			JSONObject physicalTypeJSON = locationJSON.getJSONObject("physicalType");
			CodeableConcept thePhysicalType = new CodeableConcept();

			if (!(physicalTypeJSON.isNull("coding"))) {
				JSONArray physicalTypeCoding = physicalTypeJSON.getJSONArray("coding");
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
				thePhysicalType.setCoding(physicalTypeCodingList);
			}
			location.setPhysicalType(thePhysicalType);
		}
		
		//Set Description
		if (!locationJSON.isNull("description")) {
			location.setDescription(locationJSON.getString("description"));
		}
		//Set Name
		if (!locationJSON.isNull("name")) {
			location.setName(locationJSON.getString("name"));
		}
		//set Status
		if (!locationJSON.isNull("status")) {
			location.setStatus(LocationStatus.fromCode(locationJSON.getString("status")));
		}
		//Set OperationalStatus
		if(!locationJSON.isNull("operationalStatus")) {
			JSONObject operationalStatusJSON = locationJSON.getJSONObject("operationalStatus");
			Coding theOperationalStatus = new Coding();
			if(!operationalStatusJSON.isNull("system")) {
				theOperationalStatus.setSystem(operationalStatusJSON.getString("system"));
			}
			if(!operationalStatusJSON.isNull("code")) {
				theOperationalStatus.setCode(operationalStatusJSON.getString("code"));
			}
			if (!operationalStatusJSON.isNull("display")) {
				theOperationalStatus.setDisplay(operationalStatusJSON.getString("display"));
			}
			location.setOperationalStatus(theOperationalStatus);
		}

		//Set address
		if (!locationJSON.isNull("address")) {
			JSONObject addressJSON = locationJSON.getJSONObject("address");
			Address theAddress = new Address();

			if (!(addressJSON.isNull("use"))) {
				theAddress.setUse(Address.AddressUse.fromCode(addressJSON.getString("use")));
			}

			if (!(addressJSON.isNull("type"))) {
				theAddress.setType(Address.AddressType.fromCode(addressJSON.getString("type")));
			}

			if (!(addressJSON.isNull("line"))) {
				List<StringType> addressLines = new ArrayList<StringType>();
				StringType addressLine = new StringType();
				addressLine.setValue(addressJSON.getJSONArray("line").toString());
				addressLines.add(addressLine);
				theAddress.setLine(addressLines);
			}

			if (!(addressJSON.isNull("text"))) {
				theAddress.setText(addressJSON.getString("text"));
			}
			if (!(addressJSON.isNull("state"))) {
				theAddress.setState(addressJSON.getString("state"));
			}
			if (!(addressJSON.isNull("city"))) {
				theAddress.setCity(addressJSON.getString("city"));
			}

			if (!(addressJSON.isNull("postalCode"))) {
				theAddress.setPostalCode(addressJSON.getString("postalCode"));

			}
			if (!(addressJSON.isNull("country"))) {
				theAddress.setCountry(addressJSON.getString("country"));

			}
			location.setAddress(theAddress);
		}

		//Set alias
		if (!locationJSON.isNull("alias")) {
			List<StringType> aliasList = new ArrayList<StringType>();
			StringType theAlias = new StringType();
			String alias = locationJSON.getJSONArray("alias").toString();
			theAlias.setValue(alias);
			aliasList.add(theAlias);
			location.setAlias(aliasList);
		}

		//Set managingOrganization
        if(!(locationJSON.isNull("managingOrganization"))) {
        	JSONObject managingOrganizationJSON = locationJSON.getJSONObject("managingOrganization");
            Reference theReference = new Reference();
            if(!(managingOrganizationJSON.isNull("reference"))) {
            	theReference.setReference(managingOrganizationJSON.getString("reference"));
            }
            if(!(managingOrganizationJSON.isNull("display"))) {
            	theReference.setDisplay(managingOrganizationJSON.getString("display"));
            }
            if(!(managingOrganizationJSON.isNull("type"))) {
            	theReference.setType(managingOrganizationJSON.getString("type"));
            }
     	    location.setManagingOrganization(theReference);
        }
        //set type
        if (!locationJSON.isNull("type")) { 
        	  JSONArray typeJSON = locationJSON.getJSONArray("type"); 
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
		  location.setType(typeLists); 
		}

        //set endPoint
        if (!locationJSON.isNull("endpoint")) {
			JSONArray endpointJSON = locationJSON.getJSONArray("endpoint");
			int noOfEndpoints = endpointJSON.length();
			List<Reference> endpointLists = new ArrayList<Reference>();
			for (int i = 0; i < noOfEndpoints; i++) {
				Reference theEndpoint = new Reference();
				
				if (!endpointJSON.getJSONObject(i).isNull("reference")) {
					theEndpoint.setReference(endpointJSON.getJSONObject(i).getString("reference"));
				}
				if (!endpointJSON.getJSONObject(i).isNull("display")) {
					theEndpoint.setDisplay(endpointJSON.getJSONObject(i).getString("display"));
				}
				if (!endpointJSON.getJSONObject(i).isNull("type")) {
					theEndpoint.setType(endpointJSON.getJSONObject(i).getString("type"));
				}
				endpointLists.add(theEndpoint);
			}
			location.setEndpoint(endpointLists);
		}
        
        //set partOf
        if (!locationJSON.isNull("partOf")) {
			JSONObject partOfJSON = locationJSON.getJSONObject("partOf");
			Reference thePartOf = new Reference();
			if (!partOfJSON.isNull("reference")) {
				thePartOf.setReference(partOfJSON.getString("reference"));
			}
			if (!partOfJSON.isNull("display")) {
				thePartOf.setDisplay(partOfJSON.getString("display"));
			}
			if (!partOfJSON.isNull("type")) {
				thePartOf.setType(partOfJSON.getString("type"));
			}
			location.setPartOf(thePartOf);
		}
        
        //set position
        if (!locationJSON.isNull("position")) {
        	JSONObject positionJSON = locationJSON.getJSONObject("position");
        	LocationPositionComponent thePosition = new  LocationPositionComponent();
        	if (!positionJSON.isNull("longitude")) {
        		thePosition.setLongitude(positionJSON.getDouble("longitude"));
			}
			if (!positionJSON.isNull("latitude")) {
				double latitude = positionJSON.getDouble("latitude");
				thePosition.setLatitude(latitude);
				
			}
			if (!positionJSON.isNull("altitude")) {
				thePosition.setAltitude(positionJSON.getDouble("altitude"));
			}
			location.setPosition(thePosition);
		}

		if(!locationJSON.isNull("telecom")){
			JSONArray telecomJSON = locationJSON.getJSONArray("telecom");
			int noOfTelecoms = telecomJSON.length();
			List<ContactPoint> contactPointDtList = new ArrayList<ContactPoint>();
			for(int t = 0; t < noOfTelecoms; t++) {
				ContactPoint phoneContact = new ContactPoint();
				if(!(telecomJSON.getJSONObject(t).isNull("system"))) {
					phoneContact.setSystem(ContactPoint.ContactPointSystem.fromCode(telecomJSON.getJSONObject(t).getString("system")));
				}
				if(!(telecomJSON.getJSONObject(t).isNull("value"))) {
					phoneContact.setValue(telecomJSON.getJSONObject(t).getString("value"));
				}
				contactPointDtList.add(phoneContact);
			}
			location.setTelecom(contactPointDtList);
		}

        return location;
      }
}
		

        

