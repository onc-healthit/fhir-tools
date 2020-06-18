package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.dao.LocationDao;
import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("locationService")
@Transactional
public class LocationServiceImpl implements LocationService {

	@Autowired
	private LocationDao locationDao;

	@Override
	@Transactional
	public DafLocation getLocationById(String id) {
		return locationDao.getLocationById(id);
	}

	@Override
	@Transactional
	public DafLocation getLocationByVersionId(String theId, String versionId) {
		return locationDao.getLocationByVersionId(theId, versionId);
	}

	@Override
	@Transactional
	public List<DafLocation> getLocationHistoryById(String theId) {
		return locationDao.getLocationHistoryById(theId);
	}

	@Override
	@Transactional
	public List<DafLocation> search(SearchParameterMap searchParameterMap) {
		return locationDao.search(searchParameterMap);
	}

	@Override
	public List<DafLocation> getLocationForBulkData(StringJoiner patients, Date start) {
		return locationDao.getLocationForBulkData(patients, start);
	}

}
