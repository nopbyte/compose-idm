package de.passau.uni.sec.compose.id.core.domain;

public interface IPrincipal 
{
	/**
	 * 
	 * @return String representation of a principal. This information is used for logging
	 */
	public String getStringBasicInfo();
	/**
	 * 
	 * @param token credentials provided for authenticating the principal
	 */
	public void setTokenCredentials(String token);
	

}
