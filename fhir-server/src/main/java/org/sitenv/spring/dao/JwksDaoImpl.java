package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.Jwks;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository("JwksDao")
public class JwksDaoImpl extends AbstractDao implements JwksDao {
	  Session session = null;

	@Override
	public Jwks getById(Integer id) {
		Jwks jwks=(Jwks) getSession().get(Jwks.class, id);
		return jwks;
	}

	@Override
	public void updateById(Integer id, String jwkString) {
		Criteria criteria = getSession().createCriteria(Jwks.class)
				.add(Restrictions.eq("id", id));
				Jwks jwks = (Jwks) criteria.uniqueResult();
			jwks.setJwk(jwkString);	
			getSession().update(jwks);
	}

	@Override
	public Jwks saveOrUpdate(Jwks jwks) {
		Jwks jwks1 = getById(jwks.getId());
		 if (jwks1 != null) {
			 jwks1.setJwk(jwks.getJwk());
			 jwks1.setLastUpdatedDatetime(jwks.getLastUpdatedDatetime());
		 }
		
		return null;
	}
}
