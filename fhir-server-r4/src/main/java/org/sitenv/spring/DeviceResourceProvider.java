package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Device.DeviceDeviceNameComponent;
import org.hl7.fhir.r4.model.Device.DeviceUdiCarrierComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.service.DeviceService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.*;

public class DeviceResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Device";
	public static final String VERSION_ID = "1.0";
	AbstractApplicationContext context;
	DeviceService service;

	public DeviceResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		service = (DeviceService) context.getBean("deviceService");
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Device.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Device/1/_history/4
	 * @param theId : Id of the Device
	 * @return : Object of Device information
	 */
	@Read(version = true)
	public Device readOrVread(@IdParam IdType theId) {
		String id;
		DafDevice dafDevice;
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
			dafDevice = service.getDeviceByVersionId(id, theId.getVersionIdPart());

		} else {

			dafDevice = service.getDeviceById(id);
		}

		return createDeviceObject(dafDevice);
	}

	/**
	 * The history operation retrieves a historical collection of all versions of a
	 * single resource (instance history). History methods must be annotated with
	 * the @History annotation.It supports Instance History method.
	 * "type=Device.class". Instance level (history of a specific resource instance
	 * by type and ID) The method must have a parameter annotated with the @IdParam
	 * annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<servername>/<context>/fhir/Device/1/_history
	 * @param theId : ID of the Device
	 * @return : List of Device's
	 */
	@History()
	public List<Device> getDeviceHistoryById(@IdParam IdType theId) {

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
		List<DafDevice> dafDeviceList = service.getDeviceHistoryById(id);
		List<Device> deviceList = new ArrayList<Device>();
		for (DafDevice dafDevice : dafDeviceList) {
			deviceList.add(createDeviceObject(dafDevice));
		}
		return deviceList;
	}

	/**
	 * The "@Search" annotation indicates that this method supports the search
	 * operation. You may have many different method annotated with this annotation,
	 * to support many different search criteria. The search operation returns a
	 * bundle with zero-to-many resources of a given type, matching a given set of
	 * parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theCarrier
	 * @param theManufacturer
	 * @param theExpirationDate
	 * @param theSerialNumber
	 * @param theDeviceName
	 * @param theType
	 * @param theModelNumber
	 * @param thePatient
	 * @param theOwner
	 * @param theIncludes
	 * @param theRevIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(javax.servlet.http.HttpServletRequest theServletRequest,

		@Description(shortDefinition = "The resource identity")
		@OptionalParam(name = Device.SP_RES_ID)
		StringAndListParam theId,

		@Description(shortDefinition = "An Device  identifier") 
		@OptionalParam(name = Device.SP_IDENTIFIER) 
		TokenAndListParam theIdentifier,

		@Description(shortDefinition = "Unique device identifier (UDI) assigned to device label or package")
		@OptionalParam(name = Device.SP_UDI_CARRIER) 
		StringAndListParam theCarrier,

		@Description(shortDefinition = "Status of the Device availability")
		@OptionalParam(name = Device.SP_STATUS) 
		TokenAndListParam theStatus,

		@Description(shortDefinition = "A name of the manufacturer")
		@OptionalParam(name = Device.SP_MANUFACTURER) 
		StringAndListParam theManufacturer,

		@Description(shortDefinition = "he date and time beyond which this device is no longer valid or should not be used")
		@OptionalParam(name = "expiration-date") 
		DateRangeParam theExpirationDate,

		@Description(shortDefinition = "Lot number assigned by the manufacturer") 
		@OptionalParam(name = "lot-number")
		StringAndListParam thelotNumber,

		@Description(shortDefinition = "The serial number assigned by the organization when the device was manufactured") 
		@OptionalParam(name = "serial-number") 
		StringAndListParam theSerialNumber,

		@Description(shortDefinition = "The type of deviceName")
		@OptionalParam(name = Device.SP_DEVICE_NAME)
		StringAndListParam theDeviceName,

		@Description(shortDefinition = "Technical endpoints providing access to services operated for the organization")
		@OptionalParam(name = Device.SP_TYPE) 
		TokenAndListParam theType,

		@Description(shortDefinition = "The model number for the device") 
		@OptionalParam(name = "model-number")
		StringAndListParam theModelNumber,

		@Description(shortDefinition = "Patient information, If the device is affixed to a person")
		@OptionalParam(name = Device.SP_PATIENT)
		StringAndListParam thePatient,

		@Description(shortDefinition = "An organization that is responsible for the provision and ongoing maintenance of the device")
		@OptionalParam(name = "owner")
		StringAndListParam theOwner,

		@IncludeParam(allow = {"*"})
		Set<Include> theIncludes,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,

		@Sort SortSpec theSort,

		@Count Integer theCount) {
		
		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(Device.SP_RES_ID, theId);
		paramMap.add(Device.SP_IDENTIFIER, theIdentifier);
		paramMap.add(Device.SP_UDI_CARRIER, theCarrier);
		paramMap.add(Device.SP_STATUS, theStatus);
		paramMap.add(Device.SP_MANUFACTURER, theManufacturer);
		paramMap.add("expiration-date", theExpirationDate);
		paramMap.add("lot-number", thelotNumber);
		paramMap.add("serial-number", theSerialNumber);
		paramMap.add(Device.SP_DEVICE_NAME, theDeviceName);
		paramMap.add(Device.SP_TYPE, theType);
		paramMap.add(Device.SP_PATIENT, thePatient);
		paramMap.add("owner", theOwner);
		paramMap.add("model-number", theModelNumber);
		paramMap.setSort(theSort);
		paramMap.setCount(theCount);

		final List<DafDevice> results = service.search(paramMap);

		return new IBundleProvider() {
			final InstantDt published = InstantDt.withCurrentTime();

			@Override
			public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
				List<IBaseResource> deviceList = new ArrayList<IBaseResource>();
				List<String> ids = new ArrayList<String>();
				for (DafDevice dafDevice : results) {
					Device device= createDeviceObject(dafDevice);
					deviceList.add(device);
					ids.add(((IdType)device.getIdElement()).getResourceType()+"/"+((IdType)device.getIdElement()).getIdPart());
				}
				if(theRevIncludes.size() >0 ){
					deviceList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
				}
				return deviceList;
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
	private Device createDeviceObject(DafDevice dafDevice) {

		Device device = new Device();
		JSONObject deviceJSONObj = new JSONObject(dafDevice.getData());

		// Set version
		if (!(deviceJSONObj.isNull("meta"))) {
			if (!(deviceJSONObj.getJSONObject("meta").isNull("versionId"))) {
				device.setId(new IdType(RESOURCE_TYPE, deviceJSONObj.getString("id") + "",
						deviceJSONObj.getJSONObject("meta").getString("versionId")));
			}else {
				device.setId(new IdType(RESOURCE_TYPE, deviceJSONObj.getString("id") + "", VERSION_ID));
			}
		} else {
			device.setId(new IdType(RESOURCE_TYPE, deviceJSONObj.getString("id") + "", VERSION_ID));
		}

		// Set status
		if (!(deviceJSONObj.isNull("expirationDate"))) {
			device.setStatus(Device.FHIRDeviceStatus.fromCode(deviceJSONObj.getString("status")));
		}

		// Set distinctIdentifier
		if (!(deviceJSONObj.isNull("distinctIdentifier"))) {
			device.setDistinctIdentifier(deviceJSONObj.getString("distinctIdentifier"));
		}

		// Set manufactureDate
		if (!(deviceJSONObj.isNull("manufactureDate"))) {
			String dateInStr = (String) deviceJSONObj.get("manufactureDate");
			Date theManufactureDate = CommonUtil.convertStringToDate(dateInStr);
			device.setManufactureDate(theManufactureDate);
		}

		// set expirationDate
		if (!(deviceJSONObj.isNull("expirationDate"))) {
			String dateInStr = (String) deviceJSONObj.get("expirationDate");
			Date theExpirationDate = CommonUtil.convertStringToDate(dateInStr);
			device.setExpirationDate(theExpirationDate);
		}


		 //Set identifier
        if(!(deviceJSONObj.isNull("identifier"))) {
        	JSONArray identifierJSON = deviceJSONObj.getJSONArray("identifier");
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
        	device.setIdentifier(identifiers);
        }

		// set deviceName
		if (!(deviceJSONObj.isNull("deviceName"))) {
			JSONArray deviceNameJSON = deviceJSONObj.getJSONArray("deviceName");
			int numberOfDevices = deviceNameJSON.length();
			List<DeviceDeviceNameComponent> devicesList = new ArrayList<DeviceDeviceNameComponent>();
			for (int i = 0; i < numberOfDevices; i++) {
				DeviceDeviceNameComponent theDevices = new DeviceDeviceNameComponent();
				if (!(deviceNameJSON.getJSONObject(i).isNull("name"))) {

					theDevices.setName(deviceNameJSON.getJSONObject(i).getString("name"));
				}
				if (!(deviceNameJSON.getJSONObject(i).isNull("type"))) {

					theDevices.setType(Device.DeviceNameType.fromCode(deviceNameJSON.getJSONObject(i).getString("type")));
				}
				devicesList.add(theDevices);
			}
			device.setDeviceName(devicesList);
		}

		// set udiCarrier
		if (!(deviceJSONObj.isNull("udiCarrier"))) {
			JSONArray udiCarrierJSON = deviceJSONObj.getJSONArray("udiCarrier");
			int numberOfCarrier = udiCarrierJSON.length();
			List<DeviceUdiCarrierComponent> udiCarrierList = new ArrayList<DeviceUdiCarrierComponent>();
			for (int i = 0; i < numberOfCarrier; i++) {
				DeviceUdiCarrierComponent theUdiCarrier = new DeviceUdiCarrierComponent();
				if (!(udiCarrierJSON.getJSONObject(i).isNull("deviceIdentifier"))) {
					theUdiCarrier.setDeviceIdentifier(udiCarrierJSON.getJSONObject(i).getString("deviceIdentifier"));
				}
				if (!(udiCarrierJSON.getJSONObject(i).isNull("issuer"))) {
					theUdiCarrier.setIssuer(udiCarrierJSON.getJSONObject(i).getString("issuer"));
				}
				if (!(udiCarrierJSON.getJSONObject(i).isNull("jurisdiction"))) {
					theUdiCarrier.setJurisdiction(udiCarrierJSON.getJSONObject(i).getString("jurisdiction"));
				}
				if (!(udiCarrierJSON.getJSONObject(i).isNull("carrierHRF"))) {
					theUdiCarrier.setCarrierHRF(udiCarrierJSON.getJSONObject(i).getString("carrierHRF"));
				}

				if (!(udiCarrierJSON.getJSONObject(i).isNull("carrierAIDC"))) {
					theUdiCarrier.setCarrierAIDC(Base64.getDecoder().decode(udiCarrierJSON.getJSONObject(i).getString("carrierAIDC")));
				}


				if (!(udiCarrierJSON.getJSONObject(i).isNull("entryType"))) {
					theUdiCarrier.setEntryType(
							Device.UDIEntryType.fromCode(udiCarrierJSON.getJSONObject(i).getString("entryType")));
				}
				udiCarrierList.add(theUdiCarrier);
			}
			device.setUdiCarrier(udiCarrierList);
		}

		// set manufacturer
		if (!(deviceJSONObj.isNull("manufacturer"))) {
			device.setManufacturer(deviceJSONObj.getString("manufacturer"));
		}

		// set model number
		if (!deviceJSONObj.isNull("modelNumber")) {
			device.setModelNumber(deviceJSONObj.getString("modelNumber"));
		}

		// set serial number
		if (!(deviceJSONObj.isNull("serialNumber"))) {
			device.setSerialNumber(deviceJSONObj.getString("serialNumber"));
		}

		// set lotNumber
		if (!deviceJSONObj.isNull("lotNumber")) {
			device.setLotNumber(deviceJSONObj.getString("lotNumber"));
		}

		// Set patient
		if (!(deviceJSONObj.isNull("patient"))) {
			Reference thePatient = new Reference();
			if (!(deviceJSONObj.getJSONObject("patient").isNull("reference"))) {
				thePatient.setReference(deviceJSONObj.getJSONObject("patient").getString("reference"));
			}
			if (!(deviceJSONObj.getJSONObject("patient").isNull("display"))) {
				thePatient.setDisplay(deviceJSONObj.getJSONObject("patient").getString("display"));
			}
			if (!(deviceJSONObj.getJSONObject("patient").isNull("type"))) {
				thePatient.setType(deviceJSONObj.getJSONObject("patient").getString("type"));
			}
			device.setPatient(thePatient);
		}
		
		// Set Parent
		if (!(deviceJSONObj.isNull("parent"))) {
			Reference theParent = new Reference();
			if (!(deviceJSONObj.getJSONObject("parent").isNull("reference"))) {
				theParent.setReference(deviceJSONObj.getJSONObject("parent").getString("reference"));
			}
			if (!(deviceJSONObj.getJSONObject("parent").isNull("display"))) {
				theParent.setDisplay(deviceJSONObj.getJSONObject("parent").getString("display"));
			}
			if (!(deviceJSONObj.getJSONObject("parent").isNull("type"))) {
				theParent.setType(deviceJSONObj.getJSONObject("parent").getString("type"));
			}
			device.setParent(theParent);
		}


		// Set owner
		if (!(deviceJSONObj.isNull("owner"))) {
			Reference theOwner = new Reference();
			if (!(deviceJSONObj.getJSONObject("owner").isNull("reference"))) {
				theOwner.setReference(deviceJSONObj.getJSONObject("owner").getString("reference"));
			}
			if (!(deviceJSONObj.getJSONObject("owner").isNull("display"))) {
				theOwner.setDisplay(deviceJSONObj.getJSONObject("owner").getString("display"));
			}
			if (!(deviceJSONObj.getJSONObject("owner").isNull("type"))) {
				theOwner.setType(deviceJSONObj.getJSONObject("owner").getString("type"));
			}
			device.setOwner(theOwner);
		}


		// set type
		if (!(deviceJSONObj.isNull("type"))) {
			JSONObject typeJSON = deviceJSONObj.getJSONObject("type");
			CodeableConcept theType = new CodeableConcept();
			if (!(typeJSON.isNull("coding"))) {
				JSONArray typeCodingJSON = typeJSON.getJSONArray("coding");
				int noOfCoding = typeCodingJSON.length();
				List<Coding> codingDtList = new ArrayList<>();
				for (int codingIndex = 0; codingIndex < noOfCoding; codingIndex++) {
					Coding theCoding = new Coding();
					if (!(typeCodingJSON.getJSONObject(codingIndex).isNull("system"))) {
						theCoding.setSystem(typeCodingJSON.getJSONObject(codingIndex).getString("system"));
					}
					if (!(typeCodingJSON.getJSONObject(codingIndex).isNull("code"))) {
						theCoding.setCode(typeCodingJSON.getJSONObject(codingIndex).getString("code"));
					}
					if (!(typeCodingJSON.getJSONObject(codingIndex).isNull("display"))) {
						theCoding.setDisplay(typeCodingJSON.getJSONObject(codingIndex).getString("display"));
					}
					codingDtList.add(theCoding);
				}
				theType.setCoding(codingDtList);
			}
			device.setType(theType);
		}

		return device;
	}
}