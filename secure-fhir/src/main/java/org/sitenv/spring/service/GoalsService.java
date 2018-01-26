package org.sitenv.spring.service;

import org.sitenv.spring.model.DafGoals;
import org.sitenv.spring.query.GoalsSearchCriteria;

import java.util.Date;
import java.util.List;

public interface GoalsService {

    public List<DafGoals> getAllGoals();

    public DafGoals getGoalsById(int id);

    public List<DafGoals> getGoalsByPatient(String Patient);

    public List<DafGoals> getGoalsBySearchCriteria(GoalsSearchCriteria goalsSearchCriteria);

    public List<DafGoals> getGoalsForBulkData(List<Integer> patients, Date start);
}
