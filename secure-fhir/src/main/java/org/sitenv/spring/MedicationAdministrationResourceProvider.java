package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration.Dosage;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedicationAdministration;
import org.sitenv.spring.service.MedicationAdministrationService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("request")
public class MedicationAdministrationResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "MedicationAdministration";
    public static final String VERSION_ID = "2.0";
    AbstractApplicationContext context;
    MedicationAdministrationService service;

    public MedicationAdministrationResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (MedicationAdministrationService) context.getBean("medicationAdministrationResourceService");
    }

	 /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<MedicationAdministration> getResourceType() {
        return MedicationAdministration.class;
    }

	/**
     *The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return  Returns all the available MedicationAdministration records.
     * 
     * Ex: http://<server name>/<context>/fhir/MedicationAdministration?_pretty=true&_format=json
     */
    @Search
    public List<MedicationAdministration> getAllMedication(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationAdministration> dafMedAdministrationList = service.getAllMedicationAdministration();

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }

        return medAdministrationList;
    }
    
    public List<MedicationAdministration> getMedicationAdministrationForBulkDataRequest(List<Integer> patients, Date start) {
    	
    	
		
		List<DafMedicationAdministration> dafMedicationAdministrationList = service.getMedicationAdministrationForBulkData(patients, start);

		List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

		for (DafMedicationAdministration dafMedicationAdministration : dafMedicationAdministrationList) {
			medAdministrationList.add(createMedicationAdministrationObject(dafMedicationAdministration));
		}
		
		return medAdministrationList;
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
     *  Ex: http://<server name>/<context>/fhir/MedicationAdministration/1?_format=json
     */
    @Read()
    public MedicationAdministration getMedicationAdministrationResourceById(@IdParam IdDt theId) {

        DafMedicationAdministration dafMedAdministration = service.getMedicationAdministrationResourceById(theId.getIdPartAsLong().intValue());

        MedicationAdministration medAdministration = createMedicationAdministrationObject(dafMedAdministration);

        return medAdministration;
    }

	/**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationAdministrations. This list may contain multiple matching resources, or it may also be empty.
     * 
     *  Ex: http://<server name>/<context>/fhir/MedicationAdministration?patient=1&_format=json
     */
    @Search()
    public List<MedicationAdministration> searchByPatient(@RequiredParam(name = MedicationAdministration.SP_PATIENT) ReferenceParam thePatient,
                                                          @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        
        List<DafMedicationAdministration> dafMedAdministrationList = service.getMedicationAdministrationByPatient(patientId);

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }
        return medAdministrationList;
    }

	/**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param theCode
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationAdministrations. This list may contain multiple matching resources, or it may also be empty.
     * 
     *  Ex: http://<server name>/<context>/fhir/MedicationAdministration?code=2823-3&_format=json
     */
    @Search()
    public List<MedicationAdministration> searchByCode(@RequiredParam(name = MedicationAdministration.SP_CODE) ReferenceParam theCode,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String code = theCode.getIdPart();
        
        List<DafMedicationAdministration> dafMedAdministrationList = service.getMedicationAdministrationByCode(code);

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }
        return medAdministrationList;
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
     * @return This method returns a list of MedicationAdministrations. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<MedicationAdministration> searchByIdentifier(@RequiredParam(name = MedicationAdministration.SP_IDENTIFIER) TokenParam theId,
                                                             @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String identifierSystem = theId.getSystem();
        String identifierValue = theId.getValue();

        List<DafMedicationAdministration> dafMedAdministrationList = service.getMedicationAdministrationByIdentifier(identifierSystem, identifierValue);

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }

        return medAdministrationList;
    }

	/**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param theMedication
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationAdministrations. This list may contain multiple matching resources, or it may also be empty.
     * 
     */
    @Search()
    public List<MedicationAdministration> searchByMedication(@RequiredParam(name = MedicationAdministration.SP_MEDICATION) ReferenceParam theMedication,
                                                             @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String medicationId = theMedication.getIdPart();
        
        List<DafMedicationAdministration> dafMedAdministrationList = service.getMedicationAdministrationByMedication(medicationId);

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }
        return medAdministrationList;
    }

	/**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param status
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of MedicationAdministrations. This list may contain multiple matching resources, or it may also be empty.
     * 
     */
    @Search()
    public List<MedicationAdministration> searchByStatus(@RequiredParam(name = MedicationAdministration.SP_STATUS) String status,
                                                         @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedicationAdministration> dafMedAdministrationList = service.getMedicationAdministrationByStatus(status);

        List<MedicationAdministration> medAdministrationList = new ArrayList<MedicationAdministration>();

        for (DafMedicationAdministration dafMedAdministration : dafMedAdministrationList) {
            medAdministrationList.add(createMedicationAdministrationObject(dafMedAdministration));
        }

        return medAdministrationList;
    }

	/**
     * This method converts DafMedicationAdministration object to MedicationAdministration object
     */
    private MedicationAdministration createMedicationAdministrationObject(DafMedicationAdministration dafMedAdministration) {

        MedicationAdministration medAdministration = new MedicationAdministration();

        // Set Version
        medAdministration.setId(new IdDt(RESOURCE_TYPE, dafMedAdministration.getId() + "", VERSION_ID));

        //Set identifier
        List<IdentifierDt> identifier = new ArrayList<IdentifierDt>();
        IdentifierDt identifierDt = new IdentifierDt();
        identifierDt.setSystem(dafMedAdministration.getIdentifier_system().trim());
        identifierDt.setValue(dafMedAdministration.getIdentifier_value().trim());
        identifier.add(identifierDt);
        medAdministration.setIdentifier(identifier);

        //set Status
        medAdministration.setStatus(MedicationAdministrationStatusEnum.valueOf(dafMedAdministration.getStatus().trim().toUpperCase()));

    	/*//Set Medication Reference
    	ResourceReferenceDt medicationResource =new ResourceReferenceDt();
    	String theMedicationId = "Medication/"+Integer.toString(dafMedAdministration.getMedicationreference().getId());
    	medicationResource.setReference(theMedicationId);
    	medAdministration.setMedication(medicationResource);*/

        //Set Medication
        CodeableConceptDt classCodeDt = new CodeableConceptDt();
        CodingDt classCodingDt = new CodingDt();
        classCodingDt.setCode(dafMedAdministration.getMedicationreference().getMed_code().trim());
        classCodingDt.setDisplay(dafMedAdministration.getMedicationreference().getMed_display().trim());
        classCodeDt.addCoding(classCodingDt);
        medAdministration.setMedication(classCodeDt);

        //set Patient Reference
        ResourceReferenceDt patientResource = new ResourceReferenceDt();
        String theId = "Patient/" + Integer.toString(dafMedAdministration.getPatient().getId());
        patientResource.setReference(theId);
        medAdministration.setPatient(patientResource);

        //Set Dispenser
        ResourceReferenceDt pracResource = new ResourceReferenceDt();
        String thePrescriberId = "Practitioner/" + Integer.toString(dafMedAdministration.getPractitioner().getId());
        pracResource.setReference(thePrescriberId);
        medAdministration.setPractitioner(pracResource);

        //Set Prescription
        ResourceReferenceDt prescriptionResource = new ResourceReferenceDt();
        String thePrescriptionId = "MedicationOrder/" + Integer.toString(dafMedAdministration.getPrescription().getId());
        prescriptionResource.setReference(thePrescriptionId);
        medAdministration.setPrescription(prescriptionResource);
    	

        Map<String, String> medDosage = HapiUtils.convertToJsonMap(dafMedAdministration.getDosage());
        Dosage dosa = new Dosage();
        CodeableConceptDt siteCodeDt = new CodeableConceptDt();
        CodingDt siteCodingDt = new CodingDt();
        siteCodingDt.setCode(medDosage.get("sitecode"));
        siteCodingDt.setDisplay(medDosage.get("sitedisplay"));
        siteCodeDt.addCoding(siteCodingDt);
        dosa.setSite(siteCodeDt);
        medAdministration.setDosage(dosa);

        return medAdministration;

    }

}
