package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface LocationDao {

	public DafLocation getLocationById(String id);

	public DafLocation getLocationByVersionId(String theId, String versionId);

	public List<DafLocation> getLocationHistoryById(String theId);

	public List<DafLocation> search(SearchParameterMap searchParameterMap);
}
