package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface CarePlanDao {
	
	public DafCarePlan getCarePlanById(String id);
	
	public DafCarePlan getCarePlanByVersionId(String theId, String versionId);
	
	public List<DafCarePlan> getCarePlanHistoryById(String theId);

	public List<DafCarePlan> search(SearchParameterMap paramMap);
}
