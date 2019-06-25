package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafOrganization;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface OrganizationDao {
	
	public DafOrganization getOrganizationById(int id);
	
	public DafOrganization getOrganizationByVersionId(int theId, String versionId);
	
	public List<DafOrganization> getOrganizationHistoryById(int theId);
	
	public List<DafOrganization> search(SearchParameterMap theMap);
}
