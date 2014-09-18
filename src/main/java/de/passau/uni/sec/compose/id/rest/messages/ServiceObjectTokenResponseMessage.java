package de.passau.uni.sec.compose.id.rest.messages;


public class ServiceObjectTokenResponseMessage 
{

	private String api_token;

	
	public ServiceObjectTokenResponseMessage(String api_token) {
		super();
		this.api_token = api_token;
	}

	public String getApi_token() {
		return api_token;
	}

	public void setApi_token(String api_token) {
		this.api_token = api_token;
	}
	
	
	
	
}
