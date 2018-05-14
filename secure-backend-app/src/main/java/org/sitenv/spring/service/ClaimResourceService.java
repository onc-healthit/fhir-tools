package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.ClaimResource;
import org.sitenv.spring.model.ExtractionTask;

import ca.uhn.fhir.model.dstu2.resource.Claim;

	public interface ClaimResourceService {
	
	public ClaimResource saveOrUpdate(ClaimResource claim);
	
	public List<ClaimResource> getAllClaimResources();
	
	public ClaimResource getClaimResourceById(Integer id);
	
	public List<ClaimResource> findDuplicatesBeforePersist(Claim claim, ExtractionTask et);
	
	public List<ClaimResource> getClaimResourcesByExtractionIdAndInternalPatientId(Integer etId, String patientId);
	
	public Double getUtilizationAmountByPatientId(String internalPatientId);

}
