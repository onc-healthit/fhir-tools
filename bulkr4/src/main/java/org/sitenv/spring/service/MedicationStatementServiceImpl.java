package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

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
	public DafMedicationStatement getMedicationStatementById(String id) {
		return this.medicationStatementDao.getMedicationStatementById(id);
	}

	@Override
	@Transactional
	public DafMedicationStatement getMedicationStatementByVersionId(String theId, String versionId) {
		return this.medicationStatementDao.getMedicationStatementByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafMedicationStatement> search(SearchParameterMap paramMap) {
		return this.medicationStatementDao.search(paramMap);
	}

	@Override
	@Transactional
	public List<DafMedicationStatement> getMedicationStatementHistoryById(String theId) {
		return this.medicationStatementDao.getMedicationStatementHistoryById(theId);
	}

	@Override
	public List<DafMedicationStatement> getMedicationStatementForBulkData(StringJoiner patients, Date start) {
		return this.medicationStatementDao.getMedicationStatementForBulkData(patients, start);
	}
}
