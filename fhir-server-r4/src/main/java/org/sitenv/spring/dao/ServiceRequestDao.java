package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafServiceRequest;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface  ServiceRequestDao {
	
	public DafServiceRequest getServiceRequestById(int id);

	public DafServiceRequest getServiceRequestByVersionId(int theId, String versionId);

	public List<DafServiceRequest> getServiceRequestHistoryById(int theId);

	public List<DafServiceRequest> search(SearchParameterMap theMap);
}


