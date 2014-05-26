package de.passau.uni.sec.compose.id.rest.messages;


import javax.validation.constraints.NotNull;


public class ServiceCompositionCreateMessage 
{
	
	@NotNull
	private String id;

	@NotNull
	private String Authorization;
	
	public String getAuthorization() {
		return Authorization;
	}

	public void setAuthorization(String authorization) {
		Authorization = authorization;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
		
}
