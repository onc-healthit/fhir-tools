package org.sitenv.spring.service;

import java.util.List;
import java.util.Map;

import org.sitenv.spring.exception.ClientNotFoundException;
import org.sitenv.spring.exception.FHIRHapiException;
import org.sitenv.spring.model.DafClientRegister;

public interface ClientRegistrationService {

    public DafClientRegister registerClient(DafClientRegister client) throws  FHIRHapiException;

    public DafClientRegister updateClient(DafClientRegister client) throws FHIRHapiException;

    public DafClientRegister getClientByDetails(String clientId, String regtoken);

    public DafClientRegister getClientByCredentials(String clientId, String clientSecret);

    public DafClientRegister getClient(String clientId);

    public List<DafClientRegister> getClientsByUserId(Integer userId);

    public DafClientRegister getDemoClientDetails();
    
	public boolean deleteClientByDetails(Map<String, String> clientDetails) ;

}
