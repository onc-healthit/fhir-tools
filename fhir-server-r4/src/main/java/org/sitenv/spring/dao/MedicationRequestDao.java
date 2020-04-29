package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface MedicationRequestDao {

	DafMedicationRequest getMedicationRequestById(String id);

	DafMedicationRequest getMedicationRequestByVersionId(String theId, String versionId);

	List<DafMedicationRequest> search(SearchParameterMap paramMap);

	List<DafMedicationRequest> getMedicationRequestHistoryById(String id);

}
