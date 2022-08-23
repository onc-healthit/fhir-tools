package org.sitenv.spring.service;

import org.sitenv.spring.model.DafBulkDataRequest;

import java.util.List;

public interface BulkDataRequestService {
	
	public DafBulkDataRequest saveBulkDataRequest(DafBulkDataRequest bdr);
	
	public DafBulkDataRequest getBulkDataRequestById(Integer id);
	
	public List<DafBulkDataRequest> getBulkDataRequestsByProcessedFlag(Boolean flag);
	
	public Integer deleteRequestById(Integer id);

}