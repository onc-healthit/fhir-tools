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
import org.sitenv.spring.model.DafMedicationAdministration;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("medicationAdministrationDao")
public class MedicationAdministrationDaoImpl extends AbstractDao implements MedicationAdministrationDao {

	
	/**
	 * This method builds criteria for fetching MedicationAdministration record by id.
	 * @param id : ID of the resource
	 * @return : DafMedicationAdministration object
	 */
	@Override
	public DafMedicationAdministration getMedicationAdministrationById(String id) {
		Criteria criteria = getSession().createCriteria(DafMedicationAdministration.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafMedicationAdministration) criteria.list().get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the MedicationAdministration record by id.
	 * @param theId : ID of the MedicationAdministration
	 * @param versionId : version of the MedicationAdministration record
	 * @return : DafMedicationAdministration object
	 */
	@Override
	public DafMedicationAdministration getMedicationAdministrationByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafMedicationAdministration.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" +versionId+"'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		criteria.add(versionConjunction);
		return (DafMedicationAdministration) criteria.uniqueResult();
	}
	
	/**
     * This method builds criteria for fetching history of the MedicationAdministration by id
     * @param theId : ID of the MedicationAdministration
     * @return : List of DafMedicationAdministration records
     */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafMedicationAdministration> getMedicationAdministrationHistoryById(String theId) {
		Criteria criteria = getSession().createCriteria(DafMedicationAdministration.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		return (List<DafMedicationAdministration>) criteria.list();
	}
	
	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DafMedicationAdministration object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafMedicationAdministration> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafMedicationAdministration.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		//build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);
        
        //build criteria for status
        buildStatusCriteria(theMap, criteria);
        
        //build criteria for device
        buildDeviceCriteria(theMap, criteria);
        
        //build criteria for reason-not-given
        buildReasonNotGivenCriteria(theMap, criteria);
        
        //build criteria for context
        buildContextCriteria(theMap, criteria);
        
        //build criteria for context
        buildEffectivePeriodCriteria(theMap, criteria);
        
        //build criteria for patient
        buildPatientCriteria(theMap, criteria);
        
        //build criteria for reason-given
        buildReasonGivenCriteria(theMap, criteria);
        
        //build criteria for medication
        buildMedicationCriteria(theMap, criteria);
        
        //build criteria for subject
        buildSubjectCriteria(theMap, criteria);
        
        //build criteria for performer
        buildPerformerCriteria(theMap, criteria);
        
        //build criteria for request
        buildRequestCriteria(theMap, criteria);
        return criteria.list();
	}
	
