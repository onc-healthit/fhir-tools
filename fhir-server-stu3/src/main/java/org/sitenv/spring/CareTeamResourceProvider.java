package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.CareTeam.CareTeamParticipantComponent;
import org.hl7.fhir.dstu3.model.CareTeam.CareTeamStatus;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.query.CareTeamSearchCriteria;
import org.sitenv.spring.service.CareTeamService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CareTeamResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "CareTeam";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    CareTeamService service;

    public CareTeamResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (CareTeamService) context.getBean("careTeamResourceService");
    }

    @Override
    public Class<CareTeam> getResourceType() {
        return CareTeam.class;
    }

    @Search
    public List<CareTeam> getAllCareTeams(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafCareTeam> dafCareTeamsList = service.getAllCareTeam();

        List<CareTeam> careTeamList = new ArrayList<CareTeam>();

        for (DafCareTeam dafCareTeams : dafCareTeamsList) {
            careTeamList.add(createCareTeamObject(dafCareTeams));
        }

        return careTeamList;
    }

    @Read(version = true)
    public CareTeam getCareTeamResourceById(@IdParam IdType theId) {

        DafCareTeam dafCareTeam = service.getCareTeamById(theId.getIdPartAsLong().intValue());

        CareTeam careTeam = createCareTeamObject(dafCareTeam);

        return careTeam;
    }

    @Search()
    public List<CareTeam> searchByPatient(@RequiredParam(name = CareTeam.SP_PATIENT) ReferenceParam thePatient,
                                          @OptionalParam(name = CareTeam.SP_STATUS) String theStatus,
                                          @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        CareTeamSearchCriteria careTeamSearchCriteria = new CareTeamSearchCriteria();
        careTeamSearchCriteria.setPatient(Integer.parseInt(patientId));
        if (theStatus != null) {
            careTeamSearchCriteria.setStatus(theStatus);
        }
        List<DafCareTeam> dafCareTeamList = service.getCareTeamBySearchCriteria(careTeamSearchCriteria);

        List<CareTeam> careTeamList = new ArrayList<CareTeam>();

        for (DafCareTeam dafCareTeam : dafCareTeamList) {
            careTeamList.add(createCareTeamObject(dafCareTeam));
        }
        return careTeamList;
    }

    private CareTeam createCareTeamObject(DafCareTeam dafCareTeam) {

        CareTeam careTeam = new CareTeam();

        //Set Version
        careTeam.setId(new IdType(RESOURCE_TYPE, dafCareTeam.getId() + "", VERSION_ID));

        //Set Identifier
        Map<String, String> identifier = HapiUtils.convertToJsonMap(dafCareTeam.getIdentifier());
        List<Identifier> identifiers = new ArrayList<Identifier>();
        Identifier theIdentifier = new Identifier();
        theIdentifier.setValue(identifier.get("value").trim());
        identifiers.add(theIdentifier);
        careTeam.setIdentifier(identifiers);

        //Set Status
        careTeam.setStatus(CareTeamStatus.valueOf(dafCareTeam.getStatus().trim()));

        //Set Category
        Map<String, String> category = HapiUtils.convertToJsonMap(dafCareTeam.getCategory());
        List<CodeableConcept> categoryList = new ArrayList<CodeableConcept>();
        CodeableConcept theCategory = new CodeableConcept();
        List<Coding> codings = new ArrayList<Coding>();
        Coding theCoding = new Coding();
        theCoding.setCode(category.get("code"));
        theCoding.setSystem(category.get("system"));
        codings.add(theCoding);
        theCategory.setCoding(codings);
        categoryList.add(theCategory);
        careTeam.setCategory(categoryList);

        //Set Name
        careTeam.setName(dafCareTeam.getName().trim());

        //Set Subject
        Reference reference = new Reference();
        String theId = "Patient/" + Integer.toString(dafCareTeam.getSubject().getId());
        reference.setReference(theId);
        careTeam.setSubject(reference);

        //Set Period
        org.hl7.fhir.dstu3.model.Period period = new org.hl7.fhir.dstu3.model.Period();
        period.setEnd(dafCareTeam.getPeriod());
        careTeam.setPeriod(period);

        //Set Participant

        List<CareTeamParticipantComponent> participantComponents = new ArrayList<CareTeamParticipantComponent>();
        CareTeamParticipantComponent theParticipant = new CareTeamParticipantComponent();

        //Set Participant Role
        Map<String, String> participant = HapiUtils.convertToJsonMap(dafCareTeam.getParticipant_role());
        CodeableConcept roleConcept = new CodeableConcept();
        roleConcept.setText(participant.get("text"));
        theParticipant.setRole(roleConcept);

        //Set Participant Member
        Reference memberReference = new Reference();
        memberReference.setId("Practitioner/" + dafCareTeam.getParticipant_member());
        theParticipant.setMember(memberReference);

        //Set Participant Period
        org.hl7.fhir.dstu3.model.Period participantperiod = new org.hl7.fhir.dstu3.model.Period();
        participantperiod.setEnd(dafCareTeam.getParticipant_period());
        theParticipant.setPeriod(participantperiod);

        participantComponents.add(theParticipant);
        careTeam.setParticipant(participantComponents);

        //Set Member Organization
        List<Reference> referencesList = new ArrayList<Reference>();
        Reference theManagingOrganization = new Reference();
        theManagingOrganization.setId("Organization/" + dafCareTeam.getManaging_organization());
        referencesList.add(theManagingOrganization);
        careTeam.setManagingOrganization(referencesList);

        return careTeam;

    }

}
