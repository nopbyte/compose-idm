package de.passau.uni.sec.compose.id.core.service.policy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;


@Service
@PropertySource("classpath:pip.properties")
public class PolicyManager 
{
	private static Logger LOG = LoggerFactory.getLogger(PolicyManager.class);
	
	@Autowired
	private Environment env;
	
	private String pipUrl;
	
	private String pipGetNewPolicy;
	
	@PostConstruct
	public void postConstruct()
	{
		this.pipUrl = env.getProperty("pip.url");
		this.pipGetNewPolicy = env.getProperty("pip.get_new_id");
	}
	
	public List<Map<String, Object>> getPolicyForNewServiceObject(String ownerId, ServiceObject serviceObject) throws IdManagementException
	{
		
        String url = this.pipUrl+pipGetNewPolicy;
        Map<String, Object> map = new HashMap<>();
        map.put("entityType","SO");
        map.put("entityId",serviceObject.getId());
        map.put("private",true);
        map.put("ownerId",ownerId);
        HttpEntity<Map> getPolicy = new HttpEntity(
                map);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> responseEntity = null;
        try{
        	responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, getPolicy,
                        Object.class);
        	LinkedHashMap<String, Object> policyResponse = (LinkedHashMap<String, Object>) responseEntity
                    .getBody();
        	if(responseEntity.getStatusCode().equals(HttpStatus.OK))
        		return (List<Map<String, Object>>) policyResponse.get("policy");
        	else
        		throw new IdManagementException("Unexpected http response value when getting policy for the service object ",null,LOG,"Unexpected error code :"+responseEntity.getStatusCode().toString()+" for the Service Object policy when accessing the Pip in the url: "+url+" after HTTP communication with "+url+ " with owner id:"+ownerId+ " and service object id: "+serviceObject.getId(), Level.ERROR, (responseEntity==null?500:responseEntity.getStatusCode().value()));
        }catch(Exception ex)
        {
        	throw new IdManagementException("An error ocurred while getting policy for the service object",null,LOG,"An error ocurred trying to access the Pip in the url: "+url+" after HTTP communication with "+url+ " with owner id:"+ownerId+ " and service object id: "+serviceObject.getId(), Level.ERROR, (responseEntity==null?500:responseEntity.getStatusCode().value()));
        }
      		
	}
	
	public List<Map<String, Object>> getPolicyExistingServiceObject(ServiceObject serviceObject)
	{
		List<Map<String, Object>> policy = new LinkedList<>();
		//TODO get policy for the service object here!
		return policy;
	}
}
