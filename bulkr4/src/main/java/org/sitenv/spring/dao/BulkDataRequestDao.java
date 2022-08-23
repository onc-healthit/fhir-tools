package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafBulkDataRequest;

import java.util.List;

public interface BulkDataRequestDao {
	
	public DafBulkDataRequest saveBulkDataRequest(DafBulkDataRequest bdr);
	
	public DafBulkDataRequest getBulkDataRequestById(Integer id);
	
	public List<DafBulkDataRequest> getBulkDataRequestsByProcessedFlag(Boolean flag);
	
	public Integer deleteRequestById(Integer id);
	
}
