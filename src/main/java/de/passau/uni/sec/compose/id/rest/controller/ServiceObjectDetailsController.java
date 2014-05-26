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
import de.passau.uni.sec.compose.id.core.event.GetGroupEvent;
import de.passau.uni.sec.compose.id.core.event.GetServiceObjectEvent;
import de.passau.uni.sec.compose.id.core.event.GetUserEvent;
import de.passau.uni.sec.compose.id.core.service.EntityService;
import de.passau.uni.sec.compose.id.core.service.GroupService;
import de.passau.uni.sec.compose.id.core.service.ServiceObjectService;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.GroupResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;

@Controller
@RequestMapping("/idm/serviceobject")
public class ServiceObjectDetailsController {
private static Logger LOG = LoggerFactory.getLogger(ServiceObjectDetailsController.class);
	
	@Autowired
    private ServiceObjectService soService;
	
	@Autowired
    private RestAuthentication authenticator;
    
	@RequestMapping(value="/{soId}", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<Object> getUser( @RequestHeader("Authorization") String token,
    		@PathVariable(value="soId") String uid,UriComponentsBuilder builder){
				
				Collection<String> cred = new LinkedList<String>();
		    	cred.add(token);
    			try{
    				 //This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
    				 ServiceObjectResponseMessage res = (ServiceObjectResponseMessage) soService.getEntity(new GetServiceObjectEvent(uid,principals));
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
	
	@RequestMapping(value="/api_token_data/{soId}", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<Object> getEntityFromComponent( 
    		@PathVariable(value="soId") String uid,UriComponentsBuilder builder){
				
				Collection<String> cred = new LinkedList<String>();
		    	try{
    				 //This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
    				 ServiceObjectResponseMessage res = (ServiceObjectResponseMessage) soService.getEntity(new GetServiceObjectEvent(uid,principals));
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
	
    
}