package org.sitenv.spring.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.sitenv.spring.dao.MedicationStatementResourceDao;
import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.MedicationStatementResource;

import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;

@Service
@Transactional
public class MedicationStatementResourceServiceImpl implements MedicationStatementResourceService {
	
	@Autowired
	private MedicationStatementResourceDao msDao;

	public MedicationStatementResource saveOrUpdate(MedicationStatementResource ms) {
		
		return msDao.saveOrUpdate(ms);
	}

	public List<MedicationStatementResource> getAllMedicationStatements() {
		
		return msDao.getAllMedicationStatements();
	}

	public MedicationStatementResource getMedicationStatementById(Integer msId) {
		
		return msDao.getMedicationStatementById(msId);
	}

	public List<MedicationStatementResource> findDuplicatesBeforePersist(MedicationStatement ms, ExtractionTask et) {
		
		return msDao.findDuplicatesBeforePersist(ms, et);
	}

	public List<MedicationStatementResource> getMedicationStatementsByExtractionIdAndInternalPatientId(Integer etId,
			String patientId) {
		
		return msDao.getMedicationStatementsByExtractionIdAndInternalPatientId(etId, patientId);
	}

}
