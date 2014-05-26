package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;

public class ApproveEntityGroupMembershipEvent extends AbstractUpdateEvent implements DetailsIdEvent
{
	
	private static Logger LOG = LoggerFactory.getLogger(ApproveEntityGroupMembershipEvent.class);
	
	public ApproveEntityGroupMembershipEvent( String id, Collection<IPrincipal> principals, String lastMod) throws IdManagementException
	{
		super.entityId = id;
		this.principals = principals;
		super.setLastModifiedKnown(lastMod);
		
	}
	@Override
	public String getLoggingDetails() {
	
		return "Approving a Entity Group Membership with id :"+this.entityId+ " principals: "+RestAuthentication.getBasicInfoPrincipals(principals);

	}
	
	
}
