package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "immunization")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
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

    @Column(name = "notgiven")
    @Type(type = "true_false")
    private boolean notGiven;

    @Column(name = "primary_source")
    @Type(type = "true_false")
    private boolean primarySource;

    @Column(name = "practitioner_role")
    @Type(type = "StringJsonObject")
    private String practitionerRole;

    @Column(name = "practitioner_actor")
    @Type(type = "StringJsonObject")
    private String practitionerActor;

    @Column(name = "report_origin")
    @Type(type = "StringJsonObject")
    private String reportOrigin;

    public DafImmunization() {

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

    public boolean isNotGiven() {
        return notGiven;
    }

    public void setNotGiven(boolean notGiven) {
        this.notGiven = notGiven;
    }

    public boolean isPrimarySource() {
        return primarySource;
    }

    public void setPrimarySource(boolean primarySource) {
        this.primarySource = primarySource;
    }

    public String getPractitionerRole() {
        return practitionerRole;
    }

    public void setPractitionerRole(String practitionerRole) {
        this.practitionerRole = practitionerRole;
    }

    public String getPractitionerActor() {
        return practitionerActor;
    }

    public void setPractitionerActor(String practitionerActor) {
        this.practitionerActor = practitionerActor;
    }

    public String getReportOrigin() {
        return reportOrigin;
    }

    public void setReportOrigin(String reportOrigin) {
        this.reportOrigin = reportOrigin;
    }
}
