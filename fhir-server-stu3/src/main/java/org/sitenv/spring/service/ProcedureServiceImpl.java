package org.sitenv.spring.service;

import org.sitenv.spring.dao.ProcedureDao;
import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.query.ProcedureSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("procedureResourceService")
@Transactional
public class ProcedureServiceImpl implements ProcedureService {

    @Autowired
    private ProcedureDao dao;

    @Override
    public List<DafProcedure> getAllProcedures() {

        return dao.getAllProcedures();
    }

    @Override
    public DafProcedure getProcedureById(int id) {

        return dao.getProcedureById(id);
    }

    @Override
    public List<DafProcedure> getProcedureBySearchCriteria(
            ProcedureSearchCriteria procedureSearchCriteria) {

        return dao.getProcedureBySearchCriteria(procedureSearchCriteria);
    }

}
