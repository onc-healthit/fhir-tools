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
@Table(name="location")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafLocation {
	
	@Id
    @Column(name = "location_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer locationId;
	
	@Column(name = "identifier")
    @Type(type = "StringJsonObject")
    private String identifier;
	
	@Column(name="status")
	private String status;
	
	@Column(name="name")
	private String name;
	
	@Column(name="description")
	private String description;
	
	@Column(name="mode")
	private String mode;
	
	@Column(name="type")
	@Type(type = "StringJsonObject")
	private String type;
	
	@Column(name="telecom")
	@Type(type = "StringJsonObject")
	private String telecom;
	
	@Column(name="address")
	@Type(type = "StringJsonObject")
	private String address;
	
	@Column(name="physicaltype")
	@Type(type = "StringJsonObject")
	private String physicaltype;
	
	@Column(name="position")
	@Type(type = "StringJsonObject")
	private String position;
	
	@Column(name="managingOrganization")
	private String managingOrganization;
	
	@Column(name="partof")
	private String partof;
	
	@Column(name="last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	public DafLocation(){
		
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTelecom() {
		return telecom;
	}

	public void setTelecom(String telecom) {
		this.telecom = telecom;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhysicaltype() {
		return physicaltype;
	}

	public void setPhysicaltype(String physicaltype) {
		this.physicaltype = physicaltype;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getManagingOrganization() {
		return managingOrganization;
	}

	public void setManagingOrganization(String managingOrganization) {
		this.managingOrganization = managingOrganization;
	}

	public String getPartof() {
		return partof;
	}

	public void setPartof(String partof) {
		this.partof = partof;
	}
}
