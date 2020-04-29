 
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.service.ConditionService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ConditionResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Condition";
	public static final String VERSION_ID = "1.0";
	AbstractApplicationContext context;
	ConditionService service;

	public ConditionResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		service = (ConditionService) context.getBean("conditionService");
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Condition.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Condition/1/_history/4
	 * 
	 * @param theId : Id of the Condition
	 * @return : Object of Condition information
	 */
	@Read(version = true)
	public Condition readOrVread(@IdParam IdType theId) {
		String id;
		DafCondition dafCondition;
		try {
			id = theId.getIdPart();
		} catch (NumberFormatException e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown
			 * resource
			 */
			throw new ResourceNotFoundException(theId);
		}
		if (theId.hasVersionIdPart()) {
			// this is a vread
			dafCondition = service.getConditionByVersionId(id, theId.getVersionIdPart());

		} else {

			dafCondition = service.getConditionById(id);
		}

		return createConditionObject(dafCondition);
	}

	/**
	 * The history operation retrieves a historical collection of all versions of a
	 * single resource (instance history). History methods must be annotated with
	 * the @History annotation.It supports Instance History method.
	 * "type=Condition.class". Instance level (history of a specific resource instance
	 * by type and ID) The method must have a parameter annotated with the @IdParam
	 * annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server
	 * name>/<context>/fhir/Condition/1/_history
	 * 
	 * @param theId : ID of the Condition
	 * @return : List of Condition's
	 */
	@History()
	public List<Condition> getConditionHistoryById(@IdParam IdType theId) {

		String id;
		try {
			id = theId.getIdPart();
		} catch (NumberFormatException e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown
			 * resource
			 */
			throw new ResourceNotFoundException(theId);
		}

		List<DafCondition> dafConditionList = service.getConditionHistoryById(id);
		List<Condition> conditionList = new ArrayList<Condition>();
		for (DafCondition dafCondition : dafConditionList) {
			conditionList.add(createConditionObject(dafCondition));
		}
		return conditionList;
	}

	/**
	 * The "@Search" annotation indicates that this method supports the search
	 * operation. You may have many different method annotated with this annotation,
	 * to support many different search criteria. The search operation returns a
	 * bundle with zero-to-many resources of a given type, matching a given set of
	 * parameters.
	 * 
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theClinicalStatus
	 * @param theVerificationStatus
	 * @param theCategory
	 * @param theCode
	 * @param theBodySite
	 * @param theSubject
	 * @param theEncounter
	 * @param theOnsetAge
	 * @param theOnsetDate
	 * @param theAbatementAge
	 * @param theAbatementDate
	 * @param theAbatementString
	 * @param theRecordedDate
	 * @param theOnsetAge
	 * @param theStage
	 * @param theEvidence
	 * @param theIncludes
	 * @param theRevIncludes
	 * @param theSort
	 * @param theCount
	 *
	 * @return
	 */
	@Search()
	public IBundleProvider search(
		javax.servlet.http.HttpServletRequest theServletRequest,

		@Description(shortDefinition = "The resource identity")
		@OptionalParam(name = Condition.SP_RES_ID) 
		StringAndListParam theId,
		
		@Description(shortDefinition = "An Condition identifier")
		@OptionalParam(name = Condition.SP_IDENTIFIER)
		TokenAndListParam theIdentifier,

		@Description(shortDefinition = "The clinical status of the condition") 
		@OptionalParam(name = Condition.SP_CLINICAL_STATUS) 
		TokenAndListParam theClinicalStatus,

		@Description(shortDefinition = "The verification status to support the clinical status of the condition") 
		@OptionalParam(name = Condition.SP_VERIFICATION_STATUS) 
		TokenAndListParam theVerificationStatus,

		@Description(shortDefinition = "A category assigned to the condition") 
		@OptionalParam(name = Condition.SP_CATEGORY) 
		TokenAndListParam theCategory,

		@Description(shortDefinition = "A subjective assessment of the severity of the condition as evaluated by the clinician") 
		@OptionalParam(name = Condition.SP_SEVERITY) 
		TokenAndListParam theSeverity,

		@Description(shortDefinition = "Identification of the condition, problem or diagnosis") 
		@OptionalParam(name = Condition.SP_CODE)
		TokenAndListParam theCode,

		@Description(shortDefinition = "The anatomical location where this condition manifests itself")
		@OptionalParam(name = Condition.SP_BODY_SITE) 
		TokenAndListParam theBodySite,

		@Description(shortDefinition = "Indicates the patient or group who the condition record is associated with")
		@OptionalParam(name = Condition.SP_SUBJECT)
		ReferenceAndListParam theSubject,
		

		@Description(shortDefinition = "Indicates the patient or group who the condition record is associated with")
		@OptionalParam(name = Condition.SP_PATIENT)
		ReferenceAndListParam thePatient,

		@Description(shortDefinition = "The Encounter during which this Condition was created")
		@OptionalParam(name = Condition.SP_ENCOUNTER)
		ReferenceAndListParam theEncounter,

		@Description(shortDefinition = "Age is generally used when the patient reports an age at which the Condition began to occur")
		@OptionalParam(name = Condition.SP_ONSET_AGE)
		QuantityAndListParam theOnsetAge,

		@Description(shortDefinition = "Estimated or actual date or date-time the condition began")
		@OptionalParam(name = Condition.SP_ONSET_DATE) 
		DateRangeParam theOnsetDate,

		@Description(shortDefinition = "The age or estimated age that the condition resolved or went into remission")
		@OptionalParam(name = Condition.SP_ABATEMENT_AGE)
		QuantityAndListParam theAbatementAge,

		@Description(shortDefinition = "The date or estimated date that the condition resolved or went into remission.") 
		@OptionalParam(name = Condition.SP_ABATEMENT_DATE)
		DateRangeParam theAbatementDate,

		@Description(shortDefinition = " When abatementString exists, it implies the condition is abated.")
		@OptionalParam(name = Condition.SP_ABATEMENT_STRING)
		StringAndListParam theAbatementString,

		@Description(shortDefinition = "The recordedDate represents when this particular Condition record was created in the system") 
		@OptionalParam(name = Condition.SP_RECORDED_DATE) 
		DateRangeParam theRecordedDate,

		@Description(shortDefinition = "Individual who is making the condition statement") 
		@OptionalParam(name = Condition.SP_ASSERTER) 
		ReferenceAndListParam theAsserter,

		@Description(shortDefinition = "Clinical stage or grade of a condition. May include formal severity assessments") 
		@OptionalParam(name = Condition.SP_STAGE) 
		TokenAndListParam theStage,

		@Description(shortDefinition = "Supporting evidence / manifestations that are the basis of the Condition's verification status") 
		@OptionalParam(name = Condition.SP_EVIDENCE) 
		TokenAndListParam theEvidence,

		@IncludeParam(allow = {"*"})
		Set<Include> theIncludes,

		@IncludeParam(reverse=true, allow= {"*"})
		Set<Include> theRevIncludes,



		@Sort SortSpec theSort,
		@Count Integer theCount) {
		
		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(Condition.SP_RES_ID, theId);
		paramMap.add(Condition.SP_IDENTIFIER, theIdentifier);
		paramMap.add(Condition.SP_CLINICAL_STATUS, theClinicalStatus);
		paramMap.add(Condition.SP_VERIFICATION_STATUS, theVerificationStatus);
		paramMap.add(Condition.SP_CATEGORY, theCategory);
		paramMap.add(Condition.SP_SEVERITY, theSeverity);
		paramMap.add(Condition.SP_CODE, theCode);
		paramMap.add(Condition.SP_BODY_SITE, theBodySite);
		paramMap.add(Condition.SP_SUBJECT, theSubject);
		paramMap.add(Condition.SP_PATIENT, thePatient);
		paramMap.add(Condition.SP_ENCOUNTER, theEncounter);
		paramMap.add(Condition.SP_ONSET_AGE, theOnsetAge);
		paramMap.add(Condition.SP_ONSET_DATE, theOnsetDate);
		paramMap.add(Condition.SP_ABATEMENT_AGE, theAbatementAge);
		paramMap.add(Condition.SP_ABATEMENT_DATE, theAbatementDate);
		paramMap.add(Condition.SP_ABATEMENT_STRING, theAbatementString);
		paramMap.add(Condition.SP_RECORDED_DATE, theRecordedDate);
		paramMap.add(Condition.SP_ASSERTER, theAsserter);
		paramMap.add(Condition.SP_STAGE, theStage);
		paramMap.add(Condition.SP_EVIDENCE, theEvidence);
		paramMap.setSort(theSort);
		paramMap.setCount(theCount);

		final List<DafCondition> results = service.search(paramMap);
		return new IBundleProvider() {
			final InstantDt published = InstantDt.withCurrentTime();

			@Override
			public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
				List<IBaseResource> conditionList = new ArrayList<IBaseResource>();
				List<String> ids = new ArrayList<String>();
				for (DafCondition dafCondition : results) {
					Condition condition = createConditionObject(dafCondition);
					conditionList.add(condition);
					ids.add(((IdType)condition.getIdElement()).getResourceType()+"/"+((IdType)condition.getIdElement()).getIdPart());
				}
				if(theRevIncludes.size() >0 ){
					conditionList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
				}

				return conditionList;
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
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	/**
	 * This method converts DafDocumentReference object to DocumentReference object
	 */
	private Condition createConditionObject(DafCondition dafCondition) {
		Condition condition = new Condition();
		JSONObject conditionJSON = new JSONObject(dafCondition.getData());

		// Set version
        if(!(conditionJSON.isNull("meta"))) {
        	if(!(conditionJSON.getJSONObject("meta").isNull("versionId"))) {
        		condition.setId(new IdType(RESOURCE_TYPE, conditionJSON.getString("id") + "", conditionJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				condition.setId(new IdType(RESOURCE_TYPE, conditionJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
        	condition.setId(new IdType(RESOURCE_TYPE, conditionJSON.getString("id") + "", VERSION_ID));
        }
        
        //Set identifier
        if(!(conditionJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = conditionJSON.getJSONArray("identifier");
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
        	condition.setIdentifier(identifiers);
        }

		// set clinical-status
		if (!(conditionJSON.isNull("clinicalStatus"))) {
			JSONObject clinicalStatusJSON = conditionJSON.getJSONObject("clinicalStatus");
			CodeableConcept theClinicalStatus = new CodeableConcept();
			if (!(clinicalStatusJSON.isNull("coding"))) {
				JSONArray clinicalCodingJSON = clinicalStatusJSON.getJSONArray("coding");
				int noOfCoding = clinicalCodingJSON.length();
				List<Coding> codingDtList = new ArrayList<Coding>();
				for (int i = 0; i < noOfCoding; i++) {
					Coding theCoding = new Coding();

					if (!(clinicalCodingJSON.getJSONObject(i).isNull("system"))) {
						theCoding.setSystem(clinicalCodingJSON.getJSONObject(i).getString("system"));
					}
					if (!(clinicalCodingJSON.getJSONObject(i).isNull("code"))) {
						theCoding.setCode(clinicalCodingJSON.getJSONObject(i).getString("code"));
					}
					if (!(clinicalCodingJSON.getJSONObject(i).isNull("display"))) {
						theCoding.setDisplay(clinicalCodingJSON.getJSONObject(i).getString("display"));
					}
					codingDtList.add(theCoding);
				}
				theClinicalStatus.setCoding(codingDtList);
			}
			condition.setClinicalStatus(theClinicalStatus);
		}

		// set verification-status
		if (!(conditionJSON.isNull("verificationStatus"))) {
			JSONObject verificationStatusJSON = conditionJSON.getJSONObject("verificationStatus");
			CodeableConcept theVerificationStatus = new CodeableConcept();
			if (!(verificationStatusJSON.isNull("coding"))) {
				JSONArray clinicalCodingJSON = verificationStatusJSON.getJSONArray("coding");
				int noOfCoding = clinicalCodingJSON.length();
				List<Coding> codingDtList = new ArrayList<Coding>();

				for (int i = 0; i < noOfCoding; i++) {
					Coding theCoding = new Coding();

					if (!(clinicalCodingJSON.getJSONObject(i).isNull("system"))) {
						theCoding.setSystem(clinicalCodingJSON.getJSONObject(i).getString("system"));
					}
					if (!(clinicalCodingJSON.getJSONObject(i).isNull("code"))) {
						theCoding.setCode(clinicalCodingJSON.getJSONObject(i).getString("code"));
					}
					if (!(clinicalCodingJSON.getJSONObject(i).isNull("display"))) {
						theCoding.setDisplay(clinicalCodingJSON.getJSONObject(i).getString("display"));
					}
					codingDtList.add(theCoding);
				}
				theVerificationStatus.setCoding(codingDtList);
			}
			condition.setVerificationStatus(theVerificationStatus);
		}

		// set severity
		if (!(conditionJSON.isNull("severity"))) {
			JSONObject severityJSON = conditionJSON.getJSONObject("severity");
			CodeableConcept theSeverity = new CodeableConcept();
			if (!(severityJSON.isNull("coding"))) {
				JSONArray codingJSON = severityJSON.getJSONArray("coding");
				int noOfCoding = codingJSON.length();
				List<Coding> codingDtList = new ArrayList<Coding>();
				for (int i = 0; i < noOfCoding; i++) {
					Coding theCoding = new Coding();
					if (!(codingJSON.getJSONObject(i).isNull("system"))) {
						theCoding.setSystem(codingJSON.getJSONObject(i).getString("system"));
					}
					if (!(codingJSON.getJSONObject(i).isNull("code"))) {
						theCoding.setCode(codingJSON.getJSONObject(i).getString("code"));
					}
					if (!(codingJSON.getJSONObject(i).isNull("display"))) {
						theCoding.setDisplay(codingJSON.getJSONObject(i).getString("display"));
					}
					codingDtList.add(theCoding);
				}
				theSeverity.setCoding(codingDtList);
			}
			condition.setSeverity(theSeverity);
		}

		// set code
		if (!(conditionJSON.isNull("code"))) {
			JSONObject codeJSON = conditionJSON.getJSONObject("code");
			CodeableConcept theCode = new CodeableConcept();
			if (!(codeJSON.isNull("coding"))) {
				JSONArray codingJSON = codeJSON.getJSONArray("coding");
				int noOfCoding = codingJSON.length();
				List<Coding> codingDtList = new ArrayList<Coding>();
				for (int i = 0; i < noOfCoding; i++) {
					Coding theCoding = new Coding();
					if (!(codingJSON.getJSONObject(i).isNull("system"))) {
						theCoding.setSystem(codingJSON.getJSONObject(i).getString("system"));
					}
					if (!(codingJSON.getJSONObject(i).isNull("code"))) {
						theCoding.setCode(codingJSON.getJSONObject(i).getString("code"));
					}
					if (!(codingJSON.getJSONObject(i).isNull("display"))) {
						theCoding.setDisplay(codingJSON.getJSONObject(i).getString("display"));
					}
					codingDtList.add(theCoding);
				}
				theCode.setCoding(codingDtList);
			}
			condition.setCode(theCode);
		}

		// set encounter
		if (!(conditionJSON.isNull("encounter"))) {
			Reference theEncounter = new Reference();
			if (!(conditionJSON.getJSONObject("encounter").isNull("reference"))) {
				theEncounter.setReference(conditionJSON.getJSONObject("encounter").getString("reference"));
			}
			if (!(conditionJSON.getJSONObject("encounter").isNull("display"))) {
				theEncounter.setDisplay(conditionJSON.getJSONObject("encounter").getString("display"));
			}
			if (!(conditionJSON.getJSONObject("encounter").isNull("type"))) {
				theEncounter.setType(conditionJSON.getJSONObject("encounter").getString("type"));
			}
			condition.setEncounter(theEncounter);
		}

		// set subject
		if (!(conditionJSON.isNull("subject"))) {
			Reference theSubject = new Reference();
			if (!(conditionJSON.getJSONObject("subject").isNull("reference"))) {
				theSubject.setReference(conditionJSON.getJSONObject("subject").getString("reference"));
			}
			if (!(conditionJSON.getJSONObject("subject").isNull("display"))) {
				theSubject.setDisplay(conditionJSON.getJSONObject("subject").getString("display"));
			}
			if (!(conditionJSON.getJSONObject("subject").isNull("type"))) {
				theSubject.setType(conditionJSON.getJSONObject("subject").getString("type"));
			}
			condition.setSubject(theSubject);
		}

		// set asserter
		if (!(conditionJSON.isNull("asserter"))) {
			Reference theAsserter = new Reference();
			if (!(conditionJSON.getJSONObject("asserter").isNull("reference"))) {
				theAsserter.setReference(conditionJSON.getJSONObject("asserter").getString("reference"));
			}
			if (!(conditionJSON.getJSONObject("asserter").isNull("display"))) {
				theAsserter.setDisplay(conditionJSON.getJSONObject("asserter").getString("display"));
			}
			if (!(conditionJSON.getJSONObject("asserter").isNull("type"))) {
				theAsserter.setType(conditionJSON.getJSONObject("asserter").getString("type"));
			}
			condition.setAsserter(theAsserter);
		}

		// set abatementString
		if (!conditionJSON.isNull("abatementString")) {
			StringType theAbatementString = new StringType();
			theAbatementString.setValue(conditionJSON.getString("abatementString"));
			condition.setAbatement(theAbatementString);
		}

		// set category
		if (!(conditionJSON.isNull("category"))) {
			JSONArray categoryJson = conditionJSON.getJSONArray("category");
			int noOfCategory = categoryJson.length();
			List<CodeableConcept> categoryDtList = new ArrayList<>();
			for (int i = 0; i < noOfCategory; i++) {
				CodeableConcept theCategory = new CodeableConcept();
				if (!(categoryJson.getJSONObject(i).isNull("coding"))) {
					JSONArray categoryCodingJSON = categoryJson.getJSONObject(i).getJSONArray("coding");
					int noOfCoding = categoryCodingJSON.length();
					List<Coding> codingDtList = new ArrayList<>();
					for (int codingIndex = 0; codingIndex < noOfCoding; codingIndex++) {
						Coding theCoding = new Coding();
						if (!(categoryCodingJSON.getJSONObject(codingIndex).isNull("system"))) {
							theCoding.setSystem(categoryCodingJSON.getJSONObject(codingIndex).getString("system"));
						}
						if (!(categoryCodingJSON.getJSONObject(codingIndex).isNull("code"))) {
							theCoding.setCode(categoryCodingJSON.getJSONObject(codingIndex).getString("code"));
						}
						if (!(categoryCodingJSON.getJSONObject(codingIndex).isNull("display"))) {
							theCoding.setDisplay(categoryCodingJSON.getJSONObject(codingIndex).getString("display"));
						}
						codingDtList.add(theCoding);
					}
					theCategory.setCoding(codingDtList);
				}
				categoryDtList.add(theCategory);
			}
			condition.setCategory(categoryDtList);
		}

		// set bodySite
		if (!(conditionJSON.isNull("bodySite"))) {
			JSONArray bodySiteJSON = conditionJSON.getJSONArray("bodySite");
			int noOfBodySites = bodySiteJSON.length();
			List<CodeableConcept> bodySiteDtList = new ArrayList<CodeableConcept>();
			for (int i = 0; i < noOfBodySites; i++) {
				CodeableConcept theBodySite = new CodeableConcept();
				if (!(bodySiteJSON.getJSONObject(i).isNull("coding"))) {
					JSONArray bodySiteCodingJSON = bodySiteJSON.getJSONObject(i).getJSONArray("coding");
					int noOfCoding = bodySiteCodingJSON.length();
					List<Coding> codingDtList = new ArrayList<>();
					for (int j = 0; j < noOfCoding; j++) {
						Coding theCoding = new Coding();
						if (!(bodySiteCodingJSON.getJSONObject(j).isNull("system"))) {
							theCoding.setSystem(bodySiteCodingJSON.getJSONObject(j).getString("system"));
						}
						if (!(bodySiteCodingJSON.getJSONObject(j).isNull("code"))) {
							theCoding.setCode(bodySiteCodingJSON.getJSONObject(j).getString("code"));
						}
						if (!(bodySiteCodingJSON.getJSONObject(j).isNull("display"))) {
							theCoding.setDisplay(bodySiteCodingJSON.getJSONObject(j).getString("display"));
						}
						codingDtList.add(theCoding);
					}
					theBodySite.setCoding(codingDtList);
				}
				bodySiteDtList.add(theBodySite);
			}
			condition.setBodySite(bodySiteDtList);
		}

		// set onsetDateTime
		if (!(conditionJSON.isNull("onsetDateTime"))) {		
			DateTimeType onsetDateTime = new DateTimeType();
        	Date theDate = CommonUtil.convertStringToDate(conditionJSON.getString("onsetDateTime"));
        	onsetDateTime.setValue(theDate);
        	condition.setOnset(onsetDateTime);
		}

		// set abatementDateTime
		if (!(conditionJSON.isNull("abatementDateTime"))) {
			DateTimeType abatementDateTime = new DateTimeType();
        	Date date = CommonUtil.convertStringToDate(conditionJSON.getString("abatementDateTime"));
        	abatementDateTime.setValue(date);
        	condition.setAbatement(abatementDateTime);
		}

		// set recordedDate
		if (!(conditionJSON.isNull("recordedDate"))) {
			String dateInStr = conditionJSON.getString("recordedDate");
			Date theRecordedDate = CommonUtil.convertStringToDate(dateInStr);
			condition.setRecordedDate(theRecordedDate);
		}

		// set abatement-age
		if (!(conditionJSON.isNull("abatementAge"))) {
			JSONObject abatementJSONObj = conditionJSON.getJSONObject("abatementAge");
			Age theAbatementAge = new Age();

			if (!(abatementJSONObj.isNull("unit"))) {
				theAbatementAge.setUnit(abatementJSONObj.getString("unit"));
			}
			if (!(abatementJSONObj.isNull("value"))) {
				theAbatementAge.setValue(abatementJSONObj.getLong("value"));
			}
			if (!(abatementJSONObj.isNull("system"))) {
				theAbatementAge.setSystem(abatementJSONObj.getString("system"));
			}
			if (!(abatementJSONObj.isNull("code"))) {
				theAbatementAge.setCode(abatementJSONObj.getString("code"));
			}
			condition.setAbatement(theAbatementAge);
		}

		// set onset-age
		if (!(conditionJSON.isNull("onsetAge"))) {
			JSONObject onsetJSONObj = conditionJSON.getJSONObject("onsetAge");
			Age theOnsetAge = new Age();

			if (!(onsetJSONObj.isNull("unit"))) {
				theOnsetAge.setUnit(onsetJSONObj.getString("unit"));
			}
			if (!(onsetJSONObj.isNull("value"))) {
				theOnsetAge.setValue(onsetJSONObj.getDouble("value"));
			}
			if (!(onsetJSONObj.isNull("system"))) {
				theOnsetAge.setSystem(onsetJSONObj.getString("system"));
			}
			if (!(onsetJSONObj.isNull("code"))) {
				theOnsetAge.setCode(onsetJSONObj.getString("code"));
			}
			condition.setOnset(theOnsetAge);
		}
		return condition;
	}
}
