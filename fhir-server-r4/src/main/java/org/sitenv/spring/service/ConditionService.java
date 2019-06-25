package org.sitenv.spring.service;

import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ConditionService {

    public DafCondition getConditionById(int id);
	
	public DafCondition getConditionByVersionId(int theId, String versionId);
	
	public List<DafCondition> getConditionHistoryById(int theId);
	
	public List<DafCondition> search(SearchParameterMap theMap);
}
