package org.sitenv.spring.service;

import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ObservationService {
	
	public DafObservation getObservationById(String id);
	
	public DafObservation getObservationByVersionId(String theId, String versionId);
		
	public List<DafObservation> search(SearchParameterMap paramMap);
	
	public List<DafObservation> getObservationHistoryById(String theId);
}
