package org.sitenv.spring;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;

import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.List;


@WebServlet(urlPatterns = {"/fhir/*"}, displayName = "FHIR Server")
public class DAFServer extends RestfulServer {


    /**
     * Constructor
     */
    public DAFServer() {
        super(FhirContext.forR4()); // Support R4
    }

    /**
     * This method is called automatically when the
     * servlet is initializing.
     */
    @Override
    public void initialize() {

      /*
       * The servlet defines any number of resource providers, and
       * configures itself to use them by calling
       * setResourceProviders()
       */

        List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
      
        resourceProviders.add(new PatientResourceProvider());
        resourceProviders.add(new PractitionerResourceProvider());
        resourceProviders.add(new HealthcareServiceResourceProvider());
        resourceProviders.add(new CarePlanResourceProvider());
        resourceProviders.add(new FamilyMemberHistoryResourceProvider());
        resourceProviders.add(new SpecimenResourceProvider());
        resourceProviders.add(new MedicationAdministrationResourceProvider());
        resourceProviders.add(new MedicationDispenseResourceProvider());
        resourceProviders.add(new MedicationStatementResourceProvider());
        resourceProviders.add(new GoalResourceProvider());
        resourceProviders.add(new CareTeamResourceProvider());
        resourceProviders.add(new DeviceResourceProvider());
        resourceProviders.add(new OrganizationResourceProvider());
        resourceProviders.add(new ServiceRequestResourceProvider());
        resourceProviders.add(new DiagnosticReportResourceProvider());
        resourceProviders.add(new ImagingStudyResourceProvider());
        resourceProviders.add(new RiskAssessmentResourceProvider());
        resourceProviders.add(new MedicationRequestResourceProvider());
        resourceProviders.add(new ConditionResourceProvider());
        resourceProviders.add(new LocationResourceProvider());
        resourceProviders.add(new MedicationResourceProvider());
        resourceProviders.add(new ObservationResourceProvider());
        resourceProviders.add(new ImmunizationResourceProvider());
        resourceProviders.add(new ProcedureResourceProvider());
        resourceProviders.add(new AllergyIntoleranceResourceProvider());
        resourceProviders.add(new DocumentReferenceResourceProvider());
        resourceProviders.add(new EncounterResourceProvider());
        resourceProviders.add(new ProvenanceResourceProvider());
        resourceProviders.add(new PractitionerRoleResourceProvider());

       // resourceProviders.add(new GroupResourceProvider());
        setResourceProviders(resourceProviders);

        setServerConformanceProvider(new CapabilityStatementResourceProvider());

		/*
         * Use a narrative generator. This is a completely optional step,
		 * but can be useful as it causes HAPI to generate narratives for
		 * resources which don't otherwise have one.
		 */
        /** -- revisit Devil */
       /* INarrativeGenerator narrativeGen = new DefaultThymeleafNarrativeGenerator();
        getFhirContext().setNarrativeGenerator(narrativeGen);*/

		/*
         * Tells HAPI to use content types which are not technically FHIR compliant when a browser is detected as the
		 * requesting client. This prevents browsers from trying to download resource responses instead of displaying them
		 * inline which can be handy for troubleshooting.
		 */
        //setUseBrowserFriendlyContentTypes(true);
        setDefaultPrettyPrint(true);
        setDefaultResponseEncoding(EncodingEnum.JSON);

    }

}
