package org.sitenv.spring.query;

public class DeviceSearchCriteria {

    private Integer patient;

    public Integer getPatient() {
        return patient;
    }

    public void setPatient(Integer patient) {
        this.patient = patient;
    }

    public void reset() {
        this.setPatient(null);
    }


}
