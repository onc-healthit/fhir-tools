package org.sitenv.spring.service;


import java.io.File;
import java.util.Date;
import java.util.StringJoiner;
import java.util.concurrent.Future;

import org.sitenv.spring.AllergyIntoleranceResourceProvider;
import org.sitenv.spring.BulkDataRequestProvider;
import org.sitenv.spring.CarePlanResourceProvider;
import org.sitenv.spring.CareTeamResourceProvider;
import org.sitenv.spring.ConditionResourceProvider;
import org.sitenv.spring.DeviceResourceProvider;
import org.sitenv.spring.DiagnosticReportResourceProvider;
import org.sitenv.spring.DocumentReferenceResourceProvider;
import org.sitenv.spring.EncounterResourceProvider;
import org.sitenv.spring.FamilyMemberHistoryResourceProvider;
import org.sitenv.spring.GoalResourceProvider;
import org.sitenv.spring.HealthcareServiceResourceProvider;
import org.sitenv.spring.ImagingStudyResourceProvider;
import org.sitenv.spring.ImmunizationResourceProvider;
import org.sitenv.spring.LocationResourceProvider;
import org.sitenv.spring.MedicationAdministrationResourceProvider;
import org.sitenv.spring.MedicationDispenseResourceProvider;
import org.sitenv.spring.MedicationRequestResourceProvider;
import org.sitenv.spring.MedicationResourceProvider;
import org.sitenv.spring.MedicationStatementResourceProvider;
import org.sitenv.spring.ObservationResourceProvider;
import org.sitenv.spring.OrganizationResourceProvider;
import org.sitenv.spring.PatientResourceProvider;
import org.sitenv.spring.PractitionerResourceProvider;
import org.sitenv.spring.PractitionerRoleResourceProvider;
import org.sitenv.spring.ProcedureResourceProvider;
import org.sitenv.spring.ProvenanceResourceProvider;
import org.sitenv.spring.RiskAssessmentResourceProvider;
import org.sitenv.spring.SpecimenResourceProvider;
import org.sitenv.spring.model.DafBulkDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;


@Service
public class AsyncService {
	Logger log = (Logger) LoggerFactory.getLogger(AsyncService.class);
	BulkDataRequestProvider bdd = new BulkDataRequestProvider();

