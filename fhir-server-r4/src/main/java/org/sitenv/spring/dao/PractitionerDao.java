package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.util.SearchParameterMap;

public interface PractitionerDao {
	
	public DafPractitioner getPractitionerById(int id);
	
	public DafPractitioner getPractitionerByVersionId(int theId, String versionId);
	
	public List<DafPractitioner> getPractitionerHistoryById(int theId);
	
	public List<DafPractitioner> search(SearchParameterMap theMap);
}
