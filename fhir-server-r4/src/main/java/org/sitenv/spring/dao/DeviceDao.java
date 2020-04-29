package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface DeviceDao {
	
	public DafDevice getDeviceById(String id);
	
	public DafDevice getDeviceByVersionId(String theId, String versionId);
	
	public List<DafDevice> getDeviceHistoryById(String theId);
	
	public List<DafDevice> search(SearchParameterMap theMap);
}
