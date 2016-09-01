package org.sitenv.spring.service;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.sitenv.spring.model.DafClientRegister;

public interface ClientRegistrationService {

    public DafClientRegister registerClient(DafClientRegister client) throws OAuthSystemException;

    public DafClientRegister getClientByDetails(String clientId, String regtoken);

    public DafClientRegister getClientByCredentials(String clientId, String clientSecret);

    public DafClientRegister getClient(String clientId);

}
