package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafGoals;
import org.sitenv.spring.query.GoalsSearchCriteria;

import java.util.List;

public interface GoalsDao {

    public List<DafGoals> getAllGoals();

    public DafGoals getGoalsById(int id);

    public List<DafGoals> getGoalsByPatient(String patient);

    public List<DafGoals> getGoalsBySearchCriteria(GoalsSearchCriteria goalsSearchCriteria);

}
