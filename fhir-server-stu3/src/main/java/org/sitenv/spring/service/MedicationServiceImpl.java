package org.sitenv.spring.service;

import org.sitenv.spring.dao.MedicationDao;
import org.sitenv.spring.model.DafMedication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("medicationService")
@Transactional
public class MedicationServiceImpl implements MedicationService {

    @Autowired
    private MedicationDao medicationDao;


    @Override
    @Transactional
    public List<DafMedication> getAllMedication() {
        return this.medicationDao.getAllMedication();
    }

    @Override
    @Transactional
    public DafMedication getMedicationResourceById(int id) {
        return this.medicationDao.getMedicationResourceById(id);
    }

    @Override
    public List<DafMedication> getMedicationByCode(String code) {
        return this.medicationDao.getMedicationByCode(code);
    }

	@Override
	public List<DafMedication> getMedicationForBulkData(List<Integer> patients, Date start) {
		return this.medicationDao.getMedicationForBulkData(patients, start);
	}

}
