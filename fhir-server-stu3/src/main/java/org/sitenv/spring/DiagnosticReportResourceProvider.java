package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringOrListParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.DiagnosticReport.DiagnosticReportPerformerComponent;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafDiagnosticReport;
import org.sitenv.spring.query.DiagnosticReportSearchCriteria;
import org.sitenv.spring.service.DiagnosticReportService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Scope("request")
public class DiagnosticReportResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "DiagnosticReport";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    DiagnosticReportService service;

    public DiagnosticReportResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (DiagnosticReportService) context.getBean("diagnosticResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<DiagnosticReport> getResourceType() {
        return DiagnosticReport.class;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available DiagnosticReport records.
     * <p>
     * Ex: http://<server name>/<context>/fhir/DiagnosticReport?_pretty=true&_format=json
     */
    @Search
    public List<DiagnosticReport> getAllDiagnosticReports(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafDiagnosticReport> dafDiagnosticList = service.getAllDiagnosticReports();

        List<DiagnosticReport> diagnosticList = new ArrayList<DiagnosticReport>();

        for (DafDiagnosticReport dafDiagnostics : dafDiagnosticList) {

            diagnosticList.add(createDiagnosticReportObject(dafDiagnostics));
        }

        return diagnosticList;
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
     * Ex: http://<server name>/<context>/fhir/DiagnosticReport/1?_format=json
     */
    @Read()
    public DiagnosticReport getDiagnosticResourceById(@IdParam IdType theId) {

        DafDiagnosticReport dafDiagnostic = service.getDiagnosticResourceById(theId.getIdPartAsLong().intValue());

        DiagnosticReport diagnostic = createDiagnosticReportObject(dafDiagnostic);

        return diagnostic;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theCategory
     * @param theCode
     * @param thedate
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of DiagnosticReports. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/DiagnosticReport?patient=1&code=24323-8,58410-2,24356-8&_format=json
     */
    @Search()
    public List<DiagnosticReport> searchByPatient(@RequiredParam(name = DiagnosticReport.SP_PATIENT) ReferenceParam thePatient,
                                                  @OptionalParam(name = DiagnosticReport.SP_CATEGORY) StringType theCategory,
                                                  @OptionalParam(name = DiagnosticReport.SP_CODE) StringOrListParam theCode,
                                                  @OptionalParam(name = DiagnosticReport.SP_DATE) DateRangeParam thedate,
                                                  @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        DiagnosticReportSearchCriteria diagnosticReportSearchCriteria = new DiagnosticReportSearchCriteria();
        if (theCategory != null) {
            diagnosticReportSearchCriteria.setCategory(theCategory.getValue());
        }
        if (theCode != null) {
            List<StringParam> wantedCodings = theCode.getValuesAsQueryTokens();
            List<String> aList = new ArrayList<String>();
            for (int i = 0; i < wantedCodings.size(); i++) {
                aList.add(wantedCodings.get(i).getValue().toString());
            }
            diagnosticReportSearchCriteria.setCode(aList);
        }
        if (thedate != null) {
            diagnosticReportSearchCriteria.setDate(thedate);
        }

        List<DafDiagnosticReport> dafDiagnosticList = service.getDiagnosticReportBySearchCriteria(diagnosticReportSearchCriteria);

        List<DiagnosticReport> dignosticList = new ArrayList<DiagnosticReport>();

        for (DafDiagnosticReport dafDiagnostic : dafDiagnosticList) {
            dignosticList.add(createDiagnosticReportObject(dafDiagnostic));
        }
        return dignosticList;
    }

    /**
     * This method converts DafDiagnosticReport object to DiagnosticReport object
     */
    private DiagnosticReport createDiagnosticReportObject(DafDiagnosticReport dafDiagnostic) {
        DiagnosticReport diagnostic = new DiagnosticReport();

        // Set Version
        diagnostic.setId(new IdType(RESOURCE_TYPE, dafDiagnostic.getId() + "", VERSION_ID));

        //set patient
        Reference patientResource = new Reference();
        String theId = "Patient/" + Integer.toString(dafDiagnostic.getSubject().getId());
        patientResource.setReference(theId);
        diagnostic.setSubject(patientResource);

        //Set Code
        CodeableConcept classCodeDt = new CodeableConcept();
        Coding classCoding = new Coding();
        classCoding.setSystem(dafDiagnostic.getCode_system().trim());
        classCoding.setCode(dafDiagnostic.getCode().trim());
        classCoding.setDisplay(dafDiagnostic.getCode_display().trim());
        classCodeDt.addCoding(classCoding);
        classCodeDt.setText(dafDiagnostic.getCode_text().trim());
        diagnostic.setCode(classCodeDt);

        //set Category
        CodeableConcept classCatDt = new CodeableConcept();
        Coding classCategoryDt = new Coding();
        classCategoryDt.setSystem(dafDiagnostic.getCat_system().trim());
        classCategoryDt.setCode(dafDiagnostic.getCat_code().trim());
        classCategoryDt.setDisplay(dafDiagnostic.getCat_display().trim());
        classCatDt.addCoding(classCategoryDt);
        classCatDt.setText(dafDiagnostic.getCat_text().trim());
        diagnostic.setCategory(classCatDt);

        //Set Status
        diagnostic.setStatus(DiagnosticReport.DiagnosticReportStatus.valueOf(dafDiagnostic.getStatus().trim()));

        //Set Effective date
        DateTimeType date = new DateTimeType();
        date.setValue(dafDiagnostic.getEffectivedate());
        diagnostic.setEffective(date);

        //Set Issued
        diagnostic.setIssued(dafDiagnostic.getIssued());

        //set Performer
       /* List<DiagnosticReport.DiagnosticReportPerformerComponent> performerResource = new ArrayList<>();
        DiagnosticReport.DiagnosticReportPerformerComponent reference = new DiagnosticReport.DiagnosticReportPerformerComponent();
        String performerId = "Practitioner/" + Integer.toString(dafDiagnostic.getPerformer().getId());
        reference.setId(performerId);
        performerResource.add(reference);
        diagnostic.setPerformer(performerResource);*/

        //set Performer.Role
        Map<String, String> performerRole = HapiUtils.convertToJsonMap(dafDiagnostic.getPerformerRole());
        List<DiagnosticReportPerformerComponent> thePerformer = new ArrayList<DiagnosticReport.DiagnosticReportPerformerComponent>();
        DiagnosticReportPerformerComponent pr = new DiagnosticReportPerformerComponent();
        CodeableConcept role = new CodeableConcept();
        Coding roleCoding = new Coding();
        roleCoding.setSystem(performerRole.get("system"));
        roleCoding.setCode(performerRole.get("code"));
        roleCoding.setDisplay(performerRole.get("display"));
        role.addCoding(roleCoding);
        role.setText(performerRole.get("text"));
        pr.setRole(role);

        //set Performer.Actor
        Map<String, String> performerActor = HapiUtils.convertToJsonMap(dafDiagnostic.getPerformerActor());
        Reference actor = new Reference();
        actor.setReference(performerActor.get("reference"));
        actor.setDisplay(performerActor.get("display"));
        pr.setActor(actor);
        thePerformer.add(pr);
        diagnostic.setPerformer(thePerformer);


        //set Result
        List<Reference> resList = new ArrayList<Reference>();
        Reference resultResource = new Reference();
        String resultId = "Observation/" + Integer.toString(dafDiagnostic.getResult().getId());
        resultResource.setReference(resultId);
        resList.add(resultResource);
        diagnostic.setResult(resList);

        //Set Identifier
        List<Identifier> identifier = new ArrayList<Identifier>();
        Identifier identifierDt = new Identifier();
        identifierDt.setSystem(dafDiagnostic.getIdentifier_system().trim());
        identifierDt.setValue(dafDiagnostic.getIdentifier_value().trim());
        identifier.add(identifierDt);
        diagnostic.setIdentifier(identifier);

        //Set Request -- Revisit - Prabhu
        List<Reference> referenceDts = new ArrayList<Reference>();
        Reference referenceDt = new Reference();
        referenceDt.setReference("DiagnosticOrder/1");
        referenceDts.add(referenceDt);
        diagnostic.setBasedOn(referenceDts);

        return diagnostic;
    }

	public List<DiagnosticReport> getDiagnosticReportForBulkDataRequest(List<Integer> patients, Date start) {
		List<DafDiagnosticReport> dafDiagnosticReportList = service.getDiagnosticReportForBulkData(patients, start);

        List<DiagnosticReport> diagnosticList = new ArrayList<DiagnosticReport>();

        for (DafDiagnosticReport dafDiagnosticReport : dafDiagnosticReportList) {
                diagnosticList.add(createDiagnosticReportObject(dafDiagnosticReport));
        }

        return diagnosticList;
	}

}
