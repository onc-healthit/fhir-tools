package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.*;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafProcedure;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("procedureDao")
public class ProcedureDaoImpl extends AbstractDao implements ProcedureDao {

	/**
	 * This method builds criteria for fetching Procedure record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the Procedure
	 */
	@Override
	public DafProcedure getProcedureById(String id) {
		List<DafProcedure> list = getSession().createNativeQuery(
				"select * from procedure where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", DafProcedure.class)
					.getResultList();
			return list.get(0);	}

	/**
	 * This method builds criteria for fetching particular version of the Procedure
	 * record by id.
	 * 
	 * @param theId     : ID of the Procedure
	 * @param versionId : version of the Procedure record
	 * @return : DAF object of the Procedure
	 */
	public DafProcedure getProcedureByVersionId(String theId, String versionId) {

		DafProcedure list = getSession().createNativeQuery(
				"select * from procedure where data->>'id' = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", DafProcedure.class)
					.getSingleResult();
				return list;
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap : parameter for search
	 * @return criteria : DAF Procedure object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafProcedure> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafProcedure.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		   buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		   buildIdentifierCriteria(theMap, criteria);

		// build criteria for date -- used for performedPeriod
		   buildDateCriteria(theMap, criteria);
		
		// build criteria for occurancedate -- notused
		//   buildOccurrenceDateTime(theMap, criteria);
		
		// build criteria for period
		   buildPerformedPeriodCriteria(theMap, criteria);

		// build criteria for string
		   buildPerformedStringCriteria(theMap, criteria);

		// build criteria for age
		   buildPerformedAgeCriteria(theMap, criteria);

		// build criteria for Range
		   buildRangeCriteria(theMap, criteria);

		// build criteria for code
		   buildCodeCriteria(theMap, criteria);

		// build criteria for performer
		   buildPerformerCriteria(theMap, criteria);

		// build criteria for subject
		   buildSubjectCriteria(theMap, criteria);

		// build criteria for instantiatesCanonical
		   buildInstantiatesCanonicalCriteria(theMap, criteria);

		// build criteria for partOf
		   buildPartOfCriteria(theMap, criteria);

		// build criteria for encounter
		   buildEncounterCriteria(theMap, criteria);

		// build criteria for reasonCode
		   buildReasonCodeCriteria(theMap, criteria);

		// build criteria for basedOn
		   buildBasedOnCriteria(theMap, criteria);

		// build criteria for patient
		   buildPatientCriteria(theMap, criteria);

		// build criteria for reasonReference
		buildReasonReferenceCriteria(theMap, criteria);

		// build criteria for Location
		   buildLocationCriteria(theMap, criteria);

		// build criteria for instantiatesUri
		   buildInstantiatesUriCriteria(theMap, criteria);

		// build criteria for category
		   buildCategoryCriteria(theMap, criteria);

		// build criteria for status
		   buildStatusCriteria(theMap, criteria);

		return criteria.list();
	}

