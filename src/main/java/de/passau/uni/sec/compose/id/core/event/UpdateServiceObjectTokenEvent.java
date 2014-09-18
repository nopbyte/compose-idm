package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;
import java.util.Date;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectTokenUpdateMessage;

public class UpdateServiceObjectTokenEvent extends AbstractUpdateEvent implements DetailsIdEvent
{	

	
	private ServiceObjectTokenUpdateMessage message;

	
	public UpdateServiceObjectTokenEvent(String id, ServiceObjectTokenUpdateMessage message2, 
			Collection<IPrincipal> principals, long lastModified) 
	{
		super.entityId = id;
		super.setLastModifiedKnown(lastModified);
		super.setPrincipals(principals);
		message = message2;
		
		
	}

	public ServiceObjectTokenUpdateMessage getMessage() {
		return message;
	}

	public void setMessage(ServiceObjectTokenUpdateMessage message) {
		this.message = message;
	}

	@Override
	public String getLoggingDetails() 
	{
		return "Updating password for service object with id: "+entityId;
	}

	
	
	
}
