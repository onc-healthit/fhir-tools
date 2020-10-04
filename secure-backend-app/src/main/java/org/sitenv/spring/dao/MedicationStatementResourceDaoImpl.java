package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.model.MedicationStatementResource;

import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;

@Repository
public class MedicationStatementResourceDaoImpl extends AbstractDao implements MedicationStatementResourceDao {

	public MedicationStatementResource saveOrUpdate(MedicationStatementResource ms) {
		getSession().saveOrUpdate(ms);
		return ms;
	}

	public List<MedicationStatementResource> getAllMedicationStatements() {
		Criteria criteria = getSession().createCriteria(MedicationStatementResource.class);
		return criteria.list();
	}

	@Override
	public MedicationStatementResource getMedicationStatementById(Integer msId) {
		MedicationStatementResource ms = (MedicationStatementResource) getSession().get(MedicationStatementResource.class, msId);
		return ms;
	}

	@Override
	public List<MedicationStatementResource> findDuplicatesBeforePersist(MedicationStatement ms, ExtractionTask et) {
		Criteria criteria = getSession().createCriteria(MedicationStatementResource.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		//criteria.add(Restrictions.eqOrIsNull("dataSourceId", et.getDataSourceId()));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->'patient'->>'reference' = '" + ms.getPatient().getReference() + "'"));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' = '" + ms.getStatus()+"'"));
		return criteria.list();
	}

	@Override
	public List<MedicationStatementResource> getMedicationStatementsByExtractionIdAndInternalPatientId(Integer etId,
			String patientId) {
		Criteria criteria = getSession().createCriteria(MedicationStatementResource.class);
		criteria.add(Restrictions.eq("extractionTaskId", etId));
		if(patientId!=null) {
			criteria.add(Restrictions.eq("internalPatientId", patientId));
		}
		return criteria.list();
	}

}
