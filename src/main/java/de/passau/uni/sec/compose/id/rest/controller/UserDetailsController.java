package de.passau.uni.sec.compose.id.rest.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.GetUserEvent;
import de.passau.uni.sec.compose.id.core.service.EntityService;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;

@Controller
@RequestMapping("/idm/user")
public class UserDetailsController {
private static Logger LOG = LoggerFactory.getLogger(UserDetailsController.class);
	
	@Autowired
    private UserService userService;
	
	@Autowired
    private RestAuthentication authenticator;
    /**
     * Create a new user
     * @param message containing the new user
     * @return userCreatedMessage with the appropiate data or an error otherwise
     */
	
	
	@RequestMapping(value="/{UserId}", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<Object> getUser( @RequestHeader("Authorization") String token,
    		@PathVariable(value="UserId") String uid,UriComponentsBuilder builder){
				
				Collection<String> cred = new LinkedList<String>();
		    	cred.add(token);
    			try{
    				 //This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
		    		 UserResponseMessage res = (UserResponseMessage) userService.getEntity(new GetUserEvent(uid,principals));
		    		 return new ResponseEntity<Object>(res, HttpStatus.OK);
		    	 }
		    	 catch(IdManagementException idm){
		    		 //since the creation of the exception generated the log entries for the stacktrace, we don't do it again here
		    		 return new ResponseEntity<Object>(idm.getErrorAsMap(), HttpStatus.valueOf(idm.getHTTPErrorCode()));
		    	 } 
		    	 catch(Exception e)
		    	 {
		    		 String s = IdManagementException.getStackTrace(e);
		    		 LOG.error(s);
		    		 return new ResponseEntity<Object>(null, HttpStatus.INTERNAL_SERVER_ERROR);	 
		    	 }
    	 
    	 
        }
    
    
    /**
     * Getting data for a particular user
     * @param name
     * @return
     */
   /* @RequestMapping(value="/{UserId}", method=RequestMethod.GET)
    public @ResponseBody Greeting greeting( @RequestHeader("Authorization") String token,
    		@PathVariable(value="UserId") String uid) {
    	 
    	return new Greeting(1,
                            "this is the uid for the user you want to see:"+uid+ "This is the token you are providing!:"+token);
    }*/
    
}