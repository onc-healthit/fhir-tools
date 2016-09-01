package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafUserRegister;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class UserRegistrationDaoImpl extends AbstractDao implements UserRegistrationDao {
    Session session = null;

    @Override
    public String register(DafUserRegister user) {

        session = getSession();
        int i = (Integer) session.save(user);
        if (i > 0) {
            return "Success";

        } else {
            return "Failed";
        }

    }

    @Override
    public DafUserRegister getUserById(Integer id) {
        DafUserRegister dafUserRegister = (DafUserRegister) getSession().get(DafUserRegister.class, id);
        return dafUserRegister;
    }

    @Override
    public DafUserRegister getUserByDetails(String userName, String password) {

        Criteria criteria = getSession().createCriteria(DafUserRegister.class)
                .add(Restrictions.eq("user_name", userName))
                .add(Restrictions.eq("user_password", password));

        DafUserRegister user = (DafUserRegister) criteria.uniqueResult();

        return user;
    }

}
