package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.query.DeviceSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository("deviceDao")
public class DeviceDaoImpl extends AbstractDao implements DeviceDao {

    private static final Logger logger = LoggerFactory.getLogger(DeviceDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<DafDevice> getAllDevice() {
        Criteria criteria = getSession().createCriteria(DafDevice.class);
        return (List<DafDevice>) criteria.list();
    }

    @Override
    public DafDevice getDeviceById(int id) {
        DafDevice dafDevice = (DafDevice) getSession().get(DafDevice.class, id);
        return dafDevice;
    }

    @Override
    public List<DafDevice> getDeviceByPatient(String patient) {
        Criteria criteria = getSession().createCriteria(DafDevice.class, "device")
                .createAlias("device.patient", "dp")
                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
        List<DafDevice> dafDevices = criteria.list();
        return dafDevices;
    }

    @Override
    public List<DafDevice> getDeviceBySearchCriteria(DeviceSearchCriteria deviceSearchCriteria) {
        List<DafDevice> dafDevices = getDevice(deviceSearchCriteria);
        return dafDevices;
    }

    public List<DafDevice> getDevice(DeviceSearchCriteria deviceSearchCriteria) {

        Criteria criteria = getSession().createCriteria(DafDevice.class, "device").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (deviceSearchCriteria.getPatient() != null) {
            criteria.add(Restrictions.eq("device.patient.id", deviceSearchCriteria.getPatient().intValue()));
        }

        return (List<DafDevice>) criteria.list();
    }
    
    public List<DafDevice> getDeviceForBulkData(List<Integer> patients, Date start){
      	
    	Criteria criteria = getSession().createCriteria(DafDevice.class, "device")
                .createAlias("device.patient", "dp");
    			if(patients!=null) {
                criteria.add(Restrictions.in("dp.id", patients));
    			}
    			if(start != null) {
    				criteria.add(Restrictions.ge("updated", start));
    			}
    	return criteria.list();
    }
    
   

}
