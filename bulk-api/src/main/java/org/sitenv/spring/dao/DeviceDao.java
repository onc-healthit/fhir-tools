package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.query.DeviceSearchCriteria;

import java.util.Date;
import java.util.List;

public interface DeviceDao {

    public List<DafDevice> getAllDevice();

    public DafDevice getDeviceById(int id);

    public List<DafDevice> getDeviceByPatient(String patient);

    public List<DafDevice> getDeviceBySearchCriteria(DeviceSearchCriteria deviceSearchCriteria);

    public List<DafDevice> getDeviceForBulkData(List<Integer> patients, Date start);
}
