package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.*;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("conditionDao")
public class ConditionDaoImpl extends AbstractDao implements ConditionDao {

	/**
	 * This method builds criteria for fetching condition record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the condition
	 */
	public DafCondition getConditionById(String id) {
		List<DafCondition> list = getSession().createNativeQuery(
			"select * from condition where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", DafCondition.class)
				.getResultList();
		return list.get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the condition
	 * record by id.
	 * 
	 * @param theId     : ID of the condition
	 * @param versionId : version of the condition record
	 * @return : DAF object of the condition
	 */
	public DafCondition getConditionByVersionId(String theId, String versionId) {
		DafCondition list = getSession().createNativeQuery(
			"select * from condition where data->>'id' = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", DafCondition.class)
				.getSingleResult();
		return list;
	}

	/**
	 * This method builds criteria for fetching history of the condition by id
	 * 
	 * @param theId : ID of the condition
	 * @return : List of condition DAF records
F	 */
	public List<DafCondition> getConditionHistoryById(String theId) {
		List<DafCondition> list = getSession().createNativeQuery(
				"select * from condition where data->>'id' = '"+theId+"' order by data->'meta'->>'versionId' desc", DafCondition.class)
		    	.getResultList();
		return list;
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap : parameter for search
	 * @return criteria : DAF condition object
	 */

	@SuppressWarnings("unchecked")
	public List<DafCondition> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafCondition.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);

		// build criteria for clinical-status
		buildClinicalStatusCriteria(theMap, criteria);

		// build criteria for verification-status
		buildVerificationStatusCriteria(theMap, criteria);

		// build criteria for severity
		buildSeverityCriteria(theMap, criteria);

		// build criteria for code
		buildCodeCriteria(theMap, criteria);

		// build criteria for encounter
		buildEncounterCriteria(theMap, criteria);

		// build criteria for Patients
		/* buildPatientCriteria(theMap, criteria); */

		// build criteria for asserter
		buildAsserterCriteria(theMap, criteria);

		// build criteria for abatement
		buildAbatementStringCriteria(theMap, criteria);

		// build criteria for category
		buildCategoryCriteria(theMap, criteria);

		// build criteria for bodySite
		buildBodySiteCriteria(theMap, criteria);

		// build criteria for onSetDateTime
		buildOnSetDateTimeCriteria(theMap, criteria);

		// build criteria for abatementDateTime
		buildAbatementDateTimeCriteria(theMap, criteria);

		// build criteria for onsetAge
		buildOnsetAgeCriteria(theMap, criteria);

		// build criteria for abatementString
		buildAbatementStringCriteria(theMap,criteria);

		// build criteria for RecordedDate
		buildRecordedDateCriteria(theMap, criteria);
		
		// build criteria for patient
				buildPatientCriteria(theMap, criteria);

		return criteria.list();
	}

