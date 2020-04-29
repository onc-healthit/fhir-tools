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
import org.sitenv.spring.model.DafDocumentReference;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DocumentReferenceDao")
public class DocumentReferenceDaoImpl extends AbstractDao implements DocumentReferenceDao {

	/**
	 * This method builds criteria for fetching DocumentReference record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the DocumentReference
	 */
	public DafDocumentReference getDocumentReferenceById(String id) {
		List<DafDocumentReference> list = getSession().createNativeQuery(
				"select * from documentreference where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", DafDocumentReference.class)
					.getResultList();
		return list.get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the
	 * DocumentReference record by id.
	 * 
	 * @param theId     : ID of the DocumentReference
	 * @param versionId : version of the DocumentReference record
	 * @return : DAF object of the DocumentReference
	 */
	public DafDocumentReference getDocumentReferenceByVersionId(String theId, String versionId) {
		DafDocumentReference list = getSession().createNativeQuery(
			"select * from documentreference where data->>'id' = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", DafDocumentReference.class)
				.getSingleResult();
			return list;
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap : parameter for search
	 * @return criteria : DAF DocumentReference object
	 */
	@SuppressWarnings("unchecked")
	public List<DafDocumentReference> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafDocumentReference.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);

		// build criteria for date
		buildDateCriteria(theMap, criteria);

		// build criteria for Type
		buildTypeCriteria(theMap, criteria);

		// build criteria for patient
		buildPatientCriteria(theMap, criteria);

		// build criteria for category
		buildCategoryCriteria(theMap, criteria);
		
		// build criteria for category
		buildStatusCriteria(theMap, criteria);
				
		// build criteria for category
		buildPeriodCriteria(theMap, criteria);
		
		return criteria.list();
	}

	/**
	 * This method builds criteria for Period
	 * 
	 * @param theMap : search parameter "Period"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildPeriodCriteria(SearchParameterMap theMap, Criteria criteria) {
		 List<List<? extends IQueryParameterType>> list = theMap.get("period");
	        if (list != null) {
	            for (List<? extends IQueryParameterType> values : list) {
	            	Disjunction disjunction = Restrictions.disjunction();
	                for (IQueryParameterType params : values) {
	                    DateParam date = (DateParam) params;
	                    String dateFormat = date.getValueAsString();
	                    Criterion orCond= null;
	                    if(date.getPrefix() != null) {
	                        if(date.getPrefix().getValue() == "gt"){
	                        	orCond = Restrictions.or(
	                        				Restrictions.sqlRestriction("({alias}.data->'period'->>'start')::DATE > '"+dateFormat+ "'"),
	                        				Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE > '"+dateFormat+ "'")
	                        			);
	                        }else if(date.getPrefix().getValue() == "lt"){
	                        	orCond = Restrictions.or(
	                        				Restrictions.sqlRestriction("({alias}.data->'period'->>'start')::DATE < '"+dateFormat+ "'"),
	                        				Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE < '"+dateFormat+ "'")
	                        			);
	                        }else if(date.getPrefix().getValue() == "ge"){
	                        	orCond = Restrictions.or(
	                        				Restrictions.sqlRestriction("({alias}.data->'period'->>'start')::DATE >= '"+dateFormat+ "'"),
	                        				Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE >= '"+dateFormat+ "'")
	                        			);
	                        }else if(date.getPrefix().getValue() == "le"){
								orCond = Restrictions.or(
										Restrictions.sqlRestriction("({alias}.data->'period'->>'start')::DATE <= '"+dateFormat+ "'"),
										Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE <= '"+dateFormat+ "'")
								);
							}else if(date.getPrefix().getValue() == "eq"){
								orCond = Restrictions.or(
										Restrictions.sqlRestriction("({alias}.data->'period'->>'start')::DATE = '"+dateFormat+ "'"),
										Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE = '"+dateFormat+ "'")
								);
							}
	                     }else {
							orCond = Restrictions.or(
									Restrictions.sqlRestriction("({alias}.data->'period'->>'start')::DATE = '"+dateFormat+"'"),
									Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE = '"+dateFormat+"'")
							);
						}
						disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
	}

	/**
	 * This method builds criteria for Status
	 * 
	 * @param theMap : search parameter "status"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
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
	 * This method builds criteria for type
	 * 
	 * @param theMap : search parameter "type"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
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
									Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->0->>'system' ilike '%"
											+ type.getValue() + "%'"),
									Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->0->>'code' ilike '%"
											+ type.getValue() + "%'"),
									Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->0->>'display' ilike '%"
											+ type.getValue() + "%'"),
									
									Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->1->>'system' ilike '%"
											+ type.getValue() + "%'"),
									Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->1->>'code' ilike '%"
											+ type.getValue() + "%'"),
									Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->1->>'display' ilike '%"
											+ type.getValue() + "%'")
								);
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
	 * This method builds criteria for DocumentReference category
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
								.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'system' ilike '%" + category.getValue() + "%'"),
								Restrictions
								.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'code' ilike '%" + category.getValue() + "%'"),
								Restrictions
								.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display' ilike '%" + category.getValue() + "%'"),
								Restrictions
								.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'system' ilike '%" + category.getValue() + "%'"),
								Restrictions
								.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'code' ilike '%" + category.getValue() + "%'"),
								Restrictions
								.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'display' ilike '%" + category.getValue() + "%'")
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
	 * This method builds criteria for DocumentReference patient
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
										"{alias}.data->'subject'->>'reference' ilike '%" + patient.getValue() + "%'")
							);
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
	 * This method builds criteria for DocumentReference date
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
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'date')::DATE > '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "lt") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'date')::DATE < '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "ge") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'date')::DATE >= '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "le") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'date')::DATE <= '" + dateFormat + "'"));
						}else if (date.getPrefix().getValue() == "eq") {
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'date')::DATE = '" + dateFormat + "'"));
						}
					} else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'date')::DATE = '" + dateFormat + "'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for DocumentReference id
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
	 * This method builds criteria for DocumentReference identifier
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
	 * This method builds criteria for fetching history of the DocumentReference by
	 * id
	 * 
	 * @param theId : ID of the DocumentReference
	 * @return : List of DocumentReference DAF records
	 */
	public List<DafDocumentReference> getDocumentReferenceHistoryById(String theId) {
		List<DafDocumentReference> list = getSession().createNativeQuery(
			"select * from documentreference where data->>'id' = '"+theId+"'", DafDocumentReference.class)
    			.getResultList();
		return list;
	}
}
