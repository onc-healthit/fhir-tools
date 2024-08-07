package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafGoal;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

	public interface GoalDao {
	
	public DafGoal getGoalById(String id);
	
	public DafGoal getGoalByVersionId(String theId, String versionId);

	public List<DafGoal> search(SearchParameterMap paramMap);
	
	public List<DafGoal> getGoalHistoryById(String theId);

	public List<DafGoal> getGoalForBulkData(StringJoiner patient, Date start);
}
