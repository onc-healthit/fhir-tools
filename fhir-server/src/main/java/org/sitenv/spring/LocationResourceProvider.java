package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.Location.Position;
import ca.uhn.fhir.model.dstu2.valueset.*;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
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
    @Read(version = true)
    public Location readOrVread(@IdParam IdDt theId) {
        int id;
        try {
            if (theId.hasVersionIdPart()) {
                id = Integer.parseInt(theId.getValue().split("/")[1]);
            }else {
                id = theId.getIdPartAsLong().intValue();
            }
            DafLocation dafLocation = service.getLocationById(theId.getIdPartAsLong().intValue());
            return createLocationObject(dafLocation);
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException(theId);
        }
    }

    @History()
    public Location getLocationHistory(@IdParam IdDt theId) {
        int id;
        try {
            id = Integer.parseInt(theId.getValue().split("/")[1]);
            DafLocation dafLocation = service.getLocationById(theId.getIdPartAsLong().intValue());
            return createLocationObject(dafLocation);
        } catch (Exception e) {
            throw new ResourceNotFoundException(theId);
        }
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
        location.setStatus(LocationStatusEnum.valueOf(dafLocation.getStatus()));

        // set name
        location.setName(dafLocation.getName());

        // set description
        location.setDescription(dafLocation.getDescription());

        // set mode
        location.setMode(LocationModeEnum.valueOf(dafLocation.getMode()));

        //set identifier
        Map<String, String> identifier = HapiUtils.convertToJsonMap(dafLocation
                .getIdentifier());
        List<IdentifierDt> theIdentifier = new ArrayList<IdentifierDt>();
        IdentifierDt identifierdt = new IdentifierDt();
        identifierdt.setSystem(identifier.get("system"));
        identifierdt.setValue(identifier.get("value"));
        theIdentifier.add(identifierdt);
        location.setIdentifier(theIdentifier);

        // set telecom
        Map<String, String> telecom = HapiUtils.convertToJsonMap(dafLocation
                .getTelecom());
        List<ContactPointDt> contactPointDtList = new ArrayList<ContactPointDt>();
        ContactPointDt phoneContact = new ContactPointDt();
        phoneContact.setSystem(ContactPointSystemEnum.valueOf(telecom
                .get("system")));
        phoneContact.setUse(ContactPointUseEnum.valueOf(telecom.get("use")));
        phoneContact.setValue(telecom.get("value"));
        contactPointDtList.add(phoneContact);
        location.setTelecom(contactPointDtList);

        // set address
        Map<String, String> address = HapiUtils.convertToJsonMap(dafLocation
                .getAddress());

        AddressDt addressdt = new AddressDt();
        addressdt.setUse(AddressUseEnum.valueOf(address.get("use")));
        List<StringDt> linedt = new ArrayList<StringDt>();
        StringDt line1 = new StringDt(address.get("line"));
        linedt.add(line1);
        addressdt.setLine(linedt);
        addressdt.setCity(address.get("city"));
        addressdt.setPostalCode(address.get("postalCode"));
        addressdt.setState(address.get("state"));
        addressdt.setCountry(address.get("country"));
        location.setAddress(addressdt);

        // set physicalType
        Map<String, String> physicalType = HapiUtils
                .convertToJsonMap(dafLocation.getPhysicaltype());
        CodeableConceptDt pt = new CodeableConceptDt();
        CodingDt codingPt = new CodingDt();
        codingPt.setSystem(physicalType.get("system"));
        codingPt.setCode(physicalType.get("code"));
        codingPt.setDisplay(physicalType.get("display"));
        pt.addCoding(codingPt);
        location.setPhysicalType(pt);

        // set position
        Map<String, String> position = HapiUtils.convertToJsonMap(dafLocation
                .getPosition());
        Position p = new Position();
        p.setLongitude(new BigDecimal(position.get("longitude")));
        p.setLatitude(new BigDecimal(position.get("latitude")));
        p.setAltitude(new BigDecimal(position.get("altitude")));
        location.setPosition(p);

        // set managingorganization
        ResourceReferenceDt mResource = new ResourceReferenceDt();
        String theId = "Organization/" + dafLocation.getManagingOrganization();
        mResource.setReference(theId);
        location.setManagingOrganization(mResource);

        return location;
    }
}
