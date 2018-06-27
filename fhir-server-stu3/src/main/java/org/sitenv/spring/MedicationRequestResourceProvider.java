package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.MedicationRequest.MedicationRequestIntent;
import org.hl7.fhir.dstu3.model.MedicationRequest.MedicationRequestRequesterComponent;
import org.hl7.fhir.dstu3.model.MedicationRequest.MedicationRequestStatus;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.query.MedicationRequestSearchCriteria;
import org.sitenv.spring.service.MedicationRequestService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MedicationRequestResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "MedicationRequest";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    MedicationRequestService service;

    public MedicationRequestResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (MedicationRequestService) context.getBean("medicationRequestResourceService");
    }

    @Override
    public Class<MedicationRequest> getResourceType() {
        return MedicationRequest.class;
    }

    @Search
    public List<MedicationRequest> getAllMedicationRequest(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationRequest> dafMedicationReqList = service.getAllMedicationRequest();

        List<MedicationRequest> medicationRequestList = new ArrayList<MedicationRequest>();

        for (DafMedicationRequest dafMedicationRequest : dafMedicationReqList) {
            if (dafMedicationRequest.getSubject() != null) {
                medicationRequestList.add(createMedicationRequestObject(dafMedicationRequest));
            }
        }

        return medicationRequestList;
    }

    @Read()
    public MedicationRequest getMedicationRequestResourceById(@IdParam IdType theId) {

        DafMedicationRequest dafMedicationRequest = service.getMedicationRequestResourceById(theId.getIdPartAsLong().intValue());

        MedicationRequest medRequest = createMedicationRequestObject(dafMedicationRequest);

        return medRequest;
    }

    @Search()
    public List<MedicationRequest> searchBySubject(@RequiredParam(name = MedicationRequest.SP_SUBJECT) ReferenceParam theSubject,
                                                   @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        Integer subjectId = theSubject.getIdPartAsLong().intValue();
        MedicationRequestSearchCriteria medicationRequestSearchCriteria = new MedicationRequestSearchCriteria();
        medicationRequestSearchCriteria.setSubject(subjectId);
        List<DafMedicationRequest> dafmedicationRequestList = service.getMedicationRequestBySearchCriteria(medicationRequestSearchCriteria);

        List<MedicationRequest> medicationRequestList = new ArrayList<MedicationRequest>();

        for (DafMedicationRequest dafmedicationRequest : dafmedicationRequestList) {
            medicationRequestList.add(createMedicationRequestObject(dafmedicationRequest));
        }
        return medicationRequestList;
    }

    private MedicationRequest createMedicationRequestObject(DafMedicationRequest dafMedicationRequest) {
        MedicationRequest medicationRequest = new MedicationRequest();

        // Set Version
        medicationRequest.setId(new IdType(RESOURCE_TYPE, dafMedicationRequest.getId() + "", VERSION_ID));

        //Set Identifier
        Map<String, String> identifier = HapiUtils.convertToJsonMap(dafMedicationRequest.getIdentifier());
        List<Identifier> identifierTypes = new ArrayList<Identifier>();
        Identifier identifierType = new Identifier();
        identifierType.setSystem(identifier.get("system"));
        identifierType.setValue(identifier.get("value"));
        identifierTypes.add(identifierType);
        medicationRequest.setIdentifier(identifierTypes);

        //Set Subject
        Reference subjectResource = new Reference();
        String theId = "Patient/" + Integer.toString(dafMedicationRequest.getSubject().getId());
        subjectResource.setReference(theId);
        medicationRequest.setSubject(subjectResource);

        //Set Status
        medicationRequest.setStatus(MedicationRequestStatus.valueOf(dafMedicationRequest.getStatus()));

        //Set Intent
        medicationRequest.setIntent(MedicationRequestIntent.valueOf(dafMedicationRequest.getIntent()));

        //Set Authored On
        medicationRequest.setAuthoredOn(dafMedicationRequest.getAuthoredon());

        //Set Requester
        Map<String, String> requester = HapiUtils.convertToJsonMap(dafMedicationRequest.getRequester());
        MedicationRequestRequesterComponent component = new MedicationRequestRequesterComponent();
        Reference agent = new Reference();
        agent.setReference(requester.get("reference"));
        agent.setDisplay(requester.get("display"));
        component.setAgent(agent);
        medicationRequest.setRequester(component);

        //Set ReasonCode
        Map<String, String> reasoncode = HapiUtils.convertToJsonMap(dafMedicationRequest.getReasoncode());
        List<CodeableConcept> codeableConcepts = new ArrayList<CodeableConcept>();
        CodeableConcept codeableConcept = new CodeableConcept();
        List<Coding> theCoding = new ArrayList<Coding>();
        Coding coding = new Coding();
        coding.setSystem(reasoncode.get("system"));
        coding.setCode(reasoncode.get("code"));
        coding.setDisplay(reasoncode.get("display"));
        theCoding.add(coding);
        codeableConcept.setCoding(theCoding);
        codeableConcepts.add(codeableConcept);
        medicationRequest.setReasonCode(codeableConcepts);
        
        //medication
        Map<String, String> medcode = HapiUtils.convertToJsonMap(dafMedicationRequest.getMedication());
        List<CodeableConcept> medcodeableConcepts = new ArrayList<CodeableConcept>();
        CodeableConcept medcodeableConcept = new CodeableConcept();
        List<Coding> theCoding1 = new ArrayList<Coding>();
        Coding coding1 = new Coding();
        coding1.setSystem(medcode.get("system"));
        coding1.setCode(medcode.get("code"));
        coding1.setDisplay(medcode.get("display"));
        theCoding1.add(coding1);
        medcodeableConcept .setCoding(theCoding1);
     //   medcodeableConcepts.add(medcodeableConcept );
        medicationRequest.setMedication(medcodeableConcept);
        

        //Set Dosage
        Map<String, String> dosage = HapiUtils.convertToJsonMap(dafMedicationRequest.getDosageinstruction());
        List<Dosage> dosages = new ArrayList<Dosage>();
        Dosage dosage2 = new Dosage();
        dosage2.setSequence(Integer.parseInt(dosage.get("sequence")));
        dosage2.setText(dosage.get("text"));
        dosages.add(dosage2);
        medicationRequest.setDosageInstruction(dosages);

        return medicationRequest;
    }

}
