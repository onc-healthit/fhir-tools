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
import org.sitenv.spring.model.DafMedication;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("medicationDao")
public class MedicationDaoImpl extends AbstractDao implements MedicationDao {

	/**
	 * This method builds criteria for fetching medication record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the medication
	 */
	@Override
	public DafMedication getMedicationById(String id) {

		Criteria criteria = getSession().createCriteria(DafMedication.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafMedication) criteria.list().get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the medication
	 * record by id.
	 * 
	 * @param theId     : ID of the medication
	 * @param versionId : version of the medication record
	 * @return : DAF object of the medication
	 */
	@Override
	public DafMedication getMedicationByVersionId(String theId, String versionId) {

		Criteria criteria = getSession().createCriteria(DafMedication.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" + versionId + "'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + theId + "'"));
		criteria.add(versionConjunction);
		return (DafMedication) criteria.uniqueResult();
	}


	/**
	 * This method builds criteria for fetching history of the Medication by id
	 *
	 * @param resourceIDs : ID of the resource
	 * @return : List of MedicationDAF records
	 */
	public List<DafMedication> getMedicationByResourceId(List<String> resourceIDs) {
		StringBuffer resourceIDForINOperator = new StringBuffer();
		if(resourceIDs.size() > 0) {
			boolean first = true;
			for(String resourceID : resourceIDs) {
				if(first)  resourceIDForINOperator.append("'"+resourceID+"'");
				else resourceIDForINOperator.append(",'"+resourceID+"'");
				first=false;
			}
		}

		List<DafMedication> list = getSession().createNativeQuery(
				"select * from Medication where data->>'id' in ("+ resourceIDForINOperator.toString() +")"	, DafMedication.class)
				.getResultList();
		return list;

	}



	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap : parameter for search
	 * @return criteria : DAF medication object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafMedication> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafMedication.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);
		
		// build criteria for lot number
		buildLotNumberCriteria(theMap, criteria);

		// build criteria for Ingredient
		buildIngredientCriteria(theMap, criteria);

		// build Criteria for Form
		buildFormCriteria(theMap, criteria);

		// build criteria for Manufacturer
		buildManufacturerCriteria(theMap, criteria);

		// build criteria for Status
		buildStatusCriteria(theMap, criteria);

		// build criteria for IngredientCode
		buildIngredientCodeCriteria(theMap, criteria);

		// build criteria for Code
		buildCodeCriteria(theMap, criteria);

		// build criteria for ExpirationDate
		buildExpirationDateCriteria(theMap, criteria);

		return criteria.list();
	}

