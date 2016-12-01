package org.sitenv.spring;

import java.io.IOException;

import org.sitenv.spring.model.DafUserRegister;
import org.sitenv.spring.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserRegistrationController {

    @Autowired
    private UserRegistrationService userService;

    /**
     * This method registers the user
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
     * @param id
     * @return This method will return the user. if the user not existed returns null.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DafUserRegister getUserById(@PathVariable Integer id) {

        return userService.getUserById(id);
    }

    /**
     *  This method used to get the user by user name and password
     * @param userName
     * @param password
     * @return This method will return the user. if the user not existed returns null.
     * @throws IOException 
     */
    @RequestMapping(value = "/details", method = RequestMethod.GET)
    @ResponseBody
    public DafUserRegister getUserByDetails(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, @RequestParam("userName") String userName, @RequestParam("password") String password) throws IOException {
        
        DafUserRegister user = userService.getUserByDetails(userName, password, request);
        
        if(user == null){
        	response.sendError(403, "Invalid UserName or Password.");
        }
        
        return user;
    }

}
