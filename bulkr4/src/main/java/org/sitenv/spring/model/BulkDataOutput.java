package org.sitenv.spring.model;


import java.util.ArrayList;

public class BulkDataOutput {
	
	private String transactionTime;
	private String request;
	private Boolean requiresAccessToken;
	private ArrayList<String> error;
	ArrayList<BulkDataOutputInfo> output;
	
	public BulkDataOutput() {
		output = new ArrayList<BulkDataOutputInfo>();
	}

	public String getTransactionStartTime() {
		return transactionTime;
	}

	public void setTransactionStartTime(String transactionStartTime) {
		this.transactionTime = transactionStartTime;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public Boolean getSecure() {
		return requiresAccessToken;
	}

	public void setSecure(Boolean secure) {
		this.requiresAccessToken = secure;
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
	public ArrayList<String> getError() {
		return error;
	}

	public void setError(ArrayList<String> error) {
		this.error = error;
	}
	
}
