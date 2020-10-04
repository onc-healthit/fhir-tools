package org.sitenv.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.sitenv.spring.dao.PatientResourceDao;
import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.PatientResource;

import ca.uhn.fhir.model.dstu2.resource.Patient;

@Service
@Transactional
public class PatientResourceServiceImpl implements PatientResourceService {
	
	@Autowired
	private PatientResourceDao patientDao;

	public PatientResource saveOrUpdate(PatientResource patient) {
		
		return patientDao.saveOrUpdate(patient);
	}

	public List<PatientResource> getAllPatientResources() {
		
		return patientDao.getAllPatientResources();
	}

	public PatientResource getPatientResourceById(Integer id) {
		
		return patientDao.getPatientResourceById(id);
	}

	public List<PatientResource> findDuplicatesBeforePersist(Patient patient, ExtractionTask et) {
		
		return patientDao.findDuplicatesBeforePersist(patient,et);
	}

	public List<PatientResource> getPatientsByActualId(String actualPatientId) {
		
		return patientDao.getPatientsByActualId(actualPatientId);
	}

	public List<PatientResource> getPatientResourcesByExtractionIdAndInternalPatientId(Integer etId, String patientId) {
		
		return patientDao.getPatientResourcesByExtractionIdAndInternalPatientId(etId, patientId);
	}

}
