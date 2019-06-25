package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafMedicationDispense;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface MedicationDispenseDao {
	
	public DafMedicationDispense getMedicationDispenseById(int id);
	
	public DafMedicationDispense getMedicationDispenseByVersionId(int theId, String versionId);
		
	public List<DafMedicationDispense> search(SearchParameterMap paramMap);
	
	public List<DafMedicationDispense> getMedicationDispenseHistoryById(int theId);

}
