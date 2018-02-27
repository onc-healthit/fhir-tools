package org.sitenv.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.Sequence;

import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafClaim;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.model.DafEncounter;
import org.sitenv.spring.service.ClaimService;
import org.sitenv.spring.service.ConditionService;
import org.sitenv.spring.util.HapiUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.expression.spel.ast.TypeCode;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.MoneyDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt.Repeat;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.resource.Claim;
import ca.uhn.fhir.model.dstu2.resource.Claim.Coverage;
import ca.uhn.fhir.model.dstu2.resource.Claim.Diagnosis;
import ca.uhn.fhir.model.dstu2.resource.Claim.Item;
import ca.uhn.fhir.model.dstu2.resource.Claim.Payee;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance.Reaction;
import ca.uhn.fhir.model.dstu2.resource.Encounter.Hospitalization;
import ca.uhn.fhir.model.dstu2.valueset.ClaimTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.ClinicalImpressionStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.ConditionCategoryCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.PayeeTypeCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.UnitsOfTimeEnum;
import ca.uhn.fhir.model.primitive.BoundCodeDt;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.PositiveIntDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.server.IResourceProvider;
import javassist.bytecode.analysis.Type;

@Component
@Scope("request")
public class ClaimResourceProvider implements IResourceProvider {

	public static final String RESOURCE_TYPE = "Claim";
	public static final String VERSION_ID = "2.0";
	AbstractApplicationContext context;
	ClaimService service;

	public ClaimResourceProvider() {
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		service = (ClaimService) context.getBean("claimResourceService");
	}

	@Override
	public Class<Claim> getResourceType() {
		return Claim.class;
	}

	@Read()
	public Claim getClaimResourceById(@IdParam IdDt theId) {

		DafClaim dafClaim = service.getClaimResourceById(theId.getIdPartAsLong().intValue());

		Claim claim = createClaimObject(dafClaim);

		return claim;
	}

