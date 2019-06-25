package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafGoal;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

	public interface GoalDao {
	
	public DafGoal getGoalById(int id);
	
	public DafGoal getGoalByVersionId(int theId, String versionId);

	public List<DafGoal> search(SearchParameterMap paramMap);
	
	public List<DafGoal> getGoalHistoryById(int theId);
}
