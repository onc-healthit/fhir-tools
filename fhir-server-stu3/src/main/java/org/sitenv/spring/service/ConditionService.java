package org.sitenv.spring.service;

import org.sitenv.spring.model.DafCondition;
import org.sitenv.spring.query.ConditionSearchCriteria;

import java.util.List;

public interface ConditionService {

    public List<DafCondition> getAllConditions();

    public DafCondition getConditionResourceById(int id);

    public List<DafCondition> getConditionByPatient(String patient);

    public List<DafCondition> getConditionBySearchOptions(ConditionSearchCriteria conditionSearchCriteria);

	public List<DafCondition> getConditionForBulkData(List<Integer> patients, java.util.Date start);

}
