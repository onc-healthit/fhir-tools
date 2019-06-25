package org.sitenv.spring.service;

import org.sitenv.spring.dao.JwksDao;
import org.sitenv.spring.model.Jwks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service("JwksService")
@Transactional
public class JwksServiceImpl implements JwksService {
	
	@Autowired
	private JwksDao jwksDao;

	@Override
	@Transactional
	public Jwks getById(Integer id) {
		return jwksDao.getById(id);
	}

	@Override
	@Transactional
	public void updateById(Integer id, String jwks) {
		jwksDao.updateById(id, jwks);
	}

	@Override
	@Transactional
	public Jwks saveOrUpdate(Jwks jwks) {
		return jwksDao.saveOrUpdate(jwks);
	}

}
