package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafImagingStudy;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("imagingStudyDao")
public class ImagingStudyDaoImpl extends AbstractDao implements ImagingStudyDao {

	/**
	 * This method builds criteria for fetching ImagingStudy record by id.
	 * @param id : ID of the resource
	 * @return : DAF object of the ImagingStudy
	 */
	public DafImagingStudy getImagingStudyById(String id) {
		
		Criteria criteria = getSession().createCriteria(DafImagingStudy.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"' order by {alias}.data->'meta'->>'versionId' desc"));
		return (DafImagingStudy) criteria.list().get(0);
    }
	
	/**
	 * This method builds criteria for fetchin
	 * g particular version of the  record by id.
	 * @param theId : ID of the ImagingStudy
	 * @param versionId : version of the ImagingStudy record
	 * @return : DAF object of the ImagingStudy
	 */
	public DafImagingStudy getImagingStudyByVersionId(String theId, String versionId) {
		
		Criteria criteria = getSession().createCriteria(DafImagingStudy.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Conjunction versionConjunction = Restrictions.conjunction();
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->'meta'->>'versionId' = '" +versionId+"'"));
		versionConjunction.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +theId+"'"));
		criteria.add(versionConjunction);
		return (DafImagingStudy) criteria.uniqueResult();
	}
	
	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DAF patient object
	 */
	@SuppressWarnings("unchecked")
	public List<DafImagingStudy> search(SearchParameterMap theMap) {
        Criteria criteria = getSession().createCriteria(DafImagingStudy.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        //build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);

        //build criteria for status
        buildStatusCriteria(theMap, criteria);

        //build criteria for modality
        buildModalityCriteria(theMap, criteria);

        //build criteria for subject
        buildSubjectCriteria(theMap, criteria);

        //build criteria for started
        buildStartedCriteria(theMap, criteria);
        
        //build criteria for beasedOn
        buildBasedOnCriteria(theMap, criteria);
        
        //build criteria for endpoint
        buildEndpointCriteria(theMap, criteria);
        
        //build criteria for bodysite
        buildBodysiteCriteria(theMap, criteria);
        
        //build criteria for performer
        buildPerformerCriteria(theMap, criteria);
        
        return criteria.list();
    }

	/**
	 * This method builds criteria for patient id
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
	 * This method builds criteria for patient identifier
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
	 * This method builds criteria for imagingStudy status
	 * @param theMap : search parameter "status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
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
	 * This method builds criteria for imagingStudy modality
	 * @param theMap : search parameter "modality"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildModalityCriteria(SearchParameterMap theMap, Criteria criteria) {
		 List<List<? extends IQueryParameterType>> list = theMap.get("modality");
	        if (list != null) {

	            for (List<? extends IQueryParameterType> values : list) {
	                Disjunction disjunction = Restrictions.disjunction();
	                for (IQueryParameterType params : values) {
	                    StringParam modalityName = (StringParam) params;
	                    Criterion orCond= null;
	                    if (modalityName.isExact()) {
	                    	orCond = Restrictions.or(
	                    				Restrictions.sqlRestriction("{alias}.data->'modality'->0->>'system' = '" +modalityName.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'modality'->1>>'system' = '" +modalityName.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'modality'->0->>'code' = '" +modalityName.getValue()+"'"),
	                    				Restrictions.sqlRestriction("{alias}.data->'modality'->1->>'code' = '" +modalityName.getValue()+"'")
	                    			);
	                    } else if (modalityName.isContains()) {
	                    	orCond = Restrictions.or(
		                    			Restrictions.sqlRestriction("{alias}.data->'modality'->0->>'system' ilike '%" +modalityName.getValue()+"%'"),
		                				Restrictions.sqlRestriction("{alias}.data->'modality'->1->>'system' ilike '%" +modalityName.getValue()+"%'"),
		                				Restrictions.sqlRestriction("{alias}.data->'modality'->0->>'code' ilike '%" +modalityName.getValue()+"%'"),
		                				Restrictions.sqlRestriction("{alias}.data->'modality'->1->>'code' ilike '%" +modalityName.getValue()+"%'")
	                    			);
	                    } else {
	                    	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'modality'->0->>'system' ilike '" +modalityName.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'modality'->1->>'system' ilike '" +modalityName.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'modality'->0->>'code' ilike '" +modalityName.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'modality'->1->>'code' ilike '" +modalityName.getValue()+"%'")
	                			);
	                    }
	                    disjunction.add(orCond);
	                }
	                criteria.add(disjunction);
	            }
	        }
	}

	/**
	 * This method builds criteria for imagingStudy subject
	 * @param theMap : search parameter "subject"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSubjectCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("subject");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam subjectName = (StringParam) params;
                    Criterion orCond= null;
                    if (subjectName.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' = '" +subjectName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' = '" +subjectName.getValue()+"'")
                    			);
                    } else if (subjectName.isContains()) {
                    	orCond = Restrictions.or(
                        			Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '%" + subjectName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' ilike '%" + subjectName.getValue() + "%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '" + subjectName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' ilike '" + subjectName.getValue() + "%'")
	                        		
                    			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
		
	}

	/**
	 * This method builds criteria for imagingStudy started
	 * @param theMap : search parameter "started"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildStartedCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("started");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    DateParam startedDate = (DateParam) params;
                    String startedDateFormat = startedDate.getValueAsString();
                    if(startedDate.getPrefix() != null) {
                        if(startedDate.getPrefix().getValue() == "gt"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'started')::DATE > '"+startedDateFormat+ "'"));
                        }else if(startedDate.getPrefix().getValue() == "lt"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'started')::DATE < '"+startedDateFormat+ "'"));
                        }else if(startedDate.getPrefix().getValue() == "ge"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'started')::DATE >= '"+startedDateFormat+ "'"));
                        }else if(startedDate.getPrefix().getValue() == "le"){
                            criteria.add(Restrictions.sqlRestriction("({alias}.data->>'started')::DATE <= '"+startedDateFormat+ "'"));
                        }else if(startedDate.getPrefix().getValue() == "ne"){
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'started')::DATE != '"+startedDateFormat+ "'"));
						}else if(startedDate.getPrefix().getValue() == "eq"){
							criteria.add(Restrictions.sqlRestriction("({alias}.data->>'started')::DATE = '"+startedDateFormat+ "'"));
						}
                    }else {
						criteria.add(Restrictions.sqlRestriction("({alias}.data->>'started')::DATE = '"+startedDateFormat+"'"));
					}
                }            
            }
        }
		
	}

	/**
	 * This method builds criteria for imagingStudy basedon
	 * @param theMap : search parameter "basedon"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildBasedOnCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("basedon");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam basedonName = (StringParam) params;
                    Criterion orCond= null;
                    if (basedonName.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'basedOn'->0->>'reference' = '" +basedonName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'basedOn'->0->>'display' = '" +basedonName.getValue()+"'")
                    			);
                    } else if (basedonName.isContains()) {
                    	orCond = Restrictions.or(
                        			Restrictions.sqlRestriction("{alias}.data->'basedOn'->0->>'reference' ilike '%" + basedonName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'basedOn'->0->>'display' ilike '%" + basedonName.getValue() + "%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'basedOn'->0->>'reference' ilike '" + basedonName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'basedOn'->0->>'display' ilike '" + basedonName.getValue() + "%'")
	                        		
                    			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}

	/**
	 * This method builds criteria for imagingStudy endpoint
	 * @param theMap : search parameter "endpoint"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildEndpointCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("endpoint");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam endpointName = (StringParam) params;
                    Criterion orCond= null;
                    if (endpointName.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'endpoint'->0->>'reference' = '" +endpointName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'endpoint'->0->>'display' = '" +endpointName.getValue()+"'")
                    			);
                    } else if (endpointName.isContains()) {
                    	orCond = Restrictions.or(
                        			Restrictions.sqlRestriction("{alias}.data->'endpoint'->0->>'reference' ilike '%" + endpointName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'endpoint'->0->>'display' ilike '%" + endpointName.getValue() + "%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'endpoint'->0->>'reference' ilike '" + endpointName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'endpoint'->0->>'display' ilike '" + endpointName.getValue() + "%'")
	                        		
                    			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}

	/**
	 * This method builds criteria for imagingStudy bodysite
	 * @param theMap : search parameter "bodysite"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildBodysiteCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("bodysite");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam bodysiteName = (StringParam) params;
                    Criterion orCond= null;
                    if (bodysiteName.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'bodySite'->>'code' = '" +bodysiteName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'bodySite'->>'system' = '" +bodysiteName.getValue()+"'")
                    			);
                    } else if (bodysiteName.isContains()) {
                    	orCond = Restrictions.or(
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'bodySite'->>'code' ilike '%" + bodysiteName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'bodySite'->>'system' ilike '%" + bodysiteName.getValue() + "%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'bodySite'->>'code' ilike '" + bodysiteName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'bodySite'->>'system' ilike '" + bodysiteName.getValue() + "%'")
	                        		
                    			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}

	/**
	 * This method builds criteria for imagingStudy performer
	 * @param theMap : search parameter "performer"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPerformerCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("performer");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam performerName = (StringParam) params;
                    Criterion orCond= null;
                    if (performerName.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'function'->'coding'->0->>'code' = '" +performerName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'function'->'coding'->0->>'system' = '" +performerName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'actor'->>'reference' = '" +performerName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'actor'->>'type' = '" +performerName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'actor'->>'display' = '" +performerName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'function'->'coding'->0->>'code' = '" +performerName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'function'->'coding'->0->>'system' = '" +performerName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'actor'->>'reference' = '" +performerName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'actor'->>'type' = '" +performerName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'actor'->>'display' = '" +performerName.getValue()+"'")
                    				
                    			);
                    } else if (performerName.isContains()) {
                    	orCond = Restrictions.or(
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'function'->'coding'->0->>'code' ilike '%" + performerName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'function'->'coding'->0->>'system' ilike '%" + performerName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'actor'->>'reference' ilike '%" + performerName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'actor'->>'type' ilike '%" + performerName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'actor'->>'display' ilike '%" + performerName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'function'->'coding'->0->>'code' ilike '%" + performerName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'function'->'coding'->0->>'system' ilike '%" + performerName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'actor'->>'reference' ilike '%" + performerName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'actor'->>'type' ilike '%" + performerName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'actor'->>'display' ilike '%" + performerName.getValue() + "%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'function'->'coding'->0->>'code' ilike '" + performerName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'function'->'coding'->0->>'system' ilike '" + performerName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'actor'->>'reference' ilike '" + performerName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'actor'->>'type' ilike '" + performerName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->0->'actor'->>'display' ilike '" + performerName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'function'->'coding'->0->>'code' ilike '" + performerName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'function'->'coding'->0->>'system' ilike '" + performerName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'actor'->>'reference' ilike '" + performerName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'actor'->>'type' ilike '" + performerName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'series'->0->'performer'->1->'actor'->>'display' ilike '" + performerName.getValue() + "%'")
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
     * @param id : ID of the patient
     * @return : List of patient DAF records
     */
	@SuppressWarnings("unchecked")
	public List<DafImagingStudy> getImagingStudyHistoryById(String id) {
		Criteria criteria = getSession().createCriteria(DafImagingStudy.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id+"'"));
		return (List<DafImagingStudy>) criteria.list();
	}

	
}
