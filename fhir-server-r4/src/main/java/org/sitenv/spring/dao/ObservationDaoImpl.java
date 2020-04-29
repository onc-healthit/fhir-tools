package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.*;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("observationDao")
public class ObservationDaoImpl extends AbstractDao implements ObservationDao {

	/**
	 * This method builds criteria for fetching Observation record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the Observation
	 */
	@Override
	public DafObservation getObservationById(String id) {

		Criteria criteria = getSession().createCriteria(DafObservation.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafObservation) criteria.list().get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the
	 * Observation record by id.
	 * 
	 * @param theId     : ID of the Observation
	 * @param versionId : version of the Observation record
	 * @return : DAF object of the Observation
	 */
	@Override
	public DafObservation getObservationByVersionId(String theId, String versionId) {

		Criteria criteria = getSession().createCriteria(DafObservation.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" + versionId + "'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + theId + "'"));
		criteria.add(versionConjunction);
		return (DafObservation) criteria.uniqueResult();
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap : parameter for search
	 * @return criteria : DAF Observation object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafObservation> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafObservation.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);

		// build criteria for performer
		buildPerformerCriteria(theMap, criteria);

		// build criteria for basedOn
		buildBasedONCriteria(theMap, criteria);

		// build criteria for valueQuantity
		buildValueQuantityCriteria(theMap, criteria);

		// build criteria for derivedFrom
		buildDerivedFromCriteria(theMap, criteria);

		// build criteria for hasMember
		buildHasMemberCriteria(theMap, criteria);

		// build criteria for status
		buildStatusCriteria(theMap, criteria);

		// build criteria for subject
		buildSubjectCriteria(theMap, criteria);

		// build criteria for device
		buildDeviceCriteria(theMap, criteria);

		// build criteria for category
		buildCategoryCriteria(theMap, criteria);

		// build criteria for code
		buildCodeCriteria(theMap, criteria);

		// build criteria for partOf
		buildPartOfCriteria(theMap, criteria);
		
		// build criteria for patient
		buildPatientCriteria(theMap, criteria);

		// build criteria for effectiveDateTime
		buildEffectiveDateCriteria(theMap, criteria);

		return criteria.list();
	}

	/**
	 * This method builds criteria for observation partOf
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
						criterion = Restrictions.or(Restrictions.sqlRestriction(
								"{alias}.data->'partOf'->0->>'reference' ilike '%" + partOf.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'partOf'->1->>'reference' ilike '%" + partOf.getValue() + "%'")
								);

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
	 * This method builds criteria for observation code
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
								Restrictions
										.sqlRestriction("{alias}.data->'code'->'coding'->0->>'userSelected' ilike '%"
												+ code.getValue() + "%'"),

								Restrictions.sqlRestriction("{alias}.data->'code'->'coding'->1->>'system' ilike '%"
										+ code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->1->>'code' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'code'->'coding'->1->>'display' ilike '%"
										+ code.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'code'->'coding'->1->>'userSelected' ilike '%"
												+ code.getValue() + "%'"),

								Restrictions.sqlRestriction("{alias}.data->'code'->'coding'->2->>'system' ilike '%"
										+ code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->2->>'code' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'code'->'coding'->2->>'display' ilike '%"
										+ code.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'code'->'coding'->2->>'userSelected' ilike '%"
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
	 * This method builds criteria for observation category
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
								Restrictions
										.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'system' ilike '%"
												+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'code' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display' ilike '%"
												+ category.getValue() + "%'"),
										
										Restrictions
										.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'system' ilike '%"
												+ category.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'code' ilike '%"
										+ category.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'display' ilike '%"
												+ category.getValue() + "%'")
								
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
	 * This method builds criteria for observation device
	 * 
	 * @param theMap   : search parameter "device"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildDeviceCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("device");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam device = (ReferenceParam) params;
					Criterion criterion = null;
					if (device.getValue() != null) {
						criterion = Restrictions.or(Restrictions.sqlRestriction(
								"{alias}.data->'device'->>'reference' ilike '%" + device.getValue() + "%'"));

					} else if (device.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'device' IS NULL"));

					} else if (!device.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'device' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}

	}

	/**
	 * This method builds criteria for observation subject
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
	 * This method builds criteria for observation status
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
	 * This method builds criteria for observation hasMember
	 * 
	 * @param theMap   : search parameter "has-member"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildHasMemberCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("has-member");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam hasMember = (ReferenceParam) params;
					Criterion criterion = null;
					if (hasMember.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'hasMember'->0->>'reference' ilike '%"
										+ hasMember.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'hasMember'->0->>'display' ilike '%"
										+ hasMember.getValue() + "%'"),
								
								Restrictions.sqlRestriction("{alias}.data->'hasMember'->1->>'reference' ilike '%"
										+ hasMember.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'hasMember'->1->>'display' ilike '%"
										+ hasMember.getValue() + "%'"),
								
								Restrictions.sqlRestriction("{alias}.data->'hasMember'->2->>'reference' ilike '%"
										+ hasMember.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'hasMember'->2->>'display' ilike '%"
										+ hasMember.getValue() + "%'")
								);

					} else if (hasMember.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'hasMember' IS NULL"));

					} else if (!hasMember.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'hasMember' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}

	}

	/**
	 * This method builds criteria for observation derivedFrom
	 * 
	 * @param theMap   : search parameter "derived-from"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildDerivedFromCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("derived-from");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam derivedFrom = (ReferenceParam) params;
					Criterion criterion = null;
					if (derivedFrom.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'derivedFrom'->0->>'reference' ilike '%"
										+ derivedFrom.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'derivedFrom'->0->>'display' ilike '%"
										+ derivedFrom.getValue() + "%'"),
								
								Restrictions.sqlRestriction("{alias}.data->'derivedFrom'->1->>'reference' ilike '%"
										+ derivedFrom.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'derivedFrom'->1->>'display' ilike '%"
										+ derivedFrom.getValue() + "%'"),
								
								Restrictions.sqlRestriction("{alias}.data->'derivedFrom'->2->>'reference' ilike '%"
										+ derivedFrom.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'derivedFrom'->2->>'display' ilike '%"
										+ derivedFrom.getValue() + "%'")
								);

					} else if (derivedFrom.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'derivedFrom' IS NULL"));

					} else if (!derivedFrom.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'derivedFrom' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}

	}

	/**
	 * This method builds criteria for observation valueQuantity
	 * 
	 * @param theMap   : search parameter "value-quantity"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildValueQuantityCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("value-quantity");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					QuantityParam valueQuantity = (QuantityParam) params;
					Criterion criterion = null;
					if (valueQuantity.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'valueQuantity'->>'value' ilike '%"
										+ valueQuantity.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'valueQuantity'->>'system' ilike '%"
										+ valueQuantity.getSystem() + "%'"));
								
					}
					else if (valueQuantity.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'valueQuantity' IS NULL"));
					}
					else if (!valueQuantity.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'valueQuantity' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for observation basedOn
	 * 
	 * @param theMap   : search parameter "based-on"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildBasedONCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("based-on");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam basedOn = (ReferenceParam) params;
					Criterion criterion = null;
					if (basedOn.getValue() != null) {
						criterion = Restrictions.or(
									Restrictions
											.sqlRestriction("{alias}.data->'basedOn'->0->'identifier'->>'system' ilike '%"
													+ basedOn.getValue() + "%'"),
									Restrictions
											.sqlRestriction("{alias}.data->'basedOn'->0->'identifier'->>'value' ilike '%"
													+ basedOn.getValue() + "%'"),
											
									Restrictions
											.sqlRestriction("{alias}.data->'basedOn'->1->'identifier'->>'system' ilike '%"
													+ basedOn.getValue() + "%'"),
									Restrictions
											.sqlRestriction("{alias}.data->'basedOn'->1->'identifier'->>'value' ilike '%"
													+ basedOn.getValue() + "%'"),
																					
									Restrictions
											.sqlRestriction("{alias}.data->'basedOn'->2->'identifier'->>'system' ilike '%"
													+ basedOn.getValue() + "%'"),
									Restrictions
											.sqlRestriction("{alias}.data->'basedOn'->2->'identifier'->>'value' ilike '%"
													+ basedOn.getValue() + "%'")
																					
								);
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
	 * This method builds criteria for observation performer
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
								Restrictions.sqlRestriction("{alias}.data->'performer'->0->>'reference' ilike '%"
										+ performer.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'performer'->0->>'display' ilike '%"
										+ performer.getValue() + "%'"),
								
								Restrictions.sqlRestriction("{alias}.data->'performer'->1->>'reference' ilike '%"
										+ performer.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'performer'->1->>'display' ilike '%"
										+ performer.getValue() + "%'"),
								
								Restrictions.sqlRestriction("{alias}.data->'performer'->2->>'reference' ilike '%"
										+ performer.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'performer'->2->>'display' ilike '%"
										+ performer.getValue() + "%'")
								);

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
	 * This method builds criteria for Observation id
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
	 * This method builds criteria for Observation identifier
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
	 * This method builds criteria for observation patient
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
										"{alias}.data->'subject'->>'reference' ilike '%" + patient.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'display' ilike '%" + patient.getValue() + "%'"));

					} else if (patient.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'subject' IS NULL"));

					} else if (!patient.getMissing()) {
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
	 * This method builds criteria for DiagnosticReport date
	 *
	 * @param theMap   : search parameter "date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildEffectiveDateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam effectiveDate = (DateParam) params;
					String issuedFormat = effectiveDate.getValueAsString();
					if (effectiveDate.getPrefix() != null) {
						if (effectiveDate.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'effectiveDateTime')::DATE > '" + issuedFormat + "'"));
						} else if (effectiveDate.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'effectiveDateTime')::DATE < '" + issuedFormat + "'"));
						} else if (effectiveDate.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'effectiveDateTime')::DATE >= '" + issuedFormat + "'"));
						} else if (effectiveDate.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'effectiveDateTime')::DATE <= '" + issuedFormat + "'"));
						}else if (effectiveDate.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'effectiveDateTime')::DATE = '" + issuedFormat + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'effectiveDateTime')::DATE = '" + issuedFormat + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for fetching history of the Observation by id
	 * 
	 * @param theId : ID of the Observation
	 * @return : List of Observation DAF records
	 */
	@Override
	public List<DafObservation> getObservationHistoryById(String theId) {
		List<DafObservation> list = getSession().createNativeQuery(
    			"select * from observation where data->>'id' = '"+theId+"' order by data->'meta'->>'versionId' desc", DafObservation.class)
    				.getResultList();
		return list;
	}
}
