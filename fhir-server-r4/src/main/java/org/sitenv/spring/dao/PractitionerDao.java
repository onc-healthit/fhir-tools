package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface PractitionerDao {
	
	public DafPractitioner getPractitionerById(int id);
	
	public DafPractitioner getPractitionerByVersionId(int theId, String versionId);
	
	public List<DafPractitioner> getPractitionerHistoryById(int theId);
	
	public List<DafPractitioner> search(SearchParameterMap theMap);
}
