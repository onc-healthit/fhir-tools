package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.composite.TimingDt.Repeat;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.MedicationDispenseStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.UnitsOfTimeEnum;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedicationDispense;
import org.sitenv.spring.service.MedicationDispenseService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("request")
public class MedicationDispenseResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "MedicationDispense";
    public static final String VERSION_ID = "2.0";
    AbstractApplicationContext context;
    MedicationDispenseService service;


    public MedicationDispenseResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (MedicationDispenseService) context.getBean("medicationDispenseResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<MedicationDispense> getResourceType() {
        return MedicationDispense.class;
    }

    /**
     * This method returns all the available MedicationDispense records.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/MedicationDispense?_pretty=true&_format=json
     */
    @Search
    public List<MedicationDispense> getAllMedication(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationDispense> dafMedDispenseList = service.getAllMedicationDispense();

        List<MedicationDispense> medDispenseList = new ArrayList<MedicationDispense>();

        for (DafMedicationDispense dafMedDispense : dafMedDispenseList) {
            
            medDispenseList.add(createMedicationDispenseObject(dafMedDispense));
        }

        return medDispenseList;
    }

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
	 *Ex: http://<server name>/<context>/fhir/MedicationDispense/1?_format=json
     */
    @Read()
    public MedicationDispense getMedicationDispenseResourceById(@IdParam IdDt theId) {

        DafMedicationDispense dafMedDispense = service.getMedicationDispenseResourceById(theId.getIdPartAsLong().intValue());

        MedicationDispense medDispense = createMedicationDispenseObject(dafMedDispense);

        return medDispense;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationDispenses. This list may contain multiple matching resources, or it may also be empty.
     * 
     *  Ex: http://<server name>/<context>/fhir/MedicationDispense?patient=1&_format=json
     */
    @Search()
    public List<MedicationDispense> searchByPatient(@RequiredParam(name = MedicationDispense.SP_PATIENT) ReferenceParam thePatient,
                                                    @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        List<DafMedicationDispense> dafMedDispenseList = service.getMedicationDispenseByPatient(patientId);

        List<MedicationDispense> medDispenseList = new ArrayList<MedicationDispense>();

        for (DafMedicationDispense dafMedDispense : dafMedDispenseList) {
            medDispenseList.add(createMedicationDispenseObject(dafMedDispense));
        }

        if (theIncludes.size() > 0) {
            IdDt theId = medDispenseList.get(0).getPatient().getReference();
            PatientJsonResourceProvider patRes = new PatientJsonResourceProvider();
            Patient patient = patRes.readPatient(theId);
            medDispenseList.get(0).getPatient().setResource(patient);
        }
        return medDispenseList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param theCode
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationDispenses. This list may contain multiple matching resources, or it may also be empty.
     * 
     *  Ex: http://<server name>/<context>/fhir/MedicationDispense?code=2823-3&_format=json
     */
    @Search()
    public List<MedicationDispense> searchByCode(@RequiredParam(name = MedicationDispense.SP_CODE) ReferenceParam theCode,
                                                 @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String code = theCode.getIdPart();
        List<DafMedicationDispense> dafMedDispenseList = service.getMedicationDispenseByCode(code);

        List<MedicationDispense> medDispenseList = new ArrayList<MedicationDispense>();

        for (DafMedicationDispense dafMedDispense : dafMedDispenseList) {
            medDispenseList.add(createMedicationDispenseObject(dafMedDispense));
        }
        return medDispenseList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by Identifier Value
     *
     * @param theId This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                           the search criteria. The datatype here is String, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a list of MedicationDispenses. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<MedicationDispense> searchByIdentifier(@RequiredParam(name = MedicationDispense.SP_IDENTIFIER) TokenParam theId,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String identifierSystem = theId.getSystem();
        String identifierValue = theId.getValue();

        List<DafMedicationDispense> dafMedDispenseList = service.getMedicationDispenseByIdentifier(identifierSystem, identifierValue);

        List<MedicationDispense> medDispenseList = new ArrayList<MedicationDispense>();

        for (DafMedicationDispense dafMedDispense : dafMedDispenseList) {
            medDispenseList.add(createMedicationDispenseObject(dafMedDispense));
        }

        return medDispenseList;
    }

    /**
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available MedicationDispense records.
     * 
     * Ex: http://<server name>/<context>/fhir/MedicationDispense?medication=1&_pretty=true&_format=json
     */
    @Search()
    public List<MedicationDispense> searchByMedication(@RequiredParam(name = MedicationDispense.SP_MEDICATION) ReferenceParam theMedication,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String medicationId = theMedication.getIdPart();
        
        List<DafMedicationDispense> dafMedDispenseList = service.getMedicationDispenseByMedication(medicationId);

        List<MedicationDispense> medDispenseList = new ArrayList<MedicationDispense>();

        for (DafMedicationDispense dafMedDispense : dafMedDispenseList) {
            medDispenseList.add(createMedicationDispenseObject(dafMedDispense));
        }
        return medDispenseList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param status
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationDispenses. This list may contain multiple matching resources, or it may also be empty.
     * 
     *  Ex: http://<server name>/<context>/fhir/MedicationDispense?status=active&_format=json
     */
    @Search()
    public List<MedicationDispense> searchByStatus(@RequiredParam(name = MedicationDispense.SP_STATUS) String status,
                                                   @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationDispense> dafMedDispenseList = service.getMedicationDispenseByStatus(status);

        List<MedicationDispense> medDispenseList = new ArrayList<MedicationDispense>();

        for (DafMedicationDispense dafMedDispense : dafMedDispenseList) {
            medDispenseList.add(createMedicationDispenseObject(dafMedDispense));
        }

        return medDispenseList;
    }

    /**
     * This method converts DafMedicationDispense object to MedicationDispense object
     */
    private MedicationDispense createMedicationDispenseObject(DafMedicationDispense dafMedDispense) {

        MedicationDispense medDispense = new MedicationDispense();

        // Set Version
        medDispense.setId(new IdDt(RESOURCE_TYPE, dafMedDispense.getId() + "", VERSION_ID));

        //Set identifier
        IdentifierDt identifierDt = new IdentifierDt();
        identifierDt.setSystem(dafMedDispense.getIdentifier_system().trim());
        identifierDt.setValue(dafMedDispense.getIdentifier_value().trim());
        medDispense.setIdentifier(identifierDt);

        //set Status
        medDispense.setStatus(MedicationDispenseStatusEnum.valueOf(dafMedDispense.getStatus().trim()));

        //Set Medication Reference
        ResourceReferenceDt medicationResource = new ResourceReferenceDt();
        String theMedicationId = "Medication/" + Integer.toString(dafMedDispense.getMedicationreference().getId());
        medicationResource.setReference(theMedicationId);
        medDispense.setMedication(medicationResource);

        //set Patient Reference
        ResourceReferenceDt patientResource = new ResourceReferenceDt();
        String theId = "Patient/" + Integer.toString(dafMedDispense.getPatient().getId());
        patientResource.setReference(theId);
        medDispense.setPatient(patientResource);

        //Set Dispenser
        ResourceReferenceDt dispenseResource = new ResourceReferenceDt();
        String thePrescriberId = "Practitioner/" + Integer.toString(dafMedDispense.getDispenser().getId());
        dispenseResource.setReference(thePrescriberId);
        medDispense.setDispenser(dispenseResource);

        //Set Authorizing Prescription
        List<ResourceReferenceDt> pracRes = new ArrayList<ResourceReferenceDt>();
        ResourceReferenceDt prescriptionResource = new ResourceReferenceDt();
        String thePrescriptionId = "MedicationOrder/" + Integer.toString(dafMedDispense.getAuthorizingprescription().getId());
        prescriptionResource.setReference(thePrescriptionId);
        pracRes.add(prescriptionResource);
        medDispense.setAuthorizingPrescription(pracRes);

        //set Dosage Instruction
        Map<String, String> dosageInstructions = HapiUtils.convertToJsonMap(dafMedDispense.getDosageinstruction());
        List<ca.uhn.fhir.model.dstu2.resource.MedicationDispense.DosageInstruction> medDosage = new ArrayList<ca.uhn.fhir.model.dstu2.resource.MedicationDispense.DosageInstruction>();
        ca.uhn.fhir.model.dstu2.resource.MedicationDispense.DosageInstruction medicationDosage = new ca.uhn.fhir.model.dstu2.resource.MedicationDispense.DosageInstruction();
        //Set Dosage text
        medicationDosage.setText(dosageInstructions.get("text").trim());
        //Set Dosage Timing
        TimingDt dosageTiming = new TimingDt();
        Repeat timeRepeat = new Repeat();
        PeriodDt period = new PeriodDt();
        period.setStart(new DateTimeDt(dosageInstructions.get("timingstart")));
        period.setEnd(new DateTimeDt(dosageInstructions.get("timingend")));
        timeRepeat.setBounds(period);
        timeRepeat.setPeriod(Long.parseLong(dosageInstructions.get("timingperiod").trim()));
        timeRepeat.setFrequency(Integer.parseInt(dosageInstructions.get("timingfrequency")));
        timeRepeat.setPeriodUnits(UnitsOfTimeEnum.valueOf(dosageInstructions.get("timingperiodunits").trim()));
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
        medDosage.add(medicationDosage);
        medDispense.setDosageInstruction(medDosage);

        return medDispense;

    }
}
