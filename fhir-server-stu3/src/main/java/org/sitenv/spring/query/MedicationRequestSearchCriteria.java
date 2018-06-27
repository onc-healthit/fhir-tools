package org.sitenv.spring.query;

public class MedicationRequestSearchCriteria extends SearchCriteria {

    private Integer subject;

    public Integer getSubject() {
        return subject;
    }

    public void setSubject(Integer subject) {
        this.subject = subject;
    }

    //=================================
    // Public Methods
    //=================================
    public void reset() {
        this.setSubject(null);
    }

}
