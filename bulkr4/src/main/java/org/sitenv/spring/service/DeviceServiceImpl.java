package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import javax.transaction.Transactional;

import org.sitenv.spring.dao.DeviceDao;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Override
	public List<DafDevice> getDeviceForBulkData(StringJoiner patients, Date start) {
		return this.deviceDao.getDeviceForBulkData(patients, start);
	}
}


