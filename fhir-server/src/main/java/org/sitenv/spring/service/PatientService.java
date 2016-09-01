package org.sitenv.spring.service;

import org.sitenv.spring.model.DafPatientJson;
import org.sitenv.spring.query.PatientSearchCriteria;

import java.util.List;

/**
 * Created by Prabhushankar.Byrapp on 8/22/2015.
 */
public interface PatientService {

    public List<DafPatientJson> getAllPatient();

    public DafPatientJson getPatientById(int id);

    public List<DafPatientJson> getPatientBySearchOption(PatientSearchCriteria criteria);

}
