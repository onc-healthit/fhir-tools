package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafAuthtemp;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public class AuthTempDaoImpl extends AbstractDao implements AuthTempDao {
    Session session = null;

    @Override
    public DafAuthtemp saveOrUpdate(DafAuthtemp auth) {

        DafAuthtemp auths = getAuthById(auth.getClient_id());
        if (auths != null) {

            auths.setAccess_token(auth.getAccess_token());
            auths.setAud(auth.getAud());
            auths.setAuthCode(auth.getAuthCode());
            auths.setClient_id(auth.getClient_id());
            auths.setClient_secret(auth.getClient_secret());
            auths.setExpiry(auth.getExpiry());
            auths.setRedirect_uri(auth.getRedirect_uri());
            auths.setScope(auth.getScope());
            auths.setTransaction_id(auth.getTransaction_id());
            auths.setState(auth.getState());
            auths.setRefresh_token(auth.getRefresh_token());
            auths.setLaunchPatientId(auth.getLaunchPatientId());
            session = getSession();
            session.update(auths);

        } else {

            session = getSession();
            session.saveOrUpdate(auth);

        }


        return auth;
    }

    @Override
    public DafAuthtemp getAuthByClientId(String clientId, String clientSecret) {
        Criteria crit = getSession().createCriteria(DafAuthtemp.class)
                .add(Restrictions.eq("client_id", clientId))
                .add(Restrictions.eq("client_secret", clientSecret));
        DafAuthtemp auth = (DafAuthtemp) crit.uniqueResult();
        return auth;
    }

    @Override
    public List<DafAuthtemp> getList() {

        return getSession().createCriteria(DafAuthtemp.class)
                .list();
        //return null;
    }

    @Override
    public DafAuthtemp validateAccessToken(String accessToken) {
        return (DafAuthtemp) getSession().createCriteria(DafAuthtemp.class)
                .add(Restrictions.eq("access_token", accessToken)).uniqueResult();
    }

    @Override
    public DafAuthtemp getAuthenticationById(String transactionId) {
        return (DafAuthtemp) getSession().createCriteria(DafAuthtemp.class)
                .add(Restrictions.eq("transaction_id", transactionId)).uniqueResult();
    }

    @Override
    public DafAuthtemp getAuthById(String clientId) {
        Criteria crit = getSession().createCriteria(DafAuthtemp.class)
                .add(Restrictions.eq("client_id", clientId));
        DafAuthtemp auth = (DafAuthtemp) crit.uniqueResult();
        return auth;
    }

}
