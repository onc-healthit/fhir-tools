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
@Table(name = "Questionnaire")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafQuestionnaire {
	
		@Id
		@Column(name = "id")
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Integer id;
		
		@Column(name="questionnaire")
		@Type(type = "StringJsonObject")
		private String questionnaire;
		
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getQuestionnaire() {
			return questionnaire;
		}

		public void setQuestionnaire(String questionnaire) {
			this.questionnaire = questionnaire;
		}

}
