package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.composite.TimingDt.Repeat;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder.DispenseRequest;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder.DosageInstruction;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.UnitsOfTimeEnum;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedicationOrder;
import org.sitenv.spring.service.MedicationOrderService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("request")
public class MedicationOrderResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "MedicationOrder";
    public static final String VERSION_ID = "2.0";
    AbstractApplicationContext context;
    MedicationOrderService service;


    public MedicationOrderResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (MedicationOrderService) context.getBean("medicationOrderResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<MedicationOrder> getResourceType() {
        return MedicationOrder.class;
    }


    /**
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return
     * This method returns all the available MedicationOrder records.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/MedicationOrder?_pretty=true&_format=json
     */

    @Search
    public List<MedicationOrder> getAllMedication(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationOrder> dafMedOrderList = service.getAllMedicationOrder();

        List<MedicationOrder> medOrderList = new ArrayList<MedicationOrder>();

        for (DafMedicationOrder dafMedOrder : dafMedOrderList) {
            
            medOrderList.add(createMedicationOrderObject(dafMedOrder));
        }

        return medOrderList;
    }
    
  public List<MedicationOrder> getMedicationOrderForBulkDataRequest(List<Integer> patients, Date start) {
    	
    	
		
  		List<DafMedicationOrder> dafMedicationOrderList = service.getMedicationOrderForBulkData(patients, start);

  		List<MedicationOrder> medOrderList = new ArrayList<MedicationOrder>();

  		for (DafMedicationOrder dafMedicationOrder : dafMedicationOrderList) {
  			medOrderList.add(createMedicationOrderObject(dafMedicationOrder));
  		}
  		
  		return medOrderList;
  	}

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
	 *Ex: http://<server name>/<context>/fhir/MedicationOrder/1?_format=json
     */
    @Read()
    public MedicationOrder getMedicationOrderResourceById(@IdParam IdDt theId) {

        DafMedicationOrder dafMedOrder = service.getMedicationOrderResourceById(theId.getIdPartAsLong().intValue());

        MedicationOrder medOrder = createMedicationOrderObject(dafMedOrder);

        return medOrder;
    }
    
    
    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by Resource id
     *
     * @param theId This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                           the search criteria. The data type here is String, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a MedicationStatement record. This list may contain record, or it may also be empty.
     */
    @Search
    public MedicationOrder searchMedicationOrderResourceById(@RequiredParam(name=MedicationOrder.SP_RES_ID) String theId) {
    	try{
            DafMedicationOrder dafMedOrder = service.getMedicationOrderResourceById(Integer.parseInt(theId));

            MedicationOrder medOrder = createMedicationOrderObject(dafMedOrder);

            return medOrder;
    	}catch(NumberFormatException e){
    		/*
    		 * If we can't parse the ID as a long, it's not valid so this is an unknown resource
    		 */
    		throw new ResourceNotFoundException(theId);
    	}
        }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param theCode
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationOrders. This list may contain multiple matching resources, or it may also be empty.
     * 
     *  Ex: http://<server name>/<context>/fhir/MedicationOrder?code=2823-3&_format=json
     */  
    @Search()
    public List<MedicationOrder> searchByCode(@RequiredParam(name = MedicationOrder.SP_CODE) TokenParam theCode,
                                              @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
       
        String codeValue = theCode.getValue();
        
        List<DafMedicationOrder> dafMedOrderList = service.getMedicationOrderByCode(codeValue);

        List<MedicationOrder> medOrderList = new ArrayList<MedicationOrder>();

        for (DafMedicationOrder dafMedOrder : dafMedOrderList) {
            medOrderList.add(createMedicationOrderObject(dafMedOrder));
        }
        return medOrderList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationOrders. This list may contain multiple matching resources, or it may also be empty.
     * 
     *  Ex: http://<server name>/<context>/fhir/MedicationOrder?patient=1&_format=json
     */
    @Search()
    public List<MedicationOrder> searchByPatient(@RequiredParam(name = MedicationOrder.SP_PATIENT) ReferenceParam thePatient,
                                                 @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        
        List<DafMedicationOrder> dafMedOrderList = service.getMedicationOrderByPatient(patientId);

        List<MedicationOrder> medOrderList = new ArrayList<MedicationOrder>();

        for (DafMedicationOrder dafMedOrder : dafMedOrderList) {
            medOrderList.add(createMedicationOrderObject(dafMedOrder));
        }
        return medOrderList;
    }

    /**
     *
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by Identifier Value
     *
     * @param theId This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                           the search criteria. The datatype here is String, but there are other possible parameter types depending on the specific search criteria.
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationOrders. This list may contain multiple matching resources, or it may also be empty.
     */

    @Search()
    public List<MedicationOrder> searchByIdentifier(@RequiredParam(name = MedicationOrder.SP_IDENTIFIER) TokenParam theId,
                                                    @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String identifierSystem = theId.getSystem();
        String identifierValue = theId.getValue();
        List<DafMedicationOrder> dafMedOrderList;
        if (identifierSystem != null) {
            dafMedOrderList = service.getMedicationOrderByIdentifier(identifierSystem, identifierValue);
        } else {
            dafMedOrderList = service.getMedicationOrderByIdentifierValue(identifierValue);
        }


        List<MedicationOrder> medOrderList = new ArrayList<MedicationOrder>();

        for (DafMedicationOrder dafMedOrder : dafMedOrderList) {
            medOrderList.add(createMedicationOrderObject(dafMedOrder));
        }

        return medOrderList;
    }

    /**
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param theMedication
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available MedicationOrder records.
     * 
     * Ex: http://<server name>/<context>/fhir/MedicationOrder?medication=1&_pretty=true&_format=json
     */
    @Search()
    public List<MedicationOrder> searchByMedication(@RequiredParam(name = MedicationOrder.SP_MEDICATION) ReferenceParam theMedication,
                                                    @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String medicationId = theMedication.getIdPart();
        
        List<DafMedicationOrder> dafMedOrderList = service.getMedicationOrderByMedication(medicationId);

        List<MedicationOrder> medOrderList = new ArrayList<MedicationOrder>();

        for (DafMedicationOrder dafMedOrder : dafMedOrderList) {
            medOrderList.add(createMedicationOrderObject(dafMedOrder));
        }
        return medOrderList;
    }
    
    /**
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param thePrescriber
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available MedicationOrder records.
     * 
     * Ex: http://<server name>/<context>/fhir/MedicationOrder?prescriber=1&_pretty=true&_format=json
     */
    @Search()
    public List<MedicationOrder> searchByPrescriber(@RequiredParam(name = MedicationOrder.SP_PRESCRIBER) ReferenceParam thePrescriber,
                                                    @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String prescriberId = thePrescriber.getIdPart();
        
        List<DafMedicationOrder> dafMedOrderList = service.getMedicationOrderByPrescriber(prescriberId);

        List<MedicationOrder> medOrderList = new ArrayList<MedicationOrder>();

        for (DafMedicationOrder dafMedOrder : dafMedOrderList) {
            medOrderList.add(createMedicationOrderObject(dafMedOrder));
        }
        return medOrderList;
    }

    /**
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param status
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available MedicationOrder records.
     * 
     * Ex: http://<server name>/<context>/fhir/MedicationOrder?status=active&_pretty=true&_format=json
     */
    @Search()
    public List<MedicationOrder> searchByStatus(@RequiredParam(name = MedicationOrder.SP_STATUS) String status,
                                                @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationOrder> dafMedOrderList = service.getMedicationOrderByStatus(status);

        List<MedicationOrder> medOrderList = new ArrayList<MedicationOrder>();

        for (DafMedicationOrder dafMedOrder : dafMedOrderList) {
            medOrderList.add(createMedicationOrderObject(dafMedOrder));
        }

        return medOrderList;
    }

    /**
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param theDate
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available MedicationOrder records.
     * 
     * Ex: http://<server name>/<context>/fhir/MedicationOrder?datewritten=2014-12-01&_pretty=true&_format=json
     */
    @Search()
    public List<MedicationOrder> searchByDateWritten(@RequiredParam(name = MedicationOrder.SP_DATEWRITTEN) DateRangeParam theDate,
                                                     @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String comparatorStr = "";
        ParamPrefixEnum paramPrefix = null;
        Date createdDate = null;
        if (theDate.getLowerBoundAsInstant() != null) {
            paramPrefix = theDate.getLowerBound().getPrefix();
            comparatorStr = paramPrefix.getValue();
            createdDate = theDate.getLowerBoundAsInstant(); // e.g. 2011-01-02
        }
        if (theDate.getUpperBoundAsInstant() != null) {
            paramPrefix = theDate.getUpperBound().getPrefix();
            comparatorStr = paramPrefix.getValue();
            createdDate = theDate.getUpperBoundAsInstant(); // e.g. 2011-01-02
        }
        List<DafMedicationOrder> dafMedOrderList = service.getMedicationOrderByDateWritten(comparatorStr, createdDate);

        List<MedicationOrder> medOrderList = new ArrayList<MedicationOrder>();

        for (DafMedicationOrder dafMedOrder : dafMedOrderList) {
            medOrderList.add(createMedicationOrderObject(dafMedOrder));
        }

        return medOrderList;
    }

    /**
     * This method converts DafMedicationOrder object to MedicationOrder object
     */
    private MedicationOrder createMedicationOrderObject(DafMedicationOrder dafMedOrder) {

        MedicationOrder medOrder = new MedicationOrder();

        // Set Version
        medOrder.setId(new IdDt(RESOURCE_TYPE, dafMedOrder.getId() + "", VERSION_ID));

        //Set identifier
        //Map<String, String> medIdentifier = HapiUtils.convertToJsonMap(dafMedOrder.getIdentifier());
        List<IdentifierDt> identifier = new ArrayList<IdentifierDt>();
        IdentifierDt identifierDt = new IdentifierDt();
        identifierDt.setSystem(dafMedOrder.getIdentifierSystem().trim());
        identifierDt.setValue(dafMedOrder.getIdentifierValue().trim());
        identifier.add(identifierDt);
        medOrder.setIdentifier(identifier);


        //set Status
        medOrder.setStatus(MedicationOrderStatusEnum.valueOf(dafMedOrder.getStatus().trim().toUpperCase()));

        //Set Medication
//        CodeableConceptDt classCodeDt = new CodeableConceptDt();
//        CodingDt classCodingDt = new CodingDt();
//        classCodingDt.setSystem(dafMedOrder.getMedicationreference().getMed_system().trim());
//        classCodingDt.setCode(dafMedOrder.getMedicationreference().getMed_code().trim());
//        classCodingDt.setDisplay(dafMedOrder.getMedicationreference().getMed_display().trim());
//        classCodeDt.addCoding(classCodingDt);
//        medOrder.setMedication(classCodeDt);
        
        Map<String, String> medCodeableConcept = HapiUtils.convertToJsonMap(dafMedOrder.getMedicationcodeableconcept());
        CodeableConceptDt classCodeDt = new CodeableConceptDt();
        CodingDt classCodingDt = new CodingDt();
        if(medCodeableConcept.get("system")!=null) {
        classCodingDt.setSystem(medCodeableConcept.get("system").trim());
        }
        if(medCodeableConcept.get("code")!=null) {
            classCodingDt.setCode(medCodeableConcept.get("code").trim());
          }
          if(medCodeableConcept.get("display")!=null) {
            classCodingDt.setDisplay(medCodeableConcept.get("display").trim());
          }
        classCodeDt.addCoding(classCodingDt);
        medOrder.setMedication(classCodeDt);

        //set Patient Reference
        ResourceReferenceDt patientResource = new ResourceReferenceDt();
        String theId = "Patient/" + Integer.toString(dafMedOrder.getPatient().getId());
        patientResource.setReference(theId);
        medOrder.setPatient(patientResource);

        //Set Prescriber
        ResourceReferenceDt prescriberResource = new ResourceReferenceDt();
        String thePrescriberId = "Practitioner/" + Integer.toString(dafMedOrder.getPrescriber().getId());
        prescriberResource.setReference(thePrescriberId);
        medOrder.setPrescriber(prescriberResource);

        //set Dosage Instruction
        Map<String, String> dosageInstructions = HapiUtils.convertToJsonMap(dafMedOrder.getDosageinstruction());
        List<DosageInstruction> medDosage = new ArrayList<DosageInstruction>();
        DosageInstruction medicationDosage = new DosageInstruction();
        //Set Dosage text
        if(dosageInstructions.get("text")!=null) {
        medicationDosage.setText(dosageInstructions.get("text").trim());
        }
        //Set Dosage Timing
        TimingDt dosageTiming = new TimingDt();
        Repeat timeRepeat = new Repeat();
        PeriodDt period = new PeriodDt();
        period.setStart(new DateTimeDt(dosageInstructions.get("timingstart")));
        period.setEnd(new DateTimeDt(dosageInstructions.get("timingend")));
        timeRepeat.setBounds(period);
        if(dosageInstructions.get("timingperiod")!=null) {
        timeRepeat.setPeriod(Long.parseLong(dosageInstructions.get("timingperiod").trim()));
        }
        timeRepeat.setFrequency(Integer.parseInt(dosageInstructions.get("timingfrequency").toUpperCase()));
        timeRepeat.setPeriodUnits(UnitsOfTimeEnum.valueOf(dosageInstructions.get("timingperiodunits").trim().toUpperCase()));
        dosageTiming.setRepeat(timeRepeat);
        medicationDosage.setTiming(dosageTiming);
        //Set Route
        CodeableConceptDt routeCode = new CodeableConceptDt();
        List<CodingDt> codeList = new ArrayList<CodingDt>();
        CodingDt codeRoute = new CodingDt();
        CodeDt code = new CodeDt();
        code.setValue(dosageInstructions.get("routecode"));
        codeRoute.setCode(code);
        codeRoute.setDisplay(dosageInstructions.get("routedisplay"));
        codeList.add(codeRoute);
        routeCode.setCoding(codeList);
        medicationDosage.setRoute(routeCode);
        //set dose quantity
        SimpleQuantityDt simpleQuantityDt = new SimpleQuantityDt();
        simpleQuantityDt.setCode(dosageInstructions.get("dosecode"));
        simpleQuantityDt.setValue(Long.parseLong(dosageInstructions.get("dosevalue")));
        simpleQuantityDt.setUnit(dosageInstructions.get("doseunit"));
        simpleQuantityDt.setSystem(dosageInstructions.get("dosesystem"));
        medicationDosage.setDose(simpleQuantityDt);
        medDosage.add(medicationDosage);
        medOrder.setDosageInstruction(medDosage);

        //Set Dispence Request
        Map<String, String> dispenceRequest = HapiUtils.convertToJsonMap(dafMedOrder.getDispencerequest());
        DispenseRequest dispenceReq = new DispenseRequest();
        dispenceReq.setNumberOfRepeatsAllowed(Integer.valueOf(dispenceRequest.get("repeat")));
        SimpleQuantityDt simpleQuantity = new SimpleQuantityDt();
        simpleQuantity.setCode(dispenceRequest.get("quantitycode"));
        simpleQuantity.setValue(Long.parseLong(dispenceRequest.get("quantityvalue")));
        simpleQuantity.setUnit(dispenceRequest.get("quantityunit"));
        simpleQuantity.setSystem(dispenceRequest.get("quantitysystem"));
        dispenceReq.setQuantity(simpleQuantity);
        medOrder.setDispenseRequest(dispenceReq);

        DateTimeDt date = new DateTimeDt();
        date.setValue(dafMedOrder.getDateWritten());
        medOrder.setDateWritten(date);

        return medOrder;

    }

}
