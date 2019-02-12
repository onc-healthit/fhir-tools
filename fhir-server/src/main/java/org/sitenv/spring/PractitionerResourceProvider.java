package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Practitioner.PractitionerRole;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.NarrativeStatusEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafPractitioner;
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
    public static final String VERSION_ID = "2.0";
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
    @Read(version = false)
    public Practitioner getPractitionerResourceById(@IdParam IdDt theId) {

        DafPractitioner dafPractitioner = service.getPractitionerById(theId.getIdPartAsLong().intValue());

        Practitioner docPrac = createPractitionerObject(dafPractitioner);

        return docPrac;
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

    /**
     * This method converts DafPractitioner object to Practitioner object
     */
    private Practitioner createPractitionerObject(DafPractitioner dafPractitioner) {

        Practitioner docPrac = new Practitioner();

        //ID
        docPrac.setId(new IdDt(RESOURCE_TYPE, dafPractitioner.getId() + "", VERSION_ID));

        //Identifier
        Map<String, String> identifier = HapiUtils.convertToJsonMap(dafPractitioner.getIdentifier());
        List<IdentifierDt> identifierDtList = new ArrayList<IdentifierDt>();
        IdentifierDt identifierDt = new IdentifierDt();
        identifierDt.setSystem(new UriDt(identifier.get("system")));
        identifierDt.setUse(IdentifierUseEnum.OFFICIAL);
        identifierDt.setValue(identifier.get("value"));
        identifierDtList.add(identifierDt);

        docPrac.setIdentifier(identifierDtList);

        //Name
        HumanNameDt name = new HumanNameDt();
        name.addFamily(dafPractitioner.getFamilyName());
        name.addGiven(dafPractitioner.getGivenName());

        docPrac.setName(name);

        //Telecom
        Map<String, String> telecom = HapiUtils.convertToJsonMap(dafPractitioner.getTelecom());
        List<ContactPointDt> contactPointDtList = new ArrayList<ContactPointDt>();
        ContactPointDt phoneContact = new ContactPointDt();
        phoneContact.setSystem(ContactPointSystemEnum.valueOf(telecom.get("system")));
        phoneContact.setUse(ContactPointUseEnum.valueOf(telecom.get("use")));
        phoneContact.setValue(telecom.get("value"));
        contactPointDtList.add(phoneContact);
        docPrac.setTelecom(contactPointDtList);

        //Text
        Map<String, String> text = HapiUtils.convertToJsonMap(dafPractitioner.getText());
        NarrativeDt nText = new NarrativeDt();
        nText.setStatus(NarrativeStatusEnum.valueOf(text.get("status")));
        nText.setDiv(text.get("div"));
        docPrac.setText(nText);

        List<PractitionerRole> practitionerList = new ArrayList<PractitionerRole>();
        PractitionerRole practitionerRole = new PractitionerRole();
        CodeableConceptDt codeableConceptDt = new CodeableConceptDt();
        CodingDt codingDt = new CodingDt();
        codingDt.setCode(dafPractitioner.getPractitionerrole());
        codeableConceptDt.addCoding(codingDt);
        practitionerRole.setRole(codeableConceptDt);
        practitionerList.add(practitionerRole);
        docPrac.setPractitionerRole(practitionerList);

        return docPrac;

    }

}
