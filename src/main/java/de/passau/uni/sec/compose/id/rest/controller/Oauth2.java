package de.passau.uni.sec.compose.id.rest.controller;


import java.util.HashMap;

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
import de.passau.uni.sec.compose.id.core.service.security.TokenResponse;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.rest.messages.UserAuthenticatedMessage;

@Controller
public class Oauth2 {

	//  GET /authorize?response_type=token&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
	

	@Autowired
	UsersAuthzAndAuthClient uaaClient;
	
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
    		Model model) {
		
		// GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz
		model.addAttribute("response_type", resposeType);
		model.addAttribute("client_id", clientId);
		model.addAttribute("state", state);
		model.addAttribute("redirect_uri", url);;
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
        
    	
    	TokenResponse res;
		try {
			res = uaaClient.getImplicitTokenCredentials(null, username, password);
			if(resposeType.toUpperCase().equals("CODE"))
			{
				//TODO in the future this could be actually a code instead of a token??
				return "redirect:" + url+"?code="+res.getAccessToken();
			}
			return "redirect:" + url+"#access_token="+res.getAccessToken()+"&state="+state+"&token_type=bearer&expires_in=3600";
			
			
		} catch (IdManagementException e) {
			
			return "redirect:" + url+"#error=access_denied&state="+state;
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
    
    @RequestMapping(value = {"token","oauth/token"}, method = RequestMethod.POST, produces="application/json")
    public ResponseEntity<HashMap<String, Object>> token(@RequestHeader("Authorization") String clientAuthentication,
    		@RequestParam(value="grant_type", required=true, defaultValue="authorization_code") String grantType,
    		@RequestParam(value="code", required=true, defaultValue="clientId") String code,
    		@RequestParam(value="redirect_uri", required=true) String url,
    		Model model) {
    	
    	HashMap<String, Object> map = new HashMap<>();
    	
    	//TODO at some point we could verify authenticatoin of client?? with the Authorization header?
    	
    	if(grantType.equals("authorization_code"))
    	{
    		map.put("access_token",code);
    		map.put("token_type","bearer");
    		map.put("expires_in",3600);
    		HttpHeaders headers = new HttpHeaders();
    		headers.add("Cache-Control",  "no-store");
    		headers.add("Pragma","no-cache");
    		return new ResponseEntity<>(map, headers, HttpStatus.OK);
        	
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

}