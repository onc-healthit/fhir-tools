package org.sitenv.spring.service;

import org.sitenv.spring.dao.MedicationRequestDao;
import org.sitenv.spring.model.DafMedicationRequest;
import org.sitenv.spring.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("medicationRequestService")
@Transactional
public class MedicationRequestServiceImpl implements MedicationRequestService {
	@Autowired
    private MedicationRequestDao medicationRequestDao;
	
	@Transactional
    public DafMedicationRequest getMedicationRequestById(String id) {
        return this.medicationRequestDao.getMedicationRequestById(id);
    }
	
	@Transactional
	public DafMedicationRequest getMedicationRequestByVersionId(String theId, String versionId) {
		return this.medicationRequestDao.getMedicationRequestByVersionId(theId, versionId);
	}
	
	@Transactional
    public List<DafMedicationRequest> search(SearchParameterMap paramMap){
        return this.medicationRequestDao.search(paramMap);
    }

	@Override
	public List<DafMedicationRequest> getMedicationRequestHistoryById(String id) {
		return this.medicationRequestDao.getMedicationRequestHistoryById(id);
	}

}
