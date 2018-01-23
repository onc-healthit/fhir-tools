package org.sitenv.spring.query;

public class CarePlanSearchCriteria extends SearchCriteria {

    private Integer patient;
    private String cat_code;
    private String Status;
    public CarePlanSearchCriteria() {
    }

    public Integer getPatient() {
        return patient;
    }

    public void setPatient(Integer patient) {
        this.patient = patient;
    }

    public String getCat_code() {
        return cat_code;
    }

    public void setCat_code(String cat_code) {
        this.cat_code = cat_code;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }


    public void reset() {
        this.setPatient(null);
        this.setCat_code(null);
        this.setStatus(null);
    }
}
