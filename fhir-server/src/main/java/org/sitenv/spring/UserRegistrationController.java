package org.sitenv.spring;

import org.sitenv.spring.model.DafUserRegister;
import org.sitenv.spring.service.UserRegistrationService;
import org.sitenv.spring.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserRegistrationController {

    @Autowired
    private UserRegistrationService userService;

    /**
     * This method registers the user
     *
     * @param user
     * @return This method will return the registered user.
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public String registerUser(@RequestBody DafUserRegister user) {

        return userService.registerUser(user);
    }

    /**
     * This method registers the user
     *
     * @param user
     * @return This method will return the registered user.
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseBody
    public String updateUser(@RequestBody DafUserRegister user) {

        return userService.updateUser(user);
    }

    /**
     * This method is used to get the user by id
     *
     * @param id
     * @return This method will return the user. if the user not existed returns null.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DafUserRegister getUserById(@PathVariable Integer id) {

        return userService.getUserById(id);
    }

    /**
     * This method used to get the user by user name and password
     *
     * @return This method will return the user. if the user not existed returns null.
     * @throws Exception 
     */
    @RequestMapping(value = "/details", method = RequestMethod.POST)
    @ResponseBody
    public DafUserRegister getUserByDetails(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String,String> credentials) throws Exception {

        DafUserRegister user = null;
        try{
        	user = userService.getUserByDetails(credentials.get("userName"), CommonUtil.base64Decoder(credentials.get("password")), request);

        if (user == null) {
        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please provide valid credentials.");
        }
        }catch(Exception e){
        	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to connect to the Database. Please contact Admin.");
        }

        return user;
        
    }
    /**
	 * This method updates the password for user
	 * 
	 * @return This method will returns the password updated user.
	 */
	@RequestMapping(value = "/update/password", method = RequestMethod.PUT)
	@ResponseBody
	public String updatePasswordByUsername(@RequestBody Map<String, String> credentials) {
		return userService.updateUserPassword(credentials.get("userName"),
				CommonUtil.base64Decoder(credentials.get("password")),credentials.get("oldPassword"));
	}
    
	@RequestMapping(value = "/reset/password", method = RequestMethod.POST)
	@ResponseBody
	public DafUserRegister getUserByEmail(@RequestParam String email, HttpServletRequest request )throws Exception {
		DafUserRegister checkedEmail = userService.getUserByEmail(email);
		if (checkedEmail!=null) {
			String tempPassword = UUID.randomUUID().toString().substring(0, Math.min(UUID.randomUUID().toString().length(), 10));
			String userName=checkedEmail.getUser_name();
			 // Recipient's email ID needs to be mentioned.
		      String to = checkedEmail.getUser_email();
		      
		      ResourceBundle rb = ResourceBundle.getBundle("application");
		      String from=rb.getString("from");
		      final String username =rb.getString("username");
		      final String password =rb.getString("password");
		     
		      String host = rb.getString("host");
		      String port = rb.getString("port");
		      String socketFactory_class = rb.getString("socketFactory_class");
		     // boolean auth = rb.containsKey("true");
		      String smtp_port = rb.getString("smtp_port");

		      Properties props = new Properties();
		      props.put("mail.smtp.host",host);
		      props.put("mail.smtp.socketFactory.port", port);
		      props.put("mail.smtp.socketFactory.class",socketFactory_class);
		      props.put("mail.smtp.auth", "true");
		      props.put("mail.smtp.port", smtp_port);

		      // Get the Session object.
		      Session session = Session.getInstance(props,
		    		  new javax.mail.Authenticator() {
		            	protected PasswordAuthentication getPasswordAuthentication() {
		            		return new PasswordAuthentication(username, password);
		            }
		      });

		      try {
		            // Create a default MimeMessage object.
		            Message message = new MimeMessage(session);

		            // Set From: header field of the header.
		            message.setFrom(new InternetAddress(from));

		            // Set To: header field of the header.
		            message.setRecipients(Message.RecipientType.TO,
		              InternetAddress.parse(to));

		            // Set Subject: header field
		            message.setSubject("Reset your FHIR SITE password");

		            // Send the actual HTML message, as big as you like
		            message.setContent( 
				              "<p><label>We received a request to reset FHIR SITE password for your account, </label>"+userName+".</p><p><label>Your account password is </label>"+tempPassword 
				              +"</p><p><label> Thank You!</label></p>",
				             "text/html");

		            // Send message
		            Transport.send(message);

		            System.out.println("Sent message successfully....");

		      } catch (MessagingException e) {
		    	  e.printStackTrace();
		    	  throw new RuntimeException(e);
		      }
			
	        return userService.updateTempPassword(tempPassword, email);
			
		}
		return null;
	}
	
	@RequestMapping(value = "/change/password", method = RequestMethod.PUT)
	@ResponseBody
	public String changePasswordByUsername(@RequestBody Map<String, String> credentials) {
		return userService.changeUserPassword(credentials.get("userName"),
				CommonUtil.base64Decoder(credentials.get("password")),CommonUtil.base64Decoder(credentials.get("oldPassword")));
		
	}
	
	
    

}
