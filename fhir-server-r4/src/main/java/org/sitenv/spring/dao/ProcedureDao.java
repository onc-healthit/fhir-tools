package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ProcedureDao {
	
	 public DafProcedure getProcedureById(String id);
	 
	 public DafProcedure getProcedureByVersionId(String theId, String versionId);
	 
	 public List<DafProcedure> search(SearchParameterMap theMap);
	 
	 public List<DafProcedure> getProcedureHistoryById(String theId);
}
