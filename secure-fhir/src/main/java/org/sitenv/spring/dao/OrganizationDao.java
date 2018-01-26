package org.sitenv.spring.dao;

import java.util.Date;
import java.util.List;

import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.model.DafOrganization;
import org.sitenv.spring.query.OrganizationSearchCriteria;

public interface OrganizationDao {
	
	public List<DafOrganization> getAllOrganizations();

    public DafOrganization getOrganizationResourceById(int id);
    
    public List<DafOrganization> getOrganizationBySearchCriteria(OrganizationSearchCriteria organizationSearchCriteria);

    public List<DafOrganization> getOrganizationForBulkData(List<Integer> patients, Date start);
}
