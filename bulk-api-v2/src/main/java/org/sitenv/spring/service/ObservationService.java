package org.sitenv.spring.service;

import org.sitenv.spring.model.DafLocation;
import org.sitenv.spring.model.DafObservation;
import org.sitenv.spring.query.ObservationSearchCriteria;

import java.util.Date;
import java.util.List;

public interface ObservationService {

    public List<DafObservation> getAllObservations();

    public DafObservation getObservationResourceById(Integer id);
    
    public DafObservation getObservationResourceByIdandCategory(Integer id,String Category);

    public List<DafObservation> getObservationByPatient(String patient);

    public List<DafObservation> getObservationBySearchCriteria(ObservationSearchCriteria observationSearchCriteria);

    public List<DafObservation> getObservationByBPCode(String BPCOde);
    
    public List<DafObservation> getObservationForBulkData(List<Integer> patients, Date start);
}
