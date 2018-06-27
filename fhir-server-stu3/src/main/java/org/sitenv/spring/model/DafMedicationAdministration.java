package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;


@Entity
@Table(name = "medication_administration")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafMedicationAdministration {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "identifier_system")
    private String identifier_system;

    @Column(name = "identifier_value")
    private String identifier_value;

    @Column(name = "status")
    private String status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject")
    private DafPatient subject;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prescription")
    private DafMedicationRequest prescription;

    @Column(name = "notgiven")
    private boolean notGiven;

    @Column(name = "reasonnotgiven")
    @Type(type = "StringJsonObject")
    private String reasonnotgiven;

    @Column(name = "medicationcodeableconcept")
    @Type(type = "StringJsonObject")
    private String medicationcodeableconcept;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medicationreference")
    private DafMedication medicationreference;

    @Column(name = "dosage")
    @Type(type = "StringJsonObject")
    private String dosage;

    @Column(name = "definition")
    private Integer definition;

    @Column(name = "partof")
    private Integer partof;

    @Column(name = "category")
    @Type(type = "StringJsonObject")
    private String category;

    @Column(name = "context")
    private Integer context;

    @Column(name = "supporting_info")
    private Integer supportingInfo;

    @Column(name = "effective")
    @Type(type = "StringJsonObject")
    private String effective;

    @Column(name = "performer_actor")
    @Type(type = "StringJsonObject")
    private String performerActor;

    @Column(name = "performer_onbehalfof")
    @Type(type = "StringJsonObject")
    private String onbehalfof;

    @Column(name = "reason_code")
    @Type(type = "StringJsonObject")
    private String reasonCode;

    public DafMedicationAdministration() {

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DafPatient getSubject() {
        return subject;
    }

    public void setSubject(DafPatient subject) {
        this.subject = subject;
    }

    public DafMedicationRequest getPrescription() {
        return prescription;
    }

    public void setPrescription(DafMedicationRequest prescription) {
        this.prescription = prescription;
    }

    public boolean isNotGiven() {
        return notGiven;
    }

    public void setNotGiven(boolean notGiven) {
        this.notGiven = notGiven;
    }

    public String getReasonnotgiven() {
        return reasonnotgiven;
    }

    public void setReasonnotgiven(String reasonnotgiven) {
        this.reasonnotgiven = reasonnotgiven;
    }

    public String getMedicationcodeableconcept() {
        return medicationcodeableconcept;
    }

    public void setMedicationcodeableconcept(String medicationcodeableconcept) {
        this.medicationcodeableconcept = medicationcodeableconcept;
    }

    public DafMedication getMedicationreference() {
        return medicationreference;
    }

    public void setMedicationreference(DafMedication medicationreference) {
        this.medicationreference = medicationreference;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public Integer getDefinition() {
        return definition;
    }

    public void setDefinition(Integer definition) {
        this.definition = definition;
    }

    public Integer getPartof() {
        return partof;
    }

    public void setPartof(Integer partof) {
        this.partof = partof;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getContext() {
        return context;
    }

    public void setContext(Integer context) {
        this.context = context;
    }

    public Integer getSupportingInfo() {
        return supportingInfo;
    }

    public void setSupportingInfo(Integer supportingInfo) {
        this.supportingInfo = supportingInfo;
    }

    public String getEffective() {
        return effective;
    }

    public void setEffective(String effective) {
        this.effective = effective;
    }

    public String getPerformerActor() {
        return performerActor;
    }

    public void setPerformerActor(String performerActor) {
        this.performerActor = performerActor;
    }

    public String getOnbehalfof() {
        return onbehalfof;
    }

    public void setOnbehalfof(String onbehalfof) {
        this.onbehalfof = onbehalfof;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }
}
