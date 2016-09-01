package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.composite.TimingDt.Repeat;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement.Dosage;
import ca.uhn.fhir.model.dstu2.valueset.MedicationStatementStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.UnitsOfTimeEnum;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

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
    public static final String VERSION_ID = "2.0";
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
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * This method returns all the available MedicationStatement records.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/MedicationStatement?_pretty=true&_format=json
     */
    @Search
    public List<MedicationStatement> getAllMedicationStatement(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationStatement> dafMedStatementList = service.getAllMedicationStatement();

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
	 *Ex: http://<server name>/<context>/fhir/MedicationStatement/1?_format=json
     */
    @Read()
    public MedicationStatement getMedicationStatementResourceById(@IdParam IdDt theId) {

        DafMedicationStatement dafMedStatement = service.getMedicationStatementResourceById(theId.getIdPartAsLong().intValue());

        MedicationStatement medStatement = createMedicationStatementObject(dafMedStatement);

        return medStatement;
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
     * 
     *  Ex: http://<server name>/<context>/fhir/MedicationStatement?code=2823-3&_format=json
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
     * 
     *  Ex: http://<server name>/<context>/fhir/MedicationStatement?patient=1&_format=json
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
     *
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by Identifier Value
     *
     * @param theId This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                           the search criteria. The datatype here is String, but there are other possible parameter types depending on the specific search criteria.
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
     * 
     *  Ex: http://<server name>/<context>/fhir/MedicationStatement?effectivedatetime=2012-12-01&_format=json
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
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param theMedication
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available MedicationStatement records.
     * 
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
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param status
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available MedicationStatement records.
     * 
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
        medStatement.setId(new IdDt(RESOURCE_TYPE, dafMedStatement.getId() + "", VERSION_ID));

        //Set identifier
        //Map<String, String> medIdentifier = HapiUtils.convertToJsonMap(dafMedStatement.getIdentifier());
        List<IdentifierDt> identifier = new ArrayList<IdentifierDt>();
        IdentifierDt identifierDt = new IdentifierDt();
        identifierDt.setSystem(dafMedStatement.getIdentifier_system().trim());
        identifierDt.setValue(dafMedStatement.getIdentifier_value().trim());
        identifier.add(identifierDt);
        medStatement.setIdentifier(identifier);

        //set Status
        medStatement.setStatus(MedicationStatementStatusEnum.valueOf(dafMedStatement.getStatus().trim()));

        //Set Wasnottaken
        medStatement.setWasNotTaken(dafMedStatement.isWasnottaken());

        //Set Medication
        CodeableConceptDt classCodeDt = new CodeableConceptDt();
        CodingDt classCodingDt = new CodingDt();
        classCodingDt.setSystem(dafMedStatement.getMedicationreference().getMed_system().trim());
        classCodingDt.setCode(dafMedStatement.getMedicationreference().getMed_code().trim());
        classCodingDt.setDisplay(dafMedStatement.getMedicationreference().getMed_display().trim());
        classCodeDt.addCoding(classCodingDt);
        medStatement.setMedication(classCodeDt);

        //Set ReasonNottaken
        /*List<CodeableConceptDt> reasonList =  new ArrayList<CodeableConceptDt>();
    	CodeableConceptDt reason = new CodeableConceptDt();
    	List<CodingDt> reasonCode = new ArrayList<CodingDt>();
    	CodingDt codeReason =  new CodingDt();
    	codeReason.setCode(dafMedStatement.getReasonnottaken());
    	reasonCode.add(codeReason);
    	reason.setCoding(reasonCode);
    	reasonList.add(reason);
    	medStatement.setReasonNotTaken(reasonList);*/

        //set Patient Reference
        ResourceReferenceDt patientResource = new ResourceReferenceDt();
        String theId = "Patient/" + Integer.toString(dafMedStatement.getPatient().getId());
        patientResource.setReference(theId);
        medStatement.setPatient(patientResource);

        //set Dosage
        Map<String, String> dosageStatement = HapiUtils.convertToJsonMap(dafMedStatement.getDosage());
        List<Dosage> medStateDosage = new ArrayList<Dosage>();
        Dosage dosage = new Dosage();
        TimingDt dosageTiming = new TimingDt();
        Repeat timeRepeat = new Repeat();
        timeRepeat.setPeriod(Long.parseLong(dosageStatement.get("timingperiod").trim()));
        timeRepeat.setFrequency(Integer.parseInt(dosageStatement.get("timingfrequency")));
        timeRepeat.setPeriodUnits(UnitsOfTimeEnum.valueOf(dosageStatement.get("timingperiodunits").trim()));
        dosageTiming.setRepeat(timeRepeat);
        dosage.setTiming(dosageTiming);
        //Set Route
        CodeableConceptDt routeCode = new CodeableConceptDt();
        List<CodingDt> codeList = new ArrayList<CodingDt>();
        CodingDt codeRoute = new CodingDt();
        CodeDt code = new CodeDt();
        code.setValue(dosageStatement.get("routecode"));
        codeRoute.setCode(code);
        codeRoute.setDisplay(dosageStatement.get("routedisplay"));
        codeList.add(codeRoute);
        routeCode.setCoding(codeList);
        dosage.setRoute(routeCode);
        medStateDosage.add(dosage);
        medStatement.setDosage(medStateDosage);

        PeriodDt dt = new PeriodDt();
        DateTimeDt dateTimeDt = new DateTimeDt();
        dateTimeDt.setValue(dafMedStatement.getEffectivePeriod());
        dt.setStart(dateTimeDt);
        medStatement.setEffective(dt);

        return medStatement;

    }
}
