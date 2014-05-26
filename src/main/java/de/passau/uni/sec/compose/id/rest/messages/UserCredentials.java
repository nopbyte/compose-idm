package de.passau.uni.sec.compose.id.rest.messages;

import javax.validation.constraints.NotNull;

public class UserCredentials 
{
	/**
	 * username for the user
	 */
	@NotNull
	private String username;
	/**
	 * password for the user
	 */
	@NotNull
	private String password;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
