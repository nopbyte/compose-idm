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
import de.passau.uni.sec.compose.id.core.event.GetServiceCompositionEvent;
import de.passau.uni.sec.compose.id.core.service.ServiceCompositionService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;

@Controller
@RequestMapping("/idm/servicecomposition")
public class ServiceCompositionDetailsController {
private static Logger LOG = LoggerFactory.getLogger(ServiceCompositionDetailsController.class);
	
	@Autowired
    private ServiceCompositionService serviceCompositionService;
	
	@Autowired
    private RestAuthentication authenticator;
    
	@RequestMapping(value="/{Id}", method=RequestMethod.GET)
    public @ResponseBody ResponseEntity<Object> getUser( @RequestHeader("Authorization") String token,
    		@PathVariable(value="Id") String uid,UriComponentsBuilder builder){
				
				Collection<String> cred = new LinkedList<String>();
		    	cred.add(token);
    			try{
    				 //This method just authenticates... it doesn't do access control
    				 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
		    		 EntityResponseMessage res = serviceCompositionService.getEntity(new GetServiceCompositionEvent(uid,principals));
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