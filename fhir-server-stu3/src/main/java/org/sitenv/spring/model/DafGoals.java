package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "goals")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafGoals {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "status")
    private String status;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject")
    private DafPatient subject;

    @Column(name = "description")
    @Type(type = "StringJsonObject")
    private String description;

    @Column(name = "status_reason")
    private String statusReason;

    @Column(name = "measure")
    @Type(type = "StringJsonObject")
    private String measure;

    @Column(name = "range_high")
    @Type(type = "StringJsonObject")
    private String rangeHigh;

    @Column(name = "range_low")
    @Type(type = "StringJsonObject")
    private String rangeLow;

    @Column(name = "expressed_by")
    private Integer expressedBy;

    public DafGoals() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DafPatient getSubject() {
        return subject;
    }

    public void setSubject(DafPatient subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getRangeHigh() {
        return rangeHigh;
    }

    public void setRangeHigh(String rangeHigh) {
        this.rangeHigh = rangeHigh;
    }

    public String getRangeLow() {
        return rangeLow;
    }

    public void setRangeLow(String rangeLow) {
        this.rangeLow = rangeLow;
    }

    public Integer getExpressedBy() {
        return expressedBy;
    }

    public void setExpressedBy(Integer expressedBy) {
        this.expressedBy = expressedBy;
    }
}
