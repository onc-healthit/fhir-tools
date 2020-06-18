package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.model.DafOrganization;
import org.sitenv.spring.util.SearchParameterMap;

public interface OrganizationService {

	public DafOrganization getOrganizationById(String id);

	public DafOrganization getOrganizationByVersionId(String theId, String versionId);

	public List<DafOrganization> getOrganizationHistoryById(String id);
	
	public List<DafOrganization> search(SearchParameterMap theMap);

	public List<DafOrganization> getOrganizationForBulkData(StringJoiner patients, Date start);

	
}
