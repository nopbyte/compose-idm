package de.passau.uni.sec.compose.id.rest.controller;

import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteUserEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateUserEvent;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;



@Controller
@RequestMapping("/idm/user")
public class UserCommandsController {

	private static Logger LOG = LoggerFactory.getLogger(UserCommandsController.class);
	
	@Autowired
    private UserService userService;
	
	@Autowired
	private RestAuthentication authenticator;
	
    /**
     * Create a new user
     * @param message containing the new user
     * @return userCreatedMessage with the appropiate data or an error otherwise
     */
    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = "application/json")
        @ResponseBody
        public  ResponseEntity<Object> createUser( @RequestHeader("Authorization") String token,
        		@Valid  @RequestBody UserCreateMessage message,UriComponentsBuilder builder,HttpServletRequest req) {
		    	 
    	
    			HttpHeaders headers = new HttpHeaders();
		    	Collection<String> cred = new LinkedList<String>();
		    	cred.add(token);
    			try{
    				 
    				//This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
		    		 UserResponseMessage res = (UserResponseMessage) userService.createEntity(new CreateUserEvent(message,principals));
		    		 headers.setLocation(
		                 builder.path( req.getServletPath()+"/{id}")
		                         .buildAndExpand(res.getId()).toUri());
		    		 
		    		 return new ResponseEntity<Object>(res, headers, HttpStatus.CREATED);
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
    
    /**
     * 
     * @param token authentication mechanism
     * @param timestamp modification of the latest version that the client calling the API has
     * @param uid identifier 
     * @param message message to update the user
     * @return data for the user
     */
    @RequestMapping(value = "/{UserId}", method = RequestMethod.PUT, consumes = "application/json")
    
        @ResponseBody
        public  ResponseEntity<Object> UpdateUser( @RequestHeader("Authorization") String token,
        		@RequestHeader("If-Unmodified-Since") long lastKnownUpdate,
        		@PathVariable(value="UserId") String uid, @Valid @RequestBody UserCreateMessage message) {
    	
    	HttpHeaders headers = new HttpHeaders();
    	Collection<String> credentials = new LinkedList<String>();
    	credentials.add(token);
		try{
			 
			//This method just authenticates... it doesn't do access control
			Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,credentials);
			UserResponseMessage res = (UserResponseMessage) userService.updateEntity(new UpdateUserEvent(uid,message,principals,lastKnownUpdate));
			/*
    		 UserResponseMessage res = userService.createUser(new CreateUserEvent(message,principals));
    		 headers.setLocation(
                 builder.path( req.getServletPath()+"/{id}")
                         .buildAndExpand(res.getId().toString()).toUri());
    		 
    		 return new ResponseEntity<Object>(res, headers, HttpStatus.CREATED);*/
			 return new ResponseEntity<Object>(res, headers, HttpStatus.OK);
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
    
    @RequestMapping(value="/{UserId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
        @ResponseBody
        public  ResponseEntity<Object> DeteUser(
        		@RequestHeader("If-Unmodified-Since") long lastKnownUpdate,
        		@PathVariable(value="UserId") String uid) {
    	
	    	HttpHeaders headers = new HttpHeaders();
	    	Collection<String> cred = new LinkedList<String>();

			try{
				
				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
	    		 userService.deleteEntity(new DeleteUserEvent(uid,principals,lastKnownUpdate));
	    		 return new ResponseEntity<Object>(null, headers, HttpStatus.OK);
	    		 
	    	 }
	    	 catch(IdManagementException idm){
	    		 //since the creation of the exception generated the log entries for the stacktrace, we don't do it again here
	    		 return new ResponseEntity<Object>(idm.getErrorAsMap(), headers, HttpStatus.valueOf(idm.getHTTPErrorCode()));
	    	 }catch(Exception e)
	    	 {
	    		 String s = IdManagementException.getStackTrace(e);
	    		 LOG.error(s);
	    		 return new ResponseEntity<Object>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);	 
	    	 }
        }
    
    
    
}