package org.sitenv.spring.service;

import org.sitenv.spring.model.DafMedication;

import java.util.List;

public interface MedicationService {

    public List<DafMedication> getAllMedication();

    public DafMedication getMedicationResourceById(int id);

    public List<DafMedication> getMedicationByCode(String code);

}
