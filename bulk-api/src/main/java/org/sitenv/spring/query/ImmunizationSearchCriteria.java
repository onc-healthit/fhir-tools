package org.sitenv.spring.query;

public class ImmunizationSearchCriteria extends SearchCriteria {

    private Integer patient;
    private String status;
    public ImmunizationSearchCriteria() {
    }

    public Integer getPatient() {
        return patient;
    }

    public void setPatient(Integer patient) {
        this.patient = patient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void reset() {
        this.setPatient(null);
        this.setStatus(null);
    }
}
