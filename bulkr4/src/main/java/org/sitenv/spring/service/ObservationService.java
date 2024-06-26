package org.sitenv.spring.service;

import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public interface ObservationService {
	
	public DafObservation getObservationById(String id);
	
	public DafObservation getObservationByVersionId(String theId, String versionId);
		
	public List<DafObservation> search(SearchParameterMap paramMap);
	
	public List<DafObservation> getObservationHistoryById(String theId);

	public List<DafObservation> getObservationForBulkData(StringJoiner patients, Date start);
}
