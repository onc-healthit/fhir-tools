package org.sitenv.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="data_source")
public class DataSource {
	
	@Id
	@Column(name="ds_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer dsId;
	
	@Column(name="fhir_url")
	private String fhirURL;
	
	@Column(name="iss")
	private String iss;
	
	@Column(name="sub")
	private String sub;
	
	@Column(name="exp")
	private String exp;
	
	@Column(name="aud")
	private String aud;
	
	@Column(name="scope")
	private String scope;
	
	@Column(name="is_secure")
	private Boolean isSecure;
	
	@Column(name="key_location")
	private String keyLocation;

	public Integer getDsId() {
		return dsId;
	}

	public void setDsId(Integer dsId) {
		this.dsId = dsId;
	}

	public String getFhirURL() {
		return fhirURL;
	}

	public void setFhirURL(String fhirURL) {
		this.fhirURL = fhirURL;
	}

	public String getIss() {
		return iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Boolean getIsSecure() {
		return isSecure;
	}

	public void setIsSecure(Boolean isSecure) {
		this.isSecure = isSecure;
	}

	public String getKeyLocation() {
		return keyLocation;
	}

	public void setKeyLocation(String keyLocation) {
		this.keyLocation = keyLocation;
	}
	
}
