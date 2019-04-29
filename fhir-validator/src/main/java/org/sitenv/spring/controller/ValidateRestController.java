package org.sitenv.spring.controller;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.LenientErrorHandler;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SchemaBaseValidator;
import ca.uhn.fhir.validation.ValidationResult;
import ca.uhn.fhir.validation.schematron.SchematronBaseValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ValidateRestController {


    @PostMapping(value = "/dstu2/{encoding}/validate")
    public ResponseEntity validateDSTU2(@PathVariable("encoding") String encoding, @RequestBody String bodyStr) {

        String results;
        IBaseResource resource = null;

        FhirContext ctx = FhirContext.forDstu2();
        FhirValidator validator = ctx.newValidator();
        validator.registerValidatorModule(new org.hl7.fhir.instance.hapi.validation.FhirInstanceValidator());

        validator.registerValidatorModule(new SchemaBaseValidator(ctx));
        validator.registerValidatorModule(new SchematronBaseValidator(ctx));

        validator.setValidateAgainstStandardSchema(true);
        validator.setValidateAgainstStandardSchematron(true);

        try{
            if("xml".equalsIgnoreCase(encoding)) {
                // Parse that XML string encoding
                resource = ctx.newXmlParser().setParserErrorHandler(new LenientErrorHandler().setErrorOnInvalidValue(true)).parseResource(bodyStr);
            }else{
                // Parse that JSON string encoding)
                resource = ctx.newJsonParser().setParserErrorHandler(new LenientErrorHandler().setErrorOnInvalidValue(true)).parseResource(bodyStr);
            }

            // Apply the validation. This will throw an exception on the first validation failure
            ValidationResult result = validator.validateWithResult(resource);
            //For JSON output
            results = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(result.toOperationOutcome());

            // For XML output
            //results = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(result.toOperationOutcome()); // XML output

        } catch (DataFormatException e) {
            ca.uhn.fhir.model.dstu2.resource.OperationOutcome outcome = new ca.uhn.fhir.model.dstu2.resource.OperationOutcome();
            outcome.addIssue().setSeverity(ca.uhn.fhir.model.dstu2.valueset.IssueSeverityEnum.ERROR).setDiagnostics("Failed to parse request body as JSON resource. Error was: "+e.getMessage());
            results = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(outcome);
        }

        return new ResponseEntity(results, HttpStatus.OK);
    }


    @PostMapping(value = "/stu3/{encoding}/validate")
    public ResponseEntity validateSTU3(@PathVariable("encoding") String encoding, @RequestBody String bodyStr) {

        String results;
        IBaseResource resource = null;

        FhirContext ctx = FhirContext.forDstu3();

        FhirValidator validator = ctx.newValidator();
        validator.registerValidatorModule(new org.hl7.fhir.dstu3.hapi.validation.FhirInstanceValidator());

        try{
            if("xml".equalsIgnoreCase(encoding)) {
                // Parse that XML string encoding
                resource = ctx.newXmlParser().setParserErrorHandler(new LenientErrorHandler().setErrorOnInvalidValue(false)).parseResource(bodyStr);
            }else{
                // Parse that JSON string encoding)
                resource = ctx.newJsonParser().setParserErrorHandler(new LenientErrorHandler().setErrorOnInvalidValue(false)).parseResource(bodyStr);
            }

            // Apply the validation. This will throw an exception on the first validation failure
            ValidationResult result = validator.validateWithResult(resource);
            //For JSON output
            results = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(result.toOperationOutcome());

            // For XML output
            //results = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(result.toOperationOutcome()); // XML output

        } catch (DataFormatException e) {
            org.hl7.fhir.dstu3.model.OperationOutcome outcome = new org.hl7.fhir.dstu3.model.OperationOutcome();
            outcome.addIssue().setSeverity(org.hl7.fhir.dstu3.model.OperationOutcome.IssueSeverity.ERROR).setDiagnostics("Failed to parse request body as JSON resource. Error was: "+e.getMessage());
            results = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(outcome);
        }

        return new ResponseEntity(results, HttpStatus.OK);
    }



}