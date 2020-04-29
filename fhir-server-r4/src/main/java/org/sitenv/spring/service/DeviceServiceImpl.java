package org.sitenv.spring.service;

import org.sitenv.spring.dao.DeviceDao;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service("deviceService")
@Transactional
public  class DeviceServiceImpl implements DeviceService{

	@Autowired
    private DeviceDao deviceDao;
	
	@Override
    @Transactional
    public DafDevice getDeviceById(String id) {
        return this.deviceDao.getDeviceById(id);
    }
	@Override
	@Transactional
	public DafDevice getDeviceByVersionId(String theId, String versionId) {
		return this.deviceDao.getDeviceByVersionId(theId, versionId);
	}
	
	@Override
	@Transactional
    public List<DafDevice> getDeviceHistoryById(String theId) {
		 return this.deviceDao.getDeviceHistoryById(theId);
    }
	
	@Override
	@Transactional
	public List<DafDevice> search(SearchParameterMap theMap) {
		return this.deviceDao.search(theMap);
	}
}


