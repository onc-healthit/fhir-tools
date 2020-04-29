package org.sitenv.spring.service;

import org.sitenv.spring.model.DafMedicationStatement;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface MedicationStatementService {
	
	public DafMedicationStatement getMedicationStatementById(String id);
	
	public DafMedicationStatement getMedicationStatementByVersionId(String theId, String versionId);
		
	public List<DafMedicationStatement> search(SearchParameterMap paramMap);
	
	public List<DafMedicationStatement> getMedicationStatementHistoryById(String theId);

}
