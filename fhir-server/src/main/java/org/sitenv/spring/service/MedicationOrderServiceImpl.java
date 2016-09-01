package org.sitenv.spring.service;

import org.sitenv.spring.dao.MedicationOrderDao;
import org.sitenv.spring.model.DafMedicationOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("medicationOrderResourceService")
@Transactional
public class MedicationOrderServiceImpl implements MedicationOrderService {

    @Autowired
    private MedicationOrderDao medicationOrderDao;


    @Override
    @Transactional
    public List<DafMedicationOrder> getAllMedicationOrder() {
        return this.medicationOrderDao.getAllMedicationOrder();
    }

    @Override
    @Transactional
    public DafMedicationOrder getMedicationOrderResourceById(int id) {
        return this.medicationOrderDao.getMedicationOrderResourceById(id);
    }

    @Override
    @Transactional
    public List<DafMedicationOrder> getMedicationOrderByPatient(String patient) {
        return this.medicationOrderDao.getMedicationOrderByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafMedicationOrder> getMedicationOrderByCode(String code) {
        return this.medicationOrderDao.getMedicationOrderByCode(code);
    }

    @Override
    @Transactional
    public List<DafMedicationOrder> getMedicationOrderByIdentifier(String identifierSystem, String identifierValue) {
        return this.medicationOrderDao.getMedicationOrderByIdentifier(identifierSystem, identifierValue);
    }

    @Override
    @Transactional
    public List<DafMedicationOrder> getMedicationOrderByMedication(String medication) {
        return this.medicationOrderDao.getMedicationOrderByMedication(medication);
    }

    @Override
    @Transactional
    public List<DafMedicationOrder> getMedicationOrderByPrescriber(String prescriber) {
        return this.medicationOrderDao.getMedicationOrderByPrescriber(prescriber);
    }

    @Override
    public List<DafMedicationOrder> getMedicationOrderByStatus(String status) {
        return this.medicationOrderDao.getMedicationOrderByStatus(status);
    }

    @Override
    @Transactional
    public List<DafMedicationOrder> getMedicationOrderByDateWritten(String comparatorStr, Date dateWritten) {
        return this.medicationOrderDao.getMedicationOrderByDateWritten(comparatorStr, dateWritten);
    }

    @Override
    public List<DafMedicationOrder> getMedicationOrderByIdentifierValue(String identifierValue) {
        return this.medicationOrderDao.getMedicationOrderByIdentifierValue(identifierValue);
    }

}
