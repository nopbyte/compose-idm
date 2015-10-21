package de.passau.uni.sec.compose.id.core.service.security.uaa;



import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ibm.cloudfoundry.CloudUserRegistration;
import com.ibm.cloudfoundry.IDMConnector;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.service.security.TokenResponse;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.rest.client.HTTPClient;


 

@Service
public class UAAClient implements UsersAuthzAndAuthClient
{
	private static Logger LOG = LoggerFactory.getLogger(UAAClient.class);	
	
	private static String oauthAdminToken = null;
	
	private  String UAAUrl;

	private String password;
	
	private String username;
	
	private String cCurl;
	
	private String adminuser;
	
	private String adminpassword;
	
	private String org;
	
	private String space;
	
	private String clientId;
	
	private String scope;
	
	private String redirectUriBase;
	
	
	private String getOauthAdminAuthToken() throws IdManagementException
	{
		//TODO manage expired admin token!!
		// check expiration date of token (Oauth2 library? JWT?)
		/*if(this.oauthAdminToken==null)
		{*/
			TokenResponse res = getClientCredentialsToken(username,password);	
			this.oauthAdminToken =  res.getAccessToken();
		/*}*/
		return oauthAdminToken;
	}
	
	
    public UAAClient() {

        // load properties file from classpath
        Properties properties = new Properties();
        ClassPathResource resource = new ClassPathResource("uaa.properties");
      
        try {
            properties.load(resource.getInputStream());
        } catch (IOException e) {
            LOG.error("Error while reading uaa properties file.");
            e.printStackTrace();
        }

        this.UAAUrl = properties.getProperty("uaa.url");
        this.username = properties.getProperty("client.credentials.admin.username");
        this.password = properties.getProperty("client.credentials.admin.pass");
        this.clientId = properties.getProperty("compose.client.id");
        this.redirectUriBase = properties.getProperty("compose.client.redirect");
        this.cCurl = properties.getProperty("cCurl");
        this.adminuser= properties.getProperty("admin-user");
        this.adminpassword = properties.getProperty("admin-password");
        this.org = properties.getProperty("org");
        this.space = properties.getProperty("space");
        
    }

	@Override
	public String getUAAUrl() {
		
		return UAAUrl;
	}
	

	@Override
	public TokenResponse getClientCredentialsToken(String username, String password) throws IdManagementException 
	{
		ResponseEntity<HashMap> responseEntity = null;
		HTTPClient<HashMap> http = new HTTPClient<>();
		HttpHeaders headers = http.createBasicAuthenticationHttpHeaders(username, password);
		headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "client_credentials");
		String url = this.UAAUrl+"/oauth/token";
		responseEntity = http.getDataHTTPCall(http.POST_REST, url, null, map,headers, HashMap.class);
		TokenResponse tr = new TokenResponse();
		Map<String,String> res = responseEntity.getBody();
		

		String val = (res.get("access_token"));
		if(val != null)
			tr.setAccessToken(val);
		val = res.get("jti"); 
		if(val != null)
			tr.setJti(val);
		val = res.get("scope"); 
		if(val != null)
			tr.setToken_scope(val);
		val = res.get("token_type"); 
		if(val != null)
			tr.setToken_type(val);
		
