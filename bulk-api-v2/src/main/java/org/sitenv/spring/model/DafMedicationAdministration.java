package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import java.util.Date;

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
    @JoinColumn(name = "patient")
    private DafPatient patient;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "practitioner")
    private DafPractitioner practitioner;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prescription")
    private DafMedicationOrder prescription;

    @Column(name = "wasnotgiven")
    private boolean wasnotgiven;

    @Column(name = "reasonnotgiven")
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

    public DafPatient getPatient() {
        return patient;
    }

    public void setPatient(DafPatient patient) {
        this.patient = patient;
    }

    public DafPractitioner getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(DafPractitioner practitioner) {
        this.practitioner = practitioner;
    }

    public DafMedicationOrder getPrescription() {
        return prescription;
    }

    public void setPrescription(DafMedicationOrder prescription) {
        this.prescription = prescription;
    }

    public Boolean getWasnotgiven() {
        return wasnotgiven;
    }

    public void setWasnotgiven(boolean wasnotgiven) {
        this.wasnotgiven = wasnotgiven;
    }

    public void setWasnotgiven(Boolean wasnotgiven) {
        this.wasnotgiven = wasnotgiven;
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
}
