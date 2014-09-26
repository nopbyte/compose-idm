package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

public class DeleteAttributeDefinitionEvent extends AbstractUpdateEvent implements Event
{
	
	
	private static Logger LOG = LoggerFactory.getLogger(DeleteAttributeDefinitionEvent.class);
	
	
		public DeleteAttributeDefinitionEvent(String id,  Collection<IPrincipal> principals, long lastKnownUpdate)
	{
		super.entityId = id;
		super.setLastModifiedKnown(lastKnownUpdate);
		this.principals = principals;
		
	}
	
	
	@Override
	public String getLoggingDetails() {
	
			return "Deleting attribute definition with id: "+entityId + " principals: "+RestAuthentication.getBasicInfoPrincipals(principals);

	}
}
