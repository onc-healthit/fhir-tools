package org.sitenv.spring.service;

import org.sitenv.spring.dao.AllergyIntoleranceDao;
import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("AllergyIntoleranceService")
@Transactional
public class AllergyIntoleranceServiceImpl implements AllergyIntoleranceService {
	
	@Autowired
    private AllergyIntoleranceDao allergyIntoleranceDao;
	
	@Override
    @Transactional
    public DafAllergyIntolerance getAllergyIntoleranceById(String id) {
        return this.allergyIntoleranceDao.getAllergyIntoleranceById(id);
    }
	
	@Override
	@Transactional
	public DafAllergyIntolerance getAllergyIntoleranceByVersionId(String theId, String versionId) {
		return this.allergyIntoleranceDao.getAllergyIntoleranceByVersionId(theId, versionId);
	}
	
	@Override
    @Transactional
    public List<DafAllergyIntolerance> search(SearchParameterMap paramMap){
        return this.allergyIntoleranceDao.search(paramMap);
    }

   @Override
   @Transactional
   public List<DafAllergyIntolerance> getAllergyIntoleranceHistoryById(String theId) {
	   return this.allergyIntoleranceDao.getAllergyIntoleranceHistoryById(theId);
   }
}
