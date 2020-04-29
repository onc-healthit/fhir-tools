package org.sitenv.spring.service;

import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface PractitionerService {
	
	public DafPractitioner getPractitionerById(String id);
	
	public DafPractitioner getPractitionerByVersionId(String theId, String versionId);
	
	public List<DafPractitioner> getPractitionerHistoryById(String theId);
	
	public List<DafPractitioner> search(SearchParameterMap paramMap);
}
