package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.model.DafEncounter;
import org.sitenv.spring.model.DafMedicationOrder;
import org.sitenv.spring.query.ConditionSearchCriteria;
import org.sitenv.spring.query.EncounterSearchCriteria;

import ca.uhn.fhir.model.dstu2.resource.Encounter;

public interface EncounterDao {

	 public List<DafEncounter> getAllEncounter();

	    public DafEncounter getEncounterResourceById(int id);
	    
	    public List<DafEncounter> getEncounterBySearchOptions(EncounterSearchCriteria encounterSearchCriteria);
	    
	    public List<DafEncounter> getEncounterrByPatient(String patient);
	    
	    public DafEncounter saveEncounter(DafEncounter  de);
	    
	   public DafEncounter updateEncounter(DafEncounter de);
}
