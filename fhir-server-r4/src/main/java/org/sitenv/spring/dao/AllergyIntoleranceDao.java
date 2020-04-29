package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface AllergyIntoleranceDao {
	
	 public DafAllergyIntolerance getAllergyIntoleranceById(String id);
	 
	 public DafAllergyIntolerance getAllergyIntoleranceByVersionId(String theId, String versionId);
	 
	 public List<DafAllergyIntolerance> search(SearchParameterMap theMap);
	 
	 public List<DafAllergyIntolerance> getAllergyIntoleranceHistoryById(String theId);
}
