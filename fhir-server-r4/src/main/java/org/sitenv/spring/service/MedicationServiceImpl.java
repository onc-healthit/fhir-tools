package org.sitenv.spring.service;

import org.sitenv.spring.dao.MedicationDao;
import org.sitenv.spring.model.DafMedication;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("medicationService")
@Transactional
public class MedicationServiceImpl implements MedicationService {
	
	@Autowired
    private MedicationDao medicationDao;
	
	@Override
    @Transactional
    public DafMedication getMedicationById(String id) {
        return this.medicationDao.getMedicationById(id);
    }
	
	@Override
	@Transactional
	public DafMedication getMedicationByVersionId(String theId, String versionId) {
		return this.medicationDao.getMedicationByVersionId(theId, versionId);
	}
	
	@Override
    @Transactional
    public List<DafMedication> search(SearchParameterMap paramMap){
        return this.medicationDao.search(paramMap);
    }

   @Override
   @Transactional
   public List<DafMedication> getMedicationHistoryById(String theId) {
	   return this.medicationDao.getMedicationHistoryById(theId);
   }

    @Override
    @Transactional
    public List<DafMedication> getMedicationByResourceId(List<String> resourceID) {
        return this.medicationDao.getMedicationByResourceId(resourceID);
    }

}
