package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.util.SearchParameterMap;

public interface MedicationRequestDao {

	DafMedicationRequest getMedicationRequestById(int id);

	DafMedicationRequest getMedicationRequestByVersionId(int theId, String versionId);

	List<DafMedicationRequest> search(SearchParameterMap paramMap);

	List<DafMedicationRequest> getMedicationRequestHistoryById(int id);

}
