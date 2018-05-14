package org.sitenv.spring.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.sitenv.spring.dao.ObservationResourceDao;
import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.ObservationResource;

import ca.uhn.fhir.model.dstu2.resource.Observation;

@Service
@Transactional
public class ObservationResourceServiceImpl implements ObservationResourceService {
	
	@Autowired
	private ObservationResourceDao observationDao;

	public ObservationResource saveOrUpdate(ObservationResource observation) {
		
		return observationDao.saveOrUpdate(observation);
	}

	public List<ObservationResource> getAllObservationResources() {
		
		return observationDao.getAllObservationResources();
	}

	public ObservationResource getObservationResourceById(Integer id) {
		
		return observationDao.getObservationResourceById(id);
	}

	public List<ObservationResource> findDuplicatesBeforePersist(Observation observation, ExtractionTask et) {
		
		return observationDao.findDuplicatesBeforePersist(observation, et);
	}

	public List<ObservationResource> getObservationResourcesByExtractionIdAndInternalPatientId(Integer etId,
			String patientId) {
		
		return observationDao.getObservationResourcesByExtractionIdAndInternalPatientId(etId, patientId);
	}

	public List<ObservationResource> getObservationsByPatientIdAndCategory(String internalPatienId, String category) {
	
		return observationDao.getObservationsByPatientIdAndCategory(internalPatienId, category);
	}

}
