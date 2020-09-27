package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ImmunizationDao")
public class ImmunizationDaoImpl extends AbstractDao implements ImmunizationDao {

	/**
	 * This method builds criteria for fetching Immunization record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the Immunization
	 */
	public DafImmunization getImmunizationById(String id) {
		List<DafImmunization> list = getSession().createNativeQuery(
				"select * from immunization where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", DafImmunization.class)
					.getResultList();
		return list.get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the
	 * Immunization record by id.
	 * 
	 * @param theId     : ID of the Immunization
	 * @param versionId : version of the Immunization record
	 * @return : DAF object of the Immunization
	 */
	public DafImmunization getImmunizationByVersionId(String theId, String versionId) {
		DafImmunization list = getSession().createNativeQuery(
			"select * from immunization where data->>'id' = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", DafImmunization.class)
				.getSingleResult();
		return list;
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap : parameter for search
	 * @return criteria : DAF Immunization object
	 */
	@SuppressWarnings("unchecked")
	public List<DafImmunization> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafImmunization.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);

		// build criteria for Date
		buildDateCriteria(theMap, criteria);

		// build criteria for Performer
		buildPerformerCriteria(theMap, criteria);

		// build criteria for Reaction
		buildReactionCriteria(theMap, criteria);

		// build criteria for LotNumber
		buildLotNumberCriteria(theMap, criteria);

		// build criteria for StatusReason
		buildStatusReasonCriteria(theMap, criteria);

		// build criteria for ReasonCode
		buildReasonCodeCriteria(theMap, criteria);

		// build criteria for Manufacturer
		buildManufacturerCriteria(theMap, criteria);

		// build criteria for TargetDisease
		buildTargetDiseaseCriteria(theMap, criteria);

		// build criteria for Patient
		buildPatientCriteria(theMap, criteria);

		// build criteria for Series
		buildSeriesCriteria(theMap, criteria);

		// build criteria for VaccineCode
		buildVaccineCodeCriteria(theMap, criteria);

		// build criteria for ReasonReference
		buildReasonReferenceCriteria(theMap, criteria);

		// build criteria for Location
		buildLocationCriteria(theMap, criteria);

		// build criteria for Status
		buildStatusCriteria(theMap, criteria);

		// build criteria for ReactionDate
		buildReactionDateCriteria(theMap, criteria);

		return criteria.list();
	}

