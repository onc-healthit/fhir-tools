package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.model.DafCarePlanParticipant;
import org.sitenv.spring.query.CarePlanSearchCriteria;

import java.util.Date;
import java.util.List;

public interface CarePlanDao {

    public List<DafCarePlan> getAllCarePlan();

    public DafCarePlan getCarePlanById(int id);

    public List<DafCarePlan> getCarePlanByPatient(String patient);

    public List<DafCarePlan> getCarePlanBySearchCriteria(CarePlanSearchCriteria CarePlanSearchCriteria);

    public List<DafCarePlanParticipant> getCarePlanparticipantByCarePlan(int id);

	public List<DafCarePlan> getCarePlanForBulkData(List<Integer> patients, Date start);

}
