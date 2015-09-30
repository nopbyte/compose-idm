package de.passau.uni.sec.compose.id.rest.controller;


import iotp.model.storage.model.EncodedSenderOTPData;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import de.passau.uni.sec.compose.id.core.domain.IDMEncodedSenderOTPData;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.CreateGroupEvent;
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteGroupEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteServiceCompositionEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateUserEvent;
import de.passau.uni.sec.compose.id.core.service.GenerateOTPData;
import de.passau.uni.sec.compose.id.core.service.GroupService;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.GroupResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserUpdateMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;



@Controller
@RequestMapping("/idm/key")
public class OtpCommandsController {

	private static Logger LOG = LoggerFactory.getLogger(OtpCommandsController.class);
	
	@Autowired
	GenerateOTPData otpService;
	
	@Autowired
	private RestAuthentication authenticator;
	
   
    @RequestMapping(value = "/", method = RequestMethod.GET, consumes = "application/json")
        @ResponseBody
        public  ResponseEntity<Object> createUser( @RequestHeader("Authorization") String token,
        		UriComponentsBuilder builder,HttpServletRequest req) {
		    	 
    	
    			HttpHeaders headers = new HttpHeaders();
		    	Collection<String> cred = new LinkedList<String>();
		    	cred.add(token);
    			try{
    				 
    				//This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
    				 EncodedSenderOTPData res = otpService.generateOTPdataForUser(principals);
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
    
    
    @RequestMapping(value="{key}/", method = RequestMethod.DELETE, consumes = "application/json")
    @ResponseBody
    public  ResponseEntity<Object> createUser( @RequestHeader("Authorization") String token,
    		@PathVariable(value="key") String key) {
	    	 
	
			HttpHeaders headers = new HttpHeaders();
	    	Collection<String> cred = new LinkedList<String>();
	    	cred.add(token);
			try{
				 
				//This method just authenticates... it doesn't do access control
				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
				 otpService.deleteOTPForUser(principals, key);
				 return new ResponseEntity<Object>( headers, HttpStatus.OK);
				 
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