package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafGoal;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("goalDao")
public class GoalDaoImpl extends AbstractDao implements GoalDao {

	/**
	 * This method builds criteria for fetching goal record by id.
	 * @param id : ID of the resource
	 * @return : DafGoal object
	 */
	@Override
	public DafGoal getGoalById(String id) {
		Criteria criteria = getSession().createCriteria(DafGoal.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafGoal) criteria.list().get(0);
	}
	
	/**
	 * This method builds criteria for fetching particular version of the goal record by id.
	 * @param theId : ID of the goal
	 * @param versionId : version of the goal record
	 * @return : DafGoal object
	 */
	@Override
	public DafGoal getGoalByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafGoal.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" +versionId+"'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		criteria.add(versionConjunction);
		return (DafGoal) criteria.uniqueResult();
	}

	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DafGoal object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafGoal> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafGoal.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        //build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);
        
        //build criteria for lifestatus
        buildLifecycleStatusCriteria(theMap, criteria);
        
        //build criteria for achievementstatus
        buildAchievementStatusCriteria(theMap, criteria);
        
        //build criteria for subject
        buildSubjectCriteria(theMap, criteria);
        
        //build criteria for patient
        buildPatientCriteria(theMap, criteria);
        
        //build criteria for category
        buildCategoryCriteria(theMap, criteria);
        
        //build criteria for start-date
        buildStartDateCriteria(theMap, criteria);
        
        //build criteria for target-date
        buildTargetDateCriteria(theMap, criteria);
        System.out.println(criteria.list());
		return criteria.list();
	}
	
	
	/**
	 * This method builds criteria for goal id
	 * @param theMap : search parameter "_id"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("_id");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    TokenParam id = (TokenParam) params;
                    if (id.getValue() != null) {
        				criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id.getValue()+"'"));
                    }
                }
            }
        }
	}
	
	/**
	 * This method builds criteria for goal identifier
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
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->2->>'system' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->2->>'value' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->3->>'system' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->3->>'value' ilike '%" + identifier.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
    
	/**
	 * This method builds criteria for goal lifecyclestatus
	 * @param theMap : search parameter "lifecycle-status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildLifecycleStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("lifecycle-status");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam lifecycleStatus = (TokenParam) params;
	                Criterion orCond= null;
	                if (lifecycleStatus.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->>'lifecycleStatus' ilike '%" + lifecycleStatus.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for goal achievementstatus
	 * @param theMap : search parameter "achievement-status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAchievementStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("achievement-status");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam type = (TokenParam) params;
	                Criterion orCond= null;
	                if (type.getValue() != null) {
	        			orCond = Restrictions.or(
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->0->>'system' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->0->>'code' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->0->>'display' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->1->>'system' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->1->>'code' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->1->>'display' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->2->>'system' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->2->>'code' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->2->>'display' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->>'text' ilike '%" + type.getValue() + "%'")
	            			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	/**
   	 * This method builds criteria for patient:Returns statements for a specific patient.
   	 * @param theMap : search parameter "patient"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildPatientCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("patient");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam patient = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(patient.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '%" + patient.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' ilike '%" + patient.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'type' ilike '%" + patient.getValue() + "%'")
    							);
    				}else if(patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'subject' IS NULL")
    							);
    				}else if(!patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'subject' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
   	 * This method builds criteria for subject
   	 * @param theMap : search parameter "subject"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildSubjectCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("subject");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam subject = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(subject.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '%" + subject.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' ilike '%" + subject.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'type' ilike '%" + subject.getValue() + "%'")
    							);
    				}else if(subject.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'subject' IS NULL")
    							);
    				}else if(!subject.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'subject' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
	/**
	 * This method builds criteria for goal category
	 * @param theMap : search parameter "category"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCategoryCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("category");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    TokenParam category = (TokenParam) params;
                    Criterion orCond= null;
                    if (category.getValue() != null) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'code' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'system' ilike '%" +category.getValue()+"%'"),

                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'code' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'display' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'system' ilike '%" +category.getValue()+"%'"),
                    				
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'code' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'display' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'system' ilike '%" +category.getValue()+"%'"),

                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'code' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'display' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'system' ilike '%" +category.getValue()+"%'")
                    			);
                    } 
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}
    
	/**
	 * This method builds criteria for target-date
	 * @param theMap : search parameter "target-date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildTargetDateCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("target-date");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    DateParam targetDate = (DateParam) params;
                    String theTargetDate = targetDate.getValueAsString();
                    Criterion orCond= null;
                    if(targetDate.getPrefix() != null) {
                        if(targetDate.getPrefix().getValue() == "gt"){
                        	orCond = Restrictions.or(
                        				Restrictions.sqlRestriction("({alias}.data->'target'->0->>'dueDate')::DATE > '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'target'->1->>'dueDate')::DATE > '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'target'->2->>'dueDate')::DATE > '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'target'->3->>'dueDate')::DATE > '"+theTargetDate+ "'")
                        			);
                        }else if(targetDate.getPrefix().getValue() == "lt"){
                        	orCond = Restrictions.or(
                        				Restrictions.sqlRestriction("({alias}.data->'target'->0->>'dueDate')::DATE < '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'target'->1->>'dueDate')::DATE < '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'target'->2->>'dueDate')::DATE < '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'target'->3->>'dueDate')::DATE < '"+theTargetDate+ "'")
                        			);
                        }else if(targetDate.getPrefix().getValue() == "ge"){
                        	orCond = Restrictions.or(
                        				Restrictions.sqlRestriction("({alias}.data->'target'->0->>'dueDate')::DATE >= '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'target'->1->>'dueDate')::DATE >= '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'target'->2->>'dueDate')::DATE >= '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'target'->3->>'dueDate')::DATE >= '"+theTargetDate+ "'")
                        			);
                        }else if(targetDate.getPrefix().getValue() == "le"){
							orCond = Restrictions.or(
									Restrictions.sqlRestriction("({alias}.data->'target'->0->>'dueDate')::DATE <= '"+theTargetDate+ "'"),
									Restrictions.sqlRestriction("({alias}.data->'target'->1->>'dueDate')::DATE <= '"+theTargetDate+ "'"),
									Restrictions.sqlRestriction("({alias}.data->'target'->2->>'dueDate')::DATE <= '"+theTargetDate+ "'"),
									Restrictions.sqlRestriction("({alias}.data->'target'->3->>'dueDate')::DATE <= '"+theTargetDate+ "'")
							);
						}else if(targetDate.getPrefix().getValue() == "eq"){
							orCond = Restrictions.or(
									Restrictions.sqlRestriction("({alias}.data->'target'->0->>'dueDate')::DATE = '"+theTargetDate+ "'"),
									Restrictions.sqlRestriction("({alias}.data->'target'->1->>'dueDate')::DATE = '"+theTargetDate+ "'"),
									Restrictions.sqlRestriction("({alias}.data->'target'->2->>'dueDate')::DATE = '"+theTargetDate+ "'"),
									Restrictions.sqlRestriction("({alias}.data->'target'->3->>'dueDate')::DATE = '"+theTargetDate+ "'")
							);
						}
                    }else {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("({alias}.data->'target'->0->>'dueDate')::DATE = '"+theTargetDate+"'"),
								Restrictions.sqlRestriction("({alias}.data->'target'->1->>'dueDate')::DATE = '"+theTargetDate+"'"),
								Restrictions.sqlRestriction("({alias}.data->'target'->2->>'dueDate')::DATE = '"+theTargetDate+"'"),
								Restrictions.sqlRestriction("({alias}.data->'target'->3->>'dueDate')::DATE = '"+theTargetDate+"'")
						);
					}
					disjunction.add(orCond);
                }
				criteria.add(disjunction);
            }
        }
    }
    
    
   	/**
   	 * This method builds criteria for start-date
   	 * @param theMap : search parameter "start-date"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildStartDateCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("start-date");
	    if (list != null) {
    	   for (List<? extends IQueryParameterType> values : list) {
    		   for (IQueryParameterType params : values) {
	               DateParam startDate = (DateParam) params;
	               String theStartDate = startDate.getValueAsString();
	               if(startDate.getPrefix() != null) {
	                   if(startDate.getPrefix().getValue() == "gt"){
	                       criteria.add(Restrictions.sqlRestriction("({alias}.data->>'startDate')::DATE > '"+theStartDate+ "'"));
	                   }else if(startDate.getPrefix().getValue() == "lt"){
	                       criteria.add(Restrictions.sqlRestriction("({alias}.data->>'startDate')::DATE < '"+theStartDate+ "'"));
	                   }else if(startDate.getPrefix().getValue() == "ge"){
	                       criteria.add(Restrictions.sqlRestriction("({alias}.data->>'startDate')::DATE >= '"+theStartDate+ "'"));
	                   }else if(startDate.getPrefix().getValue() == "le"){
						   criteria.add(Restrictions.sqlRestriction("({alias}.data->>'startDate')::DATE <= '"+theStartDate+ "'"));
					   }else if(startDate.getPrefix().getValue() == "eq"){
						   criteria.add(Restrictions.sqlRestriction("({alias}.data->>'startDate')::DATE = '"+theStartDate+ "'"));
					   }
                   }else {
					   criteria.add(Restrictions.sqlRestriction("({alias}.data->>'startDate')::DATE = '"+theStartDate+"'"));
				   }
               }            
           }
	    }
    }
       
	/**
     * This method builds criteria for fetching history of the goal by id
     * @param theId : ID of the goal
     * @return : List of goal records
     */
	@Override
	public List<DafGoal> getGoalHistoryById(String theId) {
		List<DafGoal> list = getSession().createNativeQuery(
    			"select * from goal where data->>'id' = '"+theId+"' order by data->'meta'->>'versionId' desc", DafGoal.class)
    				.getResultList();
		return list;
	}

}
