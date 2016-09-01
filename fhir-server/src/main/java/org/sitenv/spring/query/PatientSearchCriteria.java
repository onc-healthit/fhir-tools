package org.sitenv.spring.query;

import ca.uhn.fhir.rest.param.TokenParam;

import java.util.Date;

/**
 * Created by Prabhushankar.Byrapp on 8/23/2015.
 */
public class PatientSearchCriteria extends SearchCriteria {

    private String fullName;
    private String searchText;
    private String familyName;
    private String givenName;
    private TokenParam identifier;
    private String language;
    private String telecom;
    private String gender;
    private Date birthDate;
    private String city;
    private String postalCode;
    private String state;
    private String mothersmaidenname;
    private Long age;
    private String race;
    private String ethnicity;
    public PatientSearchCriteria() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
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

    public TokenParam getIdentifier() {
        return identifier;
    }

    public void setIdentifier(TokenParam identifier) {
        this.identifier = identifier;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMothersmaidenname() {
        return mothersmaidenname;
    }

    public void setMothersmaidenname(String mothersmaidenname) {
        this.mothersmaidenname = mothersmaidenname;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    //=================================
    // Public Methods
    //=================================
    public void reset() {
        this.setFullName(null);
        this.setSearchText(null);
        this.setFamilyName(null);
        this.setGivenName(null);
        this.setIdentifier(null);
        this.setLanguage(null);
        this.setTelecom(null);
        this.setGender(null);
        this.setBirthDate(null);
        this.setCity(null);
        this.setPostalCode(null);
        this.setState(null);
        this.setMothersmaidenname(null);
        this.setAge(null);
        this.setRace(null);
        this.setEthnicity(null);
    }


}
