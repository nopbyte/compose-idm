package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;

public class GetUserSelfEvent extends AbstractGetEvent implements Event
{
	
	private static Logger LOG = LoggerFactory.getLogger(GetUserSelfEvent.class);
	
	public GetUserSelfEvent( Collection<IPrincipal> principals) throws IdManagementException
	{
		this.principals = principals;
	}



	@Override
	public String getLoggingDetails() {
	
		return "User querying data for himself. principals: "+RestAuthentication.getBasicInfoPrincipals(principals);

	}
	
	
}
