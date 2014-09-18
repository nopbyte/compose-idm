package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;
import java.util.Date;

import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectTokenUpdateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserPasswordUpdateMessage;

public class UpdateUsersPasswordEvent extends AbstractEvent implements Event
{	

	
	private UserPasswordUpdateMessage message;

	
	public UpdateUsersPasswordEvent( UserPasswordUpdateMessage message2, 
			Collection<IPrincipal> principals) 
	{
		super.setPrincipals(principals);
		message = message2;		
	}


	public UserPasswordUpdateMessage getMessage() {
		return message;
	}

	public void setMessage(UserPasswordUpdateMessage message) {
		this.message = message;
	}

	@Override
	public String getLoggingDetails() 
	{
		return "Updating password for user, principals: "+RestAuthentication.getBasicInfoPrincipals(principals);
	}

	
	
	
}
