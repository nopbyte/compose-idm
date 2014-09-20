package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.ExtraAttributeMessage;
import de.passau.uni.sec.compose.id.rest.messages.MembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserUpdateMessage;

public class DeleteMembershipEvent extends AbstractUpdateEvent implements Event
{
	
	
	private static Logger LOG = LoggerFactory.getLogger(DeleteMembershipEvent.class);
	
	
		public DeleteMembershipEvent(String id,  Collection<IPrincipal> principals, long lastKnownUpdate)
	{
		super.entityId = id;
		super.setLastModifiedKnown(lastKnownUpdate);
		this.principals = principals;
		
	}
	
	
	@Override
	public String getLoggingDetails() {
	
			return "Deleting Membership with id "+entityId+" principals: "+RestAuthentication.getBasicInfoPrincipals(principals);

	}
}
