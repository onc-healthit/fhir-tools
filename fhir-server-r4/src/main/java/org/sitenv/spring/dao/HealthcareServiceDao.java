package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafHealthcareService;
import org.sitenv.spring.util.SearchParameterMap;

public interface HealthcareServiceDao {
	
	public DafHealthcareService getHealthcareServiceById(int id);
	
	public DafHealthcareService getHealthcareServiceByVersionId(int theId, String versionId);

	public List<DafHealthcareService> search(SearchParameterMap paramMap);

	public List<DafHealthcareService> getHealthcareServiceHistoryById(int theId);
}
