package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafCareTeam;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("careTeamDao")
public class CareTeamDaoImpl extends AbstractDao implements CareTeamDao {

	/**
	 * This method builds criteria for fetching careTeam record by id.
	 * @param id : ID of the resource
	 * @return : DAF object of the careTeam
	 */
	@Override
	public DafCareTeam getCareTeamById(String id) {
		List<DafCareTeam> list = getSession().createNativeQuery(
			"select * from careteam where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", DafCareTeam.class)
				.getResultList();
		return list.get(0);
	}


	/**
	 * This method builds criteria for fetching particular version of the careTeam record by id.
	 * @param theId : ID of the careTeam
	 * @param versionId : version of the careTeam record
	 * @return : DAF object of the careTeam
	 */
	@Override
	public DafCareTeam getCareTeamByVersionId(String theId, String versionId) {
		DafCareTeam list = getSession().createNativeQuery(
			"select * from careteam where data->>'id' = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", DafCareTeam.class)
				.getSingleResult();
		return list;
	}
	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DAF careTeam object
	 */
	@SuppressWarnings("unchecked")
	public List<DafCareTeam> search(SearchParameterMap theMap) {
        Criteria criteria = getSession().createCriteria(DafCareTeam.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        //build criteria for date
        buildDateCriteria(theMap, criteria);
        
        //build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);

        //build criteria for subject
        buildSubjectCriteria(theMap, criteria);

        //build criteria for context
        buildContextCriteria(theMap, criteria);

        //build criteria for encounter
        buildEncounterCriteria(theMap, criteria);
        
        //build criteria for category
        buildCategoryCriteria(theMap, criteria);
        
        //build criteria for participant
        buildParticipantCriteria(theMap, criteria);
        
        //build criteria for status
        buildStatusCriteria(theMap, criteria);
        
        //build criteria for Patient
        buildPatientCriteria(theMap, criteria);
      
        return criteria.list();
    }
	
	/**
	 * This method builds criteria for careTeam id
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
	 * This method builds criteria for careTeam date
	 * @param theMap : search parameter "date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */		
	private void buildDateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("date");
        if (list != null) {  
        	for (List<? extends IQueryParameterType> values : list) {
	            for (IQueryParameterType params : values) {
	                DateParam date = (DateParam) params;
	                String dateFormat = date.getValueAsString();
	                if(date.getPrefix() != null) {
	                    if(date.getPrefix().getValue() == "gt"){
	                        criteria.add(Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE > '"+dateFormat+ "'"));
	                    }else if(date.getPrefix().getValue() == "lt"){
	                        criteria.add(Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE < '"+dateFormat+ "'"));
	                    }else if(date.getPrefix().getValue() == "ge"){
	                        criteria.add(Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE >= '"+dateFormat+ "'"));
	                    }else if(date.getPrefix().getValue() == "le"){
	                        criteria.add(Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE <= '"+dateFormat+ "'"));
	                    }else if(date.getPrefix().getValue() == "ne"){
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE != '"+dateFormat+ "'"));
						}else if(date.getPrefix().getValue() == "eq"){
							criteria.add(Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE = '"+dateFormat+ "'"));
						}
	                }else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE = '"+dateFormat+"'"));
					}
	            }            
        	}
        }
	}

	/**
	 * This method builds criteria for careTeam identifier
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
	                if (identifier.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '" + identifier.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for careTeam subject
	 * @param theMap : search parameter "subject"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSubjectCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("subject");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam subjectValue = (StringParam) params;
                    Criterion orCond= null;
                    if (subjectValue.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference'='" +subjectValue.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'subject'->>'display'='" +subjectValue.getValue()+"'")
                    			);
                    } else if (subjectValue.isContains()) {
                    	orCond = Restrictions.or(
                        			Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '%" + subjectValue.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' ilike '%" + subjectValue.getValue() + "%'")
	                        		
                    			);
                    } else {
                    	orCond = Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '%" + subjectValue.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' ilike '%" + subjectValue.getValue() + "%'")
	                       
                    			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}
	
	/**
	 * This method builds criteria for careTeam enconter
	 * @param theMap : search parameter "encounter"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildContextCriteria(SearchParameterMap theMap, Criteria criteria) {    
        List<List<? extends IQueryParameterType>> list = theMap.get("context");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam encounterValue = (StringParam) params;
                    Criterion orCond= null;
                    if (encounterValue.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference'='" +encounterValue.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'encounter'->>'display'='" +encounterValue.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'encounter'->>'type'='" +encounterValue.getValue()+"'")
                    			);
                    } else if (encounterValue.isContains()) {
                    	orCond = Restrictions.or(
                        			Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference' ilike '%" + encounterValue.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'encounter'->>'display' ilike '%" + encounterValue.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'encounter'->>'type' ilike '%" + encounterValue.getValue() + "%'")
	                        );
                    } else {
                    	orCond = Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference' ilike '" + encounterValue.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'encounter'->>'display' ilike '" + encounterValue.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'encounter'->>'type' ilike '" + encounterValue.getValue() + "%'")
	                         );
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
		
	}
	
	/**
	 * This method builds criteria for careTeam encounter
	 * @param theMap : search parameter "encounter"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildEncounterCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("encounter");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam encounterValue = (StringParam) params;
                    Criterion orCond= null;
                    if (encounterValue.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference'='" +encounterValue.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'encounter'->>'display'='" +encounterValue.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'encounter'->>'type'='" +encounterValue.getValue()+"'")
                    			);
                    } else if (encounterValue.isContains()) {
                    	orCond = Restrictions.or(
                        			Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference' ilike '%" + encounterValue.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'encounter'->>'display' ilike '%" + encounterValue.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'encounter'->>'type' ilike '%" + encounterValue.getValue() + "%'")
	                        );
                    } else {
                    	orCond = Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference' ilike '%" + encounterValue.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'encounter'->>'display' ilike '%" + encounterValue.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'encounter'->>'type' ilike '%" + encounterValue.getValue() + "%'")

	                         );
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}
	
	/**
	 * This method builds criteria for careTeam category
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
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display'='" +categoryValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'system'='" +categoryValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'code'='" +categoryValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'display'='" +categoryValue.getValue()+"'"),
	                    				
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'system'='" +categoryValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'code'='" +categoryValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'display'='" +categoryValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'system'='" +categoryValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'code'='" +categoryValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'display'='" +categoryValue.getValue()+"'")
	                    			);
	                    } else if (categoryValue.isContains()) {
	                    	orCond = Restrictions.or(
	                        			Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'system' ilike '%" + categoryValue.getValue() + "%'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'code' ilike '%" +categoryValue.getValue()+"%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display'ilike '%" + categoryValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'system' ilike '%" + categoryValue.getValue() + "%'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'code' ilike '%" +categoryValue.getValue()+"%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'display'ilike '%" + categoryValue.getValue() + "%'"),
	                        			
	                        			Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'system' ilike '%" + categoryValue.getValue() + "%'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'code' ilike '%" +categoryValue.getValue()+"%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'display'ilike '%" + categoryValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'system' ilike '%" + categoryValue.getValue() + "%'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'code' ilike '%" +categoryValue.getValue()+"%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'display'ilike '%" + categoryValue.getValue() + "%'")
		                        );
	                    } else {
	                    	orCond = Restrictions.or(
		                        		Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'system' ilike '" + categoryValue.getValue() + "%'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'code' ilike '" +categoryValue.getValue()+"%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display'ilike '" + categoryValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'system' ilike '" + categoryValue.getValue() + "%'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'code' ilike '" +categoryValue.getValue()+"%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'display'ilike '" + categoryValue.getValue() + "%'"),
		                        		
		                        		Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'system' ilike '" + categoryValue.getValue() + "%'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'code' ilike '" +categoryValue.getValue()+"%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'display'ilike '" + categoryValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'system' ilike '" + categoryValue.getValue() + "%'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'code' ilike '" +categoryValue.getValue()+"%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'display'ilike '" + categoryValue.getValue() + "%'")
		                        		);
	                    }
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
	}
	
	/**
	 * This method builds criteria for careTeam participant
	 * @param theMap : search parameter "participant"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildParticipantCriteria(SearchParameterMap theMap, Criteria criteria) {
		 List<List<? extends IQueryParameterType>> list = theMap.get("participant");
	        if (list != null) {

	            for (List<? extends IQueryParameterType> values : list) {
	                Disjunction disjunction = Restrictions.disjunction();
	                for (IQueryParameterType params : values) {
	                    StringParam participantValue = (StringParam) params;
	                    Criterion orCond= null;
	                    if (participantValue.isExact()) {
	                    	orCond = Restrictions.or(
	                    				Restrictions.sqlRestriction("{alias}.data->'participant'->0->'member'->>'reference'='" +participantValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'participant'->0->'member'->>'display'='" +participantValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'participant'->1->'member'->>'reference'='" +participantValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'participant'->1->'member'->>'display'='" +participantValue.getValue()+"'")
	                    				
	                    			);
	                    } else if (participantValue.isContains()) {
	                    	orCond = Restrictions.or(
	                        			Restrictions.sqlRestriction("{alias}.data->'participant'->0->'member'->>'reference' ilike '%" + participantValue.getValue() + "%'"),	                  
	                        			Restrictions.sqlRestriction("{alias}.data->'participant'->0->'member'->>'display' ilike '%" + participantValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'participant'->1->'member'->>'reference' ilike '%" + participantValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'participant'->1->'member'->>'display' ilike '%" + participantValue.getValue() + "%'")
		                        );
	                    } else {
	                    	orCond = Restrictions.or(
		                        		Restrictions.sqlRestriction("{alias}.data->'participant'->0->'member'->>'reference' ilike '%" + participantValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'participant'->0->'member'->>'display' ilike '%" + participantValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'participant'->1->'member'->>'reference' ilike '%" + participantValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'participant'->1->'member'->>'display' ilike '%" + participantValue.getValue() + "%'")
		                        		);
	                    }
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }	
	}

	/**
	 * This method builds criteria for careTeam status
	 * @param theMap : search parameter "status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	
	private void buildStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("status");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                	TokenParam status = (TokenParam) params;
					Criterion orCond = null;
                	if(status.getModifier() != null) {
                        TokenParamModifier modifier = status.getModifier();
                        if(modifier.getValue() == ":not") {
							orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'status' not ilike '"+status.getValue()+"'"));
                        }
                	}else if(StringUtils.isNoneEmpty(status.getValue())){
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'status' ilike '" + status.getValue() + "'"));
                    }else if(status.getMissing()){
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'status' IS NULL"));
                    } else if(!status.getMissing()){
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'status' IS NOT NULL"));
                    }
					disjunction.add(orCond);
                }
				criteria.add(disjunction);
            }
        }	
		
	}

    /**
     * This method builds criteria for fetching history of the careteam by id
     * @param theId : ID of the careteam
     * @return : List of careteam records
     */
    @SuppressWarnings("unchecked")
	@Override
	public List<DafCareTeam> getCareTeamHistoryById(String theId) {
		Criteria criteria = getSession().createCriteria(DafCareTeam.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		return (List<DafCareTeam>) criteria.list();
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
					ReferenceParam patient = (ReferenceParam) params;
					Criterion orCond = null;
					if (patient.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'reference' ilike '%" + patient.getValue() + "%'"),
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
     
}
