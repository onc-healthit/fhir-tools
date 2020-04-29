package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.NumberParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafRiskAssessment;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("riskAssessmentDao")
public class RiskAssessmentDaoImpl  extends AbstractDao implements RiskAssessmentDao {

	/**
	 * This method builds criteria for fetching RiskAssessment record by id.
	 * @param id : ID of the resource
	 * @return : DAF object of the RiskAssessment
	 */
	@Override
	public DafRiskAssessment getRiskAssessmentById(String id) {
		Criteria criteria = getSession().createCriteria(DafRiskAssessment.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafRiskAssessment) criteria.list().get(0);
	}

	
	/**
	 * This method builds criteria for fetching particular version of the RiskAssessment record by id.
	 * @param theId : ID of the RiskAssessment
	 * @param versionId : version of the RiskAssessment record
	 * @return : DAF object of the RiskAssessment
	 */
	@Override
	public DafRiskAssessment getRiskAssessmentByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafRiskAssessment.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" +versionId+"'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		criteria.add(versionConjunction);
		return (DafRiskAssessment) criteria.uniqueResult();	
		}

		
		/**
		 * This method invokes various methods for search
		 * @param theMap : parameter for search
		 * @return criteria : DAF RiskAssessment object
		 */
		@SuppressWarnings("unchecked")
		public List<DafRiskAssessment> search(SearchParameterMap theMap) {
		    Criteria criteria = getSession().createCriteria(DafRiskAssessment.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		    
		    //build criteria for id
		    buildIdCriteria(theMap, criteria);
		
		    //build criteria for identifier
		    buildIdentifierCriteria(theMap, criteria);
		
		    //build criteria for condition
		    buildConditionCriteria(theMap, criteria);
		
		    //build criteria for encounter
		    buildEncounterCriteria(theMap, criteria);
		
		    //build criteria for performer
		    buildPerformerCriteria(theMap, criteria);
		
		    //build criteria for probabilty
		    buildProbabilityCriteria(theMap, criteria);
		    
		    //build criteria for subject
		    buildSubjectCriteria(theMap, criteria);
		    
		    //build criteria for risk
		    buildRiskCriteria(theMap, criteria);
		    
		    //build criteria for method
		    buildMethodCriteria(theMap, criteria);
		    
		  //build criteria for patient
		    buildPatientCriteria(theMap, criteria);
		    
		     return criteria.list();
		}
		
		/**
		 * This method builds criteria for RiskAssessment id
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
		 * This method builds criteria for RiskAssessment identifier
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
		                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '" + identifier.getValue() + "%'"),
		                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->2->>'system' ilike '" + identifier.getValue() + "%'"),
		                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->2->>'value' ilike '" + identifier.getValue() + "%'"),
		                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->3->>'system' ilike '" + identifier.getValue() + "%'"),
		                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->3->>'value' ilike '" + identifier.getValue() + "%'")
		                			);
		                }
		                disjunction.add(orCond);
		            }
		            criteria.add(disjunction);
		        }
		    }
		}
		
		/**
		 * This method builds criteria for RiskAssessment condition
		 * @param theMap : search parameter "given"
		 * @param criteria : for retrieving entities by composing Criterion objects
		 */
		private void buildConditionCriteria(SearchParameterMap theMap, Criteria criteria) {
	        List<List<? extends IQueryParameterType>> list = theMap.get("condition");
	        if (list != null) {

	            for (List<? extends IQueryParameterType> values : list) {
	                Disjunction disjunction = Restrictions.disjunction();
	                for (IQueryParameterType params : values) {
	                    StringParam conditionValue = (StringParam) params;
	                    Criterion orCond= null;
	                    if (conditionValue.isExact()) {
	                    	orCond = Restrictions.or(
	                    				Restrictions.sqlRestriction("{alias}.data->'condition'->>'reference'='" +conditionValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'condition'->>'display'='" +conditionValue.getValue()+"'")
	                    			);
	                    } else if (conditionValue.isContains()) {
	                    	orCond = Restrictions.or(
	                        			Restrictions.sqlRestriction("{alias}.data->'condition'->>'reference' ilike '%" + conditionValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'condition'->>'display' ilike '%" + conditionValue.getValue() + "%'")
		                        		
	                    			);
	                    } else {
	                    	orCond = Restrictions.or(
		                        		Restrictions.sqlRestriction("{alias}.data->'condition'->>'reference' ilike '" + conditionValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'condition'->>'display' ilike '" + conditionValue.getValue() + "%'")
		                       
	                    			);
	                    }
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
		
		}
		
		/**
		 * This method builds criteria for RiskAssessment encounter
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
	                    				Restrictions.sqlRestriction("{alias}.data->'encounter'->>'display'='" +encounterValue.getValue()+"'")
	                    			);
	                    } else if (encounterValue.isContains()) {
	                    	orCond = Restrictions.or(
	                        			Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference' ilike '%" + encounterValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'encounter'->>'display' ilike '%" + encounterValue.getValue() + "%'")
		                        		
	                    			);
	                    } else {
	                    	orCond = Restrictions.or(
		                        		Restrictions.sqlRestriction("{alias}.data->'encounter'->>'reference' ilike '" + encounterValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'encounter'->>'display' ilike '" + encounterValue.getValue() + "%'")
		                       
	                    			);
	                    }
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
		}
		
		/**
		 * This method builds criteria for RiskAssessment performer
		 * @param theMap : search parameter "performer"
		 * @param criteria : for retrieving entities by composing Criterion objects
		 */
		private void buildPerformerCriteria(SearchParameterMap theMap, Criteria criteria) {
			List<List<? extends IQueryParameterType>> list = theMap.get("performer");
	        if (list != null) {

	            for (List<? extends IQueryParameterType> values : list) {
	                Disjunction disjunction = Restrictions.disjunction();
	                for (IQueryParameterType params : values) {
	                    StringParam performerValue = (StringParam) params;
	                    Criterion orCond= null;
	                    if (performerValue.isExact()) {
	                    	orCond = Restrictions.or(
	                    				Restrictions.sqlRestriction("{alias}.data->'performer'->>'reference'='" +performerValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'performer'->>'display'='" +performerValue.getValue()+"'")
	                    			);
	                    } else if (performerValue.isContains()) {
	                    	orCond = Restrictions.or(
	                        			Restrictions.sqlRestriction("{alias}.data->'performer'->>'reference' ilike '%" + performerValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'performer'->>'display' ilike '%" + performerValue.getValue() + "%'")
		                        		
	                    			);
	                    } else {
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
		 * This method builds criteria for RiskAssessment probability
		 * @param theMap : search parameter "probability"
		 * @param criteria : for retrieving entities by composing Criterion objects
		 */
		private void buildProbabilityCriteria(SearchParameterMap theMap, Criteria criteria) {
			List<List<? extends IQueryParameterType>> list = theMap.get("probability");
	        if (list != null) {

	            for (List<? extends IQueryParameterType> values : list) {
	                Disjunction disjunction = Restrictions.disjunction();
	                for (IQueryParameterType params : values) {
	                    NumberParam probabilityValue = (NumberParam) params;
	                    Criterion orCond= null;
	                    if(probabilityValue.getValue() != null) {
	                    	 orCond = Restrictions.or(
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->0->>'probabilityDecimal' ilike '" + probabilityValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->1->>'probabilityDecimal' ilike '" + probabilityValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->2->>'probabilityDecimal' ilike '" + probabilityValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->3->>'probabilityDecimal' ilike '" + probabilityValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->4->>'probabilityDecimal' ilike '" + probabilityValue.getValue() + "%'")
	                    			);
	                    	 
	                    } 
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
		}
		
		/**
		 * This method builds criteria for RiskAssessment subject
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
		                        		Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '" + subjectValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' ilike '" + subjectValue.getValue() + "%'")
		                       
	                    			);
	                    }
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
		}
			
		/**
		 * This method builds criteria for RiskAssessment risk
		 * @param theMap : search parameter "risk"
		 * @param criteria : for retrieving entities by composing Criterion objects
		 */
		private void buildRiskCriteria(SearchParameterMap theMap, Criteria criteria) {
			List<List<? extends IQueryParameterType>> list = theMap.get("risk");
	        if (list != null) {

	            for (List<? extends IQueryParameterType> values : list) {
	                Disjunction disjunction = Restrictions.disjunction();
	                for (IQueryParameterType params : values) {
	                    StringParam riskValue = (StringParam) params;
	                    Criterion orCond= null;
	                    if (riskValue.isExact()) {
	                    	orCond = Restrictions.or(
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->0->>'code'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->0->>'display'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->0->>'system'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->1->>'code'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->1->>'display'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->1->>'system'='" +riskValue.getValue()+"'"),
	                    				
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->0->>'code'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->0->>'display'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->0->>'system'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->1->>'code'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->1->>'display'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->1->>'system'='" +riskValue.getValue()+"'"),
	                    				
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->0->>'code'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->0->>'display'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->0->>'system'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->1->>'code'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->1->>'display'='" +riskValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->1->>'system'='" +riskValue.getValue()+"'")
	                    			);
	                    } else if (riskValue.isContains()) {
	                    	orCond = Restrictions.or(
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->0->>'code' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->0->>'display' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->0->>'system' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->1->>'code' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->1->>'display' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->1->>'system' ilike '%" + riskValue.getValue() + "%'"),
	                        			
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->0->>'code' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->0->>'display' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->0->>'system' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->1->>'code' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->1->>'display' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->1->>'system' ilike '%" + riskValue.getValue() + "%'"),
	                        			
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->0->>'code' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->0->>'display' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->0->>'system' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->1->>'code' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->1->>'display' ilike '%" + riskValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->1->>'system' ilike '%" + riskValue.getValue() + "%'")
	                    			);
	                    } else {
	                    	orCond = Restrictions.or(
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->0->>'code' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->0->>'display' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->0->>'system' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->1->>'code' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->1->>'display' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->0->'qualitativeRisk'->'coding'->1->>'system' ilike '" + riskValue.getValue() + "%'"),
		                        		
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->0->>'code' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->0->>'display' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->0->>'system' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->1->>'code' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->1->>'display' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->1->'qualitativeRisk'->'coding'->1->>'system' ilike '" + riskValue.getValue() + "%'"),
		                        		
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->0->>'code' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->0->>'display' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->0->>'system' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->1->>'code' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->1->>'display' ilike '" + riskValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'prediction'->2->'qualitativeRisk'->'coding'->1->>'system' ilike '" + riskValue.getValue() + "%'")
		                        		
	                    			);
	                    }
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
		}
		
		/**
		 * This method builds criteria for RiskAssessment method
		 * @param theMap : search parameter "method"
		 * @param criteria : for retrieving entities by composing Criterion objects
		 */
		private void buildMethodCriteria(SearchParameterMap theMap, Criteria criteria) {
			List<List<? extends IQueryParameterType>> list = theMap.get("method");
	        if (list != null) {

	            for (List<? extends IQueryParameterType> values : list) {
	                Disjunction disjunction = Restrictions.disjunction();
	                for (IQueryParameterType params : values) {
	                    StringParam methodValue = (StringParam) params;
	                    Criterion orCond= null;
	                    if (methodValue.isExact()) {
	                    	orCond = Restrictions.or(
	                    				Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->0->>'code'='" +methodValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->0->>'system'='" +methodValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->0->>'display'='" +methodValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->1->>'code'='" +methodValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->1->>'system'='" +methodValue.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->1->>'display'='" +methodValue.getValue()+"'")
	                    			);
	                    } else if (methodValue.isContains()) {
	                    	orCond = Restrictions.or(
	                        			Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->0->>'code' ilike '%" + methodValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->0->>'code' ilike '%" + methodValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->0->>'code' ilike '%" + methodValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->1->>'code' ilike '%" + methodValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->1->>'code' ilike '%" + methodValue.getValue() + "%'"),
	                        			Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->1->>'code' ilike '%" + methodValue.getValue() + "%'")
	                    			);
	                    } else {
	                    	orCond = Restrictions.or(
		                        		Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->0->>'code' ilike '" + methodValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->0->>'code' ilike '" + methodValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->0->>'code' ilike '" + methodValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->1->>'code' ilike '" + methodValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->1->>'code' ilike '" + methodValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'method'->'coding'->1->>'code' ilike '" + methodValue.getValue() + "%'")
	                    			);
	                    }
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
		}
		
		/**
		 * This method builds criteria for pRiskAssessment Patient
		 * @param theMap : search parameter "patient"
		 * @param criteria : for retrieving entities by composing Criterion objects
		 */
		private void buildPatientCriteria(SearchParameterMap theMap, Criteria criteria) {
			List<List<? extends IQueryParameterType>> list = theMap.get("patient");
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
		                        		Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '" + subjectValue.getValue() + "%'"),
		                        		Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' ilike '" + subjectValue.getValue() + "%'")
		                       
	                    			);
	                    }
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
		}


		/**
	     * This method builds criteria for fetching history of the patient by id
	     * @param theId : ID of the patient
	     * @return : List of riskAssessment DAF records
	     */
		@SuppressWarnings("unchecked")
		public List<DafRiskAssessment> getRiskAssessmentHistoryById(String theId) {
			Criteria criteria = getSession().createCriteria(DafRiskAssessment.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
			return (List<DafRiskAssessment>) criteria.list();
		}
}
		
