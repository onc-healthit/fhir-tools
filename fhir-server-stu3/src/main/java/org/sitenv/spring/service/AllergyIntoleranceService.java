package org.sitenv.spring.service;

import org.sitenv.spring.model.DafAllergyIntolerance;

import java.util.Date;
import java.util.List;

public interface AllergyIntoleranceService {

    public List<DafAllergyIntolerance> getAllAllergyIntolerance();

    public DafAllergyIntolerance getAllergyIntoleranceResourceById(int id);

    public List<DafAllergyIntolerance> getAllergyIntoleranceByPatient(String patient);

	public List<DafAllergyIntolerance> getAllergyIntoleranceForBulkData(List<Integer> patients, Date start);

}
