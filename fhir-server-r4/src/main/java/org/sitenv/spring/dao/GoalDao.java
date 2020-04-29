package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafGoal;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

	public interface GoalDao {
	
	public DafGoal getGoalById(String id);
	
	public DafGoal getGoalByVersionId(String theId, String versionId);

	public List<DafGoal> search(SearchParameterMap paramMap);
	
	public List<DafGoal> getGoalHistoryById(String theId);
}
