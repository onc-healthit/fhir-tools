package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

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
	public List<DafAllergyIntolerance> search(SearchParameterMap paramMap) {
		return this.allergyIntoleranceDao.search(paramMap);
	}

	@Override
	@Transactional
	public List<DafAllergyIntolerance> getAllergyIntoleranceHistoryById(String theId) {
		return this.allergyIntoleranceDao.getAllergyIntoleranceHistoryById(theId);
	}

	@Override
	@Transactional
	public List<DafAllergyIntolerance> getAllergyIntoleranceForBulkData(StringJoiner patients, Date start) {
		return this.allergyIntoleranceDao.getAllergyIntoleranceForBulkData(patients, start);
	}
}
