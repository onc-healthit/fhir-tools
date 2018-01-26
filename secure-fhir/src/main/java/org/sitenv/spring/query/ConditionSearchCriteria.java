package org.sitenv.spring.query;

import java.util.List;

public class ConditionSearchCriteria extends SearchCriteria {

    private Integer patientId;
    private String category;
    private List<String> status;
    public ConditionSearchCriteria() {
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    //=================================
    // Public Methods
    //=================================
    public void reset() {
        this.setPatientId(null);
        this.setCategory(null);
        this.setStatus(null);
    }

}
