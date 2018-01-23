package org.sitenv.spring.service;

import java.util.List;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.sitenv.spring.dao.ClientRegistrationDao;
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
    public DafClientRegister registerClient(DafClientRegister client) throws OAuthSystemException, FHIRHapiException {

        String registerToken = CommonUtil.generateRandomString(250);
        client.setRegister_token(registerToken);

        String client_id = CommonUtil.generateRandomString(30);
        client.setClient_id(client_id);

        String client_secret = CommonUtil.base64Encoder(CommonUtil.generateRandomString(50));
        client.setClient_secret(client_secret);

        /*if (client.getClient_id() == null) {
            client.setClient_id(client_id);
        } else {
            client.setClient_id(client.getClient_id());
        }

        if (client.getClient_secret() == null) {
            client.setClient_secret(client_secret);
        } else {
            client.setClient_secret(client.getClient_secret());
        }

        if (client.getRegister_token() == null) {
            client.setRegister_token(registerToken);
        } else {
            client.setRegister_token(client.getRegister_token());
        }*/

        return clientDao.registerClient(client);
    }

    public DafClientRegister updateClient(DafClientRegister client) throws FHIRHapiException  {

        return clientDao.updateClient(client);
    }
    
    @Override
    @Transactional
    public DafClientRegister getClientByDetails(String clientId,
                                                String regtoken) {

        return clientDao.getClientByDetails(clientId, regtoken);
    }

    @Override
    @Transactional
    public DafClientRegister getClientByCredentials(String clientId,
                                                    String clientSecret) {
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

}
