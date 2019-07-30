package org.sitenv.spring;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.sitenv.spring.exception.ClientNotFoundException;
import org.sitenv.spring.exception.FHIRHapiException;
import org.sitenv.spring.model.DafClientRegister;
import org.sitenv.spring.service.ClientRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/client")
public class ClientRegistrationController {

	@Autowired
	private ClientRegistrationService registerService;

	/**
	 * This method registers the client
	 *
	 * @param client
	 * @return This method returns registered client
	 * @throws OAuthSystemException
	 * @throws FHIRHapiException
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public DafClientRegister registerClient(@RequestBody DafClientRegister client) throws FHIRHapiException {

		return registerService.registerClient(client);

	}

	/**
	 * This method updates the client
	 *
	 * @param client
	 * @return This method returns registered client
	 * @throws FHIRHapiException
	 */
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	@ResponseBody
	public DafClientRegister updateClient(@RequestBody DafClientRegister client) throws FHIRHapiException {

		return registerService.updateClient(client);

	}

	/**
	 * This method used to get the client details with clientId and register token
	 *
	 * @param clientId
	 * @param regtoken
	 * @return This method returns client, if not exist returns null
	 * @throws OAuthSystemException
	 * @throws IOException
	 */

	@RequestMapping(value = "/details", method = RequestMethod.GET)
	@ResponseBody
	public DafClientRegister getClientByDetails(
			@RequestParam("clientId") String clientId,
			@RequestParam("regtoken") String regtoken,
			HttpServletResponse response) throws OAuthSystemException, IOException {
		DafClientRegister client = registerService.getClientByDetails(clientId, regtoken);
		if (client == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid Client id or Registration Token.");
		}

		return client;

	}

	/**
	 * This method is used to get the client with client id and client secret
	 *
	 * @param clientId
	 * @param clientSecret
	 * @return This method returns client, if not exist returns null
	 * @throws OAuthSystemException
	 */
	@RequestMapping(value = "/credentials", method = RequestMethod.GET)
	@ResponseBody
	public DafClientRegister getClientByCredentials(
			@RequestParam("clientId") String clientId,
			@RequestParam("clientSecret") String clientSecret) throws OAuthSystemException {

		return registerService.getClientByCredentials(clientId, clientSecret);

	}

	/**
	 * This method is used to get the clients with user id
	 *
	 * @param userId
	 * @return This method returns list of clients, if not exist returns null
	 */
	@RequestMapping(value = "/list/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public List<DafClientRegister> getClientsByUserId(@PathVariable Integer userId) {
		return registerService.getClientsByUserId(userId);
	}

	/**
	 * This method is used to get the client details for Demo user
	 *
	 * @return This method returns client details, if not exist returns null
	 */
	@RequestMapping(value = "/democlient", method = RequestMethod.GET)
	@ResponseBody
	public DafClientRegister getDemoClientDetails(HttpServletResponse response)
			throws OAuthSystemException, IOException {
		DafClientRegister client = registerService.getDemoClientDetails();
		return client;
	}

	/**
	 * delete client
	 * @param clientDetails
	 * @return
	 */

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@ResponseBody
	public boolean getClientByDetails(@RequestBody Map<String, String> clientDetails) {
		return registerService.deleteClientByDetails(clientDetails );
		}
	}

