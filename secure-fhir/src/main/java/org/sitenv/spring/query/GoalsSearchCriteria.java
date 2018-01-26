package org.sitenv.spring.query;


import ca.uhn.fhir.rest.param.DateRangeParam;

public class GoalsSearchCriteria extends SearchCriteria {

    private Integer patient;
    private DateRangeParam date;
    private String status;
    public GoalsSearchCriteria() {
    }

    public Integer getPatient() {
        return patient;
    }

    public void setPatient(Integer patient) {
        this.patient = patient;
    }

    public DateRangeParam getDate() {
        return date;
    }

    public void setDate(DateRangeParam date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //=================================
    // Public Methods
    //=================================
    public void reset() {
        this.setPatient(null);
        this.setDate(null);
        this.setStatus(null);
    }
}
