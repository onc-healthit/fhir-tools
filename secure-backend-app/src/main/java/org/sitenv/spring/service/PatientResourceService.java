package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.PatientResource;

import ca.uhn.fhir.model.dstu2.resource.Patient;

public interface PatientResourceService {
	
	public PatientResource saveOrUpdate(PatientResource patient);
	
	public List<PatientResource> getAllPatientResources();
	
	public PatientResource getPatientResourceById(Integer id);
	
	public List<PatientResource> findDuplicatesBeforePersist(Patient patient, ExtractionTask et);
	
	public List<PatientResource> getPatientsByActualId(String actualPatientId);
	
	public List<PatientResource> getPatientResourcesByExtractionIdAndInternalPatientId(Integer etId, String patientId);
	
}
