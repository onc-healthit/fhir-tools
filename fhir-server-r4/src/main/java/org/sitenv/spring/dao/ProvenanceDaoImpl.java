package org.sitenv.spring.dao;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafProvenance;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
@Repository("provenanceDao")
public class ProvenanceDaoImpl extends AbstractDao implements ProvenanceDao {

	/**
	 * This method builds criteria for fetching provenance record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the proveance
	 */
	public DafProvenance getProvenanceById(String id) {
		List<DafProvenance> list = getSession().createNativeQuery(
				"select * from provenance where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", DafProvenance.class)
					.getResultList();
			return list.get(0);
	}
	/**
	 * This method builds criteria for fetching particular version of the provenance
	 * record by id.
	 * 
	 * @param theId     : ID of the provenance
	 * @param versionId : version of the provenance record
	 * @return : DAF object of the provenance
	 */
	public DafProvenance getProvenanceByVersionId(String theId, String versionId) {
		DafProvenance list = getSession().createNativeQuery(
				"select * from provenance where data->>'id' = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", DafProvenance.class)
					.getSingleResult();
			return list;
	}

	/**
	 * This method builds criteria for fetching history of the provenance by id
	 * 
	 * @param theId : ID of the provenance
	 * @return : List of provenance DAF records
	 */
	public List<DafProvenance> getProvenanceHistoryById(String theId) {
		List<DafProvenance> list = getSession().createNativeQuery(
				"select * from provenance where data->>'id' = '"+theId+"' order by data->'meta'->>'versionId' desc", DafProvenance.class)
		    	.getResultList();
		return list;
	}

	/**
	 * This method builds criteria for fetching history of the provenance by id
	 *
	 * @param resourceIDs : ID of the resource
	 * @return : List of provenance DAF records
	 */
	public List<DafProvenance> getProvenanceByResourceId(List<String> resourceIDs) {
		StringBuffer resourceIDForINOperator = new StringBuffer();
		if(resourceIDs.size() > 0) {
			boolean first = true;
			for(String resourceID : resourceIDs) {
				if(first)  resourceIDForINOperator.append("'"+resourceID+"'");
				else resourceIDForINOperator.append(",'"+resourceID+"'");
				first=false;
			}
		}

		List<DafProvenance> list = getSession().createNativeQuery(
				"select * from provenance\n" +
						"where id in (\n" +
						"select distinct(id) from provenance r,\n" +
						"LATERAL json_array_elements(r.data->'target') segment\n" +
						"WHERE segment ->>'reference' in ("+ resourceIDForINOperator.toString() +")"+
						")"	, DafProvenance.class)
				.getResultList();
		return list;

	}




	@Override
	@SuppressWarnings("unchecked")
	public List<DafProvenance> search(SearchParameterMap theMap) {
		Criteria criteria = getSession().createCriteria(DafProvenance.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		//build criteria for id
		buildIdCriteria(theMap, criteria);
		
		//build criteria for target
		buildTargetCriteria(theMap, criteria);
		
		//build criteria for patient
		buildPatientCriteria(theMap, criteria);
		
		//build criteria for location
		buildLocationCriteria(theMap, criteria);
		
		//build criteria for agent
		buildAgentCriteria(theMap, criteria);
				
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
	 * This method builds criteria for Provenance target
	 * @param theMap : search parameter "target"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildTargetCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("target");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam patient = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(patient.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'target'->0->>'reference' = '" +patient.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'target'->0->>'display' = '" + patient.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'target'->0->>'type' = '" + patient.getValue() + "'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'target'->1->>'reference' = '" + patient.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'target'->1->>'display' = '" + patient.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'target'->1->>'type' = '" + patient.getValue() + "'")
    							);
    				}else if(patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'target' IS NULL")
    							);
    				}else if(!patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'target' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
	}
	
	/**
	 * This method builds criteria for Provenance patient
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
    								Restrictions.sqlRestriction("{alias}.data->'target'->0->>'reference' = '" +patient.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'target'->0->>'display' = '" + patient.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'target'->0->>'type' = '" + patient.getValue() + "'"),
    								
    								Restrictions.sqlRestriction("{alias}.data->'target'->1->>'reference' = '" + patient.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'target'->1->>'display' = '" + patient.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'target'->1->>'type' = '" + patient.getValue() + "'")
    							);
    				}else if(patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'target' IS NULL")
    							);
    				}else if(!patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'target' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
	}
	
	/**
	 * This method builds criteria for Provenance location
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
                    if(location.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'location'->>'reference' = '" +location.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'location'->>'display' = '" + location.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'location'->>'type' = '" + location.getValue() + "'")
    							);
    				}else if(location.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'location' IS NULL")
    							);
    				}else if(!location.getMissing()){
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
	 * This method builds criteria for Provenance agent
	 * @param theMap : search parameter "agent
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAgentCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("agent");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam agent = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(agent.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'agent'->0->'who'->>'reference' = '" +agent.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'agent'->0->'who'->>'display' = '" + agent.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'agent'->0->'who'->>'type' = '" + agent.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'agent'->1->'who'->>'reference' = '" +agent.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'agent'->1->'who'->>'display' = '" + agent.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'agent'->1->'who'->>'type' = '" + agent.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'agent'->2->'who'->>'reference' = '" +agent.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'agent'->2->'who'->>'display' = '" + agent.getValue() + "'"),
    								Restrictions.sqlRestriction("{alias}.data->'agent'->2->'who'->>'type' = '" + agent.getValue() + "'")
    							);
    				}else if(agent.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'agent' IS NULL")
    							);
    				}else if(!agent.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'agent' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
	}
}
