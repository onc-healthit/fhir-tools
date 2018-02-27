package org.sitenv.spring.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.sitenv.spring.exception.FHIRHapiException;
import org.sitenv.spring.model.DafClientRegister;
import org.springframework.web.multipart.MultipartFile;

public interface ClientRegistrationService {

    public DafClientRegister registerClient(DafClientRegister client) throws OAuthSystemException, FHIRHapiException;
    
    public DafClientRegister updateClient(DafClientRegister client) throws FHIRHapiException;

    public DafClientRegister getClientByDetails(String clientId, String regtoken);

    public DafClientRegister getClientByCredentials(String clientId, String clientSecret);

    public DafClientRegister getClient(String clientId);
    
    public List<DafClientRegister> getClientsByUserId(Integer userId);
    
    public DafClientRegister registerBackendClient(HashMap<String,String> params, MultipartFile[] files,HttpServletRequest request) throws FHIRHapiException;

}
