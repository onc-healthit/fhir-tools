package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.dao.AllergyIntoleranceDao;
import org.sitenv.spring.dao.EncounterDao;
import org.sitenv.spring.model.DafEncounter;
import org.sitenv.spring.query.EncounterSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.model.dstu2.resource.Encounter;

@Service("encounterResourceService")
@Transactional
public class EncounterServiceIml  implements EncounterService{

	    @Autowired
	    private EncounterDao encounterDao;
	 
	 
	     @Override
	    @Transactional
	    public List<DafEncounter> getAllEncounter(){
		 return this.encounterDao.getAllEncounter();
	 }
	     
	     
	     @Override
		 @Transactional
	     public DafEncounter saveEncounter(DafEncounter  de) {
	    	 return this.encounterDao.saveEncounter(de);
	     }
	     
	     
	     @Override
		 @Transactional
	     public DafEncounter updateEncounter(DafEncounter de) {
	    	 return this.encounterDao.updateEncounter(de);
	     }
	     
	 
	    @Override
	    @Transactional
	    public DafEncounter getEncounterResourceById(int id) {
	    	return this.encounterDao.getEncounterResourceById(id);
	    }
	    
	    @Override
	    @Transactional
	    public List<DafEncounter> getEncounterBySearchOptions(EncounterSearchCriteria encounterSearchCriteria){
	    	
	    	return this.encounterDao.getEncounterBySearchOptions(encounterSearchCriteria);
	    }
	    
	    
	    @Override
	    @Transactional
	    public List<DafEncounter> getEncounterrByPatient(String patient){
	    	return this.encounterDao.getEncounterrByPatient(patient);
	    }
}

