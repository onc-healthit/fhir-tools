package org.sitenv.spring.query;

public class MedicationSearchCriteria extends SearchCriteria {

    private String code;

    public MedicationSearchCriteria() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    //=================================
    // Public Methods
    //=================================
    public void reset() {
        this.setCode(null);
    }


}
