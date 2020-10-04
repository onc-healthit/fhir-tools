package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.ConditionResource;
import org.sitenv.spring.model.ExtractionTask;

import ca.uhn.fhir.model.dstu2.resource.Condition;

public interface ConditionResourceDao {
	
	public ConditionResource saveOrUpdate(ConditionResource condition);
	
	public List<ConditionResource> getAllConditionResources();
	
	public ConditionResource getConditionResourceById(Integer id);
	
	public List<ConditionResource> findDuplicatesBeforePersist(Condition condition, ExtractionTask et);
	
	public List<ConditionResource> getConditionResourcesByExtractionIdAndInternalPatientId(Integer etId, String patientId);
	
	//public List<ConditionResource> getUniqueConditionsByInternalPatientId(String internalPatientId);
	
	//public List<ConditionResource> getUniqueConditionsByCodeAndDisplay(String code, String display);

}
