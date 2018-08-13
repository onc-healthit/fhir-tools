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
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Observation.ObservationReferenceRangeComponent;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.query.ObservationSearchCriteria;
import org.sitenv.spring.service.ObservationService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObservationResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Observation";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    ObservationService service;


    public ObservationResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (ObservationService) context.getBean("observationResourceService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<Observation> getResourceType() {
        return Observation.class;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available Observation records.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Observation?_pretty=true&_format=json
     */
    @Search
    public List<Observation> getAllObservations(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafObservation> dafObservationsList = service.getAllObservations();

        List<Observation> observationList = new ArrayList<Observation>();

        for (DafObservation dafObservation : dafObservationsList) {
            if (dafObservation.getPatient() != null) {
                observationList.add(createObservationObject(dafObservation));
            }
        }

        return observationList;
    }

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdType and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     */
    @Read()
    public Observation getObservationResourceById(@IdParam IdType theId) {

        try {
            Integer observationId = theId.getIdPartAsLong().intValue();
            DafObservation dafObservation = service.getObservationResourceById(observationId);
            if (dafObservation.getPatient() != null) {
                Observation observation = createObservationObject(dafObservation);
                return observation;
            } else {
                throw new ResourceNotFoundException(theId);
            }
        } catch (Exception e) {
            /*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
            throw new ResourceNotFoundException(theId);
        }
    }

    @Search
    public Observation getObservationResourceVitalSignsById(@RequiredParam(name = "vital-signs") ReferenceParam theId) {
        try {
            DafObservation dafObservation = service
                    .getObservationResourceByIdandCategory(theId.getIdPartAsLong().intValue(), "vital-signs");

            Observation observation = createObservationObject(dafObservation);

            return observation;
        } catch (Exception e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an
			 * unknown resource
			 */
            throw new ResourceNotFoundException("Resource vital-signs with Id " + theId.getIdPart() + " is not present");
        }
    }

    @Search
    public Observation getObservationResourceBySocialHistoryId(
            @RequiredParam(name = "social-history") ReferenceParam theId) {
        try {

            DafObservation dafObservation = service
                    .getObservationResourceByIdandCategory(theId.getIdPartAsLong().intValue(), "social-history");

            Observation observation = createObservationObject(dafObservation);

            return observation;
        } catch (Exception e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an
			 * unknown resource
			 */
            throw new ResourceNotFoundException("Resource social-history with Id " + theId.getIdPart() + " is not present");
        }
    }

    @Search
    public Observation getObservationResourceByResultsId(@RequiredParam(name = "laboratory") ReferenceParam theId) {
        try {

            DafObservation dafObservation = service
                    .getObservationResourceByIdandCategory(theId.getIdPartAsLong().intValue(), "laboratory");

            Observation observation = createObservationObject(dafObservation);

            return observation;
        } catch (Exception e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an
			 * unknown resource
			 */
            throw new ResourceNotFoundException("Resource laboratory with Id " + theId.getIdPart() + " is not present");
        }
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theCategory
     * @param theCode
     * @param theRange
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of Observations. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Observation?patient=1&category=vital-signs&_format=json
     */
    @Search()
    public List<Observation> searchByPatient(@RequiredParam(name = Observation.SP_PATIENT) ReferenceParam thePatient,
                                             @OptionalParam(name = Observation.SP_CATEGORY) StringType theCategory,
                                             @OptionalParam(name = Observation.SP_CODE) StringOrListParam theCode,
                                             @OptionalParam(name = Observation.SP_DATE) DateRangeParam theRange,
                                             @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();

        ObservationSearchCriteria observationSearchCriteria = new ObservationSearchCriteria();
        observationSearchCriteria.setPatient(Integer.parseInt(patientId));
        if (theCategory != null) {
            observationSearchCriteria.setCategory(theCategory.getValue());
        }
        if (theCode != null) {
            List<StringParam> wantedCodings = theCode.getValuesAsQueryTokens();
            List<String> aList = new ArrayList<String>();
            for (int i = 0; i < wantedCodings.size(); i++) {
                aList.add(wantedCodings.get(i).getValue().toString());
            }
            observationSearchCriteria.setCode(aList);
        }
        if (theRange != null) {
            observationSearchCriteria.setRangedates(theRange);
        }
        //List<DafObservation> dafObservationList = service.getObservationByPatient(patientId);
        List<DafObservation> dafObservationList = service.getObservationBySearchCriteria(observationSearchCriteria);

        List<Observation> observationList = new ArrayList<Observation>();

        for (DafObservation dafObservation : dafObservationList) {
            if (dafObservation.getPatient() != null) {
                observationList.add(createObservationObject(dafObservation));
            }
        }
        return observationList;
    }

    /**
     * This method converts DafObservation object to Observation object
     */
    private Observation createObservationObject(DafObservation dafObservation) {
        Observation observation = new Observation();

        // Set Version
        observation.setId(new IdType(RESOURCE_TYPE, dafObservation.getId() + "", VERSION_ID));

        //set patient
        Reference patientResource = new Reference();
        String theId = "Patient/" + Integer.toString(dafObservation.getPatient().getId());
        patientResource.setReference(theId);
        observation.setSubject(patientResource);

        //set Observation Identifier
        List<Identifier> identifierDts = new ArrayList<Identifier>();
        Identifier identifierDt = new Identifier();
        identifierDt.setSystem(dafObservation.getIdentifier_system().trim());
        identifierDt.setValue(dafObservation.getIdentifier_value().trim());
        identifierDts.add(identifierDt);
        observation.setIdentifier(identifierDts);

        //Set Observation Code
        CodeableConcept classCodeDt = new CodeableConcept();
        Coding classCoding = new Coding();
        classCoding.setSystem(dafObservation.getCode_system().trim());
        classCoding.setCode(dafObservation.getCode().trim());
        classCoding.setDisplay(dafObservation.getCode_display().trim());
        classCodeDt.addCoding(classCoding);
        classCodeDt.setText(dafObservation.getCode_text().trim());
        observation.setCode(classCodeDt);

        //Set observation category
        List<CodeableConcept> category = new ArrayList<CodeableConcept>();
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setCode(dafObservation.getCat_code().trim());
        coding.setDisplay(dafObservation.getCat_display().trim());
        coding.setSystem(dafObservation.getCat_system().trim());
        codeableConcept.addCoding(coding);
        category.add(codeableConcept);
        observation.setCategory(category);

        //Set Observation Status
        observation.setStatus(Observation.ObservationStatus.valueOf(dafObservation.getStatus().trim()));

        if (dafObservation.getCat_code().trim().equalsIgnoreCase("SOCIAL-HISTORY")) {

            //set value
            CodeableConcept severityCodeDt = new CodeableConcept();
            Coding severityCoding = new Coding();
            severityCoding.setSystem(dafObservation.getVal_system().trim());
            severityCoding.setCode(dafObservation.getVal_code().trim());
            severityCoding.setDisplay(dafObservation.getVal_display().trim());
            severityCodeDt.addCoding(severityCoding);
            severityCodeDt.setText(dafObservation.getVal_text().trim());
            observation.setValue(severityCodeDt);

            observation.setIssued(dafObservation.getEffectiveDate());

        } else if (dafObservation.getCat_code().trim().equalsIgnoreCase("LABORATORY")) {

            Quantity quanDt = new Quantity();
            quanDt.setValue(dafObservation.getValQuanValue());
            quanDt.setUnit(dafObservation.getValQuanUnit().trim());
            quanDt.setSystem(dafObservation.getValQuanSystem().trim());

            observation.setValue(quanDt);
            DateTimeType effectDate = new DateTimeType();
            effectDate.setValue(dafObservation.getEffectiveDate());
            observation.setEffective(effectDate);

        } else if (dafObservation.getCat_code().trim().equalsIgnoreCase("VITAL-SIGNS")) {

            if (dafObservation.getVal_code().trim().equals("55284-4")) {
                List<DafObservation> bpList = service.getObservationByBPCode(dafObservation.getVal_code());
                List<Observation.ObservationComponentComponent> components = new ArrayList<Observation.ObservationComponentComponent>();
                for (int i = 0; i < bpList.size(); i++) {
                    Observation.ObservationComponentComponent nComp = new Observation.ObservationComponentComponent();
                    CodeableConcept compCodeDt = new CodeableConcept();
                    Coding compCoding = new Coding();
                    compCoding.setSystem(bpList.get(i).getVal_system().trim());
                    compCoding.setCode(bpList.get(i).getVal_code().trim());
                    compCoding.setDisplay(bpList.get(i).getVal_display().trim());
                    compCodeDt.addCoding(compCoding);
                    compCodeDt.setText(bpList.get(i).getVal_text().trim());

                    nComp.setCode(compCodeDt);

                    Quantity compValDt = new Quantity();
                    compValDt.setValue(bpList.get(i).getValQuanValue());
                    compValDt.setUnit(bpList.get(i).getValQuanUnit().trim());
                    compValDt.setSystem(bpList.get(i).getValQuanSystem().trim());

                    compValDt.setCode(bpList.get(i).getValQuanCode().trim());

                    nComp.setValue(compValDt);

                    components.add(nComp);
                }
                observation.setComponent(components);
            } else {
                List<Observation.ObservationComponentComponent> compList = new ArrayList<Observation.ObservationComponentComponent>();
                Observation.ObservationComponentComponent nComp = new Observation.ObservationComponentComponent();
                CodeableConcept compCodeDt = new CodeableConcept();
                Coding compCoding = new Coding();
                compCoding.setSystem(dafObservation.getVal_system().trim());
                compCoding.setCode(dafObservation.getVal_code().trim());
                compCoding.setDisplay(dafObservation.getVal_display().trim());
                compCodeDt.addCoding(compCoding);
                compCodeDt.setText(dafObservation.getVal_text().trim());

                nComp.setCode(compCodeDt);

                Quantity compValDt = new Quantity();
                compValDt.setValue(dafObservation.getValQuanValue());
                compValDt.setUnit(dafObservation.getValQuanUnit().trim());
                compValDt.setSystem(dafObservation.getValQuanSystem().trim());

                compValDt.setCode(dafObservation.getValQuanCode().trim());

                nComp.setValue(compValDt);

                compList.add(nComp);

                observation.setComponent(compList);
            }
            DateTimeType vitalEffectDate = new DateTimeType();
            vitalEffectDate.setValue(dafObservation.getEffectiveDate());
            observation.setEffective(vitalEffectDate);
        }

        //set reference range
        String lowRange = dafObservation.getReferenceLow();
        String highRange = dafObservation.getReferenceHigh();
        if (highRange != null || lowRange != null) {
            Map<String, String> rhSet = HapiUtils.convertToJsonMap(highRange);
            Map<String, String> rlSet = HapiUtils.convertToJsonMap(lowRange);
            List<ObservationReferenceRangeComponent> theReferenceRange = new ArrayList<Observation.ObservationReferenceRangeComponent>();
            ObservationReferenceRangeComponent reference = new ObservationReferenceRangeComponent();
            if (rhSet != null) {
                SimpleQuantity high = new SimpleQuantity();
                high.setSystem(rhSet.get("system"));
                high.setCode(rhSet.get("code"));
                high.setUnit(rhSet.get("unit"));
                high.setValue(new BigDecimal(rhSet.get("value")));
                reference.setHigh(high);
            }

            if (rlSet != null) {
                SimpleQuantity low = new SimpleQuantity();
                low.setSystem(rlSet.get("system"));
                low.setCode(rlSet.get("code"));
                low.setUnit(rlSet.get("unit"));
                low.setValue(new BigDecimal(rlSet.get("value")));
                reference.setLow(low);
            }
            theReferenceRange.add(reference);
            observation.setReferenceRange(theReferenceRange);
        }

        return observation;
    }

	public List<Observation> getObservationForBulkDataRequest(List<Integer> patients, Date start) {
		List<DafObservation> dafObservationList = service.getObservationForBulkData(patients, start);

        List<Observation> observationList = new ArrayList<Observation>();

        for (DafObservation dafObservation : dafObservationList) {
                observationList.add(createObservationObject(dafObservation));
        }

        return observationList;
	}

}
