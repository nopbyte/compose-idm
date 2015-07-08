package de.passau.uni.sec.compose.id.rest.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeComponentPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;
import de.passau.uni.sec.compose.id.core.service.CaptchaService;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateFromExternalAppMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;


@Controller
@RequestMapping("/external_user_create")
public class CreateUserFromExternalAppController {
	private static Logger LOG = LoggerFactory.getLogger(CreateUserFromExternalAppController.class);
    
	@Autowired
	CaptchaService cage;
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value="captcha/", method=RequestMethod.GET, produces = "image/jpg")
    public @ResponseBody byte[] getCaptcha( @RequestParam("session_id") String session){
				
				Collection<String> cred = new LinkedList<String>();
		    	ByteArrayOutputStream os = null;
				
				  
    			try{
    				  String text = cage.getTokenGenerator().next();
    				  if(cage.setText(session, text))
    				  {
    					  os =new ByteArrayOutputStream();
    					  cage.draw(text, os);
    					  LOG.info("returning captcha with text:"+text+ " for session: "+session);
    				  	  return os.toByteArray();
    				  }
    				  return null;
		    	 }
		    	 catch(Exception e)
		    	 {
		    		 String s = IdManagementException.getStackTrace(e);
		    		 LOG.error(s);
		    		 return null;	 
		    	 }
    			 finally {
    				 if(os!=null)
						try {
							os.close();
						} catch (IOException e) {
							//just for logging purposes
							new IdManagementException("can't close byte array output stream in Capctcha controller!", e, LOG, "can't close byte array output stream in Capctcha controller!", Level.ERROR, 400);
						}
    			}
    	 
    	 
        }
	
	
	@RequestMapping(value="/", method=RequestMethod.GET,produces = "application/json")
    public @ResponseBody ResponseEntity<Object>  getUser( ){
				
				try{
					
					String id = cage.getNewSession();
					Map<String, String> ret = new HashMap<>();
					ret.put("session_id",id);
					return new ResponseEntity<Object>(ret, HttpStatus.OK);
    			 }
		    	catch(Exception e)
		    	 {
		    		 String s = IdManagementException.getStackTrace(e);
		    		 LOG.error(s);
		    		 return null;	 
		    	 }
    			
    }
	
	
	@RequestMapping(value="/", method=RequestMethod.POST,produces = "application/json")
    public @ResponseBody ResponseEntity<Object>  createUser( @RequestBody @Valid UserCreateFromExternalAppMessage message ){
				
				try{
					if(cage.verifyText(message.getSession_id(), message.getCaptcha_text()))
					{
						
						ComposeComponentPrincipal p = new ComposeComponentPrincipal();
						p.setComposeComponentName("CAPTCHA verification succeeded");
						Collection<IPrincipal> principals = new LinkedList<>();
						principals.add(p);

						 UserResponseMessage res = (UserResponseMessage) userService.createEntity(new CreateUserEvent(message,principals));
						 cage.setText(message.getSession_id(), "");
					 	 return new ResponseEntity<Object>(res, HttpStatus.OK);
					}
					LOG.info("received wrong captcha with text:"+message.getCaptcha_text()+" for session: "+message.getSession_id());
					//delete the text always, to prevent brute force attacks and repetitions...
					cage.setText(message.getSession_id(), "");
					
					return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    			 }
		    	catch(Exception e)
		    	 {
		    		 String s = IdManagementException.getStackTrace(e);
		    		 LOG.error(s);
		    		 return null;	 
		    	 }
    			
    }
    	 
    	 
}

