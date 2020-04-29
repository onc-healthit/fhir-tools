package org.sitenv.spring.service;

import org.sitenv.spring.dao.ObservationDao;
import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("observationService")
@Transactional
public class ObservationServiceImpl implements ObservationService {
	
	@Autowired
    private ObservationDao observationDao;
	
	@Override
    @Transactional
    public DafObservation getObservationById(String id) {
        return this.observationDao.getObservationById(id);
    }
	
	@Override
	@Transactional
	public DafObservation getObservationByVersionId(String theId, String versionId) {
		return this.observationDao.getObservationByVersionId(theId, versionId);
	}
	
	@Override
    @Transactional
    public List<DafObservation> search(SearchParameterMap paramMap){
        return this.observationDao.search(paramMap);
    }

   @Override
   @Transactional
   public List<DafObservation> getObservationHistoryById(String theId) {
	   return this.observationDao.getObservationHistoryById(theId);
   }
}
