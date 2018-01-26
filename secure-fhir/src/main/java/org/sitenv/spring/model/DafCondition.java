package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "condition")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafCondition {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "identifier_system")
    private String identifier_system;

    @Column(name = "identifier_value")
    private String identifier_value;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient")
    private DafPatient patient;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "asserter")
    private DafPractitioner asserter;

    @Column(name = "code")
    private String code;

    @Column(name = "code_display")
    private String code_display;

    @Column(name = "category_code")
    private String category_code;

    @Column(name = "category_display")
    private String category_display;

    @Column(name = "category_system")
    private String category_system;

    @Column(name = "clinical_status")
    private String clinical_status;

    @Column(name = "severity_code")
    private String severity_code;

    @Column(name = "severity_display")
    private String severity_display;

    @Column(name = "onset")
    @Type(type = "StringJsonObject")
    private String onset;

    @Column(name = "date_recorded")
    @Temporal(TemporalType.DATE)
    private Date date_recorded;

    @Column(name = "code_system")
    private String code_system;

    @Column(name = "verification_status")
    private String verification_status;
    
    @Column(name="last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifier_system() {
        return identifier_system;
    }

    public void setIdentifier_system(String identifier_system) {
        this.identifier_system = identifier_system;
    }

    public String getIdentifier_value() {
        return identifier_value;
    }

    public void setIdentifier_value(String identifier_value) {
        this.identifier_value = identifier_value;
    }

    public DafPatient getPatient() {
        return patient;
    }

    public void setPatient(DafPatient patient) {
        this.patient = patient;
    }

    public DafPractitioner getAsserter() {
        return asserter;
    }

    public void setAsserter(DafPractitioner asserter) {
        this.asserter = asserter;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode_display() {
        return code_display;
    }

    public void setCode_display(String code_display) {
        this.code_display = code_display;
    }

    public String getCategory_code() {
        return category_code;
    }

    public void setCategory_code(String category_code) {
        this.category_code = category_code;
    }

    public String getCategory_display() {
        return category_display;
    }

    public void setCategory_display(String category_display) {
        this.category_display = category_display;
    }

    public String getCategory_system() {
        return category_system;
    }

    public void setCategory_system(String category_system) {
        this.category_system = category_system;
    }

    public String getClinical_status() {
        return clinical_status;
    }

    public void setClinical_status(String clinical_status) {
        this.clinical_status = clinical_status;
    }

    public String getSeverity_code() {
        return severity_code;
    }

    public void setSeverity_code(String severity_code) {
        this.severity_code = severity_code;
    }

    public String getSeverity_display() {
        return severity_display;
    }

    public void setSeverity_display(String severity_display) {
        this.severity_display = severity_display;
    }

    public String getOnset() {
        return onset;
    }

    public void setOnset(String onset) {
        this.onset = onset;
    }

    public Date getDate_recorded() {
        return date_recorded;
    }

    public void setDate_recorded(Date date_recorded) {
        this.date_recorded = date_recorded;
    }

    public String getCode_system() {
        return code_system;
    }

    public void setCode_system(String code_system) {
        this.code_system = code_system;
    }

    public String getVerification_status() {
        return verification_status;
    }

    public void setVerification_status(String verification_status) {
        this.verification_status = verification_status;
    }

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
