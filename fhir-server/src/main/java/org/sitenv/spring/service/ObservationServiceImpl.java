package org.sitenv.spring.service;

import org.sitenv.spring.dao.ObservationDao;
import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.query.ObservationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("observationResourceService")
@Transactional
public class ObservationServiceImpl implements ObservationService {

    @Autowired
    private ObservationDao observationDao;

    @Override
    @Transactional
    public List<DafObservation> getAllObservations() {
        return this.observationDao.getAllObservations();
    }

    @Override
    @Transactional
    public DafObservation getObservationResourceById(int id) {
        return this.observationDao.getObservationResourceById(id);
    }

    @Override
    @Transactional
    public DafObservation getObservationResourceByIdandCategory(int id, String category) {
        return this.observationDao.getObservationResourceByIdandCategory(id, category);
    }

    @Override
    @Transactional
    public List<DafObservation> getObservationByPatient(String patient) {
        return this.observationDao.getObservationByPatient(patient);
    }

    @Override
    @Transactional
    public List<DafObservation> getObservationBySearchCriteria(ObservationSearchCriteria observationSearchCriteria) {
        return this.observationDao.getObservationBySearchCriteria(observationSearchCriteria);
    }

    @Override
    @Transactional
    public List<DafObservation> getObservationByBPCode(String BPCode) {
        return this.observationDao.getObservationByBPCode(BPCode);
    }
}
