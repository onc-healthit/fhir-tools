package org.sitenv.spring.dao;

import java.util.Date;
import java.util.List;

import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.query.ProcedureSearchCriteria;

public interface ProcedureDao {
	
	public List<DafProcedure> getAllProcedures();

    public DafProcedure getProcedureById(int id);

    public List<DafProcedure> getProcedureBySearchCriteria(ProcedureSearchCriteria procedureSearchCriteria);

    public List<DafProcedure> getProcedureForBulkData(List<Integer> patients, Date start);
}
