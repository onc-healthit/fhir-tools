package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Immunization.ImmunizationPractitionerComponent;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.query.ImmunizationSearchCriteria;
import org.sitenv.spring.service.ImmunizationService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImmunizationResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Immunization";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    ImmunizationService service;

    public ImmunizationResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (ImmunizationService) context
                .getBean("immunizationResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must be
     * overridden to indicate what type of resource this provider supplies.
     */
    @Override
    public Class<Immunization> getResourceType() {
        return Immunization.class;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different method annotated with this
     * annotation, to support many different search criteria.
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available Immunization records.
     * <p>
     * Ex: http://<server
     * name>/<context>/fhir/Immunization?_pretty=true&_format=json
     */
    @Search
    public List<Immunization> getAllImmunization(
            @IncludeParam(allow = "*") Set<Include> theIncludes,
            @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafImmunization> dafImmunizations = service.getAllImmunization();

        List<Immunization> immunizations = new ArrayList<Immunization>();

        for (DafImmunization dafImmunization : dafImmunizations) {
            immunizations.add(createImmunizationObject(dafImmunization));
        }

        return immunizations;
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
     * Ex: http://<server
     * name>/<context>/fhir/Immunization/1?_format=json
     */
    @Read()
    public Immunization getImmunizationResourceById(@IdParam IdType theId) {

        DafImmunization dafImmunization = service.getImmunizationById(theId
                .getIdPartAsLong().intValue());

        Immunization immunization = createImmunizationObject(dafImmunization);

        return immunization;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search
     * operation. You may have many different method annotated with this
     * annotation, to support many different search criteria. This example
     * searches by patient
     *
     * @param thePatient
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of CarePlans. This list may contain
     * multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server
     * name>/<context>/fhir/Immunization?patient=1&_format=json
     */
    @Search()
    public List<Immunization> searchByPatient(
            @RequiredParam(name = Immunization.SP_PATIENT) ReferenceParam thePatient,
            @IncludeParam(allow = "*") Set<Include> theIncludes,
            @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        ImmunizationSearchCriteria immunizationSearchCriteria = new ImmunizationSearchCriteria();
        immunizationSearchCriteria.setPatient(Integer.parseInt(patientId));
        List<DafImmunization> dafImmunizationList = service
                .getImmunizationBySearchCriteria(immunizationSearchCriteria);

        List<Immunization> immunizationList = new ArrayList<Immunization>();

        for (DafImmunization dafImmunization : dafImmunizationList) {
            immunizationList.add(createImmunizationObject(dafImmunization));
        }
        return immunizationList;
    }

    /**
     * This method converts DafImmunization object to Immunization object
     */
    private Immunization createImmunizationObject(
            DafImmunization dafImmunization) {

        Immunization immunization = new Immunization();

        // Set Version
        immunization.setId(new IdType(RESOURCE_TYPE, dafImmunization.getId()
                + "", VERSION_ID));

        // Set Status
        immunization.setStatus(Immunization.ImmunizationStatus
                .valueOf(dafImmunization.getStatus()));

        // set date
        immunization.setDate(dafImmunization.getDate());

        // set vaccine
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setCode(dafImmunization.getVaccine_code());
        coding.setSystem(dafImmunization.getVaccine_system());
        coding.setDisplay(dafImmunization.getVaccine_display());
        codeableConcept.addCoding(coding);
        immunization.setVaccineCode(codeableConcept);

        // set patient
        Reference patientResource = new Reference();
        String theId = "Patient/"
                + Integer.toString(dafImmunization.getPatient().getId());
        patientResource.setReference(theId);
        immunization.setPatient(patientResource);

        // set was not taken
        immunization.setNotGiven(dafImmunization.isNotGiven());

        // set primary source
        immunization.setPrimarySource(dafImmunization.isPrimarySource());

        // set report origin
        Map<String, String> originSet = HapiUtils.convertToJsonMap(dafImmunization.getReportOrigin());
        CodeableConcept origin = new CodeableConcept();
        Coding originCoding = new Coding();
        originCoding.setSystem(originSet.get("system"));
        originCoding.setCode(originSet.get("code"));
        origin.addCoding(originCoding);
        origin.setText(originSet.get("text"));
        immunization.setReportOrigin(origin);

        // set practitioner
        Map<String, String> practitionerRole = HapiUtils.convertToJsonMap(dafImmunization.getPractitionerRole());
        List<ImmunizationPractitionerComponent> thePractitioners = new ArrayList<Immunization.ImmunizationPractitionerComponent>();
        ImmunizationPractitionerComponent practitioner = new ImmunizationPractitionerComponent();
        CodeableConcept role = new CodeableConcept();
        Coding roleCoding = new Coding();
        roleCoding.setSystem(practitionerRole.get("system"));
        roleCoding.setCode(practitionerRole.get("code"));
        role.addCoding(roleCoding);
        practitioner.setRole(role);

        Map<String, String> practitionerActor = HapiUtils.convertToJsonMap(dafImmunization.getPractitionerActor());
        Reference actor = new Reference();
        actor.setReference(practitionerActor.get("reference"));
        practitioner.setActor(actor);
        thePractitioners.add(practitioner);
        immunization.setPractitioner(thePractitioners);

        return immunization;
    }

}
