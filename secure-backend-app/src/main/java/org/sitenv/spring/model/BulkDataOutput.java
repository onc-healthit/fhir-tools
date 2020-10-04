package org.sitenv.spring.model;

import java.util.ArrayList;

public class BulkDataOutput {
	
	private String transactionStartTime;
	private String request;
	private String secure;
	ArrayList<BulkDataOutputInfo> output;
	
	public BulkDataOutput() {
		output = new ArrayList<BulkDataOutputInfo>();
	}

	public String getTransactionStartTime() {
		return transactionStartTime;
	}

	public void setTransactionStartTime(String transactionStartTime) {
		this.transactionStartTime = transactionStartTime;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getSecure() {
		return secure;
	}

	public void setSecure(String secure) {
		this.secure = secure;
	}

	public ArrayList<BulkDataOutputInfo> getOutput() {
		return output;
	}

	public void setOutput(ArrayList<BulkDataOutputInfo> output) {
		this.output = output;
	}
	
	public void add(BulkDataOutputInfo bdoi) {
		output.add(bdoi);
	}
	
	
}
