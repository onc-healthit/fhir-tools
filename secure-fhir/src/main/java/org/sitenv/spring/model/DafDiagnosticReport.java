package org.sitenv.spring.model;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "diagnosticreport")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafDiagnosticReport {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "status")
    private String status;

    @Column(name = "cat_code")
    private String cat_code;

    @Column(name = "cat_system")
    private String cat_system;

    @Column(name = "cat_display")
    private String cat_display;

    @Column(name = "cat_text")
    private String cat_text;

    @Column(name = "code")
    private String code;

    @Column(name = "code_system")
    private String code_system;

    @Column(name = "code_display")
    private String code_display;

    @Column(name = "code_text")
    private String code_text;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient")
    private DafPatient patient;

    @Column(name = "effectivedate")
    @Temporal(TemporalType.DATE)
    private Date effectivedate;

    @Column(name = "issued")
    @Temporal(TemporalType.DATE)
    private Date issued;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "performer")
    private DafPractitioner performer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "result")
    private DafObservation result;

    @Column(name = "identifier_system")
    private String identifier_system;

    @Column(name = "identifier_value")
    private String identifier_value;
    
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCat_code() {
        return cat_code;
    }

    public void setCat_code(String cat_code) {
        this.cat_code = cat_code;
    }

    public String getCat_system() {
        return cat_system;
    }

    public void setCat_system(String cat_system) {
        this.cat_system = cat_system;
    }

    public String getCat_display() {
        return cat_display;
    }

    public void setCat_display(String cat_display) {
        this.cat_display = cat_display;
    }

    public String getCat_text() {
        return cat_text;
    }

    public void setCat_text(String cat_text) {
        this.cat_text = cat_text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode_system() {
        return code_system;
    }

    public void setCode_system(String code_system) {
        this.code_system = code_system;
    }

    public String getCode_display() {
        return code_display;
    }

    public void setCode_display(String code_display) {
        this.code_display = code_display;
    }

    public String getCode_text() {
        return code_text;
    }

    public void setCode_text(String code_text) {
        this.code_text = code_text;
    }

    public DafPatient getPatient() {
        return patient;
    }

    public void setPatient(DafPatient patient) {
        this.patient = patient;
    }

    public Date getEffectivedate() {
        return effectivedate;
    }

    public void setEffectivedate(Date effectivedate) {
        this.effectivedate = effectivedate;
    }

    public Date getIssued() {
        return issued;
    }

    public void setIssued(Date issued) {
        this.issued = issued;
    }

    public DafPractitioner getPerformer() {
        return performer;
    }

    public void setPerformer(DafPractitioner performer) {
        this.performer = performer;
    }

    public DafObservation getResult() {
        return result;
    }

    public void setResult(DafObservation result) {
        this.result = result;
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
}
