package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.ExtractionTask;
import org.springframework.stereotype.Repository;

@Repository("ExtractionTaskDao")
public class ExtractionTaskDaoImpl extends AbstractDao implements ExtractionTaskDao {

	
	public ExtractionTask saveOrUpdate(ExtractionTask et) {
		ExtractionTask ets = getExtractionTasksDataSource(et.getDataSourceId());
		if(ets != null) {
			ets.setResponseBody(et.getResponseBody());
			ets.setGroupId(et.getGroupId());
			ets.setDataSourceId(et.getDataSourceId());
			ets.setContentLocation(et.getContentLocation());
			ets.setRequestURL(et.getRequestURL());
			ets.setResponseBody(et.getResponseBody());
			ets.setStatus(et.getStatus());
			ets.setProcessFlag(et.getProcessFlag());
			ets.setAuthenticationMode(et.getAuthenticationMode());
			getSession().update(ets);
		}else {
			getSession().saveOrUpdate(et);
		}
		
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
	
	
	public ExtractionTask getExtractionTasksDataSource(String ds) {

		if (ds != null) {
			try {
				Criteria crit = getSession().createCriteria(ExtractionTask.class).add(Restrictions.eq("dataSourceId", ds));
				ExtractionTask extractionTask = (ExtractionTask) crit.uniqueResult();

				return extractionTask;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
