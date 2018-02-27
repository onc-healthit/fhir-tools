package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Immunization;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.query.ImmunizationSearchCriteria;
import org.sitenv.spring.service.ImmunizationService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.*;

public class ImmunizationResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Immunization";
    public static final String VERSION_ID = "2.0";
    AbstractApplicationContext context;
    ImmunizationService service;

    public ImmunizationResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (ImmunizationService) context.getBean("immunizationResourceService");
    }

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<Immunization> getResourceType() {
        return Immunization.class;
    }

	/**
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available Immunization records.
     * 
     * Ex: http://<server name>/<context>/fhir/Immunization?_pretty=true&_format=json
     */
    @Search
    public List<Immunization> getAllImmunization(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafImmunization> dafImmunizations = service.getAllImmunization();

        List<Immunization> immunization= new ArrayList<Immunization>();

        for (DafImmunization dafImmunization : dafImmunizations) {
            immunization.add(createImmunizationObject(dafImmunization));
        }

        return immunization;
    }
	
    public List<Immunization> getImmunizationForBulkDataRequest(List<Integer> patients, Date start) {
    	
    	
		
		List<DafImmunization> dafImmunizationList = service.getImmunizationForBulkData(patients, start);

		List<Immunization> immunizationList = new ArrayList<Immunization>();

		for (DafImmunization dafImmunization : dafImmunizationList) {
			immunizationList.add(createImmunizationObject(dafImmunization));
		}
		
		return immunizationList;
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
     *  Ex: http://<server name>/<context>/fhir/Immunization/1?_format=json
     */
    @Read()
    public Immunization getImmunizationResourceById(@IdParam IdDt theId) {

        DafImmunization dafImmunization = service.getImmunizationById(theId.getIdPartAsLong().intValue());

        Immunization immunization = createImmunizationObject(dafImmunization);

        return immunization;
    }

	 /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of CarePlans. This list may contain multiple matching resources, or it may also be empty.
     * 
     *  Ex: http://<server name>/<context>/fhir/Immunization?patient=1&_format=json
     */
    @Search()
    public List<Immunization> searchByPatient(@RequiredParam(name = Immunization.SP_PATIENT) ReferenceParam thePatient,
                                              @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        ImmunizationSearchCriteria immunizationSearchCriteria = new ImmunizationSearchCriteria();
        immunizationSearchCriteria.setPatient(Integer.parseInt(patientId));
        List<DafImmunization> dafImmunizationList = service.getImmunizationBySearchCriteria(immunizationSearchCriteria);

        List<Immunization> immunizationList = new ArrayList<Immunization>();

        for (DafImmunization dafImmunization : dafImmunizationList) {
            immunizationList.add(createImmunizationObject(dafImmunization));
        }
        return immunizationList;
    }

	/**
     * This method converts DafImmunization object to Immunization object
     */
    private Immunization createImmunizationObject(DafImmunization dafImmunization) {

        Immunization immunization = new Immunization();

        //Set Version
        immunization.setId(new IdDt(RESOURCE_TYPE, dafImmunization.getId() + "", VERSION_ID));

        //Set Status
        immunization.setStatus(dafImmunization.getStatus());

        //set date
        DateTimeDt dateTimeDt = new DateTimeDt();
        dateTimeDt.setValue(dafImmunization.getDate());
        immunization.setDate(dateTimeDt);

        //set vaccine
        CodeableConceptDt codeableConceptDt = new CodeableConceptDt();
        CodingDt codingDt = new CodingDt();
        codingDt.setCode(dafImmunization.getVaccine_code());
        codingDt.setSystem(dafImmunization.getVaccine_system());
        codingDt.setDisplay(dafImmunization.getVaccine_display());
        codeableConceptDt.addCoding(codingDt);
        immunization.setVaccineCode(codeableConceptDt);

        //set patient
        ResourceReferenceDt patientResource = new ResourceReferenceDt();
        String theId = "Patient/" + Integer.toString(dafImmunization.getPatient().getId());
        patientResource.setReference(theId);
        immunization.setPatient(patientResource);

        //set was not taken
        immunization.setWasNotGiven(dafImmunization.isWasnotgiven());

        //set resported
        immunization.setReported(dafImmunization.isReported());

        return immunization;
    }

}
