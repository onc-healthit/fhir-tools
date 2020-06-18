package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.model.DafMedicationStatement;
import org.sitenv.spring.util.SearchParameterMap;

public interface MedicationStatementService {
	
	public DafMedicationStatement getMedicationStatementById(String id);
	
	public DafMedicationStatement getMedicationStatementByVersionId(String theId, String versionId);
		
	public List<DafMedicationStatement> search(SearchParameterMap paramMap);
	
	public List<DafMedicationStatement> getMedicationStatementHistoryById(String theId);

	public List<DafMedicationStatement> getMedicationStatementForBulkData(StringJoiner patients, Date start);

}
