package org.sitenv.spring.query;

import ca.uhn.fhir.rest.param.TokenParam;

public class OrganizationSearchCriteria extends SearchCriteria{
	
	private Integer organizationId;
	private TokenParam identifier;
	private String name;
	private String address;
	private String address_city;
	private String address_state;
	private String address_country;
	private String address_postalCode;
	
	public Integer getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}
	public TokenParam getIdentifier() {
		return identifier;
	}
	public void setIdentifier(TokenParam identifier) {
		this.identifier = identifier;
	}
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
	
	public String getAddress_city() {
		return address_city;
	}
	public void setAddress_city(String address_city) {
		this.address_city = address_city;
	}
	public String getAddress_state() {
		return address_state;
	}
	public void setAddress_state(String address_state) {
		this.address_state = address_state;
	}
	public String getAddress_country() {
		return address_country;
	}
	public void setAddress_country(String address_country) {
		this.address_country = address_country;
	}
	public String getAddress_postalCode() {
		return address_postalCode;
	}
	public void setAddress_postalCode(String address_postalCode) {
		this.address_postalCode = address_postalCode;
	}
	//=================================
    // Public Methods
    //=================================
    public void reset() {
        this.setOrganizationId(null);
        this.setIdentifier(null);
        this.setName(null);
        this.setAddress(null);
        this.setAddress_city(null);
        this.setAddress_state(null);
        this.setAddress_country(null);
        this.setAddress_postalCode(null);
    }

}
