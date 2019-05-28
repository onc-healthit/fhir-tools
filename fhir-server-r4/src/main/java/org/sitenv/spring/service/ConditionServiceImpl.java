package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.dao.ConditionDao;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("conditionService")
@Transactional
public class ConditionServiceImpl implements ConditionService {

	@Autowired
	private ConditionDao conditionDao;

	@Override
	@Transactional
	public DafCondition getConditionById(int id) {
		return this.conditionDao.getConditionById(id);
	}

	@Override
	@Transactional
	public DafCondition getConditionByVersionId(int theId, String versionId) {
		return this.conditionDao.getConditionByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafCondition> search(SearchParameterMap paramMap) {
		return this.conditionDao.search(paramMap);
	}

	@Override
	@Transactional
	public List<DafCondition> getConditionHistoryById(int theId) {
		return this.conditionDao.getConditionHistoryById(theId);
	}
}
