package org.sitenv.spring.service;

import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface CarePlanService {
	
	public DafCarePlan getCarePlanById(String id);
	
	public DafCarePlan getCarePlanByVersionId(String theId, String versionId);
	
	public List<DafCarePlan> getCarePlanHistoryById(String theId);
	
	public List<DafCarePlan> search(SearchParameterMap paramMap);
	
}
