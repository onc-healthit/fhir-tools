package org.sitenv.spring.service;

import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ProcedureService {
	
	public DafProcedure getProcedureById(String id);
	
	public DafProcedure getProcedureByVersionId(String theId, String versionId);
		
	public List<DafProcedure> search(SearchParameterMap paramMap);
	
	public List<DafProcedure> getProcedureHistoryById(String theId);
}
