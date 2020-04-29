package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.model.DafPractitionerRole;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface PractitionerRoleDao {
	
	public DafPractitionerRole getPractitionerRoleById(String id);
	
	public DafPractitionerRole getPractitionerRoleByVersionId(String theId, String versionId);
	
	public List<DafPractitionerRole> getPractitionerRoleHistoryById(String theId);
	
	public List<DafPractitionerRole> search(SearchParameterMap theMap);
}
