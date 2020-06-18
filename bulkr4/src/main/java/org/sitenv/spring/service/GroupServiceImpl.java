package org.sitenv.spring.service;

import javax.transaction.Transactional;

import org.sitenv.spring.dao.GroupDao;
import org.sitenv.spring.model.DafGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("groupService")
@Transactional
public class GroupServiceImpl implements GroupService {
	
	@Autowired
	private GroupDao groupDao;

//	public DafGroup saveOrUpdateGroup(DafGroup group) {
//		
//		return groupDao.saveOrUpdateGroup(group);
//	}

	public DafGroup getGroupById(String id) {
		
		return groupDao.getGroupById(id);
	}

//	public List<DafGroup> getAllGroups() {
//		
//		return groupDao.getAllGroups();
//	}

}
