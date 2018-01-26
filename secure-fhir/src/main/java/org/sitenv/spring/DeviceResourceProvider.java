package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.Device;
import ca.uhn.fhir.model.dstu2.valueset.DeviceStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierTypeCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.NarrativeStatusEnum;
import ca.uhn.fhir.model.primitive.BoundCodeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.XhtmlDt;
import ca.uhn.fhir.model.valueset.BundleEntryTransactionMethodEnum;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.query.DeviceSearchCriteria;
import org.sitenv.spring.service.DeviceService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.*;

public class DeviceResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Device";
    public static final String VERSION_ID = "2.0";
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
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available Device records.
     * 
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
    
	public List<Device> getDeviceForBulkDataRequest(List<Integer> patients, Date start) {
    	
    	
		
		List<DafDevice> dafDeviceList = service.getDeviceForBulkData(patients, start);

		List<Device> deviceList = new ArrayList<Device>();

		for (DafDevice dafDevice : dafDeviceList) {
			deviceList.add(createDeviceObject(dafDevice));
		}
		
		return deviceList;
	}
    

	/**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     * 
     *  Ex: http://<server name>/<context>/fhir/Device/1?_format=json
     */
    @Read()
    public Device getDeviceResourceById(@IdParam IdDt theId) {

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
     * 
     *  Ex: http://<server name>/<context>/fhir/Device?patient=1&_format=json
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
        device.setId(new IdDt(RESOURCE_TYPE, dafDevice.getId() + "", VERSION_ID));

        //Set Status
        BoundCodeDt<DeviceStatusEnum> statenum = new BoundCodeDt(
                BundleEntryTransactionMethodEnum.VALUESET_BINDER);
        statenum.setValue(dafDevice.getStatus());
        device.setStatus(statenum);

        //set udi
        device.setUdi(dafDevice.getUdi());

        //set patient
        ResourceReferenceDt patientResource = new ResourceReferenceDt();
        String theId = "Patient/" + Integer.toString(dafDevice.getPatient().getId());
        patientResource.setReference(theId);
        device.setPatient(patientResource);

        NarrativeDt narrativeDt = new NarrativeDt();
        BoundCodeDt<NarrativeStatusEnum> narenum = new BoundCodeDt(
                BundleEntryTransactionMethodEnum.VALUESET_BINDER);
        narenum.setValue(dafDevice.getText_status());
        narrativeDt.setStatus(narenum);
        XhtmlDt xhtmlDt = new XhtmlDt();
        xhtmlDt.setValueAsString(dafDevice.getText_div());
        narrativeDt.setDiv(xhtmlDt);
        device.setText(narrativeDt);

        //set type
        CodeableConceptDt codeableConceptDt = new CodeableConceptDt();
        CodingDt codingDt = new CodingDt();
        codingDt.setCode(dafDevice.getType_code());
        codingDt.setSystem(dafDevice.getType_system());
        codingDt.setDisplay(dafDevice.getType_display());
        codeableConceptDt.addCoding(codingDt);
        device.setType(codeableConceptDt);

        //set identifier
        List<IdentifierDt> identifierDts = new ArrayList<IdentifierDt>();
        IdentifierDt identifierDt = new IdentifierDt();
        identifierDt.setValue(dafDevice.getIdentifier_value());
        identifierDt.setSystem(dafDevice.getIdentifier_system());

        IdentifierDt typeIdentifier = new IdentifierDt();
        BoundCodeableConceptDt<IdentifierTypeCodesEnum> boundCodeableConceptDt = new BoundCodeableConceptDt<IdentifierTypeCodesEnum>();
        CodingDt identifiercode = new CodingDt();
        identifiercode.setCode(dafDevice.getIdentifier_type_code());
        identifiercode.setSystem(dafDevice.getIdentifier_type_system());
        boundCodeableConceptDt.addCoding(identifiercode);
        boundCodeableConceptDt.setText(dafDevice.getIdentifier_type_text());
        typeIdentifier.setType(boundCodeableConceptDt);
        typeIdentifier.setValue(dafDevice.getIdentifier_type_value());
        identifierDts.add(identifierDt);
        identifierDts.add(typeIdentifier);
        device.setIdentifier(identifierDts);

        return device;
    }

}
