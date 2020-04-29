package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("AllergyIntoleranceDao")
public class AllergyIntoleranceDaoImpl extends AbstractDao implements AllergyIntoleranceDao {

	/**
	 * This method builds criteria for fetching AllergyIntolerance record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the AllergyIntolerance
	 */
	public DafAllergyIntolerance getAllergyIntoleranceById(String id) {

		Criteria criteria = getSession().createCriteria(DafAllergyIntolerance.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafAllergyIntolerance) criteria.list().get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the
	 * AllergyIntolerance record by id.
	 * 
	 * @param theId     : ID of the AllergyIntolerance
	 * @param versionId : version of the AllergyIntolerance record
	 * @return : DAF object of the AllergyIntolerance
	 */
	public DafAllergyIntolerance getAllergyIntoleranceByVersionId(String theId, String versionId) {

		Criteria criteria = getSession().createCriteria(DafAllergyIntolerance.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" + versionId + "'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + theId + "'"));
		criteria.add(versionConjunction);
		return (DafAllergyIntolerance) criteria.uniqueResult();
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap : parameter for search
	 * @return criteria : DAF AllergyIntolerance object
	 */
	@SuppressWarnings("unchecked")
	public List<DafAllergyIntolerance> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafAllergyIntolerance.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);

		// build criteria for date
		buildDateCriteria(theMap, criteria);

		// build criteria for severity
		buildSeverityCriteria(theMap, criteria);

		// build criteria for Manifestation
		buildManifestationCriteria(theMap, criteria);

		// build criteria for code
		buildCodeCriteria(theMap, criteria);

		// build criteria for Recorder
		buildRecorderCriteria(theMap, criteria);

		// build criteria for VerificationStatus
		buildVerificationStatusCriteria(theMap, criteria);

		// build criteria for Criticality
		buildCriticalityCriteria(theMap, criteria);

		// build criteria for ClinicalStatus
		buildClinicalStatusCriteria(theMap, criteria);

		// build criteria for Type
		buildTypeCriteria(theMap, criteria);

		// build criteria for Onset
		buildOnsetCriteria(theMap, criteria);

		// build criteria for Asserter
		buildAsserterCriteria(theMap, criteria);

		// build criteria for patient
		buildPatientCriteria(theMap, criteria);

		// build criteria for category
		buildCategoryCriteria(theMap, criteria);

		// build criteria for LastDate
		buildLastDateCriteria(theMap, criteria);

		return criteria.list();
	}

	/**
	 * This method builds criteria for AllergyIntolerance manifestation
	 * 
	 * @param theMap   : search parameter "manifestation"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildManifestationCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("manifestation");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam manifestation = (TokenParam) params;
					Criterion criterion = null;
					if (!manifestation.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'reaction'->0->'manifestation'->0->'coding'->0->>'system' ilike '%"
												+ manifestation.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'reaction'->0->'manifestation'->0->'coding'->0->>'code' ilike '%"
												+ manifestation.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'reaction'->0->'manifestation'->0->'coding'->0->>'display' ilike '%"
												+ manifestation.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'reaction'->0->'manifestation'->>'text' ilike '%"
										+ manifestation.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'reaction'->1->'manifestation'->0->'coding'->0->>'system' ilike '%"
												+ manifestation.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'reaction'->1->'manifestation'->0->'coding'->0->>'code' ilike '%"
												+ manifestation.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'reaction'->1->'manifestation'->0->'coding'->0->>'display' ilike '%"
												+ manifestation.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'reaction'->1->'manifestation'->>'text' ilike '%"
										+ manifestation.getValue() + "%'")
								
								);
					} else if (manifestation.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'reaction' IS NULL"));
					} else if (!manifestation.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'reaction' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for AllergyIntolerance verificationStatus
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
					Criterion criterion = null;
					if (!verificationStatus.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'verificationStatus'->'coding'->0->>'system' ilike '"
												+ verificationStatus.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'verificationStatus'->'coding'->0->>'code' ilike '"
												+ verificationStatus.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'verificationStatus'->'coding'->0->>'display' ilike '"
												+ verificationStatus.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'verificationStatus'->'coding'->1->>'system' ilike '"
												+ verificationStatus.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'verificationStatus'->'coding'->1->>'code' ilike '"
												+ verificationStatus.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'verificationStatus'->'coding'->1->>'display' ilike '"
												+ verificationStatus.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'verificationStatus'->>'text' ilike '"
										+ verificationStatus.getValue() + "'"));
					} else if (verificationStatus.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'verificationStatus' IS NULL"));
					} else if (!verificationStatus.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'verificationStatus' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for AllergyIntolerance recorder
	 * 
	 * @param theMap   : search parameter "recorder"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildLastDateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("last-date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam lastOccurrence = (DateParam) params;
					String dateFormat = lastOccurrence.getValueAsString();
					if (lastOccurrence.getPrefix() != null) {
						if (lastOccurrence.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'lastOccurrence')::DATE > '" + dateFormat + "'"));
						} else if (lastOccurrence.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'lastOccurrence')::DATE < '" + dateFormat + "'"));
						} else if (lastOccurrence.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'lastOccurrence')::DATE >= '" + dateFormat + "'"));
						} else if (lastOccurrence.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'lastOccurrence')::DATE <= '" + dateFormat + "'"));
						}else if (lastOccurrence.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'lastOccurrence')::DATE = '" + dateFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'lastOccurrence')::DATE = '" + dateFormat + "'"));
					}
				}
			}
		}

	}

	/**
	 * This method builds criteria for AllergyIntolerance type
	 * 
	 * @param theMap   : search parameter "type"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildTypeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("type");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam type = (TokenParam) params;
					Criterion criterion = null;
					if (!type.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->>'type' ilike '%" + type.getValue() + "%'"));
					} else if (type.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'type' IS NULL"));
					} else if (!type.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'type' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}

	}

	/**
	 * This method builds criteria for AllergyIntolerance recorder
	 * 
	 * @param theMap   : search parameter "recorder"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCriticalityCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("criticality");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam criticality = (TokenParam) params;
					Criterion criterion = null;
					if (!criticality.isEmpty()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction(
								"{alias}.data->>'criticality' ilike '%" + criticality.getValue() + "%'"));
					} else if (criticality.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'criticality' IS NULL"));
					} else if (!criticality.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'criticality' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}

	}

	/**
	 * This method builds criteria for AllergyIntolerance recorder
	 * 
	 * @param theMap   : search parameter "recorder"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildClinicalStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("clinical-status");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam clinicalStatus = (TokenParam) params;
					Criterion criterion = null;
					if (!clinicalStatus.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'clinicalStatus'->'coding'->0->>'system' ilike '%"
												+ clinicalStatus.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->0->>'code' ilike '%"
												+ clinicalStatus.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'clinicalStatus'->'coding'->0->>'display' ilike '%"
												+ clinicalStatus.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'clinicalStatus'->'coding'->1->>'system' ilike '%"
												+ clinicalStatus.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'clinicalStatus'->'coding'->1->>'code' ilike '%"
												+ clinicalStatus.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'clinicalStatus'->'coding'->1->>'display' ilike '%"
												+ clinicalStatus.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'clinicalStatus'->>'text' ilike '%"
										+ clinicalStatus.getValue() + "%'"));
					} else if (clinicalStatus.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'clinicalStatus' IS NULL"));
					} else if (!clinicalStatus.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'clinicalStatus' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for AllergyIntolerance recorder
	 * 
	 * @param theMap   : search parameter "recorder"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildOnsetCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("onset");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam onset = (DateParam) params;
					String dateFormat = onset.getValueAsString();
					if (onset.getPrefix() != null) {
						if (onset.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->reaction'->0->>'onset')::DATE > '" + dateFormat + "'"));
						} else if (onset.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->reaction'->0->>'onset')::DATE < '" + dateFormat + "'"));
						} else if (onset.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->reaction'->0->>'onset')::DATE >= '" + dateFormat + "'"));
						} else if (onset.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->reaction'->0->>'onset')::DATE <= '" + dateFormat + "'"));
						} else if (onset.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->reaction'->0->>'onset')::DATE <= '" + dateFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->reaction'->0->>'onset')::DATE = '" + dateFormat + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for AllergyIntolerance recorder
	 * 
	 * @param theMap   : search parameter "recorder"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildRecorderCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("recorder");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam recorder = (ReferenceParam) params;
					Criterion criterion = null;
					if (recorder.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'recorder'->>'reference' ilike '%" + recorder.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'recorder'->>'display' ilike '%" + recorder.getValue() + "%'"));

					} else if (recorder.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'recorder' IS NULL"));

					} else if (!recorder.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'recorder' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for AllergyIntolerance asserter
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
					Criterion criterion = null;
					if (asserter.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'asserter'->>'reference' ilike '%" + asserter.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'asserter'->>'display' ilike '%" + asserter.getValue() + "%'"));

					} else if (asserter.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'asserter' IS NULL"));

					} else if (!asserter.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'asserter' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}

				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for AllergyIntolerance Severity
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
					Criterion criterion = null;
					if (!severity.isEmpty()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction(
								"{alias}.data->'reaction'->0->>'severity' ilike '%" + severity.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'reaction'->1->>'severity' ilike '%" + severity.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'reaction'->2->>'severity' ilike '%" + severity.getValue() + "%'")
								);
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for AllergyIntolerance category
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
					Criterion criterion = null;
					if (!category.isEmpty()) {
						criterion = Restrictions.or(Restrictions
								.sqlRestriction("{alias}.data->'category'->>0 ilike '%" + category.getValue() + "%'"),
								Restrictions
								.sqlRestriction("{alias}.data->'category'->>1 ilike '%" + category.getValue() + "%'"),
								Restrictions
								.sqlRestriction("{alias}.data->'category'->>2 ilike '%" + category.getValue() + "%'")
								);
					} else if (category.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'category' IS NULL"));
					} else if (!category.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'category' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for AllergyIntolerance patient
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
					ReferenceParam patient = (ReferenceParam) params;
					Criterion criterion = null;
					if (patient.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'reference' ilike '%" + patient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'display' ilike '%" + patient.getValue() + "%'"));

					} else if (patient.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'patient' IS NULL"));

					} else if (!patient.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'patient' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for AllergyIntolerance code
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
					Criterion criterion = null;
					if (!code.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->0->>'system' ilike '%"+ code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->0->>'code' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->0->>'display' ilike '%"+ code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->1->>'system' ilike '%"+ code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->1->>'code' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->1->>'display' ilike '%"+ code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->>'text' ilike '%" + code.getValue() + "%'"));
					} else if (code.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'code' IS NULL"));
					} else if (!code.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'code' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}

		}
	}

	/**
	 * This method builds criteria for AllergyIntolerance date
	 * 
	 * @param theMap   : search parameter "date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildDateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam recordedDate = (DateParam) params;
					String dateFormat = recordedDate.getValueAsString();
					if (recordedDate.getPrefix() != null) {
						if (recordedDate.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE > '" + dateFormat + "'"));
						} else if (recordedDate.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE < '" + dateFormat + "'"));
						} else if (recordedDate.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE >= '" + dateFormat + "'"));
						} else if (recordedDate.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE <= '" + dateFormat + "'"));
						} else if (recordedDate.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE = '" + dateFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'recordedDate')::DATE = '" + dateFormat + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for AllergyIntolerance id
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
	 * This method builds criteria for AllergyIntolerance identifier
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
	 * This method builds criteria for fetching history of the AllergyIntolerance by
	 * id
	 * 
	 * @param theId : ID of the AllergyIntolerance
	 * @return : List of AllergyIntolerance DAF records
	 */
	@SuppressWarnings("unchecked")
	public List<DafAllergyIntolerance> getAllergyIntoleranceHistoryById(String theId) {
		Criteria criteria = getSession().createCriteria(DafAllergyIntolerance.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + theId + "'"));
		return (List<DafAllergyIntolerance>) criteria.list();
	}
}
