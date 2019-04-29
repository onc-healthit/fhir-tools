package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.dao.AllergyIntoleranceDao;
import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("AllergyIntoleranceService")
@Transactional
public class AllergyIntoleranceServiceImpl implements AllergyIntoleranceService {
	
	@Autowired
    private AllergyIntoleranceDao allergyIntoleranceDao;
	
	@Override
    @Transactional
    public DafAllergyIntolerance getAllergyIntoleranceById(int id) {
        return this.allergyIntoleranceDao.getAllergyIntoleranceById(id);
    }
	
	@Override
	@Transactional
	public DafAllergyIntolerance getAllergyIntoleranceByVersionId(int theId, String versionId) {
		return this.allergyIntoleranceDao.getAllergyIntoleranceByVersionId(theId, versionId);
	}
	
	@Override
    @Transactional
    public List<DafAllergyIntolerance> search(SearchParameterMap paramMap){
        return this.allergyIntoleranceDao.search(paramMap);
    }

   @Override
   @Transactional
   public List<DafAllergyIntolerance> getAllergyIntoleranceHistoryById(int theId) {
	   return this.allergyIntoleranceDao.getAllergyIntoleranceHistoryById(theId);
   }
}
