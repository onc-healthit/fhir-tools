package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafSpecimen;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface SpecimenDao {
	public DafSpecimen getSpecimenById(String id);
	
	public DafSpecimen getSpecimenByVersionId(String theId, String versionId);
	
	public List<DafSpecimen> getSpecimenHistoryById(String theId);
	
	public List<DafSpecimen> search(SearchParameterMap paramMap);

	public List<DafSpecimen> getSpecimenRequestDataRequest(StringJoiner patient, Date start);
}
