package org.sitenv.spring.service;

import org.sitenv.spring.model.DafMedicationStatement;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface MedicationStatementService {
	
	public DafMedicationStatement getMedicationStatementById(int id);
	
	public DafMedicationStatement getMedicationStatementByVersionId(int theId, String versionId);
		
	public List<DafMedicationStatement> search(SearchParameterMap paramMap);
	
	public List<DafMedicationStatement> getMedicationStatementHistoryById(int theId);

}
