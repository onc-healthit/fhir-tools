package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.dao.PractitionerDao;
import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("practitionerService")
@Transactional
public class PractitionerServiceImpl implements PractitionerService {
	
	@Autowired
    private PractitionerDao practitionerDao;
	
	@Override
    @Transactional
    public DafPractitioner getPractitionerById(int id) {
        return this.practitionerDao.getPractitionerById(id);
    }
	
	@Override
	@Transactional
	public DafPractitioner getPractitionerByVersionId(int theId, String versionId) {
		return this.practitionerDao.getPractitionerByVersionId(theId, versionId);
	}
	
	@Override
	@Transactional
	public List<DafPractitioner> getPractitionerHistoryById(int theId) {
		return this.practitionerDao.getPractitionerHistoryById(theId);
	}
	
	@Override
    @Transactional
    public List<DafPractitioner> search(SearchParameterMap paramMap){
        return this.practitionerDao.search(paramMap);
    }
}
