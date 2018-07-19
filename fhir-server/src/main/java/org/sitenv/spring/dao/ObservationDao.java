package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.query.ObservationSearchCriteria;

import java.util.List;

public interface ObservationDao {

    public List<DafObservation> getAllObservations();

    public DafObservation getObservationResourceById(int id);

    public DafObservation getObservationResourceByIdandCategory(int id, String Category);

    public List<DafObservation> getObservationByPatient(String patient);

    public List<DafObservation> getObservationBySearchCriteria(ObservationSearchCriteria observationSearchCriteria);

    public List<DafObservation> getObservationByBPCode(String BPCode);

}
