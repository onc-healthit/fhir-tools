package org.sitenv.spring.service;

import org.sitenv.spring.model.DafPractitionerRole;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface PractitionerRoleService {
	
	public DafPractitionerRole getPractitionerRoleById(String id);
	
	public DafPractitionerRole getPractitionerRoleByVersionId(String theId, String versionId);
	
	public List<DafPractitionerRole> getPractitionerRoleHistoryById(String theId);
	
	public List<DafPractitionerRole> search(SearchParameterMap paramMap);
	
	public List<DafPractitionerRole> getPractitionerRoleForBulkData(StringJoiner patients, Date start);
}