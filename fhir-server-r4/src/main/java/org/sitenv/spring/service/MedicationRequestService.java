package org.sitenv.spring.service;

import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface MedicationRequestService {

	DafMedicationRequest getMedicationRequestById(String id);

	DafMedicationRequest getMedicationRequestByVersionId(String theId, String versionId);

	List<DafMedicationRequest> search(SearchParameterMap paramMap);

	List<DafMedicationRequest> getMedicationRequestHistoryById(String id);

}
