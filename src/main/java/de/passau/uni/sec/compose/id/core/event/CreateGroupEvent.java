package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;

public class CreateGroupEvent extends AbstractEvent implements Event
{	
	private GroupCreateMessage message;

	public CreateGroupEvent(GroupCreateMessage message2,
			Collection<IPrincipal> principals) 
	{
		message = message2;
		this.principals = principals;
	}

	public GroupCreateMessage getMessage() {
		return message;
	}

	public void setMessage(GroupCreateMessage message) {
		this.message = message;
	}

	@Override
	public String getLoggingDetails() 
	{
		return "Creating a group with name: "+message.getName()+", principals"+RestAuthentication.getBasicInfoPrincipals(principals);
	}
	
}
