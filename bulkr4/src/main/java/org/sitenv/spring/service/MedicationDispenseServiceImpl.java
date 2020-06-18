package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.dao.MedicationDispenseDao;
import org.sitenv.spring.model.DafMedicationDispense;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("medicationDispenseService")
@Transactional
public class MedicationDispenseServiceImpl implements MedicationDispenseService {
	
	@Autowired
    private MedicationDispenseDao medicationDispenseDao;

	@Override
	@Transactional
	public DafMedicationDispense getMedicationDispenseById(String id) {
		return this.medicationDispenseDao.getMedicationDispenseById(id);
	}

	@Override
	@Transactional
	public DafMedicationDispense getMedicationDispenseByVersionId(String theId, String versionId) {
		return this.medicationDispenseDao.getMedicationDispenseByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafMedicationDispense> search(SearchParameterMap paramMap) {
		return this.medicationDispenseDao.search(paramMap);
	}

	@Override
	@Transactional
	public List<DafMedicationDispense> getMedicationDispenseHistoryById(String theId) {
		return this.medicationDispenseDao.getMedicationDispenseHistoryById(theId);
	}

	@Override
	public List<DafMedicationDispense> getMedicationDispenseForBulkData(StringJoiner patients, Date start) {
		return this.medicationDispenseDao.getMedicationDispenseForBulkData(patients, start);
	}

}
