package org.sitenv.spring.dao;

import org.sitenv.spring.exception.FHIRHapiException;
import org.sitenv.spring.model.DafClientRegister;

import java.util.List;

public interface ClientRegistrationDao {

    public DafClientRegister registerClient(DafClientRegister client) throws FHIRHapiException;
    
    public DafClientRegister updateClient(DafClientRegister client) throws FHIRHapiException;

    public DafClientRegister getClientByDetails(String clientId, String regtoken);

    public DafClientRegister getClientByCredentials(String clientId, String clientSecret);

    public DafClientRegister getClient(String clientId);
    
    public List<DafClientRegister> getClientsByUserId(Integer userId);

}
