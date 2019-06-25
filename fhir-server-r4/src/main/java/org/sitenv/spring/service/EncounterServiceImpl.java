package org.sitenv.spring.service;

import org.sitenv.spring.dao.EncounterDao;
import org.sitenv.spring.model.DafEncounter;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("EncounterService")
@Transactional
public class EncounterServiceImpl implements EncounterService {
	
	@Autowired
    private EncounterDao encounterDao;
	
	@Override
    @Transactional
    public DafEncounter getEncounterById(int id) {
        return this.encounterDao.getEncounterById(id);
    }
	
	@Override
	@Transactional
	public DafEncounter getEncounterByVersionId(int theId, String versionId) {
		return this.encounterDao.getEncounterByVersionId(theId, versionId);
	}
	
	@Override
    @Transactional
    public List<DafEncounter> search(SearchParameterMap paramMap){
        return this.encounterDao.search(paramMap);
    }

   @Override
   @Transactional
   public List<DafEncounter> getEncounterHistoryById(int theId) {
	   return this.encounterDao.getEncounterHistoryById(theId);
   }
}
