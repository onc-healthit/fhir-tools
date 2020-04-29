package org.sitenv.spring.service;

import org.sitenv.spring.model.DafHealthcareService;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface HealthcareServiceService {
	
	public DafHealthcareService getHealthcareServiceById(String id);
	
	public DafHealthcareService getHealthcareServiceByVersionId(String theId, String versionId);

	public List<DafHealthcareService> search(SearchParameterMap paramMap);
	
	public List<DafHealthcareService> getHealthcareServiceHistoryById(String theId);
}
