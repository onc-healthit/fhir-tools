package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafAllergyIntolerance;
import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.model.DafCarePlanParticipant;
import org.sitenv.spring.query.CarePlanSearchCriteria;

import java.util.Date;
import java.util.List;

public interface CarePlanDao {

    public List<DafCarePlan> getAllCarePlans();

    public DafCarePlan getCarePlanById(int id);

    public List<DafCarePlan> getCarePlanByPatient(String patient);

    public List<DafCarePlan> getCarePlanBySearchCriteria(CarePlanSearchCriteria carePlanSearchCriteria);

    public List<DafCarePlanParticipant> getCarePlanparticipantByCareTeam(int id);
   
    public List<DafCarePlan> getCarePlanForBulkData(List<Integer> patients, Date start);

}
