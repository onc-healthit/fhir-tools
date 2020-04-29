package org.sitenv.spring.service;

import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ImmunizationService {
	
	public DafImmunization getImmunizationById(String id);
	
	public DafImmunization getImmunizationByVersionId(String theId, String versionId);
		
	public List<DafImmunization> search(SearchParameterMap paramMap);
	
	public List<DafImmunization> getImmunizationHistoryById(String theId);
}
