package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.ObservationResource;

import ca.uhn.fhir.model.dstu2.resource.Observation;

public interface ObservationResourceService {
	
	public ObservationResource saveOrUpdate(ObservationResource observation);
	
	public List<ObservationResource> getAllObservationResources();
	
	public ObservationResource getObservationResourceById(Integer id);
	
	public List<ObservationResource> findDuplicatesBeforePersist(Observation observation, ExtractionTask et);
	
	public List<ObservationResource> getObservationResourcesByExtractionIdAndInternalPatientId(Integer etId, String patientId);
	
	public List<ObservationResource> getObservationsByPatientIdAndCategory(String internalPatienId, String category);

}
