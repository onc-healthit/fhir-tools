package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafAuthtemp;

import java.util.List;

public interface AuthTempDao {

    public DafAuthtemp saveOrUpdate(DafAuthtemp auth);

    public DafAuthtemp getAuthByClientId(String clientId, String clientSecret);

    public List<DafAuthtemp> getList();

    public DafAuthtemp validateAccessToken(String accessToken);

    public DafAuthtemp getAuthenticationById(String transactionId);

    public DafAuthtemp getAuthById(String clientId);

}
