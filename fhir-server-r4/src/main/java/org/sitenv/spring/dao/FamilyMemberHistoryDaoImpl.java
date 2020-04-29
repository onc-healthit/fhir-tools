package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafFamilyMemberHistory;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("familyMemberHistoryDao")
public class FamilyMemberHistoryDaoImpl extends AbstractDao implements FamilyMemberHistoryDao {
	
	/**
	 * This method builds criteria for fetching FamilyMemberHistory record by id.
	 * @param id : ID of the resource
	 * @return : DafFamilyMemberHistory object
	 */
	@Override
	public DafFamilyMemberHistory getFamilyMemberHistoryById(String id) {
		Criteria criteria = getSession().createCriteria(DafFamilyMemberHistory.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafFamilyMemberHistory) criteria.list().get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the FamilyMemberHistory record by id.
	 * @param theId : ID of the FamilyMemberHistory
	 * @param versionId : version of the FamilyMemberHistory record
	 * @return : DafFamilyMemberHistory object
	 */
	@Override
	public DafFamilyMemberHistory getFamilyMemberHistoryByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafFamilyMemberHistory.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" +versionId+"'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		criteria.add(versionConjunction);
		return (DafFamilyMemberHistory) criteria.uniqueResult();
	}
	
	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DafFamilyMemberHistory object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafFamilyMemberHistory> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafFamilyMemberHistory.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		//build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);
        
        //build criteria for status
        buildStatusCriteria(theMap, criteria);
        
        //build criteria for relationship
        buildRelationshipCriteria(theMap, criteria);
        
        //build criteria for instantiatesUri
        buildInstantiatesUriCriteria(theMap, criteria);
        
        //build criteria for sex
        buildSexCriteria(theMap, criteria);
        
        //build criteria for patient
        buildPatientCriteria(theMap, criteria);
        
        //build criteria for code
        buildCodeCriteria(theMap, criteria);
        return criteria.list();
	}
	
	/**
	 * This method builds criteria for FamilyMemberHistory id
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
	 * This method builds criteria for FamilyMemberHistory identifier
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
	 * This method builds criteria for Specimen status
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
	 * This method builds criteria for FamilyMemberHistory relationship
	 * @param theMap : search parameter "relationship"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildRelationshipCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("relationship");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam relationship = (TokenParam) params;
	                Criterion orCond= null;
	                if (relationship.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->0->>'system' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->0->>'code' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->0->>'display' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->1->>'system' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->1->>'code' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->1->>'display' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->2->>'system' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->2->>'code' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->2->>'display' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->>'text' ilike '%" + relationship.getValue() + "%'")
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
	 * This method builds criteria for FamilyMemberHistory sex
	 * @param theMap : search parameter "sex"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSexCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("sex");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam sex = (TokenParam) params;
	                Criterion orCond= null;
	                if (sex.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->0->>'system' ilike '" + sex.getValue() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->0->>'code' ilike '" + sex.getValue() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->0->>'display' ilike '" + sex.getValue() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->1->>'system' ilike '" + sex.getValue() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->1->>'code' ilike '" + sex.getValue() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->1->>'display' ilike '" + sex.getValue() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->2->>'system' ilike '" + sex.getValue() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->2->>'code' ilike '" + sex.getValue() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->2->>'display' ilike '" + sex.getValue() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->>'text' ilike '" + sex.getValue() + "'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	/**
	 * This method builds criteria for FamilyMemberHistory patient
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
    								Restrictions.sqlRestriction("{alias}.data->'patient'->>'reference' ilike '%" + patient.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'patient'->>'display' ilike '%" + patient.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'patient'->>'type' ilike '%" + patient.getValue() + "%'")
    							);
    				}else if(patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'patient' IS NULL")
    							);
    				}else if(!patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'patient' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
		
	}
	/**
	 * This method builds criteria for FamilyMemberHistory code
	 * @param theMap : search parameter "code"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCodeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("code");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam code = (TokenParam) params;
	                Criterion orCond= null;
	                if (code.getValue() != null) {
	                	orCond = Restrictions.or(
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->0->>'system' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->0->>'code' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->0->>'display' ilike '%" + code.getValue() + "%'"),
	                				
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->1->>'system' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->1->>'code' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->1->>'display' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->>'text' ilike '%" + code.getValue() + "%'"),

	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->0->>'system' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->0->>'code' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->0->>'display' ilike '%" + code.getValue() + "%'"),
	                				
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->1->>'system' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->1->>'code' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->1->>'display' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->>'text' ilike '%" + code.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}

	/**
     * This method builds criteria for fetching history of the FamilyMemberHistory by id
     * @param theId : ID of the FamilyMemberHistory
     * @return : List of FamilyMemberHistory records
     */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafFamilyMemberHistory> getFamilyMemberHistoryHistoryById(String theId) {
		Criteria criteria = getSession().createCriteria(DafFamilyMemberHistory.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		return (List<DafFamilyMemberHistory>) criteria.list();
	}

}
