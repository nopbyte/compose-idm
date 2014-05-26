package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceCompositionCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;

public class CreateServiceCompositionEvent extends AbstractEvent implements Event
{	
	private ServiceCompositionCreateMessage message;

	public CreateServiceCompositionEvent(ServiceCompositionCreateMessage message2,
			Collection<IPrincipal> principals) 
	{
		message = message2;
		this.principals = principals;
	}

	public ServiceCompositionCreateMessage getMessage() {
		return message;
	}

	public void setMessage(ServiceCompositionCreateMessage message) {
		this.message = message;
	}

	@Override
	public String getLoggingDetails() 
	{
		return "Creating a service composition with id: "+message.getId()+" by principals :"+RestAuthentication.getBasicInfoPrincipals(principals);
	}
	
}
