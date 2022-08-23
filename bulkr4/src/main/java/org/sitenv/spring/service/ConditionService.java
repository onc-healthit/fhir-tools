package org.sitenv.spring.service;

import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface ConditionService {

    public DafCondition getConditionById(String id);
	
	public DafCondition getConditionByVersionId(String theId, String versionId);
	
	public List<DafCondition> getConditionHistoryById(String theId);
	
	public List<DafCondition> search(SearchParameterMap theMap);
	
	public List<DafCondition> getConditionForBulkData(StringJoiner patients, Date start);
}
