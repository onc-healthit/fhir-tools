package org.sitenv.spring;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu.valueset.QuantityCompararatorEnum;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.valueset.DocumentReferenceStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafDocumentReference;
import org.sitenv.spring.service.DocumentReferenceService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Prabhushankar.Byrapp on 8/7/2015.
 */

@Component
@Scope("request")
public class DocumentReferenceResourceProvider implements IResourceProvider {


    public static final String RESOURCE_TYPE = "DocumentReference";
    public static final String VERSION_ID = "2.0";
    AbstractApplicationContext context;
    DocumentReferenceService service;

    public DocumentReferenceResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (DocumentReferenceService) context.getBean("documentReferenceService");
    }


    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<DocumentReference> getResourceType() {
        return DocumentReference.class;
    }

    /**
     * This method returns all the available document reference records.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/DocumentReference?_pretty=true&_format=json
     */
    @Search
    public List<DocumentReference> getAllDocumentReference(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        List<DafDocumentReference> dafDocRefList = service.getAllDocumentReference();
        List<DocumentReference> docRefList = new ArrayList<DocumentReference>();

        for (DafDocumentReference dafDocRef : dafDocRefList) {
            docRefList.add(createDocumentReferenceObject(dafDocRef));
        }

        return docRefList;
    }