	/**
	 * This method builds criteria for procedure reasonReference
	 * 
	 * @param theMap   : search parameter "reason-reference"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildReasonReferenceCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("reason-reference");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam reasonReference = (ReferenceParam) params;
					Criterion criterion = null;
					if (reasonReference.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'reasonReference'->0->>'reference' ilike '%"
										+ reasonReference.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'reasonReference'->0->>'display' ilike '%"
										+ reasonReference.getValue() + "%'"),

								Restrictions.sqlRestriction("{alias}.data->'reasonReference'->1->>'reference' ilike '%"
										+ reasonReference.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'reasonReference'->1->>'display' ilike '%"
										+ reasonReference.getValue() + "%'"));

					} else if (reasonReference.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'reasonReference' IS NULL"));

					} else if (!reasonReference.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'reasonReference' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for procedure location
	 * 
	 * @param theMap   : search parameter "location"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildLocationCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("location");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam location = (ReferenceParam) params;
					Criterion criterion = null;
					if (location.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'location'->>'reference' ilike '%" + location.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'location'->>'display' ilike '%" + location.getValue() + "%'"));

					} else if (location.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'location' IS NULL"));

					} else if (!location.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'location' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}

				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for instantiatesUri: Instantiates external
	 * protocol or definition
	 * 
	 * @param theMap   : search parameter "instantiates-uri"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildInstantiatesUriCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("instantiates-uri");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					UriParam instantiatesUri = (UriParam) params;
					Criterion orCond = null;
					if (instantiatesUri.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>0 ilike '%"
										+ instantiatesUri.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>1 ilike '%"
										+ instantiatesUri.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>2 ilike '%"
										+ instantiatesUri.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>3 ilike '%"
										+ instantiatesUri.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>4 ilike '%"
										+ instantiatesUri.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>5 ilike '%"
										+ instantiatesUri.getValue() + "%'"));
					} else if (instantiatesUri.getMissing()) {
						orCond = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->'instantiatesUri' IS NULL"));
					} else if (!instantiatesUri.getMissing()) {
						orCond = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->'instantiatesUri' IS NOT NULL"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for procedure status
	 * 
	 * @param theMap   : search parameter "status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("status");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					TokenParam status = (TokenParam) params;
					if (!status.isEmpty()) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'status' ilike '" + status.getValue() + "'"));
					} else if (status.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' IS NULL"));
					} else if (!status.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' IS NOT NULL"));
					}

				}
			}
		}
	}

	/**
	 * This method builds criteria for procedure category
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
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->0->>'system' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->0->>'code' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->'coding'->0->>'display' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'category'->>'text' ilike '%" + category.getValue() + "%'"));
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
	 * This method builds criteria for procedure patient
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
						if (subject.getValue().contains("Patient/")) {
							criterion = Restrictions.or(
									Restrictions.sqlRestriction(
											"{alias}.data->'subject'->>'reference' = '" + subject.getValue() + "'")
//									Restrictions.sqlRestriction(
//											"{alias}.data->'subject'->>'display' = '" + subject.getValue() + "'")
									);
						}else
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'reference' = '"+"Patient/" + subject.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'display' = '" + subject.getValue() + "'"));

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
	 * This method builds criteria for procedure basedOn
	 * 
	 * @param theMap   : search parameter "based-on"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildBasedOnCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("based-on");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam basedOn = (ReferenceParam) params;
					Criterion criterion = null;
					if (basedOn.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'basedOn'->0->>'reference' ilike '%"
										+ basedOn.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'basedOn'->0->>'display' ilike '%" + basedOn.getValue() + "%'"));

					} else if (basedOn.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'basedOn' IS NULL"));

					} else if (!basedOn.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'basedOn' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for procedure reasonCode
	 * 
	 * @param theMap   : search parameter "reason-code"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildReasonCodeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("reason-code");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					TokenParam reasonCode = (TokenParam) params;
					if (!reasonCode.isEmpty()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'reasonCode'->0->>'text' ilike '%" + reasonCode.getValue() + "%'"));
					} else if (reasonCode.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'reasonCode' IS NULL"));
					} else if (!reasonCode.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'reasonCode' IS NOT NULL"));
					}

				}
			}
		}
	}

	/**
	 * This method builds criteria for procedure encounter
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
					Criterion criterion = null;
					if (encounter.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference' ilike '%"
										+ encounter.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'encounter'->>'display' ilike '%"
										+ encounter.getValue() + "%'"));

					} else if (encounter.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'encounter' IS NULL"));

					} else if (!encounter.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'encounter' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for procedure partOf
	 * 
	 * @param theMap   : search parameter "part-of"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPartOfCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("part-of");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam partOf = (ReferenceParam) params;
					Criterion criterion = null;
					if (partOf.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->0->>'reference' ilike '%" + partOf.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->0->>'display' ilike '%" + partOf.getValue() + "%'"));

					} else if (partOf.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'partOf' IS NULL"));

					} else if (!partOf.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'partOf' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for procedure instantiatesCanonical
	 * 
	 * @param theMap   : search parameter "instantiates-canonical"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildInstantiatesCanonicalCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("instantiates-canonical");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					ReferenceParam instantiatesCanonical = (ReferenceParam) params;
					if (instantiatesCanonical.getValue() != null) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->'instantiatesCanonical'->>0 ilike '%"
								+ instantiatesCanonical.getValue() + "%'"));

					} else if (instantiatesCanonical.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'instantiatesCanonical' IS NULL"));

					} else if (!instantiatesCanonical.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'instantiatesCanonical' IS NOT NULL"));

					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for procedure subject
	 * 
	 * @param theMap   : search parameter "subject"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSubjectCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("subject");
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
	 * This method builds criteria for procedure performer
	 * 
	 * @param theMap   : search parameter "performer"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPerformerCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("performer");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam performer = (ReferenceParam) params;
					Criterion criterion = null;
					if (performer.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'performer'->0->'function'->'coding'->0->>'system' ilike '%"
												+ performer.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'performer'->0->'function'->'coding'->0->>'code' ilike '%"
												+ performer.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'performer'->0->'function'->'coding'->0->>'display' ilike '%"
												+ performer.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'performer'->0->'function'->>'text' ilike '%"
										+ performer.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'performer'->0->>'actor' ilike '%"
										+ performer.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'performer'->0->'actor'->>'reference' ilike '%"
												+ performer.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'performer'->0->'actor'->>'display' ilike '%"
										+ performer.getValue() + "%'"));

					} else if (performer.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'performer' IS NULL"));

					} else if (!performer.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'performer' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for procedure code
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
								Restrictions.sqlRestriction("{alias}.data->'code'->'coding'->0->>'system' ilike '%"
										+ code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->0->>'code' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'code'->'coding'->0->>'display' ilike '%"
										+ code.getValue() + "%'"),
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
	 * This method builds criteria for procedure date
	 * 
	 * @param theMap   : search parameter "date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildDateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam date = (DateParam) params;
					String dateFormat = date.getValueAsString();
					if (date.getPrefix() != null) {
						if (date.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'performedPeriod'->>'start')::DATE > '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'performedPeriod'->>'start')::DATE < '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'performedPeriod'->>'start')::DATE >= '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'performedPeriod'->>'start')::DATE <= '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "ne") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'performedPeriod'->>'start')::DATE != '" + dateFormat + "'"));
						}else if (date.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'performedPeriod'->>'start')::DATE = '" + dateFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->'performedPeriod'->>'start')::DATE = '" + dateFormat + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for Procedure id
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
	 * This method builds criteria for Procedure identifier
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
					if (identifier.getValue() != null && identifier.getSystem() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '"
										+ identifier.getSystem() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '"
										+ identifier.getSystem() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '"
										+ identifier.getValue() + "%'"));
					} else if (identifier.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '%"
										+ identifier.getValue() + "%'"));
					} else if (identifier.getSystem() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '%"
										+ identifier.getSystem() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '%"
										+ identifier.getSystem() + "%'"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for fetching history of the Procedure by id
	 * 
	 * @param theId : ID of the Procedure
	 * @return : List of Procedure DAF records
	 */
	@Override
	public List<DafProcedure> getProcedureHistoryById(String theId) {
		List<DafProcedure> list = getSession().createNativeQuery(
				"select * from procedure where data->>'id' = '" + theId + "' order by data->'meta'->>'versionId' desc",
				DafProcedure.class).getResultList();
		return list;
	}
	
