package org.sitenv.spring.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "immunization")
public class DafImmunization {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "status")
    private String status;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "vaccine_system")
    private String vaccine_system;

    @Column(name = "vaccine_code")
    private String vaccine_code;

    @Column(name = "vaccine_display")
    private String vaccine_display;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient")
    private DafPatient patient;

    @Column(name = "wasnotgiven")
    @Type(type = "true_false")
    private boolean wasnotgiven;

    @Column(name = "reported")
    @Type(type = "true_false")
    private boolean reported;
    
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getVaccine_system() {
        return vaccine_system;
    }

    public void setVaccine_system(String vaccine_system) {
        this.vaccine_system = vaccine_system;
    }

    public String getVaccine_code() {
        return vaccine_code;
    }

    public void setVaccine_code(String vaccine_code) {
        this.vaccine_code = vaccine_code;
    }

    public String getVaccine_display() {
        return vaccine_display;
    }

    public void setVaccine_display(String vaccine_display) {
        this.vaccine_display = vaccine_display;
    }

    public DafPatient getPatient() {
        return patient;
    }

    public void setPatient(DafPatient patient) {
        this.patient = patient;
    }

    public boolean isWasnotgiven() {
        return wasnotgiven;
    }

    public void setWasnotgiven(boolean wasnotgiven) {
        this.wasnotgiven = wasnotgiven;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

}
