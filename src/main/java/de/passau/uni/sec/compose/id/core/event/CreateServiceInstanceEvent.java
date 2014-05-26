package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceInstanceCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;

public class CreateServiceInstanceEvent extends AbstractEvent implements Event
{	
	private ServiceInstanceCreateMessage message;

	public CreateServiceInstanceEvent(ServiceInstanceCreateMessage message2,
			Collection<IPrincipal> principals) 
	{
		message = message2;
		this.principals = principals;
	}

	public ServiceInstanceCreateMessage getMessage() {
		return message;
	}

	public void setMessage(ServiceInstanceCreateMessage message) {
		this.message = message;
		
	}

	@Override
	public String getLoggingDetails() 
	{
		return "Creating a service instance with id: "+message.getId()+" and URI: "+message.getUri()+" and service source code with id:"+message.getSource_code_id()+" and collect provenance:"+message.isData_provenance_collection()+(message.isPayment()?" with payment collection":"without payment collection")+" by principals :"+RestAuthentication.getBasicInfoPrincipals(principals);
	}
	
}
