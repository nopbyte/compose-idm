package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;

public class DeleteUserMembershipEvent extends AbstractUpdateEvent implements Event
{
	private static Logger LOG = LoggerFactory.getLogger(DeleteUserMembershipEvent.class);
	
	public DeleteUserMembershipEvent(String id,  Collection<IPrincipal> principals, long lastKnownUpdate)
	{
		super.entityId = id;
		super.setLastModifiedKnown(lastKnownUpdate);
		this.principals = principals;
		
	}
	
	
	@Override
	public String getLoggingDetails() {
	
			return "Deleting UserMembersehip with id "+entityId;

	}
}
