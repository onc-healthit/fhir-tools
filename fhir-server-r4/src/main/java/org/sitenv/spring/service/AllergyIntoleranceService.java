package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.util.SearchParameterMap;

public interface AllergyIntoleranceService {
	
	public DafAllergyIntolerance getAllergyIntoleranceById(int id);
	
	public DafAllergyIntolerance getAllergyIntoleranceByVersionId(int theId, String versionId);
		
	public List<DafAllergyIntolerance> search(SearchParameterMap paramMap);
	
	public List<DafAllergyIntolerance> getAllergyIntoleranceHistoryById(int theId);
}
