package de.passau.uni.sec.compose.id.core.service.security;

import java.io.Serializable;
import java.util.Map;

import org.springframework.util.MultiValueMap;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.service.security.uaa.OpenIdUserData;



public interface UsersAuthzAndAuthClient 
{
	public String getUAAUrl();
	/**
	 * Get implicit token of behalf of a user
	 * @param username in the UAA
	 * @param password in the UAA
	 * @return response containing the relevant information for the token. This translates into a Basic HTTP call to UAA. 
	 * @throws IdManagementException 
	 */
	public TokenResponse getClientCredentialsToken(String username, String password) throws IdManagementException;
	/**
	 * 
	 * @param clientId
	 * @param username
	 * @param password
	 * @return
	 * @throws IdManagementException Whenever an error ocurrs during communication with the external API
	 */
	public TokenResponse getImplicitTokenCredentials(String clientId, String username, String password) throws IdManagementException;
	/**
	 * 
	 * @param token
	 * @return
	 * @throws IdManagementException 
	 */
	public OpenIdUserData getOpenIdData(String token) throws IdManagementException;
	
	/**
	 * 
	 * @param userData
	 * @return
	 * @throws IdManagementException
	 */
	public Map<String,Object> createUser(Serializable userData) throws IdManagementException;
	
	/**
	 * 
	 * @param userId identitifier for the user
	 * @throws IdManagementException in case the deletion was not successfull
	 */
	public void deleteUser(String userId) throws IdManagementException;
	/**
	 * 
	 * @param token token to authenticate the user
	 * @param id user id
	 * @param old_password previous password
	 * @param new_password new password
	 */
	public void changePassword(String token, String id,  String old_password,
			String new_password) throws IdManagementException;	
	
}
