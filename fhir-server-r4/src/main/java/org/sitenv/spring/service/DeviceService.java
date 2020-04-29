package org.sitenv.spring.service;

import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.util.SearchParameterMap;

import java.util.List;

public interface DeviceService {
	
	public DafDevice getDeviceById(String id);
	
	public DafDevice getDeviceByVersionId(String theId, String versionId);
	
	public List<DafDevice> getDeviceHistoryById(String theId);

	public List<DafDevice> search(SearchParameterMap theMap);
}
