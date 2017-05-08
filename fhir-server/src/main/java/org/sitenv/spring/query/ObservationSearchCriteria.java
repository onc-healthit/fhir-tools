package org.sitenv.spring.query;

import ca.uhn.fhir.rest.param.DateRangeParam;

import java.util.List;

public class ObservationSearchCriteria extends SearchCriteria {

    private Integer patient;
    private Integer observationId;
    private String category;
    private List<String> code;
    private DateRangeParam rangedates;
    public ObservationSearchCriteria() {
    }

    public Integer getPatient() {
        return patient;
    }

    public void setPatient(Integer patient) {
        this.patient = patient;
    }

    public Integer getObservationId() {
		return observationId;
	}

	public void setObservationId(Integer observationId) {
		this.observationId = observationId;
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

    public DateRangeParam getRangedates() {
        return rangedates;
    }

    public void setRangedates(DateRangeParam rangedates) {
        this.rangedates = rangedates;
    }

    //=================================
    // Public Methods
    //=================================
    public void reset() {
        this.setPatient(null);
        this.setCategory(null);
        this.setCode(null);
        this.setRangedates(null);
        ;
    }
}
