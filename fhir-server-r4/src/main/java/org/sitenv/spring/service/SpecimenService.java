package org.sitenv.spring.service;

import org.sitenv.spring.model.DafSpecimen;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface SpecimenService {
	
	public DafSpecimen getSpecimenById(String id);
	
	public DafSpecimen getSpecimenByVersionId(String theId, String versionId);
	
	public List<DafSpecimen> getSpecimenHistoryById(String theId);
	
	public List<DafSpecimen> search(SearchParameterMap paramMap);
}
