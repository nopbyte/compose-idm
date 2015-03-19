package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateFromExternalAppMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;

public class CreateUserEvent extends AbstractEvent implements Event
{

	
	private UserCreateMessage userMessage;
	
	private boolean fromExternalApp = false;
	
	private static Logger LOG = LoggerFactory.getLogger(CreateUserEvent.class);
	
	public UserCreateMessage getUserMessage() 
	{
		return userMessage;
	}

	public void setUserMessage(UserCreateMessage userMessage) 
	{
		this.userMessage = userMessage;
	}
	
	
	public CreateUserEvent(UserCreateMessage user, Collection<IPrincipal> principals) throws IdManagementException
	{
		//This constructor checks that the credentials are valid for the creation of a specific object by checking the message object type
		this.userMessage = user;
		this.principals = principals;
		
		/*for(MembershipMessage m:user.getMemberships())
		{
			LOG.info(m.getGroup());
			LOG.info(m.getRole());
		}*/
		//TODO possible conversions with UAA?
		
	}

	public CreateUserEvent(UserCreateFromExternalAppMessage message,
			Collection<IPrincipal> principals) {
		
		this.userMessage = message;		
		this.principals = principals;
		this.fromExternalApp = true;
	}

	@Override
	public String getLoggingDetails() 
	{
		return "Creating user "+(fromExternalApp?"from external app":"")+" with username: "+this.userMessage.getUsername ()+ " principals: "+RestAuthentication.getBasicInfoPrincipals(principals);
	}


	
	
}
