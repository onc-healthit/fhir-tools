package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafHealthcareService;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("healthcareServiceDao")
public class HealthcareServiceDaoImpl extends AbstractDao implements HealthcareServiceDao{

	/**
	 * This method builds criteria for fetching healthcareservice record by id.
	 * @param id : ID of the resource
	 * @return : DafHealthcareService object
	 */
	@Override
	public DafHealthcareService getHealthcareServiceById(String id) {
		Criteria criteria = getSession().createCriteria(DafHealthcareService.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafHealthcareService) criteria.list().get(0);
	}
	
	/**
	 * This method builds criteria for fetching particular version of the healthcareservice record by id.
	 * @param theId : ID of the healthcareservice
	 * @param versionId : version of the healthcareservice record
	 * @return : DafHealthcareService object
	 */
	@Override
	public DafHealthcareService getHealthcareServiceByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafHealthcareService.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" +versionId+"'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		criteria.add(versionConjunction);
		return (DafHealthcareService) criteria.uniqueResult();
	}

	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DafHealthcareService object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafHealthcareService> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafHealthcareService.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        //build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);
        
        //build criteria for name
        buildNameCriteria(theMap, criteria);
        
        //build criteria for organization 
        buildOrganizationCriteria(theMap, criteria);
        
        //build criteria for endpoint 
        buildEndpointCriteria(theMap, criteria);
        
        //build criteria for telecom
        buildTelecomCriteria(theMap, criteria);
        
        //build criteria for location
        buildLocationCriteria(theMap, criteria);
        
        //build criteria for category
        buildCategoryCriteria(theMap, criteria);

        //build criteria for characteristic
        buildCharacteristicCriteria(theMap, criteria);
        
        //build criteria for active
        buildActiveCriteria(theMap, criteria);
        
        //build criteria for service type
        buildServiceTypeCriteria(theMap, criteria);
        
        //build criteria for coverage area
        buildCoverageAreaCriteria(theMap, criteria);
        
        //build criteria for specialty
        buildSpecialtyCriteria(theMap, criteria);
        
        return criteria.list();
	}
	
	/**
	 * This method builds criteria for healthcareservice id
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
	 * This method builds criteria for healthcareservice identifier
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
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->2->>'value' ilike '%" + identifier.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for healthcareservice name
	 * @param theMap : search parameter "name"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildNameCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("name");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    StringParam name = (StringParam) params;
                    if (name.isExact()) {
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'name' = '" +name.getValue()+"'"));
                    } else if (name.isContains()) {
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'name' ilike '%" + name.getValue() + "%'"));
                    } else {
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'name' ilike '" + name.getValue() + "%'"));
                    }
                }
            }
        }
	}
	
	/**
	 * This method builds criteria for healthcareservice organization
	 * @param theMap : search parameter "organization"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildOrganizationCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("organization");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    ReferenceParam organization = (ReferenceParam) params;
                    if (organization.getValue() != null) {
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'providedBy'->>'reference' ilike '%" +organization.getValue()+"%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'providedBy'->>'display' ilike '%" +organization.getValue()+"%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'providedBy'->>'type' ilike '%" +organization.getValue()+"%'"));
                    } else if (organization.getMissing()) {
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->>'providedBy' IS NULL"));
                    } else if(!organization.getMissing()){
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->>'providedBy' IS NOT NULL"));
                    }
                }
                criteria.add(disjunction);
            }
        }
	}
	
	/**
	 * This method builds criteria for healthcareservice endpoint
	 * @param theMap : search parameter "endpoint"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildEndpointCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("endpoint");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    ReferenceParam endpoint = (ReferenceParam) params;
                    Criterion orCond= null;
                    if (endpoint.getValue() != null) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'endpoint'->0->>'reference' ilike '%" +endpoint.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'endpoint'->0->>'display' ilike '%" +endpoint.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'endpoint'->0->>'type' ilike '%" +endpoint.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'endpoint'->1->>'reference' ilike '%" +endpoint.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'endpoint'->1->>'display' ilike '%" +endpoint.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'endpoint'->1->>'type' ilike '%" +endpoint.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'endpoint'->2->>'reference' ilike '%" +endpoint.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'endpoint'->2->>'display' ilike '%" +endpoint.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'endpoint'->2->>'type' ilike '%" +endpoint.getValue()+"%'")
                    				
                    			);
                    } else if (endpoint.getMissing()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->>'endpoint' IS NULL")
                    			);
                    } else if(!endpoint.getMissing()){
                    	orCond = Restrictions.or(
                					Restrictions.sqlRestriction("{alias}.data->>'endpoint' IS NOT NULL")
                    			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}
	
	/**
	 * This method builds criteria for healthcareservice telecom
	 * @param theMap : search parameter "telecom"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildTelecomCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("telecom");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam telecom = (StringParam) params;
                    Criterion orCond= null;
                    if (telecom.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' = '" +telecom.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' = '" +telecom.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' = '" +telecom.getValue()+"'")
                    			);
                    } else if (telecom.isContains()) {
                    	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' ilike '%" +telecom.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' ilike '%" +telecom.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' ilike '%" +telecom.getValue()+"%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
                    			Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' ilike '" +telecom.getValue()+"%'"),
                				Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' ilike '" +telecom.getValue()+"%'"),
                				Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' ilike '" +telecom.getValue()+"%'")
                			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
    }
    
    /**
	 * This method builds criteria for healthcareservice location
	 * @param theMap : search parameter "location"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildLocationCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("location");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                	ReferenceParam location = (ReferenceParam) params;
                    Criterion orCond= null;
                    if (location.getValue() != null) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'location'->0->>'reference' ilike '%" +location.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'location'->0->>'display' ilike '%" +location.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'location'->0->>'type' ilike '%" +location.getValue()+"%'"),

                    				Restrictions.sqlRestriction("{alias}.data->'location'->1->>'reference' ilike '%" +location.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'location'->1->>'display' ilike '%" +location.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'location'->1->>'type' ilike '%" +location.getValue()+"%'"),
                    				
                    				Restrictions.sqlRestriction("{alias}.data->'location'->2->>'reference' ilike '%" +location.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'location'->2->>'display' ilike '%" +location.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'location'->2->>'type' ilike '%" +location.getValue()+"%'")

                    			);
                    } else if (location.getMissing()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->>'location' IS NULL")
                    			);
                    } else if(!location.getMissing()){
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->>'location' IS NOT NULL")
                    			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}
	
	/**
	 * This method builds criteria for healthcareservice category
	 * @param theMap : search parameter "category"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCategoryCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("service-category");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    TokenParam category = (TokenParam) params;
                    Criterion orCond= null;
                    if (category.getValue() != null) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'code' = '" +category.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display' = '" +category.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'system' = '" +category.getValue()+"'"),

                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'code' = '" +category.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'display' = '" +category.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'system' = '" +category.getValue()+"'"),
                    				
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'code' = '" +category.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'display' = '" +category.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'system' = '" +category.getValue()+"'"),

                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'code' = '" +category.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'display' = '" +category.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'system' = '" +category.getValue()+"'")

                    			);
                    } else if (category.getMissing()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->>'category' IS NULL")
                			);
                    } else if(!category.getMissing()){
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->>'category' IS NOT NULL")
                			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}

    /**
	 * This method builds criteria for dispense service-type
	 * @param theMap : search parameter "service-type"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildServiceTypeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("service-type");
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam serviceType = (TokenParam) params;
	                Criterion orCond= null;
	                if (serviceType.getValue() != null) {
	                	orCond = Restrictions.or(
	                				Restrictions.sqlRestriction("{alias}.data->'type'->0->'coding'->0->>'system' ilike '%" + serviceType.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->0->'coding'->0->>'code' ilike '%" + serviceType.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->0->'coding'->0->>'display' ilike '%" + serviceType.getValue() + "%'"),
	                				
	                				Restrictions.sqlRestriction("{alias}.data->'type'->1->'coding'->1->>'system' ilike '%" + serviceType.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->1->'coding'->1->>'code' ilike '%" + serviceType.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->1->'coding'->1->>'display' ilike '%" + serviceType.getValue() + "%'"),
	                				
	                				Restrictions.sqlRestriction("{alias}.data->'type'->2->'coding'->2->>'system' ilike '%" + serviceType.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->2->'coding'->2->>'code' ilike '%" + serviceType.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->2->'coding'->2->>'display' ilike '%" + serviceType.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'type'->>'text' ilike '%" + serviceType.getValue() + "%'")
	                			);
	                }
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for healthcareservice characteristic
	 * @param theMap : search parameter "characteristic"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCharacteristicCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("characteristic");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    TokenParam characteristic = (TokenParam) params;
                    Criterion orCond= null;
                    if (characteristic.getValue() != null) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->0->'coding'->0->>'code' ilike '%" +characteristic.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->0->'coding'->0->>'display' ilike '%" +characteristic.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->0->'coding'->0->>'system' ilike '%" +characteristic.getValue()+"%'"),

                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->0->'coding'->1->>'code' ilike '%" +characteristic.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->0->'coding'->1->>'display' ilike '%" +characteristic.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->0->'coding'->1->>'system' ilike '%" +characteristic.getValue()+"%'"),
                    				
                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->1->'coding'->0->>'code' ilike '%" +characteristic.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->1->'coding'->0->>'display' ilike '%" +characteristic.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->1->'coding'->0->>'system' ilike '%" +characteristic.getValue()+"%'"),

                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->1->'coding'->1->>'code' ilike '%" +characteristic.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->1->'coding'->1->>'display' ilike '%" +characteristic.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'characteristic'->1->'coding'->1->>'system' ilike '%" +characteristic.getValue()+"%'")
                    			);
                    } 
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}
	
	/**
	  * This method builds criteria for healthcareservice active
	  * @param theMap : search parameter "active"
	  * @param criteria : for retrieving entities by composing Criterion objects
	  */
	private void buildActiveCriteria(SearchParameterMap theMap, Criteria criteria) {
	    List<List<? extends IQueryParameterType>> list = theMap.get("active");
	    if (list != null) {
	
	        for (List<? extends IQueryParameterType> values : list) {
	            for (IQueryParameterType params : values) {
	                TokenParam active = (TokenParam) params;
	                if(StringUtils.isNoneEmpty(active.getValue())){
	                	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'active' ilike '%" + active.getValue() + "%'"));
	                }else if(active.getMissing()){
	                	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'active' IS NULL"));
	
	                }else if(!active.getMissing()){
	                	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'active' IS NOT NULL"));
	                }
	            }
	        }
	    }
	}
	
	/**
	 * This method builds criteria for healthcareservice coverageArea
	 * @param theMap : search parameter "coverage-area"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCoverageAreaCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("coverage-area");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    ReferenceParam coverageArea = (ReferenceParam) params;
                    Criterion orCond= null;
                    if (coverageArea.getValue() != null) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'coverageArea'->0->>'reference' ilike '%" +coverageArea.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'coverageArea'->0->>'display' ilike '%" +coverageArea.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'coverageArea'->0->>'type' ilike '%" +coverageArea.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'coverageArea'->1->>'reference' ilike '%" +coverageArea.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'coverageArea'->1->>'display' ilike '%" +coverageArea.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'coverageArea'->1->>'type' ilike '%" +coverageArea.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'coverageArea'->2->>'reference' ilike '%" +coverageArea.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'coverageArea'->2->>'display' ilike '%" +coverageArea.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'coverageArea'->2->>'type' ilike '%" +coverageArea.getValue()+"%'")

                    			);
                    } else if(coverageArea.getMissing()) {
                    	orCond = Restrictions.or(
                					Restrictions.sqlRestriction("{alias}.data->>'coverageArea' IS NULL")
                    			);
                    } else if(!coverageArea.getMissing()) {
                    	orCond = Restrictions.or(
            						Restrictions.sqlRestriction("{alias}.data->>'coverageArea' IS NOT NULL")
                    			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}
	
	/**
	 * This method builds criteria for healthcareservice specialty
	 * @param theMap : search parameter "specialty"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSpecialtyCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("specialty");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    TokenParam specialty = (TokenParam) params;
                    Criterion orCond= null;
                    if (specialty.getValue() != null) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->0->'coding'->0->>'code' = '" +specialty.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->0->'coding'->0->>'display' = '" +specialty.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->0->'coding'->0->>'system' = '" +specialty.getValue()+"'"),

                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->0->'coding'->1->>'code' = '" +specialty.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->0->'coding'->1->>'display' = '" +specialty.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->0->'coding'->1->>'system' = '" +specialty.getValue()+"'"),
                    				
                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->1->'coding'->0->>'code' = '" +specialty.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->1->'coding'->0->>'display' = '" +specialty.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->1->'coding'->0->>'system' = '" +specialty.getValue()+"'"),

                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->1->'coding'->1->>'code' = '" +specialty.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->1->'coding'->1->>'display' = '" +specialty.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'specialty'->1->'coding'->1->>'system' = '" +specialty.getValue()+"'")
                    			);
                    } 
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}


	/**
	 * This method builds criteria for fetching history of the healthcareservice by id
	 * @param theId : ID of the healthcareservice
	 * @return : List of healthcareservice records
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafHealthcareService> getHealthcareServiceHistoryById(String theId) {
		Criteria criteria = getSession().createCriteria(DafHealthcareService.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		return (List<DafHealthcareService>) criteria.list();
	}
}
