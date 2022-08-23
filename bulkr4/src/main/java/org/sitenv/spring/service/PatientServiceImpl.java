package org.sitenv.spring.service;

import ca.uhn.fhir.context.FhirContext;
import org.sitenv.spring.dao.PatientDao;
import org.sitenv.spring.model.DafPatient;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@Service("patientService")
@Transactional
public class PatientServiceImpl implements PatientService {
	public static final String RESOURCE_TYPE = "Patient";
	
	@Autowired
    private PatientDao patientDao;
	
	@Autowired
	FhirContext fhirContext;
	
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

	@Override
	@Transactional
    public List<DafPatient> getPatientJsonForBulkData(StringJoiner patients, Date start) {
//    	Patient patient = null;
//		List<Patient> patientList = new ArrayList<>();
//		List<DafPatient> dafPatientList = new ArrayList<>();
//
	return this. patientDao.getAllPatientJsonForBulkData(patients, start);
	
//		List<DafPatient> patientList = new ArrayList<>();
//		if (patients != null) {
//			for (String patient : patients) {
//				List<DafPatient> DafPatientList = patientDao.getAllPatientJsonForBulkData(patient, start);
//				patientList.addAll(DafPatientList);
//			}
//
//		}
//		return patientList;
	}
    
}
