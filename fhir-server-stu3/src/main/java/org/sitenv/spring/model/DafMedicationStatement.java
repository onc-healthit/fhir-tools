package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "medication_statement")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafMedicationStatement {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "identifier_system")
    private String identifier_system;

    @Column(name = "identifier_value")
    private String identifier_value;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject")
    private DafPatient subject;

    @Column(name = "reasonnottaken")
    @Type(type = "StringJsonObject")
    private String reasonnottaken;

    @Column(name = "effectivePeriod")
    @Temporal(TemporalType.DATE)
    private Date effectivePeriod;

    @Column(name = "medicationcodeableconcept")
    @Type(type = "StringJsonObject")
    private String medicationcodeableconcept;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medicationreference")
    private DafMedication medicationreference;

    @Column(name = "dosage")
    @Type(type = "StringJsonObject")
    private String dosage;

    @Column(name = "status")
    private String status;

    @Column(name = "basedon")
    private Integer basedon;

    @Column(name = "partof")
    private Integer partof;

    @Column(name = "category")
    @Type(type = "StringJsonObject")
    private String category;

    @Column(name = "information_source")
    private Integer informationSource;

    @Column(name = "context")
    private Integer context;

    @Column(name = "derivedfrom")
    private Integer derivedFrom;

    @Column(name = "taken")
    private String taken;

    @Column(name = "reason_code")
    @Type(type = "StringJsonObject")
    private String reasonCode;

    @Column(name = "reason_reference")
    private Integer reasonReference;

    public DafMedicationStatement() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifier_system() {
        return identifier_system;
    }

    public void setIdentifier_system(String identifier_system) {
        this.identifier_system = identifier_system;
    }

    public String getIdentifier_value() {
        return identifier_value;
    }

    public void setIdentifier_value(String identifier_value) {
        this.identifier_value = identifier_value;
    }

    public DafPatient getSubject() {
        return subject;
    }

    public void setSubject(DafPatient subject) {
        this.subject = subject;
    }

    public String getReasonnottaken() {
        return reasonnottaken;
    }

    public void setReasonnottaken(String reasonnottaken) {
        this.reasonnottaken = reasonnottaken;
    }

    public Date getEffectivePeriod() {
        return effectivePeriod;
    }

    public void setEffectivePeriod(Date effectivePeriod) {
        this.effectivePeriod = effectivePeriod;
    }

    public String getMedicationcodeableconcept() {
        return medicationcodeableconcept;
    }

    public void setMedicationcodeableconcept(String medicationcodeableconcept) {
        this.medicationcodeableconcept = medicationcodeableconcept;
    }

    public DafMedication getMedicationreference() {
        return medicationreference;
    }

    public void setMedicationreference(DafMedication medicationreference) {
        this.medicationreference = medicationreference;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getBasedon() {
        return basedon;
    }

    public void setBasedon(Integer basedon) {
        this.basedon = basedon;
    }

    public Integer getPartof() {
        return partof;
    }

    public void setPartof(Integer partof) {
        this.partof = partof;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getInformationSource() {
        return informationSource;
    }

    public void setInformationSource(Integer informationSource) {
        this.informationSource = informationSource;
    }

    public Integer getContext() {
        return context;
    }

    public void setContext(Integer context) {
        this.context = context;
    }

    public Integer getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(Integer derivedFrom) {
        this.derivedFrom = derivedFrom;
    }

    public String getTaken() {
        return taken;
    }

    public void setTaken(String taken) {
        this.taken = taken;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Integer getReasonReference() {
        return reasonReference;
    }

    public void setReasonReference(Integer reasonReference) {
        this.reasonReference = reasonReference;
    }
}
