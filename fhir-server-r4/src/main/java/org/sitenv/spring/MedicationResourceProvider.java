package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Medication.MedicationBatchComponent;
import org.hl7.fhir.r4.model.Medication.MedicationIngredientComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedication;
import org.sitenv.spring.service.MedicationService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MedicationResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Medication";
	public static final String VERSION_ID = "1.0";
	AbstractApplicationContext context;
	MedicationService service;

	public MedicationResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		service = (MedicationService) context.getBean("medicationService");
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Medication.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Medication/1/_history/4
	 * 
	 * @param theId : Id of the medication
	 * @return : Object of medication information
	 */
	@Read(version = true)
	public Medication readOrVread(@IdParam IdType theId) {
		String id;
		DafMedication dafMedication;
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
			dafMedication = service.getMedicationByVersionId(id, theId.getVersionIdPart());

		} else {
			// this is a read
			dafMedication = service.getMedicationById(id);

		}
		return createMedicationObject(dafMedication);
	}

	/**
	 * The history operation retrieves a historical collection of all versions of a
	 * single resource (instance history). History methods must be annotated with
	 * the @History annotation.It supports Instance History method.
	 * "type=Medication.class". Instance level (history of a specific resource
	 * instance by type and ID) The method must have a parameter annotated with
	 * the @IdParam annotation, indicating the ID of the resource for which to
	 * return history. Example URL to invoke this method: http://<server name>/<context>/fhir/Medication/1/_history
	 * 
	 * @param theId : ID of the medication
	 * @return : List of medication's
	 */
	@History()
	public List<Medication> getMedicationHistoryById(@IdParam IdType theId) {
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
		List<DafMedication> dafMedicationList = service.getMedicationHistoryById(id);

		List<Medication> medicationList = new ArrayList<Medication>();
		for (DafMedication dafMedication : dafMedicationList) {
			medicationList.add(createMedicationObject(dafMedication));
		}

		return medicationList;
	}

	public List<Medication> getMedicationByResourceId(List<String> resourceID) {

		List<DafMedication> results = service.getMedicationByResourceId(resourceID);
		List<Medication> medicationList = new ArrayList<Medication>();

		for (DafMedication dafMedication : results) {
			medicationList.add(createMedicationObject(dafMedication));
		}
		return medicationList;
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
	 * @param theIngredient
	 * @param theIdentifier
	 * @param theIngredientCode
	 * @param theCode
	 * @param theForm
	 * @param theLotNumber
	 * @param theExpirationDate
	 * @param theManufacturer
	 * @param theStatus
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(
			javax.servlet.http.HttpServletRequest theServletRequest,

			@Description(shortDefinition = "The resource identity")
			@OptionalParam(name = Medication.SP_RES_ID) 
			StringAndListParam theId,

			@Description(shortDefinition = "The actual ingredient or content")
			@OptionalParam(name = Medication.SP_INGREDIENT) 
			ReferenceAndListParam theIngredient,

			@Description(shortDefinition = "Returns medications with this external identifier")
			@OptionalParam(name = Medication.SP_IDENTIFIER) 
			TokenAndListParam theIdentifier,
			
			@Description(shortDefinition = "Codes that identify this medication")
			@OptionalParam(name = Medication.SP_CODE) 
			TokenAndListParam theCode,

			@Description(shortDefinition = "The actual ingredient or content") 
			@OptionalParam(name = Medication.SP_INGREDIENT_CODE) 
			TokenAndListParam theIngredientCode,

			@Description(shortDefinition = "powder | tablets | capsule +") 
			@OptionalParam(name = Medication.SP_FORM) 
			TokenAndListParam theForm,

			@Description(shortDefinition = "Identifier assigned to batch") 
			@OptionalParam(name = Medication.SP_LOT_NUMBER) 
			TokenAndListParam theLotNumber,

			@Description(shortDefinition = "When batch will expire")
			@OptionalParam(name = Medication.SP_EXPIRATION_DATE) 
			DateAndListParam theExpirationDate,

			@Description(shortDefinition = "Manufacturer of the item") 
			@OptionalParam(name = Medication.SP_MANUFACTURER) 
			ReferenceAndListParam theManufacturer,

			@Description(shortDefinition = "active | inactive | entered-in-error") 
			@OptionalParam(name = Medication.SP_STATUS)
			TokenAndListParam theStatus,

			@IncludeParam(allow = { "*" }) Set<Include> theIncludes,

			@Sort SortSpec theSort,

			@Count Integer theCount) {

				SearchParameterMap paramMap = new SearchParameterMap();
				paramMap.add(Medication.SP_RES_ID, theId);
				paramMap.add(Medication.SP_INGREDIENT, theIngredient);
				paramMap.add(Medication.SP_IDENTIFIER, theIdentifier);
				paramMap.add(Medication.SP_CODE, theCode);
				paramMap.add(Medication.SP_INGREDIENT_CODE, theIngredientCode);
				paramMap.add(Medication.SP_FORM, theForm);
				paramMap.add(Medication.SP_LOT_NUMBER, theLotNumber);
				paramMap.add(Medication.SP_EXPIRATION_DATE, theExpirationDate);
				paramMap.add(Medication.SP_MANUFACTURER, theManufacturer);
				paramMap.add(Medication.SP_STATUS, theStatus);
				paramMap.setIncludes(theIncludes);
				paramMap.setSort(theSort);
				paramMap.setCount(theCount);
	
				final List<DafMedication> results = service.search(paramMap);
	
				return new IBundleProvider() {
					final InstantDt published = InstantDt.withCurrentTime();
	
					@Override
					public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
						List<IBaseResource> medicationList = new ArrayList<IBaseResource>();
						for (DafMedication dafMedication : results) {
							medicationList.add(createMedicationObject(dafMedication));
						}
						return medicationList;
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
	 * @param dafMedication : DafDocumentReference medication object
	 * @return : DocumentReference medication object
	 */
	private Medication createMedicationObject(DafMedication dafMedication) {
		Medication medication = new Medication();
		JSONObject medicationJSONObj = new JSONObject(dafMedication.getData());

		// Set version
		if (!(medicationJSONObj.isNull("meta"))) {
			if (!(medicationJSONObj.getJSONObject("meta").isNull("versionId"))) {
				medication.setId(new IdType(RESOURCE_TYPE, medicationJSONObj.getString("id") + "",
						medicationJSONObj.getJSONObject("meta").getString("versionId")));
			}else {
				medication.setId(new IdType(RESOURCE_TYPE, medicationJSONObj.getString("id") + "", VERSION_ID));
			}
		} else {
			medication.setId(new IdType(RESOURCE_TYPE, medicationJSONObj.getString("id") + "", VERSION_ID));
		}

		// Set Code
		if (!medicationJSONObj.isNull("code")) {
			JSONObject codeJSON = medicationJSONObj.getJSONObject("code");
			CodeableConcept theCodeableConcept = new CodeableConcept();

			if (!codeJSON.isNull("coding")) {
				JSONArray codingJSON = codeJSON.getJSONArray("coding");
				int noOfCodings = codingJSON.length();
				List<Coding> codingList = new ArrayList<Coding>();

				for (int i = 0; i < noOfCodings; i++) {
					Coding theCoding = new Coding();

					if (!(codingJSON.getJSONObject(i).isNull("system"))) {
						theCoding.setSystem(codingJSON.getJSONObject(i).getString("system"));
					}
					if (!codingJSON.getJSONObject(i).isNull("code")) {
						theCoding.setCode(codingJSON.getJSONObject(i).getString("code"));
					}
					if (!codingJSON.getJSONObject(i).isNull("display")) {
						theCoding.setDisplay(codingJSON.getJSONObject(i).getString("display"));

					}
					codingList.add(theCoding);
				}
				theCodeableConcept.setCoding(codingList);
			}
			medication.setCode(theCodeableConcept);
		}

		// set manufacturer
		if (!medicationJSONObj.isNull("manufacturer")) {
			JSONObject manufacturerJSON = medicationJSONObj.getJSONObject("manufacturer");
			Reference theManufacturer = new Reference();
			if (!manufacturerJSON.isNull("reference")) {
				theManufacturer.setReference(manufacturerJSON.getString("reference"));
			}
			if (!manufacturerJSON.isNull("display")) {
				theManufacturer.setDisplay(manufacturerJSON.getString("display"));
			}
			if (!manufacturerJSON.isNull("type")) {
				theManufacturer.setType(manufacturerJSON.getString("type"));
			}
			medication.setManufacturer(theManufacturer);
		}

		// status
		if (!medicationJSONObj.isNull("status")) {
			medication.setStatus(Medication.MedicationStatus.fromCode(medicationJSONObj.getString("status")));
		}

		// Set Form
		if (!medicationJSONObj.isNull("form")) {
			JSONObject formJSON = medicationJSONObj.getJSONObject("form");
			CodeableConcept theForm = new CodeableConcept();

			if (!formJSON.isNull("coding")) {
				JSONArray codingJSON = formJSON.getJSONArray("coding");
				int noOfCodings = codingJSON.length();
				List<Coding> codingList = new ArrayList<Coding>();
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
					codingList.add(theCoding);
				}
				theForm.setCoding(codingList);
			}
			medication.setForm(theForm);
		}

		// Set Ingredient
		if (!medicationJSONObj.isNull("ingredient")) {
			JSONArray ingredientJSON = medicationJSONObj.getJSONArray("ingredient");
			int noOfIngredients = ingredientJSON.length();
			List<MedicationIngredientComponent> ingredientList = new ArrayList<MedicationIngredientComponent>();

			for (int i = 0; i < noOfIngredients; i++) {
				MedicationIngredientComponent theIngredient = new MedicationIngredientComponent();

				if (!ingredientJSON.getJSONObject(i).isNull("itemReference")) {
					JSONObject referenceJSON = ingredientJSON.getJSONObject(i).getJSONObject("itemReference");
					Reference theReference = new Reference();

					if (!referenceJSON.isNull("reference")) {
						theReference.setReference(referenceJSON.getString("reference"));
					}
					if (!referenceJSON.isNull("display")) {
						theReference.setDisplay(referenceJSON.getString("display"));
					}
					theIngredient.setItem(theReference);
				}

				if (!ingredientJSON.getJSONObject(i).isNull("strength")) {
					JSONObject strengthJSON = ingredientJSON.getJSONObject(i).getJSONObject("strength");
					Ratio theRatio = new Ratio();

					if (!strengthJSON.isNull("numerator")) {
						JSONObject numeratorJSON = strengthJSON.getJSONObject("numerator");
						Quantity theNumerator = new Quantity();

						if (!numeratorJSON.isNull("value")) {
							theNumerator.setValue(numeratorJSON.getLong("value"));
						}
						if (!numeratorJSON.isNull("system")) {
							theNumerator.setSystem(numeratorJSON.getString("system"));
						}
						if (!numeratorJSON.isNull("code")) {
							theNumerator.setCode(numeratorJSON.getString("code"));
						}
						theRatio.setNumerator(theNumerator);
					}

					if (!strengthJSON.isNull("denominator")) {
						JSONObject denominatorJSON = strengthJSON.getJSONObject("denominator");
						Quantity theDenominator = new Quantity();
						if (!denominatorJSON.isNull("value")) {
							theDenominator.setValue(denominatorJSON.getLong("value"));
						}
						if (!denominatorJSON.isNull("system")) {
							theDenominator.setSystem(denominatorJSON.getString("system"));
						}
						if (!denominatorJSON.isNull("code")) {
							theDenominator.setCode(denominatorJSON.getString("code"));
						}
						theRatio.setDenominator(theDenominator);
					}
					theIngredient.setStrength(theRatio);

				}

				ingredientList.add(theIngredient);
			}
			medication.setIngredient(ingredientList);
		}

		// Set batch
		if (!medicationJSONObj.isNull("batch")) {
			JSONObject batchJSON = medicationJSONObj.getJSONObject("batch");
			MedicationBatchComponent theBatch = new MedicationBatchComponent();
			if (!batchJSON.isNull("lotNumber")) {
				theBatch.setLotNumber(batchJSON.getString("lotNumber"));
			}

			if (!batchJSON.isNull("expirationDate")) {
				Date date = CommonUtil.convertStringToDate(batchJSON.getString("expirationDate"));
				theBatch.setExpirationDate(date);
			}
			medication.setBatch(theBatch);
		}
		
		 //Set identifier
        if(!(medicationJSONObj.isNull("identifier"))) {
        	JSONArray identifierJSON = medicationJSONObj.getJSONArray("identifier");
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
        	medication.setIdentifier(identifiers);
        }
		return medication;
	}
}
