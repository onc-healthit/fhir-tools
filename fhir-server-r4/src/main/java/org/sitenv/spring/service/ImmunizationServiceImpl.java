package org.sitenv.spring.service;

import org.sitenv.spring.dao.ImmunizationDao;
import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("immunizationService")
@Transactional
public class ImmunizationServiceImpl implements ImmunizationService {
	
	@Autowired
    private ImmunizationDao immunizationDao;
	
	@Override
    @Transactional
    public DafImmunization getImmunizationById(String id) {
        return this.immunizationDao.getImmunizationById(id);
    }
	
	@Override
	@Transactional
	public DafImmunization getImmunizationByVersionId(String theId, String versionId) {
		return this.immunizationDao.getImmunizationByVersionId(theId, versionId);
	}
	
	@Override
    @Transactional
    public List<DafImmunization> search(SearchParameterMap paramMap){
        return this.immunizationDao.search(paramMap);
    }

   @Override
   @Transactional
   public List<DafImmunization> getImmunizationHistoryById(String theId) {
	   return this.immunizationDao.getImmunizationHistoryById(theId);
   }
}
