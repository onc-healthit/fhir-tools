package org.sitenv.spring.model;

import java.util.ArrayList;
import java.util.Date;

public class TaskOutput {
	
	private Date transactionStartTime;
	
	private String secure;
	
	ArrayList<ExtractionTaskInfo> output;
	
	public TaskOutput() {
		output = new ArrayList<ExtractionTaskInfo>();
	}

	public Date getTransactionStartTime() {
		return transactionStartTime;
	}

	public void setTransactionStartTime(Date transactionStartTime) {
		this.transactionStartTime = transactionStartTime;
	}


	public String getSecure() {
		return secure;
	}

	public void setSecure(String secure) {
		this.secure = secure;
	}

	public ArrayList<ExtractionTaskInfo> getOutput() {
		return output;
	}

	public void setOutput(ArrayList<ExtractionTaskInfo> output) {
		this.output = output;
	}
	
	public void add(ExtractionTaskInfo etInfo) {
		output.add(etInfo);
	}

}
