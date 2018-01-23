package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.sitenv.spring.model.DafGroup;
import org.springframework.stereotype.Repository;

@Repository("groupDao")
public class GroupDaoImpl extends AbstractDao implements GroupDao {

	public DafGroup saveOrUpdateGroup(DafGroup group) {
		getSession().saveOrUpdate(group);
		return group;
	}

	public DafGroup getGroupById(Integer id) {
		DafGroup dafGroup = (DafGroup) getSession().get(DafGroup.class, id);
        return dafGroup;
	}

	public List<DafGroup> getAllGroups() {
		Criteria criteria = getSession().createCriteria(DafGroup.class);
        return (List<DafGroup>) criteria.list();
	}

}
