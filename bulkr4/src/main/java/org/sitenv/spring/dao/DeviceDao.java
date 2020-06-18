package org.sitenv.spring.dao;

import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.util.SearchParameterMap;

public interface DeviceDao {
	
	public DafDevice getDeviceById(String id);
	
	public DafDevice getDeviceByVersionId(String theId, String versionId);
	
	public List<DafDevice> getDeviceHistoryById(String theId);
	
	public List<DafDevice> search(SearchParameterMap theMap);

	public List<DafDevice> getDeviceForBulkData(StringJoiner patient, Date start);
}
