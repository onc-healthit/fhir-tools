package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafPatient;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface PatientDao {
	
	 public DafPatient getPatientById(String id);
	 
	 public DafPatient getPatientByVersionId(String theId, String versionId);
	 
	 public List<DafPatient> search(SearchParameterMap theMap);
	 
	 public List<DafPatient> getPatientHistoryById(String theId);
	 
	 public List<DafPatient> getPatientsOnAuthorize();
}
