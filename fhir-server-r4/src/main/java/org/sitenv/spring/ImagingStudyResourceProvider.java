package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesComponent;
import org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesInstanceComponent;
import org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesPerformerComponent;
import org.hl7.fhir.r4.model.ImagingStudy.ImagingStudyStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafImagingStudy;
import org.sitenv.spring.service.ImagingStudyService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ImagingStudyResourceProvider implements IResourceProvider{
	
	public static final String RESOURCE_TYPE = "ImagingStudy";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    ImagingStudyService service;
  
    
    public ImagingStudyResourceProvider() {
	    context = new AnnotationConfigApplicationContext(AppConfig.class);
	    service = (ImagingStudyService) context.getBean("imagingStudyService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return ImagingStudy.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * @param theId : Id of the ImagingStudy
	 * @return : Object of ImagingStudy information
	 */	
	@Read(version=true)
    public ImagingStudy readOrVread(@IdParam IdType theId) {
		String id;
		DafImagingStudy dafImagingStudy;
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
			dafImagingStudy = service.getImagingStudyByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       dafImagingStudy = service.getImagingStudyById(id);
		}
		return createImagingStudyObject(dafImagingStudy);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=ImagingStudy.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/ImagingStudy/1/_history
	 * @param theId : ID of the patient
	 * @return : List of ImagingStudy
	 */
	@History()
    public List<ImagingStudy> getImagingStudyHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafImagingStudy> dafImagingStudyList = service.getImagingStudyHistoryById(id);
        
        List<ImagingStudy> imagingStudyList = new ArrayList<ImagingStudy>();
        for (DafImagingStudy dafImagingStudy : dafImagingStudyList) {
        	imagingStudyList.add(createImagingStudyObject(dafImagingStudy));
        }
        
        return imagingStudyList;
	}
	
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theModality
	 * @param theSubject
	 * @param theStarted
	 * @param theBasedOn
	 * @param theEndpoint
	 * @param theBodySite
	 * @param thePerformer
	 */
    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = ImagingStudy.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "A ImagingStudy identifier")
        @OptionalParam(name = ImagingStudy.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "The status of the study")
        @OptionalParam(name = ImagingStudy.SP_STATUS)
        StringAndListParam theStatus,

        @Description(shortDefinition = "A portion of the family name of the ImagingStudy")
        @OptionalParam(name = ImagingStudy.SP_MODALITY)
        StringAndListParam theModality,

        @Description(shortDefinition = "Who the study is about")
        @OptionalParam(name = ImagingStudy.SP_SUBJECT)
        StringAndListParam theSubject,

        @Description(shortDefinition = "When the study was started")
        @OptionalParam(name = ImagingStudy.SP_STARTED)
        DateAndListParam theStarted,

        @Description(shortDefinition = "The order for the image")
        @OptionalParam(name = ImagingStudy.SP_BASEDON)
        StringAndListParam theBasedOn,

        @Description(shortDefinition = "The endpoint for the study or series")
        @OptionalParam(name = ImagingStudy.SP_ENDPOINT)
        StringAndListParam theEndpoint,
        
        @Description(shortDefinition="A city specified in an address")
		@OptionalParam(name = ImagingStudy.SP_BODYSITE)
		StringAndListParam theBodysite, 
		
		@Description(shortDefinition="The person who performed the study")
        @OptionalParam(name = ImagingStudy.SP_PERFORMER)
		StringAndListParam thePerformer, 

        @IncludeParam(allow = {"", "", "*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(ImagingStudy.SP_RES_ID, theId);
        paramMap.add(ImagingStudy.SP_IDENTIFIER, theIdentifier);
        paramMap.add(ImagingStudy.SP_STATUS, theStatus);
        paramMap.add(ImagingStudy.SP_MODALITY, theModality);
        paramMap.add(ImagingStudy.SP_SUBJECT, theSubject);
        paramMap.add(ImagingStudy.SP_STARTED, theStarted);
        paramMap.add(ImagingStudy.SP_BASEDON, theBasedOn);
        paramMap.add(ImagingStudy.SP_ENDPOINT, theEndpoint);
        paramMap.add(ImagingStudy.SP_BODYSITE, theBodysite);
        paramMap.add(ImagingStudy.SP_PERFORMER, thePerformer);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<DafImagingStudy> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> imagingStudyList = new ArrayList<IBaseResource>();
                for(DafImagingStudy dafImagingStudy : results){
                	imagingStudyList.add(createImagingStudyObject(dafImagingStudy));
                }
                return imagingStudyList;
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
     * @param dafImagingStudy : DafDocumentReference imagingStudy object
     * @return : DocumentReference imagingStudy object
     */

	private ImagingStudy createImagingStudyObject(DafImagingStudy dafImagingStudy) {
		ImagingStudy imagingStudy=new ImagingStudy();
		JSONObject imagingStudyJSON = new JSONObject(dafImagingStudy.getData());
		
		// Set version
	    if(!(imagingStudyJSON.isNull("meta"))) {
	    	if(!(imagingStudyJSON.getJSONObject("meta").isNull("versionId"))) {
	    		imagingStudy.setId(new IdType(RESOURCE_TYPE, imagingStudyJSON.getString("id") + "", imagingStudyJSON.getJSONObject("meta").getString("versionId")));
	    	}else {
				imagingStudy.setId(new IdType(RESOURCE_TYPE, imagingStudyJSON.getString("id") + "", VERSION_ID));
			}
	    }
	    else {
	    	imagingStudy.setId(new IdType(RESOURCE_TYPE, imagingStudyJSON.getString("id") + "", VERSION_ID));
	    }
	    
	    //Set identifier
        if(!(imagingStudyJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = imagingStudyJSON.getJSONArray("identifier");
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
        	imagingStudy.setIdentifier(identifiers);
        }
	    
	    //set status
	    if(!(imagingStudyJSON.isNull("status"))) {
	    	imagingStudy.setStatus(ImagingStudyStatus.fromCode(imagingStudyJSON.getString("status")));
	    }
	    
	   //set subject
	    if(!(imagingStudyJSON.isNull("subject"))) {
	    	Reference  theSubject = new Reference();
	    	if(!(imagingStudyJSON.getJSONObject("subject").isNull("reference"))) {
	    		theSubject.setReference(imagingStudyJSON.getJSONObject("subject").getString("reference"));    		
	    	}
	    	
	    	if(!(imagingStudyJSON.getJSONObject("subject").isNull("display"))) {
	    		theSubject.setDisplay(imagingStudyJSON.getJSONObject("subject").getString("display"));    		
	    	}
	    	imagingStudy.setSubject(theSubject);    
	    }
	    
	  //Set startDate
        if(!(imagingStudyJSON.isNull("startDate"))) {
         	Date startDate = CommonUtil.convertStringToDate(imagingStudyJSON.getString("startDate"));
        	imagingStudy.setStarted(startDate);
        }
	    
	    //set numberOfSeries
	    if(!(imagingStudyJSON.isNull("numberOfSeries"))) {
	    	imagingStudy.setNumberOfSeries(imagingStudyJSON.getInt("numberOfSeries"));
	    }
	    
	    //set numberOfInstances
	    if(!(imagingStudyJSON.isNull("numberOfInstances"))) {
	    	imagingStudy.setNumberOfInstances(imagingStudyJSON.getInt("numberOfInstances"));
	    }
	  
	    //set encounter
	    if(!(imagingStudyJSON.isNull("encounter"))) {
	    	Reference  theEncounter = new Reference();
	    	
	    	if(!(imagingStudyJSON.getJSONObject("encounter").isNull("reference"))) {
	    		theEncounter.setReference(imagingStudyJSON.getJSONObject("encounter").getString("reference"));    		
	    	}
	    	if(!(imagingStudyJSON.getJSONObject("encounter").isNull("display"))) {
	    		theEncounter.setDisplay(imagingStudyJSON.getJSONObject("encounter").getString("display"));    		
	    	}
	    	imagingStudy.setEncounter(theEncounter); 
	    }
	    
	    //set basedOn
	    if(!(imagingStudyJSON.isNull("basedOn"))) {
	    	JSONArray basedOnJSON=imagingStudyJSON.getJSONArray("basedOn");
	    	int noOfbased=basedOnJSON.length();
	    	List<Reference> basedOnList=new ArrayList<Reference>();
	    	
	    	for(int i = 0; i < noOfbased ; i++) {
		    	Reference  theBasedOn = new Reference();
		    	if(!(basedOnJSON.getJSONObject(i).isNull("reference"))) {
		    		theBasedOn.setReference(basedOnJSON.getJSONObject(i).getString("reference"));    		
		    	}
		    	if(!(basedOnJSON.getJSONObject(i).isNull("type"))) {
		    		theBasedOn.setType(basedOnJSON.getJSONObject(i).getString("type"));    		
		    	}
		    	if(!(basedOnJSON.getJSONObject(i).isNull("display"))) {
		    		theBasedOn.setDisplay(basedOnJSON.getJSONObject(i).getString("display"));    		
		    	}
		    	basedOnList.add(theBasedOn);
	    	}
	    	imagingStudy.setBasedOn(basedOnList); 
	    }
	    
	    //set referrer
	    if(!(imagingStudyJSON.isNull("referrer"))) {
	    	Reference  theReferrer = new Reference();
	    	
	    	if(!(imagingStudyJSON.getJSONObject("referrer").isNull("reference"))) {
	    		theReferrer.setReference(imagingStudyJSON.getJSONObject("referrer").getString("reference"));    		
	    	}
	    	if(!(imagingStudyJSON.getJSONObject("referrer").isNull("type"))) {
	    		theReferrer.setType(imagingStudyJSON.getJSONObject("referrer").getString("type"));    		
	    	}
	    	if(!(imagingStudyJSON.getJSONObject("referrer").isNull("display"))) {
	    		theReferrer.setDisplay(imagingStudyJSON.getJSONObject("referrer").getString("display"));    		
	    	}
	    	imagingStudy.setReferrer(theReferrer); 
	    }
	    
	    //set interpreter
	    if(!(imagingStudyJSON.isNull("interpreter"))) {
	    	JSONArray interpreterJSON=imagingStudyJSON.getJSONArray("interpreter");
	    	int noOfinterpreter=interpreterJSON.length();
	    	List<Reference> interpreterList=new ArrayList<Reference>();
	    	
	    	for(int i = 0; i < noOfinterpreter ; i++) {
		    	Reference  theInterpreter = new Reference();
		    	if(!(interpreterJSON.getJSONObject(i).isNull("reference"))) {
		    		theInterpreter.setReference(interpreterJSON.getJSONObject(i).getString("reference"));    		
		    	}
		    	if(!(interpreterJSON.getJSONObject(i).isNull("type"))) {
		    		theInterpreter.setType(interpreterJSON.getJSONObject(i).getString("type"));    		
		    	}
		    	if(!(interpreterJSON.getJSONObject(i).isNull("display"))) {
		    		theInterpreter.setDisplay(interpreterJSON.getJSONObject(i).getString("display"));    		
		    	}
		    	interpreterList.add(theInterpreter);
	    	}
	    	imagingStudy.setInterpreter(interpreterList); 
	    }
	    
	    //set endpoint
	    if(!(imagingStudyJSON.isNull("endpoint"))) {
	    	JSONArray endpointJSON=imagingStudyJSON.getJSONArray("endpoint");
	    	int noOfendpoint=endpointJSON.length();
	    	List<Reference> endpointList=new ArrayList<Reference>();
	    	
	    	for(int i = 0; i < noOfendpoint ; i++) {
		    	Reference  theEndpoint = new Reference();
		    	if(!(endpointJSON.getJSONObject(i).isNull("reference"))) {
		    		theEndpoint.setReference(endpointJSON.getJSONObject(i).getString("reference"));    		
		    	}
		    	if(!(endpointJSON.getJSONObject(i).isNull("type"))) {
		    		theEndpoint.setType(endpointJSON.getJSONObject(i).getString("type"));    		
		    	}
		    	if(!(endpointJSON.getJSONObject(i).isNull("display"))) {
		    		theEndpoint.setDisplay(endpointJSON.getJSONObject(i).getString("display"));    		
		    	}
		    	endpointList.add(theEndpoint);
	    	}
	    	imagingStudy.setEndpoint(endpointList); 
	    }
	    
	    //set procedureReference
	    if(!(imagingStudyJSON.isNull("procedureReference"))) {
	    	Reference  theProcedureReference = new Reference();
	    	
	    	if(!(imagingStudyJSON.getJSONObject("procedureReference").isNull("reference"))) {
	    		theProcedureReference.setReference(imagingStudyJSON.getJSONObject("procedureReference").getString("reference"));    		
	    	}
	    	if(!(imagingStudyJSON.getJSONObject("procedureReference").isNull("type"))) {
	    		theProcedureReference.setType(imagingStudyJSON.getJSONObject("procedureReference").getString("type"));    		
	    	}
	    	if(!(imagingStudyJSON.getJSONObject("procedureReference").isNull("display"))) {
	    		theProcedureReference.setDisplay(imagingStudyJSON.getJSONObject("procedureReference").getString("display"));    		
	    	}
	    	imagingStudy.setProcedureReference(theProcedureReference); 
	    }
	    
        //set procedureCode
  		if(!(imagingStudyJSON.isNull("procedureCode"))) {
			JSONArray procedureCodeJSON = imagingStudyJSON.getJSONArray("procedureCode");
			int noOfProcedureCodes=procedureCodeJSON.length();
			List<CodeableConcept> procedureCodeList = new ArrayList<CodeableConcept>();
			for(int i = 0; i < noOfProcedureCodes ; i++) {
				CodeableConcept theProcedureCode = new CodeableConcept();
				if(!(procedureCodeJSON.getJSONObject(i).isNull("coding"))) {
					JSONArray codingJSON = procedureCodeJSON.getJSONObject(i).getJSONArray("coding");
					int noOfprCode = codingJSON.length();
					List<Coding> codingList = new ArrayList<Coding>();
					for(int p = 0; p < noOfprCode; p++) {
						Coding theCoding = new Coding();
						if(!(codingJSON.getJSONObject(p).isNull("system"))) {
							theCoding.setSystem(codingJSON.getJSONObject(p).getString("system"));
						}
						if(!(codingJSON.getJSONObject(p).isNull("code"))) {
							theCoding.setCode(codingJSON.getJSONObject(p).getString("code"));
						}
						if(!(codingJSON.getJSONObject(p).isNull("display"))) {
							theCoding.setDisplay(codingJSON.getJSONObject(p).getString("display"));
						}
						codingList.add(theCoding);
					}
					theProcedureCode.setCoding(codingList);
				}
				if(!(procedureCodeJSON.getJSONObject(i).isNull("text"))) {
				theProcedureCode.setText(procedureCodeJSON.getJSONObject(i).getString("text"));
				}
				procedureCodeList.add(theProcedureCode);
			}
			imagingStudy.setProcedureCode(procedureCodeList);
  		}
	  		
		//set reasonCode
		if(!(imagingStudyJSON.isNull("reasonCode"))) {
			JSONArray reasonCodeJSON = imagingStudyJSON.getJSONArray("reasonCode");
			int noOfreasonCodes=reasonCodeJSON.length();
			List<CodeableConcept> reasonCodeList = new ArrayList<CodeableConcept>();
			
			for(int i = 0; i < noOfreasonCodes ; i++) {
				CodeableConcept theReasonCode=new CodeableConcept();
				if(!(reasonCodeJSON.getJSONObject(i).isNull("coding"))) {
					JSONArray reasonCodingJSON = reasonCodeJSON.getJSONObject(i).getJSONArray("coding");
					int noOfreasonCode = reasonCodingJSON.length();
					List<Coding> reasonCodingList = new ArrayList<Coding>();
					for(int p = 0; p < noOfreasonCode; p++) {
						Coding theReasonCoding = new Coding();
						if(!(reasonCodingJSON.getJSONObject(p).isNull("system"))) {
							theReasonCoding.setSystem(reasonCodingJSON.getJSONObject(p).getString("system"));
						}
						if(!(reasonCodingJSON.getJSONObject(p).isNull("code"))) {
							theReasonCoding.setCode(reasonCodingJSON.getJSONObject(p).getString("code"));
						}
						if(!(reasonCodingJSON.getJSONObject(p).isNull("display"))) {
							theReasonCoding.setDisplay(reasonCodingJSON.getJSONObject(p).getString("display"));
						}
						reasonCodingList.add(theReasonCoding);
					}
					theReasonCode.setCoding(reasonCodingList);
				}
				reasonCodeList.add(theReasonCode);
			}
			imagingStudy.setReasonCode(reasonCodeList);
		}
	  		
	  	//set note
	    if(!(imagingStudyJSON.isNull("note"))) {
	    	JSONArray noteJSON=imagingStudyJSON.getJSONArray("note");
	    	int noOfNotes=noteJSON.length();
	    	List<Annotation> noteList=new ArrayList<Annotation>();
	    	
	    	for(int i = 0 ; i < noOfNotes ; i++) {
		    	Annotation theNote=new Annotation();
	    		if(!(noteJSON.getJSONObject(i).isNull("text"))) {
	    			theNote.setText(noteJSON.getJSONObject(i).getString("text"));
	    		}
	    		noteList.add(theNote);
	    	}
	    	imagingStudy.setNote(noteList);
	    }
  		
	    //set modality
	    if(!(imagingStudyJSON.isNull("modality"))) {
	    	JSONArray modalityJSON=imagingStudyJSON.getJSONArray("modality");
	    	int noOfModality =modalityJSON.length();
	    	List<Coding> modalityList=new ArrayList<Coding>();	    	
	    	for(int i = 0; i < noOfModality ; i++) {
		    	Coding theModality=new Coding();
	    		if(!(modalityJSON.getJSONObject(i).isNull("system"))) {
	    			theModality.setSystem(modalityJSON.getJSONObject(i).getString("system"));
	    		}
	    		if(!(modalityJSON.getJSONObject(i).isNull("code"))) {
	    			theModality.setCode(modalityJSON.getJSONObject(i).getString("code"));
	    		}
	    		if(!(modalityJSON.getJSONObject(i).isNull("display"))) {
	    			theModality.setDisplay(modalityJSON.getJSONObject(i).getString("display"));
	    		}
	    		modalityList.add(theModality);
	    	}
	    	imagingStudy.setModality(modalityList);
	    }
	    
	    //set series
	    if(!(imagingStudyJSON.isNull("series"))) {
	    	JSONArray seriesJSON=imagingStudyJSON.getJSONArray("series");
	    	int noOfSeries=seriesJSON.length();
	    	List<ImagingStudySeriesComponent> seriesList=new ArrayList<ImagingStudySeriesComponent>();	    	
	    	for(int i = 0; i < noOfSeries ; i++) {
		    	ImagingStudySeriesComponent theSeries=new ImagingStudySeriesComponent();
	    		if(!(seriesJSON.getJSONObject(i).isNull("uid"))) {
	    			theSeries.setId(seriesJSON.getJSONObject(i).getString("uid"));
	    		}
	    		if(!(seriesJSON.getJSONObject(i).isNull("number"))) {
	    			theSeries.setNumber(seriesJSON.getJSONObject(i).getInt("number"));
	    		}
	    		if(!(seriesJSON.getJSONObject(i).isNull("modality"))) {
	    			JSONObject modalityJSON=seriesJSON.getJSONObject(i).getJSONObject("modality");
	    			Coding theModality=new Coding();
	    			
	    			if(!(modalityJSON.isNull("system"))) {
	    				theModality.setSystem(modalityJSON.getString("system"));
	    			}
	    			if(!(modalityJSON.isNull("code"))) {
	    				theModality.setCode(modalityJSON.getString("code"));
	    			}
	    			theSeries.setModality(theModality);
	    		}
	    		if(!(seriesJSON.getJSONObject(i).isNull("description"))) {
	    			theSeries.setDescription(seriesJSON.getJSONObject(i).getString("description"));
	    		}
	    	    if(!(seriesJSON.getJSONObject(i).isNull("numberOfInstances"))) {
	    	    	theSeries.setNumberOfInstances(seriesJSON.getJSONObject(i).getInt("numberOfInstances"));
	    	    }
	    	    if(!(seriesJSON.getJSONObject(i).isNull("endpoint"))) {
	    	    	JSONArray endpointJSON=seriesJSON.getJSONObject(i).getJSONArray("endpoint");
	    	    	int noOfendpoint=endpointJSON.length();
	    	    	List<Reference> endpointList=new ArrayList<Reference>();
	    	    	
	    	    	for(int j = 0; j < noOfendpoint ; j++) {
	    		    	Reference  theEndpoint = new Reference();
	    		    	if(!(endpointJSON.getJSONObject(j).isNull("reference"))) {
	    		    		theEndpoint.setReference(endpointJSON.getJSONObject(j).getString("reference"));    		
	    		    	}
	    		    	if(!(endpointJSON.getJSONObject(j).isNull("type"))) {
	    		    		theEndpoint.setType(endpointJSON.getJSONObject(j).getString("type"));    		
	    		    	}
	    		    	if(!(endpointJSON.getJSONObject(j).isNull("display"))) {
	    		    		theEndpoint.setDisplay(endpointJSON.getJSONObject(j).getString("display"));    		
	    		    	}
	    		    	endpointList.add(theEndpoint);
	    	    	}
	    	    	theSeries.setEndpoint(endpointList); 
	    	    }
	    	    if(!(seriesJSON.getJSONObject(i).isNull("bodySite"))) {
	    			JSONObject bodySiteJSON=seriesJSON.getJSONObject(i).getJSONObject("bodySite");
	    			Coding theBodySite=new Coding();
	    			
	    			if(!(bodySiteJSON.isNull("system"))) {
	    				theBodySite.setSystem(bodySiteJSON.getString("system"));
	    			}
	    			if(!(bodySiteJSON.isNull("code"))) {
	    				theBodySite.setCode(bodySiteJSON.getString("code"));
	    			}
	    			if(!(bodySiteJSON.isNull("display"))) {
	    				theBodySite.setDisplay(bodySiteJSON.getString("display"));
	    			}
	    			theSeries.setBodySite(theBodySite);
	    		}
	    	    
	    	    if(!(seriesJSON.getJSONObject(i).isNull("laterality"))) {
	    			JSONObject lateralityJSON=seriesJSON.getJSONObject(i).getJSONObject("laterality");
	    			Coding theLaterality=new Coding();
	    			
	    			if(!(lateralityJSON.isNull("system"))) {
	    				theLaterality.setSystem(lateralityJSON.getString("system"));
	    			}
	    			if(!(lateralityJSON.isNull("code"))) {
	    				theLaterality.setCode(lateralityJSON.getString("code"));
	    			}
	    			if(!(lateralityJSON.isNull("display"))) {
	    				theLaterality.setDisplay(lateralityJSON.getString("display"));
	    			}
	    			theSeries.setLaterality(theLaterality);
	    		}
	    	    if(!(seriesJSON.getJSONObject(i).isNull("started"))) {
	    	    	Date startDate = CommonUtil.convertStringToDate(seriesJSON.getJSONObject(i).getString("started"));
	    	    	theSeries.setStarted(startDate);
	    		}
	    	    if(!(seriesJSON.getJSONObject(i).isNull("performer"))) {
	    	    	JSONArray performerJSON=seriesJSON.getJSONObject(i).getJSONArray("performer");
			    	int noOfperformer=performerJSON.length();
			    	List<ImagingStudySeriesPerformerComponent> performerList=new ArrayList<ImagingStudySeriesPerformerComponent>();			    	
			    	for(int l = 0; l < noOfperformer ; l++) {
				    	ImagingStudySeriesPerformerComponent thePerformer=new ImagingStudySeriesPerformerComponent();
			    		if(!(performerJSON.getJSONObject(l).isNull("function"))) {
			    			JSONObject fuctionJSON=performerJSON.getJSONObject(l).getJSONObject("function");
			    			CodeableConcept theFunction=new CodeableConcept();
			    			
			    			if(!(fuctionJSON.isNull("coding"))) {
								JSONArray functionCodingJSON = fuctionJSON.getJSONArray("coding");
								int noOfreasonCode = functionCodingJSON.length();
								List<Coding> functionCodingList = new ArrayList<Coding>();
								for(int p = 0; p < noOfreasonCode; p++) {
									Coding thefunctionCoding = new Coding();
									if(!(functionCodingJSON.getJSONObject(p).isNull("system"))) {
										thefunctionCoding.setSystem(functionCodingJSON.getJSONObject(p).getString("system"));
									}
									if(!(functionCodingJSON.getJSONObject(p).isNull("code"))) {
										thefunctionCoding.setCode(functionCodingJSON.getJSONObject(p).getString("code"));
									}
									if(!(functionCodingJSON.getJSONObject(p).isNull("display"))) {
										thefunctionCoding.setDisplay(functionCodingJSON.getJSONObject(p).getString("display"));
									}
									functionCodingList.add(thefunctionCoding);
								}
								theFunction.setCoding(functionCodingList);
							}
			    			thePerformer.setFunction(theFunction);
			    		}
			    		
			    		if(!(performerJSON.getJSONObject(l).isNull("actor"))) {
			    			Reference theActor=new Reference();
			    			if(!(performerJSON.getJSONObject(l).getJSONObject("actor").isNull("reference"))) {
			    				theActor.setReference(performerJSON.getJSONObject(l).getJSONObject("actor").getString("reference"));
			    			}
			    			if(!(performerJSON.getJSONObject(l).getJSONObject("actor").isNull("type"))) {
			    				theActor.setType(performerJSON.getJSONObject(l).getJSONObject("actor").getString("type"));
			    			}
			    			if(!(performerJSON.getJSONObject(l).getJSONObject("actor").isNull("display"))) {
			    				theActor.setDisplay(performerJSON.getJSONObject(l).getJSONObject("actor").getString("display"));
			    			}
			    			thePerformer.setActor(theActor);
			    		}
			    		performerList.add(thePerformer);
			    	}
			    	theSeries.setPerformer(performerList);
	    	    }
	    	    
	    	    if(!(seriesJSON.getJSONObject(i).isNull("instance"))) {
	    	    	JSONArray instanceJSON=seriesJSON.getJSONObject(i).getJSONArray("instance");
	    	    	int noOfInstances=instanceJSON.length();
	    	    	List<ImagingStudySeriesInstanceComponent> instanceList=new ArrayList<ImagingStudySeriesInstanceComponent>();
	    	    	   	    	
	    	    	for(int m = 0; m < noOfInstances ; m++) {
	    	    		ImagingStudySeriesInstanceComponent theInstance=new ImagingStudySeriesInstanceComponent();
	    	    		if(!(instanceJSON.getJSONObject(m).isNull("uid"))) {
	    	    			theInstance.setId(instanceJSON.getJSONObject(m).getString("uid"));
			    		}
	    	    		
	    	    		if(!(instanceJSON.getJSONObject(m).isNull("sopClass"))) {
	    	    			Coding sopClassCoding=new Coding();
	    	    			if(!(instanceJSON.getJSONObject(m).getJSONObject("sopClass").isNull("system"))) {
	    	    				sopClassCoding.setSystem(instanceJSON.getJSONObject(m).getJSONObject("sopClass").getString("system"));
	    	    			}
	    	    			if(!(instanceJSON.getJSONObject(m).getJSONObject("sopClass").isNull("code"))) {
	    	    				sopClassCoding.setCode(instanceJSON.getJSONObject(m).getJSONObject("sopClass").getString("code"));
	    	    			}
	    	    			theInstance.setSopClass(sopClassCoding);
			    		}
	    	    		
	    	    		if(!(instanceJSON.getJSONObject(m).isNull("number"))) {
	    	    			theInstance.setNumber(instanceJSON.getJSONObject(m).getInt("number"));
	    	    		}
	    	    		
	    	    		if(!(instanceJSON.getJSONObject(m).isNull("title"))) {
	    	    			theInstance.setTitle(instanceJSON.getJSONObject(m).getString("title"));
	    	    		}
	    	    		instanceList.add(theInstance);
	    	    	}
	    	    	theSeries.setInstance(instanceList);
	    	    }
	    	    seriesList.add(theSeries);
	    	}	
	    	imagingStudy.setSeries(seriesList);
	    }
	    return imagingStudy;
	}
}
	
	

