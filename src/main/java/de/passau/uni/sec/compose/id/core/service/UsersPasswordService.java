package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.UpdateUsersPasswordEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.core.service.security.Authorization;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.core.service.security.uaa.OpenIdUserData;


@Service
public class UsersPasswordService  
{

	private static Logger LOG = LoggerFactory.getLogger(UsersPasswordService.class);

	/**
	 * Should be passed to ComposeRepository to manage exceptions... never access it directly
	 */
	@Autowired
    UserRepository userRepository;
	
	@Autowired
	UsersAuthzAndAuthClient uaa;
	
	@Autowired
	RestAuthentication auth;
	
	
	public void changeUserPassword(UpdateUsersPasswordEvent event)
			throws IdManagementException {
		
		Collection<IPrincipal> principals = event.getPrincipals();
		ComposeUserPrincipal user = null;
		for(IPrincipal p: principals)
			if(p instanceof ComposeUserPrincipal)
			{
				user = (ComposeUserPrincipal) p;
			}
		if(user != null)
		{
			String token = user.getOauthToken();
			String id = user.getOpenId().getUser_id();
			uaa.changePassword(token, id,  event.getMessage().getOld_password(), event.getMessage().getNew_password() );
			LOG.info(event.getLoggingDetails());
		}
				
	}
	
	
}
