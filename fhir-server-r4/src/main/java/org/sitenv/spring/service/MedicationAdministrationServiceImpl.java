package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.dao.MedicationAdministrationDao;
import org.sitenv.spring.model.DafMedicationAdministration;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("medicationAdministrationService")
@Transactional
public class MedicationAdministrationServiceImpl implements MedicationAdministrationService {

	@Autowired
    private MedicationAdministrationDao medicationAdministrationDao;

	@Override
	@Transactional
	public DafMedicationAdministration getMedicationAdministrationById(int id) {
		return this.medicationAdministrationDao.getMedicationAdministrationById(id);
	}

	@Override
	@Transactional
	public DafMedicationAdministration getMedicationAdministrationByVersionId(int theId, String versionId) {
		return this.medicationAdministrationDao.getMedicationAdministrationByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafMedicationAdministration> getMedicationAdministrationHistoryById(int theId) {
		return this.medicationAdministrationDao.getMedicationAdministrationHistoryById(theId);
	}

	@Override
	@Transactional
	public List<DafMedicationAdministration> search(SearchParameterMap paramMap) {
		return this.medicationAdministrationDao.search(paramMap);
	}
}
