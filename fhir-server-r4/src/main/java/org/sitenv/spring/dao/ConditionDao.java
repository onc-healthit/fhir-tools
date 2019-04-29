package org.sitenv.spring.dao;

import java.util.List;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.util.SearchParameterMap;

public interface ConditionDao {
	
	public DafCondition getConditionById(int id);
	
	public DafCondition getConditionByVersionId(int theId, String versionId);
	
	public List<DafCondition> getConditionHistoryById(int theId);
	
	public List<DafCondition> search(SearchParameterMap theMap);
}
