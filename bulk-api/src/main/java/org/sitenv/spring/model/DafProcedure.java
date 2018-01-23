package org.sitenv.spring.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

@Entity
@Table(name = "procedure")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafProcedure {
	
	@Id
    @Column(name = "procedure_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int procedureId;
	
	@Column(name = "identifier")
    @Type(type = "StringJsonObject")
    private String identifier;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject")
    private DafPatient subject;
	
	@Column(name="status")
	private String status;
	
	@Column(name = "category")
    @Type(type = "StringJsonObject")
    private String category;
	
	@Column(name = "code")
    @Type(type = "StringJsonObject")
    private String code;
	
	@Column(name="notperformed")
	private Boolean notperformed;
	
	@Column(name = "reasonnotperformed")
    @Type(type = "StringJsonObject")
    private String reasonnotperformed;
	
	@Column(name = "bodysite")
    @Type(type = "StringJsonObject")
    private String bodysite;
	
	@Column(name = "reason")
    @Type(type = "StringJsonObject")
    private String reason;
	
	@Column(name = "performer_actor")
    @Type(type = "StringJsonObject")
    private String performerActor;
	
	@Column(name = "performer_role")
    @Type(type = "StringJsonObject")
    private String performerRole;
	
	@Column(name = "performed")
    @Temporal(TemporalType.DATE)
    private Date performed;
	
	@Column(name="encounter")
	private Integer encounter;
	
	@Column(name="location")
	private Integer location;
	
	@Column(name = "outcome")
    @Type(type = "StringJsonObject")
    private String outcome;
	
	@Column(name="report")
	private Integer report;
	
	@Column(name = "complication")
    @Type(type = "StringJsonObject")
    private String complication;
	
	@Column(name = "followup")
    @Type(type = "StringJsonObject")
    private String followup;
	
	@Column(name="request")
	private Integer request;
	
	@Column(name="notes")
	private String notes;
	
	@Column(name = "focaldevice")
    @Type(type = "StringJsonObject")
    private String focalDevice;
	
	@Column(name="used")
	private Integer used;
	
	@Column(name="last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	public DafProcedure(){
		
	}

	public int getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(int procedureId) {
		this.procedureId = procedureId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public DafPatient getSubject() {
		return subject;
	}

	public void setSubject(DafPatient subject) {
		this.subject = subject;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Boolean getNotperformed() {
		return notperformed;
	}

	public void setNotperformed(Boolean notperformed) {
		this.notperformed = notperformed;
	}

	public String getReasonnotperformed() {
		return reasonnotperformed;
	}

	public void setReasonnotperformed(String reasonnotperformed) {
		this.reasonnotperformed = reasonnotperformed;
	}

	public String getBodysite() {
		return bodysite;
	}

	public void setBodysite(String bodysite) {
		this.bodysite = bodysite;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getPerformerActor() {
		return performerActor;
	}

	public void setPerformerActor(String performerActor) {
		this.performerActor = performerActor;
	}

	public String getPerformerRole() {
		return performerRole;
	}

	public void setPerformerRole(String performerRole) {
		this.performerRole = performerRole;
	}

	public Date getPerformed() {
		return performed;
	}

	public void setPerformed(Date performed) {
		this.performed = performed;
	}

	public Integer getEncounter() {
		return encounter;
	}

	public void setEncounter(Integer encounter) {
		this.encounter = encounter;
	}

	public Integer getLocation() {
		return location;
	}

	public void setLocation(Integer location) {
		this.location = location;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public Integer getReport() {
		return report;
	}

	public void setReport(Integer report) {
		this.report = report;
	}

	public String getComplication() {
		return complication;
	}

	public void setComplication(String complication) {
		this.complication = complication;
	}

	public String getFollowup() {
		return followup;
	}

	public void setFollowup(String followup) {
		this.followup = followup;
	}

	public Integer getRequest() {
		return request;
	}

	public void setRequest(Integer request) {
		this.request = request;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getFocalDevice() {
		return focalDevice;
	}

	public void setFocalDevice(String focalDevice) {
		this.focalDevice = focalDevice;
	}

	public Integer getUsed() {
		return used;
	}

	public void setUsed(Integer used) {
		this.used = used;
	}
	
}
