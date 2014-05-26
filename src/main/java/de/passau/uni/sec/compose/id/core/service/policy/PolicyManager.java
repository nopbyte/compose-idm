package de.passau.uni.sec.compose.id.core.service.policy;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;

@Service
public class PolicyManager 
{
	private static Logger LOG = LoggerFactory.getLogger(PolicyManager.class);
	
	
	public Map<String,Object> getPolicyForNewServiceObject(String ownerId, ServiceObject serviceObject)
	{
		Map<String,Object> policy = new HashMap<String, Object>();
		//TODO get policy for the service object here!
		return policy;
	}
}
