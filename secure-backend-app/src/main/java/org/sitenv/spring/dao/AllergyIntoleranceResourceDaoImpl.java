package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.AllergyIntoleranceResource;
import org.sitenv.spring.model.ExtractionTask;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;

@Repository
public class AllergyIntoleranceResourceDaoImpl extends AbstractDao implements AllergyIntoleranceResourceDao {

	public AllergyIntoleranceResource saveOrUpdate(AllergyIntoleranceResource ai) {
		getSession().saveOrUpdate(ai);
		return ai;
	}

	public List<AllergyIntoleranceResource> getAllAllergyIntolerances() {
		List<AllergyIntoleranceResource> aiList = getSession().createCriteria(AllergyIntoleranceResource.class).list();
		return aiList;
	}

	public AllergyIntoleranceResource getAllergyIntoleranceById(Integer aiId) {
		AllergyIntoleranceResource ai = (AllergyIntoleranceResource) getSession().get(AllergyIntoleranceResource.class, aiId);
		return ai;
	}
	
	
	public List<AllergyIntoleranceResource> findDuplicatesBeforePersist(AllergyIntolerance allergy, ExtractionTask et) {
		Criteria criteria = getSession().createCriteria(AllergyIntoleranceResource.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		//criteria.add(Restrictions.eqOrIsNull("dataSourceId", et.getDataSourceId()));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->'patient'->>'reference' = '" + allergy.getPatient().getReference() + "'"));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' = '" + allergy.getStatus()+"'"));
		return criteria.list();
	}

	@Override
	public List<AllergyIntoleranceResource> getAllergyResourceByExtractionIdAndInternalPatientId(Integer etId,
			String patientId) {
		Criteria criteria = getSession().createCriteria(AllergyIntoleranceResource.class);
		criteria.add(Restrictions.eq("extractionTaskId", etId));
		if(patientId!=null) {
			criteria.add(Restrictions.eq("internalPatientId", patientId));
		}
		return criteria.list();
	}

}
