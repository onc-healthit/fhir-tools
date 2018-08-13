package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.dao.BulkDataRequestDao;
import org.sitenv.spring.model.DafBulkDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("bulkDataRequestService")
@Transactional
public class BulkDataRequestServiceImpl implements BulkDataRequestService {
	
	@Autowired
	private BulkDataRequestDao bdrDao;

	public DafBulkDataRequest saveBulkDataRequest(DafBulkDataRequest bdr) {
		
		return bdrDao.saveBulkDataRequest(bdr);
	}

	public DafBulkDataRequest getBulkDataRequestById(Integer id) {
		
		return bdrDao.getBulkDataRequestById(id);
	}

	@Override
	public List<DafBulkDataRequest> getBulkDataRequestsByProcessedFlag(Boolean flag) {
		
		return bdrDao.getBulkDataRequestsByProcessedFlag(flag);
	}

	@Override
	public Integer deleteRequestById(Integer id) {
		
		return bdrDao.deleteRequestById(id);
	}

}
