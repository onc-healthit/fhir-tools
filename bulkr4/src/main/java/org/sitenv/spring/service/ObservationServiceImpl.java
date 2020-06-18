package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.dao.ObservationDao;
import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Override
	public List<DafObservation> getObservationForBulkData(StringJoiner patients, Date start) {
		return this.observationDao.getObservationForBulkData(patients, start);

	}
}
