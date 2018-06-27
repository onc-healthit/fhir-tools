package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Address.AddressUse;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.dstu3.model.Location.LocationMode;
import org.hl7.fhir.dstu3.model.Location.LocationPositionComponent;
import org.hl7.fhir.dstu3.model.Location.LocationStatus;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.query.LocationSearchCriteria;
import org.sitenv.spring.service.LocationService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocationResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Location";
    public static final String VERSION_ID = "2.0";
    AbstractApplicationContext context;
    LocationService service;

    public LocationResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (LocationService) context.getBean("locationResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must be
     * overridden to indicate what type of resource this provider supplies.
     */
    @Override
    public Class<Location> getResourceType() {
        return Location.class;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different method annotated with this
     * annotation, to support many different search criteria.
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available Location records.
     * <p>
     * Ex: http://<server
     * name>/<context>/fhir/Location?_pretty=true&_format=json
     */
    @Search
    public List<Location> getAllLocation(
            @IncludeParam(allow = "*") Set<Include> theIncludes,
            @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafLocation> dafLocations = service.getAllLocations();

        List<Location> locations = new ArrayList<Location>();

        for (DafLocation dafLocation : dafLocations) {
            locations.add(createLocationObject(dafLocation));
        }

        return locations;
    }

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this
     * method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the
     * {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type
     *              IdDt and must be annotated with the "@Read.IdParam"
     *              annotation.
     * @return Returns a resource matching this identifier, or null if none
     * exists.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Location/1?_format=json
     */
    @Read()
    public Location getLocationResourceById(@IdParam IdType theId) {

        DafLocation dafLocation = service.getLocationById(theId
                .getIdPartAsLong().intValue());

        Location location = createLocationObject(dafLocation);

        return location;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different method annotated with this
     * annotation, to support many different search criteria. This example
     * searches by patient
     *
     * @param theIdentifierValue
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of Locations. This list may contain
     * multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Location> searchByIdentifier(
            @RequiredParam(name = Location.SP_IDENTIFIER) TokenParam theIdentifierValue,
            @IncludeParam(allow = "*") Set<Include> theIncludes,
            @Sort SortSpec theSort, @Count Integer theCount) {
        LocationSearchCriteria locationSearchCriteria = new LocationSearchCriteria();
        locationSearchCriteria.setIdentifier(theIdentifierValue);
        List<DafLocation> dafLocationList = service
                .getLocationBySearchCriteria(locationSearchCriteria);

        List<Location> locationList = new ArrayList<Location>();

        for (DafLocation dafLocation : dafLocationList) {
            locationList.add(createLocationObject(dafLocation));
        }
        return locationList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different method annotated with this
     * annotation, to support many different search criteria. This example
     * searches by patient
     *
     * @param theName
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of Locations. This list may contain
     * multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Location> searchByName(
            @RequiredParam(name = Location.SP_NAME) StringDt theName,
            @IncludeParam(allow = "*") Set<Include> theIncludes,
            @Sort SortSpec theSort, @Count Integer theCount) {
        LocationSearchCriteria locationSearchCriteria = new LocationSearchCriteria();
        locationSearchCriteria.setName(theName.getValue());
        List<DafLocation> dafLocationList = service
                .getLocationBySearchCriteria(locationSearchCriteria);

        List<Location> locationList = new ArrayList<Location>();

        for (DafLocation dafLocation : dafLocationList) {
            locationList.add(createLocationObject(dafLocation));
        }
        return locationList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different method annotated with this
     * annotation, to support many different search criteria. This example
     * searches by patient
     *
     * @param theAddress
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of Locations. This list may contain
     * multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Location> searchByAddress(
            @RequiredParam(name = Location.SP_ADDRESS) StringDt theAddress,
            @IncludeParam(allow = "*") Set<Include> theIncludes,
            @Sort SortSpec theSort, @Count Integer theCount) {
        LocationSearchCriteria locationSearchCriteria = new LocationSearchCriteria();
        locationSearchCriteria.setAddress(theAddress.getValue());
        List<DafLocation> dafLocationList = service
                .getLocationBySearchCriteria(locationSearchCriteria);

        List<Location> locationList = new ArrayList<Location>();

        for (DafLocation dafLocation : dafLocationList) {
            locationList.add(createLocationObject(dafLocation));
        }
        return locationList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different method annotated with this
     * annotation, to support many different search criteria. This example
     * searches by patient
     *
     * @param thePostalCode
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of Locations. This list may contain
     * multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Location> searchByAddressPostalCode(
            @RequiredParam(name = Location.SP_ADDRESS_POSTALCODE) StringDt thePostalCode,
            @IncludeParam(allow = "*") Set<Include> theIncludes,
            @Sort SortSpec theSort, @Count Integer theCount) {
        LocationSearchCriteria locationSearchCriteria = new LocationSearchCriteria();
        locationSearchCriteria.setPostal(thePostalCode.getValue());
        List<DafLocation> dafLocationList = service
                .getLocationBySearchCriteria(locationSearchCriteria);

        List<Location> locationList = new ArrayList<Location>();

        for (DafLocation dafLocation : dafLocationList) {
            locationList.add(createLocationObject(dafLocation));
        }
        return locationList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different method annotated with this
     * annotation, to support many different search criteria. This example
     * searches by patient
     *
     * @param theCity
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of Locations. This list may contain
     * multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Location> searchByAddressCity(
            @RequiredParam(name = Location.SP_ADDRESS_CITY) StringDt theCity,
            @IncludeParam(allow = "*") Set<Include> theIncludes,
            @Sort SortSpec theSort, @Count Integer theCount) {
        LocationSearchCriteria locationSearchCriteria = new LocationSearchCriteria();
        locationSearchCriteria.setCity(theCity.getValue());
        List<DafLocation> dafLocationList = service
                .getLocationBySearchCriteria(locationSearchCriteria);

        List<Location> locationList = new ArrayList<Location>();

        for (DafLocation dafLocation : dafLocationList) {
            locationList.add(createLocationObject(dafLocation));
        }
        return locationList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different method annotated with this
     * annotation, to support many different search criteria. This example
     * searches by patient
     *
     * @param theState
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of Locations. This list may contain
     * multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Location> searchByAddressState(
            @RequiredParam(name = Location.SP_ADDRESS_STATE) StringDt theState,
            @IncludeParam(allow = "*") Set<Include> theIncludes,
            @Sort SortSpec theSort, @Count Integer theCount) {
        LocationSearchCriteria locationSearchCriteria = new LocationSearchCriteria();
        locationSearchCriteria.setState(theState.getValue());
        List<DafLocation> dafLocationList = service
                .getLocationBySearchCriteria(locationSearchCriteria);

        List<Location> locationList = new ArrayList<Location>();

        for (DafLocation dafLocation : dafLocationList) {
            locationList.add(createLocationObject(dafLocation));
        }
        return locationList;
    }

    /**
     * This method converts DafLocation object to Location object
     */
    private Location createLocationObject(DafLocation dafLocation) {

        Location location = new Location();

        // Set Version
        location.setId(new IdDt(RESOURCE_TYPE,
                dafLocation.getLocationId() + "", VERSION_ID));

        // Set Status
        location.setStatus(LocationStatus.valueOf(dafLocation.getStatus()));

        // set name
        location.setName(dafLocation.getName());

        // set description
        location.setDescription(dafLocation.getDescription());

        // set mode
        location.setMode(LocationMode.valueOf(dafLocation.getMode()));

        //set identifier
        Map<String, String> identifierSet = HapiUtils.convertToJsonMap(dafLocation
                .getIdentifier());
        List<Identifier> theIdentifier = new ArrayList<Identifier>();
        Identifier identifier = new Identifier();
        identifier.setSystem(identifierSet.get("system"));
        identifier.setValue(identifierSet.get("value"));
        theIdentifier.add(identifier);
        location.setIdentifier(theIdentifier);

        // set telecom
        Map<String, String> telecom = HapiUtils.convertToJsonMap(dafLocation
                .getTelecom());
        List<ContactPoint> contactPointList = new ArrayList<ContactPoint>();
        ContactPoint phoneContact = new ContactPoint();
        phoneContact.setSystem(ContactPointSystem.valueOf(telecom
                .get("system")));
        phoneContact.setUse(ContactPointUse.valueOf(telecom.get("use")));
        phoneContact.setValue(telecom.get("value"));
        contactPointList.add(phoneContact);
        location.setTelecom(contactPointList);

        // set address
        Map<String, String> addressSet = HapiUtils.convertToJsonMap(dafLocation
                .getAddress());

        Address address = new Address();
        address.setUse(AddressUse.valueOf(addressSet.get("use")));
        List<StringType> line = new ArrayList<StringType>();
        StringType line1 = new StringType(addressSet.get("line"));
        line.add(line1);
        address.setLine(line);
        address.setCity(addressSet.get("city"));
        address.setPostalCode(addressSet.get("postalCode"));
        address.setState(addressSet.get("state"));
        address.setCountry(addressSet.get("country"));
        location.setAddress(address);

        // set physicalType
        Map<String, String> physicalType = HapiUtils
                .convertToJsonMap(dafLocation.getPhysicaltype());
        CodeableConcept pt = new CodeableConcept();
        Coding codingPt = new Coding();
        codingPt.setSystem(physicalType.get("system"));
        codingPt.setCode(physicalType.get("code"));
        codingPt.setDisplay(physicalType.get("display"));
        pt.addCoding(codingPt);
        location.setPhysicalType(pt);

        // set position
        Map<String, String> position = HapiUtils.convertToJsonMap(dafLocation
                .getPosition());
        LocationPositionComponent p = new LocationPositionComponent();
        p.setLongitude(new BigDecimal(position.get("longitude")));
        p.setLatitude(new BigDecimal(position.get("latitude")));
        p.setAltitude(new BigDecimal(position.get("altitude")));
        location.setPosition(p);

        // set managingorganization
        Reference mResource = new Reference();
        String theId = "Organization/" + dafLocation.getManagingOrganization();
        mResource.setReference(theId);
        location.setManagingOrganization(mResource);

        //Set Operational Status
        Map<String, String> opearationalStatus = HapiUtils.convertToJsonMap(dafLocation
                .getOperational_status());
        Coding operationalCoding = new Coding();
        operationalCoding.setSystem(opearationalStatus.get("system"));
        operationalCoding.setCode(opearationalStatus.get("code"));
        operationalCoding.setDisplay(opearationalStatus.get("display"));
        location.setOperationalStatus(operationalCoding);

        //Set Alias
        List<StringType> aliasStrings = new ArrayList<StringType>();
        StringType theAlias = new StringType();
        theAlias.setValue(dafLocation.getAlias());
        aliasStrings.add(theAlias);
        location.setAlias(aliasStrings);

        return location;
    }
}
