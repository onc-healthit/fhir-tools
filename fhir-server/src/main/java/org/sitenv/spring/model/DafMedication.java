package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;

@Entity
@Table(name = "medication")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafMedication {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "med_code")
    private String med_code;

    @Column(name = "med_isbrand")
    @Type(type = "true_false")
    private boolean med_isBrand;

    @Column(name = "med_form")
    private String med_form;

    @Column(name = "med_ingredient")
    @Type(type = "StringJsonObject")
    private String med_ingredient;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "med_manufacturer")
    private DafManufacturer dafManufacturer;

    @Column(name = "med_display")
    private String med_display;

    @Column(name = "med_system")
    private String med_system;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMed_code() {
        return med_code;
    }

    public void setMed_code(String med_code) {
        this.med_code = med_code;
    }

    public boolean isMed_isBrand() {
        return med_isBrand;
    }

    public void setMed_isBrand(boolean med_isBrand) {
        this.med_isBrand = med_isBrand;
    }

    public String getMed_form() {
        return med_form;
    }

    public void setMed_form(String med_form) {
        this.med_form = med_form;
    }

    public String getMed_ingredient() {
        return med_ingredient;
    }

    public void setMed_ingredient(String med_ingredient) {
        this.med_ingredient = med_ingredient;
    }

    public DafManufacturer getDafManufacturer() {
        return dafManufacturer;
    }

    public void setDafManufacturer(DafManufacturer dafManufacturer) {
        this.dafManufacturer = dafManufacturer;
    }

    public String getMed_display() {
        return med_display;
    }

    public void setMed_display(String med_display) {
        this.med_display = med_display;
    }

    public String getMed_system() {
        return med_system;
    }

    public void setMed_system(String med_system) {
        this.med_system = med_system;
    }

}
