package org.sitenv.spring.model;


import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "careplan")
public class DafCarePlan {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient")
    private DafPatient patient;

    @Column(name = "status")
    private String status;

    @Column(name = "cat_code")
    private String cat_code;

    @Column(name = "cat_system")
    private String cat_system;

    @Column(name="last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DafPatient getPatient() {
        return patient;
    }

    public void setPatient(DafPatient patient) {
        this.patient = patient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