	@Search
	public List<Claim> getAllClaims(@IncludeParam(allow = "*") Set<Include> theIncludes, @Sort SortSpec theSort,
			@Count Integer theCount) {
		List<Claim> claimList = new ArrayList<Claim>();
		try {
			List<DafClaim> dafClaimsList = service.getAllclaims();

			for (DafClaim dafClaims : dafClaimsList) {

				claimList.add(createClaimObject(dafClaims));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

		}

		return claimList;
	}

	private Claim createClaimObject(DafClaim dafClaim) {
		Claim claim = new Claim();
		try {
			// Set Version
			claim.setId(new IdDt(RESOURCE_TYPE, dafClaim.getId() + "", VERSION_ID));

			// Set identifier
			Map<String, String> enIdentifier = HapiUtils.convertToJsonMap(dafClaim.getIdentifier());
			List<IdentifierDt> identifier = new ArrayList<IdentifierDt>();
			IdentifierDt identifierDt = new IdentifierDt();
			identifierDt.setSystem(enIdentifier.get("system"));
			identifierDt.setValue(enIdentifier.get("value"));
			identifier.add(identifierDt);
			claim.setIdentifier(identifier);

			// set Organization
			ResourceReferenceDt serviceProvider = new ResourceReferenceDt();
			String theId1 = "Organization/" + Integer.toString(dafClaim.getOrganization());
			serviceProvider.setReference(theId1);
			// serviceProvider.setDisplay("display");
			claim.setOrganization(serviceProvider);

			// payee_type_code
			Payee payee = new Payee();
			CodingDt type = new CodingDt();
			type.setCode(dafClaim.getPayee_type_code());
			payee.setType(type);
			claim.setPayee(payee);

			// set diagnosis
			Map<String, String> dig = HapiUtils.convertToJsonMap(dafClaim.getDiagnosis_diagnosis());
			List<Diagnosis> diagnosis = new ArrayList<Claim.Diagnosis>();

			Diagnosis digg = new Diagnosis();
			digg.setSequence(dafClaim.getDiagnosis_sequence());

			List<CodeableConceptDt> dietList = new ArrayList<CodeableConceptDt>();
			CodeableConceptDt dietapp = new CodeableConceptDt();
			CodingDt diagcode = new CodingDt();
			diagcode.setCode(dig.get("code"));
			dietapp.addCoding(diagcode);
			dietList.add(dietapp);
			digg.setDiagnosis(diagcode);
			diagnosis.add(digg);
			claim.setDiagnosis(diagnosis);

			// set patient
			ResourceReferenceDt patientResource = new ResourceReferenceDt();
			String theId = "Patient/" + Integer.toString(dafClaim.getPatient());
			patientResource.setReference(theId);
			claim.setPatient(patientResource);

			// set coverage
			Map<String, String> coverage = HapiUtils.convertToJsonMap(dafClaim.getCoverage_relationship());

			List<Coverage> cvglist = new ArrayList<Coverage>();
			Coverage cvg = new Coverage();
			cvg.setSequence(dafClaim.getCoverage_sequence());
			cvg.setFocal(dafClaim.isCoverage_focal());

			ResourceReferenceDt id = new ResourceReferenceDt();
			String theId2 = "Coverage/" + String.valueOf(dafClaim.getCoverage_reference());
			id.setReference(theId2);
			cvg.setCoverage(id);

			CodingDt cvgcode = new CodingDt();
			cvgcode.setCode(coverage.get("code"));
			cvgcode.setDisplay(coverage.get("display"));
			cvgcode.setSystem(coverage.get("system"));
			cvg.setRelationship(cvgcode);
			cvglist.add(cvg);
			claim.setCoverage(cvglist);

			// set item
			Map<String, String> itemseq = HapiUtils.convertToJsonMap(dafClaim.getItem_service());

			List<Item> itemlist = new ArrayList<Item>();
			Item items = new Item();
			items.setSequence(dafClaim.getItem_sequence());

			// item_service
			CodingDt itemservice = new CodingDt();
			itemservice.setCode(itemseq.get("code"));
			itemservice.setSystem(itemseq.get("system"));
			items.setService(itemservice);

			// item_serviceDate
			DateDt date = new DateDt();
			date.setValue(dafClaim.getItem_serviceDate());
			items.setServiceDate(date);

			// unitPrice
			Map<String, String> unit = HapiUtils.convertToJsonMap(dafClaim.getItem_unitPrice());
			MoneyDt simpleQuantity = new MoneyDt();
			simpleQuantity.setCode(unit.get("code"));
			simpleQuantity.setValue(Double.parseDouble(unit.get("value")));
			simpleQuantity.setUnit(unit.get("unit"));
			simpleQuantity.setSystem(unit.get("system"));
			items.setUnitPrice(simpleQuantity);

			// net
			Map<String, String> netp = HapiUtils.convertToJsonMap(dafClaim.getItem_net());
			MoneyDt net = new MoneyDt();
			net.setSystem(netp.get("system"));
			net.setCode(netp.get("code"));
			net.setUnit(netp.get("unit"));
			net.setValue(Double.parseDouble(netp.get("value")));
			items.setNet(net);

			// item_provider
			ResourceReferenceDt practitionerResource = new ResourceReferenceDt();
			String thepractitionerId = "practitioner/" + Integer.toString(dafClaim.getItem_provider());
			practitionerResource.setReference(thepractitionerId);
			items.setProvider(practitionerResource);

			// item_type
			Map<String, String> typep = HapiUtils.convertToJsonMap(dafClaim.getItem_type());
			CodingDt typecode = new CodingDt();
			typecode.setCode(typep.get("code"));
			typecode.setSystem(typep.get("system"));
			typecode.setDisplay(typep.get("display"));
			items.setType(typecode);

			itemlist.add(items);
			claim.setItem(itemlist);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		}
		return claim;
	}

}
