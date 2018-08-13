package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.AllergyIntolerance.AllergyIntoleranceCategory;
import org.hl7.fhir.dstu3.model.AllergyIntolerance.AllergyIntoleranceClinicalStatus;
import org.hl7.fhir.dstu3.model.AllergyIntolerance.AllergyIntoleranceVerificationStatus;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.service.AllergyIntoleranceService;
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
public class AllergyIntoleranceResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "AllergyIntolerance";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    AllergyIntoleranceService service;


    public AllergyIntoleranceResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (AllergyIntoleranceService) context.getBean("allergyIntoleranceResourceService");
    }


    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<AllergyIntolerance> getResourceType() {
        return AllergyIntolerance.class;
    }


    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns list of resource matching this identifier, or null if none exists.
     * <p>
     * Ex: http://<server name>/<context>/fhir/AllergyIntolerance?_pretty=true&_format=json
     */
    @Search
    public List<AllergyIntolerance> getAllAllergyIntolerance(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafAllergyIntolerance> dafAllergyIntoleranceList = service.getAllAllergyIntolerance();

        List<AllergyIntolerance> allergyIntoleranceList = new ArrayList<AllergyIntolerance>();

        for (DafAllergyIntolerance dafAllergyIntolerance : dafAllergyIntoleranceList) {
            allergyIntoleranceList.add(createAllergyIntoleranceObject(dafAllergyIntolerance));
        }

        return allergyIntoleranceList;
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
     * Ex: http://<server name>/<context>/fhir/AllergyIntolerance/1?_pretty=true&_format=json
     */
    @Read()
    public AllergyIntolerance getAllergyIntoleranceResourceById(@IdParam IdType theId) {

        DafAllergyIntolerance dafAllergyIntolerance = service.getAllergyIntoleranceResourceById(theId.getIdPartAsLong().intValue());

        AllergyIntolerance allergyIntolerance = createAllergyIntoleranceObject(dafAllergyIntolerance);

        return allergyIntolerance;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param thePatient
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of AllergyIntolerance. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/AllergyIntolerance?patient=1&_format=json
     */
    @Search()
    public List<AllergyIntolerance> searchByPatient(@RequiredParam(name = AllergyIntolerance.SP_PATIENT) ReferenceParam thePatient,
                                                    @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        List<DafAllergyIntolerance> dafAllergyIntoleranceList = service.getAllergyIntoleranceByPatient(patientId);

        List<AllergyIntolerance> allergyIntoleranceList = new ArrayList<AllergyIntolerance>();

        for (DafAllergyIntolerance dafAllergyIntolerance : dafAllergyIntoleranceList) {
            allergyIntoleranceList.add(createAllergyIntoleranceObject(dafAllergyIntolerance));
        }
        return allergyIntoleranceList;
    }

    /**
     * @param dafAllergyIntolerance
     * @return AllergyIntolerance object
     * <p>
     * This method converts DafAllergyIntolerance object to AllergyIntolerance object
     */
    private AllergyIntolerance createAllergyIntoleranceObject(DafAllergyIntolerance dafAllergyIntolerance) {

        AllergyIntolerance allergyIntolerance = new AllergyIntolerance();
        // Set Version
        allergyIntolerance.setId(new IdType(RESOURCE_TYPE, dafAllergyIntolerance.getId() + "", VERSION_ID));

        //Set identifier
        List<Identifier> identifier = new ArrayList<Identifier>();
        Identifier identifierDt = new Identifier();
        identifierDt.setSystem(dafAllergyIntolerance.getIdentifier_system().trim());
        identifierDt.setValue(dafAllergyIntolerance.getIdentifier_value().trim());
        identifier.add(identifierDt);
        allergyIntolerance.setIdentifier(identifier);
        //Set clinical status
        allergyIntolerance.setClinicalStatus(AllergyIntoleranceClinicalStatus.valueOf(dafAllergyIntolerance.getClinicalStatus()));

        //set verification status
        allergyIntolerance.setVerificationStatus(AllergyIntoleranceVerificationStatus.valueOf(dafAllergyIntolerance.getVerificationStatus().trim()));
        //set patient
        Reference patientResource = new Reference();
        String theId = "Patient/" + Integer.toString(dafAllergyIntolerance.getPatient().getId());
        patientResource.setReference(theId);
        allergyIntolerance.setPatient(patientResource);

        //Set Asserter
        Reference dispenseResource = new Reference();
        String thePrescriberId = "Practitioner/" + Integer.toString(dafAllergyIntolerance.getRecorder().getId());
        dispenseResource.setReference(thePrescriberId);
        allergyIntolerance.setRecorder(dispenseResource);

        //set code
        Map<String, String> code = HapiUtils.convertToJsonMap(dafAllergyIntolerance.getCode());
        CodeableConcept codeCC = new CodeableConcept();
        Coding codeCoding = new Coding();
        codeCoding.setSystem(code.get("system"));
        codeCoding.setCode(code.get("code"));
        codeCoding.setDisplay(code.get("display"));
        codeCC.addCoding(codeCoding);
        allergyIntolerance.setCode(codeCC);

        //set Criticality
        allergyIntolerance.setCriticality(AllergyIntolerance.AllergyIntoleranceCriticality.valueOf(dafAllergyIntolerance.getCriticality().trim()));

        //set Category  -- Changed in DSTU3 change it to CodeableConcept List - Prabhu
        //allergyIntolerance.setCategory(AllergyIntoleranceCategory.valueOf(dafAllergyIntolerance.getCategory().trim()));

        // revisit  - Devil
/*		CodeableConcept t = new CodeableConcept();
        Coding coding = new Coding();
		coding.setDisplay(dafAllergyIntolerance.getCategory());
		t.addCoding(coding);
		allergyIntolerance.addCategory(t );*/
        allergyIntolerance.addCategory(AllergyIntoleranceCategory.valueOf(dafAllergyIntolerance.getCategory().trim()));

        //set reaction
        Map<String, String> reaction = HapiUtils.convertToJsonMap(dafAllergyIntolerance.getReaction());
        List<AllergyIntolerance.AllergyIntoleranceReactionComponent> allergyReaactionList = new ArrayList<AllergyIntolerance.AllergyIntoleranceReactionComponent>();
        AllergyIntolerance.AllergyIntoleranceReactionComponent allergyReaction = new AllergyIntolerance.AllergyIntoleranceReactionComponent();
        //set Reaction Substance
	    	/*CodeableConcept substanceCodeDt = new CodeableConcept();
	        Coding substanceCoding = new Coding();
	        substanceCoding.setCode(reaction.get("substance_code"));
	        substanceCoding.setDisplay(reaction.get("substance_display"));
	        substanceCodeDt.addCoding(substanceCoding);
	        allergyReaction.setSubstance(substanceCodeDt);*/
        //set manifestation
        List<CodeableConcept> manifestList = new ArrayList<CodeableConcept>();
        CodeableConcept manifestcode = new CodeableConcept();
        Coding manifestcoding = new Coding();
        manifestcoding.setCode(reaction.get("mainfest_code"));
        manifestcoding.setDisplay(reaction.get("manifest_display"));
        manifestcoding.setSystem(reaction.get("manifest_system"));
        manifestcode.addCoding(manifestcoding);
        manifestcode.setText(reaction.get("manifest_text"));
        manifestList.add(manifestcode);
        allergyReaction.setManifestation(manifestList);
        //set severity
        allergyReaction.setSeverity(AllergyIntolerance.AllergyIntoleranceSeverity.valueOf(reaction.get("severity")));
        allergyReaactionList.add(allergyReaction);
        allergyIntolerance.setReaction(allergyReaactionList);

        //set asserted date
        allergyIntolerance.setAssertedDate(dafAllergyIntolerance.getAssertedDate());

        return allergyIntolerance;
    }


    /**
     * This method is implemented to get AllergyIntolerances for Bulk data request
     * @param patients
     * @param start
     * @return This method returns a list of AllergyIntolerance. This list may contain multiple matching resources, or it may also be empty.
     */
        public List<AllergyIntolerance> getAllergyIntoleranceForBulkDataRequest(List<Integer> patients, Date start) {

                List<DafAllergyIntolerance> dafAllergyIntoleranceList = service.getAllergyIntoleranceForBulkData(patients, start);

                List<AllergyIntolerance> allergyIntoleranceList = new ArrayList<AllergyIntolerance>();

                for (DafAllergyIntolerance dafAllergyIntolerance : dafAllergyIntoleranceList) {
                        allergyIntoleranceList.add(createAllergyIntoleranceObject(dafAllergyIntolerance));
                }

                return allergyIntoleranceList;
        }

}
