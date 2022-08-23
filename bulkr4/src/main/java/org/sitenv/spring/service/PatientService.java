package org.sitenv.spring.service;

import org.sitenv.spring.model.DafPatient;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface PatientService {
	
	public DafPatient getPatientById(String id);
	
	public DafPatient getPatientByVersionId(String theId, String versionId);
		
	public List<DafPatient> search(SearchParameterMap paramMap);
	
	public List<DafPatient> getPatientHistoryById(String theId);
	 
	 public List<DafPatient> getPatientsOnAuthorize();
	 
	 List<DafPatient> getPatientJsonForBulkData(StringJoiner patients, Date start);
	
}
