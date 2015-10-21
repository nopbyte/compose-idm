package de.passau.uni.sec.compose.id.rest.controller;

import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteUserEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateUserEvent;
import de.passau.uni.sec.compose.id.core.service.UpdateReputation;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.UpdateReputationMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;



@Controller
@RequestMapping("/idm/private/reputation")
@PropertySource("classpath:private-api.propertes")
public class ReputationCommandsController {

	private static Logger LOG = LoggerFactory.getLogger(ReputationCommandsController.class);
	
	@Autowired
    private UpdateReputation reputation;
	
	private String token = null;
	
	@Resource
	private Environment env;
	
	
	
    /**
     * Create a new user
     * @param message containing the new user
     * @return userCreatedMessage with the appropiate data or an error otherwise
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT, consumes = "application/json")
        @ResponseBody
        public  ResponseEntity<Object> createUser( @RequestHeader("Authorization") String token,
        		@RequestBody UpdateReputationMessage[] updates,UriComponentsBuilder builder,HttpServletRequest req) {
		    	 
    	
    			HttpHeaders headers = new HttpHeaders();
		    	
    			try{
    				 
    				if(env.getProperty("private.api.token").equals(token)){
    					for(UpdateReputationMessage r: updates)
    						reputation.updateReputation(r.getEntity_type(), r.getId(),r.getReputation());
    				}
    				return new ResponseEntity<Object>( HttpStatus.OK);
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