package org.sitenv.spring.service;

import org.sitenv.spring.model.DafMedicationAdministration;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface MedicationAdministrationService {
	
	public DafMedicationAdministration getMedicationAdministrationById(String id);
	
	public DafMedicationAdministration getMedicationAdministrationByVersionId(String theId, String versionId);
	
	public List<DafMedicationAdministration> getMedicationAdministrationHistoryById(String theId);
	
	public List<DafMedicationAdministration> search(SearchParameterMap paramMap);
}
