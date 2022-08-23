package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafPatient;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface PatientDao {
	
	 public DafPatient getPatientById(String id);
	 
	 public DafPatient getPatientByVersionId(String theId, String versionId);
	 
	 public List<DafPatient> search(SearchParameterMap theMap);
	 
	 public List<DafPatient> getPatientHistoryById(String theId);
	 
	 public List<DafPatient> getPatientsOnAuthorize();
	 
	 public List<DafPatient> getAllPatientJsonForBulkData(StringJoiner patients, Date start);
}
