package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafPractitionerRole;
import org.sitenv.spring.service.PractitionerRoleService;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class PractitionerRoleResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "PractitionerRole";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    PractitionerRoleService service;

    public PractitionerRoleResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (PractitionerRoleService) context.getBean("practitionerRoleService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return PractitionerRole.class;
    }

    /**
     * The "@Read" annotation indicate
     * s that this method supports the read operation.
     * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation.
     * This means that the read method will support both "Read" and "VRead".
     * The IdDt may or may not have the version populated depending on the client request.
     * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
     * Example URL to invoke this method: http://<server name>/<context>/fhir/PractitionerRole/1/_history/4
     * @param theId: ID of practitionerRole
     * @return : PractitionerRole object
     */
    @Read(version=true)
    public PractitionerRole readOrVread(@IdParam IdType theId) {
        String id;
        DafPractitionerRole dafPractitionerRole;
        try {
            id = theId.getIdPart();
        } catch (NumberFormatException e) {
            /*
             * If we can't parse the ID as a long, it's not valid so this is an unknown resource
             */
            throw new ResourceNotFoundException(theId);
        }
        if (theId.hasVersionIdPart()) {
            // this is a vread
            dafPractitionerRole = service.getPractitionerRoleByVersionId(id, theId.getVersionIdPart());

        } else {
            // this is a read
            dafPractitionerRole = service.getPractitionerRoleById(id);
        }

        return createPractitionerRoleObject(dafPractitionerRole);
    }

    /**
     * The history operation retrieves a historical collection of all versions of a single resource (instance history).
     * History methods must be annotated with the @History annotation.It supports Instance History method.
     * "type=PractitionerRole.class". Instance level (history of a specific resource instance by type and ID)
     * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
     * Example URL to invoke this method: http://<server name>/<context>/fhir/PractitionerRole/1/_history
     * @param theId: ID of the practitionerRole
     * @return : List of PractitionerRole's
     */
    @History()
    public List<PractitionerRole> getPractitionerRoleHistoryById(@IdParam IdType theId) {

        String id;
        try {
            id = theId.getIdPart();
        } catch (NumberFormatException e) {
            /*
             * If we can't parse the ID as a long, it's not valid so this is an unknown resource
             */
            throw new ResourceNotFoundException(theId);
        }
        List<DafPractitionerRole> dafPractitionerRoleList = service.getPractitionerRoleHistoryById(id);

        List<PractitionerRole> practitionerRoleList = new ArrayList<PractitionerRole>();
        for (DafPractitionerRole dafPractitionerRole : dafPractitionerRoleList) {
            practitionerRoleList.add(createPractitionerRoleObject(dafPractitionerRole));
        }

        return practitionerRoleList;
    }


    /**
     * The "@Search" annotation indicates that this method supports the search operation.
     * You may have many different method annotated with this annotation, to support many different search criteria.
     * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
     * @param theServletRequest
     * @param theId
     * @param theIdentifier
     * @param theSpecialty
     * @param thePractitioner
     * @param theIncludes
     * @param theRevIncludes
     * @param theSort
     * @param theCount
     * @return
     */
    @Search()
    public IBundleProvider search(
            javax.servlet.http.HttpServletRequest theServletRequest,

            @Description(shortDefinition = "The resource identity")
            @OptionalParam(name = PractitionerRole.SP_RES_ID)
            TokenAndListParam theId,

            @Description(shortDefinition = "A PractitionerRole identifier")
            @OptionalParam(name = PractitionerRole.SP_IDENTIFIER)
            TokenParam theIdentifier,

            @Description(shortDefinition = "The Practitioner ")
            @OptionalParam(name = PractitionerRole.SP_PRACTITIONER)
            ReferenceParam thePractitioner,

            @Description(shortDefinition = "The PractitionerRole Specialty ")
            @OptionalParam(name = PractitionerRole.SP_SPECIALTY)
            TokenAndListParam theSpecialty,

            @IncludeParam(allow = {"*"})
            Set<Include> theIncludes,

            @IncludeParam(reverse=true, allow= {"*"})
            Set<Include> theRevIncludes,

            @Sort
            SortSpec theSort,

            @Count
            Integer theCount) {

        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(PractitionerRole.SP_RES_ID, theId);
        paramMap.add(Practitioner.SP_IDENTIFIER, theIdentifier);
        paramMap.add(PractitionerRole.SP_PRACTITIONER, thePractitioner);
        paramMap.add(PractitionerRole.SP_SPECIALTY, theSpecialty);


        final List<DafPractitionerRole> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> practitionerRoleList = new ArrayList<IBaseResource>();
                List<String> ids = new ArrayList<String>();
                for(DafPractitionerRole dafPractitioner : results){
                    PractitionerRole practitionerRole = createPractitionerRoleObject(dafPractitioner);
                    practitionerRoleList.add(practitionerRole);
                    ids.add(((IdType)practitionerRole.getIdElement()).getResourceType()+"/"+((IdType)practitionerRole.getIdElement()).getIdPart());
                }
                if(theRevIncludes.size() >0 ){
                    practitionerRoleList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
                }

                return practitionerRoleList;
            }

            @Override
            public Integer size() {
                return results.size();
            }

            @Override
            public InstantDt getPublished() {
                return published;
            }

            @Override
            public Integer preferredPageSize() {
                return null;
            }

            @Override
            public String getUuid() {
                return null;
            }
        };
    }



            /**
             * This method converts DafPractitionerRole object to PractitionerRole object
             */
    private PractitionerRole createPractitionerRoleObject(DafPractitionerRole dafPractitionerRole) {
        PractitionerRole practitionerRole = new PractitionerRole();
        JSONObject practitionerRoleJSON = new JSONObject(dafPractitionerRole.getData());

        // Set version
        if(!(practitionerRoleJSON.isNull("meta"))) {
            if(!(practitionerRoleJSON.getJSONObject("meta").isNull("versionId"))) {
                practitionerRole.setId(new IdType(RESOURCE_TYPE, practitionerRoleJSON.getString("id") + "", practitionerRoleJSON.getJSONObject("meta").getString("versionId")));
            }else {
                practitionerRole.setId(new IdType(RESOURCE_TYPE, practitionerRoleJSON.getString("id") + "", VERSION_ID));
            }
        }
        else {
            practitionerRole.setId(new IdType(RESOURCE_TYPE, practitionerRoleJSON.getString("id") + "", VERSION_ID));
        }

        //set practitioner
        if(!(practitionerRoleJSON.isNull("practitioner"))) {
            Reference ref = new Reference();
            ref.setReference(practitionerRoleJSON.getJSONObject("practitioner").getString("reference"));
            ref.setDisplay(practitionerRoleJSON.getJSONObject("practitioner").getString("display"));
            practitionerRole.setPractitioner(ref);
        }

        //Organization
        if(!(practitionerRoleJSON.isNull("organization"))) {
            Reference ref = new Reference();
            ref.setReference(practitionerRoleJSON.getJSONObject("organization").getString("reference"));
            ref.setDisplay(practitionerRoleJSON.getJSONObject("organization").getString("display"));
            practitionerRole.setOrganization(ref);
        }

        //Set code
        if(!(practitionerRoleJSON.isNull("code"))) {
            JSONArray codeJSON = practitionerRoleJSON.getJSONArray("code");
            int noOfCode = codeJSON.length();
            List<CodeableConcept> codeList = new ArrayList<CodeableConcept>();
            for(int i = 0; i < noOfCode; i++) {
                CodeableConcept theCategory = new CodeableConcept();
                if (!(codeJSON.getJSONObject(i).isNull("coding"))) {
                    JSONArray categoryCodingJSON = codeJSON.getJSONObject(i).getJSONArray("coding");
                    int noOfCategoryCoding = categoryCodingJSON.length();
                    List<Coding> codingList = new ArrayList<Coding>();
                    for (int j = 0; j < noOfCategoryCoding; j++) {
                        Coding categoryCoding = new Coding();
                        if (!(categoryCodingJSON.getJSONObject(j).isNull("system"))) {
                            categoryCoding.setSystem(categoryCodingJSON.getJSONObject(j).getString("system"));
                        }
                        if (!(categoryCodingJSON.getJSONObject(j).isNull("version"))) {
                            categoryCoding.setVersion(categoryCodingJSON.getJSONObject(j).getString("version"));
                        }
                        if (!(categoryCodingJSON.getJSONObject(j).isNull("code"))) {
                            categoryCoding.setCode(categoryCodingJSON.getJSONObject(j).getString("code"));
                        }
                        if (!(categoryCodingJSON.getJSONObject(j).isNull("display"))) {
                            categoryCoding.setDisplay(categoryCodingJSON.getJSONObject(j).getString("display"));
                        }
                        if (!(categoryCodingJSON.getJSONObject(j).isNull("userSelected"))) {
                            categoryCoding.setUserSelected(categoryCodingJSON.getJSONObject(j).getBoolean("userSelected"));
                        }
                        codingList.add(categoryCoding);
                    }
                    theCategory.setCoding(codingList);
                }
                if (!(codeJSON.getJSONObject(i).isNull("text"))) {
                    theCategory.setText(codeJSON.getJSONObject(i).getString("text"));
                }
                codeList.add(theCategory);
            }
            practitionerRole.setCode(codeList);
        }

        //Set specialty
        if(!(practitionerRoleJSON.isNull("specialty"))) {
            JSONArray specialtyJSON = practitionerRoleJSON.getJSONArray("specialty");
            int noOfSpecialty = specialtyJSON.length();
            List<CodeableConcept> specialtyList = new ArrayList<CodeableConcept>();
            for(int i = 0; i < noOfSpecialty; i++) {
                CodeableConcept theCategory = new CodeableConcept();
                if (!(specialtyJSON.getJSONObject(i).isNull("coding"))) {
                    JSONArray categoryCodingJSON = specialtyJSON.getJSONObject(i).getJSONArray("coding");
                    int noOfCategoryCoding = categoryCodingJSON.length();
                    List<Coding> codingList = new ArrayList<Coding>();
                    for (int j = 0; j < noOfCategoryCoding; j++) {
                        Coding categoryCoding = new Coding();
                        if (!(categoryCodingJSON.getJSONObject(j).isNull("system"))) {
                            categoryCoding.setSystem(categoryCodingJSON.getJSONObject(j).getString("system"));
                        }
                        if (!(categoryCodingJSON.getJSONObject(j).isNull("version"))) {
                            categoryCoding.setVersion(categoryCodingJSON.getJSONObject(j).getString("version"));
                        }
                        if (!(categoryCodingJSON.getJSONObject(j).isNull("code"))) {
                            categoryCoding.setCode(categoryCodingJSON.getJSONObject(j).getString("code"));
                        }
                        if (!(categoryCodingJSON.getJSONObject(j).isNull("display"))) {
                            categoryCoding.setDisplay(categoryCodingJSON.getJSONObject(j).getString("display"));
                        }
                        if (!(categoryCodingJSON.getJSONObject(j).isNull("userSelected"))) {
                            categoryCoding.setUserSelected(categoryCodingJSON.getJSONObject(j).getBoolean("userSelected"));
                        }
                        codingList.add(categoryCoding);
                    }
                    theCategory.setCoding(codingList);
                }
                if (!(specialtyJSON.getJSONObject(i).isNull("text"))) {
                    theCategory.setText(specialtyJSON.getJSONObject(i).getString("text"));
                }
                specialtyList.add(theCategory);
            }
            practitionerRole.setSpecialty(specialtyList);
        }

        //Set Location
        if(!(practitionerRoleJSON.isNull("loction"))) {
            JSONArray locationJSON = practitionerRoleJSON.getJSONArray("location");
            int noOfLocation = locationJSON.length();
            List<Reference> locationList = new ArrayList<Reference>();
            for(int i = 0; i < noOfLocation; i++) {
                Reference ref = new Reference();
                ref.setReference(locationJSON.getJSONObject(i).getString("reference"));
                ref.setDisplay(locationJSON.getJSONObject(i).getString("display"));
                locationList.add(ref);
            }
            practitionerRole.setLocation(locationList);
        }

        //Set Telecom
        if(!(practitionerRoleJSON.isNull("telecom"))) {
            JSONArray telecomJSON = practitionerRoleJSON.getJSONArray("telecom");
            List<ContactPoint> telecomList = new ArrayList<ContactPoint>();
            int noOfTelecom = telecomJSON.length();
            List<Reference> locationList = new ArrayList<Reference>();
            for(int i = 0; i < noOfTelecom; i++) {
                ContactPoint phoneContact = new ContactPoint();
                if (!(telecomJSON.getJSONObject(i).isNull("system"))) {
                    phoneContact.setSystem(ContactPoint.ContactPointSystem.fromCode(telecomJSON.getJSONObject(i).getString("system")));
                }
                if (!(telecomJSON.getJSONObject(i).isNull("use"))) {
                    phoneContact.setUse(ContactPoint.ContactPointUse.fromCode(telecomJSON.getJSONObject(i).getString("use")));
                }
                if (!(telecomJSON.getJSONObject(i).isNull("value"))) {
                    phoneContact.setValue(telecomJSON.getJSONObject(i).getString("value"));
                }
                if (!(telecomJSON.getJSONObject(i).isNull("extension"))) {
                    List<Extension> extensions = new ArrayList<Extension>();
                    JSONArray extensionJSON = telecomJSON.getJSONObject(i).getJSONArray("extension");
                    for (int j = 0; j < extensionJSON.length(); j++) {
                        Extension extension = new Extension();
                        extension.setUrl(extensionJSON.getJSONObject(j).getString("url"));
                        extension.setValue(new BooleanType(extensionJSON.getJSONObject(j).getBoolean("valueBoolean")));
                        extensions.add(extension);
                    }
                    phoneContact.setExtension(extensions);
                }
                telecomList.add(phoneContact);
            }
            practitionerRole.setTelecom(telecomList);
        }

        return practitionerRole;

    }


}
