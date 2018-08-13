package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.query.LocationSearchCriteria;

import java.util.Date;
import java.util.List;

public interface LocationDao {
    public List<DafLocation> getAllLocations();

    public DafLocation getLocationById(int id);

    public List<DafLocation> getLocationBySearchCriteria(LocationSearchCriteria locationSearchCriteria);

	public List<DafLocation> getLocationForBulkData(List<Integer> patients, Date start);
}
