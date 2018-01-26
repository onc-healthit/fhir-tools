package org.sitenv.spring.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Prabhushankar.Byrapp on 8/8/2015.
 */


@Entity
@Table(name = "document_reference")
public class DafDocumentReference {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "master_dentifier_system")
    private String masterIdentifierSystem;

    @Column(name = "master_dentifier_value")
    private String masterIdentifierValue;

    @Column(name = "identifier_use")
    private String identifierUse;

    @Column(name = "identifier_system")
    private String identifierSystem;

    @Column(name = "identifier_value")
    private String identifierValue;

    @OneToOne
    @JoinColumn(name = "subject")
    private DafPatient dafPatient;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "type")
    private DafDocumentTypeCodes dafDocumentTypeCodes;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "class_code")
    private DafDocumentClassCodes dafDocumentClassCodes;

    @Column(name = "document_format")
    private String documentFormat;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author")
    private DafAuthor dafAuthor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "custodian")
    private DafCustodian dafCustodian;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "authenticator")
    private DafAuthenticator dafAuthenticator;

    @Column(name = "created", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "indexed", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date indexed;

    @Column(name = "status")
    private String status;

    @Column(name = "doc_status")
    private String documentStatus;

    @Column(name = "description")
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "confidentiality")
    private DafConfidentiality dafConfidentiality;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "content")
    private DafContent dafContent;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "context_event")
    private DafContextEvent dafContextEvent;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "context_period")
    private DafContextPeriod dafContextPeriod;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "context_facility_type")
    private DafContextFacilityType dafContextFacilityType;
    
    @Column(name="last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMasterIdentifierSystem() {
        return masterIdentifierSystem;
    }

    public void setMasterIdentifierSystem(String masterIdentifierSystem) {
        this.masterIdentifierSystem = masterIdentifierSystem;
    }

    public String getMasterIdentifierValue() {
        return masterIdentifierValue;
    }

    public void setMasterIdentifierValue(String masterIdentifierValue) {
        this.masterIdentifierValue = masterIdentifierValue;
    }

    public String getIdentifierUse() {
        return identifierUse;
    }

    public void setIdentifierUse(String identifierUse) {
        this.identifierUse = identifierUse;
    }

    public String getIdentifierSystem() {
        return identifierSystem;
    }

    public void setIdentifierSystem(String identifierSystem) {
        this.identifierSystem = identifierSystem;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    public DafPatient getDafPatient() {
        return dafPatient;
    }

    public void setDafPatient(DafPatient dafPatient) {
        this.dafPatient = dafPatient;
    }

    public DafDocumentTypeCodes getDafDocumentTypeCodes() {
        return dafDocumentTypeCodes;
    }

    public void setDafDocumentTypeCodes(DafDocumentTypeCodes dafDocumentTypeCodes) {
        this.dafDocumentTypeCodes = dafDocumentTypeCodes;
    }

    public DafDocumentClassCodes getDafDocumentClassCodes() {
        return dafDocumentClassCodes;
    }

    public void setDafDocumentClassCodes(DafDocumentClassCodes dafDocumentClassCodes) {
        this.dafDocumentClassCodes = dafDocumentClassCodes;
    }

    public String getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(String documentFormat) {
        this.documentFormat = documentFormat;
    }


    public DafAuthor getDafAuthor() {
        return dafAuthor;
    }

    public void setDafAuthor(DafAuthor dafAuthor) {
        this.dafAuthor = dafAuthor;
    }

    public DafCustodian getDafCustodian() {
        return dafCustodian;
    }

    public void setDafCustodian(DafCustodian dafCustodian) {
        this.dafCustodian = dafCustodian;
    }

    public DafAuthenticator getDafAuthenticator() {
        return dafAuthenticator;
    }

    public void setDafAuthenticator(DafAuthenticator dafAuthenticator) {
        this.dafAuthenticator = dafAuthenticator;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getIndexed() {
        return indexed;
    }

    public void setIndexed(Date indexed) {
        this.indexed = indexed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DafConfidentiality getDafConfidentiality() {
        return dafConfidentiality;
    }

    public void setDafConfidentiality(DafConfidentiality dafConfidentiality) {
        this.dafConfidentiality = dafConfidentiality;
    }

    public DafContent getDafContent() {
        return dafContent;
    }

    public void setDafContent(DafContent dafContent) {
        this.dafContent = dafContent;
    }

    public DafContextEvent getDafContextEvent() {
        return dafContextEvent;
    }

    public void setDafContextEvent(DafContextEvent dafContextEvent) {
        this.dafContextEvent = dafContextEvent;
    }

    public DafContextPeriod getDafContextPeriod() {
        return dafContextPeriod;
    }

    public void setDafContextPeriod(DafContextPeriod dafContextPeriod) {
        this.dafContextPeriod = dafContextPeriod;
    }

    public DafContextFacilityType getDafContextFacilityType() {
        return dafContextFacilityType;
    }

    public void setDafContextFacilityType(DafContextFacilityType dafContextFacilityType) {
        this.dafContextFacilityType = dafContextFacilityType;
    }
}
