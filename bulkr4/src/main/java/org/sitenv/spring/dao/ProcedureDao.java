package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface ProcedureDao {
	
	 public DafProcedure getProcedureById(String id);
	 
	 public DafProcedure getProcedureByVersionId(String theId, String versionId);
	 
	 public List<DafProcedure> search(SearchParameterMap theMap);
	 
	 public List<DafProcedure> getProcedureHistoryById(String theId);

	public List<DafProcedure> getProcedureForBulkData(StringJoiner patient, Date start);
}
