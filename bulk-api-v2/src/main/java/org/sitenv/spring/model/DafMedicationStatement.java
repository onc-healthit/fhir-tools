package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "medication_statement")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafMedicationStatement {

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

    @Column(name = "wasnottaken")
    private boolean wasnottaken;

    @Column(name = "reasonnottaken")
    private String reasonnottaken;

    @Column(name = "effectivePeriod")
    @Temporal(TemporalType.DATE)
    private Date effectivePeriod;

    @Column(name = "medicationcodeableconcept")
    @Type(type = "StringJsonObject")
    private String medicationcodeableconcept;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medicationreference")
    private DafMedication medicationreference;

    @Column(name = "dosage")
    @Type(type = "StringJsonObject")
    private String dosage;

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

    public boolean isWasnottaken() {
        return wasnottaken;
    }

    public void setWasnottaken(boolean wasnottaken) {
        this.wasnottaken = wasnottaken;
    }

    public String getReasonnottaken() {
        return reasonnottaken;
    }

    public void setReasonnottaken(String reasonnottaken) {
        this.reasonnottaken = reasonnottaken;
    }

    public Date getEffectivePeriod() {
        return effectivePeriod;
    }

    public void setEffectivePeriod(Date effectivePeriod) {
        this.effectivePeriod = effectivePeriod;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
