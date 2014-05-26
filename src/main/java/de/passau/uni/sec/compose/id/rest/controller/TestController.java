package de.passau.uni.sec.compose.id.rest.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;




import org.springframework.beans.factory.annotation.Autowired;


@Controller
@RequestMapping("/Test")
public class TestController {
	private static Logger LOG = LoggerFactory.getLogger(TestController.class);
    
	@Autowired
	UsersAuthzAndAuthClient client;
	/*
    @RequestMapping(value="/{groupId}", method=RequestMethod.GET)
    public @ResponseBody Greeting greeting( @RequestHeader("Authorization") String token,
    		@PathVariable(value="groupId") String gid) {
    	
    	
    	new IdManagementException("message to the user", new IOException(), LOG, "{\"json\":\"data\"}",IdManagementException.Level.INFO,401);
    	new IdManagementException("message to the user", null, LOG, "{\"json\":\"data\"}",IdManagementException.Level.INFO,401);
        LOG.info("hello");
    	
    	return new Greeting(1,
                           client.getUAAUrl()+ "\n\nthis is the uid for the user you want to see:"+gid+ "This is the token you are providing!:"+token);
    }*/
   

}