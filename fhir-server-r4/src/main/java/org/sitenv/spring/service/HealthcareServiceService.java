package org.sitenv.spring.service;

import org.sitenv.spring.model.DafHealthcareService;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface HealthcareServiceService {
	
	public DafHealthcareService getHealthcareServiceById(int id);
	
	public DafHealthcareService getHealthcareServiceByVersionId(int theId, String versionId);

	public List<DafHealthcareService> search(SearchParameterMap paramMap);
	
	public List<DafHealthcareService> getHealthcareServiceHistoryById(int theId);
}
