package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafSpecimen;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface SpecimenDao {
	public DafSpecimen getSpecimenById(int id);
	
	public DafSpecimen getSpecimenByVersionId(int theId, String versionId);
	
	public List<DafSpecimen> getSpecimenHistoryById(int theId);
	
	public List<DafSpecimen> search(SearchParameterMap paramMap);
}
