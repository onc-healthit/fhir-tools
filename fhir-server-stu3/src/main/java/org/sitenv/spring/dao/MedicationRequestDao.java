package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.query.MedicationRequestSearchCriteria;

import java.util.Date;
import java.util.List;

public interface MedicationRequestDao {

    public List<DafMedicationRequest> getAllMedicationRequest();

    public DafMedicationRequest getMedicationRequestResourceById(int id);

    public List<DafMedicationRequest> getMedicationRequestBySearchCriteria(MedicationRequestSearchCriteria medicationRequestSearchCriteria);

	public List<DafMedicationRequest> getMedicationRequestForBulkData(List<Integer> patients, Date start);

}
