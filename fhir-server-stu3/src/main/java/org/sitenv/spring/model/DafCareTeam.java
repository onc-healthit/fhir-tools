package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "careteam")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafCareTeam {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "identifier")
    @Type(type = "StringJsonObject")
    private String identifier;

    @Column(name = "status")
    private String status;

    @Column(name = "category")
    @Type(type = "StringJsonObject")
    private String category;

    @Column(name = "name")
    private String name;

    @Column(name = "context")
    private String context;

    @Column(name = "period")
    @Temporal(TemporalType.DATE)
    private Date period;

    @Column(name = "participant_role")
    @Type(type = "StringJsonObject")
    private String participant_role;

    @Column(name = "participant_member")
    private String participant_member;

    @Column(name = "participant_onbehalfof")
    private String participant_onbehalfof;

    @Column(name = "participant_period")
    @Temporal(TemporalType.DATE)
    private Date participant_period;

    @Column(name = "reason_code")
    @Type(type = "StringJsonObject")
    private String reason_code;

    @Column(name = "reson_reference")
    private String reson_reference;

    @Column(name = "managing_organization")
    private String managing_organization;

    @Column(name = "telecom")
    @Type(type = "StringJsonObject")
    private String telecom;

    @Column(name = "note")
    private String note;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject")
    private DafPatient subject;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public String getParticipant_role() {
        return participant_role;
    }

    public void setParticipant_role(String participant_role) {
        this.participant_role = participant_role;
    }

    public String getParticipant_member() {
        return participant_member;
    }

    public void setParticipant_member(String participant_member) {
        this.participant_member = participant_member;
    }

    public String getParticipant_onbehalfof() {
        return participant_onbehalfof;
    }

    public void setParticipant_onbehalfof(String participant_onbehalfof) {
        this.participant_onbehalfof = participant_onbehalfof;
    }

    public Date getParticipant_period() {
        return participant_period;
    }

    public void setParticipant_period(Date participant_period) {
        this.participant_period = participant_period;
    }

    public String getReason_code() {
        return reason_code;
    }

    public void setReason_code(String reason_code) {
        this.reason_code = reason_code;
    }

    public String getReson_reference() {
        return reson_reference;
    }

    public void setReson_reference(String reson_reference) {
        this.reson_reference = reson_reference;
    }

    public String getManaging_organization() {
        return managing_organization;
    }

    public void setManaging_organization(String managing_organization) {
        this.managing_organization = managing_organization;
    }

    public String getTelecom() {
        return telecom;
    }

    public void setTelecom(String telecom) {
        this.telecom = telecom;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public DafPatient getSubject() {
        return subject;
    }

    public void setSubject(DafPatient subject) {
        this.subject = subject;
    }

}
