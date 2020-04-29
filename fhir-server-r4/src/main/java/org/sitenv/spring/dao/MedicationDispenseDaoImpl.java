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
import org.sitenv.spring.model.DafMedicationDispense;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("medicationDispenseDao")
public class MedicationDispenseDaoImpl extends AbstractDao implements MedicationDispenseDao {

	/**
	 * This method builds criteria for fetching MedicationDispense record by id.
	 * @param id : ID of the resource
	 * @return : DafMedicationDispense object
	 */
	@Override
	public DafMedicationDispense getMedicationDispenseById(String id) {
		Criteria criteria = getSession().createCriteria(DafMedicationDispense.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafMedicationDispense) criteria.list().get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the MedicationDispense record by id.
	 * @param theId : ID of the MedicationDispense
	 * @param versionId : version of the MedicationDispense record
	 * @return : DafMedicationDispense object
	 */
	@Override
	public DafMedicationDispense getMedicationDispenseByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafMedicationDispense.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" +versionId+"'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		criteria.add(versionConjunction);
		return (DafMedicationDispense) criteria.uniqueResult();
	}

	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DafMedicationDispense object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafMedicationDispense> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafMedicationDispense.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		//build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);
        
        //build criteria for status
        buildStatusCriteria(theMap, criteria);
        
        //build criteria for context
        buildContextCriteria(theMap, criteria);
        
        //build criteria for patient
        buildPatientCriteria(theMap, criteria);
        
        //build criteria for authorizingPrescription
        buildAuthorizingPrescriptionCriteria(theMap, criteria);
        
        //build criteria for whenHandedOver
        buildWhenHandedOverCriteria(theMap,criteria);
        
        //build criteria for whenPrepared
        buildWhenPreparedCriteria(theMap,criteria);
        
        //build criteria for type
        buildTypeCriteria(theMap, criteria);
        
        //build criteria for medication
        buildMedicationCriteria(theMap, criteria);
        
        //build criteria for destination
        buildDestinationCriteria(theMap, criteria);
        
        //build criteria for receiver
        buildReceiverCriteria(theMap, criteria);
        
        //build criteria for code
        buildCodeCriteria(theMap, criteria);
        
        //build criteria for performer
        buildPerformerCriteria(theMap, criteria);
        
