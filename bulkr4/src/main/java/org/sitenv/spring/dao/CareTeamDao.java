package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface CareTeamDao {
	
	 public DafCareTeam getCareTeamById(String id);
	 
	 public DafCareTeam getCareTeamByVersionId(String theId, String versionId);

	 public List<DafCareTeam> search(SearchParameterMap paramMap);
	 
	 public List<DafCareTeam> getCareTeamHistoryById(String theId);

	public List<DafCareTeam> getCareTeamForBulkData(StringJoiner patient, Date start);
}
