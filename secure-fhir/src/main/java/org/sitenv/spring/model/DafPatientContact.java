package org.sitenv.spring.model;

import javax.persistence.*;

/**
 * Created by Prabhushankar.Byrapp on 8/25/2015.
 */

@Entity
@Table(name = "patient_contact")
public class DafPatientContact {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private DafPatientJson patient;

    @Column(name = "relationship")
    private String relationship;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address_use")
    private String addressUse;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "address_city")
    private String city;

    @Column(name = "address_state")
    private String state;

    @Column(name = "address_zip")
    private String zip;

    @Column(name = "address_country")
    private String country;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DafPatientJson getPatient() {
        return patient;
    }

    public void setPatient(DafPatientJson patient) {
        this.patient = patient;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressUse() {
        return addressUse;
    }

    public void setAddressUse(String addressUse) {
        this.addressUse = addressUse;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