	@Async("asyncExecutor")
	public Future<Long> processPatientData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		PatientResourceProvider patientResourceProvider = new PatientResourceProvider();
		try {
			patientResourceProvider.getPatientForBulkDataRequest(patientList, start, ctx, destDir);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());
	}

	@Async("asyncExecutor")
	public Future<Long> processAllergyIntoleranceData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		AllergyIntoleranceResourceProvider allergyIntoleranceProvider = new AllergyIntoleranceResourceProvider();
		try {
			allergyIntoleranceProvider.getAllergyIntoleranceForBulkDataRequest(patientList, start, ctx, destDir);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}
	
	@Async("asyncExecutor")
	public Future<Long> processFamilyMemberHistoryData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		
		FamilyMemberHistoryResourceProvider familyMemberHistoryResourceProvider = new FamilyMemberHistoryResourceProvider();
		try {
			familyMemberHistoryResourceProvider.getFamilyMemberHistoryForBulkDataRequest(patientList, start, ctx,
					destDir);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());
	}

	@Async("asyncExecutor")
	public Future<Long> processCarePlanData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, StringJoiner patientList,
			Date start) {
		CarePlanResourceProvider carePlanProvider = new CarePlanResourceProvider();
		try {
			carePlanProvider.getCarePlanForBulkDataRequest(patientList, start, ctx, destDir);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}
	
	@Async("asyncExecutor")
	public Future<Long> processCareTeamData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		try {
			CareTeamResourceProvider careTeamResourceProvider = new CareTeamResourceProvider();
		careTeamResourceProvider.getCareTeamForBulkDataRequest(destDir,  ctx,
				 patientList,start);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());
	}
	
	
	
	

	@Async("asyncExecutor")
	public Future<Long> processConditionData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, StringJoiner patientList,
			Date start) {
		ConditionResourceProvider conditionProvider = new ConditionResourceProvider();
		try {
			conditionProvider.getConditionForBulkDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}
	
	@Async("asyncExecutor")
	public Future<Long> processPractitionerData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		PractitionerResourceProvider practitionerResourceProvider = new PractitionerResourceProvider();
		try {
			practitionerResourceProvider.getPractitionerForBulkDataRequest(patientList, start, ctx, destDir);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());
	}
	

	@Async("asyncExecutor")
	public Future<Long> processDeviceData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, StringJoiner patientList,
			Date start) {
		DeviceResourceProvider deviceProvider = new DeviceResourceProvider();
		try {
			deviceProvider.getDeviceForBulkDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processDiagnosticReportData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		DiagnosticReportResourceProvider diagnosticReportProvider = new DiagnosticReportResourceProvider();
		try {
			diagnosticReportProvider.getDiagnosticReportForBulkDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processDocumentReferenceData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		DocumentReferenceResourceProvider documentReferenceResourceProvider = new DocumentReferenceResourceProvider();
		try {
			documentReferenceResourceProvider.getDocumentReferenceForBulkDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}
	
	
	@Async("asyncExecutor")
	public Future<Long> processEncounterData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		EncounterResourceProvider encounterResourceProvider = new EncounterResourceProvider();
		try {
			encounterResourceProvider.getEncounterForBulkDataRequest(patientList, start, ctx, destDir);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());
	}

	@Async("asyncExecutor")
	public Future<Long> processGoalsData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		GoalResourceProvider goalsResourceProvider = new GoalResourceProvider();
		try {
			goalsResourceProvider.getGoalsForBulkDataRequest(patientList, start,destDir, ctx);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processImmunizationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		ImmunizationResourceProvider immunizationResourceProvider = new ImmunizationResourceProvider();
		try {
			immunizationResourceProvider.getImmunizationForBulkDataRequest(patientList, start,destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processLocationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		LocationResourceProvider locationResourceProvider = new LocationResourceProvider();
		try {
			locationResourceProvider.getLocationForBulkDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationAdministrationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		MedicationAdministrationResourceProvider medicationAdministrationResourceProvider = new MedicationAdministrationResourceProvider();
		try {
			medicationAdministrationResourceProvider.getMedicationAdministrationForBulkDataRequest(patientList, start,
					destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}
	
	@Async("asyncExecutor")
	public Future<Long> processMedicationRequestData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		MedicationRequestResourceProvider medicationRequestResourceProvider = new MedicationRequestResourceProvider();
		try {
			medicationRequestResourceProvider.getMedicationRequestForBulkDataRequest(patientList, start,destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());
	}


	@Async("asyncExecutor")
	public Future<Long> processMedicationDispenseData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		MedicationDispenseResourceProvider medicationDispenseResourceProvider = new MedicationDispenseResourceProvider();
		try {
			medicationDispenseResourceProvider.getMedicationDispenseForBulkDataRequest(patientList, start, destDir,
					ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processPractitionerRoleData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		String fileName = "PractitionerRole.ndjson";

		PractitionerRoleResourceProvider practitionerRoleResourceProvider = new PractitionerRoleResourceProvider();
		try {
			practitionerRoleResourceProvider.getPractitionerRoleForBulkDataRequest(patientList, start, destDir,
					ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		String fileName = "Medication.ndjson";
		MedicationResourceProvider medicationResourceProvider = new MedicationResourceProvider();
		try {
			medicationResourceProvider.getMedicationForBulkDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationStatementData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		MedicationStatementResourceProvider medicationStatementResourceProvider = new MedicationStatementResourceProvider();
		try {
			medicationStatementResourceProvider.getMedicationStatementForBulkDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processObservationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		ObservationResourceProvider observationResourceProvider = new ObservationResourceProvider();
		try {
			observationResourceProvider.getObservationForBulkDataRequest(patientList, start, destDir, ctx);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processOrganizationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		OrganizationResourceProvider organizationResourceProvider = new OrganizationResourceProvider();
		try {
			organizationResourceProvider.getOrganizationForBulkDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processProcedureData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		ProcedureResourceProvider procedureResourceProvider = new ProcedureResourceProvider();
		try {
			procedureResourceProvider.getProcedureForBulkDataRequest(patientList, start,destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processSpecimenRequestData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		SpecimenResourceProvider specimenResourceProvider = new SpecimenResourceProvider();
		try {
			specimenResourceProvider.getSpecimenRequestDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processProvenanceData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		ProvenanceResourceProvider provenanceResourceProvider = new ProvenanceResourceProvider();
		try {
			provenanceResourceProvider.getProvenanceRequestDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}
	
	@Async("asyncExecutor")
	public Future<Long> processImagingStudyData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		ImagingStudyResourceProvider imagingStudyResourceProvider = new ImagingStudyResourceProvider();
		try {
			imagingStudyResourceProvider.getImagingStudyRequestDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	public Future<Long> processRiskAssessmentData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		RiskAssessmentResourceProvider riskAssessmentResourceProvider = new RiskAssessmentResourceProvider();
		try {
			riskAssessmentResourceProvider.getRiskAssessmentRequestDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	public Future<Long> processHealthcareServiceData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			StringJoiner patientList, Date start) {
		HealthcareServiceResourceProvider healthcareServiceResourceProvider = new HealthcareServiceResourceProvider();
		try {
			healthcareServiceResourceProvider.getHealthcareServiceRequestDataRequest(patientList, start, destDir, ctx);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}
}
