package org.sitenv.spring;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.narrative.INarrativeGenerator;
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
        super(FhirContext.forDstu2()); // Support DSTU2
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
        resourceProviders.add(new DocumentReferenceResourceProvider());
        resourceProviders.add(new PatientJsonResourceProvider());
        resourceProviders.add(new MedicationResourceProvider());
        resourceProviders.add(new MedicationOrderResourceProvider());
        resourceProviders.add(new MedicationStatementResourceProvider());
        resourceProviders.add(new MedicationDispenseResourceProvider());
        resourceProviders.add(new MedicationAdministrationResourceProvider());
        resourceProviders.add(new ConditionResourceProvider());
        resourceProviders.add(new AllergyIntoleranceResourceProvider());
        resourceProviders.add(new ObservationResourceProvider());
        resourceProviders.add(new DiagnosticReportResourceProvider());
        resourceProviders.add(new CarePlanResourceProvider());
        resourceProviders.add(new ImmunizationResourceProvider());
        resourceProviders.add(new GoalsResourceProvider());
        resourceProviders.add(new DeviceResourceProvider());
        resourceProviders.add(new OrganizationResourceProvider());
        resourceProviders.add(new LocationResourceProvider());
        resourceProviders.add(new PractitionerResourceProvider());
        resourceProviders.add(new ProcedureResourceProvider());
        resourceProviders.add(new GroupResourceProvider());
        resourceProviders.add(new EncounterResourceProvider());
        resourceProviders.add(new ClaimResourceProvider());

        setResourceProviders(resourceProviders);
        
        setServerConformanceProvider(new ConformanceResourceProvider());

		/*
         * Use a narrative generator. This is a completely optional step,
		 * but can be useful as it causes HAPI to generate narratives for
		 * resources which don't otherwise have one.
		 */
        INarrativeGenerator narrativeGen = new DefaultThymeleafNarrativeGenerator();
        getFhirContext().setNarrativeGenerator(narrativeGen);

		/*
		 * Tells HAPI to use content types which are not technically FHIR compliant when a browser is detected as the
		 * requesting client. This prevents browsers from trying to download resource responses instead of displaying them
		 * inline which can be handy for troubleshooting.
		 */
        setUseBrowserFriendlyContentTypes(true);

    }

}
