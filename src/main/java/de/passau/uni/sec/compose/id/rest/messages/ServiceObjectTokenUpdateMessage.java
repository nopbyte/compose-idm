package de.passau.uni.sec.compose.id.rest.messages;

//com.sun.istack.internal.NotNull  ??
import javax.validation.constraints.NotNull;


public class ServiceObjectTokenUpdateMessage 
{
	@NotNull
	private String Authorization;
	
	
	private String old_api_token;
	
	
	public String getAuthorization() {
		return Authorization;
	}

	public void setAuthorization(String authorization) {
		Authorization = authorization;
	}

	public String getOld_api_token() {
		return old_api_token;
	}

	public void setOld_api_token(String old_api_token) {
		this.old_api_token = old_api_token;
	}

	
	
}
