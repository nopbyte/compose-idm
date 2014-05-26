package de.passau.uni.sec.compose.id.rest.messages;

import javax.validation.constraints.NotNull;


public class ApplicationCreateMessage 
{
	
	@NotNull
	private String Authorization;
	
	@NotNull
	private String id;
	
	@NotNull
	private String name;

	
	public String getAuthorization() {
		return Authorization;
	}

	public void setAuthorization(String authorization) {
		Authorization = authorization;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
