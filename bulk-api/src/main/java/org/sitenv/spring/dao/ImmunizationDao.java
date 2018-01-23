package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafDevice;
import org.sitenv.spring.model.DafImmunization;
import org.sitenv.spring.query.ImmunizationSearchCriteria;

import java.util.Date;
import java.util.List;

public interface ImmunizationDao {

    public List<DafImmunization> getAllImmunization();

    public DafImmunization getImmunizationById(int id);

    public List<DafImmunization> getImmunizationByPatient(String patient);

    public List<DafImmunization> getImmunizationBySearchCriteria(ImmunizationSearchCriteria immunizationSearchCriteria);

    public List<DafImmunization> getImmunizationForBulkData(List<Integer> patients, Date start);
}
