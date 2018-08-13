package org.sitenv.spring.service;

import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.model.DafCarePlanParticipant;
import org.sitenv.spring.query.CarePlanSearchCriteria;

import java.util.Date;
import java.util.List;

public interface CarePlanService {

    public List<DafCarePlan> getAllCarePlan();

    public DafCarePlan getCarePlanById(int id);

    public List<DafCarePlan> getCarePlanByPatient(String Patient);

    public List<DafCarePlan> getCarePlanBySearchCriteria(CarePlanSearchCriteria carePlanSearchCriteria);

    public List<DafCarePlanParticipant> getCarePlanparticipantByCarePlan(int id);

	public List<DafCarePlan> getCarePlanForBulkData(List<Integer> patients, Date start);

}
