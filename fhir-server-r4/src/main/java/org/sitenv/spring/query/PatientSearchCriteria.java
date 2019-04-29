package org.sitenv.spring.query;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

public class PatientSearchCriteria extends SearchCriteria {

    private StringParam fullName;
    private Integer id;
    private String searchText;
    private String familyName;
    private String givenName;
    private TokenParam identifier;
    private String language;
    private String telecom;
    private StringParam gender;
    private DateParam birthDate;
    private String city;
    private String postalCode;
    private String state;
    private String mothersmaidenname;
    private String active;
    private String versionId;
    private String deceased;
    private String country;
    private String organization;
    private String email;
    private String phone;

	public PatientSearchCriteria() {
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public String getMothersmaidenname() {
        return mothersmaidenname;
    }

    public void setMothersmaidenname(String mothersmaidenname) {
        this.mothersmaidenname = mothersmaidenname;
    }
    
    public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}


    public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDeceased() {
		return deceased;
	}

	public void setDeceased(String deceased) {
		this.deceased = deceased;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

    //=================================
    // Public Methods
    //=================================
    public void reset() {
        this.setId(null);
        this.setSearchText(null);
        this.setFamilyName(null);
        this.setGivenName(null);
        this.setIdentifier(null);
        this.setLanguage(null);
        this.setTelecom(null);
        this.setGender(null);
        this.setBirthDate(null);
        this.setCity(null);
        this.setPostalCode(null);
        this.setState(null);
        this.setMothersmaidenname(null);
        this.setActive(null);
        this.setEmail(null);
        this.setPhone(null);
        this.setOrganization(null);
        this.setCountry(null);
    }
}
