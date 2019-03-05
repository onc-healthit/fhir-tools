package org.sitenv.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ca.uhn.fhir.rest.annotation.*;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.support.AbstractApplicationContext;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Provenance;
import ca.uhn.fhir.model.dstu2.resource.Provenance.Agent;
import ca.uhn.fhir.model.dstu2.resource.Provenance.AgentRelatedAgent;
import ca.uhn.fhir.model.dstu2.resource.Provenance.Entity;
import ca.uhn.fhir.model.dstu2.valueset.ProvenanceEntityRoleEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

public class ProvenanceResourceProvider implements IResourceProvider{
	
	public static final String RESOURCE_TYPE = "Provenance";
    public static final String VERSION_ID = "2.0";
    AbstractApplicationContext context;

    public ProvenanceResourceProvider() {
        //context = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<Provenance> getResourceType() {
        return Provenance.class;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     *
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return Returns all the available Device records.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Device?_pretty=true&_format=json
     */
    @Search
    public List<Provenance> getAllProvenance(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        List<Provenance> provenances = new ArrayList<Provenance>();
        provenances.add(createProvenanceObject(1));
        return provenances;
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
     *  Ex: http://<server name>/<context>/fhir/Provenance/1?_format=json
     */
    @Read(version = true)
    public Provenance readOrVread(@IdParam IdDt theId) {
    	int id;
        try {
			if (theId.hasVersionIdPart()) {
				id = Integer.parseInt(theId.getValue().split("/")[1]);
			}else {
				id = theId.getIdPartAsLong().intValue();
			}
            Provenance provenance = createProvenanceObject(id);
            return provenance;
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException(theId);
        }
    }

	@History()
	public Provenance getProvenanceHistory(@IdParam IdDt theId) {
		int id;
		try {
			id = Integer.parseInt(theId.getValue().split("/")[1]);
			Provenance provenance = createProvenanceObject(id);
			return provenance;
		} catch (Exception e) {
			throw new ResourceNotFoundException(theId);
		}
	}

    /**
     * This method converts returns sample Provenance object
     */
    private Provenance createProvenanceObject(int id) {

    	Provenance provenance = new Provenance();

        //Set Version
    	provenance.setId(new IdDt(RESOURCE_TYPE, 1 + "", VERSION_ID));

    	//Set Target
    	List<ResourceReferenceDt> targetList=new ArrayList<>();
    	targetList.add(new ResourceReferenceDt("Procedure/"+id));
    	provenance.setTarget(targetList);
    	
    	//Set Period
    	PeriodDt period = new PeriodDt();
    	DateTimeDt startDate = new DateTimeDt("2006-10-01");
		period.setStart(startDate );
		provenance.setPeriod(period );
		
		//Set Recorded
		InstantDt recordedDate = new InstantDt("2006-10-01T08:39:24+10:00");
		provenance.setRecorded(recordedDate );
        
		//Set Reason
		List<CodeableConceptDt> reasonList= new ArrayList<>();
		reasonList.add(HapiUtils.setCodeableConceptDtValues("Referral", "3457005", "http://snomed.info/sct").setText("Accepting a referral"));
		provenance.setReason(reasonList);
		
		//Set Location
		provenance.setLocation(new ResourceReferenceDt("Location/1"));
		
		//Set Policy
		List<UriDt> policyList= new ArrayList<>();
		policyList.add(new UriDt("http://acme.com/fhir/Consent/25"));
		provenance.setPolicy(policyList);
		
		//Set Agent
		
		List<Agent> agentsList = new ArrayList<>();
		
			Agent agent = new Agent();	
			//Set Agent Role
			agent.setRole(new CodingDt("http://hl7.org/fhir/provenance-participant-role", "author"));
			//Set Agent Actor
			agent.setActor(new ResourceReferenceDt("Practitioner/1"));
			//Set Agent UserId
			agent.setUserId(new IdentifierDt("http://acme.com/fhir/users/sso", "hhd"));
			
			//Set Agent relatedAgents
			List<AgentRelatedAgent> relatedAgents = new ArrayList<>();
				AgentRelatedAgent relatedAgent = new AgentRelatedAgent();
				//Set RelatedAgent Type
				relatedAgent.setType(new CodeableConceptDt().setText("used"));
				//Set RelatedAgent Target
				relatedAgent.setTarget("#a1");
			relatedAgents.add(relatedAgent );
			agent.setRelatedAgent(relatedAgents);
		agentsList.add(agent );
		provenance.setAgent(agentsList );
		
		//Set Entity
		List<Entity> entityList = new ArrayList<>();
		Entity entity = new Entity();
		entity.setRole(ProvenanceEntityRoleEnum.SOURCE);
		entity.setType(new CodingDt("http://loinc.org", "57133-1").setDisplay("Referral note"));
		entity.setReference("DocumentReference/1");
		entityList.add(entity );
		provenance.setEntity(entityList);

        return provenance;
    }

}
