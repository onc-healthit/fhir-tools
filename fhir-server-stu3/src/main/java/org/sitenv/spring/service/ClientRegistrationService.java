package org.sitenv.spring.service;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.sitenv.spring.exception.FHIRHapiException;
import org.sitenv.spring.model.DafClientRegister;

import java.util.List;

public interface ClientRegistrationService {

    public DafClientRegister registerClient(DafClientRegister client) throws OAuthSystemException, FHIRHapiException;

    public DafClientRegister updateClient(DafClientRegister client) throws FHIRHapiException;

    public DafClientRegister getClientByDetails(String clientId, String regtoken);

    public DafClientRegister getClientByCredentials(String clientId, String clientSecret);

    public DafClientRegister getClient(String clientId);

    public List<DafClientRegister> getClientsByUserId(Integer userId);

    public DafClientRegister getDemoClientDetails();

	public String deleteClientByDetails(String clientId, String clientSecret);

}
