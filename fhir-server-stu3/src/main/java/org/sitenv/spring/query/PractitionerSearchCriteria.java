package org.sitenv.spring.query;

import ca.uhn.fhir.rest.param.TokenParam;

public class PractitionerSearchCriteria extends SearchCriteria {

    private Integer patient;
    private String familyName;
    private String givenName;
    private TokenParam identifier;

    public Integer getPatient() {
        return patient;
    }

    public void setPatient(Integer patient) {
        this.patient = patient;
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

    //=================================
    // Public Methods
    //=================================
    public void reset() {
        this.setPatient(null);
        this.setFamilyName(null);
        this.setGivenName(null);
        this.setIdentifier(null);
    }

}
