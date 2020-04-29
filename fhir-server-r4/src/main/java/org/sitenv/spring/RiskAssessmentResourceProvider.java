package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.NumberAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafRiskAssessment;
import org.sitenv.spring.service.RiskAssessmentService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class RiskAssessmentResourceProvider implements IResourceProvider{


	public static final String RESOURCE_TYPE = "RiskAssessment";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    RiskAssessmentService service;
  
    
    public RiskAssessmentResourceProvider() {
	    context = new AnnotationConfigApplicationContext(AppConfig.class);
	    service = (RiskAssessmentService) context.getBean("riskAssessmentService");
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		
		return RiskAssessment.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/RiskAssessment/1/_history/4
	 * @param theId : Id of the RiskAssessment
	 * @return : Object of RiskAssessment information
	 */	
	@Read(version=true)
    public RiskAssessment readOrVread(@IdParam IdType theId) {
		String id;
		
		DafRiskAssessment dafRiskAssessment;
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
			dafRiskAssessment = service.getRiskAssessmentByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			dafRiskAssessment = service.getRiskAssessmentById(id);
		}
		return createRiskAssessmentObject(dafRiskAssessment);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=RiskAssessment.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/RiskAssessment/1/_history
	 * @param theId : ID of the patient
	 * @return : List of RiskAssessment's
	 */
	@History()
    public List<RiskAssessment> getRiskAssessmentHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafRiskAssessment> dafRiskAssessmentList = service.getRiskAssessmentHistoryById(id);
        
        List<RiskAssessment> riskAssessmentList = new ArrayList<RiskAssessment>();
        for (DafRiskAssessment dafRiskAssessment : dafRiskAssessmentList) {
        	riskAssessmentList.add(createRiskAssessmentObject(dafRiskAssessment));
        }
        
        return riskAssessmentList;
	}
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theId
	 * @param theIdentifier
	 * @param theCondition
	 * @param theEncounter
	 * @param thePerformer
	 * @param theProbability
	 * @param theSubject
	 * @param theRisk
	 * @param theMethod
	 * @return
	 */
    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,


        @Description(shortDefinition="A city specified in an address")
		@OptionalParam(name = RiskAssessment.SP_RES_ID)
		StringAndListParam theId, 
        
        @Description(shortDefinition = "Unique identifier for the assessment")
        @OptionalParam(name = RiskAssessment.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "Condition assessed")
        @OptionalParam(name = RiskAssessment.SP_CONDITION)
        StringAndListParam theCondition,


        @Description(shortDefinition = "Where was assessment performed?")
        @OptionalParam(name = RiskAssessment.SP_ENCOUNTER)
        StringAndListParam theEncounter,

        @Description(shortDefinition = "Evaluation mechanism\"")
        @OptionalParam(name = RiskAssessment.SP_METHOD)
        StringAndListParam theMethod,

        @Description(shortDefinition = "Who did assessment?")
        @OptionalParam(name = RiskAssessment.SP_PERFORMER)
        StringAndListParam thePerformer,

        @Description(shortDefinition = "Likelihood of specified outcome")
        @OptionalParam(name = RiskAssessment.SP_PROBABILITY)
        NumberAndListParam theProbability,

        @Description(shortDefinition = "Likelihood of specified outcome as a qualitative value")
        @OptionalParam(name = RiskAssessment.SP_RISK)
        StringAndListParam theRisk,

        @Description(shortDefinition = "Who/what does assessment apply to?")
        @OptionalParam(name = RiskAssessment.SP_SUBJECT)
        StringAndListParam theSubject,
        
        @Description(shortDefinition = "Who/what does assessment apply to?")
        @OptionalParam(name = RiskAssessment.SP_PATIENT)
        StringAndListParam thePatient,
        
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(RiskAssessment.SP_RES_ID, theId);
            paramMap.add(RiskAssessment.SP_IDENTIFIER, theIdentifier);
            paramMap.add(RiskAssessment.SP_CONDITION, theCondition);
            paramMap.add(RiskAssessment.SP_ENCOUNTER, theEncounter);
            paramMap.add(RiskAssessment.SP_METHOD, theMethod);
            paramMap.add(RiskAssessment.SP_PERFORMER, thePerformer);
            paramMap.add(RiskAssessment.SP_PROBABILITY, theProbability);
            paramMap.add(RiskAssessment.SP_RISK, theRisk);
            paramMap.add(RiskAssessment.SP_SUBJECT, theSubject);
            paramMap.add(RiskAssessment.SP_PATIENT, thePatient);
            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<DafRiskAssessment> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> riskAssessmentList = new ArrayList<IBaseResource>();
                    for(DafRiskAssessment dafRiskAssessment : results){
                    	riskAssessmentList.add(createRiskAssessmentObject(dafRiskAssessment));
                    }
                    return riskAssessmentList;
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
     * @param dafPatient : DafDocumentReference patient object
     * @return : DocumentReference patient object
     */	

	private RiskAssessment createRiskAssessmentObject(DafRiskAssessment dafRiskAssessment) {
		
		RiskAssessment riskAssessment=new RiskAssessment();
		JSONObject riskAssessmentJSON=new  JSONObject(dafRiskAssessment.getData());
	
		
		// Set version
        if(!(riskAssessmentJSON.isNull("meta"))) {
        	if(!(riskAssessmentJSON.getJSONObject("meta").isNull("versionId"))) {
        		riskAssessment.setId(new IdType(RESOURCE_TYPE, riskAssessmentJSON.getString("id") + "", riskAssessmentJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				riskAssessment.setId(new IdType(RESOURCE_TYPE, riskAssessmentJSON.getString("id") + "", VERSION_ID));
			}
        }else {
        	riskAssessment.setId(new IdType(RESOURCE_TYPE, riskAssessmentJSON.getString("id") + "", VERSION_ID));
        }
        
        
        //set status
        if(!(riskAssessmentJSON.isNull("status"))) {
        	riskAssessment.setStatus(RiskAssessment.RiskAssessmentStatus.fromCode(riskAssessmentJSON.getString("status")));
        }
        
        //set subject
	    if(!(riskAssessmentJSON.isNull("subject"))) {
	    	Reference  theSubject = new Reference();
	    	
	    	if(!(riskAssessmentJSON.getJSONObject("subject").isNull("reference"))) {
	    		theSubject.setReference(riskAssessmentJSON.getJSONObject("subject").getString("reference"));    		
	    	}
	    	
	    	if(!(riskAssessmentJSON.getJSONObject("subject").isNull("display"))) {
	    		theSubject.setDisplay(riskAssessmentJSON.getJSONObject("subject").getString("display"));    		
	    	}
	    	
	    	if(!(riskAssessmentJSON.getJSONObject("subject").isNull("type"))) {
	    		theSubject.setType(riskAssessmentJSON.getJSONObject("subject").getString("type"));    		
	    	}
	    	riskAssessment.setSubject(theSubject);    
	    }
	    
	    //Set identifier
        if(!(riskAssessmentJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = riskAssessmentJSON.getJSONArray("identifier");
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
        	riskAssessment.setIdentifier(identifiers);
        }
        
	    //set parent
	    if(!(riskAssessmentJSON.isNull("parent"))) {
	    	JSONObject parentJSON=riskAssessmentJSON.getJSONObject("parent");
	    	Reference theParent=new Reference();	    
		    if(!(parentJSON.isNull("identifier"))) {
		    	JSONObject identifierJSON = parentJSON.getJSONObject("identifier");	        	
        		Identifier theIdentifier = new Identifier();
        		if(!(identifierJSON.isNull("use"))) {
                	theIdentifier.setUse(Identifier.IdentifierUse.fromCode(identifierJSON.getString("use")));	
            	}
        		
        		if(!(identifierJSON.isNull("system"))) {
                	theIdentifier.setSystem(identifierJSON.getString("system"));
            	}
            	
            	if(!(identifierJSON.isNull("value"))) {
                	theIdentifier.setValue(identifierJSON.getString("value"));
            	}
            	
            	if(!(identifierJSON.isNull("period"))) {
            		Period identifierPeriod = new Period();
            		if(!(identifierJSON.getJSONObject("period").isNull("start"))) {
                        java.util.Date identifierSDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject("period").getString("start"));
                        identifierPeriod.setStart(identifierSDate);
            		}
            		if(!(identifierJSON.getJSONObject("period").isNull("end"))) {
                        java.util.Date identifierSDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject("period").getString("end"));
                        identifierPeriod.setEnd(identifierSDate);
            		}
                    theIdentifier.setPeriod(identifierPeriod);
            	}
            	
            	if(!(identifierJSON.isNull("assigner"))) {
        			Reference identifierReference = new Reference(); 
            		if(!(identifierJSON.getJSONObject("assigner").isNull("display"))) {
                        identifierReference.setDisplay(identifierJSON.getJSONObject("assigner").getString("display"));
            		}
                    theIdentifier.setAssigner(identifierReference);
            	}
	        	theParent.setIdentifier(theIdentifier);
	        }
		    riskAssessment.setParent(theParent);
	    }
	    
	    //set occurrenceDateTime
	    if(!(riskAssessmentJSON.isNull("occurrenceDateTime"))) {
	    	DateTimeType theStartDate = CommonUtil.convertStringToDateTimeType(riskAssessmentJSON.getString("occurrenceDateTime"));
        	riskAssessment.setOccurrence(theStartDate);
	    }
        
	    //set encounter
	    if(!(riskAssessmentJSON.isNull("encounter"))) {
	    	Reference  theEncounter = new Reference();
	    	
	    	if(!(riskAssessmentJSON.getJSONObject("encounter").isNull("reference"))) {
	    		theEncounter.setReference(riskAssessmentJSON.getJSONObject("encounter").getString("reference"));    		
	    	}
	    	if(!(riskAssessmentJSON.getJSONObject("encounter").isNull("display"))) {
	    		theEncounter.setDisplay(riskAssessmentJSON.getJSONObject("encounter").getString("display"));    		
	    	}
	    	riskAssessment.setEncounter(theEncounter); 
	    }
	    
	    //set basedOn
	    if(!(riskAssessmentJSON.isNull("basedOn"))) {
	    	Reference  theBasedOn = new Reference();
	    	
	    	if(!(riskAssessmentJSON.getJSONObject("basedOn").isNull("reference"))) {
	    		theBasedOn.setReference(riskAssessmentJSON.getJSONObject("basedOn").getString("reference"));    		
	    	}
	    	if(!(riskAssessmentJSON.getJSONObject("basedOn").isNull("type"))) {
	    		theBasedOn.setType(riskAssessmentJSON.getJSONObject("basedOn").getString("type"));    		
	    	}
	    	if(!(riskAssessmentJSON.getJSONObject("basedOn").isNull("display"))) {
	    		theBasedOn.setDisplay(riskAssessmentJSON.getJSONObject("basedOn").getString("display"));    		
	    	}
	    	riskAssessment.setBasedOn(theBasedOn); 
	    }
	    
	    //set performer
	    if(!(riskAssessmentJSON.isNull("performer"))) {
	    	Reference  thePerformer = new Reference();
	    	
	    	if(!(riskAssessmentJSON.getJSONObject("performer").isNull("reference"))) {
	    		thePerformer.setReference(riskAssessmentJSON.getJSONObject("performer").getString("reference"));    		
	    	}
	    	if(!(riskAssessmentJSON.getJSONObject("performer").isNull("display"))) {
	    		thePerformer.setDisplay(riskAssessmentJSON.getJSONObject("performer").getString("display"));    		
	    	}
	    	riskAssessment.setPerformer(thePerformer);    
	    }
	    
	    //set condition
	    if(!(riskAssessmentJSON.isNull("condition"))) {
	    	Reference  theCondition = new Reference();
	    	
	    	if(!(riskAssessmentJSON.getJSONObject("condition").isNull("reference"))) {
	    		theCondition.setReference(riskAssessmentJSON.getJSONObject("condition").getString("reference"));    		
	    	}
	    	riskAssessment.setCondition(theCondition);    
	    }
	    
	    //set method
	    if(!(riskAssessmentJSON.isNull("method"))) {
	    	JSONObject methodJSON = riskAssessmentJSON.getJSONObject("method");
			CodeableConcept methodCodeableConcept = new CodeableConcept();
    		if(!(methodJSON.isNull("coding"))) {
    			JSONArray mCodingJSON=methodJSON.getJSONArray("coding");
    			int noOfCoding=mCodingJSON.length();
    			List<Coding> codingList = new ArrayList<>();
            	for(int i = 0 ; i < noOfCoding ; i++) {
	            	Coding methodCoding = new Coding();
    				if(!(mCodingJSON.getJSONObject(i).isNull("code"))) {
    					methodCoding.setCode(mCodingJSON.getJSONObject(i).getString("code"));
    				}
    				if(!(mCodingJSON.getJSONObject(i).isNull("system"))) {
    					methodCoding.setSystem(mCodingJSON.getJSONObject(i).getString("system"));
    				}
  					if(!(mCodingJSON.getJSONObject(i).isNull("display"))) {
  						methodCoding.setDisplay(mCodingJSON.getJSONObject(i).getString("display"));
  					}
  					codingList.add(methodCoding);
            	}
            	methodCodeableConcept.setCoding(codingList);
    		}
    		if(!(methodJSON.isNull("text"))) {
    			methodCodeableConcept.setText(methodJSON.getString("text"));
			}
    		riskAssessment.setMethod(methodCodeableConcept);
	    }
		    
		//set basis
	    if(!(riskAssessmentJSON.isNull("basis"))) {
	    	JSONArray basisJSON = riskAssessmentJSON.getJSONArray("basis");
	    	int noOfBasis=basisJSON.length();
	    	List<Reference> basisList=new ArrayList<Reference>();
	    	
	    	for(int i = 0 ; i < noOfBasis ; i++) {
		    	Reference theBasis=new Reference();
	    		if(!(basisJSON.getJSONObject(i).isNull("reference"))) {
	    			theBasis.setReference(basisJSON.getJSONObject(i).getString("reference"));	
            	}
	    		if(!(basisJSON.getJSONObject(i).isNull("display"))) {
	    			theBasis.setDisplay(basisJSON.getJSONObject(i).getString("display"));	
            	}
	    		if(!(basisJSON.getJSONObject(i).isNull("type"))) {
	    			theBasis.setType(basisJSON.getJSONObject(i).getString("type"));	
            	}
	    		basisList.add(theBasis);
	    	}
	    	riskAssessment.setBasis(basisList);
	    }
		    
	    //Set note
        if(!(riskAssessmentJSON.isNull("note"))) {
        	JSONArray noteJSON = riskAssessmentJSON.getJSONArray("note");
        	int noOfNotes = noteJSON.length();
        	List<Annotation> noteList = new ArrayList<Annotation>();
        	for(int b = 0; b < noOfNotes; b++) {
        		Annotation theNote = new Annotation();
        		if(!(noteJSON.getJSONObject(b).isNull("text"))) {
        			theNote.setText(noteJSON.getJSONObject(b).getString("text"));
        		}
        		if(!(noteJSON.getJSONObject(b).isNull("time"))) {
        			Date noteTime = CommonUtil.convertStringToDate(noteJSON.getJSONObject(b).getString("time"));
        			theNote.setTime(noteTime);
        		}
        		noteList.add(theNote);
        	}
        	riskAssessment.setNote(noteList);
        }
		    
	    //set prediction
	    if(!(riskAssessmentJSON.isNull("prediction"))) {
	    	JSONArray predictionJSON=riskAssessmentJSON.getJSONArray("prediction");
	    	int noOfPredictions=predictionJSON.length();
	    	List<RiskAssessmentPredictionComponent> predictionList=new ArrayList<RiskAssessmentPredictionComponent>();
	    	
	    	for(int i = 0 ; i < noOfPredictions ; i++) {
	    		RiskAssessmentPredictionComponent thePrediction=new RiskAssessmentPredictionComponent();
		    	
	    		if(!(predictionJSON.getJSONObject(i).isNull("outcome"))) {
	    			JSONObject outcomeObj=predictionJSON.getJSONObject(i).getJSONObject("outcome");
			    	CodeableConcept theOutcome=new CodeableConcept();

	    			if(!(outcomeObj.isNull("coding"))) {
		    			JSONArray mCodingJSON=outcomeObj.getJSONArray("coding");
		    			int noOfCoding=mCodingJSON.length();
		    			List<Coding> codingList = new ArrayList<Coding>();
		            	for(int j = 0 ; j < noOfCoding ; j++) {
			            	Coding theCoding = new Coding();
		    				if(!(mCodingJSON.getJSONObject(i).isNull("code"))) {
		    					theCoding.setCode(mCodingJSON.getJSONObject(j).getString("code"));
		    				}
		    				if(!(mCodingJSON.getJSONObject(i).isNull("system"))) {
			    				theCoding.setSystem(mCodingJSON.getJSONObject(j).getString("system"));
			    			}
		    				if(!(mCodingJSON.getJSONObject(i).isNull("display"))) {
			    				theCoding.setDisplay(mCodingJSON.getJSONObject(j).getString("display"));
			    			}
		    				codingList.add(theCoding);
		            	}
		            	theOutcome.setCoding(codingList);
	    			}
		            	
	    			if(!(outcomeObj.isNull("text"))){
	    				theOutcome.setText(outcomeObj.getString("text"));
	    			}
	    			
	    			thePrediction.setOutcome(theOutcome);
	    		}
	    		
	    		if(!(predictionJSON.getJSONObject(i).isNull("qualitativeRisk"))) {
	    			JSONObject qualitativeRiskJSON=predictionJSON.getJSONObject(i).getJSONObject("qualitativeRisk");
	    			CodeableConcept theQualitativeRisk = new CodeableConcept();
	    			if(!(qualitativeRiskJSON.isNull("coding"))) {
		    			JSONArray qCodingJSON=qualitativeRiskJSON.getJSONArray("coding");
		    			int noOfCoding=qCodingJSON.length();
		    			List<Coding> codingList = new ArrayList<Coding>();
		            	for(int j = 0 ; j < noOfCoding ; j++) {
			            	Coding theCoding = new Coding();
		    				if(!(qCodingJSON.getJSONObject(i).isNull("code"))) {
		    				theCoding.setCode(qCodingJSON.getJSONObject(j).getString("code"));
		    				}
		    				if(!(qCodingJSON.getJSONObject(i).isNull("system"))) {
			    				theCoding.setSystem(qCodingJSON.getJSONObject(j).getString("system"));
			    			}
		    				if(!(qCodingJSON.getJSONObject(i).isNull("display"))) {
			    				theCoding.setDisplay(qCodingJSON.getJSONObject(j).getString("display"));
			    			}
		    				codingList.add(theCoding);
		            	}
		            	theQualitativeRisk.setCoding(codingList);
	    			}
	    			thePrediction.setQualitativeRisk(theQualitativeRisk);
	    		}
	    		
	    		
	    		if(!(predictionJSON.getJSONObject(i).isNull("probabilityDecimal"))) {
	    			DecimalType probabilityTypeVal =new DecimalType(predictionJSON.getJSONObject(i).getDouble("probabilityDecimal"));
	    			thePrediction.setProbability(probabilityTypeVal) ;
	    		}
	    		
	    		if(!(predictionJSON.getJSONObject(i).isNull("whenRange"))) {
	    			Range theRange = new Range();
	    			JSONObject whenRangeJSON=predictionJSON.getJSONObject(i).getJSONObject("whenRange");
	    			if(!(whenRangeJSON.isNull("low"))) {
	    				JSONObject lowJSON=whenRangeJSON.getJSONObject("low");
	    				SimpleQuantity lowQuantity=new SimpleQuantity();
	    				
	    				 if(!(lowJSON.isNull("value"))) {
	    					 lowQuantity.setValue(lowJSON.getDouble("value"));
	    				 }
	    				 if(!(lowJSON.isNull("unit"))) {
	    					 lowQuantity.setUnit(lowJSON.getString("unit"));
	    				 }
	    				 if(!(lowJSON.isNull("system"))) {
	    					 lowQuantity.setSystem(lowJSON.getString("system"));
	    				 }
	    				 if(!(lowJSON.isNull("code"))) {
	    					 lowQuantity.setCode(lowJSON.getString("code"));
	    				 }
	    				theRange.setLow(lowQuantity);
	    			}
	    			
	    			if(!(whenRangeJSON.isNull("high"))) {
	    				JSONObject highJSON=whenRangeJSON.getJSONObject("high");
	    				SimpleQuantity highQuantity=new SimpleQuantity();
	    				
	    				 if(!(highJSON.isNull("value"))) {
	    					 highQuantity.setValue(highJSON.getDouble("value"));
	    				 }
	    				 if(!(highJSON.isNull("unit"))) {
	    					 highQuantity.setUnit(highJSON.getString("unit"));
	    				 }
	    				 if(!(highJSON.isNull("system"))) {
	    					 highQuantity.setSystem(highJSON.getString("system"));
	    				 }
	    				 if(!(highJSON.isNull("code"))) {
	    					 highQuantity.setCode(highJSON.getString("code"));
	    				 }
	    				theRange.setHigh(highQuantity);
	    			}
	    			thePrediction.setWhen(theRange);
	    		}
	    		
	    		if(!(predictionJSON.getJSONObject(i).isNull("relativeRisk"))) {
	    			DecimalType relativeRiskVal =new DecimalType(predictionJSON.getJSONObject(i).getDouble("relativeRisk"));
	    			thePrediction.setRelativeRiskElement(relativeRiskVal);
	    		}
	    		
	    		if(!(predictionJSON.getJSONObject(i).isNull("rationale"))){
	    			thePrediction.setRationale(predictionJSON.getJSONObject(i).getString("rationale"));
    			}
	    		predictionList.add(thePrediction);
	    	}
	    	riskAssessment.setPrediction(predictionList);
	    }
			    
		//set mitigation
	    if(!(riskAssessmentJSON.isNull("mitigation"))) {
	    	riskAssessment.setMitigation(riskAssessmentJSON.getString("mitigation"));    		   
	    }
	    return riskAssessment;
	}
}
