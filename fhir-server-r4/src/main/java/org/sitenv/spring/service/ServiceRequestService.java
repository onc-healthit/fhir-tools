package org.sitenv.spring.service;

import java.util.List;
import org.sitenv.spring.model.DafServiceRequest;
import org.sitenv.spring.util.SearchParameterMap;

public interface ServiceRequestService {

	public DafServiceRequest getServiceRequestById(int id);

	public DafServiceRequest getServiceRequestByVersionId(int theId, String versionId);

	public List<DafServiceRequest> getServiceRequestHistoryById(int theId);

	public List<DafServiceRequest> search(SearchParameterMap theMap);
}
