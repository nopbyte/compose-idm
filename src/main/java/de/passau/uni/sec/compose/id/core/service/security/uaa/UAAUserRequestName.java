package de.passau.uni.sec.compose.id.core.service.security.uaa;

import java.io.Serializable;

public class UAAUserRequestName implements Serializable{
	private String formatted;
	private String familyName;
	private String givenName;
	
	public String getFormatted() {
		return formatted;
	}
	public void setFormatted(String formatted) {
		this.formatted = formatted;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	
}