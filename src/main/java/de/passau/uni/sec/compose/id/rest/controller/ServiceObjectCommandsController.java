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
import de.passau.uni.sec.compose.id.core.event.CreateServiceObjectEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteServiceObjectEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateServiceObjectTokenEvent;
import de.passau.uni.sec.compose.id.core.service.ServiceObjectService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.AuthenticatedEmptyMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectTokenUpdateMessage;



@Controller
@RequestMapping("/idm/serviceobject")
public class ServiceObjectCommandsController {

	private static Logger LOG = LoggerFactory.getLogger(ServiceObjectCommandsController.class);
	
	@Autowired
    private ServiceObjectService soService;
	
	@Autowired
	private RestAuthentication authenticator;
	
    /**
     * Create a new user
     * @param message containing the new user
     * @return userCreatedMessage with the appropiate data or an error otherwise
     */
    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = "application/json")
        @ResponseBody
        public  ResponseEntity<Object> createUser( 
        		@Valid  @RequestBody ServiceObjectCreateMessage message,UriComponentsBuilder builder,HttpServletRequest req) {
		    	 
    	
    			HttpHeaders headers = new HttpHeaders();
		    	Collection<String> cred = new LinkedList<String>();
		    	cred.add(message.getAuthorization());
    			try{
    				 
    				//This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
    				 ServiceObjectResponseMessage res = (ServiceObjectResponseMessage) soService.createEntity(new CreateServiceObjectEvent(message,principals));
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
				 soService.deleteEntity(new DeleteServiceObjectEvent(uid,principals,lastKnownUpdate));
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
    
    @RequestMapping(value="/{Id}/api_token", method = RequestMethod.PUT)
        @ResponseBody
        public  ResponseEntity<Object> UpdateTokenEntity(
        		@Valid @RequestBody ServiceObjectTokenUpdateMessage message,
        		@RequestHeader("If-Unmodified-Since") long lastKnownUpdate,
        		@PathVariable(value="Id") String uid) {
    	
	    	HttpHeaders headers = new HttpHeaders();
	    	Collection<String> cred = new LinkedList<String>();
	    	cred.add(message.getAuthorization());
			try{
				
				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
				 EntityResponseMessage res = soService.updateEntity(new UpdateServiceObjectTokenEvent(uid,message,principals,lastKnownUpdate));
	    		 return new ResponseEntity<Object>(res, headers, HttpStatus.OK);
	    		 
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