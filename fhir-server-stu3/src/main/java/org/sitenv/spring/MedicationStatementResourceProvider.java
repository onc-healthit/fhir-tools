package org.sitenv.spring;


import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.MedicationStatement.MedicationStatementTaken;
import org.hl7.fhir.dstu3.model.Timing.TimingRepeatComponent;
import org.hl7.fhir.dstu3.model.Timing.UnitsOfTime;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedicationStatement;
import org.sitenv.spring.service.MedicationStatementService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("request")
public class MedicationStatementResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "MedicationStatement";
    public static final String VERSION_ID = "3.0";
	
    AbstractApplicationContext context;
    MedicationStatementService service;

    public MedicationStatementResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (MedicationStatementService) context.getBean("medicationStatementResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<MedicationStatement> getResourceType() {
        return MedicationStatement.class;
    }

    /**
     * @param theIncludes
     * @param theSort
     * @param theCount    This method returns all the available MedicationStatement records.
     *                    <p/>
     *                    Ex: http://<server name>/<context>/fhir/MedicationStatement?_pretty=true&_format=json
     */
    @Search
    public List<MedicationStatement> getAllMedicationStatement(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationStatement> dafMedStatementList = service.getAllMedicationStatement(theCount);

        List<MedicationStatement> medStatementList = new ArrayList<MedicationStatement>();

        for (DafMedicationStatement dafMedStatement : dafMedStatementList) {

            medStatementList.add(createMedicationStatementObject(dafMedStatement));
        }

        return medStatementList;
    }

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     * Ex: http://<server name>/<context>/fhir/MedicationStatement/1?_format=json
     */
    @Read()
    public MedicationStatement getMedicationStatementResourceById(@IdParam IdType theId) {

        DafMedicationStatement dafMedStatement = service.getMedicationStatementResourceById(theId.getIdPartAsLong().intValue());

        MedicationStatement medStatement = createMedicationStatementObject(dafMedStatement);

        return medStatement;
    }


    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by Resource id
     *
     * @param theId This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *              the search criteria. The data type here is String, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a MedicationStatement record. This list may contain record, or it may also be empty.
     */
    @Search
    public MedicationStatement searchMedicationStatementResourceById(@RequiredParam(name = MedicationStatement.SP_RES_ID) String theId) {
        try {
            DafMedicationStatement dafMedStatement = service.getMedicationStatementResourceById(Integer.parseInt(theId));

            MedicationStatement medStatement = createMedicationStatementObject(dafMedStatement);

            return medStatement;
        } catch (NumberFormatException e) {
        /*
         * If we can't parse the ID as a long, it's not valid so this is an unknown resource
		 */
            throw new ResourceNotFoundException(theId);
        }
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by theCode
     *
     * @param theCode
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationStatements. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/MedicationStatement?code=2823-3&_format=json
     */
    @Search()
    public List<MedicationStatement> searchByCode(@RequiredParam(name = MedicationStatement.SP_CODE) TokenParam theCode,
                                                  @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String codeValue = theCode.getValue();
        List<DafMedicationStatement> dafMedStatementList = service.getMedicationStatementByCode(codeValue);

        List<MedicationStatement> medStatementList = new ArrayList<MedicationStatement>();

        for (DafMedicationStatement dafMedStatement : dafMedStatementList) {
            medStatementList.add(createMedicationStatementObject(dafMedStatement));
        }
        return medStatementList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationStatements. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/MedicationStatement?patient=1&_format=json
     */
    @Search()
    public List<MedicationStatement> searchByPatient(@RequiredParam(name = MedicationStatement.SP_PATIENT) ReferenceParam thePatient,
                                                     @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();

        List<DafMedicationStatement> dafMedStatementList = service.getMedicationStatementByPatient(patientId);

        List<MedicationStatement> medStatementList = new ArrayList<MedicationStatement>();

        for (DafMedicationStatement dafMedStatement : dafMedStatementList) {
            medStatementList.add(createMedicationStatementObject(dafMedStatement));
        }
        return medStatementList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by Identifier Value
     *
     * @param theId       This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                    the search criteria. The datatype here is String, but there are other possible parameter types depending on the specific search criteria.
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationStatements. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<MedicationStatement> searchByIdentifier(@RequiredParam(name = MedicationStatement.SP_IDENTIFIER) TokenParam theId,
                                                        @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String identifierSystem = theId.getSystem();
        String identifierValue = theId.getValue();
        List<DafMedicationStatement> dafMedStatementList;
        if (identifierSystem != null) {
            dafMedStatementList = service.getMedicationStatementByIdentifier(identifierSystem, identifierValue);
        } else {
            dafMedStatementList = service.getMedicationStatementByIdentifierValue(identifierValue);
        }

        List<MedicationStatement> medStatementList = new ArrayList<MedicationStatement>();

        for (DafMedicationStatement dafMedStatement : dafMedStatementList) {
            medStatementList.add(createMedicationStatementObject(dafMedStatement));
        }
        return medStatementList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param theDate
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationStatements. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/MedicationStatement?effectivedatetime=2012-12-01&_format=json
     */
    @Search()
    public List<MedicationStatement> searchByEffectiveDate(@RequiredParam(name = "effectivedatetime") DateRangeParam theDate,
                                                           @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String comparatorStr = "";
        ParamPrefixEnum paramPrefix = null;
        Date createdDate = null;
        if (theDate.getLowerBoundAsInstant() != null) {
            paramPrefix = theDate.getLowerBound().getPrefix();
            comparatorStr = paramPrefix.getValue();
            createdDate = theDate.getLowerBoundAsInstant(); // e.g. 2011-01-02
        }
        if (theDate.getUpperBoundAsInstant() != null) {
            paramPrefix = theDate.getUpperBound().getPrefix();
            comparatorStr = paramPrefix.getValue();
            createdDate = theDate.getUpperBoundAsInstant(); // e.g. 2011-01-02
        }
        List<DafMedicationStatement> dafMedStatementList = service.getMedicationStatementByEffectiveDate(comparatorStr, createdDate);

        List<MedicationStatement> medStatementList = new ArrayList<MedicationStatement>();

        for (DafMedicationStatement dafMedStatement : dafMedStatementList) {
            medStatementList.add(createMedicationStatementObject(dafMedStatement));
        }

        return medStatementList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param theMedication
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available MedicationStatement records.
     * <p>
     * Ex: http://<server name>/<context>/fhir/MedicationStatement?medication=1&_pretty=true&_format=json
     */
    @Search()
    public List<MedicationStatement> searchByMedication(@RequiredParam(name = MedicationStatement.SP_MEDICATION) ReferenceParam theMedication,
                                                        @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String medicationId = theMedication.getIdPart();

        List<DafMedicationStatement> dafMedStatementList = service.getMedicationStatementByMedication(medicationId);

        List<MedicationStatement> medStatementList = new ArrayList<MedicationStatement>();

        for (DafMedicationStatement dafMedStatement : dafMedStatementList) {
            medStatementList.add(createMedicationStatementObject(dafMedStatement));
        }

        return medStatementList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param status
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available MedicationStatement records.
     * <p>
     * Ex: http://<server name>/<context>/fhir/MedicationStatement?status=active&_pretty=true&_format=json
     */
    @Search()
    public List<MedicationStatement> searchByStatus(@RequiredParam(name = MedicationStatement.SP_STATUS) String status,
                                                    @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationStatement> dafMedStatementList = service.getMedicationStatementByStatus(status);

        List<MedicationStatement> medStatementList = new ArrayList<MedicationStatement>();

        for (DafMedicationStatement dafMedStatement : dafMedStatementList) {
            medStatementList.add(createMedicationStatementObject(dafMedStatement));
        }

        return medStatementList;
    }

    /**
     * This method converts DafMedicationStatement object to MedicationStatement object
     */
    private MedicationStatement createMedicationStatementObject(DafMedicationStatement dafMedStatement) {

        MedicationStatement medStatement = new MedicationStatement();

        // Set Version
        medStatement.setId(new IdType(RESOURCE_TYPE, dafMedStatement.getId() + "", VERSION_ID));

        //Set identifier
        //Map<String, String> medIdentifier = HapiUtils.convertToJsonMap(dafMedStatement.getIdentifier());
        List<Identifier> identifier = new ArrayList<Identifier>();
        Identifier identifierDt = new Identifier();
        identifierDt.setSystem(dafMedStatement.getIdentifier_system().trim());
        identifierDt.setValue(dafMedStatement.getIdentifier_value().trim());
        identifier.add(identifierDt);
        medStatement.setIdentifier(identifier);

        //set Status
        medStatement.setStatus(MedicationStatement.MedicationStatementStatus.valueOf(dafMedStatement.getStatus().trim()));


        //Set Medication
        CodeableConcept classCodeDt = new CodeableConcept();
        Coding classCoding = new Coding();
        classCoding.setSystem(dafMedStatement.getMedicationreference().getMed_system().trim());
        classCoding.setCode(dafMedStatement.getMedicationreference().getMed_code().trim());
        classCoding.setDisplay(dafMedStatement.getMedicationreference().getMed_display().trim());
        classCodeDt.addCoding(classCoding);
        medStatement.setMedication(classCodeDt);

        //set Patient Reference
        Reference patientResource = new Reference();
        String theId = "Patient/" + Integer.toString(dafMedStatement.getSubject().getId());
        patientResource.setReference(theId);
        medStatement.setSubject(patientResource);

        //set Dosage  - revsit Prabhu
        Map<String, String> dosageStatement = HapiUtils.convertToJsonMap(dafMedStatement.getDosage());
        List<Dosage> medStateDosage = new ArrayList<Dosage>();
        Dosage dosage = new Dosage();
        Timing dosageTiming = new Timing();
        TimingRepeatComponent timeRepeat = new TimingRepeatComponent();
        timeRepeat.setPeriod(Long.parseLong(dosageStatement.get("timingperiod").trim()));
        timeRepeat.setFrequency(Integer.parseInt(dosageStatement.get("timingfrequency")));
        timeRepeat.setPeriodUnit(UnitsOfTime.valueOf(dosageStatement.get("timingperiodunits").trim()));
        dosageTiming.setRepeat(timeRepeat);
        dosage.setTiming(dosageTiming);
        //Set Route
        CodeableConcept routeCode = new CodeableConcept();
        List<Coding> codeList = new ArrayList<Coding>();
        Coding codeRoute = new Coding();
        codeRoute.setCode(dosageStatement.get("routecode"));
        codeRoute.setDisplay(dosageStatement.get("routedisplay"));
        codeList.add(codeRoute);
        routeCode.setCoding(codeList);
        dosage.setRoute(routeCode);
        medStateDosage.add(dosage);
        medStatement.setDosage(medStateDosage);

        //set effective
        DateTimeType da = new DateTimeType();
        da.setValueAsString(dosageStatement.get("timingstart"));
        medStatement.setEffective(da);

        //set category
        Map<String, String> categorySet = HapiUtils.convertToJsonMap(dafMedStatement.getCategory());
        CodeableConcept category = new CodeableConcept();
        Coding categoryCoding = new Coding();
        categoryCoding.setSystem(categorySet.get("system"));
        categoryCoding.setCode(categorySet.get("code"));
        categoryCoding.setDisplay(categorySet.get("display"));
        category.addCoding(categoryCoding);
        medStatement.setCategory(category);


        //set taken
        medStatement.setTaken(MedicationStatementTaken.valueOf(dafMedStatement.getTaken().trim()));

        
        
        //set reasoncode
     
        Map<String, String> reasonCodeSet = HapiUtils.convertToJsonMap(dafMedStatement.getReasonCode());
        List<CodeableConcept> theReasonCode = new ArrayList<CodeableConcept>();
        CodeableConcept reasonCode = new CodeableConcept();
        Coding rcCoding = new Coding();
        rcCoding.setSystem(reasonCodeSet.get("system"));
        rcCoding.setCode(reasonCodeSet.get("code"));
        rcCoding.setDisplay(reasonCodeSet.get("display"));
        reasonCode.addCoding(rcCoding);
        theReasonCode.add(reasonCode);
        medStatement.setReasonCode(theReasonCode);
        
       

        //set reasonnottaken
      if(dafMedStatement.getTaken()== "N") {
        Map<String, String> rntSet = HapiUtils.convertToJsonMap(dafMedStatement.getReasonnottaken());
        List<CodeableConcept> theRNTCode = new ArrayList<CodeableConcept>();
        CodeableConcept rntCode = new CodeableConcept();
        Coding rntCoding = new Coding();
        rntCoding.setSystem(rntSet.get("system"));
        rntCoding.setCode(rntSet.get("code"));
        rntCoding.setDisplay(rntSet.get("display"));
        rntCode.addCoding(rntCoding);
        theRNTCode.add(rntCode);
        medStatement.setReasonNotTaken(theRNTCode);
    }
    
        //set effective date
        DateTimeType effective = new DateTimeType();
        effective.setValue(dafMedStatement.getEffectivePeriod());
        medStatement.setEffective(effective);

        return medStatement;

    }

	public List<MedicationStatement> getMedicationStatementForBulkDataRequest(List<Integer> patients, Date start) {
		List<DafMedicationStatement> dafMedicationStatementList = service.getMedicationStatementForBulkData(patients, start);

        List<MedicationStatement> medStatementList = new ArrayList<MedicationStatement>();

        for (DafMedicationStatement dafMedicationStatement : dafMedicationStatementList) {
                medStatementList.add(createMedicationStatementObject(dafMedicationStatement));
        }

        return medStatementList;
	}
}
