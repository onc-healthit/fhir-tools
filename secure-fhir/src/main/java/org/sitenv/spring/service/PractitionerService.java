package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;

import org.sitenv.spring.model.DafPatientJson;
import org.sitenv.spring.model.DafPractitioner;

public interface PractitionerService {

    public DafPractitioner getPractitionerById(int id);

    public List<DafPractitioner> getPractitionerForBulkData(List<Integer> patients, Date start);
}
