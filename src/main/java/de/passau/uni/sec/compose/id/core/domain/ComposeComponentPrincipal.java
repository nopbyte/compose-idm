package de.passau.uni.sec.compose.id.core.domain;

public class ComposeComponentPrincipal implements IPrincipal
{
	private String usernameAndPasswordHTTP;
	private String composeComponentName;

	@Override
	public String getStringBasicInfo() {
		
		return "ComposeComponent:"+composeComponentName;
	}

	@Override
	public void setTokenCredentials(String token) {
		usernameAndPasswordHTTP = token;
		
		
	}

	
	public String getComposeComponentName() {
		return composeComponentName;
	}

	public void setComposeComponentName(String composeComponentName) {
		this.composeComponentName = composeComponentName;
	}
	

}
