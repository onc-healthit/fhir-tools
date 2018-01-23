package org.sitenv.spring.query;

import ca.uhn.fhir.rest.param.TokenParam;

public class LocationSearchCriteria {
	
	private String name;
	private String address;
	private String city;
	private String state;
	private String postal;
	private TokenParam identifier;

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}


	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostal() {
		return postal;
	}


	public void setPostal(String postal) {
		this.postal = postal;
	}


	public TokenParam getIdentifier() {
		return identifier;
	}


	public void setIdentifier(TokenParam identifier) {
		this.identifier = identifier;
	}


		public void reset() {
	        this.setIdentifier(null);
	        this.setName(null);
	        this.setAddress(null);
	        this.setCity(null);
	        this.setState(null);
	        this.setPostal(null);
	    }

}
