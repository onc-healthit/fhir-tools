package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafPatient;
import org.sitenv.spring.model.PatientList;
import org.sitenv.spring.util.SearchParameterMap;

public interface PatientDao {
	
	 public DafPatient getPatientById(int id);
	 
	 public DafPatient getPatientByVersionId(int theId, String versionId);
	 
	 public List<DafPatient> search(SearchParameterMap theMap);
	 
	 public List<DafPatient> getPatientHistoryById(int theId);
	 
	 public List<PatientList> getPatientsOnAuthorize();
}
