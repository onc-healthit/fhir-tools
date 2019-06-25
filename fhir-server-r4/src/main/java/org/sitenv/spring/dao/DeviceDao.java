package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface DeviceDao {
	
	public DafDevice getDeviceById(int id);
	
	public DafDevice getDeviceByVersionId(int theId, String versionId);
	
	public List<DafDevice> getDeviceHistoryById(int theId);
	
	public List<DafDevice> search(SearchParameterMap theMap);
}
