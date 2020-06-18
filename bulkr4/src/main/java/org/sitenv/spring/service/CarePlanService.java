package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.util.SearchParameterMap;

public interface CarePlanService {
	
	public DafCarePlan getCarePlanById(String id);
	
	public DafCarePlan getCarePlanByVersionId(String theId, String versionId);
	
	public List<DafCarePlan> getCarePlanHistoryById(String theId);
	
	public List<DafCarePlan> search(SearchParameterMap paramMap);
	
	public List<DafCarePlan> getCarePlanForBulkData(StringJoiner patients, Date start);
	
}
