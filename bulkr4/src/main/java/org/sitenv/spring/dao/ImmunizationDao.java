package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface ImmunizationDao {
	
	 public DafImmunization getImmunizationById(String id);
	 
	 public DafImmunization getImmunizationByVersionId(String theId, String versionId);
	 
	 public List<DafImmunization> search(SearchParameterMap theMap);
	 
	 public List<DafImmunization> getImmunizationHistoryById(String theId);

	public List<DafImmunization> getImmunizationForBulkData(StringJoiner patient, Date start);
}