		return tr;
		
	}
	
	public TokenResponse getImplicitTokenCredentials(String client, String username, String password) throws IdManagementException
	{
		
		try{
		    IDMConnector cc = new IDMConnector(cCurl,null,username,password);
		    TokenResponse res = cc.getTokenForUserAndPassword(LOG);
		    return res;
		}catch(IdManagementException ex)
		{
			throw ex;
		}
		catch(Exception e)
		{
			if(e.getMessage().contains("401"))
					throw new IdManagementException("Authentication failed, wrong credentials ",null, LOG," Incorrect credentials while authenticating with UAA",Level.ERROR, 401);
			LOG.error("Problems while getting token using IDMCConnector from Cloud Foundry (IBM)"+IdManagementException.getStackTrace(e));
			throw new IdManagementException("An error ocurred while communicating UAA through Library for CloudFoundry (IBM)",e,LOG,"An error ocurred while communicating UAA through Library for CloudFoundry (IBM)",Level.ERROR,500);
		}
		/*
		if(client == null)
			client = this.clientId;
		
		HTTPClient<HashMap> http = new HTTPClient<>();
		ResponseEntity<HashMap> responseEntity = null;
		
		MultiValueMap<String, String> urlQueryParams = new LinkedMultiValueMap<String, String>();
		
		urlQueryParams.add("client_id",client);
		urlQueryParams.add("response_type", "token");
		urlQueryParams.add("redirect_uri", this.redirectUriBase+client);
		urlQueryParams.add("state", "8658ae7786b51a8aa750abf28815455e");
    

        MultiValueMap<String, String> postData = new LinkedMultiValueMap<>();
        postData.add("username", username);
        postData.add("password", password);
        postData.add("source", "credentials");
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept","application/json;charset=utf-8");
        
        TokenResponse tr = new TokenResponse();
        
        
        String url = this.UAAUrl+"/oauth/authorize";
        //process the post request as a Form encoded post message.
		responseEntity = http.getDataHTTPCall(http.POST_FORM, url, urlQueryParams, postData, headers, HashMap.class);
		if(responseEntity.getStatusCode().equals(HttpStatus.FOUND))
		{
			URI uri;
			try {
				uri = new URI(responseEntity.getHeaders().getFirst("Location"));
				String fragment =  uri.getFragment();
				if(fragment != null)
				{
					String []split = fragment.split("&");
					for(String s: split)
					{
						
						if(s.contains("access_token"))
							tr.setAccessToken(getStringValue(s));
						if(s.contains("token_type"))
							tr.setToken_type(getStringValue(s));
						if(s.contains("scope"))
							tr.setToken_scope(getStringValue(s));
						if(s.contains("jti"))
							tr.setJti(getStringValue(s));

					}	
					
					if(tr.getAccessToken()!=null)
						return tr;
				}
			} catch (MalformedURIException e) {
				throw new IdManagementException("An error ocurred while communicating with an external server ",e,LOG,"An error ocurred trying to parse the uri: "+responseEntity.getHeaders().getFirst("Location")+" after HTTP communication with "+url,Level.ERROR,500);
			}
				
		}
		else if(responseEntity.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
		{
			LOG.error( "Incorrect credentials \""+username+"\" and \""+password+"\"");
			throw new IdManagementException("Authentication failed, wrong credentials ",null, LOG," Incorrect credentials \""+username+"\" and \""+password+"\"",Level.INFO, 401);
		}
		throw new IdManagementException("Authentication failed.",null, LOG," Incorrect credentials \""+username+"\" and \""+password+"\"",Level.ERROR, responseEntity.getStatusCode().value());
		
		
		 */
		
	}
	
	/**
	 * 
	 * @param s string in the form expression=value
	 * @return value 
	 */
	private String getStringValue(String s) 
	{
		
		int pos = s.indexOf("=")+1;
		if(pos>0)
		{
			return s.substring(pos);
		}
		return null;
	}

	public OpenIdUserData getOpenIdData(String token) throws IdManagementException
	{
		ResponseEntity<OpenIdUserData> responseEntity = null;
		HTTPClient<OpenIdUserData> http = new HTTPClient<>();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
        headers.add("Accept","application/json;charset=utf-8");
        
        String url = this.UAAUrl+"/userinfo?schema=openid";
        try{
        responseEntity = http.getDataHTTPCall(http.GET, url, null, null, headers, OpenIdUserData.class);
        }catch(IdManagementException ex)/*MAKE sure to always forward the IdManagementException to avoid losing information.*/
        {
        	throw ex;
        }
        catch(Exception e)
        {
        	LOG.error("An Exception ocurred trying to parse the uri: "+responseEntity.getHeaders().getFirst("Location")+" after HTTP communication with "+url+ " response: "+responseEntity.toString());
        	throw new IdManagementException("An error ocurred while getting information for the user",null,LOG,"An error ocurred trying to parse the uri: "+responseEntity.getHeaders().getFirst("Location")+" after HTTP communication with "+url+ " response: "+responseEntity.toString(),Level.INFO,responseEntity.getStatusCode().value());
        }
        if(!responseEntity.getStatusCode().equals(HttpStatus.OK))
        {
        	LOG.error("Unexpected error code "+responseEntity.getStatusCode()+" trying to parse the uri: "+responseEntity.getHeaders().getFirst("Location")+" after HTTP communication with "+url+ " response: "+responseEntity.toString());
        	throw new IdManagementException("An error ocurred while getting information for the user",null,LOG,"An error ocurred trying to parse the uri: "+responseEntity.getHeaders().getFirst("Location")+" after HTTP communication with "+url+ " response: "+responseEntity.toString(),Level.INFO,responseEntity.getStatusCode().value());
        }
        
        return responseEntity.getBody();
	}

	
	@Override
	public Map<String, Object> createUser(Serializable userData)
			throws IdManagementException {
		
		UAAUserRequest req = (UAAUserRequest) userData;
		
		HTTPClient<HashMap<String,Object>> http = new HTTPClient<>();
		ResponseEntity<HashMap> responseEntity = null;
		String url = this.UAAUrl+"/Users";
		
		 
		HttpHeaders headers = http.createBasicAuthenticationHttpHeaders(username, password);
		LOG.debug("Setting token to:"+getOauthAdminAuthToken());
		LOG.info("Retrieving token from UAA successful");
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		headers.set("Authorization", "Bearer " +getOauthAdminAuthToken());
		headers.add("Accept","application/json;charset=utf-8");
		
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
        try{
        	HttpEntity request = new HttpEntity(req, headers);
            responseEntity= restTemplate.exchange(url, HttpMethod.POST, request,
			HashMap.class);
        }
        catch(HttpClientErrorException ce)
        {
        	if(ce.getStatusCode().equals(HttpStatus.CONFLICT))
        		throw new IdManagementException("Username already exists",null,LOG,"Conflict while attempting to create a user "+url+ " response: "+ce.getResponseBodyAsString(),Level.ERROR,ce.getStatusCode().value());
        	if(ce.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
        		throw new IdManagementException("An error ocurred during HTTP communication",ce,LOG,"Admin account in UAA is not authorized to create users!. This shouldn't happen!! HttClientError  while attempting to create a User. StatusCode:"+ce.getStatusCode()+ce.getStatusText()+" response Message"+ce.getResponseBodyAsString(),Level.ERROR,500);
        	throw new IdManagementException("An error ocurred during HTTP communication",ce,LOG,"HttClientError  while attempting to create a User. StatusCode:"+ce.getStatusCode()+ce.getStatusText()+" response Message"+ce.getResponseBodyAsString(),Level.ERROR,500);
        }
        catch(Exception e)
        {
        	throw new IdManagementException("An error ocurred during HTTP communication",e,LOG,"Unknown exception while trying to create a user: Exception"+e.getClass(),Level.ERROR,500);
        }
			
		if(!responseEntity.getStatusCode().equals(HttpStatus.CREATED)&&!responseEntity.getStatusCode().equals(HttpStatus.OK))
			LOG.error("Error ocurred during creation or a UAA user, status code: "+responseEntity.getStatusCode().value());
		
		return responseEntity.getBody();
	}
	
	@Override
	public void deleteUser(String userId)
			throws IdManagementException {
		
		HTTPClient<HashMap<String,Object>> http = new HTTPClient<>();
		ResponseEntity<HashMap> responseEntity = null;
		String url = this.UAAUrl+"/Users/"+userId;
		
		 
		HttpHeaders headers = http.createBasicAuthenticationHttpHeaders(username, password);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " +getOauthAdminAuthToken());
		headers.add("Accept","application/json;charset=utf-8");
		
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
        try{
        	HttpEntity request = new HttpEntity(headers);
            responseEntity= restTemplate.exchange(url, HttpMethod.DELETE, request,
			HashMap.class);
        }
        catch(HttpClientErrorException ce)
        {
        	if(ce.getStatusCode().equals(HttpStatus.NOT_FOUND))
        		throw new IdManagementException("User not found",null,LOG,"Attempting to delete a nonexisting user "+url+ " response: "+ce.getResponseBodyAsString(),Level.ERROR,ce.getStatusCode().value());
        	if(ce.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
        		throw new IdManagementException("An error ocurred during HTTP communication",ce,LOG,"Admin account in UAA is not authorized to create users!. This shouldn't happen!! HttClientError  while attempting to create a User. StatusCode:"+ce.getStatusCode()+ce.getStatusText()+" response Message"+ce.getResponseBodyAsString(),Level.ERROR,500);
        	throw new IdManagementException("An error ocurred during HTTP communication",ce,LOG,"HttClientError  while attempting to create a User. StatusCode:"+ce.getStatusCode()+ce.getStatusText()+" response Message"+ce.getResponseBodyAsString(),Level.ERROR,500);
        }
        catch(Exception e)
        {
        	throw new IdManagementException("An error ocurred during HTTP communication",e,LOG,"Unknown exception while trying to create a user: Exception"+e.getClass(),Level.ERROR,500);
        }
			
		if(!responseEntity.getStatusCode().equals(HttpStatus.OK))
			LOG.error("Error ocurred during deletion of  a UAA user, status code: "+responseEntity.getStatusCode().value());
		
	}


	@Override
	public void changePassword(String userToken, String id, String old_password,
			String new_password) throws IdManagementException {
		
		UAAUserPasswordRequest pass = new UAAUserPasswordRequest();
		
		pass.setOldPassword(old_password);
		pass.setPassword(new_password);
		
		HTTPClient<HashMap<String,Object>> http = new HTTPClient<>();
		ResponseEntity<HashMap> responseEntity = null;
		String url = this.UAAUrl+"/Users/"+id+"/password";
		
		 
		HttpHeaders headers = http.createBasicAuthenticationHttpHeaders(username, password);
		headers.setAccept(Collections.singletonList(new MediaType("application","json")));
		headers.set("Authorization", "bearer " +userToken);
		headers.add("Content-Type","application/json;charset=utf-8");
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		
		
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
        try{
        	HttpEntity request = new HttpEntity(pass, headers);
            responseEntity= restTemplate.exchange(url, HttpMethod.PUT, request,
			HashMap.class);
        }
        catch(HttpClientErrorException ce)
        {
        	if(ce.getStatusCode().equals(HttpStatus.CONFLICT))
        		throw new IdManagementException("Conflict while changing password",null,LOG,"Conflict while attempting to change password for user "+url+ " response: "+ce.getResponseBodyAsString(),Level.ERROR,ce.getStatusCode().value());
        	if(ce.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
        	{
        		if(responseEntity!=null && responseEntity.getBody() !=null && responseEntity.getBody().containsKey("description"))
        			throw new IdManagementException(responseEntity.getBody().get("description").toString(), ce, LOG, "Error while chaning password. Description from UAA"+responseEntity.getBody().get("description").toString(), Level.DEBUG , 401);
        		throw new IdManagementException("Password change usuccessfull. Not authorized",ce,LOG," Unauthorized to change password for user with id "+id+". StatusCode:"+ce.getStatusCode()+ce.getStatusText()+" response Message"+ce.getResponseBodyAsString(),Level.ERROR,500);
        	}throw new IdManagementException("An error ocurred during HTTP communication",ce,LOG,"HttClientError  while attempting to create a User. StatusCode:"+ce.getStatusCode()+ce.getStatusText()+" response Message"+ce.getResponseBodyAsString(),Level.ERROR,500);
        }
        catch(Exception e)
        {
        	throw new IdManagementException("An error ocurred during HTTP communication",e,LOG,"Unknown exception while trying to create a user: Exception"+e.getClass(),Level.ERROR,500);
        }
			
		if(!responseEntity.getStatusCode().equals(HttpStatus.CREATED)&&!responseEntity.getStatusCode().equals(HttpStatus.OK))
			LOG.error("Error ocurred during creation or a UAA user, status code: "+responseEntity.getStatusCode().value());
		
		
	}
	



	@Override
	public void removeUserFromCloud(String uid) throws IdManagementException 
	{
		CloudUserRegistration cr = new CloudUserRegistration();
		cr.unregisterUserFromCloud(cCurl, uid ,  this.adminuser, this.adminpassword);
	}


	@Override
	public void setupUserInCloud(String username, String password, String id)
			throws IdManagementException {
		
		CloudUserRegistration cr = new CloudUserRegistration();
		cr.setupUserInCloud(cCurl,username, password, id , this.org, this.space, this.adminuser, this.adminpassword); 
		
		
	}
	
	
}
