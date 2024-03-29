package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.MedicationStatementResource;

import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;

public interface MedicationStatementResourceDao {
	
	public MedicationStatementResource saveOrUpdate(MedicationStatementResource ms);
	
	public List<MedicationStatementResource> getAllMedicationStatements();
	
	public MedicationStatementResource getMedicationStatementById(Integer msId);
	
	public List<MedicationStatementResource> findDuplicatesBeforePersist(MedicationStatement ms, ExtractionTask et);
	
	public List<MedicationStatementResource> getMedicationStatementsByExtractionIdAndInternalPatientId(Integer etId, String patientId);
	
	//public List<MedicationStatementResource> getUniqueMedStatementsByInternalPatientId(String internalPatientId);
	
	//public List<MedicationStatementResource> getMedicationStatementsByCodeAndDisplay(String code, String display);

}
