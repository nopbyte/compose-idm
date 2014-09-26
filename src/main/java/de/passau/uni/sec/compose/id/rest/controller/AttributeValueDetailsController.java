package de.passau.uni.sec.compose.id.rest.controller;

import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.ListPendingAttributeValueEvent;
import de.passau.uni.sec.compose.id.core.service.AttributeValueService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

@Controller
@RequestMapping("/idm/attribute_values")
public class AttributeValueDetailsController {
private static Logger LOG = LoggerFactory.getLogger(AttributeValueDetailsController.class);
	
	@Autowired
    private AttributeValueService attributes;
	
	@Autowired
    private RestAuthentication authenticator;
    
	
	/**
     * Get the unapproved user memberships which could be approved by the user calling the API
     * @param message 
     * @return userCreatedMessage with the appropiate data or an error otherwise
     */
	@RequestMapping(value="/approve/", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<Object> getMembershipsUser( @RequestHeader("Authorization") String token,
    		UriComponentsBuilder builder){
				
				Collection<String> cred = new LinkedList<String>();
		    	cred.add(token);
    			try{
    				 //This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
    				 Object obj= attributes.listAllEntities(new ListPendingAttributeValueEvent(principals));
    				 return new ResponseEntity<Object>(obj, HttpStatus.OK);
    				 
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