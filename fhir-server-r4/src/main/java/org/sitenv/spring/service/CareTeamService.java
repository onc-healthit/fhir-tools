package org.sitenv.spring.service;

import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface CareTeamService {
	
	public DafCareTeam getCareTeamById(int id);
	
	public DafCareTeam getCareTeamByVersionId(int theId, String versionId);
	
	public List<DafCareTeam> search(SearchParameterMap paramMap);
	
	public List<DafCareTeam> getCareTeamHistoryById(int theId);
}
