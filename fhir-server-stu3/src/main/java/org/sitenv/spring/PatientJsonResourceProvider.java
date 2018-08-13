package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.*;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafBulkDataRequest;
import org.sitenv.spring.model.DafPatientJson;
import org.sitenv.spring.query.ObservationSearchCriteria;
import org.sitenv.spring.query.PatientSearchCriteria;
import org.sitenv.spring.service.BulkDataRequestService;
import org.sitenv.spring.service.PatientService;
import org.sitenv.spring.util.HapiConstants;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Prabhushankar.Byrapp on 8/22/2015.
 */

@Scope("request")
public class PatientJsonResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Patient";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    PatientService service;
    BulkDataRequestService bdrService;

    public PatientJsonResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (PatientService) context.getBean("patientService");
        bdrService = (BulkDataRequestService) context.getBean("bulkDataRequestService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     */
    @Read(version = true)
    public Patient readPatient(@IdParam IdType theId) {
        int id;
        try {
            id = theId.getIdPartAsLong().intValue();
        } catch (NumberFormatException e) {
            /*
             * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
            throw new ResourceNotFoundException(theId);
        }
        DafPatientJson dafPatient = service.getPatientById(id);

        return createPatientObject(dafPatient);
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by Resource id
     *
     * @param theId This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *              the search criteria. The data type here is String, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a ]Patient. This list may patient resource, or it may also be empty.
     */
    @Search
    public Patient searchPatient(@RequiredParam(name = Patient.SP_RES_ID) String theId) {
        int id;
        try {
            id = Integer.parseInt(theId);
        } catch (NumberFormatException e) {
            /*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
            throw new ResourceNotFoundException(theId);
        }
        DafPatientJson dafPatient = service.getPatientById(id);

        return createPatientObject(dafPatient);
    }

    /**
     * This method returns all the available Patients records.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/Patient?_pretty=true&_format=json
     */
    @Search
    public List<Patient> getAllPatient(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafPatientJson> dafPatientList = service.getAllPatient();

        List<Patient> patientList = new ArrayList<Patient>();
        for (DafPatientJson dafPatient : dafPatientList) {
            patientList.add(createPatientObject(dafPatient));
        }
        return patientList;
    }


    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by Identifier Value
     *
     * @param theIdentifierValue This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                           the search criteria. The datatype here is String, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Patient> findPatientsByIdentifierValue(@RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam theIdentifierValue,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        PatientSearchCriteria searchOption = new PatientSearchCriteria();
        searchOption.setIdentifier(theIdentifierValue);
        List<DafPatientJson> dafPatientList = service.getPatientBySearchOption(searchOption);

        List<Patient> patientList = new ArrayList<Patient>();
        for (DafPatientJson dafPatient : dafPatientList) {
            patientList.add(createPatientObject(dafPatient));
        }
        return patientList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by given name.
     *
     * @param theGivenName This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                     the search criteria. The datatype here is StringType, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Patient> findPatientsByGivenName(@RequiredParam(name = Patient.SP_GIVEN) StringType theGivenName,
                                                 @OptionalParam(name = Patient.SP_GENDER) StringParam theGender,
                                                 @OptionalParam(name = Patient.SP_FAMILY) StringType theFamily,
                                                 @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        PatientSearchCriteria searchOption = new PatientSearchCriteria();
        searchOption.setGivenName(theGivenName.getValue());
        if (theGender != null) {
            searchOption.setGender(theGender);
        }
        if (theFamily != null) {
            searchOption.setFamilyName(theFamily.getValue());
        }
        List<DafPatientJson> dafPatientList = service.getPatientBySearchOption(searchOption);

        List<Patient> patientList = new ArrayList<Patient>();
        for (DafPatientJson dafPatient : dafPatientList) {
            patientList.add(createPatientObject(dafPatient));
        }
        return patientList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by family name.
     *
     * @param theFamilyName This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                      the search criteria. The datatype here is StringType, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Patient> findPatientsByFamilyName(@RequiredParam(name = Patient.SP_FAMILY) StringType theFamilyName,
                                                  @OptionalParam(name = Patient.SP_GENDER) StringParam theGender,
                                                  @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        PatientSearchCriteria searchOption = new PatientSearchCriteria();
        searchOption.setFamilyName(theFamilyName.getValue());
        if (theGender != null) {
            searchOption.setGender(theGender);
        }
        List<DafPatientJson> dafPatientList = service.getPatientBySearchOption(searchOption);

        List<Patient> patientList = new ArrayList<Patient>();
        for (DafPatientJson dafPatient : dafPatientList) {
            patientList.add(createPatientObject(dafPatient));
        }
        return patientList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by family name or given name.
     *
     * @param theName This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                the search criteria. The datatype here is StringType, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Patient> findPatientsByName(@RequiredParam(name = Patient.SP_NAME) StringParam theName,
                                            @OptionalParam(name = Patient.SP_GENDER) StringParam theGender,
                                            @OptionalParam(name = Patient.SP_BIRTHDATE) DateParam theBirthDate,
                                            @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        PatientSearchCriteria searchOption = new PatientSearchCriteria();
        searchOption.setFullName(theName);
        if (theGender != null) {
            searchOption.setGender(theGender);
        }
        if (theBirthDate != null) {
            searchOption.setBirthDate(theBirthDate);
           /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                searchOption.setBirthDate(sdf.parse(theBirthDate.toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
        }
        List<DafPatientJson> dafPatientList = service.getPatientBySearchOption(searchOption);

        List<Patient> patientList = new ArrayList<Patient>();
        for (DafPatientJson dafPatient : dafPatientList) {
            patientList.add(createPatientObject(dafPatient));
        }
        return patientList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by birth date
     *
     * @param theBirthDate This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                     the search criteria. The datatype here is StringType, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be empty.
     */
    @Search()
    public List<Patient> findPatientsByBirthDate(@RequiredParam(name = Patient.SP_BIRTHDATE) DateParam theBirthDate,
                                                 @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        PatientSearchCriteria searchOption = new PatientSearchCriteria();
        searchOption.setBirthDate(theBirthDate);
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            searchOption.setBirthDate(sdf.parse(theBirthDate.toString()));
        } catch (Exception e) {

        }*/
        List<DafPatientJson> dafPatientList = service.getPatientBySearchOption(searchOption);

        List<Patient> patientList = new ArrayList<Patient>();
        for (DafPatientJson dafPatient : dafPatientList) {
            patientList.add(createPatientObject(dafPatient));
        }
        return patientList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by telecom data
     *
     * @param theTelecomData This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                       the search criteria. The datatype here is StringType, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be empty.
     */

    @Search()
    public List<Patient> findPatientsByTelephoneNumber(@RequiredParam(name = Patient.SP_TELECOM) StringType theTelecomData,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        PatientSearchCriteria searchOption = new PatientSearchCriteria();
        searchOption.setTelecom(theTelecomData.toString());
        List<DafPatientJson> dafPatientList = service.getPatientBySearchOption(searchOption);

        List<Patient> patientList = new ArrayList<Patient>();
        for (DafPatientJson dafPatient : dafPatientList) {
            patientList.add(createPatientObject(dafPatient));
        }
        return patientList;
    }


    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by gender type
     *
     * @param theGenderType This operation takes one parameter which is the search criteria. It is annotated with the "@Required" annotation. This annotation takes one argument, a string containing the name of
     *                      the search criteria. The datatype here is StringType, but there are other possible parameter types depending on the specific search criteria.
     * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be empty.
     */

    @Search()
    public List<Patient> findPatientsByGenderType(@RequiredParam(name = Patient.SP_GENDER) StringParam theGenderType,
                                                  @OptionalParam(name = Patient.SP_BIRTHDATE) DateParam theBirthDate,
                                                  @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        PatientSearchCriteria searchOption = new PatientSearchCriteria();
        searchOption.setGender(theGenderType);
        if (theBirthDate != null) {
            searchOption.setBirthDate(theBirthDate);
           /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                searchOption.setBirthDate(sdf.parse(theBirthDate.toString()));
            } catch (Exception e) {

            }*/
        }
        List<DafPatientJson> dafPatientList = service.getPatientBySearchOption(searchOption);

        List<Patient> patientList = new ArrayList<Patient>();
        for (DafPatientJson dafPatient : dafPatientList) {
            patientList.add(createPatientObject(dafPatient));
        }
        return patientList;
    }


    @Search(compartmentName = "DocumentReference")
    public List<DocumentReference> readByCompartment(@IdParam IdType theId,
                                                     @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        ReferenceParam patientId = new ReferenceParam("Patient/" + theId.getIdPart());
        DocumentReferenceResourceProvider res = new DocumentReferenceResourceProvider();

        List<DocumentReference> docRefList = new ArrayList<DocumentReference>();
        docRefList = res.searchByPatient(patientId, theIncludes, theSort, theCount);

        return docRefList;

    }

    /*** will come back to fix this later - Prabhu

     @Search(compartmentName = "MedicationOrder")
     public List<MedicationOrder> readByMedicationOrderCompartment(@IdParam IdDt theId,
     @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
     ReferenceParam patientId = new ReferenceParam("Patient/" + theId.getIdPart());
     MedicationOrderResourceProvider medRes = new MedicationOrderResourceProvider();

     List<MedicationOrder> docRefList = new ArrayList<MedicationOrder>();
     docRefList = medRes.searchByPatient(patientId, theIncludes, theSort, theCount);

     return docRefList;

     }

     ****/

    @Search(compartmentName = "MedicationStatement")
    public List<MedicationStatement> readByMedicationStatementCompartment(@IdParam IdType theId,
                                                                          @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        ReferenceParam patientId = new ReferenceParam("Patient/" + theId.getIdPart());
        MedicationStatementResourceProvider medRes = new MedicationStatementResourceProvider();

        List<MedicationStatement> docRefList = new ArrayList<MedicationStatement>();
        docRefList = medRes.searchByPatient(patientId, theIncludes, theSort, theCount);

        return docRefList;

    }

    @Search(compartmentName = "Condition")
    public List<Condition> readByConditionCompartment(@IdParam IdType theId,
                                                      @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        ReferenceParam patientId = new ReferenceParam("Patient/" + theId.getIdPart());
        ConditionResourceProvider medRes = new ConditionResourceProvider();

        List<Condition> docRefList = new ArrayList<Condition>();
        StringOrListParam theStatus = null;
        StringType theCategory = null;
        docRefList = medRes.searchByPatient(patientId, theStatus, theCategory, theIncludes, theSort, theCount);

        return docRefList;

    }

    @Search(compartmentName = "AllergyIntolerance")
    public List<AllergyIntolerance> readByAllergyIntoleranceCompartment(@IdParam IdType theId,
                                                                        @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        ReferenceParam patientId = new ReferenceParam("Patient/" + theId.getIdPart());
        AllergyIntoleranceResourceProvider medRes = new AllergyIntoleranceResourceProvider();

        List<AllergyIntolerance> docRefList = new ArrayList<AllergyIntolerance>();
        docRefList = medRes.searchByPatient(patientId, theIncludes, theSort, theCount);

        return docRefList;

    }

    @Search(compartmentName = "Observation")
    public List<Observation> readByObservationCompartment(@IdParam IdType theId,
                                                          @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        ReferenceParam patientId = new ReferenceParam("Patient/" + theId.getIdPart());
        ObservationResourceProvider obsRes = new ObservationResourceProvider();
        ObservationSearchCriteria observationSearchCriteria = new ObservationSearchCriteria();
        List<Observation> docRefList = new ArrayList<Observation>();
        observationSearchCriteria.setPatient(Integer.parseInt(theId.getIdPart()));
        docRefList = obsRes.searchByPatient(patientId, null, null, null, theIncludes, theSort, theCount);

        return docRefList;

    }

    @Search(compartmentName = "DiagnosticReport")
    public List<DiagnosticReport> readByDiagnosticReportCompartment(@IdParam IdType theId,
                                                                    @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        ReferenceParam patientId = new ReferenceParam("Patient/" + theId.getIdPart());
        DiagnosticReportResourceProvider diagnosticRes = new DiagnosticReportResourceProvider();

        List<DiagnosticReport> docRefList = new ArrayList<DiagnosticReport>();
        docRefList = diagnosticRes.searchByPatient(patientId, null, null, null, theIncludes, theSort, theCount);

        return docRefList;

    }


    /**
     * This method converts DafDocumentReference object to DocumentReference object
     */
    private Patient createPatientObject(DafPatientJson dafPatient) {
        Patient patient = new Patient();

        // Set Version
        patient.setId(new IdType(RESOURCE_TYPE, dafPatient.getId() + "", VERSION_ID));

        //Set Identifier
        //patient.setIdentifier()
        Map<String, String> identifier = HapiUtils.convertToJsonMap(dafPatient.getIdentifier());
        List<Identifier> identifierDtList = new ArrayList<Identifier>();
        Identifier identifierDt = new Identifier();
        identifierDt.setSystem(identifier.get("system"));
        identifierDt.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifierDt.setValue(identifier.get("value"));
        identifierDtList.add(identifierDt);
        patient.setIdentifier(identifierDtList);

        // Set Name
        //Adding Name
        List<HumanName> nameList = new ArrayList<HumanName>();
        HumanName name = new HumanName();
        name.setFamily(dafPatient.getFamilyName());
        name.addGiven(dafPatient.getGivenName());
        nameList.add(name);
        patient.setName(nameList);

        //telecom
        Map<String, String> telecom = HapiUtils.convertToJsonMap(dafPatient.getTelecom());
        List<ContactPoint> contactPointDtList = new ArrayList<ContactPoint>();
        ContactPoint phoneContact = new ContactPoint();
        phoneContact.setSystem(ContactPoint.ContactPointSystem.valueOf(telecom.get("system")));
        phoneContact.setUse(ContactPoint.ContactPointUse.valueOf(telecom.get("use")));
        phoneContact.setValue(telecom.get("value"));
        contactPointDtList.add(phoneContact);
        patient.setTelecom(contactPointDtList);

        //Gender
        patient.setGender(Enumerations.AdministrativeGender.valueOf(dafPatient.getGender()));

        //Birth Date
        patient.setBirthDate(dafPatient.getBirthDate());

        //Address - need formatting for postal code
        List<Address> addressDtList = new ArrayList<Address>();
        Address addressDt = new Address();
        List<StringType> addressLines = new ArrayList<StringType>();

        StringType line1 = new StringType();
        line1.setValue(dafPatient.getAddressLine1());
        addressLines.add(line1);
        StringType line2 = new StringType();
        line2.setValue(dafPatient.getAddressLine2());
        addressLines.add(line2);
        addressDt.setLine(addressLines);
        addressDt.setCity(dafPatient.getAddressCity());
        addressDt.setState(dafPatient.getAddressState());
        addressDt.setPostalCode(dafPatient.getAddressZip());
        addressDt.setCountry(dafPatient.getAddressCountry());
        addressDtList.add(addressDt);

        patient.setAddress(addressDtList);

        //maritalStatus -- Need to check back on this -Prabhu
        patient.setMaritalStatus(new CodeableConcept().setText(dafPatient.getMaritalStatus().getCode()));

        //Communication
        Map<String, String> communication = HapiUtils.convertToJsonMap(dafPatient.getLanguage());
        List<Patient.PatientCommunicationComponent> languageList = new ArrayList<Patient.PatientCommunicationComponent>();
        Patient.PatientCommunicationComponent language = new Patient.PatientCommunicationComponent();
    	
       language.setLanguage(HapiUtils.setCodeableConceptValues(communication.get("display"), communication.get("code"), communication.get("system")));
       languageList.add(language);
       patient.setCommunication(languageList);

        //active
        patient.setActive(true);

        //mothersMaidenName
        Extension extMothersMaidenName = new Extension();
        extMothersMaidenName.setUrl("http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName");
        extMothersMaidenName.setValue(new StringType(dafPatient.getMothersMaidenName()));
        // Add the extension to the resource
        patient.addExtension(extMothersMaidenName);

        //BirthPlace
        Map<String, String> birthPlaceAddress = HapiUtils.convertToJsonMap(dafPatient.getBirthPlace());

        Address birthPlaceDt = new Address();
        birthPlaceDt.setUse(Address.AddressUse.valueOf(StringUtils.upperCase(birthPlaceAddress.get("use"))));
        List<StringType> contactAddressLines = new ArrayList<StringType>();
        StringType contactLine1 = new StringType();
        contactLine1.setValue(birthPlaceAddress.get("line1"));
        contactAddressLines.add(contactLine1);
        StringType contactLine2 = new StringType();
        contactLine2.setValue(birthPlaceAddress.get("line2"));
        contactAddressLines.add(contactLine2);
        birthPlaceDt.setLine(contactAddressLines);
        birthPlaceDt.setCity(birthPlaceAddress.get("city"));
        birthPlaceDt.setState(birthPlaceAddress.get("state"));
        birthPlaceDt.setPostalCode(birthPlaceAddress.get("postalCode"));
        birthPlaceDt.setCountry(birthPlaceAddress.get("country"));

        Extension birthPlace = new Extension();
        birthPlace.setUrl("http://hl7.org/fhir/StructureDefinition/birthPlace");
        birthPlace.setValue(birthPlaceDt);
        patient.addExtension(birthPlace);

        //us-core-race
        Extension race = new Extension();
        race.setUrl(HapiConstants.US_CORE_RACE_SYSTEM);
        race.setValue(HapiUtils.setCodeableConceptValues(dafPatient.getRace().getDisplay(), dafPatient.getRace().getCode(), HapiConstants.FHIR_CORE_RACE_SYSTEM));
        patient.addExtension(race);

        //us-core-ethnicity
        Extension ethnicity = new Extension();
        ethnicity.setUrl(HapiConstants.US_CORE_ETHNICITY_SYSTEM);
        ethnicity.setValue(HapiUtils.setCodeableConceptValues(dafPatient.getEthnicity().getDisplay(), dafPatient.getEthnicity().getCode(), HapiConstants.FHIR_CORE_ETHNICITY_SYSTEM));
        patient.addExtension(ethnicity);

        //us-core-religion
        Extension religion = new Extension();
        religion.setUrl(HapiConstants.US_CORE_RELIGION_SYSTEM);
        religion.setValue(HapiUtils.setCodeableConceptValues(dafPatient.getReligion().getDisplay(), dafPatient.getReligion().getCode(), HapiConstants.FHIR_CORE_RELIGION_SYSTEM));
        patient.addExtension(religion);

        return patient;
    }

	public List<Patient> getPatientForBulkDataRequest(List<Integer> patients, Date start) {
		List<DafPatientJson> dafPatientList = service.getPatientJsonForBulkData(patients, start);

        List<Patient> patientList = new ArrayList<Patient>();

        for (DafPatientJson dafPatient : dafPatientList) {
                patientList.add(createPatientObject(dafPatient));
        }

        return patientList;
	}

	@Operation(name="$export", idempotent=true)
    public Binary patientTypeOperation(
     //  @OperationParam(name="_since") DateDt theStart,
     //  @OperationParam(name="end") DateDt theEnd,
       @OperationParam(name="_type") String type,
       RequestDetails requestDetails,
       HttpServletRequest request,
       HttpServletResponse response) throws IOException {
        if(requestDetails.getHeader("Prefer")!= null&&requestDetails.getHeader("Accept")!= null) {
        if(requestDetails.getHeader("Prefer").equals("respond-async") && requestDetails.getHeader("Accept").equals("application/fhir+json")) {

        DafBulkDataRequest bdr = new DafBulkDataRequest();
        bdr.setResourceName("Patient");
        bdr.setStatus("Accepted");
        bdr.setProcessedFlag(false);
        /*if(theStart!=null) {
                bdr.setStart(theStart.getValueAsString());
        }*/
        bdr.setType(type);
        bdr.setRequestResource(request.getRequestURL().toString());

        DafBulkDataRequest responseBDR = bdrService.saveBulkDataRequest(bdr);

        String uri = request.getScheme() + "://" +
                request.getServerName() +
                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
                +request.getContextPath();

        response.setStatus(202);
        response.setHeader("Content-Location", uri+"/bulkdata/"+responseBDR.getRequestId());
        GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(new Date());
                cal.setTimeZone(TimeZone.getTimeZone("GMT"));
                cal.add(Calendar.DATE, 10);
        //HTTP header date format: Thu, 01 Dec 1994 16:00:00 GMT
        String o = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz").format( cal.getTime() );
        response.setHeader("Expires", o);

        Binary retVal = new Binary();

        retVal.setContentType("application/fhir+json");
        return retVal;
        }else {
            throw new UnprocessableEntityException("Invalid header values!");
        }
        }else {
            throw new UnprocessableEntityException("Prefer or Accepted Header is missing!");
        }
     }
}
