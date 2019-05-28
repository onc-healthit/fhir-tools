package org.sitenv.spring.service;

import org.sitenv.spring.model.Jwks;

public interface JwksService {
	
	public Jwks getById(Integer id);

	public void updateById(Integer id, String jwks);

	public Jwks saveOrUpdate(Jwks jwks);
	
}
