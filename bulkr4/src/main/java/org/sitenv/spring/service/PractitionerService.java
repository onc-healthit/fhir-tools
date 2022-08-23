package org.sitenv.spring.service;

import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface PractitionerService {
	
	public DafPractitioner getPractitionerById(String id);
	
	public DafPractitioner getPractitionerByVersionId(String theId, String versionId);
	
	public List<DafPractitioner> getPractitionerHistoryById(String theId);
	
	public List<DafPractitioner> search(SearchParameterMap paramMap);
	
	public List<DafPractitioner> getPractitionerForBulkData(StringJoiner patients, Date start);
	
}
