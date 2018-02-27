package org.sitenv.spring.query;

import ca.uhn.fhir.rest.param.DateRangeParam;

public class ProcedureSearchCriteria {
	
	private Integer subject;
	private String status;
	private DateRangeParam rangedates;
	public Integer getSubject() {
		return subject;
	}
	public void setSubject(Integer subject) {
		this.subject = subject;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public DateRangeParam getRangedates() {
		return rangedates;
	}
	public void setRangedates(DateRangeParam rangedates) {
		this.rangedates = rangedates;
	}
	public void reset(){
		this.setSubject(null);
		this.setStatus(null);
		this.setRangedates(null);
	}

}
