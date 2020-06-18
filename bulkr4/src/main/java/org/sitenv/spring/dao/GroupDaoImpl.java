package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafGroup;
import org.springframework.stereotype.Repository;

@Repository("groupDao")
public class GroupDaoImpl extends AbstractDao implements GroupDao {

//	public DafGroup saveOrUpdateGroup(DafGroup group) {
//		getSession().saveOrUpdate(group);
//		return group;
//	}

	public DafGroup getGroupById(String id) {
//		DafGroup dafGroup = (DafGroup) getSession().get(DafGroup.class, id);
//        return dafGroup;
		try {
		DafGroup list = getSession().createNativeQuery(
				"select * from groups where data->>'id' = '"+id+"' ", DafGroup.class)
					.getSingleResult();
				return list;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}	

//	public List<DafGroup> getAllGroups() {
//		Criteria criteria = getSession().createCriteria(DafGroup.class);
//        return (List<DafGroup>) criteria.list();
//	}


