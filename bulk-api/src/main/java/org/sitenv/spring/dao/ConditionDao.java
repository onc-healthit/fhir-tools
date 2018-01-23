package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.query.ConditionSearchCriteria;

import java.util.Date;
import java.util.List;

public interface ConditionDao {

    public List<DafCondition> getAllConditions();

    public DafCondition getConditionResourceById(int id);

    public List<DafCondition> getConditionByPatient(String patient);

    public List<DafCondition> getConditionBySearchOptions(ConditionSearchCriteria conditionSearchCriteria);

    public List<DafCondition> getConditionForBulkData(List<Integer> patients, Date start);
}
