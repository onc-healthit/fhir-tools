package org.sitenv.spring.service;

import org.sitenv.spring.dao.PatientDao;
import org.sitenv.spring.model.DafPatient;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("patientService")
@Transactional
public class PatientServiceImpl implements PatientService {
	
	@Autowired
    private PatientDao patientDao;
	
	@Override
    @Transactional
    public DafPatient getPatientById(String id) {
        return this.patientDao.getPatientById(id);
    }
	
	@Override
	@Transactional
	public DafPatient getPatientByVersionId(String theId, String versionId) {
		return this.patientDao.getPatientByVersionId(theId, versionId);
	}
	
	@Override
    @Transactional
    public List<DafPatient> search(SearchParameterMap paramMap){
        return this.patientDao.search(paramMap);
    }

   @Override
   @Transactional
   public List<DafPatient> getPatientHistoryById(String theId) {
	   return this.patientDao.getPatientHistoryById(theId);
   }

	@Override
	@Transactional
	public List<DafPatient> getPatientsOnAuthorize() {
		return this.patientDao.getPatientsOnAuthorize();
	}
}
