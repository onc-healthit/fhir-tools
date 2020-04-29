package org.sitenv.spring.service;

import org.sitenv.spring.dao.PractitionerRoleDao;
import org.sitenv.spring.model.DafPractitionerRole;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("practitionerRoleService")
@Transactional
public class PractitionerRoleServiceImpl implements PractitionerRoleService {
	
	@Autowired
    private PractitionerRoleDao practitionerRoleDao;
	
	@Override
    @Transactional
    public DafPractitionerRole getPractitionerRoleById(String id) {
        return this.practitionerRoleDao.getPractitionerRoleById(id);
    }
	
	@Override
	@Transactional
	public DafPractitionerRole getPractitionerRoleByVersionId(String theId, String versionId) {
		return this.practitionerRoleDao.getPractitionerRoleByVersionId(theId, versionId);
	}
	
	@Override
	@Transactional
	public List<DafPractitionerRole> getPractitionerRoleHistoryById(String theId) {
		return this.practitionerRoleDao.getPractitionerRoleHistoryById(theId);
	}
	
	@Override
    @Transactional
    public List<DafPractitionerRole> search(SearchParameterMap paramMap){
        return this.practitionerRoleDao.search(paramMap);
    }
}
