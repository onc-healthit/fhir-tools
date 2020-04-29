package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafMedication;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface MedicationDao {
	
	 public DafMedication getMedicationById(String id);
	 
	 public DafMedication getMedicationByVersionId(String theId, String versionId);
	 
	 public List<DafMedication> search(SearchParameterMap theMap);
	 
	 public List<DafMedication> getMedicationHistoryById(String theId);

	public List<DafMedication> getMedicationByResourceId(List<String> resourceID);
}
