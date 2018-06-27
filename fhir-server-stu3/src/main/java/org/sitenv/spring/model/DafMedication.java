package org.sitenv.spring.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.sitenv.spring.configuration.JSONObjectUserType;

import javax.persistence.*;

@Entity
@Table(name = "medication")
@TypeDefs({@TypeDef(name = "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafMedication {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "med_code")
    private String med_code;

    @Column(name = "med_isbrand")
    @Type(type = "true_false")
    private boolean med_isBrand;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "med_manufacturer")
    private DafManufacturer dafManufacturer;

    @Column(name = "med_display")
    private String med_display;

    @Column(name = "med_system")
    private String med_system;

    @Column(name = "status")
    private String status;

    @Column(name = "isoverthecounter")
    private Boolean isOverTheCounter;

    @Column(name = "form")
    @Type(type = "StringJsonObject")
    private String form;

    @Column(name = "ingredient_item")
    @Type(type = "StringJsonObject")
    private String ingredientItem;

    @Column(name = "ingredient_denominator")
    @Type(type = "StringJsonObject")
    private String ingredientDenominator;

    @Column(name = "ingredient_numerator")
    @Type(type = "StringJsonObject")
    private String ingredientNumerator;

    @Column(name = "package_container")
    @Type(type = "StringJsonObject")
    private String packageContainer;

    @Column(name = "package_batch")
    @Type(type = "StringJsonObject")
    private String packageBatch;

    @Column(name = "package_content_item")
    @Type(type = "StringJsonObject")
    private String packageContentItem;

    @Column(name = "package_content_amount")
    @Type(type = "StringJsonObject")
    private String packageContentAmount;

    public DafMedication() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMed_code() {
        return med_code;
    }

    public void setMed_code(String med_code) {
        this.med_code = med_code;
    }

    public boolean isMed_isBrand() {
        return med_isBrand;
    }

    public void setMed_isBrand(boolean med_isBrand) {
        this.med_isBrand = med_isBrand;
    }

    public DafManufacturer getDafManufacturer() {
        return dafManufacturer;
    }

    public void setDafManufacturer(DafManufacturer dafManufacturer) {
        this.dafManufacturer = dafManufacturer;
    }

    public String getMed_display() {
        return med_display;
    }

    public void setMed_display(String med_display) {
        this.med_display = med_display;
    }

    public String getMed_system() {
        return med_system;
    }

    public void setMed_system(String med_system) {
        this.med_system = med_system;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsOverTheCounter() {
        return isOverTheCounter;
    }

    public void setIsOverTheCounter(Boolean isOverTheCounter) {
        this.isOverTheCounter = isOverTheCounter;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getIngredientItem() {
        return ingredientItem;
    }

    public void setIngredientItem(String ingredientItem) {
        this.ingredientItem = ingredientItem;
    }

    public String getIngredientDenominator() {
        return ingredientDenominator;
    }

    public void setIngredientDenominator(String ingredientDenominator) {
        this.ingredientDenominator = ingredientDenominator;
    }

    public String getIngredientNumerator() {
        return ingredientNumerator;
    }

    public void setIngredientNumerator(String ingredientNumerator) {
        this.ingredientNumerator = ingredientNumerator;
    }

    public String getPackageContainer() {
        return packageContainer;
    }

    public void setPackageContainer(String packageContainer) {
        this.packageContainer = packageContainer;
    }

    public String getPackageBatch() {
        return packageBatch;
    }

    public void setPackageBatch(String packageBatch) {
        this.packageBatch = packageBatch;
    }

    public String getPackageContentItem() {
        return packageContentItem;
    }

    public void setPackageContentItem(String packageContentItem) {
        this.packageContentItem = packageContentItem;
    }

    public String getPackageContentAmount() {
        return packageContentAmount;
    }

    public void setPackageContentAmount(String packageContentAmount) {
        this.packageContentAmount = packageContentAmount;
    }
}
