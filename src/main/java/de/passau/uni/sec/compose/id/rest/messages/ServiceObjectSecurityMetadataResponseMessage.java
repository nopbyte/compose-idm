package de.passau.uni.sec.compose.id.rest.messages;

import java.util.Map;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;

public class ServiceObjectSecurityMetadataResponseMessage 
{

	private ServiceObjectAttributeMessage attributes;
	
	private Map<String, Object> policy;
	
	public ServiceObjectSecurityMetadataResponseMessage(ServiceObject so,Map<String,Object> policy) 
	{
		//if next line is not commented!!!
		//
		// Access control is required to prevent to reply with the api_token to users who do not own the service object!
		//attributes = new ServiceObjectAttributeMessage(so);
		this.policy = policy;
		
	}

	public ServiceObjectAttributeMessage getAttributes() {
		return attributes;
	}

	public void setAttributes(ServiceObjectAttributeMessage attributes) {
		this.attributes = attributes;
	}

	public Map<String, Object> getPolicy() {
		return policy;
	}

	public void setPolicy(Map<String, Object> policy) {
		this.policy = policy;
	}
	
	

	
}
