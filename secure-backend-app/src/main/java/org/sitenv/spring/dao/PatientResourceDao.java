package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.PatientResource;

import ca.uhn.fhir.model.dstu2.resource.Patient;

public interface PatientResourceDao {
	
	public PatientResource saveOrUpdate(PatientResource patient);
	
	public List<PatientResource> getAllPatientResources();
	
	public PatientResource getPatientResourceById(Integer id);
	
	public List<PatientResource> findDuplicatesBeforePersist(Patient patient, ExtractionTask et);
	
	public List<PatientResource> getPatientsByActualId(String actualPatientId);
	
	public List<PatientResource> getPatientResourcesByExtractionIdAndInternalPatientId(Integer etId, String patientId);
	
	//public List<PatientResource> getUniqueInternalPatients();
	
	//public List<PatientResource> getUniqueRepositoryPatientsByPatientId(String internalPatientId);
	
	//public List<PatientResource> getPatientsByAge(String fromDate, String toDate, String gender);
	
	//public List<PatientResource> getPatientsByDetails(String firstName, String lastName, String gender, String dob);

}
