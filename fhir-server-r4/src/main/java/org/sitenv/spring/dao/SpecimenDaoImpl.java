package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafSpecimen;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("specimenDao")
public class SpecimenDaoImpl extends AbstractDao implements SpecimenDao {

	/**
	 * This method builds criteria for fetching Specimen record by id.
	 * @param id : ID of the resource
	 * @return : DafSpecimen object
	 */
	@Override
	public DafSpecimen getSpecimenById(String id) {
		Criteria criteria = getSession().createCriteria(DafSpecimen.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafSpecimen) criteria.list().get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the specimen record by id.
	 * @param theId : ID of the specimen
	 * @param versionId : version of the specimen record
	 * @return : DafSpecimen object
	 */
	@Override
	public DafSpecimen getSpecimenByVersionId(String theId, String versionId) {
		Criteria criteria = getSession().createCriteria(DafSpecimen.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" +versionId+"'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		criteria.add(versionConjunction);
		return (DafSpecimen) criteria.uniqueResult();
	}

	/**
	 * This method invokes various methods for search
	 * @param theId : parameter for search
	 * @return criteria : DafSpecimen object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafSpecimen> getSpecimenHistoryById(String theId) {
		Criteria criteria = getSession().createCriteria(DafSpecimen.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		return (List<DafSpecimen>)criteria.list();
	}

	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DafSpecimen object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafSpecimen> search(SearchParameterMap theMap) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafSpecimen.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		//build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);
        
        //build criteria for container
        buildContainerCriteria(theMap, criteria);
        
        //build criteria for parent
        buildParentCriteria(theMap, criteria);
        
        //build criteria for containerId
        buildContainerIdCriteria(theMap, criteria);
        
        //build criteria for bodysite
        buildBodysiteCriteria(theMap, criteria);
        
        //build criteria for subject
        buildSubjectCriteria(theMap, criteria);
        
        //build criteria for accession
        buildAccessionCriteria(theMap, criteria);
        
        //build criteria for type
        buildTypeCriteria(theMap, criteria);
        
        //build criteria for collector
        buildCollectorCriteria(theMap, criteria);
        
        //build criteria for status
        buildStatusCriteria(theMap, criteria);
        
        return criteria.list();
	}

	/**
	 * This method builds criteria for Specimen id
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
	 * This method builds criteria for Specimen identifier
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
	 * This method builds criteria for Specimen container
	 * @param theMap : search parameter "container"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildContainerCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("container");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam container = (TokenParam) params;
	                Criterion orCond= null;
	                if (container.getValue() != null) {
	                	orCond = Restrictions.or(
	                				Restrictions.sqlRestriction("{alias}.data->'container'->0->'type'->'coding'->0->>'system' ilike '%" + container.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->0->'type'->'coding'->0->>'code' ilike '%" + container.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->0->'type'->'coding'->0->>'display' ilike '%" + container.getValue() + "%'"),
	                				
	                				Restrictions.sqlRestriction("{alias}.data->'container'->0->'type'->'coding'->1->>'system' ilike '%" + container.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->0->'type'->'coding'->1->>'code' ilike '%" + container.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->0->'type'->'coding'->1->>'display' ilike '%" + container.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->0->'type'->>'text' ilike '%" + container.getValue() + "%'"),

	                				Restrictions.sqlRestriction("{alias}.data->'container'->1->'type'->'coding'->0->>'system' ilike '%" + container.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->1->'type'->'coding'->0->>'code' ilike '%" + container.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->1->'type'->'coding'->0->>'display' ilike '%" + container.getValue() + "%'"),
	                				
	                				Restrictions.sqlRestriction("{alias}.data->'container'->1->'type'->'coding'->1->>'system' ilike '%" + container.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->1->'type'->'coding'->1->>'code' ilike '%" + container.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->1->'type'->'coding'->1->>'display' ilike '%" + container.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'container'->1->'type'->>'text' ilike '%" + container.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	/**
	 * This method builds criteria for Specimen parent
	 * @param theMap : search parameter "parent"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildParentCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("parent");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                ReferenceParam parent = (ReferenceParam) params;
	                Criterion orCond= null;
	                if (parent.getValue() != null) {
	                	orCond = Restrictions.or(
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->0->>'type' ilike '%" + parent.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->0->>'reference' ilike '%" + parent.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->0->>'display' ilike '%" + parent.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->1->>'type' ilike '%" + parent.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->1->>'reference' ilike '%" + parent.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->1->>'display' ilike '%" + parent.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->2->>'type' ilike '%" + parent.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->2->>'reference' ilike '%" + parent.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->2->>'display' ilike '%" + parent.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->3->>'type' ilike '%" + parent.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->3->>'reference' ilike '%" + parent.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'parent'->3->>'display' ilike '%" + parent.getValue() + "%'")
	                			);
	                }else if(parent.getMissing()){
    					orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->>'parent' IS NULL")
							);
	                }else if(!parent.getMissing()){
	                	orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->>'parent' IS NOT NULL")
							);
	                }
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for Specimen container-id
	 * @param theMap : search parameter "container-id"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildContainerIdCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("container-id");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam containerId = (TokenParam) params;
	                Criterion orCond= null;
	                if (containerId.getValue() != null) {
	                	orCond = Restrictions.or(
	                				Restrictions.sqlRestriction("{alias}.data->'container'->0->'identifier'->0->>'system' ilike '%" + containerId.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->0->'identifier'->0->>'value' ilike '%" + containerId.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->0->'identifier'->1->>'system' ilike '%" + containerId.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->0->'identifier'->1->>'value' ilike '%" + containerId.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->1->'identifier'->0->>'system' ilike '%" + containerId.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->1->'identifier'->0->>'value' ilike '%" + containerId.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->1->'identifier'->1->>'system' ilike '%" + containerId.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'container'->1->'identifier'->1->>'value' ilike '%" + containerId.getValue() + "%'")
	                			);
	                }
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for Specimen bodysite
	 * @param theMap : search parameter "bodysite"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildBodysiteCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("bodysite");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam bodysite = (TokenParam) params;
	                Criterion orCond= null;
	                if (bodysite.getValue() != null) {
	                	orCond = Restrictions.or(
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'bodySite'->'coding'->0->>'system' ilike '%" + bodysite.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'bodySite'->'coding'->0->>'code' ilike '%" + bodysite.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'bodySite'->'coding'->0->>'display' ilike '%" + bodysite.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'bodySite'->'coding'->1->>'system' ilike '%" + bodysite.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'bodySite'->'coding'->1->>'code' ilike '%" + bodysite.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'bodySite'->'coding'->1->>'display' ilike '%" + bodysite.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'bodySite'->'coding'->2->>'system' ilike '%" + bodysite.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'bodySite'->'coding'->2->>'code' ilike '%" + bodysite.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'bodySite'->'coding'->2->>'display' ilike '%" + bodysite.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'bodySite'->>'text' ilike '%" + bodysite.getValue() + "%'")
	                			);
	                }
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for Specimen subject
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
	                if (subject.getValue() != null) {
	                	orCond = Restrictions.or(
	                				Restrictions.sqlRestriction("{alias}.data->'subject'->>'type' ilike '%" + subject.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '%" + subject.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' ilike '%" + subject.getValue() + "%'")
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
	 * This method builds criteria for Specimen accession
	 * @param theMap : search parameter "accession"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAccessionCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("accession");
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam accession = (TokenParam) params;
	                Criterion orCond= null;
	                if (accession.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'accessionIdentifier'->>'system' ilike '%" + accession.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'accessionIdentifier'->>'value' ilike '%" + accession.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for Specimen type
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
	 * This method builds criteria for Specimen collector
	 * @param theMap : search parameter "collector"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCollectorCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("collector");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                ReferenceParam collector = (ReferenceParam) params;
	                Criterion orCond= null;
	                if (collector.getValue() != null) {
	                	orCond = Restrictions.or(
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'collector'->>'type' ilike '%" + collector.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'collector'->>'reference' ilike '%" + collector.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'collection'->'collector'->>'display' ilike '%" + collector.getValue() + "%'")
	                			);
	                }else if(collector.getMissing()){
    					orCond = Restrictions.or(
									Restrictions.sqlRestriction("{alias}.data->'collection'->>'collector' IS NULL")
							);
	                }else if(!collector.getMissing()){
	                	orCond = Restrictions.or(
									Restrictions.sqlRestriction("{alias}.data->'collection'->>'collector' IS NOT NULL")
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
}