		return criteria.list();
	}
	
	/**
	 * This method builds criteria for MedicationDispense id
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
	 * This method builds criteria for MedicationDispense identifier
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
	 * This method builds criteria for Medication Dispense status
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
	 * This method builds criteria for MedicationDispense context
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
   	 * This method builds criteria for patient:The identity of a patient to list dispenses  for
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
   	 * This method builds criteria for medication: Returns dispenses of this medicine resource 
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
	 * This method builds criteria for MedicationAdministration authorizing-prescription
	 * @param theMap : search parameter "prescription"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAuthorizingPrescriptionCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("prescription");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam prescription = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(prescription.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->0->>'reference' ilike '%" + prescription.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->0->>'display' ilike '%" + prescription.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->0->>'type' ilike '%" + prescription.getValue() + "%'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->1->>'reference' ilike '%" + prescription.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->1->>'display' ilike '%" + prescription.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->1->>'type' ilike '%" + prescription.getValue() + "%'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->2->>'reference' ilike '%" + prescription.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->2->>'display' ilike '%" + prescription.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->2->>'type' ilike '%" + prescription.getValue() + "%'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->3->>'reference' ilike '%" + prescription.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->3->>'display' ilike '%" + prescription.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'authorizingPrescription'->3->>'type' ilike '%" + prescription.getValue() + "%'")
    							);
    				}else if(prescription.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'authorizingPrescription' IS NULL")
    							);
    				}else if(!prescription.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'authorizingPrescription' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
	}
	
  	/**
	 * This method builds criteria for whenHandedover
	 * @param theMap : search parameter "whenHandedover"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildWhenHandedOverCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("whenhandedover");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    DateParam whenHandedOverDate = (DateParam) params;
                    String handedOverDate = whenHandedOverDate.getValueAsString();
                    if(whenHandedOverDate.getPrefix() != null) {
                        if(whenHandedOverDate.getPrefix().getValue() == "gt"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenHandedOver')::DATE > '"+handedOverDate+ "'"));
                        }else if(whenHandedOverDate.getPrefix().getValue() == "lt"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenHandedOver')::DATE < '"+handedOverDate+ "'"));
                        }else if(whenHandedOverDate.getPrefix().getValue() == "ge"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenHandedOver')::DATE >= '"+handedOverDate+ "'"));
                        }else if(whenHandedOverDate.getPrefix().getValue() == "le"){
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenHandedOver')::DATE <= '"+handedOverDate+ "'"));
						}else if(whenHandedOverDate.getPrefix().getValue() == "eq"){
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenHandedOver')::DATE = '"+handedOverDate+ "'"));
						}
                    }else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenHandedOver')::DATE = '"+handedOverDate+"'"));
					}
                }            
            }
        }
    }
    
	/**
	 * This method builds criteria for whenprepared
	 * @param theMap : search parameter "whenprepared"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildWhenPreparedCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("whenprepared");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    DateParam whenPrepared = (DateParam) params;
                    String whenPreparedDate = whenPrepared.getValueAsString();
                    if(whenPrepared.getPrefix() != null) {
                        if(whenPrepared.getPrefix().getValue() == "gt"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenPrepared')::DATE > '"+whenPreparedDate+ "'"));
                        }else if(whenPrepared.getPrefix().getValue() == "lt"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenPrepared')::DATE < '"+whenPreparedDate+ "'"));
                        }else if(whenPrepared.getPrefix().getValue() == "ge"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenPrepared')::DATE >= '"+whenPreparedDate+ "'"));
                        }else if(whenPrepared.getPrefix().getValue() == "le"){
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenPrepared')::DATE <= '"+whenPreparedDate+ "'"));
						}else if(whenPrepared.getPrefix().getValue() == "eq"){
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenPrepared')::DATE = '"+whenPreparedDate+ "'"));
						}
                    }else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'whenPrepared')::DATE = '"+whenPreparedDate+"'"));
					}
                }            
            }
        }
    }
    
    /**
	 * This method builds criteria for dispense type
	 * @param theMap : search parameter "type"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildTypeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("type");
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam type = (TokenParam) params;
	                Criterion orCond= null;
	                if (type.getValue() != null) {
	                	orCond = Restrictions.or(
	                				Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->0->>'system' ilike '%" + type.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->0->>'code' ilike '%" + type.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->0->>'display' ilike '%" + type.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->1->>'system' ilike '%" + type.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->1->>'code' ilike '%" + type.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->1->>'display' ilike '%" + type.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->2->>'system' ilike '%" + type.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->2->>'code' ilike '%" + type.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->'coding'->2->>'display' ilike '%" + type.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->>'text' ilike '%" + type.getValue() + "%'")
	                			);
	                }
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	 /**
   	 * This method builds criteria for destination:
   	 * @param theMap : search parameter "destination"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildDestinationCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("destination");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam destination = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(destination.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'destination'->>'reference' ilike '%" + destination.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'destination'->>'display' ilike '%" + destination.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'destination'->>'type' ilike '%" + destination.getValue() + "%'")
    							);
    				}else if(destination.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'destination' IS NULL")
    							);
    				}else if(!destination.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'destination' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
   	 * This method builds criteria for MedicationAdministration receiver
   	 * @param theMap : search parameter "receiver"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
   	private void buildReceiverCriteria(SearchParameterMap theMap, Criteria criteria) {
   		List<List<? extends IQueryParameterType>> list = theMap.get("receiver");
       	if (list != null) {
   	
       		for (List<? extends IQueryParameterType> values : list) {
       			Disjunction disjunction = Restrictions.disjunction();
       			for (IQueryParameterType params : values) {
       				ReferenceParam receiver = (ReferenceParam) params;
                       Criterion orCond= null;
                       if(receiver.getValue() != null){
       					orCond = Restrictions.or(
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->0->>'reference' ilike '%" + receiver.getValue() + "%'"),
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->0->>'display' ilike '%" + receiver.getValue() + "%'"),
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->0->>'type' ilike '%" + receiver.getValue() + "%'"),
       								
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->1->>'reference' ilike '%" + receiver.getValue() + "%'"),
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->1->>'display' ilike '%" + receiver.getValue() + "%'"),
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->1->>'type' ilike '%" + receiver.getValue() + "%'"),
       								
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->2->>'reference' ilike '%" + receiver.getValue() + "%'"),
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->2->>'display' ilike '%" + receiver.getValue() + "%'"),
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->2->>'type' ilike '%" + receiver.getValue() + "%'"),
       								
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->3->>'reference' ilike '%" + receiver.getValue() + "%'"),
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->3->>'display' ilike '%" + receiver.getValue() + "%'"),
       								Restrictions.sqlRestriction("{alias}.data->'receiver'->3->>'type' ilike '%" + receiver.getValue() + "%'")
       							);
       				}else if(receiver.getMissing()){
       					orCond = Restrictions.or(
       								Restrictions.sqlRestriction("{alias}.data->>'receiver' IS NULL")
       							);
       				}else if(!receiver.getMissing()){
       					orCond = Restrictions.or(
       								Restrictions.sqlRestriction("{alias}.data->>'receiver' IS NOT NULL")
       							);
   	                }
       				disjunction.add(orCond);
       			}
       			criteria.add(disjunction);
       		}
       	}
   	}
    
    /**
	 * This method builds criteria for MedicationDispense code
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
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->0->'coding'->0->>'system' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->0->'coding'->0->>'code' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->0->'coding'->0->>'display' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->0->'coding'->1->>'system' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->0->'coding'->1->>'code' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->0->'coding'->1->>'display' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->0->>'text' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->1->'coding'->0->>'system' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->1->'coding'->0->>'code' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->1->'coding'->0->>'display' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->1->'coding'->1->>'system' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->1->'coding'->1->>'code' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->1->'coding'->1->>'display' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'medicationCodeableConcept'->1->>'text' ilike '%" + code.getValue() + "%'")
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
     * This method builds criteria for fetching history of the MedicationDispense by id
     * @param theId : ID of the MedicationDispense
     * @return : List of DafMedicationDispense records
     */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafMedicationDispense> getMedicationDispenseHistoryById(String theId) {
		Criteria criteria = getSession().createCriteria(DafMedicationDispense.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		return (List<DafMedicationDispense>) criteria.list();
	}

}
