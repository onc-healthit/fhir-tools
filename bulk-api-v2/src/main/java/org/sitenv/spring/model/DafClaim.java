package org.sitenv.spring.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

@Entity
@Table(name = "claim")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafClaim {

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	
    @Column(name = "identifier")
    @Type(type = "StringJsonObject")										
    private String identifier;
	
    @Column(name = "organization")
    private Integer organization;
     
    @Column(name = "payee_type_code")
    //@Type(type = "StringJsonObject")										
    private String  payee_type_code;
    
    @Column(name = "diagnosis_sequence")										
    private Integer  diagnosis_sequence;
    
    @Column(name = "diagnosis_diagnosis")	
    @Type(type = "StringJsonObject")
    private String  diagnosis_diagnosis;
    
    @Column(name = "coverage_sequence")										
    private Integer  coverage_sequence;
    
    @Column(name = "coverage_focal")										
    private boolean  coverage_focal;
    
    @Column(name = "coverage_reference")										
    private String  coverage_reference;
    
    @Column(name = "coverage_relationship")	
    @Type(type = "StringJsonObject")
    private String  coverage_relationship;
    
    @Column(name = "patient")
    private Integer patient;
    
    
    @Column(name = "encounter")
    private Integer encounter;
    
    @Column(name = "item_sequence")
    private Integer item_sequence;
    
    @Column(name = "item_type")
    @Type(type = "StringJsonObject")										
    private String  item_type;
    
    @Column(name = "item_provider")
    private Integer item_provider;
    
    @Column(name = "item_service")
    @Type(type = "StringJsonObject")										
    private String  item_service;
     
     @Column(name = "item_serviceDate")
     @Temporal(TemporalType.DATE)
     private Date item_serviceDate;
     
     @Column(name = "item_unitPrice")
     @Type(type = "StringJsonObject")										
     private String  item_unitPrice;
     
     @Column(name = "item_net")
     @Type(type = "StringJsonObject")										
     private String  item_net;
     
     public int getId() {
 		return id;
 	}

 	public void setId(int id) {
 		this.id = id;
 	}


 	public String getIdentifier() {
 		return identifier;
 	}

 	public void setIdentifier(String identifier) {
 		this.identifier = identifier;
 	}

 	public Integer getOrganization() {
 		return organization;
 	}

 	public void setOrganization(Integer organization) {
 		this.organization = organization;
 	}

 

 	public String getPayee_type_code() {
		return payee_type_code;
	}

	public void setPayee_type_code(String payee_type_code) {
		this.payee_type_code = payee_type_code;
	}

	public Integer getDiagnosis_sequence() {
 		return diagnosis_sequence;
 	}

 	public void setDiagnosis_sequence(Integer diagnosis_sequence) {
 		this.diagnosis_sequence = diagnosis_sequence;
 	}

 	public String getDiagnosis_diagnosis() {
 		return diagnosis_diagnosis;
 	}

 	public void setDiagnosis_diagnosis(String diagnosis_diagnosis) {
 		this.diagnosis_diagnosis = diagnosis_diagnosis;
 	}

 	public Integer getCoverage_sequence() {
 		return coverage_sequence;
 	}

 	public void setCoverage_sequence(Integer coverage_sequence) {
 		this.coverage_sequence = coverage_sequence;
 	}

 	public boolean isCoverage_focal() {
 		return coverage_focal;
 	}

 	public void setCoverage_focal(boolean coverage_focal) {
 		this.coverage_focal = coverage_focal;
 	}

 

 	public String getCoverage_reference() {
		return coverage_reference;
	}

	public void setCoverage_reference(String coverage_reference) {
		this.coverage_reference = coverage_reference;
	}

	public String getCoverage_relationship() {
 		return coverage_relationship;
 	}

 	public void setCoverage_relationship(String coverage_relationship) {
 		this.coverage_relationship = coverage_relationship;
 	}

 	public Integer getPatient() {
 		return patient;
 	}

 	public void setPatient(Integer patient) {
 		this.patient = patient;
 	}

 	

 	public Integer getEncounter() {
 		return encounter;
 	}

 	public void setEncounter(Integer encounter) {
 		this.encounter = encounter;
 	}

 	public Integer getItem_sequence() {
 		return item_sequence;
 	}

 	public void setItem_sequence(Integer item_sequence) {
 		this.item_sequence = item_sequence;
 	}

 

	public Integer getItem_provider() {
 		return item_provider;
 	}

 	public String getItem_type() {
		return item_type;
	}

	public void setItem_type(String item_type) {
		this.item_type = item_type;
	}

	public void setItem_provider(Integer item_provider) {
 		this.item_provider = item_provider;
 	}

 	public String getItem_service() {
 		return item_service;
 	}

 	public void setItem_service(String item_service) {
 		this.item_service = item_service;
 	}

 	public Date getItem_serviceDate() {
 		return item_serviceDate;
 	}

 	public void setItem_serviceDate(Date item_serviceDate) {
 		this.item_serviceDate = item_serviceDate;
 	}

 	public String getItem_unitPrice() {
 		return item_unitPrice;
 	}

 	public void setItem_unitPrice(String item_unitPrice) {
 		this.item_unitPrice = item_unitPrice;
 	}

 	public String getItem_net() {
 		return item_net;
 	}

 	public void setItem_net(String item_net) {
 		this.item_net = item_net;
 	}
	
    
	
}
