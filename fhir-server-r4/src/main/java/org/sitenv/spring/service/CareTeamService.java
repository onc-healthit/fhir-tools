package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.util.SearchParameterMap;

public interface CareTeamService {
	
	public DafCareTeam getCareTeamById(int id);
	
	public DafCareTeam getCareTeamByVersionId(int theId, String versionId);
	
	public List<DafCareTeam> search(SearchParameterMap paramMap);
	
	public List<DafCareTeam> getCareTeamHistoryById(int theId);
}
