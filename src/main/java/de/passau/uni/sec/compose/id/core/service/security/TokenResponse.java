package de.passau.uni.sec.compose.id.core.service.security;

public  class TokenResponse
{
	private String accessToken;
	private String token_scope;
	private String token_type;
	private String jti;
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getToken_scope() {
		return token_scope;
	}
	public void setToken_scope(String token_scope) {
		this.token_scope = token_scope;
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public String getJti() {
		return jti;
	}
	public void setJti(String jti) {
		this.jti = jti;
	}
	
	
	
}