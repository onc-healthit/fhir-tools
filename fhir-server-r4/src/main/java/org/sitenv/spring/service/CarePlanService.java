package org.sitenv.spring.service;

import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface CarePlanService {
	
	public DafCarePlan getCarePlanById(int id);
	
	public DafCarePlan getCarePlanByVersionId(int theId, String versionId);
	
	public List<DafCarePlan> getCarePlanHistoryById(int theId);
	
	public List<DafCarePlan> search(SearchParameterMap paramMap);
	
}
