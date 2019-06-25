package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface ObservationDao {
	
	 public DafObservation getObservationById(int id);
	 
	 public DafObservation getObservationByVersionId(int theId, String versionId);
	 
	 public List<DafObservation> search(SearchParameterMap theMap);
	 
	 public List<DafObservation> getObservationHistoryById(int theId);
}
