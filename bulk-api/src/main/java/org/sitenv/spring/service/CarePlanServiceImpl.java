package org.sitenv.spring.service;

import org.sitenv.spring.dao.CarePlanDao;
import org.sitenv.spring.model.DafCarePlan;
import org.sitenv.spring.model.DafCarePlanParticipant;
import org.sitenv.spring.query.CarePlanSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service("carePlanResourceService")
@Transactional
public class CarePlanServiceImpl implements CarePlanService {

    @Autowired
    private CarePlanDao carePlanDao;

    @Override
    @Transactional
    public List<DafCarePlan> getAllCarePlans() {
        return this.carePlanDao.getAllCarePlans();
    }

    @Override
    @Transactional
    public DafCarePlan getCarePlanById(int id) {
        return this.carePlanDao.getCarePlanById(id);
    }

    @Override
    @Transactional
    public List<DafCarePlan> getCarePlanByPatient(String patient) {
        return this.carePlanDao.getCarePlanByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafCarePlan> getCarePlanBySearchCriteria(CarePlanSearchCriteria carePlanSearchCriteria) {
        return this.carePlanDao.getCarePlanBySearchCriteria(carePlanSearchCriteria);
    }

    @Override
    @Transactional
    public List<DafCarePlanParticipant> getCarePlanparticipantByCareTeam(int id) {
        return this.carePlanDao.getCarePlanparticipantByCareTeam(id);
    }

    
    public List<DafCarePlan> getCarePlanForBulkData(List<Integer> patients, Date start){
    	return this.carePlanDao.getCarePlanForBulkData(patients, start);
    }

}
