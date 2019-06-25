package org.sitenv.spring.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.sitenv.spring.model.DafUserRegister;
import org.sitenv.spring.util.AESencryption;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public class UserRegistrationDaoImpl extends AbstractDao implements UserRegistrationDao {
    Session session = null;

    @Override
    public String register(DafUserRegister user) {

        session = getSession();

        Criteria criteria = getSession().createCriteria(DafUserRegister.class)
                .add(Restrictions.eq("user_name", user.getUser_name()).ignoreCase());
        Criteria criteriaEmail = getSession().createCriteria(DafUserRegister.class)
                .add(Restrictions.eq("user_email", user.getUser_email()).ignoreCase());
        @SuppressWarnings("unchecked")
        List<DafUserRegister> existedUser = criteria.list();
        @SuppressWarnings("unchecked")
        List<DafUserRegister> existedEmail = criteriaEmail.list();
        if (existedUser != null && existedUser.size() > 0) {
            return "Username already exists. Please use a different Username.";
        } 
        else if(existedEmail != null && existedEmail.size() > 0){
        	return "Useremail already exists. Please use a different Email Address.";
        }
        else {
        	
        	user.setPass(true);
            int i = (Integer) session.save(user);
            if (i > 0) {
                return "User Registerd Successfully. Please Login to register Clients.";

            } else {
                return "User Registration failed. Please contact Admin.";
            }
        }


    }

    public String updateUser(DafUserRegister user) {
        session = getSession();
       
        	Criteria criteria = getSession().createCriteria(DafUserRegister.class)
                    .add(Restrictions.eq("user_name", user.getUser_name()));
        	DafUserRegister userDetails = (DafUserRegister) criteria.uniqueResult();
        	 try {
        		 userDetails.setUser_full_name(user.getUser_full_name());
        		 userDetails.setUser_email(user.getUser_email());
        		 userDetails.setPass(true);
        		 session.update(userDetails);
            return "User details updated.";
        } catch (Exception e) {
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
	public DafUserRegister getUserByDetails(String userName, String password) throws Exception {

		Criteria criteria = getSession().createCriteria(DafUserRegister.class)
				.add(Restrictions.eq("user_name", userName))
				.add(Restrictions.eq("user_password", AESencryption.encrypt(password)));
		DafUserRegister user = (DafUserRegister) criteria.uniqueResult();
		if(user!=null) {
		//getSession().update(user);
		return user;
		}else {
			return null;
		}
	}
    
    @Override
	public DafUserRegister getUserByEmail(String email) {

		Criteria criteria = getSession().createCriteria(DafUserRegister.class);
		criteria.add(Restrictions.eq("user_email", email));
		DafUserRegister dafUserRegister = (DafUserRegister) criteria.uniqueResult();
		
		return dafUserRegister;
		
	}

    @Override
	public DafUserRegister updateTempPassword(String tempPassword, String email) {

		Criteria criteria = getSession().createCriteria(DafUserRegister.class);
		criteria.add(Restrictions.eq("user_email", email));
		DafUserRegister dafUserRegister = (DafUserRegister) criteria.uniqueResult();
		try {
			dafUserRegister.setTempPassword(tempPassword);
			dafUserRegister.setPass(false);
			dafUserRegister.setUser_password(tempPassword);
			getSession().update(dafUserRegister);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dafUserRegister;

	}
	
	@Override
	public String changeUserPassword(String userName, String password, String oldPassword) {
		Criteria criteria=getSession().createCriteria(DafUserRegister.class);
		criteria.add(Restrictions.eq("user_name",userName));
		//criteria.add(Restrictions.eq("user_password",oldpassword));
		
		DafUserRegister  dafUserRegister=(DafUserRegister) criteria.uniqueResult();
		try {
	
		session=getSession();
		
		if(dafUserRegister!=null && AESencryption.decrypt(dafUserRegister.getUser_password()).equals(oldPassword)){
		
			dafUserRegister.setUser_password(password);
			session.update(dafUserRegister);
			
			return "Password changed successfully.";
		} else {
			return "Please enter valid Old Password."; 
		}
			
		}catch (Exception e) {
			e.printStackTrace();	
			return "Error while updating password.";
		}
	}
    
	public String updateUserPassword(String userName, String password, String oldPassword){
    	Criteria criteria = getSession().createCriteria(DafUserRegister.class)
                .add(Restrictions.eq("user_name", userName));

        DafUserRegister dafUserRegister = (DafUserRegister) criteria.uniqueResult();
        
    	session = getSession();
    	try {
    		if(!oldPassword.equals(dafUserRegister.getTempPassword())) {
            	return "Please enter valid Old Password.";
            }
    		dafUserRegister.setUser_password(password);
    		dafUserRegister.setPass(true);
    		dafUserRegister.setTempPassword(null);
    		session.update(dafUserRegister);
    		return "Password updated successfully. Please Login with updated Password.";
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return "Failed to update User password. Please contact Admin";
    	}
    	
    }

}
