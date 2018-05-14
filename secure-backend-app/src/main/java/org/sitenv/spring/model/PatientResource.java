package org.sitenv.spring.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

@Entity
@Table(name="patient")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class PatientResource {
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="internal_patient_id")
	private String internalPatientId;
	
	@Column(name="et_id")
	private Integer extractionTaskId;
	
	@Column(name="actual_patient_id")
	private String actualPatientId;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="data")
	@Type(type = "StringJsonObject")
	private String data;
	
	@Transient
	private Integer encountersNumber;
	
	@Transient
	private Double utilization;
	
	@Transient
	private Integer repeatedTests;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getInternalPatientId() {
		return internalPatientId;
	}

	public void setInternalPatientId(String internalPatientId) {
		this.internalPatientId = internalPatientId;
	}

	public Integer getExtractionTaskId() {
		return extractionTaskId;
	}

	public void setExtractionTaskId(Integer extractionTaskId) {
		this.extractionTaskId = extractionTaskId;
	}

	public String getActualPatientId() {
		return actualPatientId;
	}

	public void setActualPatientId(String actualPatientId) {
		this.actualPatientId = actualPatientId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Integer getEncountersNumber() {
		return encountersNumber;
	}

	public void setEncountersNumber(Integer encountersNumber) {
		this.encountersNumber = encountersNumber;
	}

	public Double getUtilization() {
		return utilization;
	}

	public void setUtilization(Double utilization) {
		this.utilization = utilization;
	}

	public Integer getRepeatedTests() {
		return repeatedTests;
	}

	public void setRepeatedTests(Integer repeatedTests) {
		this.repeatedTests = repeatedTests;
	}
	
}
