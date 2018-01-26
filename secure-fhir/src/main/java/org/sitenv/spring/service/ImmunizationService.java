package org.sitenv.spring.service;

import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.query.ImmunizationSearchCriteria;

import java.util.Date;
import java.util.List;

public interface ImmunizationService {

    public List<DafImmunization> getAllImmunization();

    public DafImmunization getImmunizationById(int id);

    public List<DafImmunization> getImmunizationByPatient(String Patient);

    public List<DafImmunization> getImmunizationBySearchCriteria(ImmunizationSearchCriteria immunizationSearchCriteria);

    public List<DafImmunization> getImmunizationForBulkData(List<Integer> patients, Date start);
}
