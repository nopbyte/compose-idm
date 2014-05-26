package de.passau.uni.sec.compose.id.rest.messages;

public class UserAuthenticatedMessage 
{
	private String accessToken;
	private String token_type;
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	
	
}
