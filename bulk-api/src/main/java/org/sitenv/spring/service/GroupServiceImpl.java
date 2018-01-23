package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.dao.GroupDao;
import org.sitenv.spring.model.DafGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("groupService")
@Transactional
public class GroupServiceImpl implements GroupService {
	
	@Autowired
	private GroupDao groupDao;

	public DafGroup saveOrUpdateGroup(DafGroup group) {
		
		return groupDao.saveOrUpdateGroup(group);
	}

	public DafGroup getGroupById(Integer id) {
		
		return groupDao.getGroupById(id);
	}

	public List<DafGroup> getAllGroups() {
		
		return groupDao.getAllGroups();
	}

}
