package org.sitenv.spring.service;

import org.sitenv.spring.dao.OrganizationDao;
import org.sitenv.spring.model.DafOrganization;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("organizationService")
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

	@Autowired
    private OrganizationDao organizationDao;
	
	@Override
    @Transactional
    public DafOrganization getOrganizationById(String id) {
    return this.organizationDao.getOrganizationById(id);
    }

	@Override
	@Transactional
	public DafOrganization getOrganizationByVersionId(String theId, String versionId) {
    return this.organizationDao.getOrganizationByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafOrganization> getOrganizationHistoryById(String theId) {
	return this.organizationDao.getOrganizationHistoryById(theId);
	}

	@Override
	@Transactional
	public List<DafOrganization> search(SearchParameterMap theMap) {
	return this.organizationDao.search(theMap);
	}

	
	
}
