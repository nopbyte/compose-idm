package de.passau.uni.sec.compose.id.rest.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;



import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAClient;

public class HTTPClient<T>
{
	private static Logger LOG = LoggerFactory.getLogger(HTTPClient.class);	
    
	public static final String POST_FORM = "form";
	
	public static final String POST_REST ="rest";
	
	public static final String GET ="get";


	
	public ResponseEntity<T> getDataHTTPCall(String messageType, String url, MultiValueMap<String, String> urlQueryParams, MultiValueMap<String, String> postData,HttpHeaders headers, Class returnTypeImplementation) throws IdManagementException
	{
		ResponseEntity<T> responseEntity = null;
		try
		{
			
			RestTemplate restTemplate = new RestTemplate();
			if(messageType.equals(POST_FORM))
			{
				List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
				messageConverters.add(new FormHttpMessageConverter());
				restTemplate.setMessageConverters(messageConverters);
				headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");
			}
			if(messageType.equals(POST_REST))
			{
				/*List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		        messageConverters.add(new MappingJackson2HttpMessageConverter());
		        restTemplate.setMessageConverters(messageConverters);*/
				 headers.add("Accept","application/json;charset=utf-8");
			}
		
	        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
	        if(urlQueryParams != null)
	        {
	        	for(String k: urlQueryParams.keySet())
	        	{
	        		List<String> list = urlQueryParams.get(k);
	        		for(String v: list)
	        		{
	        			builder.queryParam(k,v);
	        		}
	        	}
	        }
			if(messageType.equals(GET))
			{
				HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
				responseEntity= restTemplate.exchange(url, HttpMethod.GET, httpEntity,
		                returnTypeImplementation);
			}
			else
			{ 
				HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(
	                postData, headers);       
				responseEntity= restTemplate.exchange(builder.build().toUri().toString(), HttpMethod.POST, request,
	        		returnTypeImplementation);
			}
		}
		catch(HttpClientErrorException clientError)
		{
			if(clientError.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
					throw new IdManagementException("Authentication failed, wrong credentials ",clientError,LOG,"Unauthorized while attempting to get token to "+url+" with Headers: "+headers+" and postdata "+postData+" and urlQueryParams "+urlQueryParams,Level.INFO,401);
			throw new IdManagementException("An error ocurred during HTTP communication",clientError,LOG,"HttClientError  while attempting "+messageType+" message to "+url+" with Headers: "+headers+" and postdata "+postData+" and urlQueryParams "+urlQueryParams,Level.ERROR,500);
		}
		catch(RestClientException restE)
		{
			throw new IdManagementException("An error ocurred during HTTP communication",restE,LOG,"RestException  while attempting "+messageType+" message to "+url+" with Headers: "+headers+" and postdata "+postData+" and urlQueryParams "+urlQueryParams,Level.ERROR,500);
		}
		catch(Exception e)
		{
			throw new IdManagementException("An error ocurred during HTTP communication",e,LOG,"Unknown exception while attempting "+messageType+" message to "+url+" with Headers: "+headers+" and postdata "+postData+" and urlQueryParams "+urlQueryParams,Level.ERROR,500);	
		}
		return responseEntity;
		
	}
	
	/**
	 * 
	 * @return 
	 */
	public  HttpHeaders createBasicAuthenticationHttpHeaders(String user, String password) {

        String plainCreds = user + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        String base64Creds = DatatypeConverter.printBase64Binary(plainCredsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        return headers;
    }
	
	
}
