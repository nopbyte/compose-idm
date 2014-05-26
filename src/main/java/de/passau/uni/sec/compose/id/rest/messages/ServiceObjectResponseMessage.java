package de.passau.uni.sec.compose.id.rest.messages;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceObjectResponseMessage extends AbstractMainEnityResponse implements EntityResponseMessage
{
	private String api_token;
	
	private ServiceObjectSecurityMetadataResponseMessage security_metadata;
	
	
	public ServiceObjectResponseMessage(ServiceObject so, Map<String,Object> policy)
	{
		this.id = so.getId();
		this.api_token = so.getApiToken();
		this.owner_id = so.getOwner().getId();
		this.lastModified = so.getLastModified();
		this.groups = (so.getApprovedGroups(so.getGroups()));
		security_metadata = new ServiceObjectSecurityMetadataResponseMessage(so,policy);
	}

	public ServiceObjectResponseMessage() 
	{

	}

	public String getApi_token() {
		return api_token;
	}

	public void setApi_token(String api_token) {
		this.api_token = api_token;
	}

	public ServiceObjectSecurityMetadataResponseMessage getSecurity_metadata() {
		return security_metadata;
	}

	public void setSecurity_metadata(
			ServiceObjectSecurityMetadataResponseMessage security_metadata) {
		this.security_metadata = security_metadata;
	}
	
	

	
	
}
