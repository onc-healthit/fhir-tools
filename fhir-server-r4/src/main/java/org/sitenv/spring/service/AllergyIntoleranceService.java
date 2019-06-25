package org.sitenv.spring.service;

import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface AllergyIntoleranceService {
	
	public DafAllergyIntolerance getAllergyIntoleranceById(int id);
	
	public DafAllergyIntolerance getAllergyIntoleranceByVersionId(int theId, String versionId);
		
	public List<DafAllergyIntolerance> search(SearchParameterMap paramMap);
	
	public List<DafAllergyIntolerance> getAllergyIntoleranceHistoryById(int theId);
}
