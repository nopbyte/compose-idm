package de.passau.uni.sec.compose.id.rest.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.GetGroupEvent;
import de.passau.uni.sec.compose.id.core.event.ListGroupsEvent;
import de.passau.uni.sec.compose.id.core.event.ListPendingUserMembershipsEvent;
import de.passau.uni.sec.compose.id.core.event.ListUsersEvent;
import de.passau.uni.sec.compose.id.core.event.ListUsersInGroupEvent;
import de.passau.uni.sec.compose.id.core.service.GroupService;
import de.passau.uni.sec.compose.id.core.service.ListGroupService;
import de.passau.uni.sec.compose.id.core.service.ListUsersGroupService;
import de.passau.uni.sec.compose.id.core.service.ListUsersService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.GroupResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;

@Controller
@RequestMapping("/idm/user")
public class ListAllUsersController {
private static Logger LOG = LoggerFactory.getLogger(ListAllUsersController.class);
	
	
	@Autowired
    private RestAuthentication authenticator;
	
	@Autowired
	private ListUsersService service;
	
	
	@RequestMapping(value="/", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<Object> getMembershipsUser(   @RequestHeader("Authorization") String token,
    		@RequestParam("page") int page,
    		UriComponentsBuilder builder){
				
			Collection<String> cred = new LinkedList<String>();
			cred.add(token);
		    	try{
    				 //This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
    				 ListUsersEvent event = new ListUsersEvent(principals,page);
    				 List<UserResponseMessage> res=(List<UserResponseMessage>) service.listAllEntities(event);
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