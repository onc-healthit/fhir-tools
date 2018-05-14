package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.AllergyIntoleranceResource;
import org.sitenv.spring.model.ExtractionTask;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;

public interface AllergyIntoleranceResourceService {
	
	public AllergyIntoleranceResource saveOrUpdate(AllergyIntoleranceResource ai);
	
	public List<AllergyIntoleranceResource> getAllAllergyIntolerances();
	
	public AllergyIntoleranceResource getAllergyIntoleranceById(Integer aiId);
	
	public List<AllergyIntoleranceResource> findDuplicatesBeforePersist(AllergyIntolerance allergy, ExtractionTask et);
	
	public List<AllergyIntoleranceResource> getAllergyResourceByExtractionIdAndInternalPatientId(Integer etId, String patientId);

}
