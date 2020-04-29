package org.sitenv.spring.service;

import org.sitenv.spring.dao.ProcedureDao;
import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("procedureService")
@Transactional
public class ProcedureServiceImpl implements ProcedureService {
	
	@Autowired
    private ProcedureDao procedureDao;
	
	@Override
    @Transactional
    public DafProcedure getProcedureById(String id) {
        return this.procedureDao.getProcedureById(id);
    }
	
	@Override
	@Transactional
	public DafProcedure getProcedureByVersionId(String theId, String versionId) {
		return this.procedureDao.getProcedureByVersionId(theId, versionId);
	}
	
	@Override
    @Transactional
    public List<DafProcedure> search(SearchParameterMap paramMap){
        return this.procedureDao.search(paramMap);
    }

   @Override
   @Transactional
   public List<DafProcedure> getProcedureHistoryById(String theId) {
	   return this.procedureDao.getProcedureHistoryById(theId);
   }
}
