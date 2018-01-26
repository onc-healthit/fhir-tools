package org.sitenv.spring.model;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "observation")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafObservation {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "code")
    private String code;

    @Column(name = "code_system")
    private String code_system;

    @Column(name = "code_display")
    private String code_display;

    @Column(name = "code_text")
    private String code_text;

    @Column(name = "cat_code")
    private String cat_code;

    @Column(name = "cat_system")
    private String cat_system;

    @Column(name = "cat_display")
    private String cat_display;

    @Column(name = "cat_text")
    private String cat_text;

    @Column(name = "val_code")
    private String val_code;

    @Column(name = "val_system")
    private String val_system;

    @Column(name = "val_display")
    private String val_display;

    @Column(name = "val_text")
    private String val_text;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient")
    private DafPatient patient;

    @Column(name = "status")
    private String status;

    @Column(name = "val_quan_value")
    private Double valQuanValue;

    @Column(name = "val_quan_sys")
    private String valQuanSystem;

    @Column(name = "val_quan_unit")
    private String valQuanUnit;

    @Column(name = "effective_date")
    @Temporal(TemporalType.DATE)
    private Date effectiveDate;

    @Column(name = "val_quan_code")
    private String valQuanCode;

    @Column(name = "bp_code")
    private String bp_code;

    @Column(name = "issued_date")
    @Temporal(TemporalType.DATE)
    private Date issued_date;

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

    public String getVal_code() {
        return val_code;
    }

    public void setVal_code(String val_code) {
        this.val_code = val_code;
    }

    public String getVal_system() {
        return val_system;
    }

    public void setVal_system(String val_system) {
        this.val_system = val_system;
    }

    public String getVal_display() {
        return val_display;
    }

    public void setVal_display(String val_display) {
        this.val_display = val_display;
    }

    public String getVal_text() {
        return val_text;
    }

    public void setVal_text(String val_text) {
        this.val_text = val_text;
    }

    public DafPatient getPatient() {
        return patient;
    }

    public void setSubject(DafPatient patient) {
        this.patient = patient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getValQuanValue() {
        return valQuanValue;
    }

    public void setValQuanValue(Double valQuanValue) {
        this.valQuanValue = valQuanValue;
    }

    public String getValQuanSystem() {
        return valQuanSystem;
    }

    public void setValQuanSystem(String valQuanSystem) {
        this.valQuanSystem = valQuanSystem;
    }

    public String getValQuanUnit() {
        return valQuanUnit;
    }

    public void setValQuanUnit(String valQuanUnit) {
        this.valQuanUnit = valQuanUnit;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getValQuanCode() {
        return valQuanCode;
    }

    public void setValQuanCode(String valQuanCode) {
        this.valQuanCode = valQuanCode;
    }

    public String getBp_code() {
        return bp_code;
    }

    public void setBp_code(String bp_code) {
        this.bp_code = bp_code;
    }

    public Date getIssued_date() {
        return issued_date;
    }

    public void setIssued_date(Date issued_date) {
        this.issued_date = issued_date;
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
