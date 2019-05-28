package org.sitenv.spring.service;

import javax.servlet.http.HttpServletRequest;

import org.sitenv.spring.model.DafUserRegister;

public interface UserRegistrationService {

    public String registerUser(DafUserRegister user);

    public String updateUser(DafUserRegister user);

    public DafUserRegister getUserById(Integer id);

    public DafUserRegister getUserByDetails(String userName, String password, HttpServletRequest request) throws Exception;

    public String updateUserPassword(String userName, String password, String oldPassword);

	public DafUserRegister getUserByEmail(String email);

	public DafUserRegister updateTempPassword(String tempPassword, String email);
	
	public String changeUserPassword(String userName, String password, String oldPassword);
    
    
    
}
