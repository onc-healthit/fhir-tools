package org.sitenv.spring.service;

import org.sitenv.spring.dao.AllergyIntoleranceDao;
import org.sitenv.spring.model.DafAllergyIntolerance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("allergyIntoleranceResourceService")
@Transactional
public class AllergyIntoleranceServiceImpl implements AllergyIntoleranceService {

    @Autowired
    private AllergyIntoleranceDao allergyIntoleranceDao;

    @Override
    @Transactional
    public List<DafAllergyIntolerance> getAllAllergyIntolerance() {
        return this.allergyIntoleranceDao.getAllAllergyIntolerance();
    }

    @Override
    @Transactional
    public DafAllergyIntolerance getAllergyIntoleranceResourceById(int id) {
        return this.allergyIntoleranceDao.getAllergyIntoleranceResourceById(id);
    }

    @Override
    @Transactional
    public List<DafAllergyIntolerance> getAllergyIntoleranceByPatient(String patient) {
        return this.allergyIntoleranceDao.getAllergyIntoleranceByPatient(patient);
    }
}
