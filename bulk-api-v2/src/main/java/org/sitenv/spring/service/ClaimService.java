package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.model.DafClaim;

public interface ClaimService {
	
	   public List<DafClaim> getAllclaims();
	
	   public DafClaim getClaimResourceById(int id);

}
