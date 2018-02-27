package org.sitenv.spring.service;

import org.sitenv.spring.dao.DeviceDao;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.query.DeviceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("deviceResourceService")
@Transactional
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceDao deviceDao;

    @Override
    @Transactional
    public List<DafDevice> getAllDevice() {
        return this.deviceDao.getAllDevice();
    }

    @Override
    @Transactional
    public DafDevice getDeviceById(int id) {
        return this.deviceDao.getDeviceById(id);
    }

    @Override
    @Transactional
    public List<DafDevice> getDeviceByPatient(String patient) {
        return this.deviceDao.getDeviceByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafDevice> getDeviceBySearchCriteria(DeviceSearchCriteria deviceSearchCriteria) {
        return this.deviceDao.getDeviceBySearchCriteria(deviceSearchCriteria);
    }
    
    @Override
    @Transactional
    public List<DafDevice> getDeviceForBulkData(List<Integer> patients, Date start){
    	return this.deviceDao.getDeviceForBulkData(patients, start);
    }

}
