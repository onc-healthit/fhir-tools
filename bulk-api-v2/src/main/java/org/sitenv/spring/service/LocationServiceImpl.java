package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;

import org.sitenv.spring.dao.LocationDao;
import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.query.LocationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("locationResourceService")
@Transactional
public class LocationServiceImpl implements LocationService {
	
	@Autowired
	private LocationDao dao;

	@Override
	public List<DafLocation> getAllLocations() {
		
		return dao.getAllLocations();
	}

	@Override
	public DafLocation getLocationById(int id) {
		
		return dao.getLocationById(id);
	}

	@Override
	public List<DafLocation> getLocationBySearchCriteria(
			LocationSearchCriteria locationSearchCriteria) {
		
		return dao.getLocationBySearchCriteria(locationSearchCriteria);
	}
	
	public List<DafLocation> getLocationForBulkData(List<Integer> patients, Date start){
		return this.dao.getLocationForBulkData(patients, start);
	}
}
