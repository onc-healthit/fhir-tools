package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface CarePlanDao {
	
	public DafCarePlan getCarePlanById(int id);
	
	public DafCarePlan getCarePlanByVersionId(int theId, String versionId);
	
	public List<DafCarePlan> getCarePlanHistoryById(int theId);

	public List<DafCarePlan> search(SearchParameterMap paramMap);
}
