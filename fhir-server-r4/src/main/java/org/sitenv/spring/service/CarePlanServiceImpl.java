package org.sitenv.spring.service;

import org.sitenv.spring.dao.CarePlanDao;
import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("carePlanService")
@Transactional
public class CarePlanServiceImpl implements CarePlanService {
	
	@Autowired
    private CarePlanDao carePlanDao;

	@Override
	@Transactional
	public DafCarePlan getCarePlanById(String id) {
		return this.carePlanDao.getCarePlanById(id);
	}

	@Override
	@Transactional
	public DafCarePlan getCarePlanByVersionId(String theId, String versionId) {
		return this.carePlanDao.getCarePlanByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafCarePlan> getCarePlanHistoryById(String theId) {
		return this.carePlanDao.getCarePlanHistoryById(theId);
	}

	@Override
	public List<DafCarePlan> search(SearchParameterMap paramMap) {
		return this.carePlanDao.search(paramMap);
	}

}
