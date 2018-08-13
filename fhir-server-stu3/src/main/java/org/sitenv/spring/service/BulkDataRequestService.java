package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafBulkDataRequest;

public interface BulkDataRequestService {
	
	public DafBulkDataRequest saveBulkDataRequest(DafBulkDataRequest bdr);
	
	public DafBulkDataRequest getBulkDataRequestById(Integer id);
	
	public List<DafBulkDataRequest> getBulkDataRequestsByProcessedFlag(Boolean flag);
	
	public Integer deleteRequestById(Integer id);

}
