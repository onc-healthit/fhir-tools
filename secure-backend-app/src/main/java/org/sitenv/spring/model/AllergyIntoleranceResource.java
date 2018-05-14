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
@Table(name="allergy_intolerance")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class AllergyIntoleranceResource {
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="internal_ai_id")
	private String internalAllergyId;
	
	@Column(name="et_id")
	private Integer extractionTaskId;
	
	@Column(name="internal_patient_id")
	private String internalPatientId;
	
	@Column(name="actual_ai_id")
	private String actualAllergyId;
	
	@Column(name="data")
	@Type(type = "StringJsonObject")
	private String data;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getInternalAllergyId() {
		return internalAllergyId;
	}

	public void setInternalAllergyId(String internalAllergyId) {
		this.internalAllergyId = internalAllergyId;
	}

	public Integer getExtractionTaskId() {
		return extractionTaskId;
	}

	public void setExtractionTaskId(Integer extractionTaskId) {
		this.extractionTaskId = extractionTaskId;
	}

	public String getInternalPatientId() {
		return internalPatientId;
	}

	public void setInternalPatientId(String internalPatientId) {
		this.internalPatientId = internalPatientId;
	}

	public String getActualAllergyId() {
		return actualAllergyId;
	}

	public void setActualAllergyId(String actualAllergyId) {
		this.actualAllergyId = actualAllergyId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
