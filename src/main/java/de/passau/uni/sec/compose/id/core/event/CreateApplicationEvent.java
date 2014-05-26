package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.ApplicationCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;

public class CreateApplicationEvent extends AbstractEvent implements Event
{	
	private ApplicationCreateMessage message;

	public CreateApplicationEvent(ApplicationCreateMessage message2,
			Collection<IPrincipal> principals) 
	{
		message = message2;
		this.principals = principals;
	}

	public ApplicationCreateMessage getMessage() {
		return message;
	}

	public void setMessage(ApplicationCreateMessage message) {
		this.message = message;
		
	}

	@Override
	public String getLoggingDetails() 
	{
		return "Creating an application with name: "+message.getName()+", principals"+RestAuthentication.getBasicInfoPrincipals(principals);
	}
	
}
