package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import org.sitenv.spring.model.ClaimResource;
import org.sitenv.spring.model.ExtractionTask;

import ca.uhn.fhir.model.dstu2.resource.Claim;

@Repository
public class ClaimResourceDaoImpl extends AbstractDao implements ClaimResourceDao {

	public ClaimResource saveOrUpdate(ClaimResource claim) {
		getSession().saveOrUpdate(claim);
		return claim;
	}

	public List<ClaimResource> getAllClaimResources() {
		List<ClaimResource> claimsList = getSession().createCriteria(ClaimResource.class).list();
		return claimsList;
	}

	public ClaimResource getClaimResourceById(Integer id) {
		ClaimResource claim = (ClaimResource) getSession().get(ClaimResource.class, id);
		return claim;
	}

	public List<ClaimResource> findDuplicatesBeforePersist(Claim claim, ExtractionTask et) {
		Criteria criteria = getSession().createCriteria(ClaimResource.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		//criteria.add(Restrictions.eqOrIsNull("dataSourceId", et.getDataSourceId()));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->'patient'->>'reference' = '" + claim.getPatient().getReference() + "'"));
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + claim.getId().getIdPart()+"'"));
		return criteria.list();
	}

	public List<ClaimResource> getClaimResourcesByExtractionIdAndInternalPatientId(Integer etId, String patientId) {
		Criteria criteria = getSession().createCriteria(ClaimResource.class);
		criteria.add(Restrictions.eq("extractionTaskId", etId));
		if(patientId!=null) {
			criteria.add(Restrictions.eq("internalPatientId", patientId));
		}
		return criteria.list();
	}

	@Override
	public Double getUtilizationAmountByPatientId(String internalPatientId) {
		String nativeQuery = "select SUM((data->'item'->0->'unitPrice'->>'value')\\:\\:float ) from claim where internal_patient_id = '"+internalPatientId+"'";
		SQLQuery query = getSession().createSQLQuery(nativeQuery);
		Double  result = (Double) query.uniqueResult();
		
		return result;
	}

}
