package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.valueset.ConditionCategoryCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.ConditionClinicalStatusCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.ConditionVerificationStatusEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringOrListParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.query.ConditionSearchCriteria;
import org.sitenv.spring.service.ConditionService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("request")
public class ConditionResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Condition";
    public static final String VERSION_ID = "2.0";
    AbstractApplicationContext context;
    ConditionService service;

    
    public ConditionResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (ConditionService) context.getBean("conditionResourceService");
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<Condition> getResourceType() {
        return Condition.class;
    }
    
    /**
     * This method returns all the available Conditions records.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/Condition?_pretty=true&_format=json
     */
    @Search
    public List<Condition> getAllConditions(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafCondition> dafConditionsList = service.getAllConditions();

        List<Condition> conditionList = new ArrayList<Condition>();

        for (DafCondition dafConditions : dafConditionsList) {
            
            conditionList.add(createConditionObject(dafConditions));
        }

        return conditionList;
    }
    
	public List<Condition> getConditionForBulkDataRequest(List<Integer> patients, Date start) {
    	
    	
		
		List<DafCondition> dafConditionList = service.getConditionForBulkData(patients, start);

		List<Condition> conditionList = new ArrayList<Condition>();

		for (DafCondition dafCondition : dafConditionList) {
			conditionList.add(createConditionObject(dafCondition));
		}
		
		return conditionList;
	}
    
	
	/**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
	 *Ex: http://<server name>/<context>/fhir/Condition/1?_format=json
     */
	@Read()
    public Condition getConditionResourceById(@IdParam IdDt theId) {

        DafCondition dafCondition = service.getConditionResourceById(theId.getIdPartAsLong().intValue());

        Condition condition = createConditionObject(dafCondition);

        return condition;
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
     * @return This method returns a list of Conditions. This list may contain multiple matching resources, or it may also be empty.
     * 
     *  Ex: http://<server name>/<context>/fhir/Condition?patient=1&clinicalstatus=active,relapse,remission&_format=json
     */
    @Search()
    public List<Condition> searchByPatient(@RequiredParam(name = Condition.SP_PATIENT) ReferenceParam thePatient,
                                           @OptionalParam(name = Condition.SP_CLINICALSTATUS) StringOrListParam theStatus,
                                           @OptionalParam(name = Condition.SP_CATEGORY) StringDt theCategory,
                                           @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        ConditionSearchCriteria searchOptions = new ConditionSearchCriteria();
        searchOptions.setPatientId(Integer.parseInt(patientId));
        if (theStatus != null) {
            List<StringParam> wantedCodings = theStatus.getValuesAsQueryTokens();
            List<String> aList = new ArrayList<String>();
            for (int i = 0; i < wantedCodings.size(); i++) {
                aList.add(wantedCodings.get(i).getValue().toString());
            }
            searchOptions.setStatus(aList);
        }
        if (theCategory != null) {
            String cat = theCategory.getValue();
            searchOptions.setCategory(cat);
        }
        
        List<DafCondition> dafConditionList = service.getConditionBySearchOptions(searchOptions);
        List<Condition> conditionList = new ArrayList<Condition>();

        for (DafCondition dafCondition : dafConditionList) {
            conditionList.add(createConditionObject(dafCondition));
        }
        return conditionList;
    }
	
	/**
     * This method converts DafCondition object to Condition object
     */
    private Condition createConditionObject(DafCondition dafCondition) {
        Condition condition = new Condition();

        // Set Version
        condition.setId(new IdDt(RESOURCE_TYPE, dafCondition.getId() + "", VERSION_ID));

        //Set identifier
        List<IdentifierDt> identifier = new ArrayList<IdentifierDt>();
        IdentifierDt identifierDt = new IdentifierDt();
        identifierDt.setSystem(dafCondition.getIdentifier_system().trim());
        identifierDt.setValue(dafCondition.getIdentifier_value().trim());
        identifier.add(identifierDt);
        condition.setIdentifier(identifier);

        //set patient
        ResourceReferenceDt patientResource = new ResourceReferenceDt();
        String theId = "Patient/" + Integer.toString(dafCondition.getPatient().getId());
        patientResource.setReference(theId);
        condition.setPatient(patientResource);

        //Set Asserter
        ResourceReferenceDt dispenseResource = new ResourceReferenceDt();
        String thePrescriberId = "Practitioner/" + Integer.toString(dafCondition.getAsserter().getId());
        dispenseResource.setReference(thePrescriberId);
        condition.setAsserter(dispenseResource);

        //Set Code
        CodeableConceptDt classCodeDt = new CodeableConceptDt();
        CodingDt classCodingDt = new CodingDt();
        classCodingDt.setSystem(dafCondition.getCode_system().trim());
        classCodingDt.setCode(dafCondition.getCode().trim());
        classCodingDt.setDisplay(dafCondition.getCode_display().trim());
        classCodeDt.addCoding(classCodingDt);
        condition.setCode(classCodeDt);

        //Set Category

        BoundCodeableConceptDt<ConditionCategoryCodesEnum> catenum = new BoundCodeableConceptDt<ConditionCategoryCodesEnum>();
        List<CodingDt> catCodinglist = new ArrayList<CodingDt>();
        CodingDt catCoding = new CodingDt();
        catCoding.setCode(dafCondition.getCategory_code().trim());
        catCoding.setDisplay(dafCondition.getCategory_display().trim());
        catCoding.setSystem(dafCondition.getCategory_system().trim());
        catCodinglist.add(catCoding);
        catenum.setCoding(catCodinglist);
        condition.setCategory(catenum);


        //Set Clinical Status
        condition.setClinicalStatus(ConditionClinicalStatusCodesEnum.valueOf(dafCondition.getClinical_status().trim().toUpperCase()));

        //set Verification Status
        condition.setVerificationStatus(ConditionVerificationStatusEnum.valueOf(dafCondition.getVerification_status().trim().toUpperCase()));

        //set Severity
        /*CodeableConceptDt severityCodeDt = new CodeableConceptDt();
        CodingDt severityCodingDt = new CodingDt();
        severityCodingDt.setCode(dafCondition.getSeverity_code().trim());
        severityCodingDt.setDisplay(dafCondition.getSeverity_display().trim());
        severityCodeDt.addCoding(severityCodingDt);
    	condition.setSeverity(severityCodeDt);*/

        //set onset
        Map<String, String> conditiononset = HapiUtils.convertToJsonMap(dafCondition.getOnset());
        DateTimeDt onsetdate = new DateTimeDt();
        onsetdate.setValue(java.sql.Date.valueOf(conditiononset.get("onsetDateTime")));
        condition.setOnset(onsetdate);

        //set Date Recorded
        condition.setDateRecorded(new DateDt(dafCondition.getDate_recorded()));

        return condition;
    }
}
