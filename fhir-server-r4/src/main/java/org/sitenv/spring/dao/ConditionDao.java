package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ConditionDao {
	
	public DafCondition getConditionById(String id);
	
	public DafCondition getConditionByVersionId(String theId, String versionId);
	
	public List<DafCondition> getConditionHistoryById(String theId);
	
	public List<DafCondition> search(SearchParameterMap theMap);
}
