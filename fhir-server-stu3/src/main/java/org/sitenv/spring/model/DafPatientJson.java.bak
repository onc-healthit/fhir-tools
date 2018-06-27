package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Prabhushankar.Byrapp on 8/11/2015.
 */

@Entity
@Table(name = "patient_json")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafPatientJson {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "identifier")
    @Type(type = "StringJsonObject")
    private String identifier;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "telecom")
    @Type(type = "StringJsonObject")
    private String telecom;

    @Column(name = "gender")
    private String gender;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;


    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "address_city")
    private String addressCity;

    @Column(name = "address_state")
    private String addressState;

    @Column(name = "address_zip")
    private String addressZip;

    @Column(name = "address_country")
    private String addressCountry;

    @Column(name = "communication_language")
    private String language;

    @Column(name = "active")
    @Type(type = "true_false")
    private boolean active;

    @Column(name = "mothers_maiden_name")
    private String mothersMaidenName;

    @Column(name = "birth_place")
    @Type(type = "StringJsonObject")
    private String birthPlace;

    @OneToMany(mappedBy = "patient", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<DafPatientContact> contacts;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "marital_status")
    private DafMaritalStatus maritalStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "race")
    private DafRace race;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ethnicity")
    private DafEthnicity ethnicity;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "religion")
    private DafReligion religion;
    
    /*@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="birthsex")
    private DafBirthSex birthsex;*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getTelecom() {
        return telecom;
    }

    public void setTelecom(String telecom) {
        this.telecom = telecom;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
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

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getAddressZip() {
        return addressZip;
    }

    public void setAddressZip(String addressZip) {
        this.addressZip = addressZip;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getMothersMaidenName() {
        return mothersMaidenName;
    }

    public void setMothersMaidenName(String mothersMaidenName) {
        this.mothersMaidenName = mothersMaidenName;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public Set<DafPatientContact> getContacts() {
        return contacts;
    }

    public void setContacts(Set<DafPatientContact> contacts) {
        this.contacts = contacts;
    }

    public DafMaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(DafMaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public DafRace getRace() {
        return race;
    }

    public void setRace(DafRace race) {
        this.race = race;
    }

    public DafEthnicity getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(DafEthnicity ethnicity) {
        this.ethnicity = ethnicity;
    }

    public DafReligion getReligion() {
        return religion;
    }

    public void setReligion(DafReligion religion) {
        this.religion = religion;
    }

	/*public DafBirthSex getBirthsex() {
        return birthsex;
	}

	public void setBirthsex(DafBirthSex birthsex) {
		this.birthsex = birthsex;
	}*/
}
