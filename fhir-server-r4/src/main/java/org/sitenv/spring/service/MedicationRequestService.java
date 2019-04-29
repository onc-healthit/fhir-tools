package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.util.SearchParameterMap;

public interface MedicationRequestService {

	DafMedicationRequest getMedicationRequestById(int id);

	DafMedicationRequest getMedicationRequestByVersionId(int theId, String versionId);

	List<DafMedicationRequest> search(SearchParameterMap paramMap);

	List<DafMedicationRequest> getMedicationRequestHistoryById(int id);

}
