package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafHealthcareService;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface HealthcareServiceDao {
	
	public DafHealthcareService getHealthcareServiceById(String id);
	
	public DafHealthcareService getHealthcareServiceByVersionId(String theId, String versionId);

	public List<DafHealthcareService> search(SearchParameterMap paramMap);

	public List<DafHealthcareService> getHealthcareServiceHistoryById(String theId);
}
