package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafGoal;
import org.sitenv.spring.util.SearchParameterMap;

public interface GoalService {
	
	public DafGoal getGoalById(int id);
	
	public DafGoal getGoalByVersionId(int theId, String versionId);

	public List<DafGoal> search(SearchParameterMap paramMap);
	
	public List<DafGoal> getGoalHistoryById(int theId);
}
