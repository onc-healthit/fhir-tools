package org.sitenv.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.sitenv.spring.dao.ConditionResourceDao;
import org.sitenv.spring.model.ConditionResource;
import org.sitenv.spring.model.ExtractionTask;

import ca.uhn.fhir.model.dstu2.resource.Condition;

@Service
@Transactional
public class ConditionResourceServiceImpl implements ConditionResourceService {
	
	@Autowired
	private ConditionResourceDao conditionDao;

	public ConditionResource saveOrUpdate(ConditionResource condition) {
		
		return conditionDao.saveOrUpdate(condition);
	}

	public List<ConditionResource> getAllConditionResources() {
		
		return conditionDao.getAllConditionResources();
	}

	public ConditionResource getConditionResourceById(Integer id) {
		
		return conditionDao.getConditionResourceById(id);
	}

	public List<ConditionResource> findDuplicatesBeforePersist(Condition condition, ExtractionTask et) {
		
		return conditionDao.findDuplicatesBeforePersist(condition, et);
	}

	public List<ConditionResource> getConditionResourcesByExtractionIdAndInternalPatientId(Integer etId,
			String patientId) {
		
		return conditionDao.getConditionResourcesByExtractionIdAndInternalPatientId(etId, patientId);
	}

}
