package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafGroup;

public interface GroupDao {
	
	public DafGroup saveOrUpdateGroup(DafGroup group);
	
	public DafGroup getGroupById(Integer id);
	
	public List<DafGroup> getAllGroups();

}
