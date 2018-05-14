package org.sitenv.spring.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.sitenv.spring.dao.EncounterResourceDao;
import org.sitenv.spring.model.EncounterResource;
import org.sitenv.spring.model.ExtractionTask;

import ca.uhn.fhir.model.dstu2.resource.Encounter;

@Service
@Transactional
public class EncounterResourceServiceImpl implements EncounterResourceService {

	@Autowired
	private EncounterResourceDao encounterDao;
	
	public EncounterResource saveOrUpdate(EncounterResource encounter) {
		
		return encounterDao.saveOrUpdate(encounter);
	}

	public List<EncounterResource> getAllEncounterResources() {
		
		return encounterDao.getAllEncounterResources();
	}

	public EncounterResource getEncounterResourceById(Integer id) {
		
		return encounterDao.getEncounterResourceById(id);
	}

	public List<EncounterResource> findDuplicatesBeforePersist(Encounter encounter, ExtractionTask et) {
		
		return encounterDao.findDuplicatesBeforePersist(encounter, et);
	}

	public List<EncounterResource> getEncounterResourcesByExtractionIdAndInternalPatientId(Integer etId,
			String patientId) {
		
		return encounterDao.getEncounterResourcesByExtractionIdAndInternalPatientId(etId, patientId);
	}

}
