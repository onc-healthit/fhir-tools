package org.sitenv.spring.query;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

public class PractitionerSearchCriteria {
	
	    private Integer id;
	    private String communication;
	    private String familyName;
	    private String givenName;
	    private TokenParam identifier;
	    private String telecom;
	    private StringParam gender;
	    private DateParam birthDate;
	    private String city;
	    private String postalCode;
	    private String state;
	    private String active;
	    private String country;
	    private String email;
	    private String phone;
	    
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getCommunication() {
			return communication;
		}
		public void setCommunication(String communication) {
			this.communication = communication;
		}
		public String getFamilyName() {
			return familyName;
		}
		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}
		public String getGivenName() {
			return givenName;
		}
		public void setGivenName(String givenName) {
			this.givenName = givenName;
		}
		public TokenParam getIdentifier() {
			return identifier;
		}
		public void setIdentifier(TokenParam identifier) {
			this.identifier = identifier;
		}
		public String getTelecom() {
			return telecom;
		}
		public void setTelecom(String telecom) {
			this.telecom = telecom;
		}
		public StringParam getGender() {
			return gender;
		}
		public void setGender(StringParam gender) {
			this.gender = gender;
		}
		public DateParam getBirthDate() {
			return birthDate;
		}
		public void setBirthDate(DateParam birthDate) {
			this.birthDate = birthDate;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getPostalCode() {
			return postalCode;
		}
		public void setPostalCode(String postalCode) {
			this.postalCode = postalCode;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getActive() {
			return active;
		}
		public void setActive(String active) {
			this.active = active;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		//=================================
	    // Public Methods
	    //=================================
	    public void reset() {
	        this.setId(null);
	        this.setFamilyName(null);
	        this.setGivenName(null);
	        this.setIdentifier(null);
	        this.setTelecom(null);
	        this.setGender(null);
	        this.setBirthDate(null);
	        this.setCity(null);
	        this.setPostalCode(null);
	        this.setState(null);
	        this.setActive(null);
	        this.setEmail(null);
	        this.setPhone(null);
	        this.setCountry(null);
	        this.setCommunication(null);
	    }
}
