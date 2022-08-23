package org.sitenv.spring.service;

import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface ProcedureService {
	
	public DafProcedure getProcedureById(String id);
	
	public DafProcedure getProcedureByVersionId(String theId, String versionId);
		
	public List<DafProcedure> search(SearchParameterMap paramMap);
	
	public List<DafProcedure> getProcedureHistoryById(String theId);

	public List<DafProcedure> getProcedureForBulkData(StringJoiner patients, Date start);
}
