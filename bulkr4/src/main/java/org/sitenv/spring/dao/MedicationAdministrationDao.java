package org.sitenv.spring.dao;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.model.DafMedicationAdministration;
import org.sitenv.spring.util.SearchParameterMap;

public interface MedicationAdministrationDao {
	public DafMedicationAdministration getMedicationAdministrationById(String id);
	
	public DafMedicationAdministration getMedicationAdministrationByVersionId(String theId, String versionId);
	
	public List<DafMedicationAdministration> getMedicationAdministrationHistoryById(String theId);
	
	public List<DafMedicationAdministration> search(SearchParameterMap paramMap);

	public List<DafMedicationAdministration> getMedicationAdministrationForBulkData(StringJoiner patient, Date start);

}
