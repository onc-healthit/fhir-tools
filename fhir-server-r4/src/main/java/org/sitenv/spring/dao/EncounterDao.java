package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafEncounter;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface EncounterDao {
	
	 public DafEncounter getEncounterById(int id);
	 
	 public DafEncounter getEncounterByVersionId(int theId, String versionId);
	 
	 public List<DafEncounter> search(SearchParameterMap theMap);
	 
	 public List<DafEncounter> getEncounterHistoryById(int theId);
}
