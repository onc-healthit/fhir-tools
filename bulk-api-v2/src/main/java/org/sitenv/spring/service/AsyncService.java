package org.sitenv.spring.service;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.sitenv.spring.AllergyIntoleranceResourceProvider;
import org.sitenv.spring.BulkDataRequestProvider;
import org.sitenv.spring.CarePlanResourceProvider;
import org.sitenv.spring.ConditionResourceProvider;
import org.sitenv.spring.DeviceResourceProvider;
import org.sitenv.spring.DiagnosticReportResourceProvider;
import org.sitenv.spring.DocumentReferenceResourceProvider;
import org.sitenv.spring.GoalsResourceProvider;
import org.sitenv.spring.ImmunizationResourceProvider;
import org.sitenv.spring.LocationResourceProvider;
import org.sitenv.spring.MedicationAdministrationResourceProvider;
import org.sitenv.spring.MedicationDispenseResourceProvider;
import org.sitenv.spring.MedicationOrderResourceProvider;
import org.sitenv.spring.MedicationResourceProvider;
import org.sitenv.spring.MedicationStatementResourceProvider;
import org.sitenv.spring.ObservationResourceProvider;
import org.sitenv.spring.OrganizationResourceProvider;
import org.sitenv.spring.PatientJsonResourceProvider;
import org.sitenv.spring.ProcedureResourceProvider;
import org.sitenv.spring.model.DafBulkDataRequest;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.resource.CarePlan;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.Device;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.Goal;
import ca.uhn.fhir.model.dstu2.resource.Immunization;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Organization;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ch.qos.logback.classic.Logger;

@Service
public class AsyncService {
	Logger log = (Logger) LoggerFactory.getLogger(AsyncService.class);
	BulkDataRequestProvider bdd = new BulkDataRequestProvider();

