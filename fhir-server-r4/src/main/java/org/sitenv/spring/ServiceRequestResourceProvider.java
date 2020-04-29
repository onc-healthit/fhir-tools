package org.sitenv.spring;

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
import org.sitenv.spring.model.DafServiceRequest;
import org.sitenv.spring.service.ServiceRequestService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceRequestResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "ServiceRequest";
	public static final String VERSION_ID = "1.0";
	AbstractApplicationContext context;
	ServiceRequestService service;

	public ServiceRequestResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		service = (ServiceRequestService) context.getBean("serviceRequestService");
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return ServiceRequest.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/ServiceRequest/1/_history/4
	 * 
	 * @param theId : Id of the organization
	 * @return : Object of organization information
	 */
	@Read(version = true)
	public ServiceRequest readOrVread(@IdParam IdType theId) {
		String id;
		DafServiceRequest dafServiceRequest;
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
			dafServiceRequest = service.getServiceRequestByVersionId(id, theId.getVersionIdPart());

		} else {

			dafServiceRequest = service.getServiceRequestById(id);
		}

		return createServiceRequestObject(dafServiceRequest);
	}

	/**
	 * The history operation retrieves a historical collection of all versions of a
	 * single resource (instance history). History methods must be annotated with
	 * the @History annotation.It supports Instance History method.
	 * "type=ServiceRequest.class". Instance level (history of a specific resource
	 * instance by type and ID) The method must have a parameter annotated with
	 * the @IdParam annotation, indicating the ID of the resource for which to
	 * return history. Example URL to invoke this method: http://<server name>/<context>/fhir/ServiceRequest/1/_history
	 * 
	 * @param theId : ID of the serviceRequest
	 * @return : List of serviceRequest's
	 */
	@History()
	public List<ServiceRequest> getServiceRequestHistoryById(@IdParam IdType theId) {

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
		List<DafServiceRequest> dafServiceRequestList = service.getServiceRequestHistoryById(id);
		List<ServiceRequest> serviceRequestList = new ArrayList<ServiceRequest>();
		for (DafServiceRequest dafServiceRequest : dafServiceRequestList) {
			serviceRequestList.add(createServiceRequestObject(dafServiceRequest));
		}
		return serviceRequestList;
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
	 * @param theStatus
	 * @param theEncounter
	 * @param theSubject
	 * @param theRequester
	 * @param theCode
	 * @param theCategory
	 * @param theBodySite
	 * @param theSpecimen
	 * @param thePerformer
	 * @param theIntent
	 * @param theRequistion
	 * @param theAuthoredOn
	 * @param theOccurance
	 * @param theBasedOn
	 * @param thePerformerType
	 * @param thePriority
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(
		javax.servlet.http.HttpServletRequest theServletRequest,

		@Description(shortDefinition = "The resource identity")
        @OptionalParam(name = ServiceRequest.SP_RES_ID)
        StringAndListParam theId,

		@Description(shortDefinition = "An ServiceRequest  identifier")
        @OptionalParam(name = ServiceRequest.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

		@Description(shortDefinition = "The status of the order")
        @OptionalParam(name = ServiceRequest.SP_STATUS)
        TokenAndListParam theStatus,

		@Description(shortDefinition = "An encounter that provides additional information about the healthcare context in which this request is made")
        @OptionalParam(name = ServiceRequest.SP_ENCOUNTER)
        ReferenceAndListParam theEncounter,

		@Description(shortDefinition = "On whom or what the service is to be performed")
        @OptionalParam(name = ServiceRequest.SP_SUBJECT) 
        ReferenceAndListParam theSubject,

		@Description(shortDefinition = "The individual who initiated the request and has responsibility for its activation")
        @OptionalParam(name = ServiceRequest.SP_REQUESTER) 
        ReferenceAndListParam theRequester,

		@Description(shortDefinition = "A code that identifies a particular service")
        @OptionalParam(name = ServiceRequest.SP_CODE) 
        TokenAndListParam theCode,

		@Description(shortDefinition = " code that classifies the service for searching, sorting and display purposes") 
        @OptionalParam(name = ServiceRequest.SP_CATEGORY) 
        TokenAndListParam theCategory,

		@Description(shortDefinition = "Anatomic location where the procedure should be performed") 
        @OptionalParam(name = ServiceRequest.SP_BODY_SITE) 
        TokenAndListParam theBodySite,

		@Description(shortDefinition = "One or more specimens that the laboratory procedure will use")
        @OptionalParam(name = ServiceRequest.SP_SPECIMEN) 
        ReferenceAndListParam theSpecimen,

		@Description(shortDefinition = "The desired performer for doing the requested service")
        @OptionalParam(name = ServiceRequest.SP_PERFORMER)
        ReferenceAndListParam thePerformer,

		@Description(shortDefinition = "Whether the request is a proposal, plan, an original order or a reflex order")
        @OptionalParam(name = ServiceRequest.SP_INTENT)
        TokenAndListParam theIntent,

		@Description(shortDefinition = "A shared identifier common to all service requests")
        @OptionalParam(name = ServiceRequest.SP_REQUISITION)
        TokenAndListParam theRequistion,

		@Description(shortDefinition = "When the request transitioned to being actionable")
        @OptionalParam(name = ServiceRequest.SP_AUTHORED)
        DateRangeParam theAuthoredOn,

		@Description(shortDefinition = "The date/time at which the requested service should occur")
        @OptionalParam(name = ServiceRequest.SP_OCCURRENCE) 
        DateAndListParam theOccurance,

		@Description(shortDefinition = "Plan/proposal/order fulfilled by this request")
        @OptionalParam(name = ServiceRequest.SP_BASED_ON)
        ReferenceAndListParam theBasedOn,

		@Description(shortDefinition = "The request takes the place of the referenced completed or terminated request(s)")
        @OptionalParam(name = ServiceRequest.SP_REPLACES) 
        ReferenceAndListParam theReplaces,

		@Description(shortDefinition = "Desired type of performer for doing the requested service")
        @OptionalParam(name = ServiceRequest.SP_PERFORMER_TYPE) 
        TokenAndListParam thePerformerType,

		@Description(shortDefinition = "Indicates how quickly the ServiceRequest should be addressed")
        @OptionalParam(name = ServiceRequest.SP_PRIORITY)
        TokenAndListParam thePriority,

		@Description(shortDefinition = "The URL pointing to an externally maintained protocol, guideline, orderset") 
        @OptionalParam(name = ServiceRequest.SP_INSTANTIATES_URI)
        UriAndListParam theInstantiatesUri,

		@Sort SortSpec theSort,
		@Count Integer theCount) {

			SearchParameterMap paramMap = new SearchParameterMap();
			paramMap.add(ServiceRequest.SP_RES_ID, theId);
			paramMap.add(ServiceRequest.SP_IDENTIFIER, theIdentifier);
			paramMap.add(ServiceRequest.SP_STATUS, theStatus);
			paramMap.add(ServiceRequest.SP_ENCOUNTER, theEncounter);
			paramMap.add(ServiceRequest.SP_SUBJECT, theSubject);
			paramMap.add(ServiceRequest.SP_REQUESTER, theRequester);
			paramMap.add(ServiceRequest.SP_CODE, theCode);
			paramMap.add(ServiceRequest.SP_CATEGORY, theCategory);
			paramMap.add(ServiceRequest.SP_BODY_SITE, theBodySite);
			paramMap.add(ServiceRequest.SP_SPECIMEN, theSpecimen);
			paramMap.add(ServiceRequest.SP_PERFORMER, thePerformer);
			paramMap.add(ServiceRequest.SP_INTENT, theIntent);
			paramMap.add(ServiceRequest.SP_REQUISITION, theRequistion);
			paramMap.add(ServiceRequest.SP_AUTHORED, theAuthoredOn);
			paramMap.add(ServiceRequest.SP_OCCURRENCE, theOccurance);
			paramMap.add(ServiceRequest.SP_BASED_ON, theBasedOn);
			paramMap.add(ServiceRequest.SP_REPLACES, theReplaces);
			paramMap.add(ServiceRequest.SP_PERFORMER_TYPE, thePerformerType);
			paramMap.add(ServiceRequest.SP_PRIORITY, thePriority);
			paramMap.add(ServiceRequest.SP_INSTANTIATES_URI, theInstantiatesUri);
			paramMap.setSort(theSort);
			paramMap.setCount(theCount);

			final List<DafServiceRequest> results = service.search(paramMap);

			return new IBundleProvider() {
				final InstantDt published = InstantDt.withCurrentTime();

				@Override
				public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
					List<IBaseResource> serviceRequestList = new ArrayList<IBaseResource>();
					for (DafServiceRequest dafServiceRequest : results) {
						serviceRequestList.add(createServiceRequestObject(dafServiceRequest));
					}
					return serviceRequestList;
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
	 */
	private ServiceRequest createServiceRequestObject(DafServiceRequest dafServiceRequest) {

		ServiceRequest serviceRequest = new ServiceRequest();
		JSONObject serviceRequestJSON = new JSONObject(dafServiceRequest.getData());

		// Set version
		if (!(serviceRequestJSON.isNull("meta"))) {
			if (!(serviceRequestJSON.getJSONObject("meta").isNull("versionId"))) {
				serviceRequest.setId(new IdType(RESOURCE_TYPE, serviceRequestJSON.getString("id") + "",
						serviceRequestJSON.getJSONObject("meta").getString("versionId")));
			}else {
				serviceRequest.setId(new IdType(RESOURCE_TYPE, serviceRequestJSON.getString("id") + "", VERSION_ID));
			}
		} else {
			serviceRequest.setId(new IdType(RESOURCE_TYPE, serviceRequestJSON.getString("id") + "", VERSION_ID));
		}

		 //Set identifier
        if(!(serviceRequestJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = serviceRequestJSON.getJSONArray("identifier");
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
        	serviceRequest.setIdentifier(identifiers);
        }
		// Set status
		if (!(serviceRequestJSON.isNull("status"))) {
			serviceRequest
					.setStatus(ServiceRequest.ServiceRequestStatus.fromCode(serviceRequestJSON.getString("status")));
		}

		// Set intent
		if (!(serviceRequestJSON.isNull("intent"))) {
			serviceRequest
					.setIntent(ServiceRequest.ServiceRequestIntent.fromCode(serviceRequestJSON.getString("intent")));
		}

		// set encounter
		if (!(serviceRequestJSON.isNull("encounter"))) {
			Reference theEncounter = new Reference();
			if (!(serviceRequestJSON.getJSONObject("encounter").isNull("reference"))) {
				theEncounter.setReference(serviceRequestJSON.getJSONObject("encounter").getString("reference"));
			}
			if (!(serviceRequestJSON.getJSONObject("encounter").isNull("display"))) {
				theEncounter.setDisplay(serviceRequestJSON.getJSONObject("encounter").getString("display"));
			}
			if (!(serviceRequestJSON.getJSONObject("encounter").isNull("type"))) {
				theEncounter.setType(serviceRequestJSON.getJSONObject("encounter").getString("type"));
			}
			serviceRequest.setEncounter(theEncounter);
		}

		// set subject
		if (!(serviceRequestJSON.isNull("subject"))) {
			Reference theSubject = new Reference();
			if (!(serviceRequestJSON.getJSONObject("subject").isNull("reference"))) {
				theSubject.setReference(serviceRequestJSON.getJSONObject("subject").getString("reference"));
			}
			if (!(serviceRequestJSON.getJSONObject("subject").isNull("display"))) {
				theSubject.setDisplay(serviceRequestJSON.getJSONObject("subject").getString("display"));
			}
			if (!(serviceRequestJSON.getJSONObject("subject").isNull("type"))) {
				theSubject.setType(serviceRequestJSON.getJSONObject("subject").getString("type"));
			}
			serviceRequest.setSubject(theSubject);
		}

		// set requisition
		if (!(serviceRequestJSON.isNull("requisition"))) {
			Identifier theIdentifier = new Identifier();
			if (!(serviceRequestJSON.getJSONObject("requisition").isNull("system"))) {
				theIdentifier.setSystem(serviceRequestJSON.getJSONObject("requisition").getString("system"));
			}
			if (!(serviceRequestJSON.getJSONObject("requisition").isNull("value"))) {
				theIdentifier.setValue(serviceRequestJSON.getJSONObject("requisition").getString("value"));
			}
			serviceRequest.setRequisition(theIdentifier);
		}

		// set requester
		if (!(serviceRequestJSON.isNull("requester"))) {
			Reference theRequester = new Reference();
			if (!(serviceRequestJSON.getJSONObject("requester").isNull("reference"))) {
				theRequester.setReference(serviceRequestJSON.getJSONObject("subject").getString("reference"));
			}
			if (!(serviceRequestJSON.getJSONObject("requester").isNull("display"))) {
				theRequester.setDisplay(serviceRequestJSON.getJSONObject("requester").getString("display"));
			}
			if (!(serviceRequestJSON.getJSONObject("requester").isNull("type"))) {
				theRequester.setType(serviceRequestJSON.getJSONObject("requester").getString("type"));
			}
			serviceRequest.setRequester(theRequester);
		}

		// set code
		if (!(serviceRequestJSON.isNull("code"))) {
			JSONObject codeJSON = serviceRequestJSON.getJSONObject("code");
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
			serviceRequest.setCode(theCode);
		}

		// set specimen
		if (!(serviceRequestJSON.isNull("specimen"))) {
			JSONArray specimenJSON = serviceRequestJSON.getJSONArray("specimen");
			int noOfSpecimens = specimenJSON.length();
			List<Reference> specimenDtList = new ArrayList<Reference>();
			for (int i = 0; i < noOfSpecimens; i++) {
				Reference theSpecimen = new Reference();
				if (!(specimenJSON.getJSONObject(i).isNull("reference"))) {
					theSpecimen.setReference(specimenJSON.getJSONObject(i).getString("reference"));
				}
				if (!(specimenJSON.getJSONObject(i).isNull("display"))) {
					theSpecimen.setDisplay(specimenJSON.getJSONObject(i).getString("display"));
				}
				if (!(specimenJSON.getJSONObject(i).isNull("type"))) {
					theSpecimen.setType(specimenJSON.getJSONObject(i).getString("type"));
				}
				specimenDtList.add(theSpecimen);
			}
			serviceRequest.setSpecimen(specimenDtList);
		}

		// set performer
		if (!(serviceRequestJSON.isNull("performer"))) {
			JSONArray performerJSON = serviceRequestJSON.getJSONArray("performer");
			int noOfPerformer = performerJSON.length();
			List<Reference> performerDtList = new ArrayList<Reference>();
			for (int i = 0; i < noOfPerformer; i++) {
				Reference thePerformer = new Reference();
				if (!(performerJSON.getJSONObject(i).isNull("reference"))) {
					thePerformer.setReference(performerJSON.getJSONObject(i).getString("reference"));
				}
				if (!(performerJSON.getJSONObject(i).isNull("display"))) {
					thePerformer.setDisplay(performerJSON.getJSONObject(i).getString("display"));
				}
				if (!(performerJSON.getJSONObject(i).isNull("type"))) {
					thePerformer.setType(performerJSON.getJSONObject(i).getString("type"));
				}
				performerDtList.add(thePerformer);
			}
			serviceRequest.setPerformer(performerDtList);
		}

		// set basedOn
		if (!(serviceRequestJSON.isNull("basedOn"))) {
			JSONArray basedOnJSON = serviceRequestJSON.getJSONArray("basedOn");
			int noOfBasedOn = basedOnJSON.length();
			List<Reference> basedOnDtList = new ArrayList<Reference>();
			for (int i = 0; i < noOfBasedOn; i++) {
				Reference theBasedOn = new Reference();
				if (!(basedOnJSON.getJSONObject(i).isNull("reference"))) {
					theBasedOn.setReference(basedOnJSON.getJSONObject(i).getString("reference"));
				}
				if (!(basedOnJSON.getJSONObject(i).isNull("display"))) {
					theBasedOn.setDisplay(basedOnJSON.getJSONObject(i).getString("display"));
				}
				if (!(basedOnJSON.getJSONObject(i).isNull("type"))) {
					theBasedOn.setType(basedOnJSON.getJSONObject(i).getString("type"));
				}
				basedOnDtList.add(theBasedOn);
			}
			serviceRequest.setBasedOn(basedOnDtList);
		}

		// set replaces
		if (!(serviceRequestJSON.isNull("replaces"))) {
			JSONArray replacesJSON = serviceRequestJSON.getJSONArray("replaces");
			int noOfReplaces = replacesJSON.length();
			List<Reference> replacesDtList = new ArrayList<Reference>();
			for (int i = 0; i < noOfReplaces; i++) {
				Reference theReplaces = new Reference();
				if (!(replacesJSON.getJSONObject(i).isNull("reference"))) {
					theReplaces.setReference(replacesJSON.getJSONObject(i).getString("reference"));
				}
				if (!(replacesJSON.getJSONObject(i).isNull("display"))) {
					theReplaces.setDisplay(replacesJSON.getJSONObject(i).getString("display"));
				}
				if (!(replacesJSON.getJSONObject(i).isNull("type"))) {
					theReplaces.setType(replacesJSON.getJSONObject(i).getString("type"));
				}
				replacesDtList.add(theReplaces);
			}
			serviceRequest.setReplaces(replacesDtList);
		}

		// Set authoredOn
		if (!(serviceRequestJSON.isNull("authoredOn"))) {
			String dateInStr = serviceRequestJSON.getString("authoredOn");
			Date dateOfauthoredOn = CommonUtil.convertStringToDate(dateInStr);
			serviceRequest.setAuthoredOn(dateOfauthoredOn);
		}

		// set occurrenceDateTime
		if (!(serviceRequestJSON.isNull("occurrenceDateTime"))) {
			DateTimeType theOccurrenceDateTime = new DateTimeType();
        	Date effectiveDate = CommonUtil.convertStringToDate(serviceRequestJSON.getString("occurrenceDateTime"));
        	theOccurrenceDateTime.setValue(effectiveDate);
        	serviceRequest.setOccurrence(theOccurrenceDateTime);
		}

		// set category
		if (!(serviceRequestJSON.isNull("category"))) {
			JSONArray categoryJSON = serviceRequestJSON.getJSONArray("category");
			int noOfCategaries = categoryJSON.length();
			List<CodeableConcept> categoryList = new ArrayList<CodeableConcept>();
			for (int c = 0; c < noOfCategaries; c++) {
				CodeableConcept theCategory = new CodeableConcept();
				if (!(categoryJSON.getJSONObject(c).isNull("coding"))) {
					JSONArray categoryCodingJSON = categoryJSON.getJSONObject(c).getJSONArray("coding");
					int noOfCategoryCoding = categoryCodingJSON.length();
					List<Coding> codingList = new ArrayList<Coding>();
					for (int j = 0; j < noOfCategoryCoding; j++) {
						Coding categoryCoding = new Coding();
						if (!(categoryCodingJSON.getJSONObject(j).isNull("system"))) {
							categoryCoding.setSystem(categoryCodingJSON.getJSONObject(j).getString("system"));
						}
						if (!(categoryCodingJSON.getJSONObject(j).isNull("display"))) {
							categoryCoding.setDisplay(categoryCodingJSON.getJSONObject(j).getString("display"));
						}
						if (!(categoryCodingJSON.getJSONObject(j).isNull("code"))) {
							categoryCoding.setCode(categoryCodingJSON.getJSONObject(j).getString("code"));
						}
						codingList.add(categoryCoding);
					}
					theCategory.setCoding(codingList);
				}
				categoryList.add(theCategory);
			}
			serviceRequest.setCategory(categoryList);
		}

		// set bodySite
		if (!(serviceRequestJSON.isNull("bodySite"))) {
			JSONArray bodySiteJson = serviceRequestJSON.getJSONArray("bodySite");
			int noOfType = bodySiteJson.length();
			List<CodeableConcept> bodySiteDtList = new ArrayList<>();
			for (int i = 0; i < noOfType; i++) {
				CodeableConcept theBodySite = new CodeableConcept();
				if (!(bodySiteJson.getJSONObject(i).isNull("coding"))) {
					JSONArray bodySiteCodingJSON = bodySiteJson.getJSONObject(i).getJSONArray("coding");
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
			serviceRequest.setBodySite(bodySiteDtList);
		}

		// set supportingInfo
		if (!(serviceRequestJSON.isNull("supportingInfo"))) {
			JSONArray supportInfoJSON = serviceRequestJSON.getJSONArray("supportingInfo");
			int noOfSupprtInfo = supportInfoJSON.length();
			List<Reference> supportingInfoDtList = new ArrayList<Reference>();
			for (int i = 0; i < noOfSupprtInfo; i++) {
				Reference theSupportingInfo = new Reference();
				if (!(supportInfoJSON.getJSONObject(i).isNull("reference"))) {
					theSupportingInfo.setReference(supportInfoJSON.getJSONObject(i).getString("reference"));
				}
				if (!(supportInfoJSON.getJSONObject(i).isNull("display"))) {
					theSupportingInfo.setDisplay(supportInfoJSON.getJSONObject(i).getString("display"));
				}
				if (!(supportInfoJSON.getJSONObject(i).isNull("type"))) {
					theSupportingInfo.setType(supportInfoJSON.getJSONObject(i).getString("type"));
				}
				supportingInfoDtList.add(theSupportingInfo);
			}
			serviceRequest.setSupportingInfo(supportingInfoDtList);
		}

		// set reasonCode
		if (!(serviceRequestJSON.isNull("reasonCode"))) {
			JSONArray reasonCodeJSON = serviceRequestJSON.getJSONArray("reasonCode");
			int noOfReasonCode = reasonCodeJSON.length();
			List<CodeableConcept> reasonCodeList = new ArrayList<CodeableConcept>();
			for (int c = 0; c < noOfReasonCode; c++) {
				CodeableConcept theReasonCode = new CodeableConcept();
				if (!(reasonCodeJSON.getJSONObject(c).isNull("coding"))) {
					JSONArray reasonCodeCodingJSON = reasonCodeJSON.getJSONObject(c).getJSONArray("coding");
					int noOfReasonCodeCoding = reasonCodeCodingJSON.length();
					List<Coding> codingList = new ArrayList<Coding>();
					for (int j = 0; j < noOfReasonCodeCoding; j++) {
						Coding reasonCoding = new Coding();
						if (!(reasonCodeCodingJSON.getJSONObject(j).isNull("system"))) {
							reasonCoding.setSystem(reasonCodeCodingJSON.getJSONObject(j).getString("system"));
						}
						if (!(reasonCodeCodingJSON.getJSONObject(j).isNull("display"))) {
							reasonCoding.setDisplay(reasonCodeCodingJSON.getJSONObject(j).getString("display"));
						}
						if (!(reasonCodeCodingJSON.getJSONObject(j).isNull("code"))) {
							reasonCoding.setCode(reasonCodeCodingJSON.getJSONObject(j).getString("code"));
						}
						codingList.add(reasonCoding);
					}
					theReasonCode.setCoding(codingList);
				}
				reasonCodeList.add(theReasonCode);
			}
			serviceRequest.setReasonCode(reasonCodeList);
		}

		// set note
		if (!(serviceRequestJSON.isNull("note"))) {
			JSONArray noteJSON = serviceRequestJSON.getJSONArray("note");
			int noOfNotes = noteJSON.length();
			List<Annotation> noteList = new ArrayList<Annotation>();
			for (int i = 0; i < noOfNotes; i++) {
				Annotation theNote = new Annotation();
				if (!(noteJSON.getJSONObject(i).isNull("text"))) {
					theNote.setText(noteJSON.getJSONObject(i).getString("text"));
				}
				if(!(noteJSON.getJSONObject(i).isNull("time"))) {
        			Date noteTime = CommonUtil.convertStringToDate(noteJSON.getJSONObject(i).getString("time"));
        			theNote.setTime(noteTime);
        		}
				noteList.add(theNote);
			}
			serviceRequest.setNote(noteList);
		}

		// Set priority
		if (!(serviceRequestJSON.isNull("priority"))) {
			serviceRequest.setPriority(
					ServiceRequest.ServiceRequestPriority.fromCode(serviceRequestJSON.getString("priority")));
		}

		// set performerType
		if (!(serviceRequestJSON.isNull("performerType"))) {
			JSONObject performerTypeJSONObj = serviceRequestJSON.getJSONObject("performerType");
			CodeableConcept thePerformerType = new CodeableConcept();
			if (!(performerTypeJSONObj.isNull("coding"))) {
				JSONArray performerCodingJSON = performerTypeJSONObj.getJSONArray("coding");
				int noOfreasonCode = performerCodingJSON.length();
				List<Coding> performerTypeList = new ArrayList<Coding>();
				for (int p = 0; p < noOfreasonCode; p++) {
					Coding theCoding = new Coding();
					if (!(performerCodingJSON.getJSONObject(p).isNull("system"))) {
						theCoding.setSystem(performerCodingJSON.getJSONObject(p).getString("system"));
					}
					if (!(performerCodingJSON.getJSONObject(p).isNull("code"))) {
						theCoding.setCode(performerCodingJSON.getJSONObject(p).getString("code"));
					}
					if (!(performerCodingJSON.getJSONObject(p).isNull("display"))) {
						theCoding.setDisplay(performerCodingJSON.getJSONObject(p).getString("display"));
					}
					performerTypeList.add(theCoding);
				}
				thePerformerType.setCoding(performerTypeList);
			}
			serviceRequest.setPerformerType(thePerformerType);
		}

		// Set instantiatesUri
		if (!(serviceRequestJSON.isNull("instantiatesUri"))) {
			List<UriType> uriList = new ArrayList<UriType>();
			JSONArray urisJSON = serviceRequestJSON.getJSONArray("instantiatesUri");
			int noOfUris = urisJSON.length();
			for (int i = 0; i < noOfUris; i++) {
				String uriName = urisJSON.getString(i);
				UriType theUri = new UriType();
				theUri.setValue(uriName);
				uriList.add(theUri);
			}
			serviceRequest.setInstantiatesUri(uriList);
		}
		return serviceRequest;
	}
}
