package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafServiceRequest;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("serviceRequestDao")
public class ServiceRequestDaoImpl extends AbstractDao implements ServiceRequestDao {

	/**
	 * This method builds criteria for fetching serviceRequest record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the serviceRequest
	 */
	public DafServiceRequest getServiceRequestById(String id) {
		Criteria criteria = getSession().createCriteria(DafServiceRequest.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafServiceRequest) criteria.list().get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the
	 * serviceRequest record by id.
	 * 
	 * @param theId     : ID of the serviceRequest
	 * @param versionId : version of the serviceRequest record
	 * @return : DAF object of the serviceRequest
	 */
	@Override
	public DafServiceRequest getServiceRequestByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafServiceRequest.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" + versionId + "'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + theId + "'"));
		criteria.add(versionConjunction);
		return (DafServiceRequest) criteria.uniqueResult();
	}

	/**
	 * This method builds criteria for fetching history of the serviceRequest by id
	 * 
	 * @param theId : ID of the serviceRequest
	 * @return : List of serviceRequest DAF records
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafServiceRequest> getServiceRequestHistoryById(String id) {
		Criteria criteria = getSession().createCriteria(DafServiceRequest.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + id + "'"));
		return (List<DafServiceRequest>) criteria.list();
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap : parameter for search
	 * @return criteria : DAF organization object
	 */
	@SuppressWarnings("unchecked")
	public List<DafServiceRequest> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafServiceRequest.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for status
		buildStatusCriteria(theMap, criteria);

		// build criteria for encounter
		buildEncounterCriteria(theMap, criteria);

		// build criteria for subject
		buildSubjectCriteria(theMap, criteria);

