package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;

public class CreateServiceObjectEvent extends AbstractEvent implements Event
{	
	private ServiceObjectCreateMessage message;

	public CreateServiceObjectEvent(ServiceObjectCreateMessage message2,
			Collection<IPrincipal> principals) 
	{
		message = message2;
		this.principals = principals;
	}

	public ServiceObjectCreateMessage getMessage() {
		return message;
	}

	public void setMessage(ServiceObjectCreateMessage message) {
		this.message = message;
	}

	@Override
	public String getLoggingDetails() 
	{
		return "Creating a service object with id: "+message.getId()+(message.isRequires_token()?" with token":"without token")+" by principals :"+RestAuthentication.getBasicInfoPrincipals(principals);
	}
	
}
