package org.sitenv.spring.service;

import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.query.MedicationRequestSearchCriteria;

import java.util.List;

public interface MedicationRequestService {

    public List<DafMedicationRequest> getAllMedicationRequest();

    public DafMedicationRequest getMedicationRequestResourceById(int id);

    public List<DafMedicationRequest> getMedicationRequestBySearchCriteria(MedicationRequestSearchCriteria medicationRequestSearchCriteria);

}
