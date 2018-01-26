package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "medication_order")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafMedicationOrder {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "identifier_system")
    private String identifierSystem;

    @Column(name = "identifier_value")
    private String identifierValue;

    @Column(name = "datewritten")
    @Temporal(TemporalType.DATE)
    private Date dateWritten;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient")
    private DafPatient patient;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prescriber")
    private DafPractitioner prescriber;

    @Column(name = "medicationcodeableconcept")
    @Type(type = "StringJsonObject")
    private String medicationcodeableconcept;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medicationreference")
    private DafMedication medicationreference;

    @Column(name = "dosageinstruction")
    @Type(type = "StringJsonObject")
    private String dosageinstruction;

    @Column(name = "dispencerequest")
    @Type(type = "StringJsonObject")
    private String dispencerequest;

    @Column(name = "status")
    private String status;
    
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

    public Date getDateWritten() {
        return dateWritten;
    }

    public void setDateWritten(Date dateWritten) {
        this.dateWritten = dateWritten;
    }

    public DafPatient getPatient() {
        return patient;
    }

    public void setPatient(DafPatient patient) {
        this.patient = patient;
    }

    public DafPractitioner getPrescriber() {
        return prescriber;
    }

    public void setPrescriber(DafPractitioner prescriber) {
        this.prescriber = prescriber;
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

    public String getDosageinstruction() {
        return dosageinstruction;
    }

    public void setDosageinstruction(String dosageinstruction) {
        this.dosageinstruction = dosageinstruction;
    }

    public String getDispencerequest() {
        return dispencerequest;
    }

    public void setDispencerequest(String dispencerequest) {
        this.dispencerequest = dispencerequest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
