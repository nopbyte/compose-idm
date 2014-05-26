package de.passau.uni.sec.compose.id.core.domain;

import de.passau.uni.sec.compose.id.core.service.security.uaa.OpenIdUserData;

public class ComposeUserPrincipal implements IPrincipal
{

	private String OauthToken;
	
	private OpenIdUserData openId;

	public String getOauthToken() {
		return OauthToken;
	}

	public void setOauthToken(String oauthToken) {
		OauthToken = oauthToken;
	}

	public OpenIdUserData getOpenId() {
		return openId;
	}

	public void setOpenId(OpenIdUserData openId) {
		this.openId = openId;
	}

	@Override
	public String getStringBasicInfo() 
	{
		return "UAA-Compose user: id: "+openId.getUser_id()+", and username: "+openId.getUser_name()+" and email: "+openId.getEmail();
	}

	@Override
	public void setTokenCredentials(String token) {
			OauthToken = token;		
	}
	
	

}
