package org.sitenv.spring.service;

import org.sitenv.spring.dao.PatientDao;
import org.sitenv.spring.model.DafPatientJson;
import org.sitenv.spring.query.PatientSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Prabhushankar.Byrapp on 8/22/2015.
 */

@Service("patientService")
@Transactional
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientDao patientDao;

    @Override
    @Transactional
    public List<DafPatientJson> getAllPatient() {
        return this.patientDao.getAllPatient();
    }

    @Override
    @Transactional
    public DafPatientJson getPatientById(int id) {
        return this.patientDao.getPatientById(id);
    }

    @Override
    @Transactional
    public List<DafPatientJson> getPatientBySearchOption(PatientSearchCriteria searchCriteria) {
        return this.patientDao.getPatientBySearchOption(searchCriteria);
    }


}
