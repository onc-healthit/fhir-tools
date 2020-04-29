package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafMedicationStatement;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("medicationStatementDao")
public class MedicationStatementDaoImpl extends AbstractDao implements MedicationStatementDao {

	/**
	 * This method builds criteria for fetching MedicationStatement record by id.
	 * 
	 * @param id
	 *            : ID of the resource
	 * @return : DafMedicationStatement object
	 */
	@Override
	public DafMedicationStatement getMedicationStatementById(String id) {
		Criteria criteria = getSession().createCriteria(DafMedicationStatement.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions
				.sqlRestriction("{alias}.data->>'id' = '" + id + "' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafMedicationStatement) criteria.list().get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the
	 * MedicationStatement record by id.
	 * 
	 * @param theId
	 *            : ID of the MedicationStatement
	 * @param versionId
	 *            : version of the MedicationStatement record
	 * @return : DafMedicationStatement object
	 */
	@Override
	public DafMedicationStatement getMedicationStatementByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafMedicationStatement.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" + versionId + "'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + theId + "'"));
		criteria.add(versionConjunction);
		return (DafMedicationStatement) criteria.uniqueResult();
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap
	 *            : parameter for search
	 * @return criteria : DafMedicationStatement object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafMedicationStatement> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafMedicationStatement.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);

		// build criteria for status
		buildStatusCriteria(theMap, criteria);

		// build criteria for context
		buildContextCriteria(theMap, criteria);

		// build criteria for patient
		buildPatientCriteria(theMap, criteria);

		// build criteria for category
		buildCategoryCriteria(theMap, criteria);

		// build criteria for partOf
		buildPartOfCriteria(theMap, criteria);

		// build criteria for medication
		buildMedicationCriteria(theMap, criteria);

		// build criteria for source
		buildSourceCriteria(theMap, criteria);

		// build criteria for subject
		buildSubjectCriteria(theMap, criteria);

		// build criteria for code
		buildCodeCriteria(theMap, criteria);

		return criteria.list();

	}

	/**
	 * This method builds criteria for MedicationStatement id
	 * 
	 * @param theMap
	 *            : search parameter "_id"
	 * @param criteria
	 *            : for retrieving entities by composing Criterion objects
	 */
	private void buildIdCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("_id");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					TokenParam id = (TokenParam) params;
					if (id.getValue() != null) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + id.getValue() + "'"));

					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for MedicationStatement identifier
	 * 
	 * @param theMap
	 *            : search parameter "identifier"
	 * @param criteria
	 *            : for retrieving entities by composing Criterion objects
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
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '%"
										+ identifier.getValue() + "%'"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for medicationstatement status
	 * 
	 * @param theMap
	 *            : search parameter "status"
	 * @param criteria
	 *            : for retrieving entities by composing Criterion objects
	 */
	private void buildStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("status");

		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam status = (TokenParam) params;
					Criterion orCond = null;
					if (status.getValue() != null) {
						orCond = Restrictions.or(Restrictions
								.sqlRestriction("{alias}.data->>'status' ilike '" + status.getValue() + "'"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for MedicationStatement context
	 * 
	 * @param theMap
	 *            : search parameter "context"
	 * @param criteria
	 *            : for retrieving entities by composing Criterion objects
	 */
	private void buildContextCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("context");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam context = (ReferenceParam) params;
					Criterion orCond = null;
					if (context.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'context'->>'reference' ilike '%" + context.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'context'->>'display' ilike '%" + context.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'context'->>'type' ilike '%" + context.getValue() + "%'"));
					} else if (context.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'context' IS NULL"));
					} else if (!context.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'context' IS NOT NULL"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for patient:Returns statements for a specific
	 * patient.
	 * 
	 * @param theMap
	 *            : search parameter "patient"
	 * @param criteria
	 *            : for retrieving entities by composing Criterion objects
	 */
	private void buildPatientCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("patient");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam patient = (ReferenceParam) params;
					Criterion orCond = null;
					if (patient.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'reference' ilike '%" + patient.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'display' ilike '%" + patient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'type' ilike '%" + patient.getValue() + "%'"));
					} else if (patient.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'subject' IS NULL"));
					} else if (!patient.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'subject' IS NOT NULL"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for patient:Returns statements for a specific
	 * patient.
	 * 
	 * @param theMap
	 *            : search parameter "patient"
	 * @param criteria
	 *            : for retrieving entities by composing Criterion objects
	 */
	private void buildSubjectCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("subject");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam subject = (ReferenceParam) params;
					Criterion orCond = null;
					if (subject.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'reference' ilike '%" + subject.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'display' ilike '%" + subject.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'type' ilike '%" + subject.getValue() + "%'"));
					} else if (subject.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'subject' IS NULL"));
					} else if (!subject.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'subject' IS NOT NULL"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for medicationstatement category
	 * 
	 * @param theMap
	 *            : search parameter "category"
	 * @param criteria
	 *            : for retrieving entities by composing Criterion objects
	 */
	private void buildCategoryCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("category");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam category = (TokenParam) params;
					Criterion orCond = null;
					if (category.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->0->>'code' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->0->>'display' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->0->>'system' ilike '%"
										+ category.getValue() + "%'"),

								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->1->>'code' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->1->>'display' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->1->>'system' ilike '%"
										+ category.getValue() + "%'"),

								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->2->>'code' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->2->>'display' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->2->>'system' ilike '%"
										+ category.getValue() + "%'"),

								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->3->>'code' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->3->>'display' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->3->>'system' ilike '%"
										+ category.getValue() + "%'"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for MedicationStatement partOf
	 * 
	 * @param theMap
	 *            : search parameter "partOf"
	 * @param criteria
	 *            : for retrieving entities by composing Criterion objects
	 */
	private void buildPartOfCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("part-of");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam partOf = (ReferenceParam) params;
					Criterion orCond = null;
					if (partOf.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->0->>'reference' ilike '%" + partOf.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->0->>'display' ilike '%" + partOf.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->0->>'type' ilike '%" + partOf.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->1->>'reference' ilike '%" + partOf.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->1->>'display' ilike '%" + partOf.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->1->>'type' ilike '%" + partOf.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->2->>'reference' ilike '%" + partOf.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->2->>'display' ilike '%" + partOf.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->2->>'type' ilike '%" + partOf.getValue() + "%'"));
					} else if (partOf.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'partOf' IS NULL"));
					} else if (!partOf.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'partOf' IS NOT NULL"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for medication: Returns dispenses of this
	 * medicine resource
	 * 
	 * @param theMap
	 *            : search parameter "medication"
	 * @param criteria
	 *            : for retrieving entities by composing Criterion objects
	 */
	private void buildMedicationCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("medication");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam medication = (ReferenceParam) params;
					Criterion orCond = null;
					if (medication.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'medicationReference'->>'reference' ilike '%"
										+ medication.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'medicationReference'->>'display' ilike '%"
										+ medication.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'medicationReference'->>'type' ilike '%"
										+ medication.getValue() + "%'"));
					} else if (medication.getMissing()) {
						orCond = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'medicationReference' IS NULL"));
					} else if (!medication.getMissing()) {
						orCond = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'medicationReference' IS NOT NULL"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for source: Who or where the information in the
	 * statement came from
	 * 
	 * @param theMap
	 *            : search parameter "source"
	 * @param criteria
	 *            : for retrieving entities by composing Criterion objects
	 */
	private void buildSourceCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("source");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam source = (ReferenceParam) params;
					Criterion orCond = null;
					if (source.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'informationSource'->>'reference' ilike '%"
										+ source.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'informationSource'->>'display' ilike '%"
										+ source.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'informationSource'->>'type' ilike '%"
										+ source.getValue() + "%'"));
					} else if (source.getMissing()) {
						orCond = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'informationSource' IS NULL"));
					} else if (!source.getMissing()) {
						orCond = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'informationSource' IS NOT NULL"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for MedicationStatement code
	 * 
	 * @param theMap
	 *            : search parameter "code"
	 * @param criteria
	 *            : for retrieving entities by composing Criterion objects
	 */
	private void buildCodeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("code");

		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam medicationCodeable = (TokenParam) params;
					Criterion orCond = null;
					if (medicationCodeable.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'medicationCodeableConcept'->'coding'->0->>'system' ilike '%"
												+ medicationCodeable.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'medicationCodeableConcept'->'coding'->0->>'code' ilike '%"
												+ medicationCodeable.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'medicationCodeableConcept'->'coding'->0->>'display' ilike '%"
												+ medicationCodeable.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'medicationCodeableConcept'->'coding'->1->>'system' ilike '%"
												+ medicationCodeable.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'medicationCodeableConcept'->'coding'->1->>'code' ilike '%"
												+ medicationCodeable.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'medicationCodeableConcept'->'coding'->1->>'display' ilike '%"
												+ medicationCodeable.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'medicationCodeableConcept'->'coding'->2->>'system' ilike '%"
												+ medicationCodeable.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'medicationCodeableConcept'->'coding'->2->>'code' ilike '%"
												+ medicationCodeable.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'medicationCodeableConcept'->'coding'->2->>'display' ilike '%"
												+ medicationCodeable.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'medicationCodeableConcept'->>'text' ilike '%"
												+ medicationCodeable.getValue() + "%'"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for fetching history of the MedicationStatement
	 * by id
	 * 
	 * @param theId : ID of the MedicationStatement
	 * @return : List of DafMedicationStatement records
	 */
	@Override
	public List<DafMedicationStatement> getMedicationStatementHistoryById(String theId) {
		List<DafMedicationStatement> list = getSession()
				.createNativeQuery("select * from medicationstatement where data->>'id' = '" + theId
						+ "' order by data->'meta'->>'versionId' desc", DafMedicationStatement.class)
				.getResultList();
		return list;
	}
}
