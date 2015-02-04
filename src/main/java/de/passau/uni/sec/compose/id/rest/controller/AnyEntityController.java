package de.passau.uni.sec.compose.id.rest.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
import de.passau.uni.sec.compose.id.core.service.ApplicationService;
import de.passau.uni.sec.compose.id.core.service.ServiceCompositionService;
import de.passau.uni.sec.compose.id.core.service.ServiceInstanceService;
import de.passau.uni.sec.compose.id.core.service.ServiceObjectService;
import de.passau.uni.sec.compose.id.core.service.ServiceSourceCodeService;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.Id;

@Controller
@RequestMapping("/idm/any")
public class AnyEntityController {
private static Logger LOG = LoggerFactory.getLogger(AnyEntityController.class);
	
	@Autowired
    private ApplicationService applicationService;
	@Autowired
	private ServiceInstanceService siService;
	@Autowired
	private ServiceSourceCodeService ssService;
	@Autowired
	private ServiceObjectService soService;
	@Autowired
	private ServiceCompositionService scService;
	@Autowired
	private UserService uService;
	
	@Autowired
    private RestAuthentication authenticator;
    
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
			 EntityResponseMessage r =null;
			 Map<String,Object> res = new HashMap<>();
			 try{
				 r = applicationService.getEntity(new GetApplicationEvent(uid,principals));
				 res.put("application",r);
				 r=null;
			 }catch(IdManagementException ex)
			 {
				 if(ex.getHTTPErrorCode()!=404)
					 throw ex;
			 }
			 
			 try{
				 r = siService.getEntity(new GetServiceInstanceEvent(uid,principals));
				 res.put("service_instance",r);
				 r=null;
			 }catch(IdManagementException ex)
			 {
				 if(ex.getHTTPErrorCode()!=404)
					 throw ex;
			 }
			 
			 try{
				 r = ssService.getEntity(new GetServiceSourceCodeEvent(uid,principals));
				 res.put("service_source_code",r);
				 r=null;
			 }catch(IdManagementException ex)
			 {
				 if(ex.getHTTPErrorCode()!=404)
					 throw ex;
			 }
			 
			 try{
				 r = soService.getEntity(new GetServiceObjectEvent(uid,principals));
				 res.put("service_object_code",r);
				 r=null;
			 }catch(IdManagementException ex)
			 {
				 if(ex.getHTTPErrorCode()!=404)
					 throw ex;
			 }
			 
			 
			 try{
				 r = scService.getEntity(new GetServiceCompositionEvent(uid,principals));
				 res.put("service_composition",r);
				 r=null;
			 }catch(IdManagementException ex)
			 {
				 if(ex.getHTTPErrorCode()!=404)
					 throw ex;
			 }
			 
			 try{
				 r = uService.getEntity(new GetUserEvent(uid,principals));
				 res.put("user",r);
				 r=null;
			 }catch(IdManagementException ex)
			 {
				 if(ex.getHTTPErrorCode()!=404)
					 throw ex;
			 }
			 
			 
			 return new ResponseEntity<Object>(res, HttpStatus.OK);
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