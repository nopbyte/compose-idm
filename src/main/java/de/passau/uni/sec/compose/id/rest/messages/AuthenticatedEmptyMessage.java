package de.passau.uni.sec.compose.id.rest.messages;

//com.sun.istack.internal.NotNull  ??
import javax.validation.constraints.NotNull;


public class AuthenticatedEmptyMessage 
{
	@NotNull
	private String Authorization;

	public String getAuthorization() {
		return Authorization;
	}

	public void setAuthorization(String authorization) {
		Authorization = authorization;
	}
	
	
	
}
