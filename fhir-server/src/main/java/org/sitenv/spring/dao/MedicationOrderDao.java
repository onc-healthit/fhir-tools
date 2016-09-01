package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafMedicationOrder;

import java.util.Date;
import java.util.List;

public interface MedicationOrderDao {

    public List<DafMedicationOrder> getAllMedicationOrder();

    public DafMedicationOrder getMedicationOrderResourceById(int id);

    public List<DafMedicationOrder> getMedicationOrderByPatient(String patient);

    public List<DafMedicationOrder> getMedicationOrderByCode(String code);

    public List<DafMedicationOrder> getMedicationOrderByIdentifier(String identifierSystem, String identifierValue);

    public List<DafMedicationOrder> getMedicationOrderByMedication(String medication);

    public List<DafMedicationOrder> getMedicationOrderByPrescriber(String prescriber);

    public List<DafMedicationOrder> getMedicationOrderByStatus(String status);

    public List<DafMedicationOrder> getMedicationOrderByDateWritten(String comparatorStr, Date dateWritten);

    public List<DafMedicationOrder> getMedicationOrderByIdentifierValue(String identifierValue);

}
