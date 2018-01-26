package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafGroup;

public interface GroupService {
	
	public DafGroup saveOrUpdateGroup(DafGroup group);
	
	public DafGroup getGroupById(Integer id);
	
	public List<DafGroup> getAllGroups();

}
