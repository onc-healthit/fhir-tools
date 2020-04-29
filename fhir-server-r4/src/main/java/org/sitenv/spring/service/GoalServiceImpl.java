package org.sitenv.spring.service;

import org.sitenv.spring.dao.GoalDao;
import org.sitenv.spring.model.DafGoal;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("goalService")
@Transactional
public class GoalServiceImpl implements GoalService {
	
	@Autowired
    private GoalDao goalDao;

	@Override
	public DafGoal getGoalById(String id) {
		return this.goalDao.getGoalById(id);
	}

	@Override
	public DafGoal getGoalByVersionId(String theId, String versionId) {
		return this.goalDao.getGoalByVersionId(theId, versionId);
	}

	@Override
	public List<DafGoal> search(SearchParameterMap paramMap) {
		return this.goalDao.search(paramMap);
	}

	@Override
	public List<DafGoal> getGoalHistoryById(String theId) {
		return this.goalDao.getGoalHistoryById(theId);
	}

}