public List<DocumentReference> getDocumentReferenceForBulkDataRequest(List<Integer> patients, Date start) {
    	
    	
		
		List<DafDocumentReference> dafDocumentReferenceList = service.getDocumentReferenceForBulkData(patients, start);

		List<DocumentReference> docRefList = new ArrayList<DocumentReference>();

		for (DafDocumentReference dafDocumentReference : dafDocumentReferenceList) {
			docRefList.add(createDocumentReferenceObject(dafDocumentReference));
		}
		
		return docRefList;
	}
    
    /**
     * The "@Read" annotation indicates that this method supports the
     * read operation. Read operations should return a single resource
     * instance.
     *
     * @param theId The read operation takes one parameter, which must be of type
     *              IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/DocumentReference/1?_pretty=true&_format=json
     */
    @Read()
    public DocumentReference getResourceById(@IdParam IdDt theId) {
        DafDocumentReference dafDocRef = service.getDocumentReferenceById(theId.getIdPartAsLong().intValue());
        DocumentReference docRef = createDocumentReferenceObject(dafDocRef);
        return docRef;
    }
    
    @Operation(name="$docref", idempotent=true)
    public List<DocumentReference> getResourceByDocRefId(@OperationParam (name = DocumentReference.SP_PATIENT) ReferenceParam thePatient,
    		@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
    	System.out.println("inOperation Docref");
    	String patientId = thePatient.getIdPart();
        List<DafDocumentReference> dafDocRefList = service.getDocumentReferenceBySubject(patientId);

        List<DocumentReference> docRefList = new ArrayList<DocumentReference>();

        for (DafDocumentReference dafDocRef : dafDocRefList) {
            docRefList.add(createDocumentReferenceObject(dafDocRef));
        }

        if (theIncludes.size() > 0) {
            IdDt practitionerId = docRefList.get(0).getAuthor().get(0).getReference();
            PractitionerResourceProvider practitioner = new PractitionerResourceProvider();
            Practitioner prac = practitioner.getPractitionerResourceById(practitionerId);
            docRefList.get(0).getAuthor().get(0).setResource(prac);
        }
        return docRefList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the
     * search operation. This searches by DocumentRederence identifier.
     *
     * @param theId This operation takes one parameter which is the search criteria. It is
     *              annotated with the "@Required" annotation. This annotation takes one argument,
     *              a string containing the name of the search criteria.
     * @return This method returns a list of DocumentReference. This list may contain multiple
     * matching resources, or it may also be empty.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/DocumentReference?_pretty=true&_format=json&identifier=urn:foo|7766
     */
    @Search()
    public List<DocumentReference> searchByIdentifier(@RequiredParam(name = DocumentReference.SP_IDENTIFIER) TokenParam theId,
                                                      @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String identifierSystem = theId.getSystem();
        String identifierValue = theId.getValue();

        List<DafDocumentReference> dafDocRefList = service.getDocumentReferenceByIdentifier(identifierSystem, identifierValue);

        List<DocumentReference> docRefList = new ArrayList<DocumentReference>();

        for (DafDocumentReference dafDocRef : dafDocRefList) {
            docRefList.add(createDocumentReferenceObject(dafDocRef));
        }

        return docRefList;
    }


    /**
     * The "@Search" annotation indicates that this method supports the
     * search operation. This searches by subject.
     *
     * @param thePatient This operation takes one parameter which is the search criteria.
     * @return This method returns a list of DocumentReference. This list may contain multiple
     * matching resources, or it may also be empty.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/DocumentReference?_pretty=true&_format=json&subject=test2
     */
    @Search()
    public List<DocumentReference> searchBySubject(@RequiredParam(name = DocumentReference.SP_SUBJECT) ReferenceParam thePatient,
                                                   @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        List<DafDocumentReference> dafDocRefList = service.getDocumentReferenceBySubject(patientId);

        List<DocumentReference> docRefList = new ArrayList<DocumentReference>();

        for (DafDocumentReference dafDocRef : dafDocRefList) {
            docRefList.add(createDocumentReferenceObject(dafDocRef));
        }

        return docRefList;
    }

	/**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by patient
     *
     * @param thePatient
	 * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of DocumentReferences. This list may contain multiple matching resources, or it may also be empty.
     * 
     *  Ex: http://<server name>/<context>/fhir/DocumentReference?patient=1&_format=json
     */
    @Search()
    public List<DocumentReference> searchByPatient(@RequiredParam(name = DocumentReference.SP_PATIENT) ReferenceParam thePatient,
                                                   @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String patientId = thePatient.getIdPart();
        System.out.println("In Compartment Patient");
        List<DafDocumentReference> dafDocRefList = service.getDocumentReferenceBySubject(patientId);

        List<DocumentReference> docRefList = new ArrayList<DocumentReference>();

        for (DafDocumentReference dafDocRef : dafDocRefList) {
            docRefList.add(createDocumentReferenceObject(dafDocRef));
        }

        if (theIncludes.size() > 0) {
            IdDt practitionerId = docRefList.get(0).getAuthor().get(0).getReference();
            PractitionerResourceProvider practitioner = new PractitionerResourceProvider();
            Practitioner prac = practitioner.getPractitionerResourceById(practitionerId);
            docRefList.get(0).getAuthor().get(0).setResource(prac);
        }
        return docRefList;
    }

    /**
     * The "@Search" annotation indicates that this method supports the
     * search operation. This searches by subject.identifier.
     *
     * @param subject This operation takes one parameter which is the search criteria. It is
     *                annotated with the "@Required" annotation. This annotation takes one argument,
     *                a string containing the name of the search criteria.
     * @return This method returns a list of DocumentReference. This list may contain multiple
     * matching resources, or it may also be empty.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/DocumentReference?_pretty=true&_format=json&subject.identifier=SSN|2298817777
     */
    @Search
    public List<DocumentReference> findBySubject(
            @RequiredParam(name = DocumentReference.SP_SUBJECT, chainWhitelist = {Patient.SP_IDENTIFIER}) ReferenceParam subject,
            @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        List<DocumentReference> docRefList = new ArrayList<DocumentReference>();

        String chain = subject.getChain();
        FhirContext fhirContext = new FhirContext();
        if (Patient.SP_IDENTIFIER.equals(chain)) {
            TokenParam subjectToken = subject.toTokenParam(fhirContext);
            String system = subjectToken.getSystem();
            String value = subjectToken.getValue();

            List<DafDocumentReference> dafDocRefList = service.getDocumentReferenceBySubjectIdentifier(system, value);

            for (DafDocumentReference dafDocRef : dafDocRefList) {
                docRefList.add(createDocumentReferenceObject(dafDocRef));
            }
        }

        return docRefList;
    }

	/**
     * The "@Search" annotation indicates that this method supports the
     * search operation. This searches by status.
     *
     * @param thePatient
	 * @param type
     * @return This method returns a list of DocumentReference. This list may contain multiple
     * matching resources, or it may also be empty.
     * <p/>
     * 
     */
    @Search()
    public List<DocumentReference> findDocumentResourceWithType(
            @RequiredParam(name = DocumentReference.SP_TYPE) TokenParam type,
            @OptionalParam(name = DocumentReference.SP_PATIENT) ReferenceParam thePatient,
            @OptionalParam(name = DocumentReference.SP_PERIOD) DateRangeParam thePeriod,
            @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        String identifierSystem = type.getSystem();
        String identifierCode = type.getValue();
        List<DafDocumentReference> dafDocRefList;

        if (thePatient != null) {
            dafDocRefList = service.getDocumentReferenceByTypeOptional(thePatient.getIdPart(), identifierSystem, identifierCode);
        } else {
            dafDocRefList = service.getDocumentReferenceByType(identifierSystem, identifierCode);
        }

        List<DocumentReference> docRefList = new ArrayList<DocumentReference>();

        for (DafDocumentReference dafDocRef : dafDocRefList) {
            docRefList.add(createDocumentReferenceObject(dafDocRef));
        }

        return docRefList;
    }

	/**
     * The "@Search" annotation indicates that this method supports the
     * search operation. This searches by status.
     *
     * @param status This operation takes one parameter which is the search criteria.
     * @return This method returns a list of DocumentReference. This list may contain multiple
     * matching resources, or it may also be empty.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/DocumentReference?_pretty=true&_format=json&status=active
     */
    @Search()
    public List<DocumentReference> searchByStatus(@RequiredParam(name = DocumentReference.SP_STATUS) String status,
                                                  @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafDocumentReference> dafDocRefList = service.getDocumentReferenceByStatus(status);

        List<DocumentReference> docRefList = new ArrayList<DocumentReference>();

        for (DafDocumentReference dafDocRef : dafDocRefList) {
            docRefList.add(createDocumentReferenceObject(dafDocRef));
        }

        return docRefList;
    }


    /**
     * The "@Search" annotation indicates that this method supports the
     * search operation. This searches by Created date.
     *
     * @param theDate This operation takes one parameter which is the search criteria.
     * @return This method returns a list of DocumentReference. This list may contain multiple
     * matching resources, or it may also be empty.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/DocumentReference?_pretty=true&_format=json&created=>=2005-12-24
     */
    @Search()
    public List<DocumentReference> searchByCreated(@RequiredParam(name = DocumentReference.SP_CREATED) DateParam theDate,
                                                   @OptionalParam(name = DocumentReference.SP_PATIENT) ReferenceParam thePatient,
                                                   @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        QuantityCompararatorEnum comparator = theDate.getComparator(); // e.g. <=
        String comparatorStr = "";
        if (comparator != null)
            comparatorStr = comparator.getCode();

        Date createdDate = theDate.getValueAsDateTimeDt().getValue(); // e.g. 2011-01-02
        TemporalPrecisionEnum precision = theDate.getPrecision(); // e.g. DAY

        
        List<DafDocumentReference> dafDocRefList;
        if (thePatient != null) {
            dafDocRefList = service.getDocumentReferenceByCreatedDateOptional(thePatient.getIdPart(), comparatorStr, createdDate);
        } else {
            dafDocRefList = service.getDocumentReferenceByCreatedDate(comparatorStr, createdDate);
        }

        List<DocumentReference> docRefList = new ArrayList<DocumentReference>();

        for (DafDocumentReference dafDocRef : dafDocRefList) {
            docRefList.add(createDocumentReferenceObject(dafDocRef));
        }

        return docRefList;
    }


    /**
     * The "@Search" annotation indicates that this method supports the
     * search operation. This searches by Context Period start and end date.
     *
     * @param theRange This operation takes one parameter which is the search criteria.
     * @return This method returns a list of DocumentReference. This list may contain multiple
     * matching resources, or it may also be empty.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/DocumentReference?_pretty=true&_format=json&period=>=2004-12-22&period=<=2004-12-22
     */
    @Search()
    public List<DocumentReference> searchByPeriod(@RequiredParam(name = DocumentReference.SP_PERIOD) DateRangeParam theRange,
                                                  @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        Date fromDate = theRange.getLowerBoundAsInstant();
        QuantityCompararatorEnum fromComparator = theRange.getLowerBound().getComparator(); // e.g. <=
        String fromComparatorStr = "";
        if (fromComparator != null)
            fromComparatorStr = fromComparator.getCode();

        Date toDate = theRange.getUpperBoundAsInstant();
        String toComparatorStr = "";

        if (toDate != null) {
            QuantityCompararatorEnum toComparator = theRange.getUpperBound().getComparator(); // e.g. <=
            if (toComparator != null)
                toComparatorStr = toComparator.getCode();
        }
        List<DafDocumentReference> dafDocRefList = service.getDocumentReferenceByPeriod(fromComparatorStr, fromDate, toComparatorStr, toDate);

        List<DocumentReference> docRefList = new ArrayList<DocumentReference>();

        for (DafDocumentReference dafDocRef : dafDocRefList) {
            docRefList.add(createDocumentReferenceObject(dafDocRef));
        }

        return docRefList;
    }


    /**
     * This method converts DafDocumentReference object to DocumentReference object
     */
    private DocumentReference createDocumentReferenceObject(DafDocumentReference dafDocRef) {
        DocumentReference docRef = new DocumentReference();


        // Set Version
        docRef.setId(new IdDt(RESOURCE_TYPE, dafDocRef.getId() + "", VERSION_ID));

        //MasterIdentifier
        docRef.setMasterIdentifier(new IdentifierDt(dafDocRef.getMasterIdentifierSystem(), dafDocRef.getMasterIdentifierValue()));

        //identifier
        docRef.addIdentifier();
        docRef.getIdentifier().get(0).setUse(IdentifierUseEnum.OFFICIAL);
        docRef.getIdentifier().get(0).setSystem(new UriDt(dafDocRef.getIdentifierSystem()));
        docRef.getIdentifier().get(0).setValue(dafDocRef.getIdentifierValue());

        //Subject
        if (dafDocRef.getDafPatient() != null)
            docRef.setSubject(new ResourceReferenceDt(new IdDt("Patient", Integer.toString(dafDocRef.getDafPatient().getId()))));


        //Type
        CodeableConceptDt codeableConceptDt = new CodeableConceptDt();
        CodingDt codingDt = new CodingDt();
        codingDt.setCode(dafDocRef.getDafDocumentTypeCodes().getCode());
        codingDt.setDisplay(dafDocRef.getDafDocumentTypeCodes().getDisplay());
        codingDt.setSystem(dafDocRef.getDafDocumentTypeCodes().getSystem());
        codeableConceptDt.addCoding(codingDt);
        docRef.setType(codeableConceptDt);

        //class code
        CodeableConceptDt classCodeDt = new CodeableConceptDt();
        CodingDt classCodingDt = new CodingDt();
        classCodingDt.setCode(dafDocRef.getDafDocumentClassCodes().getCode());
        classCodingDt.setDisplay(dafDocRef.getDafDocumentClassCodes().getDisplay());
        classCodingDt.setSystem(dafDocRef.getDafDocumentClassCodes().getSystem());
        classCodeDt.addCoding(classCodingDt);
        docRef.setClassElement(classCodeDt);

        //format
        CodingDt formatCodingDt = new CodingDt();
        formatCodingDt.setCode(dafDocRef.getDocumentFormat());
        List<CodingDt> format = new ArrayList<CodingDt>();
        format.add(formatCodingDt);
        //docRef.setFormat(format);


        //Author
        ResourceReferenceDt rrDt = new ResourceReferenceDt();
        ResourceReferenceDt authorReferenceDt1 = new ResourceReferenceDt(new IdDt(dafDocRef.getDafAuthor().getName()));
        List<ResourceReferenceDt> authors = new ArrayList<ResourceReferenceDt>();
        
        authors.add(authorReferenceDt1);
        docRef.setAuthor(authors);

        //custodian
        docRef.setCustodian(new ResourceReferenceDt(dafDocRef.getDafCustodian().getName()));

        //authenticator
        docRef.setAuthenticator(new ResourceReferenceDt(dafDocRef.getDafAuthenticator().getName()));

        //created
        docRef.setCreated(new DateTimeDt(dafDocRef.getCreated()));

        //indexed
        docRef.setIndexed(new InstantDt(dafDocRef.getIndexed()));

        //status
        //docRef.setStatus(DocumentReferenceStatusEnum.valueOf(dafDocRef.getStatus()));
        docRef.setStatus(DocumentReferenceStatusEnum.valueOf(dafDocRef.getStatus()));

        //Doc status - need to convert to new table
        docRef.setDocStatus(new CodeableConceptDt("http://hl7.org/fhir/composition-status", dafDocRef.getDocumentStatus()));

        //Description
        docRef.setDescription(dafDocRef.getDescription());

        //confidentiality
        CodeableConceptDt confCodeDt = new CodeableConceptDt();
        CodingDt confCodingDt = new CodingDt();
        confCodingDt.setCode(dafDocRef.getDafConfidentiality().getCode());
        confCodingDt.setDisplay(dafDocRef.getDafConfidentiality().getDisplay());
        confCodingDt.setSystem(dafDocRef.getDafConfidentiality().getSystem());
        confCodeDt.addCoding(confCodingDt);
        List<CodeableConceptDt> confidentiality = new ArrayList<CodeableConceptDt>();
        confidentiality.add(confCodeDt);
        //docRef.setConfidentiality(confidentiality);

        //content
        DocumentReference.Content content = new DocumentReference.Content();
        content.getAttachment().setContentType(dafDocRef.getDafContent().getContentType());
        content.getAttachment().setLanguage(dafDocRef.getDafContent().getLanguage());
        content.getAttachment().setUrl(dafDocRef.getDafContent().getUrl());
        content.getAttachment().setSize(dafDocRef.getDafContent().getContentSize());
        content.getAttachment().setTitle(dafDocRef.getDafContent().getTitle());
        List<DocumentReference.Content> attachment = new ArrayList<DocumentReference.Content>();
        attachment.add(content);
        docRef.setContent(attachment);
        
        
        /*AttachmentDt attachdt = new AttachmentDt();
        attachdt.setContentType(dafDocRef.getDafContent().getContentType());
        attachdt.setLanguage(dafDocRef.getDafContent().getLanguage());
        attachdt.setUrl(dafDocRef.getDafContent().getUrl());
        attachdt.setSize(dafDocRef.getDafContent().getContentSize());
        attachdt.setTitle(dafDocRef.getDafContent().getTitle());
        List<AttachmentDt> attachment = new ArrayList<AttachmentDt>();
        attachment.add(attachdt);
        docRef.getContent().add(attachdt);
        docRef.setContent(attachment);*/


        //context
        DocumentReference.Context docContext = new DocumentReference.Context();

        //Event
        /*CodeableConceptDt eventDt = new CodeableConceptDt();
        CodingDt eventCodingDt = new CodingDt();
        eventCodingDt.setCode(dafDocRef.getDafContextEvent().getCode());
        eventCodingDt.setDisplay(dafDocRef.getDafContextEvent().getDisplay());
        eventCodingDt.setSystem(dafDocRef.getDafContextEvent().getSystem());
        eventDt.addCoding(eventCodingDt);

        List<CodeableConceptDt> event = new ArrayList<CodeableConceptDt>();
        event.add(eventDt);
        docContext.setEvent(event);*/

        //period
        PeriodDt periodDt = new PeriodDt();
        periodDt.setStart(new DateTimeDt(dafDocRef.getDafContextPeriod().getStart()));
        periodDt.setEnd(new DateTimeDt(dafDocRef.getDafContextPeriod().getEnd()));
        docContext.setPeriod(periodDt);

        //facilityType
        CodeableConceptDt facilityDt = new CodeableConceptDt();
        CodingDt facilityCodingDt = new CodingDt();
        facilityCodingDt.setCode(dafDocRef.getDafContextFacilityType().getCode());
        facilityCodingDt.setDisplay(dafDocRef.getDafContextFacilityType().getDisplay());
        facilityCodingDt.setSystem(dafDocRef.getDafContextFacilityType().getSystem());
        facilityDt.addCoding(facilityCodingDt);
        docContext.setFacilityType(facilityDt);

        docRef.setContext(docContext);

        return docRef;
    }


}
