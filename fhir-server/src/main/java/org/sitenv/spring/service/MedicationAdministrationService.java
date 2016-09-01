package org.sitenv.spring.service;

import org.sitenv.spring.model.DafMedicationAdministration;

import java.util.List;

public interface MedicationAdministrationService {

    public List<DafMedicationAdministration> getAllMedicationAdministration();

    public DafMedicationAdministration getMedicationAdministrationResourceById(int id);

    public List<DafMedicationAdministration> getMedicationAdministrationByPatient(String patient);

    public List<DafMedicationAdministration> getMedicationAdministrationByCode(String code);

    public List<DafMedicationAdministration> getMedicationAdministrationByIdentifier(String identifierSystem, String identifierValue);

    public List<DafMedicationAdministration> getMedicationAdministrationByMedication(String medication);

    public List<DafMedicationAdministration> getMedicationAdministrationByStatus(String status);

}
