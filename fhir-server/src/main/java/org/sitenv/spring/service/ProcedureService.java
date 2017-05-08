package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.query.ProcedureSearchCriteria;

public interface ProcedureService{
	
	public List<DafProcedure> getAllProcedures();

    public DafProcedure getProcedureById(int id);

    public List<DafProcedure> getProcedureBySearchCriteria(ProcedureSearchCriteria procedureSearchCriteria);

}
