package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafServiceRequest;
import org.sitenv.spring.util.SearchParameterMap;

public interface  ServiceRequestDao {
	
	public DafServiceRequest getServiceRequestById(String id);

	public DafServiceRequest getServiceRequestByVersionId(String theId, String versionId);

	public List<DafServiceRequest> getServiceRequestHistoryById(String theId);

	public List<DafServiceRequest> search(SearchParameterMap theMap);
}


