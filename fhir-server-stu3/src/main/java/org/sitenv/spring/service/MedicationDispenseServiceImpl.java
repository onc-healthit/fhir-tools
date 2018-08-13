package org.sitenv.spring.service;

import org.sitenv.spring.dao.MedicationDispenseDao;
import org.sitenv.spring.model.DafMedicationDispense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("medicationDispenseResourceService")
@Transactional
public class MedicationDispenseServiceImpl implements MedicationDispenseService {

    @Autowired
    private MedicationDispenseDao medicationDispenseDao;


    @Override
    @Transactional
    public List<DafMedicationDispense> getAllMedicationDispense() {
        return this.medicationDispenseDao.getAllMedicationDispense();
    }

    @Override
    @Transactional
    public DafMedicationDispense getMedicationDispenseResourceById(int id) {
        return this.medicationDispenseDao.getMedicationDispenseResourceById(id);
    }

    @Override
    @Transactional
    public List<DafMedicationDispense> getMedicationDispenseByPatient(String patient) {
        return this.medicationDispenseDao.getMedicationDispenseByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafMedicationDispense> getMedicationDispenseByCode(String code) {
        return this.medicationDispenseDao.getMedicationDispenseByCode(code);
    }

    @Override
    @Transactional
    public List<DafMedicationDispense> getMedicationDispenseByIdentifier(String identifierSystem, String identifierValue) {
        return this.medicationDispenseDao.getMedicationDispenseByIdentifier(identifierSystem, identifierValue);
    }

    @Override
    @Transactional
    public List<DafMedicationDispense> getMedicationDispenseByMedication(String medication) {
        return this.medicationDispenseDao.getMedicationDispenseByMedication(medication);
    }

    @Override
    public List<DafMedicationDispense> getMedicationDispenseByStatus(String status) {
        return this.medicationDispenseDao.getMedicationDispenseByStatus(status);
    }

	@Override
	public List<DafMedicationDispense> getMedicationDispenseForBulkData(List<Integer> patients, Date start) {
		return this.medicationDispenseDao.getMedicationDispenseForBulkData(patients, start);
	}

}
