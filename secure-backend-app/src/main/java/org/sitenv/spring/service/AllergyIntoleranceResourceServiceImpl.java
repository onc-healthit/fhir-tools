package org.sitenv.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.sitenv.spring.dao.AllergyIntoleranceResourceDao;
import org.sitenv.spring.model.AllergyIntoleranceResource;
import org.sitenv.spring.model.ExtractionTask;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;

@Service
@Transactional
public class AllergyIntoleranceResourceServiceImpl implements AllergyIntoleranceResourceService {

	@Autowired
	private AllergyIntoleranceResourceDao aiDao;
	
	public AllergyIntoleranceResource saveOrUpdate(AllergyIntoleranceResource ai) {
		
		return aiDao.saveOrUpdate(ai);
	}

	public List<AllergyIntoleranceResource> getAllAllergyIntolerances() {
		
		return aiDao.getAllAllergyIntolerances();
	}

	public AllergyIntoleranceResource getAllergyIntoleranceById(Integer aiId) {
		
		return aiDao.getAllergyIntoleranceById(aiId);
	}

	public List<AllergyIntoleranceResource> findDuplicatesBeforePersist(AllergyIntolerance allergy, ExtractionTask et) {
		
		return aiDao.findDuplicatesBeforePersist(allergy, et);
	}

	public List<AllergyIntoleranceResource> getAllergyResourceByExtractionIdAndInternalPatientId(Integer etId,
			String patientId) {
		
		return aiDao.getAllergyResourceByExtractionIdAndInternalPatientId(etId, patientId);
	}

}
