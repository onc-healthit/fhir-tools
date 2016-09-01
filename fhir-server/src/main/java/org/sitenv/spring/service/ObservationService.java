package org.sitenv.spring.service;

import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.query.ObservationSearchCriteria;

import java.util.List;

public interface ObservationService {

    public List<DafObservation> getAllObservations();

    public DafObservation getObservationResourceById(int id);

    public List<DafObservation> getObservationByPatient(String patient);

    public List<DafObservation> getObservationBySearchCriteria(ObservationSearchCriteria observationSearchCriteria);

    public List<DafObservation> getObservationByBPCode(String BPCOde);
}
