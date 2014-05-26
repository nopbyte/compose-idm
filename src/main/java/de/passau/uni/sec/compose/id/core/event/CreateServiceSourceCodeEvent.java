package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;

public class CreateServiceSourceCodeEvent extends AbstractEvent implements Event
{	
	private ServiceSourceCodeCreateMessage message;

	public CreateServiceSourceCodeEvent(ServiceSourceCodeCreateMessage message2,
			Collection<IPrincipal> principals) 
	{
		message = message2;
		this.principals = principals;
	}

	public ServiceSourceCodeCreateMessage getMessage() {
		return message;
	}

	public void setMessage(ServiceSourceCodeCreateMessage message) {
		this.message = message;
	}

	@Override
	public String getLoggingDetails() 
	{
		return "Creating a service source code with id: "+message.getId()+" and version: "+message.getVersion()+" by principals :"+RestAuthentication.getBasicInfoPrincipals(principals);
	}
	
}
