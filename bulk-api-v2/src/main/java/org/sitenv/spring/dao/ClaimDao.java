package org.sitenv.spring.dao;

import java.util.List;

import org.sitenv.spring.model.DafClaim;
import org.sitenv.spring.model.DafCondition;

public interface ClaimDao {
	
	public List<DafClaim> getAllclaims();
	
   public DafClaim getClaimResourceById(int id);

}
