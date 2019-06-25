package org.sitenv.spring.service;

import org.sitenv.spring.dao.AuthTempDao;
import org.sitenv.spring.model.DafAuthtemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("authTempService")
@Transactional
public class AuthTempServiceImpl implements AuthTempService {
	
	@Autowired
	private AuthTempDao authTempDao;

	@Override
	@Transactional
	public DafAuthtemp saveOrUpdate(DafAuthtemp auth) {
		return this.authTempDao.saveOrUpdate(auth);
	}

	@Override
	@Transactional
	public DafAuthtemp getAuthByClientId(String clientId, String clientSecret) {
		return this.authTempDao.getAuthByClientId(clientId, clientSecret);
	}

	@Override
	@Transactional
	public List<DafAuthtemp> getList() {
		return this.authTempDao.getList();
	}

	@Override
	@Transactional
	public DafAuthtemp validateAccessToken(String accessToken) {
		return this.authTempDao.validateAccessToken(accessToken);
	}

	@Override
	@Transactional
	public DafAuthtemp getAuthenticationById(String transactionId) {
		return this.authTempDao.getAuthenticationById(transactionId);
	}

	@Override
	@Transactional
	public DafAuthtemp getAuthById(String clientId) {
		return this.authTempDao.getAuthById(clientId);
	}

	@Override
	@Transactional
	public DafAuthtemp validateIdToken(String idToken) {
		return this.authTempDao.validateIdToken(idToken);
	}

}
