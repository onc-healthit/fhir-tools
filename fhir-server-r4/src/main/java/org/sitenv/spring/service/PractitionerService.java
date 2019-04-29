package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.util.SearchParameterMap;

public interface PractitionerService {
	
	public DafPractitioner getPractitionerById(int id);
	
	public DafPractitioner getPractitionerByVersionId(int theId, String versionId);
	
	public List<DafPractitioner> getPractitionerHistoryById(int theId);
	
	public List<DafPractitioner> search(SearchParameterMap paramMap);
}
