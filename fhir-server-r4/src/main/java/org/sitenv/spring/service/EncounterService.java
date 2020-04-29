package org.sitenv.spring.service;

import org.sitenv.spring.model.DafEncounter;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface EncounterService {
	
	public DafEncounter getEncounterById(String id);
	
	public DafEncounter getEncounterByVersionId(String theId, String versionId);
		
	public List<DafEncounter> search(SearchParameterMap paramMap);
	
	public List<DafEncounter> getEncounterHistoryById(String theId);
}
