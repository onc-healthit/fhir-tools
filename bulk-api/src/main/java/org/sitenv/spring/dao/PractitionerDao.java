package org.sitenv.spring.dao;

import java.util.Date;
import java.util.List;

import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.model.DafPractitioner;

public interface PractitionerDao {

    public DafPractitioner getPractitionerById(int id);
    
    public List<DafPractitioner> getPractitionerForBulkData(List<Integer> patients, Date start);
}
