package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface CareTeamDao {
	
	 public DafCareTeam getCareTeamById(String id);
	 
	 public DafCareTeam getCareTeamByVersionId(String theId, String versionId);

	 public List<DafCareTeam> search(SearchParameterMap paramMap);
	 
	 public List<DafCareTeam> getCareTeamHistoryById(String theId);
}
