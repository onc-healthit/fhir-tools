package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ImmunizationDao {
	
	 public DafImmunization getImmunizationById(int id);
	 
	 public DafImmunization getImmunizationByVersionId(int theId, String versionId);
	 
	 public List<DafImmunization> search(SearchParameterMap theMap);
	 
	 public List<DafImmunization> getImmunizationHistoryById(int theId);
}
