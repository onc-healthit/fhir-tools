package org.sitenv.spring.query;

import ca.uhn.fhir.rest.param.DateRangeParam;

import java.util.List;

public class DiagnosticReportSearchCriteria extends SearchCriteria {

    private Integer patient;
    private String category;
    private List<String> code;
    private DateRangeParam date;
    public DiagnosticReportSearchCriteria() {
    }

    public Integer getPatient() {
        return patient;
    }

    public void setPatient(Integer patient) {
        this.patient = patient;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getCode() {
        return code;
    }

    public void setCode(List<String> code) {
        this.code = code;
    }

    public DateRangeParam getDate() {
        return date;
    }

    public void setDate(DateRangeParam date) {
        this.date = date;
    }

    //=================================
    // Public Methods
    //=================================
    public void reset() {
        this.setPatient(null);
        this.setCategory(null);
        this.setCode(null);
        this.setDate(null);
    }

}
