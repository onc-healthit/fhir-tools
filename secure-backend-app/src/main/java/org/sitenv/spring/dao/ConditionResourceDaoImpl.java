package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import org.sitenv.spring.model.ConditionResource;
import org.sitenv.spring.model.ExtractionTask;

import ca.uhn.fhir.model.dstu2.resource.Condition;

@Repository
public class ConditionResourceDaoImpl extends AbstractDao implements ConditionResourceDao {

	public ConditionResource saveOrUpdate(ConditionResource condition) {
		getSession().saveOrUpdate(condition);
		return condition;
	}

	public List<ConditionResource> getAllConditionResources() {
		List<ConditionResource> conditionsList = getSession().createCriteria(ConditionResource.class).list();
		return conditionsList;
	}

	public ConditionResource getConditionResourceById(Integer id) {
		ConditionResource condition = (ConditionResource) getSession().get(ConditionResource.class, id);
		return condition;
	}

	public List<ConditionResource> findDuplicatesBeforePersist(Condition condition, ExtractionTask et) {
		Criteria criteria = getSession().createCriteria(ConditionResource.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		//criteria.add(Restrictions.eqOrIsNull("dataSourceId", et.getDataSourceId()));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->'patient'->>'reference' = '" + condition.getPatient().getReference() + "'"));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'clinicalStatus' = '" + condition.getClinicalStatus()+"'"));
		return criteria.list();
	}

	public List<ConditionResource> getConditionResourcesByExtractionIdAndInternalPatientId(Integer etId,
			String patientId) {
		Criteria criteria = getSession().createCriteria(ConditionResource.class);
		criteria.add(Restrictions.eq("extractionTaskId", etId));
		if(patientId!=null) {
			criteria.add(Restrictions.eq("internalPatientId", patientId));
		}
		return criteria.list();
	}

}
