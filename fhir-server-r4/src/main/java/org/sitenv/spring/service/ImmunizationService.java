package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.util.SearchParameterMap;

public interface ImmunizationService {
	
	public DafImmunization getImmunizationById(int id);
	
	public DafImmunization getImmunizationByVersionId(int theId, String versionId);
		
	public List<DafImmunization> search(SearchParameterMap paramMap);
	
	public List<DafImmunization> getImmunizationHistoryById(int theId);
}