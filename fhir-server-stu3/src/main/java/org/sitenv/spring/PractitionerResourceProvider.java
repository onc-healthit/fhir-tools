package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.query.PractitionerSearchCriteria;
import org.sitenv.spring.service.PractitionerService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Scope("request")
public class PractitionerResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Practitioner";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    PractitionerService service;

    public PractitionerResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (PractitionerService) context.getBean("practitionerService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<Practitioner> getResourceType() {
        return Practitioner.class;
    }

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     * Ex: http://<server name>/<context>/fhir/Practitioner/1?_format=json
     */
    @Read()
    public Practitioner getPractitionerResourceById(@IdParam IdType theId) {

        DafPractitioner dafPractitioner = service.getPractitionerById(theId.getIdPartAsLong().intValue());

        Practitioner docPrac = createPractitionerObject(dafPractitioner);

        return docPrac;
    }

    @Search
    public List<Practitioner> getAllPractitioners(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafPractitioner> dafPractitionersList = service.getAllPractitioners();

        List<Practitioner> practitionerList = new ArrayList<Practitioner>();
        for (DafPractitioner dafPractitioner : dafPractitionersList) {
            practitionerList.add(createPractitionerObject(dafPractitioner));
        }
        return practitionerList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param theId
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available Practitioner records.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Practitioner?_id=1&_format=json
     */
    @Search()
    public Practitioner searchById(@RequiredParam(name = Practitioner.SP_RES_ID) ReferenceParam theId,
                                   @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        String practitionerId = theId.getIdPart();
        int pracId = Integer.parseInt(practitionerId);
        DafPractitioner dafPrac = service.getPractitionerById(pracId);

        Practitioner docPrac = createPractitionerObject(dafPrac);

        return docPrac;
    }

    @Search()
    public List<Practitioner> findPractitionerByIdentifierValue(@RequiredParam(name = Practitioner.SP_IDENTIFIER) TokenParam theIdentifierValue,
                                                                @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        PractitionerSearchCriteria searchOption = new PractitionerSearchCriteria();
        searchOption.setIdentifier(theIdentifierValue);
        List<DafPractitioner> dafPractitionerList = service.getPractitionerBySearchCriteria(searchOption);

        List<Practitioner> practitionerList = new ArrayList<Practitioner>();
        for (DafPractitioner dafPractitioner : dafPractitionerList) {
            practitionerList.add(createPractitionerObject(dafPractitioner));
        }
        return practitionerList;
    }

    @Search()
    public List<Practitioner> findPractitionerByGivenName(@RequiredParam(name = Practitioner.SP_GIVEN) StringType theGivenName,
                                                          @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        PractitionerSearchCriteria searchOption = new PractitionerSearchCriteria();
        searchOption.setGivenName(theGivenName.getValue());
        List<DafPractitioner> dafPractitionerList = service.getPractitionerBySearchCriteria(searchOption);

        List<Practitioner> practitionerList = new ArrayList<Practitioner>();
        for (DafPractitioner dafPractitioner : dafPractitionerList) {
            practitionerList.add(createPractitionerObject(dafPractitioner));
        }
        return practitionerList;
    }

    @Search()
    public List<Practitioner> findPatientsByFamilyName(@RequiredParam(name = Patient.SP_FAMILY) StringType theFamilyName,
                                                       @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        PractitionerSearchCriteria searchOption = new PractitionerSearchCriteria();
        searchOption.setFamilyName(theFamilyName.getValue());
        List<DafPractitioner> dafPractitionerList = service.getPractitionerBySearchCriteria(searchOption);

        List<Practitioner> practitionerList = new ArrayList<Practitioner>();
        for (DafPractitioner dafPractitioner : dafPractitionerList) {
            practitionerList.add(createPractitionerObject(dafPractitioner));
        }
        return practitionerList;
    }

    /**
     * This method converts DafPractitioner object to Practitioner object
     */
    private Practitioner createPractitionerObject(DafPractitioner dafPractitioner) {

        Practitioner docPrac = new Practitioner();

        //ID
        docPrac.setId(new IdType(RESOURCE_TYPE, dafPractitioner.getId() + "", VERSION_ID));

        //Identifier
        Map<String, String> identifier = HapiUtils.convertToJsonMap(dafPractitioner.getIdentifier());
        List<Identifier> identifierDtList = new ArrayList<Identifier>();
        Identifier identifierDt = new Identifier();
        identifierDt.setSystem(identifier.get("system"));
        identifierDt.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifierDt.setValue(identifier.get("value"));
        identifierDtList.add(identifierDt);

        docPrac.setIdentifier(identifierDtList);

        //Name
        List<HumanName> humanNameList = new ArrayList<HumanName>();

        HumanName name = new HumanName();
        name.setFamily(dafPractitioner.getFamilyName());
        name.addGiven(dafPractitioner.getGivenName());
        humanNameList.add(name);
        docPrac.setName(humanNameList);

        //Telecom
        Map<String, String> telecom = HapiUtils.convertToJsonMap(dafPractitioner.getTelecom());
        List<ContactPoint> contactPointDtList = new ArrayList<ContactPoint>();
        ContactPoint phoneContact = new ContactPoint();
        phoneContact.setSystem(ContactPoint.ContactPointSystem.valueOf(telecom.get("system")));
        phoneContact.setUse(ContactPoint.ContactPointUse.valueOf(telecom.get("use")));
        phoneContact.setValue(telecom.get("value"));
        contactPointDtList.add(phoneContact);
        docPrac.setTelecom(contactPointDtList);

        //Text
        Map<String, String> text = HapiUtils.convertToJsonMap(dafPractitioner.getText());
        Narrative nText = new Narrative();
        nText.setStatus(Narrative.NarrativeStatus.valueOf(text.get("status")));
        nText.setDivAsString(text.get("div"));
        docPrac.setText(nText);

        return docPrac;

    }
}
