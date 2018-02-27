package org.sitenv.spring.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonManagedReference;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

@Entity
@Table(name = "encounter")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafEncounter  {
	
	    @Id
	    @Column(name = "id")
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;

	    @Column(name = "identifier")
	    @Type(type = "StringJsonObject")										
	    private String identifier;


	    @Column(name = "status")
	    private String status;
	    
	    @Column(name = "class")
	    private String classes;
	    
	    @Column(name = "type")
	    @Type(type = "StringJsonObject")
	    private String type;
	    
	    @Column(name = "length")
	    @Type(type = "StringJsonObject")
	    private String length;
	    
	    @Column(name = "priority")
	    @Type(type = "StringJsonObject")
	    private String priority;
	    
	  
	    @Column(name = "participant")
	    private Integer participant;
	    
	 
	    @Column(name = "patient")
	    private Integer patient;
	   
	   
	    @Column(name = "reason")
	    @Type(type = "StringJsonObject")
	    private String reason;
	   
	  
	    @Column(name = "location")
	    private Integer location;

	    @Column(name = "sh_period")
	    @Type(type = "StringJsonObject")
	    private String shPeriod;
	    
	    @Column(name = "h_preAdmissionIdentifier")
	    @Type(type = "StringJsonObject")
	    private String hospPreAdmissionIdentifier;
	    
	    @Column(name = "h_admitSource")
	    @Type(type = "StringJsonObject")
	    private String hospAdmitSource;
	    
	    @Column(name = "h_reAdmission")
	    @Type(type = "StringJsonObject")
	    private String hospReAdmission;
	    
	    @Column(name = "h_dietPreference")
	    @Type(type = "StringJsonObject")
	    private String hospDietPreference;
	    
	    @Column(name = "h_dischargeDisposition")
	    @Type(type = "StringJsonObject")
	    private String hospDischargeDisposition;
	   
	    
	    @Column(name = "claim")
	    private Integer claim;
	  

		@Column(name = "serviceProvider")
	    private Integer serviceProvider;


		public int getId() {
			return id;
		}


		public void setId(int id) {
			this.id = id;
		}
		
		public Integer getClaim() {
				return claim;
			}


	   public void setClaim(Integer claim) {
				this.claim = claim;
			}

		public String getIdentifier() {
			return identifier;
		}


		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}


		public String getStatus() {
			return status;
		}


		public void setStatus(String status) {
			this.status = status;
		}


		public String getClasses() {
			return classes;
		}


		public void setClasses(String classes) {
			this.classes = classes;
		}


		public String getType() {
			return type;
		}


		public void setType(String type) {
			this.type = type;
		}


		public String getLength() {
			return length;
		}


		public void setLength(String length) {
			this.length = length;
		}


		public String getPriority() {
			return priority;
		}


		public void setPriority(String priority) {
			this.priority = priority;
		}


		public Integer getParticipant() {
			return participant;
		}


		public void setParticipant(Integer participant) {
			this.participant = participant;
		}


		public Integer getPatient() {
			return patient;
		}


		public void setPatient(Integer patient) {
			this.patient = patient;
		}


		public String getReason() {
			return reason;
		}


		public void setReason(String reason) {
			this.reason = reason;
		}


		public Integer getLocation() {
			return location;
		}


		public void setLocation(Integer location) {
			this.location = location;
		}


		public String getShPeriod() {
			return shPeriod;
		}


		public void setShPeriod(String shPeriod) {
			this.shPeriod = shPeriod;
		}


		public String getHospPreAdmissionIdentifier() {
			return hospPreAdmissionIdentifier;
		}


		public void setHospPreAdmissionIdentifier(String hospPreAdmissionIdentifier) {
			this.hospPreAdmissionIdentifier = hospPreAdmissionIdentifier;
		}


		public String getHospAdmitSource() {
			return hospAdmitSource;
		}


		public void setHospAdmitSource(String hospAdmitSource) {
			this.hospAdmitSource = hospAdmitSource;
		}


		public String getHospReAdmission() {
			return hospReAdmission;
		}


		public void setHospReAdmission(String hospReAdmission) {
			this.hospReAdmission = hospReAdmission;
		}


		public String getHospDietPreference() {
			return hospDietPreference;
		}


		public void setHospDietPreference(String hospDietPreference) {
			this.hospDietPreference = hospDietPreference;
		}


		public String getHospDischargeDisposition() {
			return hospDischargeDisposition;
		}


		public void setHospDischargeDisposition(String hospDischargeDisposition) {
			this.hospDischargeDisposition = hospDischargeDisposition;
		}


		public Integer getServiceProvider() {
			return serviceProvider;
		}


		public void setServiceProvider(Integer serviceProvider) {
			this.serviceProvider = serviceProvider;
		}
       

}