	/**
	 * This method builds criteria for condition patient
	 * 
	 * @param theMap   : search parameter "patient"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	
	private void buildPatientCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("patient");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam subject = (ReferenceParam) params;
					Criterion criterion = null;
					if (subject.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'reference' ilike '%" + subject.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'display' ilike '%" + subject.getValue() + "%'"));

					} else if (subject.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'subject' IS NULL"));

					} else if (!subject.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'subject' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}		
	}

	/**
	 * This method builds criteria for condition id
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
	 * This method builds criteria for condition identifier
	 * 
	 * @param theMap   : search parameter "identifier"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdentifierCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("identifier");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam identifier = (TokenParam) params;
					Criterion orCond = null;
					if (identifier.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '%"
										+ identifier.getValue() + "%'"),
								
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '%"
										+ identifier.getValue() + "%'")
								);
					} 
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for condition clinical-status
	 * 
	 * @param theMap   : search parameter "clinical-status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildClinicalStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("clinical-status");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam clinicalStatus = (TokenParam) params;
					if (clinicalStatus.getValue() != null) {
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->0->>'system' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->0->>'code' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->0->>'display' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->1->>'system' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->1->>'code' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->1->>'display' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->2->>'system' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->2->>'code' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->2->>'display' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->3->>'system' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->3->>'code' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->3->>'display' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
					}
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for condition verification-status
	 * 
	 * @param theMap   : search parameter "verification-status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildVerificationStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("verification-status");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam verificationStatus = (TokenParam) params;
					if (verificationStatus.getValue() != null) {
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->0->>'system' ilike '%"
										+ verificationStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->0->>'code' ilike '%"
										+ verificationStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->0->>'display' ilike '%"
										+ verificationStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->1->>'system' ilike '%"
										+ verificationStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->1->>'code' ilike '%"
										+ verificationStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->1->>'display' ilike '%"
										+ verificationStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->2->>'system' ilike '%"
										+ verificationStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->2->>'code' ilike '%"
										+ verificationStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->2->>'display' ilike '%"
										+ verificationStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->3->>'system' ilike '%"
										+ verificationStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->3->>'code' ilike '%"
										+ verificationStatus.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'verificationStatus'->'coding'->3->>'display' ilike '%"
										+ verificationStatus.getValue() + "%'"));
					}
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for condition severity
	 * 
	 * @param theMap   : search parameter "severity"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSeverityCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("severity");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam severity = (TokenParam) params;
					if (severity.getValue() != null) {
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->0->>'system' ilike '%"
										+ severity.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->0->>'code' ilike '%"
										+ severity.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->0->>'display' ilike '%"
										+ severity.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->1->>'system' ilike '%"
										+ severity.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->1->>'code' ilike '%"
										+ severity.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->1->>'display' ilike '%"
										+ severity.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->2->>'system' ilike '%"
										+ severity.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->2->>'code' ilike '%"
										+ severity.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->2->>'display' ilike '%"
										+ severity.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->3->>'system' ilike '%"
										+ severity.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->3->>'code' ilike '%"
										+ severity.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'severity'->'coding'->3->>'display' ilike '%"
										+ severity.getValue() + "%'"));
					}
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for condition code
	 * 
	 * @param theMap   : search parameter "code"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCodeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("code");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam code = (TokenParam) params;
					 Criterion orCond= null;
					if (code.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->0->>'system' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->0->>'code' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->0->>'display' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->1->>'system' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->1->>'code' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->1->>'display' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->2->>'system' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->2->>'code' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->2->>'display' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->3->>'system' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->3->>'code' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->3->>'display' ilike '%" + code.getValue() + "%'")
								);
						
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for condition encounter
	 * 
	 * @param theMap   : search parameter "encounter"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildEncounterCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("encounter");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam encounter = (ReferenceParam) params;
					Criterion orCond = null;
					if (encounter.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference' ilike '%"
										+ encounter.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'encounter'->>'type' ilike '%" + encounter.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'encounter'->>'display' ilike '%"
										+ encounter.getValue() + "%'"));
					} else if (encounter.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'encounter' IS NULL"));
					} else if (!encounter.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'encounter' IS NOT NULL"));
					}

					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	
		/**
	 * This method builds criteria for condition asserter
	 * 
	 * @param theMap   : search parameter "asserter"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAsserterCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("asserter");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam asserter = (ReferenceParam) params;
					Criterion orCond = null;
					if (asserter.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'asserter'->>'reference' ilike '%" + asserter.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'asserter'->>'display' ilike '%" + asserter.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'asserter'->>'type' ilike '%" + asserter.getValue() + "%'"));
					} else if (asserter.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'asserter' IS NULL"));
					} else if (!asserter.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'asserter' IS NOT NULL"));
					}

					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for condition AbatementString
	 * 
	 * @param theMap   : search parameter "AbatementString"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	@SuppressWarnings("unused")
	private void buildAbatementStringCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("abatement-string");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam abatementString = (StringParam) params;
					if (abatementString.isExact()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->>'abatementString' = '" + abatementString.getValue() + "'"));
					} else if (abatementString.isContains()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->>'abatementString' ilike '%" + abatementString.getValue() + "%'"));
					} else {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->>'abatementString' ilike '" + abatementString.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for condition category
	 * 
	 * @param theMap   : search parameter "category"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCategoryCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("category");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam category = (TokenParam) params;
					if (category.getValue() != null) {
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'system' ilike '"
										+ category.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'code' ilike '"
										+ category.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display' ilike '"
										+ category.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'system' ilike '"
										+ category.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'code' ilike '"
										+ category.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'display' ilike '"
										+ category.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'system' ilike '"
										+ category.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'code' ilike '"
										+ category.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'display' ilike '"
										+ category.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'system' ilike '"
										+ category.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'code' ilike '"
										+ category.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'display' ilike '"
										+ category.getValue() + "%'"));
					}
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for condition bodySite
	 * 
	 * @param theMap   : search parameter "bodySite"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildBodySiteCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("body-site");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam bodySite = (TokenParam) params;
					if (bodySite.getValue() != null) {
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'bodySite'->0->'coding'->0->>'system' ilike '"
										+ bodySite.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'bodySite'->0->'coding'->0->>'code' ilike '"
										+ bodySite.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'bodySite'->0->'coding'->0->>'display' ilike '"
										+ bodySite.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'bodySite'->0->'coding'->1->>'system' ilike '"
										+ bodySite.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'bodySite'->0->'coding'->1->>'code' ilike '"
										+ bodySite.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'bodySite'->0->'coding'->1->>'display' ilike '"
										+ bodySite.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'bodySite'->1->'coding'->0->>'system' ilike '"
										+ bodySite.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'bodySite'->1->'coding'->0->>'code' ilike '"
										+ bodySite.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'bodySite'->1->'coding'->0->>'display' ilike '"
										+ bodySite.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'bodySite'->1->'coding'->1->>'system' ilike '"
										+ bodySite.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'bodySite'->1->'coding'->1->>'code' ilike '"
										+ bodySite.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'bodySite'->1->'coding'->1->>'display' ilike '"
										+ bodySite.getValue() + "%'"));
					}
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for condition onsetDate
	 * 
	 * @param theMap   : search parameter "onsetDate"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildOnSetDateTimeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("onset-date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam onsetDateTime = (DateParam) params;
					String issuedFormat = onsetDateTime.getValueAsString();
					if (onsetDateTime.getPrefix() != null) {
						if (onsetDateTime.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'onsetDateTime')::DATE > '" + issuedFormat + "'"));
						} else if (onsetDateTime.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'onsetDateTime')::DATE < '" + issuedFormat + "'"));
						} else if (onsetDateTime.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'onsetDateTime')::DATE >= '" + issuedFormat + "'"));
						} else if (onsetDateTime.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'onsetDateTime')::DATE <= '" + issuedFormat + "'"));
						} else if (onsetDateTime.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'onsetDateTime')::DATE = '" + issuedFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'onsetDateTime')::DATE = '" + issuedFormat + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for condition abatementDate
	 * 
	 * @param theMap   : search parameter "abatementDate"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAbatementDateTimeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("abatement-date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam abatementDateTime = (DateParam) params;
					String issuedFormat = abatementDateTime.getValueAsString();
					if (abatementDateTime.getPrefix() != null) {
						if (abatementDateTime.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'abatementDateTime')::DATE > '" + issuedFormat + "'"));
						} else if (abatementDateTime.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'abatementDateTime')::DATE < '" + issuedFormat + "'"));
						} else if (abatementDateTime.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'abatementDateTime')::DATE >= '" + issuedFormat + "'"));
						} else if (abatementDateTime.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'abatementDateTime')::DATE <= '" + issuedFormat + "'"));
						} else if (abatementDateTime.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'abatementDateTime')::DATE = '" + issuedFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'abatementDateTime')::DATE = '" + issuedFormat + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for condition recordedDate
	 * 
	 * @param theMap   : search parameter "recordedDate"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildRecordedDateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("recorded-date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam recordedDate = (DateParam) params;
					String issuedFormat = recordedDate.getValueAsString();
					if (recordedDate.getPrefix() != null) {
						if (recordedDate.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE > '" + issuedFormat + "'"));
						} else if (recordedDate.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE < '" + issuedFormat + "'"));
						} else if (recordedDate.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE >= '" + issuedFormat + "'"));
						} else if (recordedDate.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE <= '" + issuedFormat + "'"));
						} else if (recordedDate.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE = '" + issuedFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE = '" + issuedFormat + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria query for onset-age
	 * @param theMap search parameter
	 * @param criteria
	 */
	private void buildOnsetAgeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("onset-age");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					QuantityParam onSetAge = (QuantityParam) params;
					if (onSetAge.getValue() != null) {
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'onsetAge'->>'system' ilike '%"
										+ onSetAge.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'onsetAge'->>'value' ilike '%"
										+ onSetAge.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'onsetAge'->>'unit' ilike '%"
										+ onSetAge.getValue() + "%'"));
					}
				}
				criteria.add(disjunction);
			}
		}
	}


}
