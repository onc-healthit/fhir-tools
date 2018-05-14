package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.ObservationResource;

import ca.uhn.fhir.model.dstu2.resource.Observation;

@Repository
public class ObservationResourceDaoImpl extends AbstractDao implements ObservationResourceDao {

	public ObservationResource saveOrUpdate(ObservationResource observation) {
		getSession().saveOrUpdate(observation);
		return observation;
	}

	public List<ObservationResource> getAllObservationResources() {
		List<ObservationResource> observationsList = getSession().createCriteria(ObservationResource.class).list();
		return observationsList;
	}

	@Override
	public ObservationResource getObservationResourceById(Integer id) {
		ObservationResource observation = (ObservationResource) getSession().get(ObservationResource.class, id);
		return observation;
	}

	@Override
	public List<ObservationResource> findDuplicatesBeforePersist(Observation observation, ExtractionTask et) {
		Criteria criteria = getSession().createCriteria(ObservationResource.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		//criteria.add(Restrictions.eqOrIsNull("dataSourceId", et.getDataSourceId()));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->'patient'->>'reference' = '" + observation.getSubject().getReference() + "'"));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' = '" + observation.getStatus()+"'"));
		return criteria.list();
	}

	@Override
	public List<ObservationResource> getObservationResourcesByExtractionIdAndInternalPatientId(Integer etId,
			String patientId) {
		Criteria criteria = getSession().createCriteria(ObservationResource.class);
		criteria.add(Restrictions.eq("extractionTaskId", etId));
		if(patientId!=null) {
			criteria.add(Restrictions.eq("internalPatientId", patientId));
		}
		return criteria.list();
	}
	
	@Override
	public List<ObservationResource> getObservationsByPatientIdAndCategory(String internalPatienId, String category) {
		Criteria criteria = getSession().createCriteria(ObservationResource.class);
		criteria.add(Restrictions.eq("internalPatientId", internalPatienId));
		if(category!=null) {
			criteria.add(Restrictions.ilike("category", category));
		}
		return criteria.list();
	}

}
