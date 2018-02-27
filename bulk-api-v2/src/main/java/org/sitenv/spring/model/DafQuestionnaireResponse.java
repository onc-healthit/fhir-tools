package org.sitenv.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

@Entity
@Table(name = "QuestionnaireResponse")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafQuestionnaireResponse {
	
	@Id
	@Column(name = "qr_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@OrderBy(clause = "asc")
	private Integer questionnaireResponseId;
	
	/*@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="questionnaire_id")
	private DafQuestionnaire questionnaire;*/
	
	@Column(name="questionnaire_id")
	private Integer questionnaire_id;
	
	@Column(name="response_id")
	private String response_id;
	
	@Column(name="questionnaire_response")
	@Type(type = "StringJsonObject")
	private String questionnaire_response;

	public Integer getQuestionnaireResponseId() {
		return questionnaireResponseId;
	}

	public void setQuestionnaireResponseId(Integer questionnaireResponseId) {
		this.questionnaireResponseId = questionnaireResponseId;
	}

	public Integer getQuestionnaire_id() {
		return questionnaire_id;
	}

	public void setQuestionnaire_id(Integer questionnaire_id) {
		this.questionnaire_id = questionnaire_id;
	}

	public String getResponse_id() {
		return response_id;
	}

	public void setResponse_id(String response_id) {
		this.response_id = response_id;
	}

	public String getQuestionnaire_response() {
		return questionnaire_response;
	}

	public void setQuestionnaire_response(String questionnaire_response) {
		this.questionnaire_response = questionnaire_response;
	}
	
}