		// build criteria for requester
		buildRequesterCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);

		// build criteria for code
		buildCodeCriteria(theMap, criteria);

		// build criteria for specimen
		buildSpecimenCriteria(theMap, criteria);

		// build criteria for performer
		buildPerformerCriteria(theMap, criteria);

		// build criteria for requisition
		buildRequisitionCriteria(theMap, criteria);

		// build criteria for category
		buildCategoryCriteria(theMap, criteria);

		// build criteria for authoredOn
		buildAuthoredOnCriteria(theMap, criteria);

		// build criteria for intent
		buildIntentCriteria(theMap, criteria);

		// build criteria for bodySite
		buildBodySiteCriteria(theMap, criteria);

		// build criteria for occurrenceDateTime
		buildOccurenceDateTimeCriteria(theMap, criteria);

		// build criteria for basedOn
		buildBasedOnCriteria(theMap, criteria);

		// build criteria for reasonCode
		buildReplacesCriteria(theMap, criteria);

		// build criteria for priority
		buildPriorityCriteria(theMap, criteria);

		// build criteria for performance-type
		buildPerformerTypeCriteria(theMap, criteria);

		// build criteria for instantiates-uri
		buildInstantiatesUriCriteria(theMap, criteria);

		return criteria.list();
	}

	/**
	 * This method builds criteria for serviceRequest id
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
	 * This method builds criteria for serviceRequest identifier
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
	 * This method builds criteria for serviceRequest status
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
					if (status.getModifier() != null) {
						TokenParamModifier modifier = status.getModifier();
						if (modifier.getValue() == ":not") {
							criteria.add(Restrictions
									.sqlRestriction("{alias}.data->>'status' not ilike '" + status.getValue() + "'"));
						}
					} else if (StringUtils.isNoneEmpty(status.getValue())) {
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
	 * This method builds criteria for seviceRequest intent
	 * 
	 * @param theMap   : search parameter "intent"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */

	private void buildIntentCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("intent");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					TokenParam intent = (TokenParam) params;
					if (intent.getModifier() != null) {
						TokenParamModifier modifier = intent.getModifier();
						if (modifier.getValue() == ":not") {
							criteria.add(Restrictions
									.sqlRestriction("{alias}.data->>'intent' not ilike '" + intent.getValue() + "'"));
						}
					} else if (StringUtils.isNoneEmpty(intent.getValue())) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'intent' ilike '%" + intent.getValue() + "%'"));
					} else if (intent.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'intent' IS NULL"));
					} else if (!intent.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'intent' IS NOT NULL"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for serviceRequest priority
	 * 
	 * @param theMap   : search parameter "priority"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */

	private void buildPriorityCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("priority");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					TokenParam priority = (TokenParam) params;
					if (priority.getModifier() != null) {
						TokenParamModifier modifier = priority.getModifier();
						if (modifier.getValue() == ":not") {
							criteria.add(Restrictions.sqlRestriction(
									"{alias}.data->>'priority' not ilike '" + priority.getValue() + "'"));
						}
					} else if (StringUtils.isNoneEmpty(priority.getValue())) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'priority' ilike '%" + priority.getValue() + "%'"));
					} else if (priority.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'priority' IS NULL"));
					} else if (!priority.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'priority' IS NOT NULL"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for serviceRequest encounter
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
										+ encounter.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'encounter'->>'type' ilike '%"
										+ encounter.getValue() + "%'")
								);
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
	 * This method builds criteria for serviceRequest subject
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
	 * This method builds criteria for serviceRequest requisition
	 * 
	 * @param theMap   : search parameter "requisition"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildRequisitionCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("requisition");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam requisition = (TokenParam) params;
					Criterion orCond = null;
					if (requisition.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'requisition'->>'system' ilike '%"
										+ requisition.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'requisition'->>'value' ilike '%"
										+ requisition.getValue() + "%'"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for serviceRequest requester
	 * 
	 * @param theMap   : search parameter "requester"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildRequesterCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("requester");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam requester = (ReferenceParam) params;
					Criterion orCond = null;
					if (requester.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'requester'->>'reference' ilike '%"
										+ requester.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'requester'->>'display' ilike '%" + requester.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'requester'->>'type' ilike '%" + requester.getValue() + "%'"));
					} else if (requester.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'requester' IS NULL"));
					} else if (!requester.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'requester' IS NOT NULL"));
					}

					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for serviceRequest code
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
					if (code.getValue() != null) {
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'code'->'coding'->0->>'system' ilike '%" + code.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'code'->'coding'->0->>'code' ilike '%" + code.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'code'->'coding'->0->>'display' ilike '%" + code.getValue() + "%'"));
						/*
						 * disjunction.add(Restrictions.sqlRestriction(
						 * "{alias}.data->'code'->0->>'text' ilike '%" + code.getValue() + "%'"));
						 */
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'code'->'coding'->1->>'system' ilike '%" + code.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'code'->'coding'->1->>'code' ilike '%" + code.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'code'->'coding'->1->>'display' ilike '%" + code.getValue() + "%'"));
					}
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for service-request specimen
	 * 
	 * @param theMap   : search parameter "specimen"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSpecimenCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("specimen");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam specimen = (ReferenceParam) params;
					Criterion orCond = null;
					if (specimen.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'specimen'->0->>'reference' ilike '%"
										+ specimen.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'specimen'->0->>'type' ilike '%" + specimen.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'specimen'->0->>'display' ilike '%"
										+ specimen.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'specimen'->1->>'reference' ilike '%"
										+ specimen.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'specimen'->1->>'type' ilike '%" + specimen.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'specimen'->1->>'display' ilike '%"
										+ specimen.getValue() + "%'")
								);
					} else if (specimen.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'specimen' IS NULL"));
					} else if (!specimen.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'specimen' IS NOT NULL"));
					}

					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for service-request performer
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
					Criterion orCond = null;
					if (performer.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'performer'->0->>'reference' ilike '%"
										+ performer.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'performer'->0->>'type' ilike '%" + performer.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'performer'->0->>'display' ilike '%"
										+ performer.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'performer'->1->>'reference' ilike '%"
										+ performer.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'performer'->1->>'type' ilike '%" + performer.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'performer'->1->>'display' ilike '%"
										+ performer.getValue() + "%'")
								);
					} else if (performer.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'performer' IS NULL"));
					} else if (!performer.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'performer' IS NOT NULL"));
					}

					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for serviceRequest category
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
	 * This method builds criteria for serviceRequest authored
	 * 
	 * @param theMap   : search parameter "authored"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAuthoredOnCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("authored");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam authored = (DateParam) params;
					String authoredFormat = authored.getValueAsString();
					if (authored.getPrefix() != null) {
						if (authored.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'authoredOn')::DATE > '" + authoredFormat + "'"));
						} else if (authored.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'authoredOn')::DATE < '" + authoredFormat + "'"));
						} else if (authored.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'authoredOn')::DATE >= '" + authoredFormat + "'"));
						} else if (authored.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'authoredOn')::DATE <= '" + authoredFormat + "'"));
						}else if (authored.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'authoredOn')::DATE = '" + authoredFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'authoredOn')::DATE = '" + authoredFormat + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for serviceRequest occurrence
	 * 
	 * @param theMap   : search parameter "occurence"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildOccurenceDateTimeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("occurrence");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam occurrence = (DateParam) params;
					String occurenceFormat = occurrence.getValueAsString();
					if (occurrence.getPrefix() != null) {
						if (occurrence.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE > '" + occurenceFormat + "'"));
						} else if (occurrence.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE < '" + occurenceFormat + "'"));
						} else if (occurrence.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE >= '" + occurenceFormat + "'"));
						} else if (occurrence.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE <= '" + occurenceFormat + "'"));
						}else if (occurrence.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE = '" + occurenceFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE = '" + occurenceFormat + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for serviceRequest bodySite
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
	 * This method builds criteria for service-request based-on
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
					Criterion orCond = null;
					if (basedOn.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'basedOn'->0->>'reference' ilike '%"
										+ basedOn.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'basedOn'->0->>'type' ilike '%" + basedOn.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'basedOn'->0->>'display' ilike '%" + basedOn.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'basedOn'->1->>'reference' ilike '%"
										+ basedOn.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'basedOn'->1->>'type' ilike '%" + basedOn.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'basedOn'->1->>'display' ilike '%" + basedOn.getValue() + "%'")
								);
					} else if (basedOn.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'basedOn' IS NULL"));
					} else if (!basedOn.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'basedOn' IS NOT NULL"));
					}

					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for service-request replaces
	 * 
	 * @param theMap   : search parameter "replaces"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildReplacesCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("replaces");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam replaces = (ReferenceParam) params;
					Criterion orCond = null;
					if (replaces.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'replaces'->0->>'reference' ilike '%"
										+ replaces.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'replaces'->0->>'type' ilike '%" + replaces.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'replaces'->0->>'display' ilike '%"
										+ replaces.getValue() + "%'"),
								
								Restrictions.sqlRestriction("{alias}.data->'replaces'->1->>'reference' ilike '%"
										+ replaces.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'replaces'->1->>'type' ilike '%" + replaces.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'replaces'->1->>'display' ilike '%"
										+ replaces.getValue() + "%'")
								);
					} else if (replaces.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'replaces' IS NULL"));
					} else if (!replaces.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'replaces' IS NOT NULL"));
					}

					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for serviceRequest performer-type
	 * 
	 * @param theMap   : search parameter "performer-type"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPerformerTypeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("performer-type");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam performerType = (TokenParam) params;
					if (performerType.getValue() != null) {
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'system' ilike '"
										+ performerType.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'code' ilike '"
										+ performerType.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'display' ilike '"
										+ performerType.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->1->>'system' ilike '"
										+ performerType.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->1->>'code' ilike '"
										+ performerType.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->1->>'display' ilike '"
										+ performerType.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->2->>'system' ilike '"
										+ performerType.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->2->>'code' ilike '"
										+ performerType.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->2->>'display' ilike '"
										+ performerType.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->3->>'system' ilike '"
										+ performerType.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->3->>'code' ilike '"
										+ performerType.getValue() + "%'"));
						disjunction.add(Restrictions
								.sqlRestriction("{alias}.data->'performerType'->'coding'->3->>'display' ilike '"
										+ performerType.getValue() + "%'"));
					}
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
}
