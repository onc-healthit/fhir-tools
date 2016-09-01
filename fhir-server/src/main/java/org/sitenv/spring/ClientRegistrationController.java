package org.sitenv.spring;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.sitenv.spring.model.DafClientRegister;
import org.sitenv.spring.service.ClientRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public DafClientRegister registerClient(@RequestBody DafClientRegister client) throws OAuthSystemException {

        return registerService.registerClient(client);

    }
    
    /**
     * This method used to get the client details with clientId and register token 
     * @param clientId
     * @param regtoken
     * @return This method returns client, if not exist returns null
     * @throws OAuthSystemException
     */

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    @ResponseBody
    public DafClientRegister getClientByDetails(@RequestParam("clientId") String clientId, @RequestParam("regtoken") String regtoken) throws OAuthSystemException {
        
        return registerService.getClientByDetails(clientId, regtoken);

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

}
