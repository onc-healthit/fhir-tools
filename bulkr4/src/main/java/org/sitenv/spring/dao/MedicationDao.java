package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafMedication;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface MedicationDao {
	
	 public DafMedication getMedicationById(String id);
	 
	 public DafMedication getMedicationByVersionId(String theId, String versionId);
	 
	 public List<DafMedication> search(SearchParameterMap theMap);
	 
	 public List<DafMedication> getMedicationHistoryById(String theId);

	public List<DafMedication> getMedicationForBulkData(StringJoiner patient, Date start);
}
