package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.MedicationAdministration.MedicationAdministrationDosageComponent;
import org.hl7.fhir.dstu3.model.MedicationAdministration.MedicationAdministrationPerformerComponent;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedicationAdministration;
import org.sitenv.spring.service.MedicationAdministrationService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Scope("request")
public class MedicationAdministrationResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "MedicationAdministration";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    MedicationAdministrationService service;

    public MedicationAdministrationResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (MedicationAdministrationService) context.getBean("medicationAdministrationResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<MedicationAdministration> getResourceType() {
        return MedicationAdministration.class;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available MedicationAdministration records.
     * <p>
     * Ex: http://<server name>/<context>/fhir/MedicationAdministration?_pretty=true&_format=json
     */
    @Search
    public List<MedicationAdministration> getAllMedication(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationAdministration> dafMedAdministrationList = service.getAllMedicationAdministration();

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }

        return medAdministrationList;
    }

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     * <p>
     * Ex: http://<server name>/<context>/fhir/MedicationAdministration/1?_format=json
     */
    @Read()
    public MedicationAdministration getMedicationAdministrationResourceById(@IdParam IdType theId) {

        DafMedicationAdministration dafMedAdministration = service.getMedicationAdministrationResourceById(theId.getIdPartAsLong().intValue());

        MedicationAdministration medAdministration = createMedicationAdministrationObject(dafMedAdministration);

        return medAdministration;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationAdministrations. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/MedicationAdministration?patient=1&_format=json
     */
    @Search()
    public List<MedicationAdministration> searchByPatient(@RequiredParam(name = MedicationAdministration.SP_PATIENT) ReferenceParam thePatient,
                                                          @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();

        List<DafMedicationAdministration> dafMedAdministrationList = service.getMedicationAdministrationByPatient(patientId);

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }
        return medAdministrationList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param theCode
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationAdministrations. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/MedicationAdministration?code=2823-3&_format=json
     */
    @Search()
    public List<MedicationAdministration> searchByCode(@RequiredParam(name = MedicationAdministration.SP_CODE) ReferenceParam theCode,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String code = theCode.getIdPart();

        List<DafMedicationAdministration> dafMedAdministrationList = service.getMedicationAdministrationByCode(code);

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }
        return medAdministrationList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by Identifier Value
     *
     * @param theId       This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                    the search criteria. The datatype here is String, but there are other possible parameter types depending on the specific search criteria.
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationAdministrations. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<MedicationAdministration> searchByIdentifier(@RequiredParam(name = MedicationAdministration.SP_IDENTIFIER) TokenParam theId,
                                                             @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String identifierSystem = theId.getSystem();
        String identifierValue = theId.getValue();

        List<DafMedicationAdministration> dafMedAdministrationList = service.getMedicationAdministrationByIdentifier(identifierSystem, identifierValue);

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }

        return medAdministrationList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param theMedication
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationAdministrations. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<MedicationAdministration> searchByMedication(@RequiredParam(name = MedicationAdministration.SP_MEDICATION) ReferenceParam theMedication,
                                                             @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String medicationId = theMedication.getIdPart();

        List<DafMedicationAdministration> dafMedAdministrationList = service.getMedicationAdministrationByMedication(medicationId);

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }
        return medAdministrationList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param status
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationAdministrations. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<MedicationAdministration> searchByStatus(@RequiredParam(name = MedicationAdministration.SP_STATUS) String status,
                                                         @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationAdministration> dafMedAdministrationList = service.getMedicationAdministrationByStatus(status);

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }

        return medAdministrationList;
    }

    /**
     * This method converts DafMedicationAdministration object to MedicationAdministration object
     */
    private MedicationAdministration createMedicationAdministrationObject(DafMedicationAdministration dafMedAdministration) {

        MedicationAdministration medAdministration = new MedicationAdministration();

        // Set Version
        medAdministration.setId(new IdType(RESOURCE_TYPE, dafMedAdministration.getId() + "", VERSION_ID));

        //Set identifier
        List<Identifier> identifier = new ArrayList<Identifier>();
        Identifier identifierDt = new Identifier();
        identifierDt.setSystem(dafMedAdministration.getIdentifier_system().trim());
        identifierDt.setValue(dafMedAdministration.getIdentifier_value().trim());
        identifier.add(identifierDt);
        medAdministration.setIdentifier(identifier);

        //set Status
        medAdministration.setStatus(MedicationAdministration.MedicationAdministrationStatus.valueOf(dafMedAdministration.getStatus().trim()));

    	/*//Set Medication Reference
        Reference medicationResource =new Reference();
    	String theMedicationId = "Medication/"+Integer.toString(dafMedAdministration.getMedicationreference().getId());
    	medicationResource.setReference(theMedicationId);
    	medAdministration.setMedication(medicationResource);*/

        //Set Medication
        CodeableConcept classCodeDt = new CodeableConcept();
        Coding classCoding = new Coding();
        classCoding.setCode(dafMedAdministration.getMedicationreference().getMed_code().trim());
        classCoding.setDisplay(dafMedAdministration.getMedicationreference().getMed_display().trim());
        classCodeDt.addCoding(classCoding);
        medAdministration.setMedication(classCodeDt);

        //set Patient Reference
        Reference patientResource = new Reference();
        String theId = "Patient/" + Integer.toString(dafMedAdministration.getSubject().getId());
        patientResource.setReference(theId);
        medAdministration.setSubject(patientResource);

        //Set Performer
      /*  List<MedicationAdministrationPerformerComponent> thePerformer = new ArrayList<MedicationAdministration.MedicationAdministrationPerformerComponent>();
        MedicationAdministrationPerformerComponent pc = new MedicationAdministrationPerformerComponent();
        Reference pracResource = new Reference();
        String thePrescriberId = "Practitioner/" + Integer.toString(dafMedAdministration.getPractitioner().getId());
        pracResource.setReference(thePrescriberId);
		pc.setActor(pracResource);
		thePerformer.add(pc);
		medAdministration.setPerformer(thePerformer);*/

        //Set Prescription
        Reference prescriptionResource = new Reference();
        String thePrescriptionId = "MedicationOrder/" + Integer.toString(dafMedAdministration.getPrescription().getId());
        prescriptionResource.setReference(thePrescriptionId);
        medAdministration.setPrescription(prescriptionResource);


        Map<String, String> medDosage = HapiUtils.convertToJsonMap(dafMedAdministration.getDosage());
        MedicationAdministrationDosageComponent dosage = new MedicationAdministrationDosageComponent();
        CodeableConcept siteCodeDt = new CodeableConcept();
        Coding siteCoding = new Coding();
        siteCoding.setCode(medDosage.get("sitecode"));
        siteCoding.setDisplay(medDosage.get("sitedisplay"));
        siteCodeDt.addCoding(siteCoding);
        dosage.setSite(siteCodeDt);
        medAdministration.setDosage(dosage);
        
        //set dose
        SimpleQuantity simpleQuantityDt = new SimpleQuantity();
        simpleQuantityDt.setCode(medDosage.get("code"));
        simpleQuantityDt.setValue(Long.parseLong(medDosage.get("value")));
        simpleQuantityDt.setUnit(medDosage.get("unit"));
        simpleQuantityDt.setSystem(medDosage.get("system"));
        dosage.setDose(simpleQuantityDt);
     //   medDosage.add(medicationDosage);
     //   medOrder.setDosageInstruction(medDosage);
        medAdministration.setDosage(dosage);

        //set category
        Map<String, String> categorySet = HapiUtils.convertToJsonMap(dafMedAdministration.getCategory());
        CodeableConcept category = new CodeableConcept();
        Coding categoryCoding = new Coding();
        categoryCoding.setSystem(categorySet.get("system"));
        categoryCoding.setCode(categorySet.get("code"));
        categoryCoding.setDisplay(categorySet.get("display"));
        category.addCoding(categoryCoding);
        medAdministration.setCategory(category);

        //set effective
        Map<String, String> effectiveSet = HapiUtils.convertToJsonMap(dafMedAdministration.getEffective());
        Period effective = new Period();
        DateTimeType startDate = new DateTimeType();
        startDate.setValueAsString(effectiveSet.get("start"));
        effective.setStartElement(startDate);

        DateTimeType endDate = new DateTimeType();
        endDate.setValueAsString(effectiveSet.get("end"));
        effective.setEndElement(endDate);
        medAdministration.setEffective(effective);
        
        //notgiven
        medAdministration.setNotGiven(dafMedAdministration.isNotGiven());

        //set reasonnotgiven
   
        Map<String, String> rngSet = HapiUtils.convertToJsonMap(dafMedAdministration.getReasonnotgiven());
        List<CodeableConcept> theReasonNotGiven = new ArrayList<CodeableConcept>();
        CodeableConcept rngCC = new CodeableConcept();
        Coding rngCoding = new Coding();
        rngCoding.setSystem(rngSet.get("system"));
        rngCoding.setCode(rngSet.get("code"));
        rngCoding.setDisplay(rngSet.get("display"));
        rngCC.addCoding(rngCoding);
        theReasonNotGiven.add(rngCC);
        medAdministration.setReasonNotGiven(theReasonNotGiven);
       

        //set performer role
        Map<String, String> actorSet = HapiUtils.convertToJsonMap(dafMedAdministration.getPerformerActor());
        List<MedicationAdministrationPerformerComponent> thePerformer = new ArrayList<MedicationAdministration.MedicationAdministrationPerformerComponent>();
        MedicationAdministrationPerformerComponent performer = new MedicationAdministrationPerformerComponent();
        Reference actor = new Reference();
        actor.setReference(actorSet.get("reference"));
        actor.setDisplay(actorSet.get("display"));
        performer.setActor(actor);
        thePerformer.add(performer);
        medAdministration.setPerformer(thePerformer);

        //set reasoncode
        if(dafMedAdministration.isNotGiven()==false) {
        Map<String, String> reasonSet = HapiUtils.convertToJsonMap(dafMedAdministration.getReasonCode());
        List<CodeableConcept> theReasonCode = new ArrayList<CodeableConcept>();
        CodeableConcept reason = new CodeableConcept();
        Coding reasonCoding = new Coding();
        reasonCoding.setSystem(reasonSet.get("system"));
        reasonCoding.setCode(reasonSet.get("code"));
        reasonCoding.setDisplay(reasonSet.get("display"));
        reason.addCoding(reasonCoding);
        theReasonCode.add(reason);
        medAdministration.setReasonCode(theReasonCode);
        }

        return medAdministration;

    }

}
