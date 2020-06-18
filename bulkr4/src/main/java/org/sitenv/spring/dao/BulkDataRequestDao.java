package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafBulkDataRequest;

public interface BulkDataRequestDao {
	
	public DafBulkDataRequest saveBulkDataRequest(DafBulkDataRequest bdr);
	
	public DafBulkDataRequest getBulkDataRequestById(Integer id);
	
	public List<DafBulkDataRequest> getBulkDataRequestsByProcessedFlag(Boolean flag);
	
	public Integer deleteRequestById(Integer id);
	
}
