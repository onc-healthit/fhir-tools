package org.sitenv.spring.dao;

import java.util.List;

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
        
        Criteria criteria = getSession().createCriteria(DafUserRegister.class)
                .add(Restrictions.eq("user_name", user.getUser_name()).ignoreCase());
        @SuppressWarnings("unchecked")
		List<DafUserRegister> existedUser =  criteria.list();
        if(existedUser != null && existedUser.size()>0){
        	return "Username already exists. Please use a different Username.";
        }else {
        	int i = (Integer) session.save(user);
            if (i > 0) {
                return "User Registerd Successfully. Please Login to register Clients.";

            } else {
                return "User Registration failed. Please contact Admin.";
            }
		}
        
        

    }
    
    public String updateUser(DafUserRegister user){
    	session = getSession();
    	try{
    	session.update(user);
    	return "User details updated.";
    	}catch(Exception e){
    		e.printStackTrace();
    		return "Failed to update User details. Please contact Admin.";
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
