package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.NarrativeDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Goal;
import ca.uhn.fhir.model.dstu2.valueset.GoalStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.NarrativeStatusEnum;
import ca.uhn.fhir.model.primitive.BoundCodeDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.XhtmlDt;
import ca.uhn.fhir.model.valueset.BundleEntryTransactionMethodEnum;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafGoals;
import org.sitenv.spring.query.GoalsSearchCriteria;
import org.sitenv.spring.service.GoalsService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.*;

public class GoalsResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Goal";
    public static final String VERSION_ID = "2.0";
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
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available Goal records.
     * 
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
    
public List<Goal> getGoalsForBulkDataRequest(List<Integer> patients, Date start) {
    	
    	
		
		List<DafGoals> dafGoalsList = service.getGoalsForBulkData(patients, start);

		List<Goal> goalsList = new ArrayList<Goal>();

		for (DafGoals dafGoals: dafGoalsList) {
			goalsList.add(createGoalsObject(dafGoals));
		}
		
		return goalsList;
	}
    

	/**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     * 
     *  Ex: http://<server name>/<context>/fhir/Goal/1?_format=json
     */
    @Read()
    public Goal getGoalsResourceById(@IdParam IdDt theId) {

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
     * 
     *  Ex: http://<server name>/<context>/fhir/Goal?patient=1&_format=json&date=ge2015-01-14
     */
    @Search()
    public List<Goal> searchByPatient(@RequiredParam(name = Goal.SP_PATIENT) ReferenceParam thePatient,
                                      @OptionalParam(name = Goal.SP_TARGETDATE) DateRangeParam theRange,
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
     * 
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
        goal.setId(new IdDt(RESOURCE_TYPE, dafGoals.getId() + "", VERSION_ID));

        //Set Status
        BoundCodeDt<GoalStatusEnum> statenum = new BoundCodeDt(
                BundleEntryTransactionMethodEnum.VALUESET_BINDER,
                BundleEntryTransactionMethodEnum.POST);
        statenum.setValue(dafGoals.getStatus());
        goal.setStatus(statenum);

        //set description
        goal.setDescription(dafGoals.getDescription());

        //set patient
        ResourceReferenceDt patientResource = new ResourceReferenceDt();
        String theId = "Patient/" + Integer.toString(dafGoals.getPatient().getId());
        patientResource.setReference(theId);
        goal.setSubject(patientResource);
        
        //Set Date
        DateDt dateDt = new DateDt();
        dateDt.setValue(dafGoals.getDate());
        goal.setStart(dateDt);

        NarrativeDt narrativeDt = new NarrativeDt();
        BoundCodeDt<NarrativeStatusEnum> narenum = new BoundCodeDt(
                BundleEntryTransactionMethodEnum.VALUESET_BINDER);
        narenum.setValue(dafGoals.getTextstatus());
        narrativeDt.setStatus(narenum);
        XhtmlDt xhtmlDt = new XhtmlDt();
        xhtmlDt.setValueAsString(dafGoals.getDescription());
        narrativeDt.setDiv(xhtmlDt);
        goal.setText(narrativeDt);

        return goal;
    }

}
