package de.passau.uni.sec.compose.id.core.service.servioticy;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceObjectRepository;
import de.passau.uni.sec.compose.id.rest.client.HTTPClient;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectResponseMessage;


@Service
@PropertySource("classpath:servioticy.properties")
public class ServioticyManager 
{
	private static Logger LOG = LoggerFactory.getLogger(ServioticyManager.class);
	
	@Autowired
	private Environment env;
	
	private String servioticyPrivateUrl;
	
	@Autowired
	private ServiceObjectRepository soRepo;
	
	@PostConstruct
	public void postConstruct()
	{
		this.servioticyPrivateUrl = env.getProperty("servioticy.private.url");

	}
	
	/**
	 * Attempts to update an SO, in case it indeed is...
	 * @param id
	 * @throws IdManagementException 
	 */
	public void attemptToUpdateSO(String id) throws IdManagementException
	{
			ServiceObject soRaw = soRepo.findOne(id);
			if(soRaw != null)
			{
				ServiceObjectResponseMessage rso = new ServiceObjectResponseMessage(soRaw);
				Map<String, Object> r = getMapFromServioticy(id);
				Map<String, Object> res = (Map<String, Object>) r.get("security");
		        ObjectMapper m = new ObjectMapper();
		        Map<String,Object> props = m.convertValue(rso, Map.class);
		        props.remove("policy");
		        for(String key: props.keySet())//replace new values of IDM stuff
		        	res.put(key, props.get(key));
		        PostMapToServioticy(id, r);
		    }
		
	}
	
	private void PostMapToServioticy(String id, Map<String, Object> so)
			throws IdManagementException
	{

		ResponseEntity<Object> responseEntity= null;
        try{
	
			String url = this.servioticyPrivateUrl+"/"+id;
	        HttpHeaders header = new HttpHeaders();
	        HttpEntity<Map> updateUser = new HttpEntity<Map>(
	                so, header);
	        RestTemplate restTemplate = new RestTemplate();
	        responseEntity= restTemplate
	                .exchange(url, HttpMethod.PUT,
	                        updateUser, Object.class);
	
	        if(responseEntity.getStatusCode().equals(HttpStatus.OK))
	    		LOG.info("Servioticy updated successfully for SO with ID"+id);	
	    	else
	    		throw new IdManagementException("Unexpected http response value when updating for the service object ",null,LOG,"Unexpected error code :"+responseEntity.getStatusCode().toString(), Level.ERROR, (responseEntity==null?500:responseEntity.getStatusCode().value()));
    	 
        }catch(Exception ex)
        {
        	throw new IdManagementException("An error ocurred while getting policy for the service object",null,LOG,"An error ocurred trying to access servioticy" , Level.ERROR, (responseEntity==null?500:responseEntity.getStatusCode().value()));
        }		
	}

	private Map<String, Object> getMapFromServioticy(String id)
			throws IdManagementException
	{
		ResponseEntity<Map> responseEntity = null;
		HTTPClient<Map> http = new HTTPClient<>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept","application/json;charset=utf-8");
		String url = this.servioticyPrivateUrl+"/"+id;
		try{
			responseEntity = http.getDataHTTPCall(http.GET, url, null, null, headers, HashMap.class);
		}catch(IdManagementException ex)/*MAKE sure to always forward the IdManagementException to avoid losing information.*/
		{
			throw ex;
		}
		catch(Exception e)
		{
			throw new IdManagementException("An error ocurred while getting information for the user",null,LOG,"An error ocurred trying to parse the uri: "+responseEntity.getHeaders().getFirst("Location")+" after HTTP communication with "+url+ " response: "+responseEntity.toString(),Level.ERROR,responseEntity.getStatusCode().value());
		}
		if(!responseEntity.getStatusCode().equals(HttpStatus.OK))
		{
			throw new IdManagementException("An error ocurred while getting information for the user",null,LOG,"An error ocurred trying to parse the uri: "+responseEntity.getHeaders().getFirst("Location")+" after HTTP communication with "+url+ " response: "+responseEntity.toString(),Level.ERROR,responseEntity.getStatusCode().value());
		}
		Map<String,Object> res =(Map<String, Object>) responseEntity.getBody();
		return res;
	}
}
