package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "medication_request")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafMedicationRequest {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "identifier")
    @Type(type = "StringJsonObject")
    private String identifier;

    @Column(name = "definition")
    private Integer definition;

    @Column(name = "basedon")
    private Integer basedon;

    @Column(name = "groupidentifier")
    @Type(type = "StringJsonObject")
    private String groupidentifier;

    @Column(name = "status")
    private String status;

    @Column(name = "intent")
    private String intent;

    @Column(name = "category")
    @Type(type = "StringJsonObject")
    private String category;

    @Column(name = "priority")
    private String priority;

    @Column(name = "medication")
    @Type(type = "StringJsonObject")
    private String medication;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject")
    private DafPatient subject;

    @Column(name = "context")
    private Integer context;

    @Column(name = "supportinginfo")
    private Integer supportinginfo;

    @Column(name = "authoredon")
    @Temporal(TemporalType.DATE)
    private Date authoredon;

    @Column(name = "requester")
    @Type(type = "StringJsonObject")
    private String requester;

    @Column(name = "recorder")
    private Integer recorder;

    @Column(name = "reasoncode")
    @Type(type = "StringJsonObject")
    private String reasoncode;

    @Column(name = "reasonreference")
    private Integer reasonreference;

    @Column(name = "notes")
    private String notes;

    @Column(name = "dosageinstruction")
    @Type(type = "StringJsonObject")
    private String dosageinstruction;

    @Column(name = "dispenserequest_period")
    @Type(type = "StringJsonObject")
    private String dispenserequest_period;

    @Column(name = "dispenserequest_quantity")
    @Type(type = "StringJsonObject")
    private String dispenserequest_quantity;

    @Column(name = "dispenserequest_supply")
    @Type(type = "StringJsonObject")
    private String dispenserequest_supply;

    @Column(name = "substitution")
    @Type(type = "StringJsonObject")
    private String substitution;

    @Column(name = "priorpresciption")
    private Integer priorpresciption;

    @Column(name = "detectedissue")
    private Integer detectedissue;

    @Column(name = "eventhistory")
    private Integer eventhistory;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medication_reference")
    private DafMedication medication_reference;

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

    public Integer getDefinition() {
        return definition;
    }

    public void setDefinition(Integer definition) {
        this.definition = definition;
    }

    public Integer getBasedon() {
        return basedon;
    }

    public void setBasedon(Integer basedon) {
        this.basedon = basedon;
    }

    public String getGroupidentifier() {
        return groupidentifier;
    }

    public void setGroupidentifier(String groupidentifier) {
        this.groupidentifier = groupidentifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public DafPatient getSubject() {
        return subject;
    }

    public void setSubject(DafPatient subject) {
        this.subject = subject;
    }

    public Integer getContext() {
        return context;
    }

    public void setContext(Integer context) {
        this.context = context;
    }

    public Integer getSupportinginfo() {
        return supportinginfo;
    }

    public void setSupportinginfo(Integer supportinginfo) {
        this.supportinginfo = supportinginfo;
    }

    public Date getAuthoredon() {
        return authoredon;
    }

    public void setAuthoredon(Date authoredon) {
        this.authoredon = authoredon;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public Integer getRecorder() {
        return recorder;
    }

    public void setRecorder(Integer recorder) {
        this.recorder = recorder;
    }

    public String getReasoncode() {
        return reasoncode;
    }

    public void setReasoncode(String reasoncode) {
        this.reasoncode = reasoncode;
    }

    public Integer getReasonreference() {
        return reasonreference;
    }

    public void setReasonreference(Integer reasonreference) {
        this.reasonreference = reasonreference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDosageinstruction() {
        return dosageinstruction;
    }

    public void setDosageinstruction(String dosageinstruction) {
        this.dosageinstruction = dosageinstruction;
    }

    public String getDispenserequest_period() {
        return dispenserequest_period;
    }

    public void setDispenserequest_period(String dispenserequest_period) {
        this.dispenserequest_period = dispenserequest_period;
    }

    public String getDispenserequest_quantity() {
        return dispenserequest_quantity;
    }

    public void setDispenserequest_quantity(String dispenserequest_quantity) {
        this.dispenserequest_quantity = dispenserequest_quantity;
    }

    public String getDispenserequest_supply() {
        return dispenserequest_supply;
    }

    public void setDispenserequest_supply(String dispenserequest_supply) {
        this.dispenserequest_supply = dispenserequest_supply;
    }

    public String getSubstitution() {
        return substitution;
    }

    public void setSubstitution(String substitution) {
        this.substitution = substitution;
    }

    public Integer getPriorpresciption() {
        return priorpresciption;
    }

    public void setPriorpresciption(Integer priorpresciption) {
        this.priorpresciption = priorpresciption;
    }

    public Integer getDetectedissue() {
        return detectedissue;
    }

    public void setDetectedissue(Integer detectedissue) {
        this.detectedissue = detectedissue;
    }

    public Integer getEventhistory() {
        return eventhistory;
    }

    public void setEventhistory(Integer eventhistory) {
        this.eventhistory = eventhistory;
    }

    public DafMedication getMedication_reference() {
        return medication_reference;
    }

    public void setMedication_reference(DafMedication medication_reference) {
        this.medication_reference = medication_reference;
    }

}
