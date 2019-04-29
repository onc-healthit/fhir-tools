package org.sitenv.spring.service;

import java.util.List;
import org.sitenv.spring.dao.OrganizationDao;
import org.sitenv.spring.model.DafOrganization;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("organizationService")
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

	@Autowired
    private OrganizationDao organizationDao;
	
	@Override
    @Transactional
    public DafOrganization getOrganizationById(int id) {
    return this.organizationDao.getOrganizationById(id);
    }

	@Override
	@Transactional
	public DafOrganization getOrganizationByVersionId(int theId, String versionId) {
    return this.organizationDao.getOrganizationByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafOrganization> getOrganizationHistoryById(int theId) {
	return this.organizationDao.getOrganizationHistoryById(theId);
	}

	@Override
	@Transactional
	public List<DafOrganization> search(SearchParameterMap theMap) {
	return this.organizationDao.search(theMap);
	}

	
	
}
