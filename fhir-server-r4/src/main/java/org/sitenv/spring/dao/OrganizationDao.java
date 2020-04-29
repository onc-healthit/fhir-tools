package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafOrganization;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface OrganizationDao {
	
	public DafOrganization getOrganizationById(String id);
	
	public DafOrganization getOrganizationByVersionId(String theId, String versionId);
	
	public List<DafOrganization> getOrganizationHistoryById(String theId);
	
	public List<DafOrganization> search(SearchParameterMap theMap);
}
