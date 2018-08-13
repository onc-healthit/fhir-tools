package org.sitenv.spring.service;

import org.sitenv.spring.dao.MedicationStatementDao;
import org.sitenv.spring.model.DafMedicationStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("medicationStatementResourceService")
@Transactional
public class MedicationStatementServiceImpl implements MedicationStatementService {

    @Autowired
    private MedicationStatementDao medicationStatementDao;


    @Override
    @Transactional
    public List<DafMedicationStatement> getAllMedicationStatement(Integer count) {
        return this.medicationStatementDao.getAllMedicationStatement(count);
    }

    @Override
    @Transactional
    public DafMedicationStatement getMedicationStatementResourceById(Integer id) {
        return this.medicationStatementDao.getMedicationStatementResourceById(id);
    }

    @Override
    @Transactional
    public List<DafMedicationStatement> getMedicationStatementByPatient(String patient) {
        return this.medicationStatementDao.getMedicationStatementByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafMedicationStatement> getMedicationStatementByCode(String code) {
        return this.medicationStatementDao.getMedicationStatementByCode(code);
    }

    @Override
    @Transactional
    public List<DafMedicationStatement> getMedicationStatementByIdentifier(String identifierSystem, String identifierValue) {
        return this.medicationStatementDao.getMedicationStatementByIdentifier(identifierSystem, identifierValue);
    }

    @Override
    @Transactional
    public List<DafMedicationStatement> getMedicationStatementByEffectiveDate(String comparatorStr, Date effectiveDate) {
        return this.medicationStatementDao.getMedicationStatementByEffectiveDate(comparatorStr, effectiveDate);
    }

    @Override
    @Transactional
    public List<DafMedicationStatement> getMedicationStatementByMedication(String medication) {
        return this.medicationStatementDao.getMedicationStatementByMedication(medication);
    }

    @Override
    public List<DafMedicationStatement> getMedicationStatementByStatus(String status) {
        return this.medicationStatementDao.getMedicationStatementByStatus(status);
    }

    @Override
    public List<DafMedicationStatement> getMedicationStatementByIdentifierValue(String identifierValue) {
        return this.medicationStatementDao.getMedicationStatementByIdentifierValue(identifierValue);
    }

	@Override
	public List<DafMedicationStatement> getMedicationStatementForBulkData(List<Integer> patients, Date start) {
		return this.medicationStatementDao.getMedicationStatementForBulkData(patients, start);
	}

}
