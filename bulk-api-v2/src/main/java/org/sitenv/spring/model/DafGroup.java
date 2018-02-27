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
@Table(name = "groups")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafGroup {
	
		@Id
	    @Column(name = "group_id")
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;
		
		@Column(name="identifier")
		@Type(type = "StringJsonObject")
		private String identifier;
		
		@Column(name="type")
		private String type;
		
		@Column(name="actual")
		private Boolean actual;
		
		@Column(name="code")
		@Type(type="StringJsonObject")
		private String code;
		
		@Column(name="name")
		private String name;
		
		@Column(name="quantity")
		private Integer quantity;
	
		@Column(name="char_code")
		@Type(type="StringJsonObject")
		private String characteristicCode;
		
		@Column(name="char_value")
		@Type(type="StringJsonObject")
		private String characteristicValue;
	
		@Column(name="char_exclude")
		private Boolean characteristicExclude;
	
		@Column(name="char_period")
		@Type(type="StringJsonObject")
		private String characteristicPeriod;
		
		@Column(name="member")
		@Type(type="StringJsonObject")
		private String member;
		
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getIdentifier() {
			return identifier;
		}

		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Boolean getActual() {
			return actual;
		}

		public void setActual(Boolean actual) {
			this.actual = actual;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public String getCharacteristicCode() {
			return characteristicCode;
		}

		public void setCharacteristicCode(String characteristicCode) {
			this.characteristicCode = characteristicCode;
		}

		public String getCharacteristicValue() {
			return characteristicValue;
		}

		public void setCharacteristicValue(String characteristicValue) {
			this.characteristicValue = characteristicValue;
		}

		public Boolean getCharacteristicExclude() {
			return characteristicExclude;
		}

		public void setCharacteristicExclude(Boolean characteristicExclude) {
			this.characteristicExclude = characteristicExclude;
		}

		public String getCharacteristicPeriod() {
			return characteristicPeriod;
		}

		public void setCharacteristicPeriod(String characteristicPeriod) {
			this.characteristicPeriod = characteristicPeriod;
		}

		public String getMember() {
			return member;
		}

		public void setMember(String member) {
			this.member = member;
		}

}
