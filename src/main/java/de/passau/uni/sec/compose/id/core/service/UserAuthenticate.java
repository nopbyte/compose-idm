package de.passau.uni.sec.compose.id.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.service.security.TokenResponse;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.rest.messages.UserAuthenticatedMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

@Service
public class UserAuthenticate {

	@Autowired
	UsersAuthzAndAuthClient uaaClient;
	
	public UserAuthenticatedMessage authenticateUser(UserCredentials ev) throws IdManagementException
	{
		TokenResponse res = uaaClient.getImplicitTokenCredentials(null, ev.getUsername(), ev.getPassword());
		UserAuthenticatedMessage mes = new UserAuthenticatedMessage();
		mes.setAccessToken(res.getAccessToken());
		mes.setToken_type(res.getToken_type());
		return mes;
	}
}
