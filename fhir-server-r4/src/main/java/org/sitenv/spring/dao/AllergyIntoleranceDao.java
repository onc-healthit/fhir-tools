package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.util.SearchParameterMap;

public interface AllergyIntoleranceDao {
	
	 public DafAllergyIntolerance getAllergyIntoleranceById(int id);
	 
	 public DafAllergyIntolerance getAllergyIntoleranceByVersionId(int theId, String versionId);
	 
	 public List<DafAllergyIntolerance> search(SearchParameterMap theMap);
	 
	 public List<DafAllergyIntolerance> getAllergyIntoleranceHistoryById(int theId);
}
