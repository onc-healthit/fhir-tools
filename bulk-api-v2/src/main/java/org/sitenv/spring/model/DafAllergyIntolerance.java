package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import java.util.Date;

import javax.persistence.*;

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

    @Column(name = "substance_code")
    private String substance_code;

    @Column(name = "substance_display")
    private String substance_display;

    @Column(name = "status")
    private String status;

    @Column(name = "criticality")
    private String criticality;

    @Column(name = "category")
    private String category;

    @Column(name = "reaction")
    @Type(type = "StringJsonObject")
    private String reaction;

    @Column(name = "substance_system")
    private String substance_system;
    
    @Column(name="last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public String getSubstance_system() {
        return substance_system;
    }

    public void setSubstance_system(String substance_system) {
        this.substance_system = substance_system;
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

    public String getSubstance_code() {
        return substance_code;
    }

    public void setSubstance_code(String substance_code) {
        this.substance_code = substance_code;
    }

    public String getSubstance_display() {
        return substance_display;
    }

    public void setSubstance_display(String substance_display) {
        this.substance_display = substance_display;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

}
