package org.sitenv.spring.service;

import org.sitenv.spring.model.DafMedicationDispense;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface MedicationDispenseService {
	
	public DafMedicationDispense getMedicationDispenseById(String id);
	
	public DafMedicationDispense getMedicationDispenseByVersionId(String theId, String versionId);
		
	public List<DafMedicationDispense> search(SearchParameterMap paramMap);
	
	public List<DafMedicationDispense> getMedicationDispenseHistoryById(String theId);

	public List<DafMedicationDispense> getMedicationDispenseForBulkData(StringJoiner patients, Date start);

}
