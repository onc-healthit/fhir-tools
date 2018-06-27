package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Goal.GoalTargetComponent;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafGoals;
import org.sitenv.spring.query.GoalsSearchCriteria;
import org.sitenv.spring.service.GoalsService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GoalsResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Goal";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    GoalsService service;

    public GoalsResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (GoalsService) context.getBean("goalsResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<Goal> getResourceType() {
        return Goal.class;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available Goal records.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Goal?_pretty=true&_format=json
     */
    @Search
    public List<Goal> getAllGoals(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafGoals> dafGoals = service.getAllGoals();

        List<Goal> goals = new ArrayList<Goal>();

        for (DafGoals dafGoals2 : dafGoals) {
            goals.add(createGoalsObject(dafGoals2));
        }

        return goals;
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
     * Ex: http://<server name>/<context>/fhir/Goal/1?_format=json
     */
    @Read()
    public Goal getGoalsResourceById(@IdParam IdType theId) {

        DafGoals dafGoals = service.getGoalsById(theId.getIdPartAsLong().intValue());

        Goal goal = createGoalsObject(dafGoals);

        return goal;
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
     * @return This method returns a list of Goals. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Goal?patient=1&_format=json&date=ge2015-01-14
     */
    @Search()
    public List<Goal> searchByPatient(@RequiredParam(name = Goal.SP_PATIENT) ReferenceParam thePatient,
                                      @OptionalParam(name = Goal.SP_TARGET_DATE) DateRangeParam theRange,
                                      @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        GoalsSearchCriteria goalsSearchCriteria = new GoalsSearchCriteria();
        goalsSearchCriteria.setPatient(Integer.parseInt(patientId));
        if (theRange != null) {
            goalsSearchCriteria.setDate(theRange);
        }
        List<DafGoals> dafGoalsList = service.getGoalsBySearchCriteria(goalsSearchCriteria);

        List<Goal> goalList = new ArrayList<Goal>();

        for (DafGoals dafGoals : dafGoalsList) {
            goalList.add(createGoalsObject(dafGoals));
        }
        return goalList;
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
     * @return This method returns a list of Goals. This list may contain multiple matching resources, or it may also be empty.
     */

    @Search()
    public List<Goal> searchByDate(@RequiredParam(name = "date") DateRangeParam theRange,
                                   @OptionalParam(name = Goal.SP_PATIENT) ReferenceParam thePatient,
                                   @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        GoalsSearchCriteria goalsSearchCriteria = new GoalsSearchCriteria();
        if (theRange != null) {
            goalsSearchCriteria.setDate(theRange);
        }
        if (thePatient != null) {
            String patientId = thePatient.getIdPart();
            goalsSearchCriteria.setPatient(Integer.parseInt(patientId));
        }
        List<DafGoals> dafGoalsList = service.getGoalsBySearchCriteria(goalsSearchCriteria);

        List<Goal> goalList = new ArrayList<Goal>();

        for (DafGoals dafGoals : dafGoalsList) {
            goalList.add(createGoalsObject(dafGoals));
        }
        return goalList;
    }

    /**
     * This method converts DafGoals object to Goal object
     */
    private Goal createGoalsObject(DafGoals dafGoals) {

        Goal goal = new Goal();

        //Set Version
        goal.setId(new IdType(RESOURCE_TYPE, dafGoals.getId() + "", VERSION_ID));

        //Set Status
        goal.setStatus(Goal.GoalStatus.valueOf(dafGoals.getStatus()));

       /* CodeableConcept goalDesc = new CodeableConcept();
        goalDesc.setText(dafGoals.getDescription());
		//set description
        goal.setDescription(goalDesc);//(dafGoals.getDescription());
*/
        //set patient
        Reference patientResource = new Reference();
        String theId = "Patient/" + Integer.toString(dafGoals.getSubject().getId());
        patientResource.setReference(theId);
        goal.setSubject(patientResource);

        //set description
        Map<String, String> descriptionSet = HapiUtils.convertToJsonMap(dafGoals.getDescription());
        CodeableConcept description = new CodeableConcept();
        description.setText(descriptionSet.get("text"));
        goal.setDescription(description);

        //set target
        GoalTargetComponent target = new GoalTargetComponent();
        Map<String, String> measureSet = HapiUtils.convertToJsonMap(dafGoals.getMeasure());
        CodeableConcept measure = new CodeableConcept();
        Coding measureCoding = new Coding();
        measureCoding.setSystem(measureSet.get("system"));
        measureCoding.setCode(measureSet.get("code"));
        measureCoding.setDisplay(measureSet.get("display"));
        measure.addCoding(measureCoding);
        target.setMeasure(measure);

        Range detailRange = new Range();
        Map<String, String> lowRange = HapiUtils.convertToJsonMap(dafGoals.getRangeLow());
        SimpleQuantity low = new SimpleQuantity();
        low.setValue(new BigDecimal(lowRange.get("value")));
        low.setUnit(lowRange.get("unit"));
        low.setSystem(lowRange.get("system"));
        low.setCode(lowRange.get("code"));
        detailRange.setLow(low);

        Map<String, String> highRange = HapiUtils.convertToJsonMap(dafGoals.getRangeHigh());
        SimpleQuantity high = new SimpleQuantity();
        high.setValue(new BigDecimal(highRange.get("value")));
        high.setUnit(highRange.get("unit"));
        high.setSystem(highRange.get("system"));
        high.setCode(highRange.get("code"));
        detailRange.setHigh(high);

        target.setDetail(detailRange);
        goal.setTarget(target);

        goal.setStatusDate(dafGoals.getDate());

        DateType startDate = new DateType();
        startDate.setValue(dafGoals.getDate());
        goal.setStart(startDate);

        //set Expressedby
        Reference expressedBy = new Reference();
        String expId = "Patient/" + dafGoals.getExpressedBy();
        expressedBy.setReference(expId);
        goal.setExpressedBy(expressedBy);
        return goal;
    }

}
