package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafPractitionerRole;
import org.sitenv.spring.model.DafProvenance;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository("practitionerRoleDao")
public class PractitionerRoleDaoImpl extends AbstractDao implements PractitionerRoleDao {

	/**
	 * This method builds criteria for fetching PractitionerRole record by id.
	 * @param id : ID of the resource
	 * @return : DafPractitionerRole object
	 */
	@Override
	public DafPractitionerRole getPractitionerRoleById(String id) {
		
		Criteria criteria = getSession().createCriteria(DafPractitionerRole.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafPractitionerRole) criteria.list().get(0);
    }
	
	/**
	 * This method builds criteria for fetching particular version of the practitionerRole record by id.
	 * @param theId : ID of the practitionerRole
	 * @param versionId : version of the practitionerRole record
	 * @return : DafPractitionerRole object
	 */
	@Override
	public DafPractitionerRole getPractitionerRoleByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafPractitionerRole.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" +versionId+"'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		criteria.add(versionConjunction);
		return (DafPractitionerRole) criteria.uniqueResult();
	}
	
	/**
	 * This method invokes various methods for search
	 * @param theId : parameter for search
	 * @return criteria : DafPractitionerRole object
	 */
	@SuppressWarnings("unchecked")
	public List<DafPractitionerRole> getPractitionerRoleHistoryById(String theId) {
		Criteria criteria = getSession().createCriteria(DafPractitionerRole.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		return (List<DafPractitionerRole>)criteria.list();
	}

	/**
	 * This method invokes various methods for search
	 *
	 * @param theMap : parameter for search
	 * @return criteria : DAFPractitionerRole object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafPractitionerRole> search(SearchParameterMap theMap) {

		Criteria criteria = getSession().createCriteria(DafPractitionerRole.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);

		// build criteria for Specialty
		buildSpecialtyCriteria(theMap, criteria);

		// build criteria for Practitioner
		buildPractitionerCriteria(theMap, criteria);

		return criteria.list();
	}


	/**
	 * This method builds criteria for PractitionerRole id
	 *
	 * @param theMap   : search parameter "_id"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("_id");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam id = (StringParam) params;
					if (id.getValue() != null) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + id.getValue() + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for PractitionerRole identifier
	 *
	 * @param theMap   : search parameter "identifier"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdentifierCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("identifier");
//		criteria.add(Restrictions.in("this_.data->'practitioner'->>'reference'","Practitioner/77d9bef6-d3b0-3b5f-8ba6-a46814924757"));

/*
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam identifier = (TokenParam) params;


					Criterion orCond= null;
					if (identifier.getValue() != null) {
						orCond = Restrictions.or(

					}
						identifier.getSystem()


					if (id.getValue() != null) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + id.getValue() + "'"));
					}
				}
			}
		}
*/
	}

	/**
	 * This method builds criteria for PractitionerRole Practitioner
	 *
	 * @param theMap   : search parameter "practitioner"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPractitionerCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("practitioner");

		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam practitioner = (ReferenceParam) params;
					Criterion criterion = null;
					if(practitioner.getChain() != null){
						if("identifier".equalsIgnoreCase(practitioner.getChain())){
							criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->'practitioner'->>'reference' LIKE ANY ("+getPractitionerSql(practitioner.getValue())+")"));
						}else if("name".equalsIgnoreCase(practitioner.getChain())){
							criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->'practitioner'->>'display' ilike '%"+ practitioner.getValue() + "%'"));
						}
					}else{
						if (practitioner.getValue() != null) {
							criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->'practitioner'->>'reference' = '"+ practitioner.getValue() + "'"),
									Restrictions.sqlRestriction("{alias}.data->'practitioner'->>'display' ilike '%"+ practitioner.getValue() + "%'"));
						}
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	private String getPractitionerSql(String identifier){
		String[] arrOfStr = StringUtils.split(identifier, "|");
		return	"select CONCAT('Practitioner/', p.data->>'id') as id from practitioner p, " +
						"LATERAL json_array_elements(p.data->'identifier') identifier " +
						"WHERE (identifier ->>'system' = '"+arrOfStr[0]+"' " +
						"and identifier ->>'value' = '"+arrOfStr[1]+"')";
	}

	/**
	 * This method builds criteria for PractitionerRole patient
	 *
	 * @param theMap   : search parameter "Specialty"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSpecialtyCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("specialty");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam code = (TokenParam) params;
					Criterion criterion = null;
					if (!code.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'specialty'->0->'coding'->0->>'system' ilike '%"
										+ code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'specialty'->0->'coding'->0->>'code' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'specialty'->0->'coding'->0->>'display' ilike '%"
										+ code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'specialty'->0->>'text' ilike '%" + code.getValue() + "%'"));
					} else if (code.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'specialty' IS NULL"));
					} else if (!code.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'specialty' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}
}