	/**
	 * This method builds criteria for immunization reaction-date
	 * 
	 * @param theMap   : search parameter "reaction-date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildReactionDateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("reaction-date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam reactionDate = (DateParam) params;
					String reactionDateFormat = reactionDate.getValueAsString();

					if (reactionDate.getPrefix().getValue() == "eq") {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'reactionDate')::DATE = '" + reactionDateFormat + "'"));
					} else if (reactionDate.getPrefix().getValue() == "gt") {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'reactionDate')::DATE > '" + reactionDateFormat + "'"));
					} else if (reactionDate.getPrefix().getValue() == "lt") {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'reactionDate')::DATE < '" + reactionDateFormat + "'"));
					} else if (reactionDate.getPrefix().getValue() == "ge") {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'reactionDate')::DATE >= '" + reactionDateFormat + "'"));
					} else if (reactionDate.getPrefix().getValue() == "le") {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'reactionDate')::DATE <= '" + reactionDateFormat + "'"));
					} 
				}

			}
		}
	}

	/**
	 * This method builds criteria for immunization status
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
	 * This method builds criteria for immunization location
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
										"{alias}.data->'location'->>'display' ilike '%" + location.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'location'->>'type' ilike '%" + location.getValue() + "%'")
								);

					} else if (location.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'location' IS NULL"));

					} else if (!location.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'location' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for immunization reasonReference
	 * 
	 * @param theMap   : search parameter "reason-reference"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildReasonReferenceCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("reason-reference");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction =Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam reasonReference = (ReferenceParam) params;
					Criterion criterion = null;
					if (reasonReference.getValue() != null) {
						criterion = Restrictions.or(Restrictions.sqlRestriction(
								"{alias}.data->'reasonReference' ilike '%" + reasonReference.getValue() + "%'"));

					} else if (reasonReference.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'reasonReference' IS NULL"));

					} else if (!reasonReference.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'reasonReference' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for immunization vaccineCode
	 * 
	 * @param theMap   : search parameter "vaccine-code"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildVaccineCodeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("vaccine-code");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam vaccineCode = (TokenParam) params;
					Criterion criterion = null;
					if (!vaccineCode.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'vaccineCode'->'coding'->0->>'system' ilike '%"
										+ vaccineCode.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'vaccineCode'->'coding'->0->>'code' ilike '%"
										+ vaccineCode.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'vaccineCode'->>'text' ilike '%"
										+ vaccineCode.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'vaccineCode'->'coding'->1->>'system' ilike '%"
										+ vaccineCode.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'vaccineCode'->'coding'->1->>'code' ilike '%"
										+ vaccineCode.getValue() + "%'")
								);
					} else if (vaccineCode.getMissing()) {
						criterion = Restrictions.or(
								Restrictions
										.sqlRestriction("{alias}.data->>'vaccineCode' IS NULL"));
					} else if (!vaccineCode.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'vaccineCode' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}

	}

	/**
	 * This method builds criteria for immunization series
	 * 
	 * @param theMap   : search parameter "series"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSeriesCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("series");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam series = (StringParam) params;
					if (series.isExact()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'protocolApplied'->0->>'series' = '" + series.getValue() + "'"));
					} else if (series.isContains()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'protocolApplied'->0->>'series' ilike '%" + series.getValue() + "%'"));
					} else {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'protocolApplied'->0->>'series' ilike '" + series.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for immunization patient
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
						if (patient.getValue().contains("Patient/")) {
							criterion = Restrictions.or(
									Restrictions.sqlRestriction(
											"{alias}.data->'patient'->>'reference' = '"+patient.getValue()+"'")
							);	
						}else
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'reference' = '"+"Patient/"+patient.getValue()+"'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'display' = '" + patient.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'type' = '" + patient.getValue() + "'")
						);
					} else if (patient.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'patient' IS NULL"));

					} else if (!patient.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'patient' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}
	/**
	 * This method builds criteria for immunization target-disease
	 * 
	 * @param theMap   : search parameter "target-disease"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildTargetDiseaseCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("target-disease");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam targetDisease = (TokenParam) params;
					Criterion criterion = null;
					if (!targetDisease.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->0->'targetDisease'->0->'coding'->0->>'system' ilike '%"
										+ targetDisease.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->0->'targetDisease'->0->'coding'->0->>'code' ilike '%"
												+ targetDisease.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->0->'targetDisease'->0->'coding'->1->>'system' ilike '%"
										+ targetDisease.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->0->'targetDisease'->0->'coding'->1->>'code' ilike '%"
												+ targetDisease.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->0->'targetDisease'->1->'coding'->0->>'system' ilike '%"
										+ targetDisease.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->0->'targetDisease'->1->'coding'->0->>'code' ilike '%"
												+ targetDisease.getValue() + "%'"),
								
								
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->0->'targetDisease'->1->'coding'->1->>'system' ilike '%"
										+ targetDisease.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->0->'targetDisease'->1->'coding'->1->>'code' ilike '%"
												+ targetDisease.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->1->'targetDisease'->0->'coding'->0->>'system' ilike '%"
										+ targetDisease.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->1->'targetDisease'->0->'coding'->0->>'code' ilike '%"
												+ targetDisease.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->1->'targetDisease'->0->'coding'->1->>'system' ilike '%"
										+ targetDisease.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->1->'targetDisease'->0->'coding'->1->>'code' ilike '%"
												+ targetDisease.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->1->'targetDisease'->1->'coding'->0->>'system' ilike '%"
										+ targetDisease.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->1->'targetDisease'->1->'coding'->0->>'code' ilike '%"
												+ targetDisease.getValue() + "%'"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->1->'targetDisease'->1->'coding'->1->>'system' ilike '%"
										+ targetDisease.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'protocolApplied'->1->'targetDisease'->1->'coding'->1->>'code' ilike '%"
												+ targetDisease.getValue() + "%'")
								);
					} 
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}

	}

	/**
	 * This method builds criteria for immunization manufacturer
	 * 
	 * @param theMap   : search parameter "manufacturer"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildManufacturerCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("manufacturer");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam manufacturer = (ReferenceParam) params;
					Criterion criterion = null;
					if (manufacturer.getValue() != null) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->'manufacturer'->>'reference' ilike '%"
								+ manufacturer.getValue() + "%'"));

					} else if (manufacturer.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'manufacturer' IS NULL"));

					} else if (!manufacturer.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'manufacturer' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for immunization reasonCode
	 * 
	 * @param theMap   : search parameter "reason-code"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildReasonCodeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("reason-code");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam reasonCode = (TokenParam) params;
					Criterion criterion = null;
					if (!reasonCode.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions
										.sqlRestriction("{alias}.data->'reasonCode'->0->'coding'->0->>'system' ilike '%"
												+ reasonCode.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'reasonCode'->0->'coding'->0->>'code' ilike '%"
												+ reasonCode.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'reasonCode'->1->'coding'->0->>'system' ilike '%"
												+ reasonCode.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'reasonCode'->1->'coding'->0->>'code' ilike '%"
												+ reasonCode.getValue() + "%'")
										);
					} else if (reasonCode.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'reasonCode' IS NULL"));
					} else if (!reasonCode.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'reasonCode' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}

	}

	/**
	 * This method builds criteria for immunization statusReason
	 * 
	 * @param theMap   : search parameter "status-reason"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildStatusReasonCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("status-reason");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam statusReason = (TokenParam) params;
					Criterion criterion = null;
					if (!statusReason.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions
										.sqlRestriction("{alias}.data->'statusReason'->'coding'->0->>'system' ilike '%"
												+ statusReason.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'statusReason'->'coding'->0->>'code' ilike '%"
												+ statusReason.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'statusReason'->'coding'->0->>'display' ilike '%"
												+ statusReason.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'statusReason'->'coding'->1->>'system' ilike '%"
												+ statusReason.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'statusReason'->'coding'->1->>'code' ilike '%"
												+ statusReason.getValue() + "%'"),
								Restrictions
										.sqlRestriction("{alias}.data->'statusReason'->'coding'->1->>'display' ilike '%"
												+ statusReason.getValue() + "%'")
								
								);
					} else if (statusReason.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'statusReason' IS NULL"));
					} else if (!statusReason.getMissing()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->>'statusReason' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}

	}

	/**
	 * This method builds criteria for immunization lotNumber
	 * 
	 * @param theMap   : search parameter "lot-number"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildLotNumberCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("lot-number");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam lotNumber = (StringParam) params;
					if (lotNumber.isExact()) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'lotNumber' = '" + lotNumber.getValue() + "'"));
					} else if (lotNumber.isContains()) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'lotNumber' ilike '%" + lotNumber.getValue() + "%'"));
					} else {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'lotNumber' ilike '" + lotNumber.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for immunization reaction
	 * 
	 * @param theMap   : search parameter "reaction"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildReactionCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("reaction");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					ReferenceParam reaction = (ReferenceParam) params;
					if (reaction.getValue() != null) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'reaction' ilike '%" + reaction.getValue() + "%'"));

					} else if (reaction.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'reaction' IS NULL"));

					} else if (!reaction.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'reaction' IS NOT NULL"));

					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for immunization performer
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
										Restrictions
										.sqlRestriction("{alias}.data->'performer'->0->'actor'->>'reference' ilike '%"
												+ performer.getValue() + "%'"),
										
										Restrictions.sqlRestriction(
												"{alias}.data->'performer'->0->'function'->'coding'->1->>'system' ilike '%"
														+ performer.getValue() + "%'"),
										Restrictions.sqlRestriction(
												"{alias}.data->'performer'->0->'function'->'coding'->1->>'code' ilike '%"
														+ performer.getValue() + "%'"),
										
										Restrictions.sqlRestriction(
												"{alias}.data->'performer'->1->'function'->'coding'->0->>'system' ilike '%"
														+ performer.getValue() + "%'"),
										Restrictions.sqlRestriction(
												"{alias}.data->'performer'->1->'function'->'coding'->0->>'code' ilike '%"
														+ performer.getValue() + "%'"),
										Restrictions
												.sqlRestriction("{alias}.data->'performer'->1->'actor'->>'reference' ilike '%"
														+ performer.getValue() + "%'")
								
								);

					} else if (performer.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'performer' IS NULL"));

					} else if (!performer.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'performer' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for immunization date
	 * 
	 * @param theMap   : search parameter "date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildDateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					DateParam date = (DateParam) params;
					Criterion criterion = null;
					String dateFormat = date.getValueAsString();

					if (date.getPrefix() != null) {
						if (date.getPrefix().getValue() == "gt") {
							criterion = Restrictions.or(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE > '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "lt") {
							criterion = Restrictions.or(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE < '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "ge") {
							criterion = Restrictions.or(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE >= '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "le") {
							criterion = Restrictions.or(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE <= '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "eq") {
							criterion = Restrictions.or(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE = '" + dateFormat + "'"));
						}
					}else {
						criterion = Restrictions.or(Restrictions.sqlRestriction("({alias}.data->>'occurrenceDateTime')::DATE = '" + dateFormat + "'"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for Immunization id
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
	 * This method builds criteria for Immunization identifier
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
	 * This method builds criteria for fetching history of the Immunization by id
	 * 
	 * @param theId : ID of the Immunization
	 * @return : List of Immunization DAF records
	 */
	@Override
	public List<DafImmunization> getImmunizationHistoryById(String theId) {
		List<DafImmunization> list = getSession().createNativeQuery(
				"select * from immunization where data->>'id' = '"+theId+"' order by data->'meta'->>'versionId' desc",  DafImmunization.class).getResultList();
		return list;
		
	}
}
