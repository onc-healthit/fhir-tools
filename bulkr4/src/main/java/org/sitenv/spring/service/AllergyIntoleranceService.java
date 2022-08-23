package org.sitenv.spring.service;

import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface AllergyIntoleranceService {
	
	public DafAllergyIntolerance getAllergyIntoleranceById(String id);
	
	public DafAllergyIntolerance getAllergyIntoleranceByVersionId(String theId, String versionId);
		
	public List<DafAllergyIntolerance> search(SearchParameterMap paramMap);
	
	public List<DafAllergyIntolerance> getAllergyIntoleranceHistoryById(String theId);
	
	public List<DafAllergyIntolerance> getAllergyIntoleranceForBulkData(StringJoiner patients, Date start);
}
