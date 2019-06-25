package org.sitenv.spring.service;

import org.sitenv.spring.dao.ServiceRequestDao;
import org.sitenv.spring.model.DafServiceRequest;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service("serviceRequestService")
@Transactional
public  class ServiceRequestServiceImpl implements ServiceRequestService{

	@Autowired
    private ServiceRequestDao serviceRequestDao;
	
	@Override
    @Transactional
    public DafServiceRequest getServiceRequestById(int id) {
        return this.serviceRequestDao.getServiceRequestById(id);
    }
	@Override
	@Transactional
	public DafServiceRequest getServiceRequestByVersionId(int theId, String versionId) {
		return this.serviceRequestDao.getServiceRequestByVersionId(theId, versionId);
	}
	
	@Override
	@Transactional
    public List<DafServiceRequest> getServiceRequestHistoryById(int theId) {
		 return this.serviceRequestDao.getServiceRequestHistoryById(theId);
    }
	
	@Override
	@Transactional
	public List<DafServiceRequest> search(SearchParameterMap theMap) {
		return this.serviceRequestDao.search(theMap);
	}
}


