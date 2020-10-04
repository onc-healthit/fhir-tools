package org.sitenv.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

@Entity
@Table(name="medication_statement")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class MedicationStatementResource {
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="et_id")
	private Integer extractionTaskId;
	
	@Column(name="internal_ms_id")
	private String internalMedStatementId;
	
	@Column(name="internal_patient_id")
	private String internalPatientId;
	
	@Column(name="actual_ms_id")
	private String actualMedStatementId;
	
	@Column(name="data")
	@Type(type = "StringJsonObject")
	private String data;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getExtractionTaskId() {
		return extractionTaskId;
	}

	public void setExtractionTaskId(Integer extractionTaskId) {
		this.extractionTaskId = extractionTaskId;
	}

	public String getInternalMedStatementId() {
		return internalMedStatementId;
	}

	public void setInternalMedStatementId(String internalMedStatementId) {
		this.internalMedStatementId = internalMedStatementId;
	}

	public String getInternalPatientId() {
		return internalPatientId;
	}

	public void setInternalPatientId(String internalPatientId) {
		this.internalPatientId = internalPatientId;
	}

	public String getActualMedStatementId() {
		return actualMedStatementId;
	}

	public void setActualMedStatementId(String actualMedStatementId) {
		this.actualMedStatementId = actualMedStatementId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
}
