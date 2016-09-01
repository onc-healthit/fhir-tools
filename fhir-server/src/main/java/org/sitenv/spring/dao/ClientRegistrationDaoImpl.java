package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafClientRegister;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Repository
public class ClientRegistrationDaoImpl extends AbstractDao implements ClientRegistrationDao {
    Session session = null;

    @Override
    public DafClientRegister registerClient(DafClientRegister client) {

        session = getSession();
        session.saveOrUpdate(client);
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
                .add(Restrictions.eq("client_id", clientId))
                .add(Restrictions.eq("client_secret", clientSecret));
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

}
