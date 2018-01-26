package org.sitenv.spring.service;

import java.util.Date;
import java.util.List;

import org.sitenv.spring.dao.PractitionerDao;
import org.sitenv.spring.model.DafPractitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<DafPractitioner> getPractitionerForBulkData(List<Integer> patients, Date start){
    	return this.practitionerDao.getPractitionerForBulkData(patients, start);
    }
}
