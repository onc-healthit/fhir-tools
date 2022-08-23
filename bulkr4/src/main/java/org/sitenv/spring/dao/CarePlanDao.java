package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface CarePlanDao {
	
	public DafCarePlan getCarePlanById(String id);
	
	public DafCarePlan getCarePlanByVersionId(String theId, String versionId);
	
	public List<DafCarePlan> getCarePlanHistoryById(String theId);

	public List<DafCarePlan> search(SearchParameterMap paramMap);
	
	public List<DafCarePlan> getCarePlanForBulkData(StringJoiner patients, Date start);
}
