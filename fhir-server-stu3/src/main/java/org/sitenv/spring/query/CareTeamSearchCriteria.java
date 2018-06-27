package org.sitenv.spring.query;

public class CareTeamSearchCriteria extends SearchCriteria {

    private Integer patient;
    private String Status;

    public Integer getPatient() {
        return patient;
    }

    public void setPatient(Integer patient) {
        this.patient = patient;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void reset() {
        this.setPatient(null);
        this.setStatus(null);
    }

}
