package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.util.SearchParameterMap;

public interface ImmunizationDao {
	
	 public DafImmunization getImmunizationById(int id);
	 
	 public DafImmunization getImmunizationByVersionId(int theId, String versionId);
	 
	 public List<DafImmunization> search(SearchParameterMap theMap);
	 
	 public List<DafImmunization> getImmunizationHistoryById(int theId);
}
