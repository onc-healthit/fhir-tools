package org.sitenv.spring.dao;

import org.sitenv.spring.model.Jwks;

public interface JwksDao {
	
public Jwks getById(Integer id);

public void updateById(Integer id, String jwks);

public Jwks saveOrUpdate(Jwks jwks);

}
