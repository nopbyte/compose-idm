package de.passau.uni.sec.compose.id.rest.controller;

import java.util.Collection;
import java.util.LinkedList;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetGroupEvent;
import de.passau.uni.sec.compose.id.core.event.ListApplicationsOwnedByUserEvent;
import de.passau.uni.sec.compose.id.core.event.ListEntitiesInGroupEvent;
import de.passau.uni.sec.compose.id.core.event.ListPendingUserMembershipsEvent;
import de.passau.uni.sec.compose.id.core.event.ListServiceCompositionsOwnedByUserEvent;
import de.passau.uni.sec.compose.id.core.event.ListServiceInstancesOwnedByUserEvent;
import de.passau.uni.sec.compose.id.core.event.ListServiceObjectsOwnedByUserEvent;
import de.passau.uni.sec.compose.id.core.event.ListServiceSourceCodesOwnedByUserEvent;
import de.passau.uni.sec.compose.id.core.event.ListUsersInGroupEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Membership;
import de.passau.uni.sec.compose.id.core.service.GroupService;
import de.passau.uni.sec.compose.id.core.service.ListEntitiesGroupService;
import de.passau.uni.sec.compose.id.core.service.ListEntitiesOwnedByUserService;
import de.passau.uni.sec.compose.id.core.service.ListUsersGroupService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.GroupResponseMessage;

@Controller
@RequestMapping("/idm")
public class ListEntitiesOwnedByUserDetailsController {
private static Logger LOG = LoggerFactory.getLogger(ListEntitiesOwnedByUserDetailsController.class);
	
	
	@Autowired
    private RestAuthentication authenticator;
	
	@Autowired
	private ListEntitiesOwnedByUserService listEntitiesService;
    
	
	
	@RequestMapping(value="/user/{userId}/{entity_type}/", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<Object> getMembershipsUser(   @RequestHeader("Authorization") String token,
    		@PathVariable(value="userId") String uid,@PathVariable(value="entity_type") String entity_type, UriComponentsBuilder builder){
				
			Collection<String> cred = new LinkedList<String>();
			cred.add(token);
		    	try{
    				 //This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
    				 Event e = null;
    				 if(entity_type.equals(EntityGroupMembership.APPLICATION))
    				 	 e = new ListApplicationsOwnedByUserEvent(principals,uid);
    				 else if(entity_type.equals(EntityGroupMembership.SERVICECOMPOSITION))
    					 e = new ListServiceCompositionsOwnedByUserEvent(principals,uid);
    				 else if(entity_type.equals(EntityGroupMembership.SERVICEINSTANCE))
    					 e = new ListServiceInstancesOwnedByUserEvent(principals,uid);
    				 else if(entity_type.equals(EntityGroupMembership.SERVICEOBJECT))
    					 e = new ListServiceObjectsOwnedByUserEvent(principals,uid);
    				 else if(entity_type.equals(EntityGroupMembership.SERVICESOURCECODE))
    					 e = new ListServiceSourceCodesOwnedByUserEvent(principals, uid);
    				 Object obj= listEntitiesService.listAllEntities(e);
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