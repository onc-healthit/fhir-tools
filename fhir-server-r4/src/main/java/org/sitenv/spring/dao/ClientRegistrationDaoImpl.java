package org.sitenv.spring.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.exception.ClientNotFoundException;
import org.sitenv.spring.exception.FHIRHapiException;
import org.sitenv.spring.model.DafClientRegister;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Repository
public class ClientRegistrationDaoImpl extends AbstractDao implements ClientRegistrationDao {
    Session session = null;

    @Override
    public DafClientRegister registerClient(DafClientRegister client) throws FHIRHapiException {

        session = getSession();

        Criteria criteria = getSession().createCriteria(DafClientRegister.class)
                .add(Restrictions.eq("name", client.getName()).ignoreCase())
                .add(Restrictions.eq("userId", client.getUserId()));

        @SuppressWarnings("unchecked")
        List<DafClientRegister> existedClient = criteria.list();
        if (existedClient != null && existedClient.size() > 0) {
            throw new FHIRHapiException("Client Name is already existed. Please use a different Client Name.");

        } else {
            session.saveOrUpdate(client);
            return client;
        }
    }

    public DafClientRegister updateClient(DafClientRegister client) throws FHIRHapiException {
        String clientLaunchId= RandomStringUtils.randomAlphanumeric(8);
        client.setLaunchId(clientLaunchId);

        session = getSession();
        session.update(client);
        return client;
    }


    @Override
    public DafClientRegister getClientByDetails(String clientId,
                                                String regtoken) {

        Criteria crit = getSession().createCriteria(DafClientRegister.class)
                .add(Restrictions.eq("client_id", clientId))
                .add(Restrictions.eq("register_token", regtoken));
        DafClientRegister client = (DafClientRegister) crit.uniqueResult();
        return client;
    }

    @Override
    public DafClientRegister getClientByCredentials(String clientId,
                                                    String clientSecret) {
        Criteria crit = getSession().createCriteria(DafClientRegister.class)
                .add(Restrictions.eq("client_id", clientId));

        if(clientSecret != null)  crit.add(Restrictions.eq("client_secret", clientSecret));

        DafClientRegister client = (DafClientRegister) crit.uniqueResult();
        return client;
    }


    @Override
    public DafClientRegister getClient(String clientId) {
        Criteria crit = getSession().createCriteria(DafClientRegister.class)
                .add(Restrictions.eq("client_id", clientId));
        DafClientRegister client = (DafClientRegister) crit.uniqueResult();
        return client;
    }

    @Override
    public List<DafClientRegister> getClientsByUserId(Integer userId) {

        Criteria crit = getSession().createCriteria(DafClientRegister.class)
                .add(Restrictions.eq("userId", userId));

        return crit.list();
    }

    @Override
    public DafClientRegister getDemoClientDetails() {
        Criteria criteria = getSession().createCriteria(DafClientRegister.class);
        criteria.add(Restrictions.sqlRestriction("{alias}.user_id in (select user_id from users where user_name='demouser')"));
        DafClientRegister client = (DafClientRegister) criteria.uniqueResult();
        return client;
    }


	@Override
	public boolean deleteClientByDetails(Map<String, String> clientDetails) {
		Criteria criteria = getSession().createCriteria(DafClientRegister.class);
		criteria.add(Restrictions.eq("client_id", clientDetails.get("clientId")));
		criteria.add(Restrictions.eq("client_secret", clientDetails.get("clientSecret")));
		DafClientRegister deleteClient = (DafClientRegister) criteria.uniqueResult();
		
		if(deleteClient != null) {
			getSession().delete(deleteClient);
			return true;
		}else {
			return false;

		}
	}
}
