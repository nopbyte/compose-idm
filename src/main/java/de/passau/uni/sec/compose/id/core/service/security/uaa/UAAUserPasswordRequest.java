package de.passau.uni.sec.compose.id.core.service.security.uaa;

import java.io.Serializable;


public class UAAUserPasswordRequest implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1202476326108260948L;
	private String oldPassword;
	private String password;

	
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
