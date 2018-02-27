package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import java.util.Date;

import javax.persistence.*;


@Entity
@Table(name = "medication_dispense")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafMedicationDispense {

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
    @JoinColumn(name = "dispenser")
    private DafPractitioner dispenser;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "authorizingprescription")
    private DafMedicationOrder authorizingprescription;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medicationreference")
    private DafMedication medicationreference;

    @Column(name = "dosageinstruction")
    @Type(type = "StringJsonObject")
    private String dosageinstruction;

    @Column(name = "status")
    private String status;
    
    @Column(name = "medicationcodeableconcept")
    @Type(type = "StringJsonObject")
    private String medicationcodeableconcept; 

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

    public DafPatient getPatient() {
        return patient;
    }

    public void setPatient(DafPatient patient) {
        this.patient = patient;
    }

    public DafPractitioner getDispenser() {
        return dispenser;
    }

    public void setDispenser(DafPractitioner dispenser) {
        this.dispenser = dispenser;
    }

    public DafMedicationOrder getAuthorizingprescription() {
        return authorizingprescription;
    }

    public void setAuthorizingprescription(
            DafMedicationOrder authorizingprescription) {
        this.authorizingprescription = authorizingprescription;
    }

    public DafMedication getMedicationreference() {
        return medicationreference;
    }

    public void setMedicationreference(DafMedication medicationreference) {
        this.medicationreference = medicationreference;
    }

    public String getDosageinstruction() {
        return dosageinstruction;
    }

    public void setDosageinstruction(String dosageinstruction) {
        this.dosageinstruction = dosageinstruction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public String getMedicationcodeableconcept() {
		return medicationcodeableconcept;
	}

	public void setMedicationcodeableconcept(String medicationcodeableconcept) {
		this.medicationcodeableconcept = medicationcodeableconcept;
	}
}
