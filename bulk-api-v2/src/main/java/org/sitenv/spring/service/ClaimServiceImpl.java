package org.sitenv.spring.service;

import java.util.List;

import org.sitenv.spring.dao.ClaimDao;
import org.sitenv.spring.dao.ConditionDao;
import org.sitenv.spring.model.DafClaim;
import org.sitenv.spring.model.DafCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("claimResourceService")
@Transactional
public class ClaimServiceImpl implements ClaimService{
	
	     @Autowired
	    private ClaimDao claimDao;

	    @Override
	    @Transactional
	    public List<DafClaim> getAllclaims() {
	        return this.claimDao.getAllclaims();
	    }

	    @Override
	    @Transactional
	    public DafClaim getClaimResourceById(int id) {
	    	return this.claimDao.getClaimResourceById(id);
	    }
	    
}
