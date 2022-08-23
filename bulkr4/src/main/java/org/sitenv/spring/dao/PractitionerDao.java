package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface PractitionerDao {
	
	public DafPractitioner getPractitionerById(String id);
	
	public DafPractitioner getPractitionerByVersionId(String theId, String versionId);
	
	public List<DafPractitioner> getPractitionerHistoryById(String theId);
	
	public List<DafPractitioner> search(SearchParameterMap theMap);

	public List<DafPractitioner> getPractitionerForBulkData(StringJoiner patient, Date start);
}
