package de.passau.uni.sec.compose.id.rest.controller.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.service.UserAuthenticate;
import de.passau.uni.sec.compose.id.rest.controller.UserCommandsController;
import de.passau.uni.sec.compose.id.rest.messages.UserAuthenticatedMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

@Controller
@RequestMapping("/auth/user")
public class AuthenticateUserController
{
private static Logger LOG = LoggerFactory.getLogger(UserCommandsController.class);
	
	@Autowired
    private UserAuthenticate auth;
	
	
    /**
     * Create a new user
     * @param message containing the new user
     * @return userCreatedMessage with the appropiate data or an error otherwise
     */
    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = "application/json")
        @ResponseBody
        public  ResponseEntity<Object> createUser( 
        		@Valid  @RequestBody UserCredentials credentials,UriComponentsBuilder builder,HttpServletRequest req) {
		    	 
    	
    			HttpHeaders headers = new HttpHeaders();
		    	try{
    				 
		    		 UserAuthenticatedMessage res = auth.authenticateUser(credentials);
		    		 LOG.info("user authenticated: "+credentials.getUsername());
    				 return new ResponseEntity<Object>(res, HttpStatus.OK);
		    	 }
		    	 catch(IdManagementException idm){
		    		 //since the creation of the exception generated the log entries for the stacktrace, we don't do it again here
		    		 return new ResponseEntity<Object>(idm.getErrorAsMap(), headers, HttpStatus.valueOf(idm.getHTTPErrorCode()));
		    	 } 
		    	 catch(Exception e)
		    	 {
		    		 String s = IdManagementException.getStackTrace(e);
		    		 LOG.error(s);
		    		 return new ResponseEntity<Object>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);	 
		    	 }
    	 
    	 
        }
    
}
