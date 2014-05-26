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
import de.passau.uni.sec.compose.id.core.event.CreateApplicationEvent;
import de.passau.uni.sec.compose.id.core.event.CreateServiceSourceCodeEvent;
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteApplicationEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteServiceSourceCodeEvent;
import de.passau.uni.sec.compose.id.core.service.ApplicationService;
import de.passau.uni.sec.compose.id.core.service.ServiceSourceCodeService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.ApplicationCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ApplicationResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.AuthenticatedEmptyMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;



@Controller
@RequestMapping("/idm/servicesourcecode")
public class ServiceSourceCodeCommandsController {

	private static Logger LOG = LoggerFactory.getLogger(ServiceSourceCodeCommandsController.class);
	
	@Autowired
    private ServiceSourceCodeService scService;
	
	@Autowired
	private RestAuthentication authenticator;
	
    /**
     * Create a new user
     * @param message containing the new user
     * @return userCreatedMessage with the appropiate data or an error otherwise
     */
    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = "application/json")
        @ResponseBody
        public  ResponseEntity<Object> createEntity( 
        		@Valid  @RequestBody ServiceSourceCodeCreateMessage message,UriComponentsBuilder builder,HttpServletRequest req) {
		    	 
    	
    			HttpHeaders headers = new HttpHeaders();
		    	Collection<String> cred = new LinkedList<String>();
		    	cred.add(message.getAuthorization());
    			try{
    				 
    				//This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
    				 ServiceSourceCodeResponseMessage res = (ServiceSourceCodeResponseMessage) scService.createEntity(new CreateServiceSourceCodeEvent(message,principals));
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
        public  ResponseEntity<Object> updateEntity( @RequestHeader("Authorization") String token,
        		@RequestHeader("If-Unmodified-Since") long lastKnownUpdate,
        		@PathVariable(value="UserId") String uid, @Valid @RequestBody UserCreateMessage message) {
    	
    	HttpHeaders headers = new HttpHeaders();
    	Collection<String> credentials = new LinkedList<String>();
    	credentials.add(token);
		try{
			
			return new ResponseEntity<Object>(null, headers, HttpStatus.NOT_IMPLEMENTED);
			//This method just authenticates... it doesn't do access control
			/*Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,credentials);
			UserResponseMessage res = (UserResponseMessage) applicationService.updateEntity(new UpdateUserEvent(uid,message,principals,lastKnownUpdate));
			*/
			/*
    		 UserResponseMessage res = userService.createUser(new CreateUserEvent(message,principals));
    		 headers.setLocation(
                 builder.path( req.getServletPath()+"/{id}")
                         .buildAndExpand(res.getId().toString()).toUri());
    		 
    		 return new ResponseEntity<Object>(res, headers, HttpStatus.CREATED);*/
			 /*return new ResponseEntity<Object>(res, headers, HttpStatus.OK);
    	 }
    	 catch(IdManagementException idm){
    		 //since the creation of the exception generated the log entries for the stacktrace, we don't do it again here
    		 return new ResponseEntity<Object>(idm.getErrorAsMap(), headers, HttpStatus.valueOf(idm.getHTTPErrorCode()));*/
    	 } 
    	 catch(Exception e)
    	 {
    		 String s = IdManagementException.getStackTrace(e);
    		 LOG.error(s);
    		 return new ResponseEntity<Object>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);	 
    	 }
 
        }
    
    /**
     * Update a user
     * @param data
     * @return
     */
    @RequestMapping(value="/{Id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
        @ResponseBody
        public  ResponseEntity<Object> DeteEntity(
        		@Valid @RequestBody AuthenticatedEmptyMessage message,
        		@RequestHeader("If-Unmodified-Since") long lastKnownUpdate,
        		@PathVariable(value="Id") String uid) {
    	
	    	HttpHeaders headers = new HttpHeaders();
	    	Collection<String> cred = new LinkedList<String>();
	    	cred.add(message.getAuthorization());
			try{
				
				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
	    		 scService.deleteEntity(new DeleteServiceSourceCodeEvent(uid,principals,lastKnownUpdate));
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