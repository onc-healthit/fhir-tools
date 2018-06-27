package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "allergy_intolerance")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafAllergyIntolerance {

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
    @JoinColumn(name = "recorder")
    private DafPractitioner recorder;

    @Column(name = "clinical_status")
    private String clinicalStatus;

    @Column(name = "criticality")
    private String criticality;

    @Column(name = "category")
    private String category;

    @Column(name = "reaction")
    @Type(type = "StringJsonObject")
    private String reaction;

    @Column(name = "verification_status")
    private String verificationStatus;

    @Column(name = "code")
    @Type(type = "StringJsonObject")
    private String code;

    @Column(name = "asserted_date")
    @Temporal(TemporalType.DATE)
    private Date assertedDate;

    @Column(name = "asserter")
    private Integer asserter;

    public DafAllergyIntolerance() {

    }

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

    public DafPractitioner getRecorder() {
        return recorder;
    }

    public void setRecorder(DafPractitioner recorder) {
        this.recorder = recorder;
    }

    public String getClinicalStatus() {
        return clinicalStatus;
    }

    public void setClinicalStatus(String clinicalStatus) {
        this.clinicalStatus = clinicalStatus;
    }

    public String getCriticality() {
        return criticality;
    }

    public void setCriticality(String criticality) {
        this.criticality = criticality;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getAssertedDate() {
        return assertedDate;
    }

    public void setAssertedDate(Date assertedDate) {
        this.assertedDate = assertedDate;
    }

    public Integer getAsserter() {
        return asserter;
    }

    public void setAsserter(Integer asserter) {
        this.asserter = asserter;
    }

}
