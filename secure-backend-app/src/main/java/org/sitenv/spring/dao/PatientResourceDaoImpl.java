package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.PatientResource;

import ca.uhn.fhir.model.dstu2.resource.Patient;

@Repository
public class PatientResourceDaoImpl extends AbstractDao implements PatientResourceDao {

	public PatientResource saveOrUpdate(PatientResource patient) {
		getSession().saveOrUpdate(patient);
		return patient;
	}

	public List<PatientResource> getAllPatientResources() {
		List<PatientResource> patientsList = getSession().createCriteria(PatientResource.class).list();
		return patientsList;
	}

	public PatientResource getPatientResourceById(Integer id) {
		PatientResource patient = (PatientResource) getSession().get(PatientResource.class, id);
		return patient;
	}

	public List<PatientResource> findDuplicatesBeforePersist(Patient patient, ExtractionTask et) {
		Criteria criteria = getSession().createCriteria(PatientResource.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		// criteria.add(Restrictions.eqOrIsNull("dataSourceId", et.getDataSourceId()));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'gender' = '" + patient.getGender() + "'"));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->'name'->0->>'given' = '[\""
				+ patient.getName().get(0).getGivenAsSingleString() + "\"]'"));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' = '[\""
				+ patient.getName().get(0).getFamilyAsSingleString() + "\"]'"));
		criteria.add(Restrictions.sqlRestriction(
				"{alias}.data->>'birthDate' = '" + patient.getBirthDateElement().getValueAsString() + "'"));
		return criteria.list();
	}

	public List<PatientResource> getPatientsByActualId(String actualPatientId) {
		Criteria criteria = getSession().createCriteria(PatientResource.class);
		criteria.add(Restrictions.eq("actualPatientId", actualPatientId));
		return criteria.list();
	}

	public List<PatientResource> getPatientResourcesByExtractionIdAndInternalPatientId(Integer etId, String patientId) {
		Criteria criteria = getSession().createCriteria(PatientResource.class);
		criteria.add(Restrictions.eq("extractionTaskId", etId));
		if (patientId != null) {
			criteria.add(Restrictions.eq("internalPatientId", patientId));
		}
		return criteria.list();
	}

}
