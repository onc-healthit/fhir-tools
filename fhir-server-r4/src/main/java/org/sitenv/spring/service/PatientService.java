package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafPatient;
import org.sitenv.spring.model.PatientList;
import org.sitenv.spring.util.SearchParameterMap;

public interface PatientService {
	
	public DafPatient getPatientById(int id);
	
	public DafPatient getPatientByVersionId(int theId, String versionId);
		
	public List<DafPatient> search(SearchParameterMap paramMap);
	
	public List<DafPatient> getPatientHistoryById(int theId);
	 
	 public List<DafPatient> getPatientsOnAuthorize();
	
}
