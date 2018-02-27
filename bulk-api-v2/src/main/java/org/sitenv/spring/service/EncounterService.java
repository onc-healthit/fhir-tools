package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafEncounter;
import org.sitenv.spring.query.EncounterSearchCriteria;

import ca.uhn.fhir.model.dstu2.resource.Encounter;

public interface EncounterService {
	
	 public List<DafEncounter> getAllEncounter();

	    public DafEncounter getEncounterResourceById(int id);
	    
	    public List<DafEncounter> getEncounterBySearchOptions(EncounterSearchCriteria encounterSearchCriteria);

	    public List<DafEncounter> getEncounterrByPatient(String patient);
	    
	    public DafEncounter saveEncounter(DafEncounter  de);
	    
	    public DafEncounter updateEncounter(DafEncounter de);
}
