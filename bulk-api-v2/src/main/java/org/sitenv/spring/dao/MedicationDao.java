package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.model.DafMedication;

import java.util.Date;
import java.util.List;

public interface MedicationDao {

    public List<DafMedication> getAllMedication();

    public DafMedication getMedicationResourceById(int id);

    public List<DafMedication> getMedicationByCode(String code);

    public List<DafMedication> getMedicationForBulkData(List<Integer> patients, Date start);
}
