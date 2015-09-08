package de.passau.uni.sec.compose.id.rest.controller;


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.persistence.entities.Code;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceInstanceRepository;
import de.passau.uni.sec.compose.id.core.service.CodeService;
import de.passau.uni.sec.compose.id.core.service.ServiceInstanceService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.core.service.security.TokenResponse;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.rest.messages.UserAuthenticatedMessage;

@Controller
public class Oauth2 {

	private static Logger LOG = LoggerFactory.getLogger(Oauth2.class);

	//  GET /authorize?response_type=token&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
	

	@Autowired
	UsersAuthzAndAuthClient uaaClient;
	
	@Autowired
	CodeService codeService;
	
	@Autowired
	RestAuthentication restAuth;
	
	@Autowired
	ServiceInstanceRepository serviceInstanceRepo;
	
	/**
	 * implicit grant! respond with the credentials as a URL fragment
	 * @param resposeType
	 * @param clientId
	 * @param state
	 * @param url
	 * @param model
	 * @return
	 */
	@RequestMapping(value={"/authorize","oauth/authorize"})
    public String authorize(@RequestParam(value="response_type", required=false, defaultValue="token") String resposeType,
    		@RequestParam(value="client_id", required=false, defaultValue="clientId") String clientId,
    		@RequestParam(value="state", required=false, defaultValue="pre") String state,
    		@RequestParam(value="redirect_uri", required=true) String url,
    		@RequestParam(value="error", required=false, defaultValue="") String error,
    		Model model) {
		
		// GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz
		LOG.info("Calling authorization endpoint from: "+url+" with clientId: "+clientId+" and responseType: "+resposeType);
		model.addAttribute("response_type", resposeType);
		model.addAttribute("client_id", clientId);
		model.addAttribute("state", state);
		model.addAttribute("redirect_uri", url);
		if(error!= null && !"".equals(error))
		{
			model.addAttribute("error", error);
		}
		
        return "login";
	
    }
	
	
    @RequestMapping(value ={"/login","/oauth/login"}, method = RequestMethod.POST)
    public String login(
    		@RequestParam(value="response_type", required=true, defaultValue="token") String resposeType,
    		@RequestParam(value="client_id", required=true) String clientId,
    		@RequestParam(value="state", required=false, defaultValue="pre") String state,
    		@RequestParam(value="redirect_uri", required=true) String url,
    		@RequestParam(value="username", required=true) String username,
    		@RequestParam(value="password", required=true) String password,
    		Model model) {
        
    	LOG.info("Posting credentials to Login controller with redirect_url: "+url+" with clientId: "+clientId+" and responseType: "+resposeType+" and username: "+username);
    	TokenResponse res;
		try {
			res = uaaClient.getImplicitTokenCredentials(null, username, password);
			if(resposeType.toUpperCase().equals("CODE"))
			{
				String code = generateCode();
				codeService.addCode(code, encodeOauthRequestData(username, password,clientId), CodeService.TYPE_OAUTH2_CODE);				//TODO in the future this could be actually a code instead of a token??
				return "redirect:" + url+"?code="+code;
			}
			else{
				return "redirect:" + url+"#access_token="+res.getAccessToken()+"&state="+state+"&token_type=bearer&expires_in=3600";
			}
			
		} catch (IdManagementException e) {
			
			 model.addAttribute("loginError", true);
			 model.addAttribute("response_type", resposeType);
			 model.addAttribute("client_id", clientId);
			 model.addAttribute("state", state);
			 model.addAttribute("redirect_uri", url);
			 model.addAttribute("username", username);
			 model.addAttribute("password",password);
	    		 
			//TODO change this! do not redirect... reload idm page.
			return "login";
		}
		
    	/*
		 *  HTTP/1.1 302 Found
     Location: http://example.com/cb#access_token=2YotnFZFEjr1zCsicMWpAA
               &state=xyz&token_type=example&expires_in=3600
		 */
		
		/* ERROR url
		 * HTTP/1.1 302 Found
   Location: https://client.example.com/cb#error=access_denied&state=xyz
		 */
		
    }
    /*
     *      POST /token HTTP/1.1
     Host: server.example.com
     Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
     Content-Type: application/x-www-form-urlencoded

     grant_type=password&username=johndoe&password=A3ddj3w
     * */





	private String generateCode()
	{
		return UUID.randomUUID().toString().replaceAll("-","").substring(0,10);
	}
    
    @RequestMapping(value = {"token","oauth/token"}, method = RequestMethod.POST, produces="application/json")
    public ResponseEntity<HashMap<String, Object>> token(@RequestHeader("Authorization") String clientAuthentication,
    		@RequestParam(value="grant_type", required=true, defaultValue="authorization_code") String grantType,
    		@RequestParam(value="code", required=true, defaultValue="clientId") String code,
    		@RequestParam(value="redirect_uri", required=true) String url,
    		Model model) throws IdManagementException {
    	
    	LOG.info("Retrieving token for auth code: "+code+" with redirect_url: "+url+" with Authorization heder: "+clientAuthentication);
    	HashMap<String, Object> map = new HashMap<>();
    	
    	//TODO at some point we could verify authenticatoin of client?? with the Authorization header?
    	
    	if(grantType.equals("authorization_code"))
    	{
    		
    		Code tokenCode = codeService.getCode(code, CodeService.TYPE_OAUTH2_CODE);
    		if(tokenCode!=null)
    		{
    			List<String> credentials = decodeOauth2RequestData(tokenCode.getReference());
				TokenResponse res = uaaClient.getImplicitTokenCredentials(null, credentials.get(0), credentials.get(1));
    			map.put("access_token",res.getAccessToken());
    			map.put("token_type","bearer");
    			map.put("expires_in",3600);
    			HttpHeaders headers = new HttpHeaders();
    			headers.add("Cache-Control",  "no-store");
    			headers.add("Pragma","no-cache");
    			codeService.deleteCode(tokenCode);
    			return new ResponseEntity<>(map, headers, HttpStatus.OK);
    		}
        	
    	}
    	//TODO check this...
		return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
    	/*		
    	     {
       "access_token":"2YotnFZFEjr1zCsicMWpAA",
       "token_type":"example",
       "expires_in":3600,
       "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
       "example_parameter":"example_value"
     }*/
    	
    	
    }
    //grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb

	private String encodeOauthRequestData(String username, String password, String clientId)
	{
		return username+"#!"+password+"#!"+clientId;
	}

	private List<String> decodeOauth2RequestData(String reference)
	{
		String[] arr = reference.split("#!");
		return Arrays.asList(arr);
		
	}

}