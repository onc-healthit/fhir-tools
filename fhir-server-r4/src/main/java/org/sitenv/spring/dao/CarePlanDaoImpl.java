package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("carePlanDao")
public class CarePlanDaoImpl extends AbstractDao implements CarePlanDao {

	/**
	 * This method builds criteria for fetching careplan record by id.
	 * @param id : ID of the resource
	 * @return : DafCarePlan object
	 */
	@Override
	public DafCarePlan getCarePlanById(String id) {
		List<DafCarePlan> list = getSession().createNativeQuery(
			"select * from careplan where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", DafCarePlan.class)
				.getResultList();
		return list.get(0);
	}


	/**
	 * This method builds criteria for fetching particular version of the careplan record by id.
	 * @param theId : ID of the careplan
	 * @param versionId : version of the careplan record
	 * @return : DafCarePlan object
	 */
	@Override
	public DafCarePlan getCarePlanByVersionId(String theId, String versionId) {
		DafCarePlan list = getSession().createNativeQuery(
			"select * from careplan where data->>'id' = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", DafCarePlan.class)
				.getSingleResult();
		return list;
	}

	/**
     * This method builds criteria for fetching history of the careplan by id
     * @param theId : ID of the careplan
     * @return : List of careplan records
     */
	@Override
	public List<DafCarePlan> getCarePlanHistoryById(String theId) {
		List<DafCarePlan> list = getSession().createNativeQuery(
    			"select * from careplan where data->>'id' = '"+theId+"' order by data->'meta'->>'versionId' desc", DafCarePlan.class)
    				.getResultList();
		return list;
	}

	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DafCarePlan object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafCarePlan> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafCarePlan.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		//build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);
        
        //build criteria for date
        buildDateCriteria(theMap, criteria);
        
        //build criteria for careteam
        buildCareTeamCriteria(theMap, criteria);
        
        //build criteria for performer
        buildPerformerCriteria(theMap, criteria);
        
        //build criteria for goal
        buildGoalCriteria(theMap, criteria);
        
        //build criteria for subject
        buildSubjectCriteria(theMap, criteria);
        
        //build criteria for replaces
        buildReplacesCriteria(theMap, criteria);
        
        //build criteria for partOf
        buildPartOfCriteria(theMap, criteria);
        
        //build criteria for intent
        buildIntentCriteria(theMap, criteria);
        
        //build criteria for condition
        buildConditionCriteria(theMap, criteria);
        
        //build criteria for basedOn
        buildBasedOnCriteria(theMap, criteria);
        
        //build criteria for patient
        buildPatientCriteria(theMap, criteria);
        
        //build criteria for instantiatesUri
        buildInstantiatesUriCriteria(theMap, criteria);
        
        //build criteria for status
        buildStatusCriteria(theMap, criteria);
        
        // build criteria for category
     	buildCategoryCriteria(theMap, criteria);
        
