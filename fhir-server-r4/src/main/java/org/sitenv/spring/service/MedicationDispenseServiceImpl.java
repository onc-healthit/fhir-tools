package org.sitenv.spring.service;

import java.util.List;

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
	public DafMedicationDispense getMedicationDispenseById(int id) {
		return this.medicationDispenseDao.getMedicationDispenseById(id);
	}

	@Override
	@Transactional
	public DafMedicationDispense getMedicationDispenseByVersionId(int theId, String versionId) {
		return this.medicationDispenseDao.getMedicationDispenseByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafMedicationDispense> search(SearchParameterMap paramMap) {
		return this.medicationDispenseDao.search(paramMap);
	}

	@Override
	@Transactional
	public List<DafMedicationDispense> getMedicationDispenseHistoryById(int theId) {
		return this.medicationDispenseDao.getMedicationDispenseHistoryById(theId);
	}

}
