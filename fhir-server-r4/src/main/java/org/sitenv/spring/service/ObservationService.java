package org.sitenv.spring.service;

import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ObservationService {
	
	public DafObservation getObservationById(int id);
	
	public DafObservation getObservationByVersionId(int theId, String versionId);
		
	public List<DafObservation> search(SearchParameterMap paramMap);
	
	public List<DafObservation> getObservationHistoryById(int theId);
}
