package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.dao.MedicationStatementDao;
import org.sitenv.spring.model.DafMedicationStatement;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("medicationStatementService")
@Transactional
public class MedicationStatementServiceImpl implements MedicationStatementService {
	
	@Autowired
    private MedicationStatementDao medicationStatementDao;

	@Override
	@Transactional
	public DafMedicationStatement getMedicationStatementById(int id) {
		return this.medicationStatementDao.getMedicationStatementById(id);
	}

	@Override
	@Transactional
	public DafMedicationStatement getMedicationStatementByVersionId(int theId, String versionId) {
		return this.medicationStatementDao.getMedicationStatementByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafMedicationStatement> search(SearchParameterMap paramMap) {
		return this.medicationStatementDao.search(paramMap);
	}

	@Override
	@Transactional
	public List<DafMedicationStatement> getMedicationStatementHistoryById(int theId) {
		return this.medicationStatementDao.getMedicationStatementHistoryById(theId);
	}
}
