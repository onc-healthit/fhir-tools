package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.CarePlan;
import ca.uhn.fhir.model.dstu2.resource.CarePlan.Participant;
import ca.uhn.fhir.model.dstu2.valueset.CarePlanStatusEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.model.DafCareTeamParticipant;
import org.sitenv.spring.query.CareTeamSearchCriteria;
import org.sitenv.spring.service.CareTeamService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CareTeamResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "CarePlan";
    public static final String VERSION_ID = "2.0";
    AbstractApplicationContext context;
    CareTeamService service;


    public CareTeamResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (CareTeamService) context.getBean("careTeamResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<CarePlan> getResourceType() {
        return CarePlan.class;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available CarePlan records.
     * <p>
     * Ex: http://<server name>/<context>/fhir/CarePlan?_pretty=true&_format=json
     */
    @Search
    public List<CarePlan> getAllCarePlans(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafCareTeam> dafCarePlansList = service.getAllCareTeam();

        List<CarePlan> carePlanList = new ArrayList<CarePlan>();

        for (DafCareTeam dafCarePlans : dafCarePlansList) {
            carePlanList.add(createCareTeamObject(dafCarePlans));
        }

        return carePlanList;
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
     * Ex: http://<server name>/<context>/fhir/CarePlan/1?_format=json
     */
    @Read(version = true)
    public CarePlan getCarePlanResourceById(@IdParam IdDt theId) {

        DafCareTeam dafCareTeam = service.getCareTeamById(theId.getIdPartAsLong().intValue());

        CarePlan carePlan = createCareTeamObject(dafCareTeam);

        return carePlan;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theCategory
     * @param theStatus
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of CarePlans. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/CarePlan?patient=1&category=careteam&status=active&_format=json
     */
    @Search()
    public List<CarePlan> searchByPatient(@RequiredParam(name = CarePlan.SP_PATIENT) ReferenceParam thePatient,
                                          @OptionalParam(name = "category") String theCategory,
                                          @OptionalParam(name = "status") String theStatus,
                                          @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        CareTeamSearchCriteria careTeamSearchCriteria = new CareTeamSearchCriteria();
        careTeamSearchCriteria.setPatient(Integer.parseInt(patientId));
        if (theCategory != null) {
            careTeamSearchCriteria.setCat_code(theCategory);
        }
        if (theStatus != null) {
            careTeamSearchCriteria.setStatus(theStatus);
        }
        List<DafCareTeam> dafCareTeamList = service.getCareTeamBySearchCriteria(careTeamSearchCriteria);

        List<CarePlan> carePlanList = new ArrayList<CarePlan>();

        for (DafCareTeam dafCareTeam : dafCareTeamList) {
            carePlanList.add(createCareTeamObject(dafCareTeam));
        }
        return carePlanList;
    }

    /**
     * This method converts DafCareTeam object to CarePlan object
     */
    private CarePlan createCareTeamObject(DafCareTeam dafCareTeam) {

        CarePlan carePlan = new CarePlan();

        //Set Version
        carePlan.setId(new IdDt(RESOURCE_TYPE, dafCareTeam.getId() + "", VERSION_ID));

        //Set Reference
        ResourceReferenceDt patientResource = new ResourceReferenceDt();
        String theId = "Patient/" + Integer.toString(dafCareTeam.getPatient().getId());
        patientResource.setReference(theId);
        carePlan.setSubject(patientResource);

        //Set Status
        carePlan.setStatus(CarePlanStatusEnum.valueOf(dafCareTeam.getStatus().trim()));

        //Set Category
        List<CodeableConceptDt> catenum = new ArrayList<CodeableConceptDt>();
        CodeableConceptDt catCodinglist = new CodeableConceptDt();
        CodingDt catCoding = new CodingDt();
        catCoding.setCode(dafCareTeam.getCat_code().trim());
        catCoding.setSystem(dafCareTeam.getCat_system().trim());
        catCodinglist.addCoding(catCoding);
        catenum.add(catCodinglist);
        carePlan.setCategory(catenum);


        //set Participant
        List<DafCareTeamParticipant> dafCareTeamList = service.getCareteamparticipantByCareTeam(dafCareTeam.getId());
        List<Participant> participantlist = new ArrayList<CarePlan.Participant>();
        for (int i = 0; i < dafCareTeamList.size(); i++) {
            Participant participant = new Participant();
            //setRole
            CodeableConceptDt roldCode = new CodeableConceptDt();
            CodingDt roleCoding = new CodingDt();
            roleCoding.setCode(dafCareTeamList.get(i).getRole_code());
            roleCoding.setDisplay(dafCareTeamList.get(i).getRole_display());
            roleCoding.setSystem(dafCareTeamList.get(i).getRole_system());
            roldCode.addCoding(roleCoding);
            participant.setRole(roldCode);
            //Set Member
            ResourceReferenceDt memres = new ResourceReferenceDt();
            memres.setReference(dafCareTeamList.get(i).getMember_reference());
            memres.setDisplay(dafCareTeamList.get(i).getMember_display());
            participant.setMember(memres);
            participantlist.add(participant);
        }

        carePlan.setParticipant(participantlist);
        return carePlan;
    }

}
