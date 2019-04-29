package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafMedication;
import org.sitenv.spring.util.SearchParameterMap;

public interface MedicationService {
	
	public DafMedication getMedicationById(int id);
	
	public DafMedication getMedicationByVersionId(int theId, String versionId);
		
	public List<DafMedication> search(SearchParameterMap paramMap);
	
	public List<DafMedication> getMedicationHistoryById(int theId);
}
