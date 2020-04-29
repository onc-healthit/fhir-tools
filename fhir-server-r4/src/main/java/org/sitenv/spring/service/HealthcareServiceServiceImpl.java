package org.sitenv.spring.service;

import org.sitenv.spring.dao.HealthcareServiceDao;
import org.sitenv.spring.model.DafHealthcareService;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("healthcareServiceService")
@Transactional
public class HealthcareServiceServiceImpl implements HealthcareServiceService{
	
	@Autowired
    private HealthcareServiceDao healthcareServiceDao;

	@Override
	@Transactional
	public DafHealthcareService getHealthcareServiceById(String id) {
		return this.healthcareServiceDao.getHealthcareServiceById(id);
	}

	@Override
	@Transactional
	public DafHealthcareService getHealthcareServiceByVersionId(String theId, String versionId) {
		return this.healthcareServiceDao.getHealthcareServiceByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafHealthcareService> search(SearchParameterMap paramMap) {
		return this.healthcareServiceDao.search(paramMap);
	}

	@Override
	@Transactional
	public List<DafHealthcareService> getHealthcareServiceHistoryById(String theId) {
		return this.healthcareServiceDao.getHealthcareServiceHistoryById(theId);
	}

}
