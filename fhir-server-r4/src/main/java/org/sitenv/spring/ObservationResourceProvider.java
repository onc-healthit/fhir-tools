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
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.service.ObservationService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ObservationResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Observation";
	public static final String VERSION_ID = "1.0";
	AbstractApplicationContext context;
	ObservationService service;

	public ObservationResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		service = (ObservationService) context.getBean("observationService");
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Observation.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Observation/1/_history/4
	 * 
	 * @param theId : Id of the observation
	 * @return : Object of observation information
	 */
	@Read(version = true)
	public Observation readOrVread(@IdParam IdType theId) {
		String id;
		DafObservation dafObservation;
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
			dafObservation = service.getObservationByVersionId(id, theId.getVersionIdPart());

		} else {
			// this is a read
			dafObservation = service.getObservationById(id);
		}
		return createObservationObject(dafObservation);
	}

	/**
	 * The history operation retrieves a historical collection of all versions of a
	 * single resource (instance history). History methods must be annotated with
	 * the @History annotation.It supports Instance History method.
	 * "type=Observation.class". Instance level (history of a specific resource
	 * instance by type and ID) The method must have a parameter annotated with
	 * the @IdParam annotation, indicating the ID of the resource for which to
	 * return history. Example URL to invoke this method: http://<server
	 * name>/<context>/fhir/Observation/1/_history
	 * 
	 * @param theId : ID of the observation
	 * @return : List of observation's
	 */
	@History()
	public List<Observation> getObservationHistoryById(@IdParam IdType theId) {

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
		List<DafObservation> dafObservationList = service.getObservationHistoryById(id);

		List<Observation> observationList = new ArrayList<Observation>();
		for (DafObservation dafObservation : dafObservationList) {
			observationList.add(createObservationObject(dafObservation));
		}

		return observationList;
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
	 * @param theDate
	 * @param theSubject
	 * @param theValueQuantity
	 * @param theBasedOn
	 * @param theHasMember
	 * @param thePerformer
	 * @param theDevice
	 * @param theStatus
	 * @param thePartOf
	 * @param theDerivedFrom
	 * @param theCategory
	 * @param theCode
	 * @param theIncludes
	 * @param theRevIncludes
	 * @param thePatient
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(
			javax.servlet.http.HttpServletRequest theServletRequest,

			@Description(shortDefinition = "The resource identity") 
			@OptionalParam(name = Observation.SP_RES_ID) 
			StringAndListParam theId,

			@Description(shortDefinition = "The unique id for a particular observation") 
			@OptionalParam(name = Observation.SP_IDENTIFIER) 
			TokenAndListParam theIdentifier,

			@Description(shortDefinition = "Obtained date/time. If the obtained element is a period, a date that falls in the period") 
			@OptionalParam(name = Observation.SP_DATE)
			DateRangeParam theDate,

			@Description(shortDefinition = "The code of the observation type") 
			@OptionalParam(name = Observation.SP_CODE) 
			TokenAndListParam theCode,

			@Description(shortDefinition = "The subject that the observation is about")
			@OptionalParam(name = Observation.SP_SUBJECT) 
			ReferenceAndListParam theSubject,

			@Description(shortDefinition = "The reason why the expected value in the element Observation.component.value[x] is missing")
			@OptionalParam(name = Observation.SP_COMPONENT_DATA_ABSENT_REASON) 
			TokenAndListParam theComponentDataAbsentReason,

			@Description(shortDefinition = "The value of the observation, if the value is a CodeableConcept") 
			@OptionalParam(name = Observation.SP_VALUE_CONCEPT) 
			TokenAndListParam theValueConcept,

			@Description(shortDefinition = "The value of the observation, if the value is a date or period of time") 
			@OptionalParam(name = Observation.SP_VALUE_DATE)
			DateRangeParam theValueDate,

			@Description(shortDefinition = "The focus of an observation when the focus is not the patient of record.") 
			@OptionalParam(name = Observation.SP_FOCUS)
			ReferenceAndListParam theFocus,

			@Description(shortDefinition = "Related measurements the observation is made from")
			@OptionalParam(name = Observation.SP_DERIVED_FROM)
			ReferenceAndListParam theDerivedFrom,

			@Description(shortDefinition = "Part of referenced event")
			@OptionalParam(name = Observation.SP_PART_OF)
			ReferenceAndListParam thePartOf,

			@Description(shortDefinition = "Related resource that belongs to the Observation group")
			@OptionalParam(name = Observation.SP_HAS_MEMBER)
			ReferenceAndListParam theHasMember,

			@Description(shortDefinition = "Reference to the service request.") 
			@OptionalParam(name = Observation.SP_BASED_ON)
			ReferenceAndListParam theBasedOn,

			@Description(shortDefinition = "The subject that the observation is about (if patient)")
			@OptionalParam(name = Observation.SP_PATIENT)
			ReferenceAndListParam thePatient,

			@Description(shortDefinition = "Specimen used for this observation")
			@OptionalParam(name = Observation.SP_SPECIMEN) 
			ReferenceAndListParam theSpecimen,

			@Description(shortDefinition = "The component code of the observation type") 
			@OptionalParam(name = Observation.SP_COMPONENT_CODE)
			TokenAndListParam thecomponentCode,

			@Description(shortDefinition = "The value of the observation, if the value is a string, and also searches in CodeableConcept.text") 
			@OptionalParam(name = Observation.SP_VALUE_STRING) 
			StringAndListParam theValueString,

			@Description(shortDefinition = "Who performed the observation") 
			@OptionalParam(name = Observation.SP_PERFORMER)
			ReferenceAndListParam thePerformer,

			@Description(shortDefinition = "The code of the observation type or component type")
			@OptionalParam(name = Observation.SP_COMBO_CODE)
			TokenAndListParam theComboCOde,

			@Description(shortDefinition = "The method used for the observation")
			@OptionalParam(name = Observation.SP_METHOD) 
			TokenAndListParam theMethod,

			@Description(shortDefinition = "The value of the observation, if the value is a Quantity, or a SampledData (just search on the bounds of the values in sampled data)")
			@OptionalParam(name = Observation.SP_VALUE_QUANTITY) 
			QuantityAndListParam theValueQuantity,

			@Description(shortDefinition = "The value of the component observation, if the value is a Quantity, or a SampledData (just search on the bounds of the values in sampled data)")
			@OptionalParam(name = Observation.SP_COMPONENT_VALUE_QUANTITY) 
			QuantityAndListParam theComponentValueQuantity,

			@Description(shortDefinition = "The reason why the expected value in the element Observation.value[x] is missing.")
			@OptionalParam(name = Observation.SP_DATA_ABSENT_REASON) 
			TokenAndListParam theDataAbsentReason,

			@Description(shortDefinition = "The value or component value of the observation, if the value is a Quantity, or a SampledData (just search on the bounds of the values in sampled data)") 
			@OptionalParam(name = Observation.SP_COMBO_VALUE_QUANTITY) 
			QuantityAndListParam theComboValueQuantity,

			@Description(shortDefinition = "Encounter related to the observation")
			@OptionalParam(name = Observation.SP_ENCOUNTER) 
			ReferenceAndListParam theEncounter,

			@Description(shortDefinition = "The classification of the type of observation") 
			@OptionalParam(name = Observation.SP_CATEGORY) 
			TokenAndListParam theCategory,

			@Description(shortDefinition = "The status of the observation")
			@OptionalParam(name = Observation.SP_STATUS) 
			TokenAndListParam theStatus,

			@Description(shortDefinition = "The Device that generated the observation data.")
			@OptionalParam(name = Observation.SP_DEVICE) 
			ReferenceAndListParam theDevice,

			@IncludeParam(allow = { "*" })
			Set<Include> theIncludes,

			@IncludeParam(reverse=true, allow= {"*"})
			Set<Include> theRevIncludes,

			@Sort SortSpec theSort,

			@Count Integer theCount) {

				SearchParameterMap paramMap = new SearchParameterMap();
				paramMap.add(Observation.SP_RES_ID, theId);
				paramMap.add(Observation.SP_IDENTIFIER, theIdentifier);
				paramMap.add(Observation.SP_SUBJECT, theSubject);
				paramMap.add(Observation.SP_VALUE_QUANTITY, theValueQuantity);
				paramMap.add(Observation.SP_BASED_ON, theBasedOn);
				paramMap.add(Observation.SP_HAS_MEMBER, theHasMember);
				paramMap.add(Observation.SP_PERFORMER, thePerformer);
				paramMap.add(Observation.SP_DEVICE, theDevice);
				paramMap.add(Observation.SP_STATUS, theStatus);
				paramMap.add(Observation.SP_PART_OF, thePartOf);
				paramMap.add(Observation.SP_DERIVED_FROM, theDerivedFrom);
				paramMap.add(Observation.SP_CATEGORY, theCategory);
				paramMap.add(Observation.SP_CODE, theCode);
				paramMap.add(Observation.SP_PATIENT, thePatient);
				paramMap.add(Observation.SP_DATE, theDate);

				paramMap.setIncludes(theIncludes);
				paramMap.setSort(theSort);
				paramMap.setCount(theCount);
		
				final List<DafObservation> results = service.search(paramMap);
		
				return new IBundleProvider() {
					final InstantDt published = InstantDt.withCurrentTime();
		
					@Override
					public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
						List<IBaseResource> observationList = new ArrayList<IBaseResource>();
						List<String> ids = new ArrayList<String>();
						for (DafObservation dafObservation : results) {
							Observation observation = createObservationObject(dafObservation);
							observationList.add(observation);
							ids.add(((IdType)observation.getIdElement()).getResourceType()+"/"+((IdType)observation.getIdElement()).getIdPart());
						}
						if(theRevIncludes.size() >0 ){
							observationList.addAll(new ProvenanceResourceProvider().getProvenanceByResourceId(ids));
						}
						return observationList;
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
	 * 
	 * @param dafObservation : DafDocumentReference observation object
	 * @return : DocumentReference observation object
	 */
	private Observation createObservationObject(DafObservation dafObservation) {
		Observation observation = new Observation();
		JSONObject observationJSON = new JSONObject(dafObservation.getData());

		// Set version
		if (!(observationJSON.isNull("meta"))) {
			if (!(observationJSON.getJSONObject("meta").isNull("versionId"))) {
				observation.setId(new IdType(RESOURCE_TYPE, observationJSON.getString("id") + "",
						observationJSON.getJSONObject("meta").getString("versionId")));
			}else {
				observation.setId(new IdType(RESOURCE_TYPE, observationJSON.getString("id") + "", VERSION_ID));
			}
		} else {
			observation.setId(new IdType(RESOURCE_TYPE, observationJSON.getString("id") + "", VERSION_ID));
		}

		 //Set identifier
        if(!(observationJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = observationJSON.getJSONArray("identifier");
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
        	observation.setIdentifier(identifiers);
        }

		// set basedOn
		if (!observationJSON.isNull("basedOn")) {
			JSONArray basedOnJSON = observationJSON.getJSONArray("basedOn");
			int noOfBasedOn = basedOnJSON.length();
			List<Reference> basedOnList = new ArrayList<Reference>();
			for (int j = 0; j < noOfBasedOn; j++) {
				Reference theBasedOn = new Reference();
				if (!basedOnJSON.getJSONObject(j).isNull("identifier")) {
					JSONObject identifierJSON = basedOnJSON.getJSONObject(j).getJSONObject("identifier");
					Identifier theIdentifier = new Identifier();
					if (!identifierJSON.isNull("system")) {
						theIdentifier.setSystem(identifierJSON.getString("system"));
					}
					if (!identifierJSON.isNull("value")) {
						theIdentifier.setValue(identifierJSON.getString("value"));
					}
					theBasedOn.setIdentifier(theIdentifier);
				}
				basedOnList.add(theBasedOn);
			}
			observation.setBasedOn(basedOnList);
		}

		
		// status
		if (!observationJSON.isNull("status")) {
			observation.setStatus(ObservationStatus.fromCode(observationJSON.getString("status")));
		}

		// set partOf
		if (!observationJSON.isNull("partOf")) {
			JSONArray partOfJSON = observationJSON.getJSONArray("partOf");
			int noOfPartOf = partOfJSON.length();
			List<Reference> partOfList = new ArrayList<Reference>();
			for (int j = 0; j < noOfPartOf; j++) {
				Reference thePartOf = new Reference();
				if (!partOfJSON.getJSONObject(j).isNull("reference")) {
					thePartOf.setReference(partOfJSON.getJSONObject(j).getString("reference"));
				}
				partOfList.add(thePartOf);
			}
			observation.setPartOf(partOfList);
		}
		// set category
		if (!observationJSON.isNull("category")) {
			JSONArray categoryJSON = observationJSON.getJSONArray("category");
			List<CodeableConcept> categoryList = new ArrayList<CodeableConcept>();
			int noOfCategories = categoryJSON.length();
			for (int i = 0; i < noOfCategories; i++) {
				CodeableConcept theCategory = new CodeableConcept();
				if (!categoryJSON.getJSONObject(i).isNull("coding")) {
					JSONArray codingJSON = categoryJSON.getJSONObject(i).getJSONArray("coding");
					List<Coding> codingList = new ArrayList<Coding>();
					for (int j = 0; j < codingJSON.length(); j++) {
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

				if (!categoryJSON.getJSONObject(i).isNull("text")) {
					theCategory.setText(categoryJSON.getJSONObject(i).getString("text"));
				}
				categoryList.add(theCategory);
			}
			observation.setCategory(categoryList);
		}

		// set Code
		if (!observationJSON.isNull("code")) {
			JSONObject codeJSON = observationJSON.getJSONObject("code");
			CodeableConcept theCode = new CodeableConcept();
			if (!codeJSON.isNull("coding")) {
				JSONArray codingJSON = codeJSON.getJSONArray("coding");
				List<Coding> codingLists = new ArrayList<Coding>();
				int noOfCodings = codingJSON.length();
				for (int i = 0; i < noOfCodings; i++) {
					Coding theCoding = new Coding();
					if (!codingJSON.getJSONObject(i).isNull("system")) {
						theCoding.setSystem(codingJSON.getJSONObject(i).getString("system"));
					}
					if (!codingJSON.getJSONObject(i).isNull("code")) {
						theCoding.setCode(codingJSON.getJSONObject(i).getString("code"));
					}
					if (!codingJSON.getJSONObject(i).isNull("display")) {
						theCoding.setDisplay(codingJSON.getJSONObject(i).getString("display"));
					}
					if (!codingJSON.getJSONObject(i).isNull("userSelected")) {
						theCoding.setUserSelected(codingJSON.getJSONObject(i).getBoolean("userSelected"));
					}
					codingLists.add(theCoding);
				}
				theCode.setCoding(codingLists);
			}
			if (!codeJSON.isNull("text")) {
				theCode.setText(codeJSON.getString("text"));
			}
			observation.setCode(theCode);
		}
		 
		// SetSubject
		if (!observationJSON.isNull("subject")) {
			JSONObject subjectJSON = observationJSON.getJSONObject("subject");
			Reference theSubject = new Reference();
			if (!subjectJSON.isNull("reference"))  theSubject.setReference(subjectJSON.getString("reference"));
			if (!subjectJSON.isNull("display")) theSubject.setDisplay(subjectJSON.getString("display"));
			observation.setSubject(theSubject);
		}

		// SetEncounter
		if (!observationJSON.isNull("encounter")) {
			JSONObject encounterJSON = observationJSON.getJSONObject("encounter");
			Reference theEncounter = new Reference();
			if (!encounterJSON .isNull("reference")) {
				theEncounter.setReference(encounterJSON .getString("reference"));

			}
			if (!encounterJSON .isNull("display")) {
				theEncounter.setDisplay(encounterJSON .getString("display"));
			}
			observation.setEncounter(theEncounter);
		}

		// set hasMember
		if (!observationJSON.isNull("hasMember")) {
			JSONArray hasMemberJSON = observationJSON.getJSONArray("hasMember");

			int noOfMembers = hasMemberJSON.length();
			List<Reference> hasMemberList = new ArrayList<Reference>();

			for (int j = 0; j < noOfMembers; j++) {
				Reference theHasMember = new Reference();

				if (!hasMemberJSON.getJSONObject(j).isNull("reference")) {
					theHasMember.setReference(hasMemberJSON.getJSONObject(j).getString("reference"));
				}
				if (!hasMemberJSON.getJSONObject(j).isNull("display")) {
					theHasMember.setDisplay(hasMemberJSON.getJSONObject(j).getString("display"));
				}
				hasMemberList.add(theHasMember);
			}
			observation.setHasMember(hasMemberList);
		}

		// Set performer
		if (!observationJSON.isNull("performer")) {
			JSONArray performerJSON = observationJSON.getJSONArray("performer");
			int noOfPerformers = performerJSON.length();
			List<Reference> performerList = new ArrayList<Reference>();

			for (int j = 0; j < noOfPerformers; j++) {
				Reference thePerformer = new Reference();

				if (!performerJSON.getJSONObject(j).isNull("reference")) {
					thePerformer.setReference(performerJSON.getJSONObject(j).getString("reference"));
				}
				if (!performerJSON.getJSONObject(j).isNull("display")) {
					thePerformer.setDisplay(performerJSON.getJSONObject(j).getString("display"));
				}
				performerList.add(thePerformer);
			}
			observation.setPerformer(performerList);
		}

		// set Code
		if (!observationJSON.isNull("valueCodeableConcept")) {
			JSONObject valueCodeableConceptJSON = observationJSON.getJSONObject("valueCodeableConcept");
			CodeableConcept theCode = new CodeableConcept();
			if (!valueCodeableConceptJSON.isNull("coding")) {
				JSONArray codingJSON = valueCodeableConceptJSON.getJSONArray("coding");
				List<Coding> codingLists = new ArrayList<Coding>();
				int noOfCodings = codingJSON.length();
				for (int i = 0; i < noOfCodings; i++) {
					Coding theCoding = new Coding();
					if (!codingJSON.getJSONObject(i).isNull("system")) {
						theCoding.setSystem(codingJSON.getJSONObject(i).getString("system"));
					}
					if (!codingJSON.getJSONObject(i).isNull("code")) {
						theCoding.setCode(codingJSON.getJSONObject(i).getString("code"));
					}
					if (!codingJSON.getJSONObject(i).isNull("display")) {
						theCoding.setDisplay(codingJSON.getJSONObject(i).getString("display"));
					}
					if (!codingJSON.getJSONObject(i).isNull("userSelected")) {
						theCoding.setUserSelected(codingJSON.getJSONObject(i).getBoolean("userSelected"));
					}
					codingLists.add(theCoding);
				}
				theCode.setCoding(codingLists);
			}
			if (!valueCodeableConceptJSON.isNull("text")) {
				theCode.setText(valueCodeableConceptJSON.getString("text"));
			}
			observation.setValue(theCode);
		}


		// Set Value Quantity
		if (!observationJSON.isNull("valueQuantity")) {
			JSONObject valueQuantityJSON = observationJSON.getJSONObject("valueQuantity");
			Quantity theValueQuantity = new Quantity();
			
			if (!valueQuantityJSON.isNull("value")) {
				double double1 = valueQuantityJSON.getDouble("value");
				theValueQuantity.setValue(double1);
			}
			if (!valueQuantityJSON.isNull("unit")) {
				String string = valueQuantityJSON.getString("unit");
				theValueQuantity.setUnit(string);
			}
			if (!valueQuantityJSON.isNull("system")) {
				String string = valueQuantityJSON.getString("system");
				theValueQuantity.setSystem(string);
			}
			if (!valueQuantityJSON.isNull("code")) {
				String string = valueQuantityJSON.getString("code");
				theValueQuantity.setCode(string);
			}
			observation.setValue(theValueQuantity);
		}

		// device
		if (!observationJSON.isNull("device")) {
			JSONObject deviceJSON = observationJSON.getJSONObject("device");
			Reference theDevice = new Reference();

			if (!deviceJSON.isNull("reference")) {
				theDevice.setReference(deviceJSON.getString("reference"));
			}
			if (!deviceJSON.isNull("display")) {
				theDevice.setDisplay(deviceJSON.getString("display"));
			}
			observation.setDevice(theDevice);
		}

		//Set effectiveDateTime
		if(!(observationJSON.isNull("effectiveDateTime"))) {
			DateTimeType theEffectiveDateTime = new DateTimeType();
			Date effectiveDateTime = CommonUtil.convertStringToDate(observationJSON.getString("effectiveDateTime"));
			theEffectiveDateTime.setValue(effectiveDateTime);
			observation.setEffective(theEffectiveDateTime);
		}

		//Set issuedDateTime
		if(!(observationJSON.isNull("issued"))) {
			Date issuedDateTime = CommonUtil.convertStringToDate(observationJSON.getString("issued"));
			observation.setIssued(issuedDateTime);
		}

		//Set dataAbsentReason
		if(!(observationJSON.isNull("dataAbsentReason"))) {
			JSONObject dataAbsentReasonJSON = (JSONObject) observationJSON.getJSONObject("dataAbsentReason");
			CodeableConcept theCode = new CodeableConcept();
			//set Code
			JSONArray codingJSON = dataAbsentReasonJSON.getJSONArray("coding");
			List<Coding> codingLists = new ArrayList<Coding>();
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
				codingLists.add(theCoding);
			}
			if (!dataAbsentReasonJSON.isNull("text")) {
				theCode.setText(dataAbsentReasonJSON.getString("text"));
			}
			theCode.setCoding(codingLists);
			observation.setDataAbsentReason(theCode);
		}

			//Set Component
		if(!(observationJSON.isNull("component"))){
			JSONArray componentJSON = observationJSON.getJSONArray("component");
			List<Observation.ObservationComponentComponent> componentList = new ArrayList();

			for(int i=0; i < componentJSON.length(); i++) {
				Observation.ObservationComponentComponent compMap = new Observation.ObservationComponentComponent();

				//Set Component.Code
				if(!(componentJSON.getJSONObject(i).isNull("code"))){
					JSONObject codeJSON = ((JSONObject) componentJSON.get(i)).getJSONObject("code");
					CodeableConcept theCode = new CodeableConcept();
					//set Code
					JSONArray codingJSON = codeJSON.getJSONArray("coding");
					List<Coding> codingLists = new ArrayList<Coding>();
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
						codingLists.add(theCoding);
					}
					if (!codeJSON.isNull("text")) {
						theCode.setText(codeJSON.getString("text"));
					}
					theCode.setCoding(codingLists);
					compMap.setCode(theCode);
				}

				//Set Component.valueQuantity
				if(!(componentJSON.getJSONObject(i).isNull("valueQuantity"))){
					JSONObject valueQuantityJSON = ((JSONObject) componentJSON.get(i)).getJSONObject("valueQuantity");
					JSONObject valueQty = new JSONObject();
					Quantity theValueQuantity = new Quantity();

					if (!valueQuantityJSON.isNull("value")) {
						double double1 = valueQuantityJSON.getDouble("value");
						theValueQuantity.setValue(double1);
					}
					if (!valueQuantityJSON.isNull("unit")) {
						String string = valueQuantityJSON.getString("unit");
						theValueQuantity.setUnit(string);
					}
					if (!valueQuantityJSON.isNull("system")) {
						String string = valueQuantityJSON.getString("system");
						theValueQuantity.setSystem(string);
					}
					if (!valueQuantityJSON.isNull("code")) {
						String string = valueQuantityJSON.getString("code");
						theValueQuantity.setCode(string);
					}
					compMap.setValue(theValueQuantity);
				}

				//Set Component.dataAbsentReason
				if(!(componentJSON.getJSONObject(i).isNull("dataAbsentReason"))){
					JSONObject dataAbsentReasonJSON = ((JSONObject) componentJSON.get(i)).getJSONObject("dataAbsentReason");
					CodeableConcept theCode = new CodeableConcept();
					//set Code
					JSONArray codingJSON = dataAbsentReasonJSON.getJSONArray("coding");
					List<Coding> codingLists = new ArrayList<Coding>();
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
						codingLists.add(theCoding);
					}
					if (!dataAbsentReasonJSON.isNull("text")) {
						theCode.setText(dataAbsentReasonJSON.getString("text"));
					}
					theCode.setCoding(codingLists);
					compMap.setDataAbsentReason(theCode);
				}
				componentList.add(compMap);
			}
			observation.setComponent(componentList);
		}


		// set Derived from
		if (!observationJSON.isNull("derivedFrom")) {
			JSONArray derivedFromJSON = observationJSON.getJSONArray("derivedFrom");
			int noOfCodings = derivedFromJSON.length();
			List<Reference> derivedFromList = new ArrayList<Reference>();

			for (int j = 0; j < noOfCodings; j++) {
				Reference theDerivedFrom = new Reference();

				if (!derivedFromJSON.getJSONObject(j).isNull("reference")) {
					theDerivedFrom.setReference(derivedFromJSON.getJSONObject(j).getString("reference"));
				}
				if (!derivedFromJSON.getJSONObject(j).isNull("display")) {
					theDerivedFrom.setDisplay(derivedFromJSON.getJSONObject(j).getString("display"));
				}

				derivedFromList.add(theDerivedFrom);
			}
			observation.setDerivedFrom(derivedFromList);

		}
		return observation;
	}

}
