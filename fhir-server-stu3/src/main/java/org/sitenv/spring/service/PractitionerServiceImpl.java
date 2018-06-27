package org.sitenv.spring.service;

import org.sitenv.spring.dao.PractitionerDao;
import org.sitenv.spring.model.DafPractitioner;
import org.sitenv.spring.query.PractitionerSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("practitionerService")
@Transactional
public class PractitionerServiceImpl implements PractitionerService {

    @Autowired
    private PractitionerDao practitionerDao;


    @Override
    @Transactional
    public DafPractitioner getPractitionerById(int id) {
        return this.practitionerDao.getPractitionerById(id);
    }

    @Override
    @Transactional
    public List<DafPractitioner> getAllPractitioners() {
        return this.practitionerDao.getAllPractitioners();
    }

    @Override
    @Transactional
    public List<DafPractitioner> getPractitionerBySearchCriteria(PractitionerSearchCriteria practitionerSearchCriteria) {
        return this.practitionerDao.getPractitionerBySearchCriteria(practitionerSearchCriteria);
    }

}
