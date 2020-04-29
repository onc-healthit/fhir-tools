package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Specimen.SpecimenCollectionComponent;
import org.hl7.fhir.r4.model.Specimen.SpecimenContainerComponent;
import org.hl7.fhir.r4.model.Specimen.SpecimenProcessingComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafSpecimen;
import org.sitenv.spring.service.SpecimenService;
import org.sitenv.spring.util.CommonUtil;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class SpecimenResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Specimen";
    public static final String VERSION_ID = "1.0";
    AbstractApplicationContext context;
    SpecimenService service;
    
    public SpecimenResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (SpecimenService) context.getBean("specimenService");
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Specimen.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Specimen/1/_history/4
	 * @param theId : Id of the Specimen
	 * @return : Object of Specimen information
	 */
	@Read(version=true)
    public Specimen readOrVread(@IdParam IdType theId) {
		String id;
		DafSpecimen dafSpecimen;
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
		   dafSpecimen = service.getSpecimenByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       dafSpecimen = service.getSpecimenById(id);
		}
		return createSpecimenObject(dafSpecimen);
    }
	
	/**
	 * The history operation retrieves a historical collection of all versions of a single resource (instance history).
	 * History methods must be annotated with the @History annotation.It supports Instance History method.
	 * "type=Specimen.class". Instance level (history of a specific resource instance by type and ID)
	 * The method must have a parameter annotated with the @IdParam annotation, indicating the ID of the resource for which to return history.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Specimen/1/_history
	 * @param theId : ID of the Specimen
	 * @return : List of Specimen's
	 */
	@History()
    public List<Specimen> getSpecimenById( @IdParam IdType theId) {

		String id;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		List<DafSpecimen> dafSpecimenList = service.getSpecimenHistoryById(id);
        
        List<Specimen> specimenList = new ArrayList<Specimen>();
        for (DafSpecimen dafSpecimen : dafSpecimenList) {
        	specimenList.add(createSpecimenObject(dafSpecimen));
        }
        
        return specimenList;
	}
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theContainer
	 * @param theParent
	 * @param theContainerId
	 * @param theBodysite
	 * @param theSubject
	 * @param theAccession
	 * @param theType
	 * @param theCollector
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
            @OptionalParam(name = Specimen.SP_RES_ID)
            TokenAndListParam theId,

            @Description(shortDefinition = "A Specimen identifier")
            @OptionalParam(name = Specimen.SP_IDENTIFIER)
            TokenAndListParam theIdentifier,
            
            @Description(shortDefinition = "The kind of specimen container")
            @OptionalParam(name = Specimen.SP_CONTAINER)
            TokenAndListParam theContainer,
            
            @Description(shortDefinition = "The parent of the specimen")
            @OptionalParam(name = Specimen.SP_PARENT)
            ReferenceAndListParam theParent,
            
            @Description(shortDefinition = "The unique identifier associated with the specimen container")
            @OptionalParam(name = Specimen.SP_CONTAINER_ID)
            TokenAndListParam theContainerId,
            
            @Description(shortDefinition = "The code for the body site from where the specimen originated")
            @OptionalParam(name = Specimen.SP_BODYSITE)
            TokenAndListParam theBodysite,
            
            @Description(shortDefinition = "The subject of the specimen")
            @OptionalParam(name = Specimen.SP_SUBJECT)
            ReferenceAndListParam theSubject,
            
            @Description(shortDefinition = "The accession number associated with the specimen")
            @OptionalParam(name = Specimen.SP_ACCESSION)
            TokenAndListParam theAccession,
            
            @Description(shortDefinition = "The specimen type")
            @OptionalParam(name = Specimen.SP_TYPE)
            TokenAndListParam theType,
            
            @Description(shortDefinition = "Who collected the specimen")
            @OptionalParam(name = Specimen.SP_COLLECTOR)
            ReferenceAndListParam theCollector,
            
            @Description(shortDefinition = "available | unavailable | unsatisfactory | entered-in-error")
            @OptionalParam(name = Specimen.SP_STATUS)
            TokenAndListParam theStatus,
                       
            @IncludeParam(allow = {"*"})
            Set<Include> theIncludes,

            @Sort
            SortSpec theSort,

            @Count
            Integer theCount
    ) {

        try {
            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(Specimen.SP_RES_ID, theId);
            paramMap.add(Specimen.SP_IDENTIFIER, theIdentifier);
            paramMap.add(Specimen.SP_CONTAINER, theContainer);
            paramMap.add(Specimen.SP_PARENT, theParent);
            paramMap.add(Specimen.SP_CONTAINER_ID, theContainerId);
            paramMap.add(Specimen.SP_BODYSITE, theBodysite);
            paramMap.add(Specimen.SP_SUBJECT, theSubject);
            paramMap.add(Specimen.SP_ACCESSION, theAccession);
            paramMap.add(Specimen.SP_TYPE, theType);
            paramMap.add(Specimen.SP_COLLECTOR, theCollector);
            paramMap.add(Specimen.SP_STATUS, theStatus);

            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<DafSpecimen> results = service.search(paramMap);

            //ca.uhn.fhir.rest.server.IBundleProvider retVal = getDao().search(paramMap);
            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> specimenList = new ArrayList<IBaseResource>();
                    for(DafSpecimen dafSpecimen : results){
                    	specimenList.add(createSpecimenObject(dafSpecimen));
                    }
                    return specimenList;
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
        } finally {

        }
    }
	/**
     * This method converts DafDocumentReference object to DocumentReference object
     * @param dafSpecimen : DafDocumentReference specimen object
     * @return : DocumentReference specimen object
     */
    private Specimen createSpecimenObject(DafSpecimen dafSpecimen) {
    	Specimen specimen = new Specimen();
        JSONObject specimenJSON = new JSONObject(dafSpecimen.getData());

        // Set version
        if(!(specimenJSON.isNull("meta"))) {
        	if(!(specimenJSON.getJSONObject("meta").isNull("versionId"))) {
                specimen.setId(new IdType(RESOURCE_TYPE, specimenJSON.getString("id") + "", specimenJSON.getJSONObject("meta").getString("versionId")));
        	}else {
				specimen.setId(new IdType(RESOURCE_TYPE, specimenJSON.getString("id") + "", VERSION_ID));
			}
        }
        else {
            specimen.setId(new IdType(RESOURCE_TYPE, specimenJSON.getString("id") + "", VERSION_ID));
        }

        //Set identifier
        if(!(specimenJSON.isNull("identifier"))) {
        	JSONArray identifierJSON = specimenJSON.getJSONArray("identifier");
        	int noOfIdentifiers = identifierJSON.length();
        	List<Identifier> identifiers = new ArrayList<Identifier>();
        	
        	for(int i = 0; i < noOfIdentifiers; i++) {
            	Identifier theIdentifier = new Identifier();
        		if(!(identifierJSON.getJSONObject(i).isNull("use"))) {
                	theIdentifier.setUse(Identifier.IdentifierUse.fromCode(identifierJSON.getJSONObject(i).getString("use")));	
            	}
        		
        		if(!(identifierJSON.getJSONObject(i).isNull("type"))) {
        			CodeableConcept identifierCodeableConcept = new CodeableConcept();
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("type").isNull("coding"))) {
            			JSONArray iCodingJSON = identifierJSON.getJSONObject(i).getJSONObject("type").getJSONArray("coding");
            			int noOfCodings = iCodingJSON.length();
            			List<Coding> theCodingList = new ArrayList<Coding>();
            			for(int j = 0; j < noOfCodings; j++) {
            				Coding identifierCoding = new Coding();
                			if(!(iCodingJSON.getJSONObject(j).isNull("system"))) {
                				identifierCoding.setSystem(iCodingJSON.getJSONObject(j).getString("system"));
                			}
                			if(!(iCodingJSON.getJSONObject(j).isNull("code"))) {
                				identifierCoding.setCode(iCodingJSON.getJSONObject(j).getString("code"));
                			}
                			if(!(iCodingJSON.getJSONObject(j).isNull("display"))) {
                				identifierCoding.setDisplay(iCodingJSON.getJSONObject(j).getString("display"));
                			}
                			theCodingList.add(identifierCoding);
            			}
                    	
            			identifierCodeableConcept.setCoding(theCodingList);
            		}
                	theIdentifier.setType(identifierCodeableConcept);
            	}
        		
        		if(!(identifierJSON.getJSONObject(i).isNull("system"))) {
                	theIdentifier.setSystem(identifierJSON.getJSONObject(i).getString("system"));
            	}
            	
            	if(!(identifierJSON.getJSONObject(i).isNull("value"))) {
                	theIdentifier.setValue(identifierJSON.getJSONObject(i).getString("value"));
            	}
            	
            	if(!(identifierJSON.getJSONObject(i).isNull("period"))) {
            		Period identifierPeriod = new Period();
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("period").isNull("start"))) {
                        Date identifierSDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject(i).getJSONObject("period").getString("start"));
                        identifierPeriod.setStart(identifierSDate);
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("period").isNull("end"))) {
                        Date identifierEDate = CommonUtil.convertStringToDate(identifierJSON.getJSONObject(i).getJSONObject("period").getString("end"));
                        identifierPeriod.setStart(identifierEDate);
            		}
                    theIdentifier.setPeriod(identifierPeriod);
            	}

            	if(!(identifierJSON.getJSONObject(i).isNull("assigner"))) {
        			Reference identifierReference = new Reference(); 
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("display"))) {
                        identifierReference.setDisplay(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("display"));
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("reference"))) {
                        identifierReference.setReference(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("reference"));
            		}
            		if(!(identifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("type"))) {
                        identifierReference.setReference(identifierJSON.getJSONObject(i).getJSONObject("assigner").getString("type"));
            		}
                    theIdentifier.setAssigner(identifierReference);
            	}
         
            	identifiers.add(theIdentifier);
        	}
        	specimen.setIdentifier(identifiers);
        }
        
        //Set accessionIdentifier
        if(!(specimenJSON.isNull("accessionIdentifier"))) {
        	JSONObject aIdentifierJSON = specimenJSON.getJSONObject("accessionIdentifier");
        	Identifier theAIdentifier = new Identifier();
        	
    		if(!(aIdentifierJSON.isNull("use"))) {
            	theAIdentifier.setUse(Identifier.IdentifierUse.fromCode(aIdentifierJSON.getString("use")));	
        	}
    		
    		if(!(aIdentifierJSON.isNull("type"))) {
    			CodeableConcept aIdentifierType = new CodeableConcept();
        		if(!(aIdentifierJSON.getJSONObject("type").isNull("coding"))) {
        			JSONArray aICodingJSON = aIdentifierJSON.getJSONObject("type").getJSONArray("coding");
        			int noOfAICodings = aICodingJSON.length();
        			List<Coding> theAICodingList = new ArrayList<Coding>();
        			for(int j = 0; j < noOfAICodings; j++) {
        				Coding aICoding = new Coding();
            			if(!(aICodingJSON.getJSONObject(j).isNull("system"))) {
            				aICoding.setSystem(aICodingJSON.getJSONObject(j).getString("system"));
            			}
            			if(!(aICodingJSON.getJSONObject(j).isNull("code"))) {
            				aICoding.setCode(aICodingJSON.getJSONObject(j).getString("code"));
            			}
            			theAICodingList.add(aICoding);
        			}
                	
        			aIdentifierType.setCoding(theAICodingList);
        		}
            	theAIdentifier.setType(aIdentifierType);
        	}
    		
    		if(!(aIdentifierJSON.isNull("system"))) {
            	theAIdentifier.setSystem(aIdentifierJSON.getString("system"));
        	}
        	
        	if(!(aIdentifierJSON.isNull("value"))) {
            	theAIdentifier.setValue(aIdentifierJSON.getString("value"));
        	}
        	
        	if(!(aIdentifierJSON.isNull("period"))) {
        		Period identifierPeriod = new Period();
        		if(!(aIdentifierJSON.getJSONObject("period").isNull("start"))) {
                    Date identifierSDate = CommonUtil.convertStringToDate(aIdentifierJSON.getJSONObject("period").getString("start"));
                    identifierPeriod.setStart(identifierSDate);
        		}
                theAIdentifier.setPeriod(identifierPeriod);
        	}

        	if(!(aIdentifierJSON.isNull("assigner"))) {
    			Reference identifierReference = new Reference(); 
        		if(!(aIdentifierJSON.getJSONObject("assigner").isNull("display"))) {
                    identifierReference.setReference(aIdentifierJSON.getJSONObject("assigner").getString("display"));
        		}
                theAIdentifier.setAssigner(identifierReference);
        	}
    
        	specimen.setAccessionIdentifier(theAIdentifier);
        }

        //set status
        if(!(specimenJSON.isNull("status"))) {
            specimen.setStatus(Specimen.SpecimenStatus.fromCode(specimenJSON.getString("status")));
        }

        //Set type
        if(!(specimenJSON.isNull("type"))) {
        	JSONObject typeJSON = specimenJSON.getJSONObject("type");
    		CodeableConcept theType = new CodeableConcept();
    		if(!(typeJSON.isNull("coding"))) {
				JSONArray tCodingJSON = typeJSON.getJSONArray("coding");
				int noOfTCodings = tCodingJSON.length();
				List<Coding> tCodingList = new ArrayList<Coding>();
				for(int j = 0; j < noOfTCodings; j++) {
					Coding theTCoding = new Coding();

					if(!(tCodingJSON.getJSONObject(j).isNull("system"))) {
						theTCoding.setSystem(tCodingJSON.getJSONObject(j).getString("system"));
					}
					if(!(tCodingJSON.getJSONObject(j).isNull("code"))) {
						theTCoding.setCode(tCodingJSON.getJSONObject(j).getString("code"));
					}
					if(!(tCodingJSON.getJSONObject(j).isNull("display"))) {
						theTCoding.setDisplay(tCodingJSON.getJSONObject(j).getString("display"));
					}
					tCodingList.add(theTCoding);
				}
				theType.setCoding(tCodingList);
			}
    		if(!typeJSON.isNull("text")) {
        		theType.setText(typeJSON.getString("text"));
    		}
        	specimen.setType(theType);
        }
        
		//Set subject
        if(!(specimenJSON.isNull("subject"))) {
        	JSONObject subjectJSON = specimenJSON.getJSONObject("subject");
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
        	specimen.setSubject(theSubject);
        }
        
        
        //Set receivedTime
        if(!(specimenJSON.isNull("receivedTime"))) {
        	Date date = CommonUtil.convertStringToDate(specimenJSON.getString("receivedTime"));
        	specimen.setReceivedTime(date);
        }
        
        //Set parent
        if(!(specimenJSON.isNull("parent"))) {
        	JSONArray parentJSON = specimenJSON.getJSONArray("parent");
        	int noOfParents = parentJSON.length();
        	List<Reference> parentList = new ArrayList<Reference>();
        	for(int k = 0; k < noOfParents; k++) {
        		Reference theParent = new Reference();
            	if(!parentJSON.getJSONObject(k).isNull("reference")) {
            		theParent.setReference(parentJSON.getJSONObject(k).getString("reference"));
            	}
            	if(!parentJSON.getJSONObject(k).isNull("display")) {
            		theParent.setDisplay(parentJSON.getJSONObject(k).getString("display"));
            	}
            	if(!parentJSON.getJSONObject(k).isNull("type")) {
            		theParent.setType(parentJSON.getJSONObject(k).getString("type"));
            	}
            	parentList.add(theParent);
        	}
        	specimen.setParent(parentList);
        }
        
        //Set request
        if(!(specimenJSON.isNull("request"))) {
        	JSONArray requestJSON = specimenJSON.getJSONArray("request");
        	int noOfRequests = requestJSON.length();
        	List<Reference> requestList = new ArrayList<Reference>();
        	for(int r = 0; r < noOfRequests; r++) {
        		Reference theRequest = new Reference();
            	if(!requestJSON.getJSONObject(r).isNull("reference")) {
            		theRequest.setReference(requestJSON.getJSONObject(r).getString("reference"));
            	}
            	if(!requestJSON.getJSONObject(r).isNull("display")) {
            		theRequest.setDisplay(requestJSON.getJSONObject(r).getString("display"));
            	}
            	if(!requestJSON.getJSONObject(r).isNull("type")) {
            		theRequest.setType(requestJSON.getJSONObject(r).getString("type"));
            	}
            	requestList.add(theRequest);
        	}
        	specimen.setRequest(requestList);
        }
        
        //Set collection
        if(!(specimenJSON.isNull("collection"))) {
        	JSONObject collectionJSON = specimenJSON.getJSONObject("collection");
        	SpecimenCollectionComponent theCollection = new SpecimenCollectionComponent();
        	//Set collector
        	if(!(collectionJSON.isNull("collector"))) {
        		Reference theCollector = new Reference();
            	if(!collectionJSON.getJSONObject("collector").isNull("reference")) {
            		theCollector.setReference(collectionJSON.getJSONObject("collector").getString("reference"));
            	}
            	if(!collectionJSON.getJSONObject("collector").isNull("display")) {
            		theCollector.setDisplay(collectionJSON.getJSONObject("collector").getString("display"));
            	}
            	if(!collectionJSON.getJSONObject("collector").isNull("type")) {
            		theCollector.setType(collectionJSON.getJSONObject("collector").getString("type"));
            	}
            	theCollection.setCollector(theCollector);
        	}
        	//Set collectedDateTime
//        	if(!(collectionJSON.isNull("collectedDateTime"))) {
//        		Date collectedDateTime = CommonUtil.convertStringToDate(collectionJSON.getString("collectedDateTime"));
//        		DateType dt = new DateType();
//        		dt.setValue(collectedDateTime);
//        		theCollection.setCollected(dt);
//        	}
        	//Set quantity
        	if(!(collectionJSON.isNull("quantity"))) {
        		JSONObject quantityJSON = collectionJSON.getJSONObject("quantity");
        		SimpleQuantity sq = new SimpleQuantity();
        		if(!(quantityJSON.isNull("value"))) {
        			sq.setValue(quantityJSON.getLong("value"));
        		}
        		if(!(quantityJSON.isNull("unit"))) {
        			sq.setUnit(quantityJSON.getString("unit"));
        		}
        		theCollection.setQuantity(sq);
        	}
        	//Set method
        	if(!(collectionJSON.isNull("method"))) {
        		JSONObject methodJSON = collectionJSON.getJSONObject("method");
        		CodeableConcept theMethod = new CodeableConcept();
        		if(!(methodJSON.isNull("coding"))) {
        			JSONArray mCodingJSON = methodJSON.getJSONArray("coding");
        			int noOfMethods = mCodingJSON.length();
        			List<Coding> methodCodingList = new ArrayList<Coding>();
        			for(int m = 0; m < noOfMethods; m++) {
        				Coding methodCoding = new Coding();
        				if(!(mCodingJSON.getJSONObject(m).isNull("system"))) {
        					methodCoding.setSystem(mCodingJSON.getJSONObject(m).getString("system"));
        				}
        				if(!(mCodingJSON.getJSONObject(m).isNull("code"))) {
        					methodCoding.setCode(mCodingJSON.getJSONObject(m).getString("code"));
        				}
        				if(!(mCodingJSON.getJSONObject(m).isNull("display"))) {
        					methodCoding.setDisplay(mCodingJSON.getJSONObject(m).getString("display"));
        				}
        				methodCodingList.add(methodCoding);
        			}
        			theMethod.setCoding(methodCodingList);
        		}
        		if(!(methodJSON.isNull("text"))) {
        			theMethod.setText(methodJSON.getString("text"));
        		}
        		theCollection.setMethod(theMethod);
        	}
        	
        	//Set bodySite
        	if(!(collectionJSON.isNull("bodySite"))) {
        		JSONObject bodySiteJSON = collectionJSON.getJSONObject("bodySite");
        		CodeableConcept theBodySite = new CodeableConcept();
        		if(!(bodySiteJSON.isNull("coding"))) {
        			JSONArray bsCodingJSON = bodySiteJSON.getJSONArray("coding");
        			int noOfBodySites = bsCodingJSON.length();
        			List<Coding> bsCodingList = new ArrayList<Coding>();
        			for(int m = 0; m < noOfBodySites; m++) {
        				Coding bsCoding = new Coding();
        				if(!(bsCodingJSON.getJSONObject(m).isNull("system"))) {
        					bsCoding.setSystem(bsCodingJSON.getJSONObject(m).getString("system"));
        				}
        				if(!(bsCodingJSON.getJSONObject(m).isNull("code"))) {
        					bsCoding.setCode(bsCodingJSON.getJSONObject(m).getString("code"));
        				}
        				if(!(bsCodingJSON.getJSONObject(m).isNull("display"))) {
        					bsCoding.setDisplay(bsCodingJSON.getJSONObject(m).getString("display"));
        				}
        				bsCodingList.add(bsCoding);
        			}
        			theBodySite.setCoding(bsCodingList);
        		}
        		if(!(bodySiteJSON.isNull("text"))) {
        			theBodySite.setText(bodySiteJSON.getString("text"));
        		}
        		theCollection.setBodySite(theBodySite);
        	}
        	specimen.setCollection(theCollection);
        }
        
        //Set container
        if(!(specimenJSON.isNull("container"))) {
        	JSONArray containerJSON = specimenJSON.getJSONArray("container");
        	int noOfContainers = containerJSON.length();
        	List<SpecimenContainerComponent> containerList = new ArrayList<SpecimenContainerComponent>();
        	for(int n = 0; n < noOfContainers; n++) {
        		SpecimenContainerComponent theContainer = new SpecimenContainerComponent();
        		//Set identifier
        		if(!(containerJSON.getJSONObject(n).isNull("identifier"))) {
        			JSONArray cIdentifierJSON = containerJSON.getJSONObject(n).getJSONArray("identifier");
        			int noOfCIdentifiers = cIdentifierJSON.length();
                	List<Identifier> identifierList = new ArrayList<Identifier>();
                	Identifier theCIdentifier = new Identifier();
                	
                	for(int i = 0; i < noOfCIdentifiers; i++) {
                		if(!(cIdentifierJSON.getJSONObject(i).isNull("use"))) {
                        	theCIdentifier.setUse(Identifier.IdentifierUse.fromCode(cIdentifierJSON.getJSONObject(i).getString("use")));	
                    	}
                		
                		if(!(cIdentifierJSON.getJSONObject(i).isNull("type"))) {
                			CodeableConcept identifierType = new CodeableConcept();
                    		if(!(cIdentifierJSON.getJSONObject(i).getJSONObject("type").isNull("coding"))) {
                    			JSONArray cICodingJSON = cIdentifierJSON.getJSONObject(i).getJSONObject("type").getJSONArray("coding");
                    			int noOfCCodings = cICodingJSON.length();
                    			List<Coding> theCodingList = new ArrayList<Coding>();
                    			for(int j = 0; j < noOfCCodings; j++) {
                    				Coding identifierCoding = new Coding();
                        			if(!(cICodingJSON.getJSONObject(j).isNull("system"))) {
                        				identifierCoding.setSystem(cICodingJSON.getJSONObject(j).getString("system"));
                        			}
                        			if(!(cICodingJSON.getJSONObject(j).isNull("code"))) {
                        				identifierCoding.setCode(cICodingJSON.getJSONObject(j).getString("code"));
                        			}
                        			theCodingList.add(identifierCoding);
                    			}
                            	
                    			identifierType.setCoding(theCodingList);
                    		}
                        	theCIdentifier.setType(identifierType);
                    	}
                		
                		if(!(cIdentifierJSON.getJSONObject(i).isNull("system"))) {
                        	theCIdentifier.setSystem(cIdentifierJSON.getJSONObject(i).getString("system"));
                    	}
                    	
                    	if(!(cIdentifierJSON.getJSONObject(i).isNull("value"))) {
                        	theCIdentifier.setValue(cIdentifierJSON.getJSONObject(i).getString("value"));
                    	}
                    	
                    	if(!(cIdentifierJSON.getJSONObject(i).isNull("period"))) {
                    		Period identifierPeriod = new Period();
                    		if(!(cIdentifierJSON.getJSONObject(i).getJSONObject("period").isNull("start"))) {
                                Date identifierSDate = CommonUtil.convertStringToDate(cIdentifierJSON.getJSONObject(i).getJSONObject("period").getString("start"));
                                identifierPeriod.setStart(identifierSDate);
                    		}
                            theCIdentifier.setPeriod(identifierPeriod);
                    	}

                    	if(!(cIdentifierJSON.getJSONObject(i).isNull("assigner"))) {
                			Reference identifierReference = new Reference(); 
                    		if(!(cIdentifierJSON.getJSONObject(i).getJSONObject("assigner").isNull("display"))) {
                                identifierReference.setReference(cIdentifierJSON.getJSONObject(i).getJSONObject("assigner").getString("display"));
                    		}
                            theCIdentifier.setAssigner(identifierReference);
                    	}
                 
                    	identifierList.add(theCIdentifier);
                	}
                	theContainer.setIdentifier(identifierList);
        		}
        		//Set description
        		if(!(containerJSON.getJSONObject(n).isNull("description"))) {
        			theContainer.setDescription(containerJSON.getJSONObject(n).getString("description"));
        		}
        		//Set type
        		if(!(containerJSON.getJSONObject(n).isNull("type"))) {
        			JSONObject containerTypeJSON = containerJSON.getJSONObject(n).getJSONObject("type");
            		CodeableConcept theContainerType = new CodeableConcept();
            		if(!(containerTypeJSON.isNull("coding"))) {
            			JSONArray typeCodingJSON = containerTypeJSON.getJSONArray("coding");
            			int noOfTypes = typeCodingJSON.length();
            			List<Coding> containerTypeList = new ArrayList<Coding>();
            			for(int t = 0; t < noOfTypes; t++) {
            				Coding typeCoding = new Coding();
            				if(!(typeCodingJSON.getJSONObject(t).isNull("system"))) {
            					typeCoding.setSystem(typeCodingJSON.getJSONObject(t).getString("system"));
            				}
            				if(!(typeCodingJSON.getJSONObject(t).isNull("code"))) {
            					typeCoding.setCode(typeCodingJSON.getJSONObject(t).getString("code"));
            				}
            				if(!(typeCodingJSON.getJSONObject(t).isNull("display"))) {
            					typeCoding.setDisplay(typeCodingJSON.getJSONObject(t).getString("display"));
            				}
            				containerTypeList.add(typeCoding);
            			}
            			theContainerType.setCoding(containerTypeList);
            		}
            		if(!(containerTypeJSON.isNull("text"))) {
            			theContainerType.setText(containerTypeJSON.getString("text"));
            		}
            		theContainer.setType(theContainerType);
        		}
        		//Set capacity
            	if(!(containerJSON.getJSONObject(n).isNull("capacity"))) {
            		JSONObject capacityJSON = containerJSON.getJSONObject(n).getJSONObject("capacity");
            		SimpleQuantity theCapacity = new SimpleQuantity();
            		if(!(capacityJSON.isNull("value"))) {
            			theCapacity.setValue(capacityJSON.getLong("value"));
            		}
            		if(!(capacityJSON.isNull("unit"))) {
            			theCapacity.setUnit(capacityJSON.getString("unit"));
            		}
            		theContainer.setCapacity(theCapacity);
            	}
            	//Set specimenQuantity
            	if(!(containerJSON.getJSONObject(n).isNull("specimenQuantity"))) {
            		JSONObject sQuantityJSON = containerJSON.getJSONObject(n).getJSONObject("specimenQuantity");
            		SimpleQuantity theSQuantity = new SimpleQuantity();
            		if(!(sQuantityJSON.isNull("value"))) {
            			theSQuantity.setValue(sQuantityJSON.getLong("value"));
            		}
            		if(!(sQuantityJSON.isNull("unit"))) {
            			theSQuantity.setUnit(sQuantityJSON.getString("unit"));
            		}
            		theContainer.setSpecimenQuantity(theSQuantity);
            	}
            	//Set additiveReference
            	if(!(containerJSON.getJSONObject(n).isNull("additiveReference"))) {
            		JSONObject additiveReferenceJSON = containerJSON.getJSONObject(n).getJSONObject("additiveReference");
            		Reference theAdditiveReference = new Reference();
            		if(!(additiveReferenceJSON.isNull("reference"))) {
            			theAdditiveReference.setReference(additiveReferenceJSON.getString("reference"));
            		}
            		if(!(additiveReferenceJSON.isNull("type"))) {
            			theAdditiveReference.setType(additiveReferenceJSON.getString("type"));
            		}
            		if(!(additiveReferenceJSON.isNull("display"))) {
            			theAdditiveReference.setDisplay(additiveReferenceJSON.getString("display"));
            		}
            		theContainer.setAdditive(theAdditiveReference);
            	}
        		containerList.add(theContainer);
        	}
        	specimen.setContainer(containerList);
        }
        //Set processing
        if(!(specimenJSON.isNull("processing"))) {
        	JSONArray processingJSON = specimenJSON.getJSONArray("processing");
        	int noOfProcessings = processingJSON.length();
        	List<SpecimenProcessingComponent> processingList = new ArrayList<SpecimenProcessingComponent>();
        	for(int p = 0; p < noOfProcessings; p++) {
        		SpecimenProcessingComponent theProcessing = new SpecimenProcessingComponent();
        		if(!(processingJSON.getJSONObject(p).isNull("description"))) {
        			theProcessing.setDescription(processingJSON.getJSONObject(p).getString("description"));
        		}
//        		if(!(processingJSON.getJSONObject(p).isNull("timeDateTime"))) {
//        			Date time = CommonUtil.convertStringToDate(processingJSON.getJSONObject(p).getString("timeDateTime"));
//        			theProcessing.setTime(time);
//        		}
        		if(!(processingJSON.getJSONObject(p).isNull("procedure"))) {
        			JSONObject procedureJSON = processingJSON.getJSONObject(p).getJSONObject("procedure");
        			CodeableConcept theProcedure = new CodeableConcept();
        			if(!(procedureJSON.isNull("coding"))) {
        				JSONArray procedureCodingJSON = procedureJSON.getJSONArray("coding");
        				int noOfProcedureCoding = procedureCodingJSON.length();
        				List<Coding> procedureCodingList = new ArrayList<Coding>();
        				for(int c = 0; c < noOfProcedureCoding; c++) {
        					Coding theProcedureCoding = new Coding();
        					if(!(procedureCodingJSON.getJSONObject(c).isNull("system"))) {
        						theProcedureCoding.setSystem(procedureCodingJSON.getJSONObject(c).getString("system"));
        					}
        					if(!(procedureCodingJSON.getJSONObject(c).isNull("code"))) {
        						theProcedureCoding.setCode(procedureCodingJSON.getJSONObject(c).getString("code"));
        					}
        					if(!(procedureCodingJSON.getJSONObject(c).isNull("display"))) {
        						theProcedureCoding.setDisplay(procedureCodingJSON.getJSONObject(c).getString("display"));
        					}
        					procedureCodingList.add(theProcedureCoding);
        				} 
        				theProcedure.setCoding(procedureCodingList);
        			}
        			if(!(procedureJSON.isNull("text"))) {
        				theProcedure.setText(procedureJSON.getString("text"));
        			}
        			theProcessing.setProcedure(theProcedure);
        		}
        		//Set additive
        		if(!(processingJSON.getJSONObject(p).isNull("additive"))) {
        			JSONArray additiveJSON = processingJSON.getJSONObject(p).getJSONArray("additive");
        			int noOfAdditives = additiveJSON.length();
        			List<Reference> additiveList = new ArrayList<Reference>();
        			for(int a = 0; a < noOfAdditives; a++) {
        				Reference theAdditive = new Reference();
        				if(!(additiveJSON.getJSONObject(a).isNull("display"))) {
        					theAdditive.setDisplay(additiveJSON.getJSONObject(a).getString("display"));
        				}
        				if(!(additiveJSON.getJSONObject(a).isNull("type"))) {
        					theAdditive.setType(additiveJSON.getJSONObject(a).getString("type"));
        				}
        				if(!(additiveJSON.getJSONObject(a).isNull("reference"))) {
        					theAdditive.setReference(additiveJSON.getJSONObject(a).getString("reference"));
        				}
        				additiveList.add(theAdditive);
        			}
        			theProcessing.setAdditive(additiveList);
        		}
        		processingList.add(theProcessing);
        	}
        	specimen.setProcessing(processingList);
        }
        //Set note
        if(!(specimenJSON.isNull("note"))) {
        	JSONArray noteJSON = specimenJSON.getJSONArray("note");
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
        	specimen.setNote(noteList);
        }
        return specimen;
    }
}