	/**
	 * This method builds criteria for MedicationAdministration id
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
	 * This method builds criteria for MedicationAdministration identifier
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
	 * This method builds criteria for MedicationAdministration device
	 * @param theMap : search parameter "device"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildDeviceCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("device");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam device = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(device.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'device'->0->>'reference' ilike '%" + device.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'device'->0->>'display' ilike '%" + device.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'device'->0->>'type' ilike '%" + device.getValue() + "%'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'device'->1->>'reference' ilike '%" + device.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'device'->1->>'display' ilike '%" + device.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'device'->1->>'type' ilike '%" + device.getValue() + "%'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'device'->2->>'reference' ilike '%" + device.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'device'->2->>'display' ilike '%" + device.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'device'->2->>'type' ilike '%" + device.getValue() + "%'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'device'->3->>'reference' ilike '%" + device.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'device'->3->>'display' ilike '%" + device.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'device'->3->>'type' ilike '%" + device.getValue() + "%'")
    							);
    				}else if(device.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'device' IS NULL")
    							);
    				}else if(!device.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'device' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
	}
	/**
	 * This method builds criteria for MedicationAdministration reason-not-given
	 * @param theMap : search parameter "reason-not-given"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildReasonNotGivenCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("reason-not-given");
			
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam reasonNotGiven = (TokenParam) params;
	                Criterion orCond= null;
	                if (reasonNotGiven.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->0->'coding'->0->>'system' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->0->'coding'->0->>'code' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->0->'coding'->0->>'display' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->0->'coding'->1->>'system' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->0->'coding'->1->>'code' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->0->'coding'->1->>'display' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->0->>'text' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->1->'coding'->0->>'system' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->1->'coding'->0->>'code' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->1->'coding'->0->>'display' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->1->'coding'->1->>'system' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->1->'coding'->1->>'code' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->1->'coding'->1->>'display' ilike '%" + reasonNotGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'statusReason'->1->>'text' ilike '%" + reasonNotGiven.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	/**
	 * This method builds criteria for MedicationAdministration context
	 * @param theMap : search parameter "context"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildContextCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("context");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam context = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(context.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'context'->>'reference' ilike '%" + context.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'context'->>'display' ilike '%" + context.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'context'->>'type' ilike '%" + context.getValue() + "%'")
    							);
    				}else if(context.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'context' IS NULL")
    							);
    				}else if(!context.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'context' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
	}
	/**
	 * This method builds criteria for MedicationAdministration effective-time
	 * @param theMap : search parameter "effective-time"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildEffectivePeriodCriteria(SearchParameterMap theMap, Criteria criteria) {
       List<List<? extends IQueryParameterType>> list = theMap.get("effective-time");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    DateParam effectiveTime = (DateParam) params;
                    String dateFormat = effectiveTime.getValueAsString();
                    Criterion orCond= null;
                    if(effectiveTime.getPrefix() != null) {
						if(effectiveTime.getPrefix().getValue() == "eq"){
							orCond = Restrictions.or(
									Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'start')::DATE = '"+dateFormat+ "'"),
									Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'end')::DATE = '"+dateFormat+ "'")
							);
						}else if(effectiveTime.getPrefix().getValue() == "gt"){
                        	orCond = Restrictions.or(
                        				Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'start')::DATE > '"+dateFormat+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'end')::DATE > '"+dateFormat+ "'")
                        			);
                        }else if(effectiveTime.getPrefix().getValue() == "lt"){
                        	orCond = Restrictions.or(
                        				Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'start')::DATE < '"+dateFormat+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'end')::DATE < '"+dateFormat+ "'")
                        			);
                        }else if(effectiveTime.getPrefix().getValue() == "ge"){
                        	orCond = Restrictions.or(
                        				Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'start')::DATE >= '"+dateFormat+ "'"),
                        				Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'end')::DATE >= '"+dateFormat+ "'")
                        			);
                        }else if(effectiveTime.getPrefix().getValue() == "le"){
                        	orCond = Restrictions.or(
	                        			Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'start')::DATE <= '"+dateFormat+ "'"),
	                        			Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'end')::DATE <= '"+dateFormat+ "'")
                        			);
                        }
                     }else {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'start')::DATE = '"+dateFormat+"'"),
								Restrictions.sqlRestriction("({alias}.data->'effectivePeriod'->>'end')::DATE = '"+dateFormat+"'")
						);
					}
					disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}
	/**
   	 * This method builds criteria for patient: The identity of a patient to list administrations  for
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
	 * This method builds criteria for MedicationAdministration reason-given
	 * @param theMap : search parameter "reason-given"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildReasonGivenCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("reason-given");
			
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam reasonGiven = (TokenParam) params;
	                Criterion orCond= null;
	                if (reasonGiven.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->0->'coding'->0->>'system' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->0->'coding'->0->>'code' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->0->'coding'->0->>'display' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->0->'coding'->1->>'system' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->0->'coding'->1->>'code' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->0->'coding'->1->>'display' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->0->>'text' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->1->'coding'->0->>'system' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->1->'coding'->0->>'code' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->1->'coding'->0->>'display' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->1->'coding'->1->>'system' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->1->'coding'->1->>'code' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->1->'coding'->1->>'display' ilike '%" + reasonGiven.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'reasonCode'->1->>'text' ilike '%" + reasonGiven.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	/**
   	 * This method builds criteria for medication
   	 * @param theMap : search parameter "medication"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildMedicationCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("medication");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam medication = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(medication.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'medicationReference'->>'reference' ilike '%" + medication.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'medicationReference'->>'display' ilike '%" + medication.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'medicationReference'->>'type' ilike '%" + medication.getValue() + "%'")
    							);
    				}else if(medication.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'medicationReference' IS NULL")
    							);
    				}else if(!medication.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'medicationReference' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    /**
   	 * This method builds criteria for subject:
   	 * @param theMap : search parameter "suject"
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
   	 * This method builds criteria for performer:
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
    								Restrictions.sqlRestriction("{alias}.data->'performer'->0->'actor'->>'reference' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'performer'->0->'actor'->>'display' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'performer'->0->'actor'->>'type' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'performer'->1->'actor'->>'reference' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'performer'->1->'actor'->>'display' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'performer'->1->'actor'->>'type' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'performer'->2->'actor'->>'reference' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'performer'->2->'actor'->>'display' ilike '%" + performer.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'performer'->2->'actor'->>'type' ilike '%" + performer.getValue() + "%'")
    							);
    				}
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
   	 * This method builds criteria for request:
   	 * @param theMap : search parameter "request"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildRequestCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("request");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam request = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(request.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'request'->>'reference' ilike '%" + request.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'request'->>'display' ilike '%" + request.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'request'->>'type' ilike '%" + request.getValue() + "%'")
    							);
    				}else if(request.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'request' IS NULL")
    							);
    				}else if(!request.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'request' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }

}
