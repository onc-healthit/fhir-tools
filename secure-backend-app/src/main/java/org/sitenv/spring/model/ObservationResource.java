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
@Table(name="observation")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class ObservationResource {
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="et_id")
	private Integer extractionTaskId;
	
	@Column(name="internal_observation_id")
	private String internalObservationId;
	
	@Column(name="internal_patient_id")
	private String internalPatientId;
	
	@Column(name="actual_observation_id")
	private String actualObservationId;
	
	@Column(name="category")
	private String category;
	
	@Column(name="code")
	private String code;
	
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

	public String getInternalObservationId() {
		return internalObservationId;
	}

	public void setInternalObservationId(String internalObservationId) {
		this.internalObservationId = internalObservationId;
	}

	public String getInternalPatientId() {
		return internalPatientId;
	}

	public void setInternalPatientId(String internalPatientId) {
		this.internalPatientId = internalPatientId;
	}

	public String getActualObservationId() {
		return actualObservationId;
	}

	public void setActualObservationId(String actualObservationId) {
		this.actualObservationId = actualObservationId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
}
