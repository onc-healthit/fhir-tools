package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.query.ProcedureSearchCriteria;

import java.util.Date;
import java.util.List;

public interface ProcedureDao {

    public List<DafProcedure> getAllProcedures();

    public DafProcedure getProcedureById(int id);

    public List<DafProcedure> getProcedureBySearchCriteria(ProcedureSearchCriteria procedureSearchCriteria);

	public List<DafProcedure> getProcedureForBulkData(List<Integer> patients, Date start);

}
