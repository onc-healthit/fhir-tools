package org.sitenv.spring.service;

import org.sitenv.spring.model.DafMedication;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface MedicationService {
	
	public DafMedication getMedicationById(String id);
	
	public DafMedication getMedicationByVersionId(String theId, String versionId);
		
	public List<DafMedication> search(SearchParameterMap paramMap);
	
	public List<DafMedication> getMedicationHistoryById(String theId);

	public List<DafMedication> getMedicationByResourceId(List<String> resourceID);
}
