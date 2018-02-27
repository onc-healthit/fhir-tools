package org.sitenv.spring.service;

import org.sitenv.spring.dao.ImmunizationDao;
import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.query.ImmunizationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("immunizationResourceService")
@Transactional
public class ImmunizationServiceImpl implements ImmunizationService {

    @Autowired
    private ImmunizationDao immunizationDao;

    @Override
    @Transactional
    public List<DafImmunization> getAllImmunization() {
        return this.immunizationDao.getAllImmunization();
    }

    @Override
    @Transactional
    public DafImmunization getImmunizationById(int id) {
        return this.immunizationDao.getImmunizationById(id);
    }

    @Override
    @Transactional
    public List<DafImmunization> getImmunizationByPatient(String patient) {
        return this.immunizationDao.getImmunizationByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafImmunization> getImmunizationBySearchCriteria(ImmunizationSearchCriteria immunizationSearchCriteria) {
        return this.immunizationDao.getImmunizationBySearchCriteria(immunizationSearchCriteria);
    }
    
    @Override
    @Transactional
    public List<DafImmunization> getImmunizationForBulkData(List<Integer> patients, Date start) {
    	return this.immunizationDao.getImmunizationForBulkData(patients, start);
    }

}
