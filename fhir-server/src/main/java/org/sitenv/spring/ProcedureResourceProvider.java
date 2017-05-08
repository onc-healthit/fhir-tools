package org.sitenv.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.query.ProcedureSearchCriteria;
import org.sitenv.spring.service.ProcedureService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.resource.Procedure.Performer;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

public class ProcedureResourceProvider implements IResourceProvider{
	
	public static final String RESOURCE_TYPE = "Procedure";
	public static final String VERSION_ID = "2.0";
	AbstractApplicationContext context;
	ProcedureService service;

	public ProcedureResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		service = (ProcedureService) context.getBean("procedureResourceService");
	}

	/**
	 * The getResourceType method comes from IResourceProvider, and must be
	 * overridden to indicate what type of resource this provider supplies.
	 */
	@Override
	public Class<Procedure> getResourceType() {
		return Procedure.class;
	}

	/**
	 * The "@Search" annotation indicates that this method supports the search
	 * operation. You may have many different method annotated with this
	 * annotation, to support many different search criteria.
	 * 
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return Returns all the available Procedure records.
	 * 
	 *         Ex: http://<server
	 *         name>/<context>/fhir/Procedure?_pretty=true&_format=json
	 */
	@Search
	public List<Procedure> getAllProcedure(
			@IncludeParam(allow = "*") Set<Include> theIncludes,
			@Sort SortSpec theSort, @Count Integer theCount) {

		List<DafProcedure> dafProcedures = service.getAllProcedures();

		List<Procedure> procedures = new ArrayList<Procedure>();

		for (DafProcedure dafProcedure : dafProcedures) {
			procedures.add(createProcedureObject(dafProcedure));
		}

		return procedures;
	}
	
	/**
	 * This is the "read" operation. The "@Read" annotation indicates that this
	 * method supports the read and/or vread operation.
	 * <p>
	 * Read operations take a single parameter annotated with the
	 * {@link IdParam} paramater, and should return a single resource instance.
	 * </p>
	 *
	 * @param theId
	 *            The read operation takes one parameter, which must be of type
	 *            IdDt and must be annotated with the "@Read.IdParam"
	 *            annotation.
	 * @return Returns a resource matching this identifier, or null if none
	 *         exists.
	 * 
	 *         Ex: http://<server name>/<context>/fhir/Procedure/1?_format=json
	 */
	@Read()
	public Procedure getProcedureResourceById(@IdParam IdDt theId) {

		DafProcedure dafProcedure = service.getProcedureById(theId
				.getIdPartAsLong().intValue());

		Procedure procedure = createProcedureObject(dafProcedure);

		return procedure;
	}
	
	/**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
     * @param theRange
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of Procedures. This list may contain multiple matching resources, or it may also be empty.
     * 
     *  Ex: http://<server name>/<context>/fhir/Procedure?patient=1&date=lt2015-01-01&_format=json
     */
    @Search()
    public List<Procedure> searchByOptions(@RequiredParam(name = Procedure.SP_PATIENT) ReferenceParam thePatient,
                                             @OptionalParam(name = Procedure.SP_DATE) DateRangeParam theRange,
                                             @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
       
        ProcedureSearchCriteria procedureSearchCriteria = new ProcedureSearchCriteria();
        procedureSearchCriteria.setSubject(Integer.parseInt(patientId));
        if (theRange != null) {
        	procedureSearchCriteria.setRangedates(theRange);
        }
        List<DafProcedure> dafProcedureList = service.getProcedureBySearchCriteria(procedureSearchCriteria);

        List<Procedure> procedureList = new ArrayList<Procedure>();

        for (DafProcedure dafProcedure : dafProcedureList) {
        	if(dafProcedure.getSubject() != null){
        		procedureList.add(createProcedureObject(dafProcedure));
        	}
        }
        return procedureList;
    }

	/**
	 * This method converts DafProcedure object to Procedure object
	 */
	private Procedure createProcedureObject(DafProcedure dafProcedure) {

		Procedure procedure = new Procedure();

		// Set Version
		procedure.setId(new IdDt(RESOURCE_TYPE,
				dafProcedure.getProcedureId() + "", VERSION_ID));
		
		//set subject
		 ResourceReferenceDt patientResource = new ResourceReferenceDt();
	        String theId = "Patient/" + Integer.toString(dafProcedure.getSubject().getId());
	        patientResource.setReference(theId);
	        procedure.setSubject(patientResource);
	        
	        //set status
	        procedure.setStatus(ProcedureStatusEnum.valueOf(dafProcedure.getStatus()));
	        
	        //set code
	        Map<String, String> code = HapiUtils.convertToJsonMap(dafProcedure.getCode());
	        CodeableConceptDt theCode=new CodeableConceptDt();
	        CodingDt cc=new CodingDt();
	        cc.setSystem(code.get("system"));
	        cc.setCode(code.get("code"));
	        cc.setDisplay(code.get("display"));
			theCode.addCoding(cc);
			theCode.setText(code.get("text"));
			procedure.setCode(theCode);
			
			//set bodysite
			Map<String, String> bodysite = HapiUtils.convertToJsonMap(dafProcedure.getBodysite());
			List<CodeableConceptDt> theBodysite= new ArrayList<CodeableConceptDt>();
			CodeableConceptDt bodysiteCC= new CodeableConceptDt();
			CodingDt bsCoding=new CodingDt();
			bsCoding.setSystem(bodysite.get("system"));
			bsCoding.setCode(bodysite.get("code"));
			bsCoding.setDisplay(bodysite.get("display"));
			bodysiteCC.addCoding(bsCoding);
			bodysiteCC.setText(bodysite.get("text"));
			theBodysite.add(bodysiteCC);
			procedure.setBodySite(theBodysite);
			
			//set reason
			Map<String, String> reasonNP = HapiUtils.convertToJsonMap(dafProcedure.getReasonnotperformed());
			List<CodeableConceptDt> theReasonNP= new ArrayList<CodeableConceptDt>();
			CodeableConceptDt rnpCC= new CodeableConceptDt();
			/*CodingDt rnpCoding=new CodingDt();
			rnpCoding.setSystem(reasonNP.get("system"));
			rnpCoding.setCode(reasonNP.get("code"));
			rnpCoding.setDisplay(reasonNP.get("display"));
			bodysiteCC.addCoding(rnpCoding);*/
			rnpCC.setText(reasonNP.get("text"));
			theReasonNP.add(rnpCC);
			procedure.setReasonNotPerformed(theReasonNP);
			
			//set performer 
			Map<String, String> actor = HapiUtils.convertToJsonMap(dafProcedure.getPerformerActor());
			List<Performer> performers = new ArrayList<Procedure.Performer>();
			Performer performer=new Performer();
			ResourceReferenceDt theActor=new ResourceReferenceDt();
			theActor.setReference(actor.get("reference"));
			theActor.setDisplay(actor.get("display"));
			performer.setActor(theActor);
			performers.add(performer);
			procedure.setPerformer(performers );
			
			//set performed date
			DateTimeDt performed = new DateTimeDt();
			performed.setValue(dafProcedure.getPerformed());
			procedure.setPerformed(performed);
			
			//set notes
			List<AnnotationDt> theNotes=new ArrayList<AnnotationDt>();
			AnnotationDt note=new AnnotationDt();
			note.setText(dafProcedure.getNotes());
			theNotes.add(note);
			procedure.setNotes(theNotes);
		
		return procedure;
	}

}
