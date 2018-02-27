package org.sitenv.spring.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.model.DafEncounter;
import org.sitenv.spring.model.DafMedicationOrder;
import org.sitenv.spring.query.ConditionSearchCriteria;
import org.sitenv.spring.query.EncounterSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.model.dstu2.resource.Encounter;

@Repository("encounterDao")
public class EncounterDaoImpl extends AbstractDao implements EncounterDao {
	
	 private static final Logger logger = LoggerFactory.getLogger(EncounterDaoImpl.class);

	 
	    
	    
	    
	    @Override
	    public DafEncounter saveEncounter(DafEncounter  de) {
	    	try {
	    	 getSession().save(de);
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    		System.out.println(e.getMessage());
	    	}
	         return  de;	
	    }
	    
	    
	    @Override
	    public DafEncounter updateEncounter(DafEncounter de) {
	    	getSession().update(de);
	    	return de;
	    }
	    
	    
	    @SuppressWarnings("unchecked")
	    @Override
	 public List<DafEncounter> getAllEncounter(){
	    	
	    	 Criteria criteria = getSession().createCriteria(DafEncounter.class);
	         return (List<DafEncounter>) criteria.list();
	    }

	    @Override
	    public DafEncounter getEncounterResourceById(int id) {
	    	DafEncounter dafEncounter = (DafEncounter) getSession().get(DafEncounter.class, id);
	          return dafEncounter;
	    }
	    
	    @Override
	    public List<DafEncounter> getEncounterBySearchOptions(EncounterSearchCriteria encounterSearchCriteria){
	    	List<DafEncounter> dafEncounter = getEncounters(encounterSearchCriteria);
	        return dafEncounter;
	    }
	    
	    public List<DafEncounter> getEncounters(EncounterSearchCriteria encounterSearchCriteria) {

	        Criteria criteria = getSession().createCriteria(DafEncounter.class, "encounter").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

	      

	        if (encounterSearchCriteria.getStatus() != null && encounterSearchCriteria.getStatus().size() > 0) {
	            criteria.add(Restrictions.in("status", encounterSearchCriteria.getStatus()));
	            //criteria.add(Expression.in("clinical_status", conditionSearchCriteria.getStatus()));
	        }
	        return (List<DafEncounter>) criteria.list();
	    }

	    @Override
	    public List<DafEncounter> getEncounterrByPatient(String patient){
	        Criteria criteria = getSession().createCriteria(DafEncounter.class, "encounter")
	                .createAlias("encounter.patient", "dp")
	                .add(Restrictions.eq("dp.id", Integer.valueOf(patient)));
	        List<DafEncounter> dafEncounter = criteria.list();
	        return dafEncounter;
	    }
	    
	    
}
