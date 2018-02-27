package org.sitenv.spring.service;

import org.sitenv.spring.dao.GoalsDao;
import org.sitenv.spring.model.DafGoals;
import org.sitenv.spring.query.GoalsSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("goalsResourceService")
@Transactional
public class GoalsServiceImpl implements GoalsService {

    @Autowired
    private GoalsDao goalsDao;

    @Override
    @Transactional
    public List<DafGoals> getAllGoals() {
        return this.goalsDao.getAllGoals();
    }

    @Override
    @Transactional
    public DafGoals getGoalsById(int id) {
        return this.goalsDao.getGoalsById(id);
    }

    @Override
    @Transactional
    public List<DafGoals> getGoalsByPatient(String patient) {
        return this.goalsDao.getGoalsByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafGoals> getGoalsBySearchCriteria(GoalsSearchCriteria goalsSearchCriteria) {
        return this.goalsDao.getGoalsBySearchCriteria(goalsSearchCriteria);
    }

    @Override
    @Transactional
    public List<DafGoals> getGoalsForBulkData(List<Integer> patients, Date start){
    	return this.goalsDao.getGoalsForBulkData(patients, start);
    }
}
