package de.passau.uni.sec.compose.id.rest.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.GetApplicationEvent;
import de.passau.uni.sec.compose.id.core.event.GetServiceCompositionEvent;
import de.passau.uni.sec.compose.id.core.event.GetServiceInstanceEvent;
import de.passau.uni.sec.compose.id.core.event.GetServiceObjectEvent;
import de.passau.uni.sec.compose.id.core.event.GetServiceSourceCodeEvent;
import de.passau.uni.sec.compose.id.core.event.GetUserEvent;
import de.passau.uni.sec.compose.id.core.service.AnyEntityById;
import de.passau.uni.sec.compose.id.core.service.ApplicationService;
import de.passau.uni.sec.compose.id.core.service.CloudPublisher;
import de.passau.uni.sec.compose.id.core.service.ServiceCompositionService;
import de.passau.uni.sec.compose.id.core.service.ServiceInstanceService;
import de.passau.uni.sec.compose.id.core.service.ServiceObjectService;
import de.passau.uni.sec.compose.id.core.service.ServiceSourceCodeService;
import de.passau.uni.sec.compose.id.core.service.UpdateManager;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.Id;

@Controller
@RequestMapping("/idm/update/push/")
public class TriggerUpdateEntityController {
private static Logger LOG = LoggerFactory.getLogger(TriggerUpdateEntityController.class);
	

	@Autowired
	private AnyEntityById entityById;
	
	@Autowired
    private RestAuthentication authenticator;

	@Autowired
	UpdateManager update;
	
	@RequestMapping(value="{Id}/", method={RequestMethod.GET})
    public @ResponseBody ResponseEntity<Object> getUser( @RequestHeader("Authorization") String token,
    		@PathVariable(value="Id") String uid,UriComponentsBuilder builder
    		){
				return doCall(token, uid);
    	 
        }
	
	@RequestMapping(value="", method={RequestMethod.POST})
    public @ResponseBody ResponseEntity<Object> getUser( @RequestHeader("Authorization") String token,
    		UriComponentsBuilder builder,
    		@Valid @RequestBody Id message){
		
			String uid = message.getId();
			return doCall(token, uid);
    	 
        }


	private ResponseEntity<Object> doCall(String token, String uid) {
		Collection<String> cred = new LinkedList<String>();
		cred.add(token);
		try{
			 //This method just authenticates... it doesn't do access control
			 Collection<IPrincipal> principals = authenticator.authenticatePrincipals(LOG,cred);
			 int status = update.handleUpdateForEntity(uid,principals);
			 return new ResponseEntity<Object>(status, HttpStatus.OK);
		 }
		 catch(IdManagementException idm){
			 //sinc@Autowirede the creation of the exception generated the log entries for the stacktrace, we don't do it again here
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