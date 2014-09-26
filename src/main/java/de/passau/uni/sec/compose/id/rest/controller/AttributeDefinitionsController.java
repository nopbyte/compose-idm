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
import de.passau.uni.sec.compose.id.core.event.ApproveAttributeValueEvent;
import de.passau.uni.sec.compose.id.core.event.CreateAttributeDefinitionEvent;
import de.passau.uni.sec.compose.id.core.event.CreateAttributeValueEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteAttributeDefinitionEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteAttributeValueEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateAttributeValueEvent;
import de.passau.uni.sec.compose.id.core.service.AttributeDefinitionService;
import de.passau.uni.sec.compose.id.core.service.AttributeValueService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.AttributeDefinitionCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.AttributeValueCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.AttributeValueResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.AttributeValueUpdateMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;



@Controller
@RequestMapping("/idm/group")
public class AttributeDefinitionsController {

	private static Logger LOG = LoggerFactory.getLogger(AttributeDefinitionsController.class);
	
	@Autowired
    private AttributeDefinitionService attributeDefinitionService;
	
	@Autowired
	private RestAuthentication authenticator;
	
    /**
     * Create a new user
     * @param message containing the new user
     * @return userCreatedMessage with the appropiate data or an error otherwise
     */
    @RequestMapping(value = "/{groupId}/attribute_definition/", method = RequestMethod.POST, consumes = "application/json")
        @ResponseBody
        public  ResponseEntity<Object> createEntity( @RequestHeader("Authorization") String token,
        		 @PathVariable(value="groupId") String groupId,  
        		@Valid  @RequestBody AttributeDefinitionCreateMessage message,UriComponentsBuilder builder,HttpServletRequest req) {
		    	 
    	
    			HttpHeaders headers = new HttpHeaders();
		    	Collection<String> cred = new LinkedList<String>();
		    	cred.add(token);
    			try{
    				 
    				//This method just authenticates... it doesn't do access controlgroup
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
    				 EntityResponseMessage res =  attributeDefinitionService.createEntity(new CreateAttributeDefinitionEvent(message,groupId, principals));
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
    
    
    /*@RequestMapping(value = "/{entityId}/", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public  ResponseEntity<Object> updateEntity( 
    		@RequestHeader("Authorization") String token,
    		@RequestHeader("If-Unmodified-Since") long lastKnownUpdate,
    		 @PathVariable(value="entityId") String entitytId,  
    		@Valid  @RequestBody AttributeValueUpdateMessage message,UriComponentsBuilder builder,HttpServletRequest req) {
	    	 
	
			HttpHeaders headers = new HttpHeaders();
	    	Collection<String> cred = new LinkedList<String>();
	    	cred.add(token);
			try{
				 
				//This method just authenticates... it doesn't do access control
				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
				 EntityResponseMessage res =  attributeDefinitionService.updateEntity(new UpdateAttributeValueEvent(entitytId, message,principals, lastKnownUpdate));
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
	 }*/
    
    @RequestMapping(value="/attribute_definition/{entityId}/", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
        @ResponseBody
        public  ResponseEntity<Object> DeteMembership(@RequestHeader("Authorization") String token,
        		@RequestHeader("If-Unmodified-Since") long lastKnownUpdate,
        		@PathVariable(value="entityId") String uid) {

    		HttpHeaders headers = new HttpHeaders();
	    	Collection<String> cred = new LinkedList<String>();
	    	cred.add(token);
			try{
				
				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
				 attributeDefinitionService.deleteEntity(new DeleteAttributeDefinitionEvent(uid,principals,lastKnownUpdate));
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