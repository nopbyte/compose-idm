package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.EntityGroupMembershipCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.MembershipCreateMessage;

public class CreateEntityGroupMembershipEvent extends AbstractEvent implements Event
{	
	private String entityId; 
	
	private String entityType;
	
	private EntityGroupMembershipCreateMessage message;
	
	
	/**
	 * These 3 attributes are used to decide which approval is set to true when adding the memebership
	 */
	
	private boolean executedByEntityOwner = false;
	
	private boolean executedByGroupAdmin = false;
	
	private boolean executedByGroupOwner = false;

	public CreateEntityGroupMembershipEvent(String entityId,String type,EntityGroupMembershipCreateMessage message2,
			Collection<IPrincipal> principals) 
	{
		message = message2;
		this.principals = principals;
		this.entityId= entityId;
		this.entityType = type;
	}

	public EntityGroupMembershipCreateMessage getMessage() {
		return message;
	}

	public void setMessage(EntityGroupMembershipCreateMessage message) {
		this.message = message;
	}

		public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public boolean isExecutedByEntityOwner() {
		return executedByEntityOwner;
	}

	public void setExecutedByEntityOwner(boolean executedByEntityOwner) {
		this.executedByEntityOwner = executedByEntityOwner;
	}

	public boolean isExecutedByGroupAdmin() {
		return executedByGroupAdmin;
	}

	public void setExecutedByGroupAdmin(boolean executedByGroupAdmin) {
		this.executedByGroupAdmin = executedByGroupAdmin;
	}

	public boolean isExecutedByGroupOwner() {
		return executedByGroupOwner;
	}

	public void setExecutedByGroupOwner(boolean executedByGroupOwner) {
		this.executedByGroupOwner = executedByGroupOwner;
	}
	
	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	@Override
	public String getLoggingDetails() 
	{
		String ret ="Creating Entity Group Membership for entity id:"+entityId+" of type "+entityType+"for group "+message.getGroup_id()+", principals"+RestAuthentication.getBasicInfoPrincipals(principals);
		if(executedByGroupAdmin)
			ret += ". Action executed by group admin";
		if(executedByGroupOwner)
			ret += ". Action executed by group owner";
		if(executedByEntityOwner)
			ret += ". Action executed by the user itself";
		return ret;
	}
	
}
