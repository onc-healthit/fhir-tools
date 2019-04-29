package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafSpecimen;
import org.sitenv.spring.util.SearchParameterMap;

public interface SpecimenService {
	
	public DafSpecimen getSpecimenById(int id);
	
	public DafSpecimen getSpecimenByVersionId(int theId, String versionId);
	
	public List<DafSpecimen> getSpecimenHistoryById(int theId);
	
	public List<DafSpecimen> search(SearchParameterMap paramMap);
}
