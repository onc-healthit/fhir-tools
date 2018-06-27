package org.sitenv.spring;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Medication.*;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafMedication;
import org.sitenv.spring.service.MedicationService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Scope("request")
public class MedicationResourceProvider implements IResourceProvider {

    public static final String RESOURCE_TYPE = "Medication";
    public static final String VERSION_ID = "3.0";
    AbstractApplicationContext context;
    MedicationService service;

    public MedicationResourceProvider() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (MedicationService) context.getBean("medicationService");
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
    @Override
    public Class<Medication> getResourceType() {
        return Medication.class;
    }

    /**
     * This method returns all the available Medication records.
     * <p/>
     * Ex: http://<server name>/<context>/fhir/Medication?_pretty=true&_format=json
     */
    @Search
    public List<Medication> getAllMedication(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {

        List<DafMedication> dafMedList = service.getAllMedication();

        List<Medication> medList = new ArrayList<Medication>();

        for (DafMedication dafMed : dafMedList) {

            medList.add(createMedicationResourceObject(dafMed));
        }

        return medList;
    }

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or vread operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a single resource instance.
     * </p>
     *
     * @param theId The read operation takes one parameter, which must be of type IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     * Ex: http://<server name>/<context>/fhir/Medication/1?_format=json
     */
    @Read()
    public Medication getMedicationResourceById(@IdParam IdType theId) {

        DafMedication dafMed = service.getMedicationResourceById(theId.getIdPartAsLong().intValue());

        Medication med = createMedicationResourceObject(dafMed);

        return med;
    }

    /**
     * The "@Search" annotation indicates that this method supports the search operation. You may have many different method annotated with this annotation, to support many different search criteria.
     * This example searches by the Code
     *
     * @param code
     * @param theIncludes
     * @param theSort
     * @param theCount
     * @return This method returns a list of Medications. This list may contain multiple matching resources, or it may also be empty.
     * <p>
     * Ex: http://<server name>/<context>/fhir/Medication?code=2823-3&_format=json
     */
    @Search()
    public List<Medication> searchByCode(@RequiredParam(name = Medication.SP_CODE) String code,
                                         @IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort, @Count Integer theCount) {
        //String codeValue = code.getIdPart();
        List<DafMedication> dafMedList = service.getMedicationByCode(code);

        List<Medication> medList = new ArrayList<Medication>();

        for (DafMedication dafMed : dafMedList) {

            medList.add(createMedicationResourceObject(dafMed));
        }

        return medList;
    }

    /**
     * This method converts DafMedication object to Medication object
     */
    private Medication createMedicationResourceObject(DafMedication dafMed) {
        Medication medRes = new Medication();

        //Set Version
        medRes.setId(new IdType(RESOURCE_TYPE, dafMed.getId() + "", VERSION_ID));

        //Set Code
        CodeableConcept medCode = new CodeableConcept();
        Coding medCoding = new Coding();
        medCoding.setCode(dafMed.getMed_code());
        medCoding.setDisplay(dafMed.getMed_display());
        medCode.addCoding(medCoding);
        medRes.setCode(medCode);

        //Set is_Brand
        medRes.setIsBrand(dafMed.isMed_isBrand());

        //Set Product --- deleted
        /*Medication.Product medProd = new Medication.Product();

        CodeableConcept medFormCode = new CodeableConcept();
        Coding medFormCoding = new Coding();
        medFormCoding.setCode(dafMed.getMed_form());
        medFormCode.addCoding(medFormCoding);
        medProd.setForm(medFormCode);
        medRes.setProduct(medProd);*/

     /*   Map<String, String> ingredients = HapiUtils.convertToJsonMap(dafMed.getMed_ingredient());
       // List<Medication.Produc> medProdIngredient = new ArrayList<ProductIngredient>();
       // ProductIngredient medProdIng = new ProductIngredient();
        List<MedicationIngredientComponent> medProdIngredient = new ArrayList<Medication.MedicationIngredientComponent>();
        //Set Item
        Reference resRefDt = new Reference();
        String theId = ingredients.get("item");
        resRefDt.setReference(theId);
        //Set Amount
        Ratio ratioDt = new Ratio();
        Quantity quantityDt = new Quantity();
        //String theQantity = ingredients.get("amount");
        //quantityDt.setUnits(theQantity);
        ratioDt.setNumerator(quantityDt);
        MedicationIngredientComponent medProdIng = new MedicationIngredientComponent();
		medProdIng .setItem(resRefDt);
        medProdIng.setAmount(ratioDt);
        
		medProdIngredient.add(medProdIng);
        medRes.setIngredient(medProdIngredient);*/

        //   medRes.setProduct(medProd);

        //Set Manufacturer
        Reference authorReferenceDt1 = new Reference();
        String theManID = dafMed.getDafManufacturer().getName();
        authorReferenceDt1.setReference(theManID);
        medRes.setManufacturer(authorReferenceDt1);

        //set status
        medRes.setStatus(MedicationStatus.valueOf(dafMed.getStatus()));

        //set isoverthecounter
        medRes.setIsOverTheCounter(dafMed.getIsOverTheCounter());

        //set form
        Map<String, String> formSet = HapiUtils.convertToJsonMap(dafMed.getForm());
        CodeableConcept form = new CodeableConcept();
        Coding formCoding = new Coding();
        formCoding.setSystem(formSet.get("system"));
        formCoding.setCode(formSet.get("code"));
        formCoding.setDisplay(formSet.get("display"));
        form.addCoding(formCoding);
        medRes.setForm(form);

        //set ingrediant - revisit prabhu (1.*)
        //set ingrediant.item
        Map<String, String> iItemSet = HapiUtils.convertToJsonMap(dafMed.getIngredientItem());
        List<MedicationIngredientComponent> theIngredient = new ArrayList<Medication.MedicationIngredientComponent>();
        MedicationIngredientComponent ingredient = new MedicationIngredientComponent();
        CodeableConcept itemCodeableConcept = new CodeableConcept();
        Coding ingredientItem = new Coding();
        ingredientItem.setSystem(iItemSet.get("system"));
        ingredientItem.setCode(iItemSet.get("code"));
        ingredientItem.setDisplay(iItemSet.get("display"));
        itemCodeableConcept.addCoding(ingredientItem);
        ingredient.setItem(itemCodeableConcept);

        //set ingredient.amount.denominator
        Map<String, String> denominatorSet = HapiUtils.convertToJsonMap(dafMed.getIngredientDenominator());
        Ratio amount = new Ratio();
        Quantity denominator = new Quantity();
        denominator.setSystem(denominatorSet.get("system"));
        denominator.setValue(new BigDecimal(denominatorSet.get("value")));
        denominator.setCode(denominatorSet.get("code"));
        amount.setDenominator(denominator);


        //set ingredient.amount.numerator
        Map<String, String> numeratorSet = HapiUtils.convertToJsonMap(dafMed.getIngredientNumerator());
        Quantity numerator = new Quantity();
        numerator.setSystem(numeratorSet.get("system"));
        numerator.setValue(new BigDecimal(numeratorSet.get("value")));
        numerator.setCode(numeratorSet.get("code"));
        amount.setNumerator(numerator);

        ingredient.setAmount(amount);
        theIngredient.add(ingredient);
        medRes.setIngredient(theIngredient);

        //set package
        MedicationPackageComponent medPackage = new MedicationPackageComponent();
        //set package.container
        Map<String, String> containerSet = HapiUtils.convertToJsonMap(dafMed.getPackageContainer());
        CodeableConcept container = new CodeableConcept();
        Coding containerCoding = new Coding();
        containerCoding.setSystem(containerSet.get("system"));
        containerCoding.setCode(containerSet.get("code"));
        containerCoding.setDisplay(containerSet.get("dispaly"));
        container.addCoding(containerCoding);
        medPackage.setContainer(container);
        //set package.content
        List<MedicationPackageContentComponent> theContent = new ArrayList<Medication.MedicationPackageContentComponent>();
        MedicationPackageContentComponent content = new MedicationPackageContentComponent();
        Map<String, String> contentItemSet = HapiUtils.convertToJsonMap(dafMed.getPackageContentItem());
        CodeableConcept contentItem = new CodeableConcept();
        Coding contentItemCoding = new Coding();
        contentItemCoding.setSystem(contentItemSet.get("system"));
        contentItemCoding.setCode(contentItemSet.get("code"));
        contentItemCoding.setDisplay(contentItemSet.get("dispaly"));
        contentItem.addCoding(contentItemCoding);
        content.setItem(contentItem);

        Map<String, String> contentAmountSet = HapiUtils.convertToJsonMap(dafMed.getPackageContentAmount());
        SimpleQuantity contentAmount = new SimpleQuantity();
        contentAmount.setSystem(contentAmountSet.get("system"));
        contentAmount.setValue(new BigDecimal(contentAmountSet.get("value")));
        contentAmount.setCode(contentAmountSet.get("code"));
        content.setAmount(contentAmount);
        theContent.add(content);
        medPackage.setContent(theContent);


        //set package.batch
        Map<String, String> batchSet = HapiUtils.convertToJsonMap(dafMed.getPackageBatch());
        MedicationPackageBatchComponent batch = new MedicationPackageBatchComponent();
        batch.setLotNumber(batchSet.get("lotNumber"));
        DateTimeType expire = new DateTimeType();
        expire.setValueAsString(batchSet.get("expirationDate"));
        batch.setExpirationDateElement(expire);
        medPackage.addBatch(batch);

        medRes.setPackage(medPackage);

        return medRes;
    }
}
