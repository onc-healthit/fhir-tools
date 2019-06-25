package org.sitenv.spring.service;

import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface LocationService {

	public DafLocation getLocationById(int id);

	public DafLocation getLocationByVersionId(int theId, String versionId);

	public List<DafLocation> getLocationHistoryById(int theId);

	public List<DafLocation> search(SearchParameterMap searchParameterMap);

}
