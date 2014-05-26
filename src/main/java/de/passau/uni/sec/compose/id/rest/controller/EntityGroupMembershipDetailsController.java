package de.passau.uni.sec.compose.id.rest.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import de.passau.uni.sec.compose.id.core.event.GetUserEvent;
import de.passau.uni.sec.compose.id.core.event.ListPendingEntityGroupMembershipsEvent;
import de.passau.uni.sec.compose.id.core.event.ListPendingUserMembershipsEvent;
import de.passau.uni.sec.compose.id.core.service.EntityGroupMembershipService;
import de.passau.uni.sec.compose.id.core.service.EntityService;
import de.passau.uni.sec.compose.id.core.service.GroupService;
import de.passau.uni.sec.compose.id.core.service.UserMembershipService;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.GroupResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;

@Controller
@RequestMapping("/idm/group_memberships")
public class EntityGroupMembershipDetailsController {
private static Logger LOG = LoggerFactory.getLogger(EntityGroupMembershipDetailsController.class);
	
	@Autowired
    private EntityGroupMembershipService memberships;
	
	@Autowired
    private RestAuthentication authenticator;
    
	
	/**
     * Get the unapproved user memberships which could be approved by the user calling the API
     * @param message 
     * @return userCreatedMessage with the appropiate data or an error otherwise
     */
	@RequestMapping(value="/", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<Object> getMembershipsUser( @RequestHeader("Authorization") String token,
    		UriComponentsBuilder builder){
				
				Collection<String> cred = new LinkedList<String>();
		    	cred.add(token);
    			try{
    				 //This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
    				 Object obj= memberships.listAllEntities(new ListPendingEntityGroupMembershipsEvent(principals));
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