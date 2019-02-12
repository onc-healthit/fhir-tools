package org.sitenv.spring.model;


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

    @Column(name = "description")
    private String description;

    @Column(name = "textstatus")
    private String textstatus;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTextstatus() {
        return textstatus;
    }

    public void setTextstatus(String textstatus) {
        this.textstatus = textstatus;
    }
}
