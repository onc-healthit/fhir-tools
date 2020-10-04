package org.sitenv.spring.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.sitenv.spring.dao.ClaimResourceDao;
import org.sitenv.spring.model.ClaimResource;
import org.sitenv.spring.model.ExtractionTask;

import ca.uhn.fhir.model.dstu2.resource.Claim;

@Service
@Transactional
public class ClaimResourceServiceImpl implements ClaimResourceService {

	@Autowired
	private ClaimResourceDao claimDao;
	
	public ClaimResource saveOrUpdate(ClaimResource claim) {
		
		return claimDao.saveOrUpdate(claim);
	}

	public List<ClaimResource> getAllClaimResources() {
		
		return claimDao.getAllClaimResources();
	}

	public ClaimResource getClaimResourceById(Integer id) {
		
		return claimDao.getClaimResourceById(id);
	}

	public List<ClaimResource> findDuplicatesBeforePersist(Claim claim, ExtractionTask et) {
		
		return claimDao.findDuplicatesBeforePersist(claim, et);
	}

	public List<ClaimResource> getClaimResourcesByExtractionIdAndInternalPatientId(Integer etId, String patientId) {
		
		return claimDao.getClaimResourcesByExtractionIdAndInternalPatientId(etId, patientId);
	}

	@Override
	public Double getUtilizationAmountByPatientId(String internalPatientId) {
		
		return claimDao.getUtilizationAmountByPatientId(internalPatientId);
	}

}
