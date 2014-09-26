package de.passau.uni.sec.compose.id.core.service.policy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;


@Service
public class PolicyManager 
{
	private static Logger LOG = LoggerFactory.getLogger(PolicyManager.class);
	
	
	public List<Map<String, Object>> getPolicyForNewServiceObject(String ownerId, ServiceObject serviceObject)
	{
	
		List<Map<String,Object>> policy = new LinkedList<>();
		/*"policy" : [
	    { "flow" : { "target" : "userid/ownerId" }},
	    { "flow" : { "source" : "userid/ownerId" }}
		]*/

		Map<String, String> flow1 = new HashMap<String, String>();
		flow1.put("target","userid/"+ownerId);
		Map<String, String> flow2 = new HashMap<String, String>();
		flow2.put("source","userid/"+ownerId);
		Map<String,Object> level11 = new HashMap<String,Object>();
		Map<String,Object> level12 = new HashMap<String,Object>();
		level11.put("flow", flow1);
		level12.put("flow", flow2);
		policy.add(level11);
		policy.add(level12);
		//policies should be registered with the PIP
		return policy;
	}
	
	public List<Map<String, Object>> getPolicyExistingServiceObject(ServiceObject serviceObject)
	{
		List<Map<String, Object>> policy = new LinkedList<>();
		//TODO get policy for the service object here!
		return policy;
	}
}
