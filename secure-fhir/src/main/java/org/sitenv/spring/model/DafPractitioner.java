package org.sitenv.spring.model;

import org.hibernate.annotations.Type;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "practitioner")
public class DafPractitioner {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "text")
    @Type(type = "StringJsonObject")
    private String text;

    @Column(name = "identifier")
    @Type(type = "StringJsonObject")
    private String identifier;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "telecom")
    @Type(type = "StringJsonObject")
    private String telecom;

    @Column(name = "practitionerrole")
    private String practitionerrole;
    
    @Column(name="last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getTelecom() {
        return telecom;
    }

    public void setTelecom(String telecom) {
        this.telecom = telecom;
    }

    public String getPractitionerrole() {
        return practitionerrole;
    }

    public void setPractitionerrole(String practitionerrole) {
        this.practitionerrole = practitionerrole;
    }

}