	@Async("asyncExecutor")
    public Future<Long> processPatientData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<Integer> patientList,
			Date start) {
		String fileName = "Patient.ndjson";
		PatientJsonResourceProvider patientResourceProvider = new PatientJsonResourceProvider();

		try {
			List<Patient> patients = patientResourceProvider.getPatientForBulkDataRequest(patientList, start);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < patients.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(patients.get(i)));
				if (i < patients.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());


	}

	@Async("asyncExecutor")
	public Future<Long> processAllergyIntoleranceData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<Integer> patientList, Date start) {
		String fileName = "AllergyIntolerance.ndjson";

		AllergyIntoleranceResourceProvider allergyIntoleranceProvider = new AllergyIntoleranceResourceProvider();

		try {
			List<AllergyIntolerance> allergyIntoleranceList = allergyIntoleranceProvider
					.getAllergyIntoleranceForBulkDataRequest(patientList, start);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < allergyIntoleranceList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(allergyIntoleranceList.get(i)));

				if (i < allergyIntoleranceList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processCarePlanData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<Integer> patientList,
			Date start) {
		String fileName = "CarePlan.ndjson";

		CarePlanResourceProvider carePlanProvider = new CarePlanResourceProvider();

		try {
			List<CarePlan> carePlanList = carePlanProvider.getCarePlanForBulkData(patientList, start);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < carePlanList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(carePlanList.get(i)));

				if (i < carePlanList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processConditionData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<Integer> patientList,
			Date start) {
		String fileName = "Condition.ndjson";

		ConditionResourceProvider conditionProvider = new ConditionResourceProvider();

		try {
			List<Condition> conditionList = conditionProvider.getConditionForBulkDataRequest(patientList, start);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < conditionList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(conditionList.get(i)));

				if (i < conditionList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processDeviceData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<Integer> patientList,
			Date start) {
		String fileName = "Device.ndjson";

		DeviceResourceProvider deviceProvider = new DeviceResourceProvider();

		try {
			List<Device> deviceList = deviceProvider.getDeviceForBulkDataRequest(patientList, start);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < deviceList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(deviceList.get(i)));

				if (i < deviceList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processDiagnosticReportData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<Integer> patientList, Date start) {
		String fileName = "DiagnosticReport.ndjson";

		DiagnosticReportResourceProvider diagnosticReportProvider = new DiagnosticReportResourceProvider();

		try {
			List<DiagnosticReport> diagnosticReportList = diagnosticReportProvider
					.getDiagnosticReportForBulkDataRequest(patientList, start);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < diagnosticReportList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(diagnosticReportList.get(i)));

				if (i < diagnosticReportList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processDocumentReferenceData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<Integer> patientList, Date start) {
		String fileName = "DocumentReference.ndjson";

		DocumentReferenceResourceProvider documentReferenceResourceProvider = new DocumentReferenceResourceProvider();

		try {
			List<DocumentReference> documentReferenceList = documentReferenceResourceProvider
					.getDocumentReferenceForBulkDataRequest(patientList, start);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < documentReferenceList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(documentReferenceList.get(i)));

				if (i < documentReferenceList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processGoalsData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<Integer> patientList,
			Date start) {
		String fileName = "Goal.ndjson";

		GoalsResourceProvider goalsResourceProvider = new GoalsResourceProvider();

		try {
			List<Goal> goalList = goalsResourceProvider.getGoalsForBulkDataRequest(patientList, start);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < goalList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(goalList.get(i)));

				if (i < goalList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processImmunizationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<Integer> patientList, Date start) {
		String fileName = "Immunization.ndjson";

		ImmunizationResourceProvider immunizationResourceProvider = new ImmunizationResourceProvider();

		try {
			List<Immunization> immunizationList = immunizationResourceProvider
					.getImmunizationForBulkDataRequest(patientList, start);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < immunizationList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(immunizationList.get(i)));

				if (i < immunizationList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long>  processLocationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<Integer> patientList,
			Date start) {
		String fileName = "Location.ndjson";

		LocationResourceProvider locationResourceProvider = new LocationResourceProvider();

		try {
			List<Location> locationList = locationResourceProvider.getLocationForBulkDataRequest(patientList, start);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < locationList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(locationList.get(i)));

				if (i < locationList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationAdministrationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<Integer> patientList, Date start) {
		String fileName = "MedicationAdministration.ndjson";

		MedicationAdministrationResourceProvider medicationAdministrationResourceProvider = new MedicationAdministrationResourceProvider();

		try {
			List<MedicationAdministration> medicationAdministrationList = medicationAdministrationResourceProvider
					.getMedicationAdministrationForBulkDataRequest(patientList, start);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < medicationAdministrationList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(medicationAdministrationList.get(i)));
				if (i < medicationAdministrationList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationDispenseData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<Integer> patientList, Date start) {
		String fileName = "MedicationDispense.ndjson";

		MedicationDispenseResourceProvider medicationDispenseResourceProvider = new MedicationDispenseResourceProvider();

		try {
			List<MedicationDispense> medicationDispenseList = medicationDispenseResourceProvider
					.getMedicationDispenseForBulkDataRequest(patientList, start);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < medicationDispenseList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(medicationDispenseList.get(i)));

				if (i < medicationDispenseList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationOrderData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<Integer> patientList, Date start) {
		String fileName = "MedicationOrder.ndjson";

		MedicationOrderResourceProvider medicationOrderResourceProvider = new MedicationOrderResourceProvider();

		try {
			List<MedicationOrder> medicationOrderList = medicationOrderResourceProvider
					.getMedicationOrderForBulkDataRequest(patientList, start);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < medicationOrderList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(medicationOrderList.get(i)));

				if (i < medicationOrderList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<Integer> patientList,
			Date start) {
		String fileName = "Medication.ndjson";

		MedicationResourceProvider medicationResourceProvider = new MedicationResourceProvider();

		try {
			List<Medication> medicationList = medicationResourceProvider.getMedicationForBulkDataRequest(patientList,
					start);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < medicationList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(medicationList.get(i)));

				if (i < medicationList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationStatementData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<Integer> patientList, Date start) {
		String fileName = "MedicationStatement.ndjson";

		MedicationStatementResourceProvider medicationStatementResourceProvider = new MedicationStatementResourceProvider();

		try {
			List<MedicationStatement> medicationStatementList = medicationStatementResourceProvider
					.getMedicationStatementForBulkDataRequest(patientList, start);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < medicationStatementList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(medicationStatementList.get(i)));

				if (i < medicationStatementList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processObservationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<Integer> patientList,
			Date start) {
		String fileName = "Observation.ndjson";

		ObservationResourceProvider observationResourceProvider = new ObservationResourceProvider();

		try {
			List<Observation> observationList = observationResourceProvider
					.getObservationForBulkDataRequest(patientList, start);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < observationList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(observationList.get(i)));
				if (i < observationList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processOrganizationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<Integer> patientList, Date start) {
		String fileName = "Organization.ndjson";

		OrganizationResourceProvider organizationResourceProvider = new OrganizationResourceProvider();

		try {
			List<Organization> organizationList = organizationResourceProvider
					.getOrganizationForBulkDataRequest(patientList, start);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < organizationList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(organizationList.get(i)));

				if (i < organizationList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processProcedureData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<Integer> patientList,
			Date start) {
		String fileName = "Procedure.ndjson";

		ProcedureResourceProvider procedureResourceProvider = new ProcedureResourceProvider();

		try {
			List<Procedure> procedureList = procedureResourceProvider.getProcedureForBulkDataRequest(patientList,
					start);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < procedureList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(procedureList.get(i)));

				if (i < procedureList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		bdd.seta();
		return new AsyncResult<>(System.nanoTime());

	}
}
