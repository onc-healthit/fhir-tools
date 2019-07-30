package org.sitenv.spring.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.sitenv.spring.dao.ClientRegistrationDao;
import org.sitenv.spring.exception.ClientNotFoundException;
import org.sitenv.spring.exception.FHIRHapiException;
import org.sitenv.spring.model.DafClientRegister;
import org.sitenv.spring.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("clientRegistrationService")
@Transactional
public class ClientRegistrationServiceImpl implements ClientRegistrationService {

    @Autowired(required = true)
    private ClientRegistrationDao clientDao;

    @Override
    public DafClientRegister registerClient(DafClientRegister client) throws  FHIRHapiException {

        String registerToken = CommonUtil.generateRandomString(250);
        client.setRegister_token(registerToken);

        String client_id = CommonUtil.generateRandomString(30);
        client.setClient_id(client_id);

        String client_secret = CommonUtil.base64Encoder(CommonUtil.generateRandomString(50));
        client.setClient_secret(client_secret);
        
       // String clientLaunchId = CommonUtil.base64Encoder(CommonUtil.generateRandomString(8));
        String clientLaunchId= RandomStringUtils.randomAlphanumeric(8);
        client.setLaunchId(clientLaunchId);


        return clientDao.registerClient(client);
    }

    public DafClientRegister updateClient(DafClientRegister client) throws FHIRHapiException {

        return clientDao.updateClient(client);
    }

    @Override
    @Transactional
    public DafClientRegister getClientByDetails(String clientId, String regtoken) {
        return clientDao.getClientByDetails(clientId, regtoken);
    }

    @Override
    @Transactional
    public DafClientRegister getClientByCredentials(String clientId, String clientSecret) {
        return clientDao.getClientByCredentials(clientId, clientSecret);
    }

    @Override
    public DafClientRegister getClient(String clientId) {
        return clientDao.getClient(clientId);
    }

    @Override
    public List<DafClientRegister> getClientsByUserId(Integer userId) {
        return clientDao.getClientsByUserId(userId);
    }

    @Override
    public DafClientRegister getDemoClientDetails() {
        return clientDao.getDemoClientDetails();
    }

	
	@Override
	public boolean deleteClientByDetails(Map<String, String> clientDetails ) {
		return clientDao.deleteClientByDetails(clientDetails);
	}
}
