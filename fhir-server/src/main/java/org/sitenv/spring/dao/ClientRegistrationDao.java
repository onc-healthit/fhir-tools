package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafClientRegister;

public interface ClientRegistrationDao {

    public DafClientRegister registerClient(DafClientRegister client);

    public DafClientRegister getClientByDetails(String clientId, String regtoken);

    public DafClientRegister getClientByCredentials(String clientId, String clientSecret);

    public DafClientRegister getClient(String clientId);


}
