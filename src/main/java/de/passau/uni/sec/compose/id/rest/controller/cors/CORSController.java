package de.passau.uni.sec.compose.id.rest.controller.cors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.DispatcherServlet;

@RestController
public class CORSController {

	private static Logger LOG = LoggerFactory.getLogger(CORSController.class);
	
	// This is a global configuration that lets us implement our own OPTIONS request handles... the "simpleCORSController" from spring didn't work...
	@Bean
	public DispatcherServlet dispatcherServlet() {
	    DispatcherServlet servlet = new DispatcherServlet();
	    servlet.setDispatchOptionsRequest(true);
	    return servlet;
	}
	
    
    public CORSController() {

	}
    
   @RequestMapping(value="/**",method = RequestMethod.OPTIONS)
    public ResponseEntity<Object> greetingOP(@RequestHeader(required=false) MultiValueMap<String, String> head,
    		@RequestBody(required=false) MultiValueMap<String, String> body ) {
    	LOG.info("executing OPTIONS request for CORS");
  	    HttpHeaders headers = new HttpHeaders();
        //This is located in the CORS filter
  	    //headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,  Authorization");
    	headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
    	headers.add("Access-Control-Max-Age", "3600");
    	headers.add("Access-Control-Allow-Headers", "x-requested-with");
		return new ResponseEntity<Object>(null, headers, HttpStatus.NO_CONTENT);
    }
    
    
}
