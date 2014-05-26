package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;

public class GetUserEvent extends AbstractGetEvent implements Event
{
	
	private static Logger LOG = LoggerFactory.getLogger(GetUserEvent.class);
	
	public GetUserEvent( String id, Collection<IPrincipal> principals) throws IdManagementException
	{
		//This constructor checks that the credentials are valid for the creation of a specific object by checking the message object type
		this.id= id;
		this.principals = principals;
		/*for(MembershipMessage m:user.getMemberships())
		{
			LOG.info(m.getGroup());
			LOG.info(m.getRole());
		}*/
		//TODO possible conversions with UAA?
		
	}



	@Override
	public String getLoggingDetails() {
	
		return "Getting single user with id : "+this.id+ " principals: "+RestAuthentication.getBasicInfoPrincipals(principals);

	}
	
	
}
