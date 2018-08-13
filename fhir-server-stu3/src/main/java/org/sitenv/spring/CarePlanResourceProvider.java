package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.query.CarePlanSearchCriteria;
import org.sitenv.spring.service.CarePlanService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CarePlanResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "CarePlan";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    CarePlanService service;


    public CarePlanResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (CarePlanService) context.getBean("carePlanResourceService");
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

        List<DafCarePlan> dafCarePlansList = service.getAllCarePlan();

        List<CarePlan> carePlanList = new ArrayList<CarePlan>();

        for (DafCarePlan dafCarePlans : dafCarePlansList) {
            carePlanList.add(createCarePlanObject(dafCarePlans));
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
    public CarePlan getCarePlanResourceById(@IdParam IdType theId) {

        DafCarePlan dafCareTeam = service.getCarePlanById(theId.getIdPartAsLong().intValue());

        CarePlan carePlan = createCarePlanObject(dafCareTeam);

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
                                          @OptionalParam(name = CarePlan.SP_DATE) DateParam theDate,
                                          @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        CarePlanSearchCriteria carePlanSearchCriteria = new CarePlanSearchCriteria();
        carePlanSearchCriteria.setPatient(Integer.parseInt(patientId));
        if (theCategory != null) {
            carePlanSearchCriteria.setCat_code(theCategory);
        }
        if (theStatus != null) {
            carePlanSearchCriteria.setStatus(theStatus);
        }
        List<DafCarePlan> dafCarePlanList = service.getCarePlanBySearchCriteria(carePlanSearchCriteria);

        List<CarePlan> carePlanList = new ArrayList<CarePlan>();

        for (DafCarePlan dafCarePlan : dafCarePlanList) {
            carePlanList.add(createCarePlanObject(dafCarePlan));
        }
        return carePlanList;
    }

    /**
     * This method converts DafCareTeam object to CarePlan object
     */
    private CarePlan createCarePlanObject(DafCarePlan dafCarePlan) {

        CarePlan carePlan = new CarePlan();

        //Set Version
        carePlan.setId(new IdType(RESOURCE_TYPE, dafCarePlan.getId() + "", VERSION_ID));

        //Set Reference
        Reference patientResource = new Reference();
        String theId = "Patient/" + Integer.toString(dafCarePlan.getPatient().getId());
        patientResource.setReference(theId);
        carePlan.setSubject(patientResource);

        //Set Period - Revisit Prabhu.
        Period periodDt = new Period();
        periodDt.setStartElement(new DateTimeType("2005-12-22"));
        periodDt.setEndElement(new DateTimeType("2007-12-23"));
        carePlan.setPeriod(periodDt);

        //Set Status
        carePlan.setStatus(CarePlan.CarePlanStatus.valueOf(dafCarePlan.getStatus().trim()));
        
        //set intent
        carePlan.setIntent(CarePlanIntent.valueOf(dafCarePlan.getIntent()));

        //Set Category
        List<CodeableConcept> catenum = new ArrayList<CodeableConcept>();
        CodeableConcept catCodinglist = new CodeableConcept();
        Coding catCoding = new Coding();
        catCoding.setCode(dafCarePlan.getCat_code().trim());
        catCoding.setSystem(dafCarePlan.getCat_system().trim());
        catCodinglist.addCoding(catCoding);
        catenum.add(catCodinglist);
        carePlan.setCategory(catenum);


        return carePlan;
    }

	public List<CarePlan> getCarePlanForBulkData(List<Integer> patients, Date start) {
		List<DafCarePlan> dafCarePlanList = service.getCarePlanForBulkData(patients, start);

        List<CarePlan> careplanList = new ArrayList<CarePlan>();

        for (DafCarePlan dafCarePlan : dafCarePlanList) {
                careplanList.add(createCarePlanObject(dafCarePlan));
        }

        return careplanList;
	}

}
