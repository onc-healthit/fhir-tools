package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.ObservationResource;

import ca.uhn.fhir.model.dstu2.resource.Observation;

public interface ObservationResourceDao {
	
	public ObservationResource saveOrUpdate(ObservationResource observation);
	
	public List<ObservationResource> getAllObservationResources();
	
	public ObservationResource getObservationResourceById(Integer id);
	
	public List<ObservationResource> findDuplicatesBeforePersist(Observation observation, ExtractionTask et);
	
	public List<ObservationResource> getObservationResourcesByExtractionIdAndInternalPatientId(Integer etId, String patientId);
	
	//public List<ObservationResource> getUniqueObservationResourcesByInternalPatientId(String internalPatientId);
	
	//public List<ObservationResource> getUniqueObservationResourcesByCodeAndDisplay(String code, String display);
	
	public List<ObservationResource> getObservationsByPatientIdAndCategory(String internalPatienId, String category);

}
