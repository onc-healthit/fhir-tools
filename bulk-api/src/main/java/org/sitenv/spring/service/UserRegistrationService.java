package org.sitenv.spring.service;

import org.sitenv.spring.model.DafUserRegister;

import javax.servlet.http.HttpServletRequest;

public interface UserRegistrationService {

    public String registerUser(DafUserRegister user);
    
    public String updateUser(DafUserRegister user);

    public DafUserRegister getUserById(Integer id);

    public DafUserRegister getUserByDetails(String userName, String password, HttpServletRequest request);

}
