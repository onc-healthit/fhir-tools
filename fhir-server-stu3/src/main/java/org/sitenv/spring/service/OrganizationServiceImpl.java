package org.sitenv.spring.service;

import org.sitenv.spring.dao.OrganizationDao;
import org.sitenv.spring.model.DafOrganization;
import org.sitenv.spring.query.OrganizationSearchCriteria;
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
    public List<DafOrganization> getAllOrganizations() {
        return this.organizationDao.getAllOrganizations();
    }

    @Override
    @Transactional
    public DafOrganization getOrganizationResourceById(int id) {
        return this.organizationDao.getOrganizationResourceById(id);
    }

    @Override
    @Transactional
    public List<DafOrganization> getOrganizationBySearchCriteria(OrganizationSearchCriteria criteria) {
        return this.organizationDao.getOrganizationBySearchCriteria(criteria);
    }

}
