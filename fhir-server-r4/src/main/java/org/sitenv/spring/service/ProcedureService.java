package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.util.SearchParameterMap;

public interface ProcedureService {
	
	public DafProcedure getProcedureById(int id);
	
	public DafProcedure getProcedureByVersionId(int theId, String versionId);
		
	public List<DafProcedure> search(SearchParameterMap paramMap);
	
	public List<DafProcedure> getProcedureHistoryById(int theId);
}
