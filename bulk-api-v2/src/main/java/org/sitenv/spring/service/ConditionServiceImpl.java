package org.sitenv.spring.service;

import org.sitenv.spring.dao.ConditionDao;
import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.query.ConditionSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("conditionResourceService")
@Transactional
public class ConditionServiceImpl implements ConditionService {

    @Autowired
    private ConditionDao conditionDao;

    @Override
    @Transactional
    public List<DafCondition> getAllConditions() {
        return this.conditionDao.getAllConditions();
    }

    @Override
    @Transactional
    public DafCondition getConditionResourceById(int id) {
        return this.conditionDao.getConditionResourceById(id);
    }

    @Override
    @Transactional
    public List<DafCondition> getConditionByPatient(String patient) {
        return this.conditionDao.getConditionByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafCondition> getConditionBySearchOptions(ConditionSearchCriteria conditionSearchCriteria) {
        return this.conditionDao.getConditionBySearchOptions(conditionSearchCriteria);
    }
    
    @Override
    @Transactional
    public List<DafCondition> getConditionForBulkData(List<Integer> patients, Date start) {
    return this.conditionDao.getConditionForBulkData(patients, start);
}
}
