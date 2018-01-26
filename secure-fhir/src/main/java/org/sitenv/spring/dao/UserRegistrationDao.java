package org.sitenv.spring.dao;

import org.sitenv.spring.model.DafUserRegister;

public interface UserRegistrationDao {

    public String register(DafUserRegister user);
    
    public String updateUser(DafUserRegister user);

    public DafUserRegister getUserById(Integer id);

    public DafUserRegister getUserByDetails(String userName, String password);

}
