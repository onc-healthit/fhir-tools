package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Device.DeviceUdiComponent;
import org.hl7.fhir.dstu3.model.Device.FHIRDeviceStatus;
import org.hl7.fhir.dstu3.model.Device.UDIEntryType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.query.DeviceSearchCriteria;
import org.sitenv.spring.service.DeviceService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeviceResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Device";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    DeviceService service;

    public DeviceResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (DeviceService) context.getBean("deviceResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<Device> getResourceType() {
        return Device.class;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available Device records.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Device?_pretty=true&_format=json
     */
    @Search
    public List<Device> getAllDevice(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafDevice> dafDevices = service.getAllDevice();

        List<Device> devices = new ArrayList<Device>();

        for (DafDevice dafDevice : dafDevices) {
            devices.add(createDeviceObject(dafDevice));
        }

        return devices;
    }

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdType and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Device/1?_format=json
     */
    @Read()
    public Device getDeviceResourceById(@IdParam IdType theId) {

        DafDevice dafDevice = service.getDeviceById(theId.getIdPartAsLong().intValue());

        Device device = createDeviceObject(dafDevice);

        return device;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of Devices. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Device?patient=1&_format=json
     */
    @Search()
    public List<Device> searchByPatient(@RequiredParam(name = Device.SP_PATIENT) ReferenceParam thePatient,
                                        @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        DeviceSearchCriteria deviceSearchCriteria = new DeviceSearchCriteria();
        deviceSearchCriteria.setPatient(Integer.parseInt(patientId));
        List<DafDevice> dafDeviceList = service.getDeviceBySearchCriteria(deviceSearchCriteria);

        List<Device> deviceList = new ArrayList<Device>();

        for (DafDevice dafDevice : dafDeviceList) {
            deviceList.add(createDeviceObject(dafDevice));
        }
        return deviceList;
    }

    /**
     * This method converts DafDevice object to Device object
     */
    private Device createDeviceObject(DafDevice dafDevice) {

        Device device = new Device();

        //Set Version
        device.setId(new IdType(RESOURCE_TYPE, dafDevice.getId() + "", VERSION_ID));

        //Set Status
        device.setStatus(FHIRDeviceStatus.valueOf(dafDevice.getStatus()));

        //set udi
        Map<String, String> udiValues = HapiUtils.convertToJsonMap(dafDevice.getUdi());
        DeviceUdiComponent udi = new DeviceUdiComponent();
        udi.setDeviceIdentifier(udiValues.get("deviceIdentifier"));
        udi.setName(udiValues.get("name"));
        udi.setJurisdiction(udiValues.get("jurisdiction"));
        udi.setCarrierHRF(udiValues.get("carrierHRF"));
        udi.setIssuer(udiValues.get("issuer"));

        Base64BinaryType value = new Base64BinaryType();
        value.setValueAsString(udiValues.get("carrierAIDC"));
        udi.setCarrierAIDCElement(value);
        udi.setEntryType(UDIEntryType.valueOf(udiValues.get("entryType")));
        device.setUdi(udi);

        //set patient
        Reference patientResource = new Reference();
        String theId = "Patient/" + Integer.toString(dafDevice.getPatient().getId());
        patientResource.setReference(theId);
        device.setPatient(patientResource);

        Narrative narrativeDt = new Narrative();
        narrativeDt.setStatus(Narrative.NarrativeStatus.valueOf(dafDevice.getText_status()));

        XhtmlNode xhtmlDt = new XhtmlNode();
        xhtmlDt.setValueAsString(dafDevice.getText_div());
        narrativeDt.setDiv(xhtmlDt);
        device.setText(narrativeDt);

        //set type
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setCode(dafDevice.getType_code());
        coding.setSystem(dafDevice.getType_system());
        coding.setDisplay(dafDevice.getType_display());
        codeableConcept.addCoding(coding);
        device.setType(codeableConcept);

        //set identifier
        List<Identifier> identifierDts = new ArrayList<Identifier>();
        Identifier identifierDt = new Identifier();
        identifierDt.setValue(dafDevice.getIdentifier_value());
        identifierDt.setSystem(dafDevice.getIdentifier_system());
        Identifier typeIdentifier = new Identifier();

      /*  Identifier typeIdentifier = new Identifier();
        boundCodeableConcept<Identifier.IdentifierUse> boundCodeableConceptDt = new BoundCodeableConcept<IdentifierTypeCodesEnum>();
        Coding identifiercode = new Coding();
        identifiercode.setCode(dafDevice.getIdentifier_type_code());
        identifiercode.setSystem(dafDevice.getIdentifier_type_system());
        boundCodeableConcept.addCoding(identifiercode);
        boundCodeableConcept.setText(dafDevice.getIdentifier_type_text());*/


        List<Coding> theCoding = new ArrayList<Coding>();
        Coding typeCoding = new Coding();
        typeCoding.setCode(dafDevice.getIdentifier_type_code());
        typeCoding.setSystem(dafDevice.getIdentifier_type_system());
        theCoding.add(typeCoding);
        CodeableConcept type = new CodeableConcept();
        type.setCoding(theCoding);
        type.setText(dafDevice.getIdentifier_type_text());
        typeIdentifier.setType(type);
        typeIdentifier.setValue(dafDevice.getIdentifier_type_value());
        identifierDts.add(identifierDt);
        identifierDts.add(typeIdentifier);
        device.setIdentifier(identifierDts);

        device.setExpirationDate(dafDevice.getExpirationDate());

        Map<String, String> safetyValues = HapiUtils.convertToJsonMap(dafDevice.getSafety());
        List<CodeableConcept> safetyccList = new ArrayList<CodeableConcept>();
        CodeableConcept safety = new CodeableConcept();
        List<Coding> safetyCodingList = new ArrayList<Coding>();
        Coding sc = new Coding();
        sc.setSystem(safetyValues.get("system"));
        sc.setCode(safetyValues.get("code"));
        sc.setDisplay(safetyValues.get("display"));
        safetyCodingList.add(sc);
        safety.setCoding(safetyCodingList);
        safetyccList.add(safety);
        device.setSafety(safetyccList);

        return device;
    }

}
