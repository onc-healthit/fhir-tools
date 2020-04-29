package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.*;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Procedure.ProcedureFocalDeviceComponent;
import org.hl7.fhir.r4.model.Procedure.ProcedurePerformerComponent;
import org.hl7.fhir.r4.model.Procedure.ProcedureStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.service.ProcedureService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ProcedureResourceProvider implements IResourceProvider {
	
	public static final String RESOURCE_TYPE = "Procedure";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    ProcedureService service;

    public ProcedureResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (ProcedureService) context.getBean("procedureService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Procedure.class;
	}
	
 	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Procedure/1/_history/4
	 * @param theId : Id of the Procedure
	 * @return : Object of Procedure information
	 */
	@Read(version=true)
    public Procedure readOrVread(@IdParam IdType theId) {
		String id;
		DafProcedure dafProcedure;
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
		   dafProcedure = service.getProcedureByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       dafProcedure = service.getProcedureById(id);
		}
		return createProcedureObject(dafProcedure);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=Procedure.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Procedure/1/_history
	 * @param theId : ID of the Procedure
	 * @return : List of Procedures
	 */
	@History()
    public List<Procedure> getProcedureHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafProcedure> dafProcedureList = service.getProcedureHistoryById(id);
        
        List<Procedure> procedureList = new ArrayList<Procedure>();
        for (DafProcedure dafProcedure : dafProcedureList) {
        	procedureList.add(createProcedureObject(dafProcedure));
        }
        
        return procedureList;
	}
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theDate
	 * @param theCode
	 * @param thePerformer
	 * @param theSubject
	 * @param theInstantiatesCanonical
	 * @param thePartOf
	 * @param theEncounter
	 * @param theReasonCode
	 * @param theBasedOn
	 * @param thePatient
	 * @param theReasonReference
	 * @param theLocation
	 * @param theInstantiatesUri
	 * @param theCategory
	 * @param theStatus
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
            @OptionalParam(name = Procedure.SP_RES_ID)
            StringAndListParam theId,

            @Description(shortDefinition = "A Procedure identifier")
            @OptionalParam(name = Procedure.SP_IDENTIFIER)
            TokenAndListParam theIdentifier,
            
            @Description(shortDefinition = "When the procedure was performed")
            @OptionalParam(name = Procedure.SP_DATE)
            DateRangeParam theDate,
            
            @Description(shortDefinition = "A code to identify a  procedure")
            @OptionalParam(name = Procedure.SP_CODE)
            TokenAndListParam theCode,
            
            @Description(shortDefinition = "The reference to the practitioner")
            @OptionalParam(name = Procedure.SP_PERFORMER)
            ReferenceAndListParam thePerformer,
            
            @Description(shortDefinition = "A code to identify a  procedure")
            @OptionalParam(name = Procedure.SP_SUBJECT)
            ReferenceAndListParam theSubject,
            
            @Description(shortDefinition = "Instantiates FHIR protocol or definition")
            @OptionalParam(name = Procedure.SP_INSTANTIATES_CANONICAL)
            ReferenceAndListParam theInstantiatesCanonical,
            
            @Description(shortDefinition = "Part of referenced event")
            @OptionalParam(name = Procedure.SP_PART_OF)
            ReferenceAndListParam thePartOf,
            
            @Description(shortDefinition = "Encounter created as part of")
            @OptionalParam(name = Procedure.SP_ENCOUNTER)
            ReferenceAndListParam theEncounter,
            
            @Description(shortDefinition = "Coded reason procedure performed")
            @OptionalParam(name = Procedure.SP_REASON_CODE)
            TokenAndListParam theReasonCode,
           
            @Description(shortDefinition = "A request for this procedure")
            @OptionalParam(name = Procedure.SP_BASED_ON)
            ReferenceAndListParam theBasedOn,
            
            @Description(shortDefinition = "Search by subject - a patient")
            @OptionalParam(name = Procedure.SP_PATIENT)
            ReferenceAndListParam thePatient,
            
            @Description(shortDefinition="The justification that the procedure was performed")
            @OptionalParam(name=Procedure.SP_REASON_REFERENCE)
            ReferenceAndListParam theReasonReference,
            
            @Description(shortDefinition="Where the procedure happened")
            @OptionalParam(name=Procedure.SP_LOCATION)
            ReferenceAndListParam theLocation,
            
            @Description(shortDefinition="Instantiates external protocol or definition")
            @OptionalParam(name=Procedure.SP_INSTANTIATES_URI)
            UriAndListParam theInstantiatesUri,
            
            @Description(shortDefinition="Classification of the procedure")
            @OptionalParam(name=Procedure.SP_CATEGORY)
            TokenAndListParam theCategory,
            
            @Description(shortDefinition="preparation | in-progress | not-done | suspended | aborted | completed | entered-in-error | unknown")
            @OptionalParam(name=Procedure.SP_STATUS)
            TokenAndListParam theStatus,

            @IncludeParam(allow = {"*"})
            Set<Include> theIncludes,

			@IncludeParam(reverse=true, allow= {"*"})
			Set<Include> theRevIncludes,

			@Sort
            SortSpec theSort,

            @Count
            Integer theCount) {

            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(Procedure.SP_RES_ID, theId);
            paramMap.add(Procedure.SP_IDENTIFIER, theIdentifier);
            paramMap.add(Procedure.SP_DATE, theDate);
            paramMap.add(Procedure.SP_CODE, theCode);
            paramMap.add(Procedure.SP_PERFORMER, thePerformer);
            paramMap.add(Procedure.SP_SUBJECT, theSubject);
            paramMap.add(Procedure.SP_INSTANTIATES_CANONICAL, theInstantiatesCanonical);
            paramMap.add(Procedure.SP_PART_OF, thePartOf);
            paramMap.add(Procedure.SP_ENCOUNTER, theEncounter);
            paramMap.add(Procedure.SP_REASON_CODE, theReasonCode);
            paramMap.add(Procedure.SP_BASED_ON, theBasedOn);
            paramMap.add(Procedure.SP_PATIENT, thePatient);
            paramMap.add(Procedure.SP_REASON_REFERENCE, theReasonReference);
            paramMap.add(Procedure.SP_LOCATION, theLocation);
            paramMap.add(Procedure.SP_INSTANTIATES_URI, theInstantiatesUri);
            paramMap.add(Procedure.SP_CATEGORY, theCategory);
            paramMap.add(Procedure.SP_STATUS, theStatus);
            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<DafProcedure> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> procedureList = new ArrayList<IBaseResource>();
					List<String> ids = new ArrayList<String>();
					for(DafProcedure dafProcedure : results){
						Procedure procedure = createProcedureObject(dafProcedure);
						procedureList.add(procedure);
						ids.add(((IdType)procedure.getIdElement()).getResourceType()+"/"+((IdType)procedure.getIdElement()).getIdPart());
					}
					if(theRevIncludes.size() >0 ){
						procedureList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
					}
                    return procedureList;
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
     * This method converts DafDocumentReference object to DocumentReference object
     * @param dafProcedure : DafDocumentReference Procedure object
     * @return : DocumentReference Procedure object
     */
    private Procedure createProcedureObject(DafProcedure dafProcedure) {
        Procedure procedure = new Procedure();
        JSONObject procedureJSON = new JSONObject(dafProcedure.getData());

        // Set version
        if(!(procedureJSON.isNull("meta"))) {
        	if(!(procedureJSON.getJSONObject("meta").isNull("versionId"))) {
        		procedure.setId(new IdType(RESOURCE_TYPE, procedureJSON.getString("id") + "", procedureJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				procedure.setId(new IdType(RESOURCE_TYPE, procedureJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
        	procedure.setId(new IdType(RESOURCE_TYPE, procedureJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(procedureJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = procedureJSON.getJSONArray("identifier");
        	int noOfIdentifiers = identifierJSON.length();
        	List<Identifier> identifiers = new ArrayList<Identifier>();
        	for(int i = 0; i < noOfIdentifiers; i++) {
            	Identifier theIdentifier = new Identifier();
        		if(!(identifierJSON.getJSONObject(i).isNull("use"))) {
                	theIdentifier.setUse(Identifier.IdentifierUse.fromCode(identifierJSON.getJSONObject(i).getString("use")));	
            	}
        		
        		if(!(identifierJSON.getJSONObject(i).isNull("type"))) {
        			CodeableConcept theType = new CodeableConcept();
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("type").isNull("coding"))) {
            			JSONArray typeCodingJSON = identifierJSON.getJSONObject(i).getJSONObject("type").getJSONArray("coding");
            			int noOfCodings = typeCodingJSON.length();
            			List<Coding> theCodingList = new ArrayList<Coding>();
            			for(int j = 0; j < noOfCodings; j++) {
            				Coding theCoding = new Coding();
                			if(!(typeCodingJSON.getJSONObject(j).isNull("system"))) {
                				theCoding.setSystem(typeCodingJSON.getJSONObject(j).getString("system"));
                			}
                			if(!(typeCodingJSON.getJSONObject(j).isNull("code"))) {
                				theCoding.setCode(typeCodingJSON.getJSONObject(j).getString("code"));
                			}
                			if(!(typeCodingJSON.getJSONObject(j).isNull("display"))) {
                				theCoding.setDisplay(typeCodingJSON.getJSONObject(j).getString("display"));
                			}
                			theCodingList.add(theCoding);
            			}
                    	
            			theType.setCoding(theCodingList);
            		}
                	theIdentifier.setType(theType);
            	}
        		
        		if(!(identifierJSON.getJSONObject(i).isNull("system"))) {
                	theIdentifier.setSystem(identifierJSON.getJSONObject(i).getString("system"));
            	}
            	
            	if(!(identifierJSON.getJSONObject(i).isNull("value"))) {
                	theIdentifier.setValue(identifierJSON.getJSONObject(i).getString("value"));
            	}
            	
            	if(!(identifierJSON.getJSONObject(i).isNull("period"))) {
            		Period thePeriod = new Period();
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("period").isNull("start"))) {
                        Date startDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject(i).getJSONObject("period").getString("start"));
                        thePeriod.setStart(startDate);
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("period").isNull("end"))) {
                        Date endDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject(i).getJSONObject("period").getString("end"));
                        thePeriod.setStart(endDate);
            		}
                    theIdentifier.setPeriod(thePeriod);
            	}

            	if(!(identifierJSON.getJSONObject(i).isNull("assigner"))) {
        			Reference theAssigner = new Reference(); 
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("display"))) {
                        theAssigner.setDisplay(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("display"));
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("reference"))) {
                        theAssigner.setReference(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("reference"));
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("type"))) {
                        theAssigner.setType(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("type"));
            		}
                    theIdentifier.setAssigner(theAssigner);
            	}
         
            	identifiers.add(theIdentifier);
        	}
        	procedure.setIdentifier(identifiers);
        }
        
        //set instantiatesCanonical
        if (!procedureJSON.isNull("instantiatesCanonical")) {
			JSONArray  instantiatesCanonicalJSON = procedureJSON.getJSONArray("instantiatesCanonical");
			List<CanonicalType> instantiatesCanonicalList =new ArrayList<CanonicalType>();
			int noOfCanonocals = instantiatesCanonicalJSON.length();
			
			for(int i = 0; i < noOfCanonocals; i++) {
				CanonicalType instantiatesCanonical = new CanonicalType();
				instantiatesCanonical.setValue(instantiatesCanonicalJSON.getString(i));
				instantiatesCanonicalList.add(instantiatesCanonical);
			}
			
			procedure.setInstantiatesCanonical(instantiatesCanonicalList);
		}
        
        //Set instantiatesUri
        if(!(procedureJSON.isNull("instantiatesUri"))) {
        	List<UriType> uriList = new ArrayList<UriType>();
        	JSONArray uriJSON = procedureJSON.getJSONArray("instantiatesUri");
        	int noOfUris = uriJSON.length();
    		for(int i = 0; i < noOfUris; i++) {
    			String uriName = uriJSON.getString(i) ;
    			UriType theUri = new UriType();
    			theUri.setValue(uriName);
			   	uriList.add(theUri);
    		}
    		procedure.setInstantiatesUri(uriList);
        }

        //set basedOn
        if (!procedureJSON.isNull("basedOn")) {
			JSONArray basedOnJSON = procedureJSON.getJSONArray("basedOn");
			List<Reference> basedOnList = new ArrayList<Reference>();
			
			for (int j = 0; j < basedOnJSON.length(); j++) {
				Reference theBasedOn = new Reference();
				
				if (!basedOnJSON.getJSONObject(j).isNull("reference")) {
					theBasedOn.setReference(basedOnJSON.getJSONObject(j).getString("reference"));
				}
				if (!basedOnJSON.getJSONObject(j).isNull("display")) {
					theBasedOn.setDisplay(basedOnJSON.getJSONObject(j).getString("display"));
				}
				basedOnList.add(theBasedOn);
			}
			procedure.setBasedOn(basedOnList);
		}
        
        //set partOf
        if (!procedureJSON.isNull("partOf")) {
			JSONArray partOfJSON = procedureJSON.getJSONArray("partOf");
			List<Reference> partOfList = new ArrayList<Reference>();
			
			for (int j = 0; j < partOfJSON.length(); j++) {
				Reference thePartOf = new Reference();
				
				if (!partOfJSON.getJSONObject(j).isNull("reference")) {
					thePartOf.setReference(partOfJSON.getJSONObject(j).getString("reference"));
				}
				if (!partOfJSON.getJSONObject(j).isNull("display")) {
					thePartOf.setDisplay(partOfJSON.getJSONObject(j).getString("display"));
				}
				partOfList.add(thePartOf);
			}
			procedure.setPartOf(partOfList);
		}
        
        //set status
        if (!procedureJSON.isNull("status")) {
			procedure.setStatus(ProcedureStatus.fromCode(procedureJSON.getString("status")));
        }
        
        //set category
        if (!procedureJSON.isNull("category")) {
			JSONObject categoryJSON = procedureJSON.getJSONObject("category");
			CodeableConcept theCategory = new CodeableConcept();
			
			if (!categoryJSON.isNull("coding")) {
				JSONArray codingJSON = categoryJSON.getJSONArray("coding");
				List<Coding> codingList = new ArrayList<Coding>();
				int noOfCodings = codingJSON.length();
				
				for (int j = 0; j < noOfCodings; j++) {
					Coding theCoding = new Coding();
					
					if (!codingJSON.getJSONObject(j).isNull("system")) {
						theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
					}
					if (!codingJSON.getJSONObject(j).isNull("code")) {
						theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
					}
					if (!codingJSON.getJSONObject(j).isNull("display")) {
						theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
					}
					codingList.add(theCoding);
				}
				theCategory.setCoding(codingList);
			}
			if (!categoryJSON.isNull("text")) {
				theCategory.setText(categoryJSON.getString("text"));
			}
			procedure.setCategory(theCategory);
        }
        
        //set code
        if (!procedureJSON.isNull("code")) {
			JSONObject codeJSON = procedureJSON.getJSONObject("code");
			CodeableConcept theCode = new CodeableConcept();
			
			if (!codeJSON.isNull("coding")) {
				JSONArray codingJSON = codeJSON.getJSONArray("coding");
				List<Coding> codingList = new ArrayList<Coding>();
				int noOfCodings = codingJSON.length();
				
				for (int j = 0; j < noOfCodings; j++) {
					Coding theCoding = new Coding();
					
					if (!codingJSON.getJSONObject(j).isNull("system")) {
						theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
					}
					if (!codingJSON.getJSONObject(j).isNull("code")) {
						theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
					}
					if (!codingJSON.getJSONObject(j).isNull("display")) {
						theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
					}
					codingList.add(theCoding);
				}
				theCode.setCoding(codingList);
			}
			if (!codeJSON.isNull("text")) {
				theCode.setText(codeJSON.getString("text"));
			}
			procedure.setCode(theCode);
		}
        
        //set subject
        if (!procedureJSON.isNull("subject")) {
        	JSONObject subjectJSON = procedureJSON.getJSONObject("subject");
        	Reference theSubject = new Reference();
        	
        	if (!subjectJSON.isNull("reference")) {
				theSubject.setReference(subjectJSON.getString("reference"));
			}
        	if (!subjectJSON.isNull("display")) {
				theSubject.setDisplay(subjectJSON.getString("display"));
			}
			procedure.setSubject(theSubject);
		}
        
        //set performedPeriod
        if (!procedureJSON.isNull("performedPeriod")) {
			JSONObject performedPeriodJSON = procedureJSON.getJSONObject("performedPeriod");
			Period performedPeriod = new Period();
			if (!performedPeriodJSON.isNull("start")) {
				Date startDate = CommonUtil.convertStringToDate(performedPeriodJSON.getString("start"));
				performedPeriod.setStart(startDate);
			}
			if (!performedPeriodJSON.isNull("end")) {
				Date endDate = CommonUtil.convertStringToDate(performedPeriodJSON.getString("end"));
				performedPeriod.setEnd(endDate);
			}
			procedure.setPerformed(performedPeriod);
		}
        
        //set performedDateTime
        if (!procedureJSON.isNull("performedDateTime")) {
        	DateTimeType thePerformedDateTime= new DateTimeType();
        	Date performedDateTime = CommonUtil.convertStringToDate(procedureJSON.getString("performedDateTime"));
        	thePerformedDateTime.setValue(performedDateTime);
        	procedure.setPerformed(thePerformedDateTime);
		}
        
        //set encounter
        if (!procedureJSON.isNull("encounter")) {
        	JSONObject encounterJSON = procedureJSON.getJSONObject("encounter");
        	Reference theEncounter = new Reference();
        	
        	if (!encounterJSON.isNull("reference")) {
        		theEncounter.setReference(encounterJSON.getString("reference"));
			}
        	if (!encounterJSON.isNull("type")) {
        		theEncounter.setType(encounterJSON.getString("type"));
			}
        	if (!encounterJSON.isNull("display")) {
        		theEncounter.setDisplay(encounterJSON.getString("display"));
			}
			procedure.setEncounter(theEncounter);
		}
        
        //set performer
        if (!procedureJSON.isNull("performer")) {
			JSONArray performerJSON = procedureJSON.getJSONArray("performer");
			List<ProcedurePerformerComponent> performerList = new ArrayList<Procedure.ProcedurePerformerComponent>();
			int noOfPerformers = performerJSON.length();
			
			for (int i = 0; i < noOfPerformers; i++) {
				ProcedurePerformerComponent thePerformer = new ProcedurePerformerComponent();
				
				if (!performerJSON.getJSONObject(i).isNull("function")) {
					JSONObject functionJSON = performerJSON.getJSONObject(i).getJSONObject("function");
					CodeableConcept theFunction = new CodeableConcept();
					
					if (!functionJSON.isNull("coding")) {
						JSONArray codingJSON = functionJSON.getJSONArray("coding");
						List<Coding> codingList = new ArrayList<Coding>();
						int noOfCodings = codingJSON.length();
						
						for (int j = 0; j < noOfCodings; j++) {
							Coding theCoding = new Coding();
							
							if (!codingJSON.getJSONObject(j).isNull("system")) {
								theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
							}
							if (!codingJSON.getJSONObject(j).isNull("code")) {
								theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
							}
							if (!codingJSON.getJSONObject(j).isNull("display")) {
								theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
							}
							codingList.add(theCoding);
						}
						theFunction.setCoding(codingList);
					}
					if (!functionJSON.isNull("text")) {
						theFunction.setText(functionJSON.getString("text"));
					}
					thePerformer.setFunction(theFunction);
				}
				
				if (!performerJSON.getJSONObject(i).isNull("actor")) {
					JSONObject actorJSON = performerJSON.getJSONObject(i).getJSONObject("actor");
					Reference theActor = new Reference();
					
					if (!actorJSON.isNull("reference")) {
						theActor.setReference(actorJSON.getString("reference"));
					}
					if (!actorJSON.isNull("display")) {
						theActor.setDisplay(actorJSON.getString("display"));
					}
					thePerformer.setActor(theActor);
				}
				if (!performerJSON.getJSONObject(i).isNull("onBehalfOf")) {
					JSONObject onBehalfOfJSON = performerJSON.getJSONObject(i).getJSONObject("onBehalfOf");
					Reference theOnBehalfOf = new Reference();
					
					if (!onBehalfOfJSON.isNull("reference")) {
						theOnBehalfOf.setReference(onBehalfOfJSON.getString("reference"));
					}
					if (!onBehalfOfJSON.isNull("display")) {
						theOnBehalfOf.setDisplay(onBehalfOfJSON.getString("display"));
					}
					thePerformer.setOnBehalfOf(theOnBehalfOf);
				}
				performerList.add(thePerformer);
			}
			procedure.setPerformer(performerList);
		}
        
        //set location
        if (!procedureJSON.isNull("location")) {
			JSONObject locationJSON = procedureJSON.getJSONObject("location");
			Reference theLocation = new Reference();
			
			if (!locationJSON.isNull("reference")) {
				theLocation.setReference(locationJSON.getString("reference"));
			}
			if (!locationJSON.isNull("display")) {
				theLocation.setDisplay(locationJSON.getString("display"));
			}
			procedure.setLocation(theLocation);
		}
        
        //set resonCode
        if (!procedureJSON.isNull("reasonCode")) {
			JSONArray reasonCodeJSON = procedureJSON.getJSONArray("reasonCode");
			List<CodeableConcept> reasonCodeList = new ArrayList<CodeableConcept>();
			int noOfReasonCodes = reasonCodeJSON.length();
			
			for (int i = 0; i < noOfReasonCodes; i++) {
				CodeableConcept theReasonCode = new CodeableConcept();
				
				if (!reasonCodeJSON.getJSONObject(i).isNull("coding")) {
					 JSONArray codingJSON = reasonCodeJSON.getJSONObject(i).getJSONArray("coding");
					 List<Coding> codingList = new ArrayList<Coding>();
					 int noOfCodings = codingJSON.length();
					 for (int k = 0; k < noOfCodings; k++) {
						Coding theCoding = new Coding();
						
						if (!codingJSON.getJSONObject(k).isNull("system")) {
							theCoding.setSystem(codingJSON.getJSONObject(k).getString("system"));
						}
						if (!codingJSON.getJSONObject(k).isNull("code")) {
							theCoding.setCode(codingJSON.getJSONObject(k).getString("code"));
						}
						if (!codingJSON.getJSONObject(k).isNull("display")) {
							theCoding.setDisplay(codingJSON.getJSONObject(k).getString("display"));
						}
						codingList.add(theCoding);
					}
					 theReasonCode.setCoding(codingList);
				}
				if (!reasonCodeJSON.getJSONObject(i).isNull("text")) {
					theReasonCode.setText(reasonCodeJSON.getJSONObject(i).getString("text"));
				}
				reasonCodeList.add(theReasonCode);
			}
			procedure.setReasonCode(reasonCodeList);
		}
        
      //set reasonReference
        if (!procedureJSON.isNull("reasonReference")) {
			JSONArray reasonReferenceJSON = procedureJSON.getJSONArray("reasonReference");
			List<Reference> reasonReferenceList = new ArrayList<Reference>();
			
			for (int j = 0; j < reasonReferenceJSON.length(); j++) {
				Reference theReasonReference = new Reference();
				
				if (!reasonReferenceJSON.getJSONObject(j).isNull("reference")) {
					theReasonReference.setReference(reasonReferenceJSON.getJSONObject(j).getString("reference"));
				}
				if (!reasonReferenceJSON.getJSONObject(j).isNull(" type")) {
					theReasonReference.setType(reasonReferenceJSON.getJSONObject(j).getString(" type"));
				}
				if (!reasonReferenceJSON.getJSONObject(j).isNull("display")) {
					theReasonReference.setDisplay(reasonReferenceJSON.getJSONObject(j).getString("display"));
				}
				reasonReferenceList.add(theReasonReference);
			}
			procedure.setReasonReference(reasonReferenceList);
		}
        
        //set bodySite
        if (!procedureJSON.isNull("bodySite")) {
			JSONArray bodySiteJSON = procedureJSON.getJSONArray("bodySite");
			List<CodeableConcept> bodySiteList = new ArrayList<CodeableConcept>();
			int noOfBodySites= bodySiteJSON.length();
			
			for (int j = 0; j < noOfBodySites; j++) {
				CodeableConcept theBodySite = new CodeableConcept();
				
				if (!bodySiteJSON.getJSONObject(j).isNull("coding")) {
					 JSONArray codingJSON = bodySiteJSON.getJSONObject(j).getJSONArray("coding");
					 List<Coding> codingList = new ArrayList<Coding>();
					 int noOfCodings = codingJSON.length();
					 for (int k = 0; k < noOfCodings; k++) {
						Coding theCoding = new Coding();
						
						if (!codingJSON.getJSONObject(k).isNull("system")) {
							theCoding.setSystem(codingJSON.getJSONObject(k).getString("system"));
						}
						if (!codingJSON.getJSONObject(k).isNull("code")) {
							theCoding.setCode(codingJSON.getJSONObject(k).getString("code"));
						}
						if (!codingJSON.getJSONObject(k).isNull("display")) {
							theCoding.setDisplay(codingJSON.getJSONObject(k).getString("display"));
						}
						codingList.add(theCoding);
					}
					 theBodySite.setCoding(codingList);
				}
				if (!bodySiteJSON.getJSONObject(j).isNull("text")) {
					theBodySite.setText(bodySiteJSON.getJSONObject(j).getString("text"));
				}
				bodySiteList.add(theBodySite);
			}
			procedure.setBodySite(bodySiteList);
		}
		
        //set outcome
        if (!procedureJSON.isNull("outcome")) {
		JSONObject outcomeJSON = procedureJSON.getJSONObject("outcome");
		CodeableConcept theOutcome = new CodeableConcept();
		
		if (!outcomeJSON.isNull("coding")) {
			JSONArray codingJSON = outcomeJSON.getJSONArray("coding");
			List<Coding> codingList = new ArrayList<Coding>();
			int noOfCodings = codingJSON.length();
			
			for (int j = 0; j < noOfCodings; j++) {
				Coding theCoding = new Coding();
				
				if (!codingJSON.getJSONObject(j).isNull("system")) {
					theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
				}
				if (!codingJSON.getJSONObject(j).isNull("code")) {
					theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
				}
				if (!codingJSON.getJSONObject(j).isNull("display")) {
					theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
				}
				codingList.add(theCoding);
			}
			theOutcome.setCoding(codingList);
		}
		if (!outcomeJSON.isNull("text")) {
			theOutcome.setText(outcomeJSON.getString("text"));
		}
		procedure.setOutcome(theOutcome);
	}
        
        //set complication
        if (!procedureJSON.isNull("complication")) {
			JSONArray complicationJSON = procedureJSON.getJSONArray("complication");
			List<CodeableConcept> complicationList = new ArrayList<CodeableConcept>();
			int noOfComplications= complicationJSON.length();
			
			for (int j = 0; j < noOfComplications; j++) {
				CodeableConcept theComplication = new CodeableConcept();
				
				if (!complicationJSON.getJSONObject(j).isNull("coding")) {
					 JSONArray codingJSON = complicationJSON.getJSONObject(j).getJSONArray("coding");
					 List<Coding> codingList = new ArrayList<Coding>();
					 int noOfCodings = codingJSON.length();
					 for (int k = 0; k < noOfCodings; k++) {
						Coding theCoding = new Coding();
						
						if (!codingJSON.getJSONObject(k).isNull("system")) {
							theCoding.setSystem(codingJSON.getJSONObject(k).getString("system"));
						}
						if (!codingJSON.getJSONObject(k).isNull("code")) {
							theCoding.setCode(codingJSON.getJSONObject(k).getString("code"));
						}
						if (!codingJSON.getJSONObject(k).isNull("display")) {
							theCoding.setDisplay(codingJSON.getJSONObject(k).getString("display"));
						}
						codingList.add(theCoding);
					}
					 theComplication.setCoding(codingList);
				}
				if (!complicationJSON.getJSONObject(j).isNull("text")) {
					theComplication.setText(complicationJSON.getJSONObject(j).getString("text"));
				}
				complicationList.add(theComplication);
			}
			procedure.setComplication(complicationList);
		}
        
        //set followUp
        if (!procedureJSON.isNull("followUp")) {
			JSONArray followUpJSON = procedureJSON.getJSONArray("followUp");
			List<CodeableConcept> followUpList = new ArrayList<CodeableConcept>();
			int noOffollowUps= followUpJSON.length();
			
			for (int j = 0; j < noOffollowUps; j++) {
				CodeableConcept theFollowUp = new CodeableConcept();
				
				if (!followUpJSON.getJSONObject(j).isNull("coding")) {
					 JSONArray codingJSON = followUpJSON.getJSONObject(j).getJSONArray("coding");
					 List<Coding> codingList = new ArrayList<Coding>();
					 int noOfCodings = codingJSON.length();
					 for (int k = 0; k < noOfCodings; k++) {
						Coding theCoding = new Coding();
						
						if (!codingJSON.getJSONObject(k).isNull("system")) {
							theCoding.setSystem(codingJSON.getJSONObject(k).getString("system"));
						}
						if (!codingJSON.getJSONObject(k).isNull("code")) {
							theCoding.setCode(codingJSON.getJSONObject(k).getString("code"));
						}
						if (!codingJSON.getJSONObject(k).isNull("display")) {
							theCoding.setDisplay(codingJSON.getJSONObject(k).getString("display"));
						}
						codingList.add(theCoding);
					}
					 theFollowUp.setCoding(codingList);
				}
				if (!followUpJSON.getJSONObject(j).isNull("text")) {
					theFollowUp.setText(followUpJSON.getJSONObject(j).getString("text"));
				}
				followUpList.add(theFollowUp);
			}
			procedure.setFollowUp(followUpList);
		}
		
        //set note
        if (!procedureJSON.isNull("note")) {
        	JSONArray noteJSON = procedureJSON.getJSONArray("note");
			List<Annotation> noteList = new ArrayList<Annotation>();
			int length = noteJSON.length();
			for (int i = 0; i < length; i++) {
				Annotation theNote = new Annotation();
				if (!noteJSON.getJSONObject(i).isNull("text")) {
					theNote.setText(noteJSON.getJSONObject(i).getString("text"));
				}
				noteList.add(theNote);
			}
			procedure.setNote(noteList);
		}
        
      //set report
        if (!procedureJSON.isNull("report")) {
			JSONArray reportJSON = procedureJSON.getJSONArray("report");
			List<Reference> reportList = new ArrayList<Reference>();
			
			for (int j = 0; j < reportJSON.length(); j++) {
				Reference theReasonReference = new Reference();
				
				if (!reportJSON.getJSONObject(j).isNull("reference")) {
					theReasonReference.setReference(reportJSON.getJSONObject(j).getString("reference"));
				}
				if (!reportJSON.getJSONObject(j).isNull("display")) {
					theReasonReference.setDisplay(reportJSON.getJSONObject(j).getString("display"));
				}
				reportList.add(theReasonReference);
			}
			procedure.setReport(reportList);
		}
        
        //set focalDevice
        if (!procedureJSON.isNull("focalDevice")) {
			JSONArray focalDeviceJSON = procedureJSON.getJSONArray("focalDevice");
			List<ProcedureFocalDeviceComponent> focalDeviceList = new ArrayList<Procedure.ProcedureFocalDeviceComponent>();
			int noOfFocalDevices = focalDeviceJSON.length();
			
			for (int i = 0; i < noOfFocalDevices; i++) {
				ProcedureFocalDeviceComponent theFocalDevice = new ProcedureFocalDeviceComponent();
				
				if (!focalDeviceJSON.getJSONObject(i).isNull("action")) {
					JSONObject actionJSON = focalDeviceJSON.getJSONObject(i).getJSONObject("action");
					CodeableConcept theAction = new CodeableConcept();
					
					if (!actionJSON.isNull("coding")) {
						JSONArray codingJSON = actionJSON.getJSONArray("coding");
						List<Coding> codingList = new ArrayList<Coding>();
						int noOfCodings = codingJSON.length();
						
						for (int j = 0; j < noOfCodings; j++) {
							Coding theCoding = new Coding();
							
							if (!codingJSON.getJSONObject(j).isNull("system")) {
								theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
							}
							if (!codingJSON.getJSONObject(j).isNull("code")) {
								theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
							}
							if (!codingJSON.getJSONObject(j).isNull("display")) {
								theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
							}
							codingList.add(theCoding);
						}
						theAction.setCoding(codingList);
					}
					if (!actionJSON.isNull("text")) {
						theAction.setText(actionJSON.getString("text"));
					}
					theFocalDevice.setAction(theAction);
				}
				
				if (!focalDeviceJSON.getJSONObject(i).isNull("manipulated")) {
					JSONObject manipulatedJSON = focalDeviceJSON.getJSONObject(i).getJSONObject("manipulated");
					Reference theManipulated = new Reference();
					
					if (!manipulatedJSON.isNull("reference")) {
						theManipulated.setReference(manipulatedJSON.getString("reference"));
					}
					if (!manipulatedJSON.isNull("display")) {
						theManipulated.setDisplay(manipulatedJSON.getString("display"));
					}
					theFocalDevice.setManipulated(theManipulated);
				}
				
				focalDeviceList.add(theFocalDevice);
			}
			procedure.setFocalDevice(focalDeviceList);
		}
        
        //set usedReference
        if (!procedureJSON.isNull("usedReference")) {
			JSONArray usedReferenceJSON = procedureJSON.getJSONArray("usedReference");
			List<Reference> usedReferenceList = new ArrayList<Reference>();
			
			for (int j = 0; j < usedReferenceJSON.length(); j++) {
				Reference theReasonReference = new Reference();
				
				if (!usedReferenceJSON.getJSONObject(j).isNull("reference")) {
					theReasonReference.setReference(usedReferenceJSON.getJSONObject(j).getString("reference"));
				}
				if (!usedReferenceJSON.getJSONObject(j).isNull(" type")) {
					theReasonReference.setType(usedReferenceJSON.getJSONObject(j).getString(" type"));
				}
				if (!usedReferenceJSON.getJSONObject(j).isNull("display")) {
					theReasonReference.setDisplay(usedReferenceJSON.getJSONObject(j).getString("display"));
				}
				usedReferenceList.add(theReasonReference);
			}
			procedure.setUsedReference(usedReferenceList);
		}
        
      //set usedCode
        if (!procedureJSON.isNull("usedCode")) {
			JSONArray usedCodeJSON = procedureJSON.getJSONArray("usedCode");
			List<CodeableConcept> usedCodeList = new ArrayList<CodeableConcept>();
			int noOfUsedCode= usedCodeJSON.length();
			
			for (int j = 0; j < noOfUsedCode; j++) {
				CodeableConcept theUsedCode = new CodeableConcept();
				
				if (!usedCodeJSON.getJSONObject(j).isNull("coding")) {
					 JSONArray codingJSON = usedCodeJSON.getJSONObject(j).getJSONArray("coding");
					 List<Coding> codingList = new ArrayList<Coding>();
					 int noOfCodings = codingJSON.length();
					 for (int k = 0; k < noOfCodings; k++) {
						Coding theCoding = new Coding();
						
						if (!codingJSON.getJSONObject(k).isNull("system")) {
							theCoding.setSystem(codingJSON.getJSONObject(k).getString("system"));
						}
						if (!codingJSON.getJSONObject(k).isNull("code")) {
							theCoding.setCode(codingJSON.getJSONObject(k).getString("code"));
						}
						if (!codingJSON.getJSONObject(k).isNull("display")) {
							theCoding.setDisplay(codingJSON.getJSONObject(k).getString("display"));
						}
						codingList.add(theCoding);
					}
					 theUsedCode.setCoding(codingList);
				}
				if (!usedCodeJSON.getJSONObject(j).isNull("text")) {
					theUsedCode.setText(usedCodeJSON.getJSONObject(j).getString("text"));
				}
				usedCodeList.add(theUsedCode);
			}
			procedure.setUsedCode(usedCodeList);
		}
        
        //set statusReason
        if (!procedureJSON.isNull("statusReason")) {
			JSONObject actionJSON = procedureJSON.getJSONObject("statusReason");
			CodeableConcept theStatusReason = new CodeableConcept();
			
			if (!actionJSON.isNull("coding")) {
				JSONArray codingJSON = actionJSON.getJSONArray("coding");
				List<Coding> codingList = new ArrayList<Coding>();
				int noOfCodings = codingJSON.length();
				
				for (int j = 0; j < noOfCodings; j++) {
					Coding theCoding = new Coding();
					
					if (!codingJSON.getJSONObject(j).isNull("system")) {
						theCoding.setSystem(codingJSON.getJSONObject(j).getString("system"));
					}
					if (!codingJSON.getJSONObject(j).isNull("code")) {
						theCoding.setCode(codingJSON.getJSONObject(j).getString("code"));
					}
					if (!codingJSON.getJSONObject(j).isNull("display")) {
						theCoding.setDisplay(codingJSON.getJSONObject(j).getString("display"));
					}
					codingList.add(theCoding);
				}
				theStatusReason.setCoding(codingList);
			}
			if (!actionJSON.isNull("text")) {
				theStatusReason.setText(actionJSON.getString("text"));
			}
			procedure.setStatusReason(theStatusReason);
		}
        
        //set recorder
        if (!procedureJSON.isNull("recorder")) {
        	JSONObject recorderJSON = procedureJSON.getJSONObject("recorder");
        	Reference theRecorder = new Reference();
        	
        	if (!recorderJSON.isNull("reference")) {
        		theRecorder.setReference(recorderJSON.getString("reference"));
			}
        	if (!recorderJSON.isNull("type")) {
        		theRecorder.setType(recorderJSON.getString("type"));
			}
        	if (!recorderJSON.isNull("display")) {
        		theRecorder.setDisplay(recorderJSON.getString("display"));
			}
			procedure.setRecorder(theRecorder);
		}
        
        //set asserter
        if (!procedureJSON.isNull("asserter")) {
        	JSONObject asserterJSON = procedureJSON.getJSONObject("asserter");
        	Reference theAsserter = new Reference();
        	
        	if (!asserterJSON.isNull("reference")) {
        		theAsserter.setReference(asserterJSON.getString("reference"));
			}
        	if (!asserterJSON.isNull("type")) {
        		theAsserter.setType(asserterJSON.getString("type"));
			}
        	if (!asserterJSON.isNull("display")) {
        		theAsserter.setDisplay(asserterJSON.getString("display"));
			}
			procedure.setAsserter(theAsserter);
		}
        
        //set complicationDetail
        if (!procedureJSON.isNull("complicationDetail")) {
			JSONArray complicationDetailJSON = procedureJSON.getJSONArray("complicationDetail");
			List<Reference> complicationDetailList = new ArrayList<Reference>();
			
			for (int j = 0; j < complicationDetailJSON.length(); j++) {
				Reference theReasonReference = new Reference();
				
				if (!complicationDetailJSON.getJSONObject(j).isNull("reference")) {
					theReasonReference.setReference(complicationDetailJSON.getJSONObject(j).getString("reference"));
				}
				if (!complicationDetailJSON.getJSONObject(j).isNull(" type")) {
					theReasonReference.setType(complicationDetailJSON.getJSONObject(j).getString(" type"));
				}
				if (!complicationDetailJSON.getJSONObject(j).isNull("display")) {
					theReasonReference.setDisplay(complicationDetailJSON.getJSONObject(j).getString("display"));
				}
				complicationDetailList.add(theReasonReference);
			}
			procedure.setComplicationDetail(complicationDetailList);
		}
        
        return procedure;
     }
}
