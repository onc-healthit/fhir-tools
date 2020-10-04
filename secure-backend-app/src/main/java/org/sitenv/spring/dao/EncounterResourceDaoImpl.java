package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import org.sitenv.spring.model.EncounterResource;
import org.sitenv.spring.model.ExtractionTask;

import ca.uhn.fhir.model.dstu2.resource.Encounter;

@Repository
public class EncounterResourceDaoImpl extends AbstractDao implements EncounterResourceDao {

	public EncounterResource saveOrUpdate(EncounterResource encounter) {
		getSession().saveOrUpdate(encounter);
		return encounter;
	}

	public List<EncounterResource> getAllEncounterResources() {
		List<EncounterResource> encountersList = getSession().createCriteria(EncounterResource.class).list();
		return encountersList;
	}

	public EncounterResource getEncounterResourceById(Integer id) {
		EncounterResource encounter = (EncounterResource) getSession().get(EncounterResource.class, id);
		return encounter;
	}

	public List<EncounterResource> findDuplicatesBeforePersist(Encounter encounter, ExtractionTask et) {
		Criteria criteria = getSession().createCriteria(EncounterResource.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		//criteria.add(Restrictions.eqOrIsNull("dataSourceId", et.getDataSourceId()));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->'patient'->>'reference' = '" + encounter.getPatient().getReference() + "'"));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' = '" + encounter.getStatus()+"'"));
		return criteria.list();
	}

	public List<EncounterResource> getEncounterResourcesByExtractionIdAndInternalPatientId(Integer etId,
			String patientId) {
		Criteria criteria = getSession().createCriteria(EncounterResource.class);
		criteria.add(Restrictions.eq("extractionTaskId", etId));
		if(patientId!=null) {
			criteria.add(Restrictions.eq("internalPatientId", patientId));
		}
		return criteria.list();
	}

}
