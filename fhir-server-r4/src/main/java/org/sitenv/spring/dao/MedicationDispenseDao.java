package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafMedicationDispense;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface MedicationDispenseDao {
	
	public DafMedicationDispense getMedicationDispenseById(String id);
	
	public DafMedicationDispense getMedicationDispenseByVersionId(String theId, String versionId);
		
	public List<DafMedicationDispense> search(SearchParameterMap paramMap);
	
	public List<DafMedicationDispense> getMedicationDispenseHistoryById(String theId);

}
