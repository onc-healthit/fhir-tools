package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.query.PractitionerSearchCriteria;

import java.util.List;

public interface PractitionerDao {

    public DafPractitioner getPractitionerById(int id);

    public List<DafPractitioner> getAllPractitioners();

    public List<DafPractitioner> getPractitionerBySearchCriteria(PractitionerSearchCriteria practitionerSearchCriteria);
}
