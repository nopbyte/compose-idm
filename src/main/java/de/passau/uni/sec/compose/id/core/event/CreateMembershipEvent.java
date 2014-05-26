package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.MembershipCreateMessage;

public class CreateMembershipEvent extends AbstractEvent implements Event
{	
	private String userId; 
	
	private MembershipCreateMessage message;
	
	/**
	 * These 3 attributes are used to decide which approval is set to true when adding the memebership
	 */
	
	private boolean executedByUser = false;
	
	private boolean executedByGroupAdmin = false;
	
	private boolean executedByGroupOwner = false;

	public CreateMembershipEvent(String uid,MembershipCreateMessage message2,
			Collection<IPrincipal> principals) 
	{
		message = message2;
		this.principals = principals;
		userId= uid;
	}

	public MembershipCreateMessage getMessage() {
		return message;
	}

	public void setMessage(MembershipCreateMessage message) {
		this.message = message;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isExecutedByUser() {
		return executedByUser;
	}

	public void setExecutedByUser(boolean executedByUser) {
		this.executedByUser = executedByUser;
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

	@Override
	public String getLoggingDetails() 
	{
		String ret ="Creating membership for user with id:"+userId+" for group "+message.getGroup_id()+" and with role: "+message.getRole()+", principals"+RestAuthentication.getBasicInfoPrincipals(principals);
		if(executedByGroupAdmin)
			ret += ". Action executed by group admin";
		if(executedByGroupOwner)
			ret += ". Action executed by group owner";
		if(executedByUser)
			ret += ". Action executed by the user itself";
		return ret;
	}
	
}
