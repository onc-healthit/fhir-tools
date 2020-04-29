package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("medicationRequestDao")
public class MedicationRequestDaoImpl extends AbstractDao implements MedicationRequestDao {

	/**
	 * This method builds criteria for fetching MedicationRequest record by id.
	 * @param id : ID of the resource
	 * @return : DAF object of the MedicationRequest
	 */
	@Override
	public DafMedicationRequest getMedicationRequestById(String id) {
		List<DafMedicationRequest> list = getSession().createNativeQuery(
				"select * from medicationrequest where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", DafMedicationRequest.class)
						.getResultList();
		return list.get(0);
    }
	
	/**
	 * This method builds criteria for fetching particular version of the MedicationRequest record by id.
	 * @param theId : ID of the MedicationRequest
	 * @param versionId : version of the MedicationRequest record
	 * @return : DAF object of the MedicationRequest
	 */
	@Override
	public DafMedicationRequest getMedicationRequestByVersionId(String theId, String versionId) {
		DafMedicationRequest list = getSession().createNativeQuery(
				"select * from medicationrequest where data->>'id' = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", DafMedicationRequest.class)
					.getSingleResult();
		return list;
	}

	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DAF medicatioRequest object
	 */
	@SuppressWarnings("unchecked")
	public List<DafMedicationRequest> search(SearchParameterMap theMap) {
        Criteria criteria = getSession().createCriteria(DafMedicationRequest.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        //build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);

        //build criteria for subject
        buildSubjectCriteria(theMap, criteria);

        //build criteria for status
        buildstatusCriteria(theMap, criteria);

        //build criteria for intent
        buildIntentCriteria(theMap, criteria);

        //build criteria for category
        buildCategoryCriteria(theMap, criteria);
        
        //build criteria for context
        buildContextCriteria(theMap, criteria);
        
        //build criteria for priority
        buildPriorityCriteria(theMap, criteria);
        
        //build criteria for requester
        buildRequesterCriteria(theMap, criteria);
        
        //build criteria for performer
        buildPerformerCriteria(theMap, criteria);
        
        //build criteria for performerType
        buildPerformerTypeCriteria(theMap, criteria);
        
        //build criteria for authoredOn
        buildAuthoredOnCriteria(theMap, criteria);
          
        return criteria.list();
    }

	

	/**
	 * This method builds criteria for MedicationRequest id
	 * @param theMap : search parameter "_id"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("_id");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    StringParam id = (StringParam) params;
                    if (id.getValue() != null) {
        				criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id.getValue()+"'"));

                    }
                }
            }
        }
	}

	/**
	 * This method builds criteria for MedicationRequest identifier
	 * @param theMap : search parameter "identifier"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdentifierCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("identifier");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam identifier = (TokenParam) params;
	                Criterion orCond= null;
	                if (identifier.getValue() != null && identifier.getSystem() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '" + identifier.getSystem() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '" + identifier.getSystem() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'use' ilike '" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'use' ilike '" + identifier.getValue() + "%'")
	                			);
	                } else if (identifier.getValue() != null) {
	                    orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'use' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'use' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '%" + identifier.getValue() + "%'")
	                    		);
	                } else if (identifier.getSystem() != null) {
	                    orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '%" + identifier.getSystem() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '%" + identifier.getSystem() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'use' ilike '%" + identifier.getSystem() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'use' ilike '%" + identifier.getSystem() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '%" + identifier.getSystem() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '%" + identifier.getSystem() + "%'")
	                    		);
	                }
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}

	/**
	 * This method builds criteria for MedicationRequest subject
	 * @param theMap : search parameter "subject"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSubjectCriteria(SearchParameterMap theMap, Criteria criteria) {

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
										"{alias}.data->'subject'->>'reference' ilike '%" + subject.getValue() + "'"),
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
	 * This method builds criteria for MedicationRequest status
	 * @param theMap : search parameter "status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildstatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("status");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam statusName = (StringParam) params;
                    Criterion orCond= null;
                    if (statusName.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->>'status' = '" +statusName.getValue()+"'")
                    			);
                    } else if (statusName.isContains()) {
                    	orCond = Restrictions.or(
                        			Restrictions.sqlRestriction("{alias}.data->>'status'ilike '%" + statusName.getValue() + "%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->>'status' ilike '" + statusName.getValue() + "'")
                    			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
		
	}

	/**
	 * This method builds criteria for MedicationRequest intent
	 * @param theMap : search parameter "intent"
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
	 * This method builds criteria for MedicationRequest category
	 * @param theMap : search parameter "category"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCategoryCriteria(SearchParameterMap theMap, Criteria criteria) {
		 List<List<? extends IQueryParameterType>> list = theMap.get("category");
	        if (list != null) {

	            for (List<? extends IQueryParameterType> values : list) {
	                Disjunction disjunction = Restrictions.disjunction();
	                for (IQueryParameterType params : values) {
	                    StringParam categoryValue = (StringParam) params;
	                    Criterion orCond= null;
	                    if (categoryValue.isExact()) {
	                    	orCond = Restrictions.or(
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'system'='" +categoryValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'code'='" +categoryValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display'='" +categoryValue.getValue()+"'")                    				
	                    			);
	                    } else if (categoryValue.isContains()) {
	                    	orCond = Restrictions.or(
	                        			Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'system' ilike '%" + categoryValue.getValue() + "%'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'code' ilike '%" +categoryValue.getValue()+"%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display'ilike '%" + categoryValue.getValue() + "%'")
		                        );
	                    } else {
	                    	orCond = Restrictions.or(
		                        		Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'system' ilike '" + categoryValue.getValue() + "%'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'code' ilike '" +categoryValue.getValue()+"%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display'ilike '" + categoryValue.getValue() + "%'")		                         );
	                    }
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
	}

	/**
	 * This method builds criteria for MedicationRequest encounter
	 * @param theMap : search parameter "encounter"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildContextCriteria(SearchParameterMap theMap, Criteria criteria) {
		 List<List<? extends IQueryParameterType>> list = theMap.get("encounter");
	        if (list != null) {

	            for (List<? extends IQueryParameterType> values : list) {
	                Disjunction disjunction = Restrictions.disjunction();
	                for (IQueryParameterType params : values) {
	                    StringParam encounterValue = (StringParam) params;
	                    Criterion orCond= null;
	                    if (encounterValue.isExact()) {
	                    	orCond = Restrictions.or(
	                    				Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference'='" +encounterValue.getValue()+"'")
	                    			);
	                    } else if (encounterValue.isContains()) {
	                    	orCond = Restrictions.or(
	                        			Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference' ilike '%" + encounterValue.getValue() + "%'")
		                        );
	                    } else {
	                    	orCond = Restrictions.or(
		                        		Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference' ilike '" + encounterValue.getValue() + "%'")
		                         );
	                    }
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
	}

	/**
	 * This method builds criteria for MedicationRequest priority
	 * @param theMap : search parameter "priority"
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
							criteria.add(Restrictions
									.sqlRestriction("{alias}.data->>'priority' not ilike '" + priority.getValue() + "'"));
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
	 * This method builds criteria for MedicationRequest requester
	 * @param theMap : search parameter "requester"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildRequesterCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("requester");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    ReferenceParam requesterValue = (ReferenceParam) params;
                    Criterion orCond= null;
                    if (requesterValue.getValue() != null) {
	                	orCond = Restrictions.or(
	                			Restrictions.sqlRestriction("{alias}.data->'requester'->>'reference' ilike '" + requesterValue.getValue() + "%'"),
                        		Restrictions.sqlRestriction("{alias}.data->'requester'->>'display' ilike '" + requesterValue.getValue() + "%'")
	                			);
	                }  
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}
	
	/**
	 * This method builds criteria for MedicationRequest intended-performer
	 * @param theMap : search parameter "intended-performer"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPerformerCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("intended-performer");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    ReferenceParam performerValue = (ReferenceParam) params;
                    Criterion orCond= null;
                    if (performerValue.getValue() != null) {
	                	orCond = Restrictions.or(
	                			Restrictions.sqlRestriction("{alias}.data->'performer'->>'reference' ilike '" + performerValue.getValue() + "%'"),
                        		Restrictions.sqlRestriction("{alias}.data->'performer'->>'display' ilike '" + performerValue.getValue() + "%'")
	                			);
	                }  
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }		
	}

	/**
	 * This method builds criteria for MedicationRequest intended-performertype
	 * @param theMap : search parameter "intended-performertype"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPerformerTypeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("intended-performertype");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					TokenParam performertype = (TokenParam) params;
					if (performertype.getModifier() != null) {
						TokenParamModifier modifier = performertype.getModifier();
						if (modifier.getValue() == ":not") {
							criteria.add(Restrictions.or(
							Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'code' not ilike '" + performertype.getValue() + "'"),
							Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'system' not ilike '" + performertype.getValue() + "'"),
							Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'display' not ilike '" + performertype.getValue() + "'"))
									);
						}
					} else if (StringUtils.isNoneEmpty(performertype.getValue())) {
						criteria.add(Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'code' ilike '%" + performertype.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'system' ilike '%" + performertype.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'display' ilike '%" + performertype.getValue() + "%'"))
								);
					} else if (performertype.getMissing()) {
						criteria.add(Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'code' IS NULL"),
								Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'system' IS NULL"),
								Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'display' IS NULL"))
								);
					} else if (!performertype.getMissing()) {
						criteria.add(Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'code' IS NOT NULL"),
								Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'system' IS NOT NULL"),
								Restrictions.sqlRestriction("{alias}.data->'performerType'->'coding'->0->>'display' IS NOT NULL"))
								);
					}
				}
			}
		}
	}


	/**
	 * This method builds criteria for MedicationRequest authoredOn
	 * @param theMap : search parameter "authoredOn"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAuthoredOnCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("authoredon");
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
						} else if (authored.getPrefix().getValue() == "eq") {
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
     * This method builds criteria for fetching history of the patient by id
     * @param theId : ID of the patient
     * @return : List of request DAF records
     */
	@Override
	public List<DafMedicationRequest> getMedicationRequestHistoryById(String theId) {
		List<DafMedicationRequest> list = getSession().createNativeQuery(
    			"select * from medicationrequest where data->>'id' = '"+theId+"' order by data->'meta'->>'versionId' desc", DafMedicationRequest.class)
    				.getResultList();
		return list;
	}
	
}
