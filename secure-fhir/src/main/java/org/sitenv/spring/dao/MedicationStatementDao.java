package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.model.DafMedicationStatement;

import java.util.Date;
import java.util.List;

public interface MedicationStatementDao {

    public List<DafMedicationStatement> getAllMedicationStatement(Integer count);

    public DafMedicationStatement getMedicationStatementResourceById(Integer id);

    public List<DafMedicationStatement> getMedicationStatementByPatient(String patient);

    public List<DafMedicationStatement> getMedicationStatementByCode(String code);

    public List<DafMedicationStatement> getMedicationStatementByIdentifier(String identifierSystem, String identifierValue);

    public List<DafMedicationStatement> getMedicationStatementByEffectiveDate(String comparatorStr, Date effectiveDate);

    public List<DafMedicationStatement> getMedicationStatementByMedication(String medication);

    public List<DafMedicationStatement> getMedicationStatementByStatus(String status);

    public List<DafMedicationStatement> getMedicationStatementByIdentifierValue(String identifierValue);

    public List<DafMedicationStatement> getMedicationStatementForBulkData(List<Integer> patients, Date start);
}
