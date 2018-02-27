package org.sitenv.spring.service;

import org.sitenv.spring.dao.MedicationAdministrationDao;
import org.sitenv.spring.model.DafMedicationAdministration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("medicationAdministrationResourceService")
@Transactional
public class MedicationAdministrationServiceImpl implements MedicationAdministrationService {

    @Autowired
    private MedicationAdministrationDao medicationAdministrationDao;


    @Override
    @Transactional
    public List<DafMedicationAdministration> getAllMedicationAdministration() {
        return this.medicationAdministrationDao.getAllMedicationAdministration();
    }

    @Override
    @Transactional
    public DafMedicationAdministration getMedicationAdministrationResourceById(int id) {
        return this.medicationAdministrationDao.getMedicationAdministrationResourceById(id);
    }

    @Override
    @Transactional
    public List<DafMedicationAdministration> getMedicationAdministrationByPatient(String patient) {
        return this.medicationAdministrationDao.getMedicationAdministrationByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafMedicationAdministration> getMedicationAdministrationByCode(String code) {
        return this.medicationAdministrationDao.getMedicationAdministrationByCode(code);
    }

    @Override
    @Transactional
    public List<DafMedicationAdministration> getMedicationAdministrationByIdentifier(String identifierSystem, String identifierValue) {
        return this.medicationAdministrationDao.getMedicationAdministrationByIdentifier(identifierSystem, identifierValue);
    }

    @Override
    @Transactional
    public List<DafMedicationAdministration> getMedicationAdministrationByMedication(String medication) {
        return this.medicationAdministrationDao.getMedicationAdministrationByMedication(medication);
    }

    @Override
    public List<DafMedicationAdministration> getMedicationAdministrationByStatus(String status) {
        return this.medicationAdministrationDao.getMedicationAdministrationByStatus(status);
    }
    
    @Override
    public List<DafMedicationAdministration> getMedicationAdministrationForBulkData(List<Integer> patients, Date start) {
    	return this.medicationAdministrationDao.getMedicationAdministrationForBulkData(patients, start);
    }

}
