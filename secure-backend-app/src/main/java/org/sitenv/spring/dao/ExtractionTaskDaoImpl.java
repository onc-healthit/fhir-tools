package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import org.sitenv.spring.model.ExtractionTask;

@Repository
public class ExtractionTaskDaoImpl extends AbstractDao implements ExtractionTaskDao {

	
	public ExtractionTask saveOrUpdate(ExtractionTask et) {
		getSession().saveOrUpdate(et);
		return et;
	}

	@Override
	public ExtractionTask getExtractionTaskById(Integer etId) {
		ExtractionTask et = (ExtractionTask) getSession().get(ExtractionTask.class, etId);
		return et;
	}

	@Override
	public List<ExtractionTask> getAllExtractionTasks() {
		List<ExtractionTask> etList = getSession().createCriteria(ExtractionTask.class).list();
		return etList;
	}

	@Override
	public List<ExtractionTask> getExtractionTasksByProcessFlag(Boolean processFlag) {
		Criteria crit = getSession().createCriteria(ExtractionTask.class);
		crit.add(Restrictions.eq("processFlag", processFlag));
		return crit.list();
	}

}
