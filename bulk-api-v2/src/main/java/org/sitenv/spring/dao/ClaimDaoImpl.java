package org.sitenv.spring.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.sitenv.spring.model.DafClaim;
import org.sitenv.spring.model.DafCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository("claimDao")
public class ClaimDaoImpl  extends AbstractDao implements ClaimDao {
	
	  private static final Logger logger = LoggerFactory.getLogger(ConditionDaoImpl.class);

	    @SuppressWarnings("unchecked")
	    @Override
	    public List<DafClaim> getAllclaims() {
	        Criteria criteria = getSession().createCriteria(DafClaim.class);
	        return (List<DafClaim>) criteria.list();
	    }
	    
	    @Override
	    public DafClaim getClaimResourceById(int id) {
	        DafClaim dafClaim = (DafClaim) getSession().get(DafClaim.class, id);
	        return dafClaim;
	    }

}
