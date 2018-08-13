package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Procedure.ProcedurePerformerComponent;
import org.hl7.fhir.dstu3.model.Procedure.ProcedureStatus;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.query.ProcedureSearchCriteria;
import org.sitenv.spring.service.ProcedureService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcedureResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Procedure";
    public static final String VERSION_ID = "2.0";
    AbstractApplicationContext context;
    ProcedureService service;

    public ProcedureResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (ProcedureService) context.getBean("procedureResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must be
     * overridden to indicate what type of resource this provider supplies.
     */
    @Override
    public Class<Procedure> getResourceType() {
        return Procedure.class;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different method annotated with this
     * annotation, to support many different search criteria.
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available Procedure records.
     * <p>
     * Ex: http://<server
     * name>/<context>/fhir/Procedure?_pretty=true&_format=json
     */
    @Search
    public List<Procedure> getAllProcedure(
            @IncludeParam(allow = "*") Set<Include> theIncludes,
            @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafProcedure> dafProcedures = service.getAllProcedures();

        List<Procedure> procedures = new ArrayList<Procedure>();

        for (DafProcedure dafProcedure : dafProcedures) {
            procedures.add(createProcedureObject(dafProcedure));
        }

        return procedures;
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
     * Ex: http://<server name>/<context>/fhir/Procedure/1?_format=json
     */
    @Read()
    public Procedure getProcedureResourceById(@IdParam IdType theId) {

        DafProcedure dafProcedure = service.getProcedureById(theId
                .getIdPartAsLong().intValue());

        Procedure procedure = createProcedureObject(dafProcedure);

        return procedure;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theRange
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of Procedures. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Procedure?patient=1&date=lt2015-01-01&_format=json
     */
    @Search()
    public List<Procedure> searchByOptions(@RequiredParam(name = Procedure.SP_PATIENT) ReferenceParam thePatient,
                                           @OptionalParam(name = Procedure.SP_DATE) DateRangeParam theRange,
                                           @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();

        ProcedureSearchCriteria procedureSearchCriteria = new ProcedureSearchCriteria();
        procedureSearchCriteria.setSubject(Integer.parseInt(patientId));
        if (theRange != null) {
            procedureSearchCriteria.setRangedates(theRange);
        }
        List<DafProcedure> dafProcedureList = service.getProcedureBySearchCriteria(procedureSearchCriteria);

        List<Procedure> procedureList = new ArrayList<Procedure>();

        for (DafProcedure dafProcedure : dafProcedureList) {
            if (dafProcedure.getSubject() != null) {
                procedureList.add(createProcedureObject(dafProcedure));
            }
        }
        return procedureList;
    }

    /**
     * This method converts DafProcedure object to Procedure object
     */
    private Procedure createProcedureObject(DafProcedure dafProcedure) {

        Procedure procedure = new Procedure();

        // Set Version
        procedure.setId(new IdDt(RESOURCE_TYPE,
                dafProcedure.getProcedureId() + "", VERSION_ID));

        //set subject
        Reference patientResource = new Reference();
        String theId = "Patient/" + Integer.toString(dafProcedure.getSubject().getId());
        patientResource.setReference(theId);
        procedure.setSubject(patientResource);

        //set status
        procedure.setStatus(ProcedureStatus.valueOf(dafProcedure.getStatus()));

        //set code
        Map<String, String> code = HapiUtils.convertToJsonMap(dafProcedure.getCode());
        CodeableConcept theCode = new CodeableConcept();
        Coding cc = new Coding();
        cc.setSystem(code.get("system"));
        cc.setCode(code.get("code"));
        cc.setDisplay(code.get("display"));
        theCode.addCoding(cc);
        theCode.setText(code.get("text"));
        procedure.setCode(theCode);

        //set bodysite
        Map<String, String> bodysite = HapiUtils.convertToJsonMap(dafProcedure.getBodysite());
        List<CodeableConcept> theBodysite = new ArrayList<CodeableConcept>();
        CodeableConcept bodysiteCC = new CodeableConcept();
        Coding bsCoding = new Coding();
        bsCoding.setSystem(bodysite.get("system"));
        bsCoding.setCode(bodysite.get("code"));
        bsCoding.setDisplay(bodysite.get("display"));
        bodysiteCC.addCoding(bsCoding);
        bodysiteCC.setText(bodysite.get("text"));
        theBodysite.add(bodysiteCC);
        procedure.setBodySite(theBodysite);

        //Set Reason Code
        Map<String, String> reasonNP = HapiUtils.convertToJsonMap(dafProcedure.getReason());
        List<CodeableConcept> codeableConcepts = new ArrayList<CodeableConcept>();
        CodeableConcept theReasonCode = new CodeableConcept();
        theReasonCode.setText(reasonNP.get("text"));
        codeableConcepts.add(theReasonCode);
        procedure.setReasonCode(codeableConcepts);

        //set performer
        Map<String, String> actor = HapiUtils.convertToJsonMap(dafProcedure.getPerformerActor());
        List<ProcedurePerformerComponent> performers = new ArrayList<ProcedurePerformerComponent>();
        ProcedurePerformerComponent performer = new ProcedurePerformerComponent();
        Reference theActor = new Reference();
        theActor.setReference(actor.get("reference"));
        theActor.setDisplay(actor.get("display"));
        performer.setActor(theActor);
        performers.add(performer);
        procedure.setPerformer(performers);

        //set performed date
        DateTimeType performed = new DateTimeType();
        performed.setValue(dafProcedure.getPerformed());
        procedure.setPerformed(performed);

        //set notes
        List<Annotation> theNotes = new ArrayList<Annotation>();
        Annotation note = new Annotation();
        note.setText(dafProcedure.getNotes());
        theNotes.add(note);
        procedure.setNote(theNotes);

        //Set Complication
        Map<String, String> complication = HapiUtils.convertToJsonMap(dafProcedure.getComplication());
        List<CodeableConcept> complicationCodeableConcept = new ArrayList<CodeableConcept>();
        CodeableConcept theComplication = new CodeableConcept();
        List<Coding> codings = new ArrayList<Coding>();
        Coding theCoding = new Coding();
        theCoding.setSystem(complication.get("system"));
        theCoding.setDisplay(complication.get("display"));
        theCoding.setCode(complication.get("code"));
        codings.add(theCoding);
        theComplication.setCoding(codings);
        theComplication.setText(complication.get("text"));
        complicationCodeableConcept.add(theComplication);
        procedure.setComplication(complicationCodeableConcept);

        //Set Used Code
        Map<String, String> usedCode = HapiUtils.convertToJsonMap(dafProcedure.getUsed_code());
        List<CodeableConcept> usedCodeCodeableConcept = new ArrayList<CodeableConcept>();
        CodeableConcept theUsedCode = new CodeableConcept();
        List<Coding> usedCodings = new ArrayList<Coding>();
        Coding theUsedCoding = new Coding();
        theUsedCoding.setSystem(usedCode.get("system"));
        theUsedCoding.setDisplay(usedCode.get("display"));
        theUsedCoding.setCode(usedCode.get("code"));
        usedCodings.add(theUsedCoding);
        theUsedCode.setCoding(usedCodings);
        theUsedCode.setText(usedCode.get("text"));
        usedCodeCodeableConcept.add(theUsedCode);
        procedure.setUsedCode(usedCodeCodeableConcept);

        return procedure;
    }

	public List<Procedure> getProcedureForBulkDataRequest(List<Integer> patients, Date start) {
		List<DafProcedure> dafProcedureList = service.getProcedureForBulkData(patients, start);

        List<Procedure> procedureList = new ArrayList<Procedure>();

        for (DafProcedure dafProcedure : dafProcedureList) {
                procedureList.add(createProcedureObject(dafProcedure));
        }

        return procedureList;
	}

}
