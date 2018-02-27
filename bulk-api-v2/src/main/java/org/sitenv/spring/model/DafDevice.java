package org.sitenv.spring.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "device")
public class DafDevice {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "status")
    private String status;

    @Column(name = "udi")
    private String udi;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient")
    private DafPatient patient;

    @Column(name = "type_code")
    private String type_code;

    @Column(name = "type_system")
    private String type_system;

    @Column(name = "type_display")
    private String type_display;

    @Column(name = "text_status")
    private String text_status;

    @Column(name = "text_div")
    private String text_div;

    @Column(name = "identifier_value")
    private String identifier_value;

    @Column(name = "identifier_system")
    private String identifier_system;

    @Column(name = "identifier_type_code")
    private String identifier_type_code;

    @Column(name = "identifier_type_system")
    private String identifier_type_system;

    @Column(name = "identifier_type_text")
    private String identifier_type_text;

    @Column(name = "identifier_type_value")
    private String identifier_type_value;
    
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

    public String getUdi() {
        return udi;
    }

    public void setUdi(String udi) {
        this.udi = udi;
    }

    public DafPatient getPatient() {
        return patient;
    }

    public void setPatient(DafPatient patient) {
        this.patient = patient;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public String getType_system() {
        return type_system;
    }

    public void setType_system(String type_system) {
        this.type_system = type_system;
    }

    public String getType_display() {
        return type_display;
    }

    public void setType_display(String type_display) {
        this.type_display = type_display;
    }

    public String getText_status() {
        return text_status;
    }

    public void setText_status(String text_status) {
        this.text_status = text_status;
    }

    public String getText_div() {
        return text_div;
    }

    public void setText_div(String text_div) {
        this.text_div = text_div;
    }

    public String getIdentifier_value() {
        return identifier_value;
    }

    public void setIdentifier_value(String identifier_value) {
        this.identifier_value = identifier_value;
    }

    public String getIdentifier_system() {
        return identifier_system;
    }

    public void setIdentifier_system(String identifier_system) {
        this.identifier_system = identifier_system;
    }

    public String getIdentifier_type_code() {
        return identifier_type_code;
    }

    public void setIdentifier_type_code(String identifier_type_code) {
        this.identifier_type_code = identifier_type_code;
    }

    public String getIdentifier_type_system() {
        return identifier_type_system;
    }

    public void setIdentifier_type_system(String identifier_type_system) {
        this.identifier_type_system = identifier_type_system;
    }

    public String getIdentifier_type_text() {
        return identifier_type_text;
    }

    public void setIdentifier_type_text(String identifier_type_text) {
        this.identifier_type_text = identifier_type_text;
    }

    public String getIdentifier_type_value() {
        return identifier_type_value;
    }

    public void setIdentifier_type_value(String identifier_type_value) {
        this.identifier_type_value = identifier_type_value;
    }


}
