package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.util.SearchParameterMap;

public interface CarePlanDao {
	
	public DafCarePlan getCarePlanById(int id);
	
	public DafCarePlan getCarePlanByVersionId(int theId, String versionId);
	
	public List<DafCarePlan> getCarePlanHistoryById(int theId);

	public List<DafCarePlan> search(SearchParameterMap paramMap);
}
