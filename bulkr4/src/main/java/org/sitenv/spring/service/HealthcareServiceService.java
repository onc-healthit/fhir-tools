package org.sitenv.spring.service;

import org.sitenv.spring.model.DafHealthcareService;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface HealthcareServiceService {
	
	public DafHealthcareService getHealthcareServiceById(String id);
	
	public DafHealthcareService getHealthcareServiceByVersionId(String theId, String versionId);

	public List<DafHealthcareService> search(SearchParameterMap paramMap);
	
	public List<DafHealthcareService> getHealthcareServiceHistoryById(String theId);

	public List<DafHealthcareService> getHealthcareServiceForBulkData(StringJoiner patients, Date start);
}
