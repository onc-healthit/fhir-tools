package org.sitenv.spring;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.sitenv.spring.exception.FHIRHapiException;
import org.sitenv.spring.model.DafClientRegister;
import org.sitenv.spring.service.ClientRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/client")
public class ClientRegistrationController {

    @Autowired
    private ClientRegistrationService registerService;
    
    /**
     * This method registers the client 
     * @param client
     * @return This method returns registered client 
     * @throws OAuthSystemException
     * @throws FHIRHapiException 
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public DafClientRegister registerClient(@RequestBody DafClientRegister client) throws OAuthSystemException, FHIRHapiException {

        return registerService.registerClient(client);

    }
    
    /**
     * This method updates the client 
     * @param client
     * @return This method returns registered client 
     * @throws FHIRHapiException 
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseBody
    public DafClientRegister updateClient(@RequestBody DafClientRegister client) throws FHIRHapiException  {

        return registerService.updateClient(client);

    }
    
    /**
     * This method used to get the client details with clientId and register token 
     * @param clientId
     * @param regtoken
     * @return This method returns client, if not exist returns null
     * @throws OAuthSystemException
     * @throws IOException 
     */

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    @ResponseBody
    public DafClientRegister getClientByDetails(@RequestParam("clientId") String clientId, @RequestParam("regtoken") String regtoken, HttpServletResponse response) throws OAuthSystemException, IOException {
    	DafClientRegister client = registerService.getClientByDetails(clientId, regtoken);
    	if(client == null){
    		response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid Client id or Registration Token.");
    	}
        
    	return client;

    }
    
    /**
     * This method is used to get the client with client id and client secret
     * @param clientId
     * @param clientSecret
     * @return This method returns client, if not exist returns null
     * @throws OAuthSystemException
     */
    @RequestMapping(value = "/credentials", method = RequestMethod.GET)
    @ResponseBody
    public DafClientRegister getClientByCredentials(@RequestParam("clientId") String clientId, @RequestParam("clientSecret") String clientSecret) throws OAuthSystemException {
        
        return registerService.getClientByCredentials(clientId, clientSecret);

    }
    
    /**
     * This method is used to get the clients with user id 
     * @param userId
     * @return This method returns list of clients, if not exist returns null
     */
    @RequestMapping(value = "/list/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public List<DafClientRegister> getClientsByUserId(@PathVariable Integer userId){
    	return registerService.getClientsByUserId(userId);
    }
    
    /**
     * This method registers the backend client 
     * @param client
     * @return This method returns registered backend client 
     * @throws OAuthSystemException
     * @throws FHIRHapiException 
     */
    @RequestMapping(value = "/backendclient/", method = RequestMethod.POST)
    @ResponseBody
    public DafClientRegister registerBackendClient(@RequestParam HashMap<String,String> params,@RequestPart("file") MultipartFile[] files,HttpServletRequest request) throws OAuthSystemException, FHIRHapiException {
    	
    	
        return registerService.registerBackendClient(params,files,request);

    }
    
    @RequestMapping(value = "/model", method = RequestMethod.GET)
    @ResponseBody
    public DafClientRegister getModel() {
    	return new DafClientRegister();
    }

}