	/**
	 * This method builds criteria for procedure OccurrenceDateTime
	 * 
	 * @param theMap   : search parameter "OccurrenceDateTime"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildOccurrenceDateTime(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam date = (DateParam) params;
					String dateFormat = date.getValueAsString();
					if (date.getPrefix() != null) {
						if (date.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE > '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE < '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE >= '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE <= '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "ne") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE != '" + dateFormat + "'"));
						}else if (date.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE = '" + dateFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE = '" + dateFormat + "'"));
					}
				}
			}
		}
	}
	
	/**
	 * This method builds criteria for procedure performedPeriod
	 * 
	 * @param theMap   : search parameter "performedPeriod"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPerformedPeriodCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("performedPeriod");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					QuantityParam performedPeriod = (QuantityParam) params;
					if (performedPeriod.getValue() != null) {
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performedPeriod'->>'start' ilike '%"
										+ performedPeriod.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'performedPeriod'->>'end' ilike '%"
										+ performedPeriod.getValue() + "%'"));
					}
				}
				criteria.add(disjunction);
			}
		}

	}
	
	/**
	 * This method builds criteria for procedure performedString
	 * 
	 * @param theMap   : search parameter "performedString"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPerformedStringCriteria(SearchParameterMap theMap, Criteria criteria) {
		
			List<List<? extends IQueryParameterType>> list = theMap.get("performedString");
			if (list != null) {
				for (List<? extends IQueryParameterType> values : list) {
					for (IQueryParameterType params : values) {
						StringParam performedString = (StringParam) params;
						if (performedString.getValue() != null) {
							if (performedString.getValue() == "gt") {
								criteria.add(Restrictions
										.sqlRestriction("{alias}.data->>'performedString' > '" + performedString + "'"));
							} else if (performedString.getValue() == "lt") {
								criteria.add(Restrictions
										.sqlRestriction("{alias}.data->>'performedString' < '" + performedString + "'"));
							} else if (performedString.getValue() == "ge") {
								criteria.add(Restrictions
										.sqlRestriction("{alias}.data->>'performedString' >= '" + performedString + "'"));
							} else if (performedString.getValue() == "le") {
								criteria.add(Restrictions
										.sqlRestriction("{alias}.data->>'performedString' <= '" + performedString + "'"));
							} else if (performedString.getValue() == "ne") {
								criteria.add(Restrictions
										.sqlRestriction("{alias}.data->>'performedString' != '" + performedString + "'"));
							} else {
								criteria.add(Restrictions
										.sqlRestriction("{alias}.data->>'performedString' = '" + performedString + "'"));
							}
						}
					}
				}
			}
		}

	/**
	 * This method builds criteria query for performedAge
	 * @param theMap search parameter
	 * @param criteria
	 */
	private void buildPerformedAgeCriteria(SearchParameterMap theMap, Criteria criteria) {
		
			List<List<? extends IQueryParameterType>> list = theMap.get("performedAge");
			if (list != null) {
				for (List<? extends IQueryParameterType> values : list) {
					Disjunction disjunction = Restrictions.disjunction();
					for (IQueryParameterType params : values) {
						QuantityParam performedAge = (QuantityParam) params;
						if (performedAge.getValue() != null) {
							disjunction.add(Restrictions
									.sqlRestriction("{alias}.data->'performedAge'->>'system' ilike '%"
											+ performedAge.getValue() + "%'"));
							disjunction.add(
									Restrictions.sqlRestriction("{alias}.data->'performedAge'->>'value' ilike '%"
											+ performedAge.getValue() + "%'"));
							disjunction.add(Restrictions
									.sqlRestriction("{alias}.data->'performedAge'->>'unit' ilike '%"
											+ performedAge.getValue() + "%'"));
						}
					}
					criteria.add(disjunction);
				}
			}
		}

	/**
	 * This method builds criteria query for performedRange
	 * @param theMap search parameter
	 * @param criteria
	 */
	private void buildRangeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("performedRange");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					QuantityParam performedRange = (QuantityParam) params;
					if (performedRange.getValue() != null) {
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performedRange'->>'low' ilike '%"
										+ performedRange.getValue() + "%'"));
						disjunction.add(
								Restrictions.sqlRestriction("{alias}.data->'performedRange'->>'high' ilike '%"
										+ performedRange.getValue() + "%'"));
						
					}
				}
				criteria.add(disjunction);
			}
		}

	}

}
