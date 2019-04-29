package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafMedicationDispense;
import org.sitenv.spring.util.SearchParameterMap;

public interface MedicationDispenseDao {
	
	public DafMedicationDispense getMedicationDispenseById(int id);
	
	public DafMedicationDispense getMedicationDispenseByVersionId(int theId, String versionId);
		
	public List<DafMedicationDispense> search(SearchParameterMap paramMap);
	
	public List<DafMedicationDispense> getMedicationDispenseHistoryById(int theId);

}
