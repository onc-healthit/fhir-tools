package org.sitenv.spring.service;

import org.sitenv.spring.dao.MedicationRequestDao;
import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.query.MedicationRequestSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("medicationRequestResourceService")
@Transactional
public class MedicationRequestServiceImpl implements MedicationRequestService {

    @Autowired
    private MedicationRequestDao medicationRequestDao;

    @Override
    @Transactional
    public List<DafMedicationRequest> getAllMedicationRequest() {
        return this.medicationRequestDao.getAllMedicationRequest();
    }

    @Override
    @Transactional
    public DafMedicationRequest getMedicationRequestResourceById(int id) {
        return this.medicationRequestDao.getMedicationRequestResourceById(id);
    }

    @Override
    @Transactional
    public List<DafMedicationRequest> getMedicationRequestBySearchCriteria(MedicationRequestSearchCriteria medicationRequestSearchCriteria) {
        return this.medicationRequestDao.getMedicationRequestBySearchCriteria(medicationRequestSearchCriteria);
    }

	@Override
	public List<DafMedicationRequest> getMedicationRequestForBulkData(List<Integer> patients, Date start) {
		return this.medicationRequestDao.getMedicationRequestForBulkData(patients, start);
	}

}