	/**
	 * This method builds criteria for medication expirationDate
	 * 
	 * @param theMap   : search parameter "expiration-date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildExpirationDateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("expiration-date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					DateParam expirationDate = (DateParam) params;
					String expDateFormat  = expirationDate.getValueAsString();
					if (expirationDate.getPrefix() != null) {
						if (expirationDate.getPrefix().getValue() == "gt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'batch'->>'expirationDate')::DATE > '" + expDateFormat  + "'"));
						} else if (expirationDate.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'batch'->>'expirationDate')::DATE < '" + expDateFormat  + "'"));
						} else if (expirationDate.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'batch'->>'expirationDate')::DATE >= '" + expDateFormat  + "'"));
						} else if (expirationDate.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'batch'->>'expirationDate')::DATE <= '" + expDateFormat  + "'"));
						} else if (expirationDate.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'batch'->>'expirationDate')::DATE = '" + expDateFormat  + "'"));
						}
					}else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->'batch'->>'expirationDate')::DATE = '" + expDateFormat  + "'"));
					}
				}
			}
		}
		
	}

	
	/**
	 * This method builds criteria for medication code
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
								Restrictions.sqlRestriction("{alias}.data->'code'->'coding'->1->>'system' ilike '%"
										+ code.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'code'->'coding'->1->>'code' ilike '%" + code.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'code'->'coding'->1->>'display' ilike '%"
										+ code.getValue() + "%'")
								
								);
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
	 * This method builds criteria for medication ingredientCode
	 * 
	 * @param theMap   : search parameter "ingredient-code"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIngredientCodeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("ingredient-code");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam ingredientCode = (TokenParam) params;
					Criterion criterion = null;
					if (!ingredientCode.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'numerator'->>'code' ilike '%"
												+ ingredientCode.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'denominator'->>'code' ilike '%"
												+ ingredientCode.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->1->'strength'->'numerator'->>'code' ilike '%"
												+ ingredientCode.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->1->'strength'->'denominator'->>'code' ilike '%"
												+ ingredientCode.getValue() + "%'")
								);
					} else if (ingredientCode.getMissing()) {

						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'numerator'->>'code' IS NULL"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'denominator'->>'code' IS NULL"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->1->'strength'->'numerator'->>'code' IS NULL"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->1->'strength'->'denominator'->>'code' IS NULL")
								);

					} else if (!ingredientCode.getMissing()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'numerator'->>'code' IS NOT NULL"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'denominator'->>'code' IS NOT NULL"),
								
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->1->'strength'->'numerator'->>'code' IS NOT NULL"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->1->'strength'->'denominator'->>'code' IS NOT NULL")
								);
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for medication lotNumber
	 * 
	 * @param theMap   : search parameter "lot-number"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	
	private void buildLotNumberCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("lot-number");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					TokenParam lotNumber = (TokenParam) params;
					if (!lotNumber.isEmpty()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'batch'->>'lotNumber' ilike '%" + lotNumber.getValue() + "%'"));
					} else if (lotNumber.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->'batch'->>'lotNumber' IS NULL"));
					} else if (!lotNumber.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->'batch'->>'lotNumber' IS NOT NULL"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for medication status
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
	 * This method builds criteria for medication manufacturer
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
	 * This method builds criteria for medication form
	 * 
	 * @param theMap   : search parameter "form"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildFormCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("form");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam form = (TokenParam) params;
					Criterion criterion = null;
					if (!form.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'form'->'coding'->0->>'system' ilike '%"
										+ form.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'form'->'coding'->0->>'code' ilike '%" + form.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'form'->'coding'->0->>'display' ilike '%"
										+ form.getValue() + "%'"),
								
								Restrictions.sqlRestriction("{alias}.data->'form'->'coding'->1->>'system' ilike '%"
										+ form.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'form'->'coding'->1->>'code' ilike '%" + form.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'form'->'coding'->1->>'display' ilike '%"
										+ form.getValue() + "%'")
								
								);
					} else if (form.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'form' IS NULL"));

					} else if (!form.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'form' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);

			}
		}
	}

	/**
	 * This method builds criteria for medication identifier
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
	 * This method builds criteria for medication ingredient
	 * 
	 * @param theMap   : search parameter "ingredient"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIngredientCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("ingredient");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam ingredient = (ReferenceParam) params;
					Criterion criterion = null;
					if (ingredient.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'itemReference'->>'reference' ilike '%"
												+ ingredient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'numerator'->>'value' ilike '%"
												+ ingredient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'numerator'->>'system' ilike '%"
												+ ingredient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'numerator'->>'code' ilike '%"
												+ ingredient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'denominator'->>'value' ilike '%"
												+ ingredient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'denominator'->>'system' ilike '%"
												+ ingredient.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'ingredient'->0->'strength'->'denominator'->>'code' ilike '%"
												+ ingredient.getValue() + "%'"));
					} else if (ingredient.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'ingredient' IS NULL"));
					} else if (!ingredient.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'ingredient' IS NOT NULL"));
						}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for medication id
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
	 * This method builds criteria for fetching history of the medication by id
	 * 
	 * @param theId : ID of the medication
	 * @return : List of medication DAF records
	 */
	@Override
	public List<DafMedication> getMedicationHistoryById(String theId) {
		List<DafMedication> list = getSession().createNativeQuery(
    			"select * from medication where data->>'id' = '"+theId+"' order by data->'meta'->>'versionId' desc", DafMedication.class)
    				.getResultList();
		return list;
	}
}
