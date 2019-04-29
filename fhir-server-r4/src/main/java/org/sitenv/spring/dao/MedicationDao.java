package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafMedication;
import org.sitenv.spring.util.SearchParameterMap;

public interface MedicationDao {
	
	 public DafMedication getMedicationById(int id);
	 
	 public DafMedication getMedicationByVersionId(int theId, String versionId);
	 
	 public List<DafMedication> search(SearchParameterMap theMap);
	 
	 public List<DafMedication> getMedicationHistoryById(int theId);
}