		return criteria.list();
	}
	
	/**
	 * This method builds criteria for careplan id
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
	 * This method builds criteria for careplan identifier
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
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '%" + identifier.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for careplan date
	 * @param theMap : search parameter "date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildDateCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("date");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    DateParam date = (DateParam) params;
                    String dateFormat = date.getValueAsString();
                    Criterion orCond= null;
                    if(date.getPrefix() != null) {
						if(date.getPrefix().getValue() == "eq"){
							orCond = Restrictions.or(
									Restrictions.sqlRestriction("({alias}.data->'period'->>'start')::DATE = '"+dateFormat+ "'"),
									Restrictions.sqlRestriction("({alias}.data->'period'->>'end')::DATE = '"+dateFormat+ "'")
							);
						}else if(date.getPrefix().getValue() == "gt"){
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
   	 * This method builds criteria for careplan linked to the careteam
   	 * @param theMap : search parameter "careteam"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildCareTeamCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("care-team");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam careteam = (ReferenceParam) params;
                    Criterion orCond= null;
    				if(careteam.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'careTeam'->0->>'reference' ilike '%" + careteam.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'careTeam'->0->>'display' ilike '%" + careteam.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'careTeam'->0->>'type' ilike '%" + careteam.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'careTeam'->1->>'reference' ilike '%" + careteam.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'careTeam'->1->>'display' ilike '%" + careteam.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'careTeam'->1->>'type' ilike '%" + careteam.getValue() + "%'")
    							);
    				}else if(careteam.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'careTeam' IS NULL")
    							);
    				}else if(!careteam.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'careTeam' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
   	 * This method builds criteria for performer
   	 * @param theMap : search parameter "performer"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildPerformerCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("performer");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam performer = (ReferenceParam) params;
                    Criterion orCond= null;
    				if(performer.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'activity'->0->'detail'->'performer'->0->>'reference' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->0->'detail'->'performer'->0->>'display' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->0->'detail'->'performer'->0->>'type' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->1->'detail'->'performer'->0->>'reference' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->1->'detail'->'performer'->0->>'display' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->1->'detail'->'performer'->0->>'type' ilike '%" + performer.getValue() + "%'"),

    								Restrictions.sqlRestriction("{alias}.data->'activity'->2->'detail'->'performer'->0->>'reference' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->2->'detail'->'performer'->0->>'display' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->2->'detail'->'performer'->0->>'type' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->3->'detail'->'performer'->0->>'reference' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->3->'detail'->'performer'->0->>'display' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->3->'detail'->'performer'->0->>'type' ilike '%" + performer.getValue() + "%'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'activity'->4->'detail'->'performer'->0->>'reference' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->4->'detail'->'performer'->0->>'display' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->4->'detail'->'performer'->0->>'type' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->5->'detail'->'performer'->0->>'reference' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->5->'detail'->'performer'->0->>'display' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'activity'->5->'detail'->'performer'->0->>'type' ilike '%" + performer.getValue() + "%'")
    							);
    				}
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
   	 * This method builds criteria for subject: Who the care plan is for
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
   	 * This method builds criteria for goal
   	 * @param theMap : search parameter "goal"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildGoalCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("goal");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam goal = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(goal.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'goal'->0->>'reference' ilike '%" + goal.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'goal'->0->>'display' ilike '%" + goal.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'goal'->0->>'type' ilike '%" + goal.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'goal'->1->>'reference' ilike '%" + goal.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'goal'->1->>'display' ilike '%" + goal.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'goal'->1->>'type' ilike '%" + goal.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'goal'->2->>'reference' ilike '%" + goal.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'goal'->2->>'display' ilike '%" + goal.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'goal'->2->>'type' ilike '%" + goal.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'goal'->3->>'reference' ilike '%" + goal.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'goal'->3->>'display' ilike '%" + goal.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'goal'->3->>'type' ilike '%" + goal.getValue() + "%'")
    							);
    				}else if(goal.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'goal' IS NULL")
    							);
    				}else if(!goal.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'goal' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
   	 * This method builds criteria for replaces
   	 * @param theMap : search parameter "replaces"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildReplacesCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("replaces");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam replaces = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(replaces.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'replaces'->0->>'reference' ilike '%" + replaces.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'replaces'->0->>'display' ilike '%" + replaces.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'replaces'->0->>'type' ilike '%" + replaces.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'replaces'->1->>'reference' ilike '%" + replaces.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'replaces'->1->>'display' ilike '%" + replaces.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'replaces'->1->>'type' ilike '%" + replaces.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'replaces'->2->>'reference' ilike '%" + replaces.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'replaces'->2->>'display' ilike '%" + replaces.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'replaces'->2->>'type' ilike '%" + replaces.getValue() + "%'")
    							);
    				}else if(replaces.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'replaces' IS NULL")
    							);
    				}else if(!replaces.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'replaces' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
   	 * This method builds criteria for partOf
   	 * @param theMap : search parameter "part-of"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildPartOfCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("part-of");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam partOf = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(partOf.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'partOf'->0->>'reference' ilike '%" + partOf.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'partOf'->0->>'display' ilike '%" + partOf.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'partOf'->0->>'type' ilike '%" + partOf.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'partOf'->1->>'reference' ilike '%" + partOf.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'partOf'->1->>'display' ilike '%" + partOf.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'partOf'->1->>'type' ilike '%" + partOf.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'partOf'->2->>'reference' ilike '%" + partOf.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'partOf'->2->>'display' ilike '%" + partOf.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'partOf'->2->>'type' ilike '%" + partOf.getValue() + "%'")
    							);
    				}else if(partOf.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'partOf' IS NULL")
    							);
    				}else if(!partOf.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'partOf' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
	 * This method builds criteria for careplan intent
	 * @param theMap : search parameter "intent"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIntentCriteria(SearchParameterMap theMap, Criteria criteria) {
	    List<List<? extends IQueryParameterType>> list = theMap.get("intent");
	
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam intent = (TokenParam) params;
	                Criterion orCond= null;
	                if (intent.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->>'intent' ilike '%" + intent.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
   	 * This method builds criteria for condition: Health issues this plan addresses
   	 * @param theMap : search parameter "condition"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildConditionCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("condition");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam condition = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(condition.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->0->>'reference' ilike '%" + condition.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->0->>'display' ilike '%" + condition.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->0->>'type' ilike '%" + condition.getValue() + "%'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->1->>'reference' ilike '%" + condition.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->1->>'display' ilike '%" + condition.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->1->>'type' ilike '%" + condition.getValue() + "%'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->2->>'reference' ilike '%" + condition.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->2->>'display' ilike '%" + condition.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->2->>'type' ilike '%" + condition.getValue() + "%'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->3->>'reference' ilike '%" + condition.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->3->>'display' ilike '%" + condition.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'addresses'->3->>'type' ilike '%" + condition.getValue() + "%'")
    							);
    				}else if(condition.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'addresses' IS NULL")
    							);
    				}else if(!condition.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'addresses' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
   	 * This method builds criteria for basedOn: Fulfills CarePlan
   	 * @param theMap : search parameter "based-on"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildBasedOnCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("based-on");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam basedOn = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(basedOn.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->0->>'reference' ilike '%" + basedOn.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->0->>'display' ilike '%" + basedOn.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->0->>'type' ilike '%" + basedOn.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->1->>'reference' ilike '%" + basedOn.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->1->>'display' ilike '%" + basedOn.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->1->>'type' ilike '%" + basedOn.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->2->>'reference' ilike '%" + basedOn.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->2->>'display' ilike '%" + basedOn.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->2->>'type' ilike '%" + basedOn.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->3->>'reference' ilike '%" + basedOn.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->3->>'display' ilike '%" + basedOn.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'basedOn'->3->>'type' ilike '%" + basedOn.getValue() + "%'")
    							);
    				}else if(basedOn.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'basedOn' IS NULL")
    							);
    				}else if(!basedOn.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'basedOn' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
   	 * This method builds criteria for patient: Who the care plan is for
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
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '%" + patient.getValue() + "'"),
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
   	 * This method builds criteria for instantiatesUri: Instantiates external protocol or definition
   	 * @param theMap : search parameter "instantiates-uri"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildInstantiatesUriCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("instantiates-uri");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				UriParam instantiatesUri = (UriParam) params;
                    Criterion orCond= null;
                    if(instantiatesUri.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>0 ilike '%" + instantiatesUri.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>1 ilike '%" + instantiatesUri.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>2 ilike '%" + instantiatesUri.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>3 ilike '%" + instantiatesUri.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>4 ilike '%" + instantiatesUri.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>5 ilike '%" + instantiatesUri.getValue() + "%'")
    							);
    				}else if(instantiatesUri.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri' IS NULL")
    							);
    				}else if(!instantiatesUri.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
	 * This method builds criteria for careplan status
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
	                Criterion orCond= null;
	                if (status.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->>'status' ilike '" + status.getValue() + "'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
    
	/**
	 * This method builds criteria for careplan category
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
												+ category.getValue() + "%'"),
										Restrictions
										.sqlRestriction("{alias}.data->'category'->0->>'text' ilike '%"
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

}
