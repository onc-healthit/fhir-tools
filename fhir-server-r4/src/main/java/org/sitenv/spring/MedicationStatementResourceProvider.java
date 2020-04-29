package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent;
import org.hl7.fhir.r4.model.Timing.TimingRepeatComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedicationStatement;
import org.sitenv.spring.service.MedicationStatementService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MedicationStatementResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "MedicationStatement";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    MedicationStatementService service;
    
    public MedicationStatementResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (MedicationStatementService) context.getBean("medicationStatementService");
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return MedicationStatement.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationStatement/1/_history/3.0
	 * @param theId : Id of the MedicationStatement
	 * @return : Object of MedicationStatement information
	 */
	@Read(version=true)
    public MedicationStatement readOrVread(@IdParam IdType theId) {
		String id;
		DafMedicationStatement dafMedicationStatement;
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
			dafMedicationStatement = service.getMedicationStatementByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			dafMedicationStatement = service.getMedicationStatementById(id);
		}
		return createMedicationStatementObject(dafMedicationStatement);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=MedicationStatement.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationStatement/1/_history
	 * @param theId : ID of the MedicationStatement
	 * @return : List of MedicationStatement's
	 */
	@History()
    public List<MedicationStatement> getMedicationStatementHistoryById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafMedicationStatement> dafMedicationStatementList = service.getMedicationStatementHistoryById(id);
        
        List<MedicationStatement> medicationStatementList = new ArrayList<MedicationStatement>();
        for (DafMedicationStatement dafMedicationStatement : dafMedicationStatementList) {
        	medicationStatementList.add(createMedicationStatementObject(dafMedicationStatement));
        }
        
        return medicationStatementList;
	}
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theCategory
	 * @param theSource
	 * @param thePartOf
	 * @param theMedication
	 * @param theContext
	 * @param thePatient
	 * @param theSubject
	 * @param theCode
	 * @param theEffective
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
    public IBundleProvider search(
	    javax.servlet.http.HttpServletRequest theServletRequest,
	
	    @Description(shortDefinition = "The resource identity")
	    @OptionalParam(name = MedicationStatement.SP_RES_ID)
	    TokenAndListParam theId,
	
	    @Description(shortDefinition = "A MedicationStatement identifier")
	    @OptionalParam(name = MedicationStatement.SP_IDENTIFIER)
	    TokenAndListParam theIdentifier,
	    
	    @Description(shortDefinition = "Who or where the information in the statement came from")
	    @OptionalParam(name = MedicationStatement.SP_STATUS)
	    TokenAndListParam theStatus,
	    
	    @Description(shortDefinition = "Returns statements of this category of medicationstatement")
	    @OptionalParam(name = MedicationStatement.SP_CATEGORY)
	    TokenAndListParam theCategory,
	    
	    @Description(shortDefinition = "Who or where the information in the statement came from")
	    @OptionalParam(name = MedicationStatement.SP_SOURCE)
	    ReferenceAndListParam theSource,
	    
	    @Description(shortDefinition = "Returns statements that are part of another event.")
	    @OptionalParam(name = MedicationStatement.SP_PART_OF)
	    ReferenceAndListParam thePartOf,
	    
	    @Description(shortDefinition = "Return statements of this medication reference")
	    @OptionalParam(name = MedicationStatement.SP_MEDICATION)
	    ReferenceAndListParam theMedication,
	    
	    @Description(shortDefinition = "Returns statements for a specific context (episode or episode of Care).")
	    @OptionalParam(name = MedicationStatement.SP_CONTEXT)
	    ReferenceAndListParam theContext,
	    
	    @Description(shortDefinition = "Returns statements for a specific patient.")
	    @OptionalParam(name = MedicationStatement.SP_PATIENT)
	    ReferenceAndListParam thePatient,
	    
	    @Description(shortDefinition = "The identity of a patient, animal or group to list statements for")
	    @OptionalParam(name = MedicationStatement.SP_SUBJECT)
	    ReferenceAndListParam theSubject,
	    
	    @Description(shortDefinition = "Return statements of this medication code")
	    @OptionalParam(name = MedicationStatement.SP_CODE)
	    TokenAndListParam theCode,
	    
	    @Description(shortDefinition = "Date when patient was taking (or not taking) the medication")
	    @OptionalParam(name = MedicationStatement.SP_EFFECTIVE)
	    DateRangeParam theEffective,
	
	    @IncludeParam(allow = {"*"})
	    Set<Include> theIncludes,
	
	    @Sort
	    SortSpec theSort,
	
	    @Count
	    Integer theCount) {
	
	        SearchParameterMap paramMap = new SearchParameterMap();
	        paramMap.add(MedicationStatement.SP_RES_ID, theId);
	        paramMap.add(MedicationStatement.SP_IDENTIFIER, theIdentifier);
	        paramMap.add(MedicationStatement.SP_STATUS, theStatus);
	        paramMap.add(MedicationStatement.SP_CONTEXT, theContext);
	        paramMap.add(MedicationStatement.SP_PATIENT, thePatient);
	        paramMap.add(MedicationStatement.SP_MEDICATION, theMedication);
	        paramMap.add(MedicationStatement.SP_CODE, theCode);
	        paramMap.add(MedicationStatement.SP_EFFECTIVE, theEffective);
	        paramMap.add(MedicationStatement.SP_SUBJECT, theSubject);
	        paramMap.add(MedicationStatement.SP_PART_OF, thePartOf);
	        paramMap.add(MedicationStatement.SP_CATEGORY, theCategory);
	        paramMap.add(MedicationStatement.SP_SOURCE, theSource);
	
	        paramMap.setIncludes(theIncludes);
	        paramMap.setSort(theSort);
	        paramMap.setCount(theCount);
	        
	        final List<DafMedicationStatement> results = service.search(paramMap);
	
	        return new IBundleProvider() {
	            final InstantDt published = InstantDt.withCurrentTime();
	            @Override
	            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
	                List<IBaseResource> medicationStatementList = new ArrayList<IBaseResource>();
	                for(DafMedicationStatement dafMedicationStatement : results){
	                	medicationStatementList.add(createMedicationStatementObject(dafMedicationStatement));
	                }
	                return medicationStatementList;
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
     * This method converts DafDocumentReference object to medicationstatement object
     * @param dafMedicationStatement : DafDocumentReference medicationstatement object
     * @return : DocumentReference medicationstatement object
     */
    private MedicationStatement createMedicationStatementObject(DafMedicationStatement dafMedicationStatement) {
    	MedicationStatement medicationStatement = new MedicationStatement();
        JSONObject medicationStatementJSON = new JSONObject(dafMedicationStatement.getData());

        // Set version
        if(!(medicationStatementJSON.isNull("meta"))) {
        	if(!(medicationStatementJSON.getJSONObject("meta").isNull("versionId"))) {
                medicationStatement.setId(new IdType(RESOURCE_TYPE, medicationStatementJSON.getString("id") + "", medicationStatementJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				medicationStatement.setId(new IdType(RESOURCE_TYPE, medicationStatementJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
            medicationStatement.setId(new IdType(RESOURCE_TYPE, medicationStatementJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(medicationStatementJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = medicationStatementJSON.getJSONArray("identifier");
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
        	medicationStatement.setIdentifier(identifiers);
        }
        //Set status
        if(!(medicationStatementJSON.isNull("status"))) {
        	medicationStatement.setStatus(MedicationStatement.MedicationStatementStatus.fromCode(medicationStatementJSON.getString("status")));
        }
        
        //Set subject
        if(!(medicationStatementJSON.isNull("subject"))) {
        	JSONObject subjectJSON = medicationStatementJSON.getJSONObject("subject");
        	Reference theSubject = new Reference();
        	if(!subjectJSON.isNull("reference")) {
        		theSubject.setReference(subjectJSON.getString("reference"));
        	}
        	if(!subjectJSON.isNull("display")) {
        		theSubject.setDisplay(subjectJSON.getString("display"));
        	}
        	if(!subjectJSON.isNull("type")) {
        		theSubject.setType(subjectJSON.getString("type"));
        	}
        	medicationStatement.setSubject(theSubject);
        }
        //Set context
        if(!(medicationStatementJSON.isNull("context"))) {
        	JSONObject contextJSON = medicationStatementJSON.getJSONObject("context");
        	Reference theContext = new Reference();
        	if(!contextJSON.isNull("reference")) {
        		theContext.setReference(contextJSON.getString("reference"));
        	}
        	if(!contextJSON.isNull("display")) {
        		theContext.setDisplay(contextJSON.getString("display"));
        	}
        	if(!contextJSON.isNull("type")) {
        		theContext.setType(contextJSON.getString("type"));
        	}
        	medicationStatement.setContext(theContext);
        }

        //Set partOf
        if(!(medicationStatementJSON.isNull("partOf"))) {
        	JSONArray partOfJSON = medicationStatementJSON.getJSONArray("partOf");
        	int noOfPartOf = partOfJSON.length();
        	List<Reference> partOfList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfPartOf; i++) {
        		Reference thePartOf = new Reference();
        		if(!partOfJSON.getJSONObject(i).isNull("display")) {
            		thePartOf.setDisplay(partOfJSON.getJSONObject(i).getString("display"));
        		}
        		if(!partOfJSON.getJSONObject(i).isNull("type")) {
            		thePartOf.setType(partOfJSON.getJSONObject(i).getString("type"));
        		}
        		if(!partOfJSON.getJSONObject(i).isNull("reference")) {
            		thePartOf.setReference(partOfJSON.getJSONObject(i).getString("reference"));
        		}
        		partOfList.add(thePartOf);
        	}
        	medicationStatement.setPartOf(partOfList);
        }
        
        //Set effectiveDateTime
        if(!(medicationStatementJSON.isNull("effectiveDateTime"))) {
        	DateTimeType theEffectiveDateTime = new DateTimeType();
        	Date effectiveDateTime = CommonUtil.convertStringToDate(medicationStatementJSON.getString("effectiveDateTime"));
        	theEffectiveDateTime.setValue(effectiveDateTime);
        	medicationStatement.setEffective(theEffectiveDateTime);
        }
        //Set basedOn
        if(!(medicationStatementJSON.isNull("basedOn"))) {
        	JSONArray basedOnJSON = medicationStatementJSON.getJSONArray("basedOn");
        	int noOfBasedOn = basedOnJSON.length();
        	List<Reference> basedOnList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfBasedOn; i++) {
        		Reference theBasedOn = new Reference();
        		if(!basedOnJSON.getJSONObject(i).isNull("display")) {
            		theBasedOn.setDisplay(basedOnJSON.getJSONObject(i).getString("display"));
        		}
        		if(!basedOnJSON.getJSONObject(i).isNull("type")) {
            		theBasedOn.setType(basedOnJSON.getJSONObject(i).getString("type"));
        		}
        		if(!basedOnJSON.getJSONObject(i).isNull("reference")) {
            		theBasedOn.setReference(basedOnJSON.getJSONObject(i).getString("reference"));
        		}
        		basedOnList.add(theBasedOn);
        	}
        	medicationStatement.setBasedOn(basedOnList);
        }
        
        //Set medicationCodeableConcept
       	if(!(medicationStatementJSON.isNull("medicationCodeableConcept"))) {
    		JSONObject medicationCodeableConceptJSON = medicationStatementJSON.getJSONObject("medicationCodeableConcept");
    		CodeableConcept theMedicationCodeableConcept = new CodeableConcept();
    		if(!medicationCodeableConceptJSON.isNull("coding")) {
    			JSONArray mCodingJSON = medicationCodeableConceptJSON.getJSONArray("coding");
    			int noOfMCoding = mCodingJSON.length();
    			List<Coding> mCodingList = new ArrayList<Coding>();
    			for(int s = 0; s < noOfMCoding; s++) {
    				Coding theMCoding = new Coding();
    				if(!mCodingJSON.getJSONObject(s).isNull("system")) {
    					theMCoding.setSystem(mCodingJSON.getJSONObject(s).getString("system"));
    				}
    				if(!mCodingJSON.getJSONObject(s).isNull("code")) {
    					theMCoding.setCode(mCodingJSON.getJSONObject(s).getString("code"));
    				}
    				if(!mCodingJSON.getJSONObject(s).isNull("display")) {
    					theMCoding.setDisplay(mCodingJSON.getJSONObject(s).getString("display"));
    				}
    				mCodingList.add(theMCoding);
    			}
    			theMedicationCodeableConcept.setCoding(mCodingList);
    		}
    		if(!medicationCodeableConceptJSON.isNull("text")) {
    			theMedicationCodeableConcept.setText(medicationCodeableConceptJSON.getString("text"));
    		}
    		medicationStatement.setMedication(theMedicationCodeableConcept);
    	}
        
        //Set medicationReference
        if(!(medicationStatementJSON.isNull("medicationReference"))) {
        	JSONObject medicationReferenceJSON = medicationStatementJSON.getJSONObject("medicationReference");
        	Reference theMedicationReference = new Reference();
        	if(!medicationReferenceJSON.isNull("reference")) {
        		theMedicationReference.setReference(medicationReferenceJSON.getString("reference"));
        	}
        	if(!medicationReferenceJSON.isNull("display")) {
        		theMedicationReference.setDisplay(medicationReferenceJSON.getString("display"));
        	}
        	if(!medicationReferenceJSON.isNull("type")) {
        		theMedicationReference.setType(medicationReferenceJSON.getString("type"));
        	}
        	medicationStatement.setMedication(theMedicationReference);
        }
        
        //Set informationSource
        if(!(medicationStatementJSON.isNull("informationSource"))) {
        	JSONObject informationSourceJSON = medicationStatementJSON.getJSONObject("informationSource");
        	Reference theInformationSource = new Reference();
        	if(!informationSourceJSON.isNull("reference")) {
        		theInformationSource.setReference(informationSourceJSON.getString("reference"));
        	}
        	if(!informationSourceJSON.isNull("display")) {
        		theInformationSource.setDisplay(informationSourceJSON.getString("display"));
        	}
        	if(!informationSourceJSON.isNull("type")) {
        		theInformationSource.setType(informationSourceJSON.getString("type"));
        	}
        	medicationStatement.setInformationSource(theInformationSource);
        }
        
        //Set derivedFrom
        if(!(medicationStatementJSON.isNull("derivedFrom"))) {
        	JSONArray derivedFromJSON = medicationStatementJSON.getJSONArray("derivedFrom");
        	int noOfDerivedFrom = derivedFromJSON.length();
        	List<Reference> derivedFromList = new ArrayList<Reference>();
        	for(int i = 0; i < noOfDerivedFrom; i++) {
        		Reference theDerivedFrom = new Reference();
        		if(!derivedFromJSON.getJSONObject(i).isNull("display")) {
            		theDerivedFrom.setDisplay(derivedFromJSON.getJSONObject(i).getString("display"));
        		}
        		if(!derivedFromJSON.getJSONObject(i).isNull("type")) {
            		theDerivedFrom.setType(derivedFromJSON.getJSONObject(i).getString("type"));
        		}
        		if(!derivedFromJSON.getJSONObject(i).isNull("reference")) {
            		theDerivedFrom.setReference(derivedFromJSON.getJSONObject(i).getString("reference"));
        		}
        		derivedFromList.add(theDerivedFrom);
        	}
        	medicationStatement.setDerivedFrom(derivedFromList);
        }
        //Set dateAsserted
        if(!(medicationStatementJSON.isNull("dateAsserted"))) {
        	Date dateAsserted = CommonUtil.convertStringToDate(medicationStatementJSON.getString("dateAsserted"));
        	medicationStatement.setDateAsserted(dateAsserted);
        }
       
        //Set category
        if(!(medicationStatementJSON.isNull("category"))) {
        	JSONObject categoryJSON = medicationStatementJSON.getJSONObject("category");
    		CodeableConcept theCategory = new CodeableConcept();
    		if(!(categoryJSON.isNull("coding"))) {
				JSONArray cCodingJSON = categoryJSON.getJSONArray("coding");
				int noOfCCodings = cCodingJSON.length();
				List<Coding> cCodingList = new ArrayList<Coding>();
				for(int j = 0; j < noOfCCodings; j++) {
					Coding theCCoding = new Coding();

					if(!(cCodingJSON.getJSONObject(j).isNull("system"))) {
						theCCoding.setSystem(cCodingJSON.getJSONObject(j).getString("system"));
					}
					if(!(cCodingJSON.getJSONObject(j).isNull("code"))) {
						theCCoding.setCode(cCodingJSON.getJSONObject(j).getString("code"));
					}
					if(!(cCodingJSON.getJSONObject(j).isNull("display"))) {
						theCCoding.setDisplay(cCodingJSON.getJSONObject(j).getString("display"));
					}
					cCodingList.add(theCCoding);
				}
				theCategory.setCoding(cCodingList);
			}
    		if(!categoryJSON.isNull("text")) {
        		theCategory.setText(categoryJSON.getString("text"));
    		}
    		medicationStatement.setCategory(theCategory);
        }

        //Set note
        if(!(medicationStatementJSON.isNull("note"))) {
        	JSONArray noteJSON = medicationStatementJSON.getJSONArray("note");
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
        	medicationStatement.setNote(noteList);
        }

        //Set reasonCode
        if(!(medicationStatementJSON.isNull("reasonCode"))) {
        	JSONArray  reasonCodeJSON = medicationStatementJSON.getJSONArray("reasonCode");
        	List<CodeableConcept> reasonCodeList = new ArrayList<CodeableConcept>();
        	int noOfReasonCode = reasonCodeJSON.length();
        	for(int r = 0; r < noOfReasonCode; r++) {
        		CodeableConcept theReasonCode = new CodeableConcept();
        		if(!(reasonCodeJSON.getJSONObject(r).isNull("coding"))) {
    				JSONArray rCodingJSON = reasonCodeJSON.getJSONObject(r).getJSONArray("coding");
    				int noOfRCodings = rCodingJSON.length();
    				List<Coding> rCodingList = new ArrayList<Coding>();
    				for(int j = 0; j < noOfRCodings; j++) {
    					Coding theRCoding = new Coding();

    					if(!(rCodingJSON.getJSONObject(j).isNull("system"))) {
    						theRCoding.setSystem(rCodingJSON.getJSONObject(j).getString("system"));
    					}
    					if(!(rCodingJSON.getJSONObject(j).isNull("code"))) {
    						theRCoding.setCode(rCodingJSON.getJSONObject(j).getString("code"));
    					}
    					if(!(rCodingJSON.getJSONObject(j).isNull("display"))) {
    						theRCoding.setDisplay(rCodingJSON.getJSONObject(j).getString("display"));
    					}
    					rCodingList.add(theRCoding);
    				}
    				theReasonCode.setCoding(rCodingList);
    			}
        		if(!reasonCodeJSON.getJSONObject(r).isNull("text")) {
            		theReasonCode.setText(reasonCodeJSON.getJSONObject(r).getString("text"));
        		}
        		reasonCodeList.add(theReasonCode);
        	}
    		medicationStatement.setReasonCode(reasonCodeList);
        }
        
        //Set statusReason
        if(!(medicationStatementJSON.isNull("statusReason"))) {
        	JSONArray  statusReasonJSON = medicationStatementJSON.getJSONArray("statusReason");
        	List<CodeableConcept> statusReasonList = new ArrayList<CodeableConcept>();
        	int noOfStatusReason = statusReasonJSON.length();
        	for(int r = 0; r < noOfStatusReason; r++) {
        		CodeableConcept theStatusReason = new CodeableConcept();
        		if(!(statusReasonJSON.getJSONObject(r).isNull("coding"))) {
    				JSONArray sCodingJSON = statusReasonJSON.getJSONObject(r).getJSONArray("coding");
    				int noOfSCodings = sCodingJSON.length();
    				List<Coding> sCodingList = new ArrayList<Coding>();
    				for(int j = 0; j < noOfSCodings; j++) {
    					Coding theSCoding = new Coding();

    					if(!(sCodingJSON.getJSONObject(j).isNull("system"))) {
    						theSCoding.setSystem(sCodingJSON.getJSONObject(j).getString("system"));
    					}
    					if(!(sCodingJSON.getJSONObject(j).isNull("code"))) {
    						theSCoding.setCode(sCodingJSON.getJSONObject(j).getString("code"));
    					}
    					if(!(sCodingJSON.getJSONObject(j).isNull("display"))) {
    						theSCoding.setDisplay(sCodingJSON.getJSONObject(j).getString("display"));
    					}
    					sCodingList.add(theSCoding);
    				}
    				theStatusReason.setCoding(sCodingList);
    			}
        		if(!statusReasonJSON.getJSONObject(r).isNull("text")) {
            		theStatusReason.setText(statusReasonJSON.getJSONObject(r).getString("text"));
        		}
        		statusReasonList.add(theStatusReason);
        	}
    		medicationStatement.setStatusReason(statusReasonList);
        }
        //Set dosage
        if(!(medicationStatementJSON.isNull("dosage"))) {
        	JSONArray dosageJSON = medicationStatementJSON.getJSONArray("dosage");
        	List<Dosage> dosageList = new ArrayList<Dosage>();
        	int noOfDosages = dosageJSON.length();
        	
        	for(int i = 0; i < noOfDosages; i++) {
        		Dosage theDosage = new Dosage();
        		if(!(dosageJSON.getJSONObject(i).isNull("sequence"))) {
        			theDosage.setSequence(dosageJSON.getJSONObject(i).getInt("sequence"));
        		}
        		if(!(dosageJSON.getJSONObject(i).isNull("text"))) {
        			theDosage.setText(dosageJSON.getJSONObject(i).getString("text"));
        		}
        		if(!(dosageJSON.getJSONObject(i).isNull("asNeededBoolean"))) {
        			BooleanType theBoolean = new BooleanType();
        			theBoolean.setValue(dosageJSON.getJSONObject(i).getBoolean("asNeededBoolean"));
        			theDosage.setAsNeeded(theBoolean);
        		}
        		//Set maxDosePerAdministration
            	if(!(dosageJSON.getJSONObject(i).isNull("maxDosePerAdministration"))) {
            		JSONObject maxDosePerAdministrationJSON = dosageJSON.getJSONObject(i).getJSONObject("maxDosePerAdministration");
            		SimpleQuantity theMaxDose = new SimpleQuantity();
            		if(!(maxDosePerAdministrationJSON.isNull("value"))) {
            			theMaxDose.setValue(maxDosePerAdministrationJSON.getLong("value"));
            		}
            		if(!(maxDosePerAdministrationJSON.isNull("unit"))) {
            			theMaxDose.setUnit(maxDosePerAdministrationJSON.getString("unit"));
            		}
            		if(!(maxDosePerAdministrationJSON.isNull("system"))) {
            			theMaxDose.setSystem(maxDosePerAdministrationJSON.getString("system"));
            		}
            		if(!(maxDosePerAdministrationJSON.isNull("code"))) {
            			theMaxDose.setCode(maxDosePerAdministrationJSON.getString("code"));
            		}
            		theDosage.setMaxDosePerAdministration(theMaxDose);
            	}
            	//Set maxDosePerLifetime
            	if(!(dosageJSON.getJSONObject(i).isNull("maxDosePerLifetime"))) {
            		JSONObject maxDosePerLifetimeJSON = dosageJSON.getJSONObject(i).getJSONObject("maxDosePerLifetime");
            		SimpleQuantity theLifeTime = new SimpleQuantity();
            		if(!(maxDosePerLifetimeJSON.isNull("value"))) {
            			theLifeTime.setValue(maxDosePerLifetimeJSON.getLong("value"));
            		}
            		if(!(maxDosePerLifetimeJSON.isNull("unit"))) {
            			theLifeTime.setUnit(maxDosePerLifetimeJSON.getString("unit"));
            		}
            		if(!(maxDosePerLifetimeJSON.isNull("system"))) {
            			theLifeTime.setSystem(maxDosePerLifetimeJSON.getString("system"));
            		}
            		if(!(maxDosePerLifetimeJSON.isNull("code"))) {
            			theLifeTime.setCode(maxDosePerLifetimeJSON.getString("code"));
            		}
            		theDosage.setMaxDosePerLifetime(theLifeTime);
            	}
            	//Set maxDosePerPeriod
            	if(!(dosageJSON.getJSONObject(i).isNull("maxDosePerPeriod"))) {
            		JSONObject maxDosePerPeriodJSON = dosageJSON.getJSONObject(i).getJSONObject("maxDosePerPeriod");
            		Ratio thePerPeriod = new Ratio();
            		if(!maxDosePerPeriodJSON.isNull("numerator")) {
            			JSONObject numeratorJSON = maxDosePerPeriodJSON.getJSONObject("numerator");
            			Quantity theNumerator = new Quantity();
            			if(!(numeratorJSON.isNull("value"))) {
            				theNumerator.setValue(numeratorJSON.getLong("value"));
                		}
                	 	if(!(numeratorJSON.isNull("system"))) {
                	 		theNumerator.setSystem(numeratorJSON.getString("system"));
                		}
                		if(!(numeratorJSON.isNull("code"))) {
                			theNumerator.setCode(numeratorJSON.getString("code"));
                		}
                		thePerPeriod.setNumerator(theNumerator);
            		}
            		if(!maxDosePerPeriodJSON.isNull("denominator")) {
            			JSONObject denominatorJSON = maxDosePerPeriodJSON.getJSONObject("denominator");
            			Quantity theDenominator = new Quantity();
            			if(!(denominatorJSON.isNull("value"))) {
            				theDenominator.setValue(denominatorJSON.getLong("value"));
                		}
                	 	if(!(denominatorJSON.isNull("system"))) {
                	 		theDenominator.setSystem(denominatorJSON.getString("system"));
                		}
                		if(!(denominatorJSON.isNull("code"))) {
                			theDenominator.setCode(denominatorJSON.getString("code"));
                		}
                		thePerPeriod.setDenominator(theDenominator);
            		}
            		theDosage.setMaxDosePerPeriod(thePerPeriod);
            	}
        		if(!(dosageJSON.getJSONObject(i).isNull("timing"))) {
        			Timing theTiming = new Timing();
        			JSONObject timingJSON = dosageJSON.getJSONObject(i).getJSONObject("timing");
        			if(!timingJSON.isNull("repeat")) {
        				TimingRepeatComponent theRepeat = new TimingRepeatComponent();
        				JSONObject repeatJSON = timingJSON.getJSONObject("repeat");
        				if(!(repeatJSON.isNull("frequency"))) {
        					theRepeat.setFrequency(repeatJSON.getInt("frequency"));
        				}
        				if(!(repeatJSON.isNull("period"))) {
        					theRepeat.setPeriod(repeatJSON.getInt("period"));
        				}
        				if(!(repeatJSON.isNull("periodUnit"))) {
        					theRepeat.setPeriodUnit(Timing.UnitsOfTime.fromCode(repeatJSON.getString("periodUnit")));
        				}
        				if(!(repeatJSON.isNull("periodMax"))) {
        					theRepeat.setPeriodMax(repeatJSON.getLong("periodMax"));
        				}
        				if(!(repeatJSON.isNull("duration"))) {
        					theRepeat.setDuration(repeatJSON.getLong("duration"));
        				}
        				if(!(repeatJSON.isNull("durationUnit"))) {
        					theRepeat.setDurationUnit(Timing.UnitsOfTime.fromCode(repeatJSON.getString("durationUnit")));
        				}
        				if(!(repeatJSON.isNull("boundsPeriod"))) {
        					JSONObject boundsPeriodJSON = repeatJSON.getJSONObject("boundsPeriod");
        					Period theBoundsPeriod = new Period();
        					if(!(boundsPeriodJSON.isNull("start"))) {
        		        		Date start = CommonUtil.convertStringToDate(boundsPeriodJSON.getString("start"));
        		        		theBoundsPeriod.setStart(start);
        		        	}
        		        	if(!(boundsPeriodJSON.isNull("end"))) {
        		        		Date end = CommonUtil.convertStringToDate(boundsPeriodJSON.getString("end"));
        		        		theBoundsPeriod.setEnd(end);
        		        	}
        		        	theRepeat.setBounds(theBoundsPeriod);
        				}
        				theTiming.setRepeat(theRepeat);
        			}
        			theDosage.setTiming(theTiming);
        		}
        		if(!(dosageJSON.getJSONObject(i).isNull("route"))) {
        			JSONObject routeJSON = dosageJSON.getJSONObject(i).getJSONObject("route");
        			CodeableConcept theRoute = new CodeableConcept();
        			if(!(routeJSON.isNull("coding"))) {
        				JSONArray rCodingJSON = routeJSON.getJSONArray("coding");
        				int noOfRCodings = rCodingJSON.length();
        				List<Coding> theRCodingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfRCodings; j++) {
        					Coding theRCoding = new Coding();
        					if(!(rCodingJSON.getJSONObject(j).isNull("system"))) {
        						theRCoding.setSystem(rCodingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(rCodingJSON.getJSONObject(j).isNull("code"))) {
        						theRCoding.setCode(rCodingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(rCodingJSON.getJSONObject(j).isNull("display"))) {
        						theRCoding.setDisplay(rCodingJSON.getJSONObject(j).getString("display"));
        					}
        					theRCodingList.add(theRCoding);
        				}
        				theRoute.setCoding(theRCodingList);
        			}
        			if(!(dosageJSON.getJSONObject(i).isNull("text"))) {
        				theRoute.setText(dosageJSON.getJSONObject(i).getString("text"));
        			}
        			theDosage.setRoute(theRoute);
        		}
        		if(!(dosageJSON.getJSONObject(i).isNull("method"))) {
        			JSONObject methodJSON = dosageJSON.getJSONObject(i).getJSONObject("method");
        			CodeableConcept theMethod = new CodeableConcept();
        			if(!(methodJSON.isNull("coding"))) {
        				JSONArray mCodingJSON = methodJSON.getJSONArray("coding");
        				int noOfMCodings = mCodingJSON.length();
        				List<Coding> theMCodingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfMCodings; j++) {
        					Coding theMCoding = new Coding();
        					if(!(mCodingJSON.getJSONObject(j).isNull("system"))) {
        						theMCoding.setSystem(mCodingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(mCodingJSON.getJSONObject(j).isNull("code"))) {
        						theMCoding.setCode(mCodingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(mCodingJSON.getJSONObject(j).isNull("display"))) {
        						theMCoding.setDisplay(mCodingJSON.getJSONObject(j).getString("display"));
        					}
        					theMCodingList.add(theMCoding);
        				}
        				theMethod.setCoding(theMCodingList);
        			}
        			if(!(dosageJSON.getJSONObject(i).isNull("text"))) {
        				theMethod.setText(dosageJSON.getJSONObject(i).getString("text"));
        			}
        			theDosage.setMethod(theMethod);
        		}
        		if(!(dosageJSON.getJSONObject(i).isNull("site"))) {
        			JSONObject siteJSON = dosageJSON.getJSONObject(i).getJSONObject("site");
        			CodeableConcept theSite = new CodeableConcept();
        			if(!(siteJSON.isNull("coding"))) {
        				JSONArray sCodingJSON = siteJSON.getJSONArray("coding");
        				int noOfSCodings = sCodingJSON.length();
        				List<Coding> theSCodingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfSCodings; j++) {
        					Coding theSCoding = new Coding();
        					if(!(sCodingJSON.getJSONObject(j).isNull("system"))) {
        						theSCoding.setSystem(sCodingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(sCodingJSON.getJSONObject(j).isNull("code"))) {
        						theSCoding.setCode(sCodingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(sCodingJSON.getJSONObject(j).isNull("display"))) {
        						theSCoding.setDisplay(sCodingJSON.getJSONObject(j).getString("display"));
        					}
        					theSCodingList.add(theSCoding);
        				}
        				theSite.setCoding(theSCodingList);
        			}
        			if(!(dosageJSON.getJSONObject(i).isNull("text"))) {
        				theSite.setText(dosageJSON.getJSONObject(i).getString("text"));
        			}
        			theDosage.setSite(theSite);
        		}
        		if(!(dosageJSON.getJSONObject(i).isNull("asNeededCodeableConcept"))) {
        			JSONObject asNeededCodeableConceptJSON = dosageJSON.getJSONObject(i).getJSONObject("asNeededCodeableConcept");
        			CodeableConcept theAsNeededCodeableConcept = new CodeableConcept();
        			if(!(asNeededCodeableConceptJSON.isNull("coding"))) {
        				JSONArray asCodingJSON = asNeededCodeableConceptJSON.getJSONArray("coding");
        				int noOfAsCodings = asCodingJSON.length();
        				List<Coding> theAsCodingList = new ArrayList<Coding>();
        				for(int j = 0; j < noOfAsCodings; j++) {
        					Coding theAsCoding = new Coding();
        					if(!(asCodingJSON.getJSONObject(j).isNull("system"))) {
        						theAsCoding.setSystem(asCodingJSON.getJSONObject(j).getString("system"));
        					}
        					if(!(asCodingJSON.getJSONObject(j).isNull("code"))) {
        						theAsCoding.setCode(asCodingJSON.getJSONObject(j).getString("code"));
        					}
        					if(!(asCodingJSON.getJSONObject(j).isNull("display"))) {
        						theAsCoding.setDisplay(asCodingJSON.getJSONObject(j).getString("display"));
        					}
        					theAsCodingList.add(theAsCoding);
        				}
        				theAsNeededCodeableConcept.setCoding(theAsCodingList);
        			}
        			if(!(dosageJSON.getJSONObject(i).isNull("text"))) {
        				theAsNeededCodeableConcept.setText(dosageJSON.getJSONObject(i).getString("text"));
        			}
        			theDosage.setAsNeeded(theAsNeededCodeableConcept);
        		}
        		//Set additionalInstruction
                if(!(dosageJSON.getJSONObject(i).isNull("additionalInstruction"))) {
                	JSONArray additionalInstructionJSON = dosageJSON.getJSONObject(i).getJSONArray("additionalInstruction");
                	int noOfAdditionalInfo = additionalInstructionJSON.length();
                	List<CodeableConcept> additionalInstructionList = new ArrayList<CodeableConcept>();
                	for(int a = 0; a < noOfAdditionalInfo; a++) {
                		CodeableConcept theAdditionalInstruction = new CodeableConcept();
                		if(!(additionalInstructionJSON.getJSONObject(a).isNull("coding"))) {
            				JSONArray cCodingJSON = additionalInstructionJSON.getJSONObject(a).getJSONArray("coding");
            				int noOfCCodings = cCodingJSON.length();
            				List<Coding> cCodingList = new ArrayList<Coding>();
            				for(int j = 0; j < noOfCCodings; j++) {
            					Coding theCCoding = new Coding();

            					if(!(cCodingJSON.getJSONObject(j).isNull("system"))) {
            						theCCoding.setSystem(cCodingJSON.getJSONObject(j).getString("system"));
            					}
            					if(!(cCodingJSON.getJSONObject(j).isNull("code"))) {
            						theCCoding.setCode(cCodingJSON.getJSONObject(j).getString("code"));
            					}
            					if(!(cCodingJSON.getJSONObject(j).isNull("display"))) {
            						theCCoding.setDisplay(cCodingJSON.getJSONObject(j).getString("display"));
            					}
            					cCodingList.add(theCCoding);
            				}
            				theAdditionalInstruction.setCoding(cCodingList);
            			}
                		if(!additionalInstructionJSON.getJSONObject(a).isNull("text")) {
                    		theAdditionalInstruction.setText(additionalInstructionJSON.getJSONObject(a).getString("text"));
                		}
                		additionalInstructionList.add(theAdditionalInstruction);
                	}
            		theDosage.setAdditionalInstruction(additionalInstructionList);
                }
        		//Set doseAndRate
        		if(!(dosageJSON.getJSONObject(i).isNull("doseAndRate"))) {
        			JSONArray doseAndRateJSON = dosageJSON.getJSONObject(i).getJSONArray("doseAndRate"); 
        			int noOfDoseAndRates = doseAndRateJSON.length(); 
        			List<DosageDoseAndRateComponent> dosageDoseAndRateList = new ArrayList<DosageDoseAndRateComponent>();
        			for(int k = 0; k < noOfDoseAndRates; k++) {
        				DosageDoseAndRateComponent theDosageDoseAndRate = new DosageDoseAndRateComponent();
        				if(!(doseAndRateJSON.getJSONObject(k).isNull("type"))) {
        					JSONObject typeJSON = doseAndRateJSON.getJSONObject(k).getJSONObject("type");
            				CodeableConcept theTType = new CodeableConcept();
        					if(!(typeJSON.isNull("coding"))) {
        						JSONArray tCodingJSON = typeJSON.getJSONArray("coding");
                				int noOfTCodings = tCodingJSON.length();
                				List<Coding> tCodingList = new ArrayList<Coding>(); 
                				for(int l = 0; l < noOfTCodings; l++) {
                					Coding theTCoding = new Coding();
                					if(!(tCodingJSON.getJSONObject(l).isNull("system"))) {
                						theTCoding.setSystem(tCodingJSON.getJSONObject(l).getString("system"));
                					}
                					if(!(tCodingJSON.getJSONObject(l).isNull("code"))) {
                						theTCoding.setCode(tCodingJSON.getJSONObject(l).getString("code"));
                					}
                					if(!(tCodingJSON.getJSONObject(l).isNull("display"))) {
                						theTCoding.setDisplay(tCodingJSON.getJSONObject(l).getString("display"));
                					}
                					if(!(tCodingJSON.getJSONObject(l).isNull("version"))) {
                						theTCoding.setVersion(tCodingJSON.getJSONObject(l).getString("version"));
                					}
                					if(!(tCodingJSON.getJSONObject(l).isNull("userSelected"))) {
                						theTCoding.setUserSelected(tCodingJSON.getJSONObject(l).getBoolean("userSelected"));
                					}
                					tCodingList.add(theTCoding);
                				}
                				theTType.setCoding(tCodingList);
        					} 
        					theDosageDoseAndRate.setType(theTType);
            			}
        				if(!(doseAndRateJSON.getJSONObject(k).isNull("doseQuantity"))) {
        					JSONObject doseQuantityJSON = doseAndRateJSON.getJSONObject(k).getJSONObject("doseQuantity");
                    		SimpleQuantity theDoseQuantity = new SimpleQuantity();
                    		if(!(doseQuantityJSON.isNull("value"))) {
                    			theDoseQuantity.setValue(doseQuantityJSON.getLong("value"));
                    		}
                    		if(!(doseQuantityJSON.isNull("unit"))) {
                    			theDoseQuantity.setUnit(doseQuantityJSON.getString("unit"));
                    		}
                    		if(!(doseQuantityJSON.isNull("system"))) {
                    			theDoseQuantity.setSystem(doseQuantityJSON.getString("system"));
                    		}
                    		if(!(doseQuantityJSON.isNull("code"))) {
                    			theDoseQuantity.setCode(doseQuantityJSON.getString("code"));
                    		}
                    		theDosageDoseAndRate.setDose(theDoseQuantity);
        				}
        				if(!(doseAndRateJSON.getJSONObject(k).isNull("rateRatio"))) {
        					JSONObject rateRatioJSON = doseAndRateJSON.getJSONObject(k).getJSONObject("rateRatio");
                    		Ratio theRateRatio = new Ratio();
                    		if(!rateRatioJSON.isNull("numerator")) {
                    			JSONObject numeratorJSON = rateRatioJSON.getJSONObject("numerator");
                    			Quantity theNumerator = new Quantity();
                    			if(!(numeratorJSON.isNull("value"))) {
                    				theNumerator.setValue(numeratorJSON.getLong("value"));
                        		}
                        	 	if(!(numeratorJSON.isNull("system"))) {
                        	 		theNumerator.setSystem(numeratorJSON.getString("system"));
                        		}
                        		if(!(numeratorJSON.isNull("code"))) {
                        			theNumerator.setCode(numeratorJSON.getString("code"));
                        		}
                        		theRateRatio.setNumerator(theNumerator);
                    		}
                    		if(!rateRatioJSON.isNull("denominator")) {
                    			JSONObject denominatorJSON = rateRatioJSON.getJSONObject("denominator");
                    			Quantity theDenominator = new Quantity();
                    			if(!(denominatorJSON.isNull("value"))) {
                    				theDenominator.setValue(denominatorJSON.getLong("value"));
                        		}
                        	 	if(!(denominatorJSON.isNull("system"))) {
                        	 		theDenominator.setSystem(denominatorJSON.getString("system"));
                        		}
                        		if(!(denominatorJSON.isNull("code"))) {
                        			theDenominator.setCode(denominatorJSON.getString("code"));
                        		}
                        		theRateRatio.setDenominator(theDenominator);
                    		}
                    		theDosageDoseAndRate.setRate(theRateRatio);
        				}
        				if(!(doseAndRateJSON.getJSONObject(k).isNull("doseRange"))) {
        					JSONObject doseRangeJSON = doseAndRateJSON.getJSONObject(k).getJSONObject("doseRange");
                    		Range theDoseRange = new Range();
                    		if(!doseRangeJSON.isNull("low")) {
                    			JSONObject lowJSON = doseRangeJSON.getJSONObject("low");
                    			SimpleQuantity theLow = new SimpleQuantity();
                    			if(!(lowJSON.isNull("value"))) {
                    				theLow.setValue(lowJSON.getLong("value"));
                        		}
                    			if(!(lowJSON.isNull("unit"))) {
                    				theLow.setUnit(lowJSON.getString("unit"));
                        		}
                        	 	if(!(lowJSON.isNull("system"))) {
                        	 		theLow.setSystem(lowJSON.getString("system"));
                        		}
                        		if(!(lowJSON.isNull("code"))) {
                        			theLow.setCode(lowJSON.getString("code"));
                        		}
                        		theDoseRange.setLow(theLow);
                    		}
                    		if(!doseRangeJSON.isNull("high")) {
                    			JSONObject highJSON = doseRangeJSON.getJSONObject("high");
                    			SimpleQuantity theHigh = new SimpleQuantity();
                    			if(!(highJSON.isNull("value"))) {
                    				theHigh.setValue(highJSON.getLong("value"));
                        		}
                    			if(!(highJSON.isNull("unit"))) {
                        	 		theHigh.setUnit(highJSON.getString("unit"));
                        		}
                        	 	if(!(highJSON.isNull("system"))) {
                        	 		theHigh.setSystem(highJSON.getString("system"));
                        		}
                        		if(!(highJSON.isNull("code"))) {
                        			theHigh.setCode(highJSON.getString("code"));
                        		}
                        		theDoseRange.setHigh(theHigh);
                    		}
                    		theDosageDoseAndRate.setDose(theDoseRange);
        				}
        				dosageDoseAndRateList.add(theDosageDoseAndRate);
        			}
        			theDosage.setDoseAndRate(dosageDoseAndRateList);
        		}
        		dosageList.add(theDosage);
        	}
        	medicationStatement.setDosage(dosageList);
        }
        return medicationStatement;
    }
}
