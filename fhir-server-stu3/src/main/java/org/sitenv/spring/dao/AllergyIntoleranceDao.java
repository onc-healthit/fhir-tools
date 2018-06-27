package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafAllergyIntolerance;

import java.util.List;

public interface AllergyIntoleranceDao {

    public List<DafAllergyIntolerance> getAllAllergyIntolerance();

    public DafAllergyIntolerance getAllergyIntoleranceResourceById(int id);

    public List<DafAllergyIntolerance> getAllergyIntoleranceByPatient(String patient);

}
