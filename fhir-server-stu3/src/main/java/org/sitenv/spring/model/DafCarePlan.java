package org.sitenv.spring.model;


import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;

@Entity
@Table(name = "careplan")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafCarePlan {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient")
    private DafPatient patient;

    @Column(name = "status")
    private String status;

    @Column(name = "cat_code")
    private String cat_code;

    @Column(name = "cat_system")
    private String cat_system;

    @Column(name = "definition")
    private Integer definition;

    @Column(name = "basedon")
    private Integer basedon;

    @Column(name = "replaces")
    private Integer replaces;

    @Column(name = "partof")
    private Integer partof;

    @Column(name = "intent")
    private String intent;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private Integer author;

    @Column(name = "careteam")
    private Integer careTeam;

    @Column(name = "supporting_info")
    private Integer supportingInfo;

    @Column(name = "activity")
    @Type(type = "StringJsonObject")
    private String activity;

    @Column(name = "note")
    private String note;

    public DafCarePlan() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DafPatient getPatient() {
        return patient;
    }

    public void setPatient(DafPatient patient) {
        this.patient = patient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCat_code() {
        return cat_code;
    }

    public void setCat_code(String cat_code) {
        this.cat_code = cat_code;
    }

    public String getCat_system() {
        return cat_system;
    }

    public void setCat_system(String cat_system) {
        this.cat_system = cat_system;
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

    public Integer getReplaces() {
        return replaces;
    }

    public void setReplaces(Integer replaces) {
        this.replaces = replaces;
    }

    public Integer getPartof() {
        return partof;
    }

    public void setPartof(Integer partof) {
        this.partof = partof;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public Integer getCareTeam() {
        return careTeam;
    }

    public void setCareTeam(Integer careTeam) {
        this.careTeam = careTeam;
    }

    public Integer getSupportingInfo() {
        return supportingInfo;
    }

    public void setSupportingInfo(Integer supportingInfo) {
        this.supportingInfo = supportingInfo;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
