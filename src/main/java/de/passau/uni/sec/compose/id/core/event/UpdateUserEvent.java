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

public class UpdateUserEvent extends AbstractUpdateEvent implements Event
{
	private UserCreateMessage message;
	
	/**
	 * Latest modification as epoch
	 */

	
	private static Logger LOG = LoggerFactory.getLogger(UpdateUserEvent.class);
	

	public UserCreateMessage getMessage() {
		return message;
	}

	public void setMessage(UserCreateMessage message) {
		this.message = message;
	}

	
		public UpdateUserEvent(String id, UserCreateMessage messate, Collection<IPrincipal> principals, long lastKnownUpdate)
	{
		super.entityId = id;
		super.setLastModifiedKnown(lastKnownUpdate);
		this.message = messate;
		this.principals = principals;
		
	}
	
	
	@Override
	public String getLoggingDetails() {
	
		/*String updateData = "extra_attributes = [";
		List<ExtraAttributeMessage> as=  this.message.getExtraAttributes();
		for (ExtraAttributeMessage at: as)
			updateData += at.getName()+","+at.getValue();
		updateData+= "]";
		updateData+= ",memberships = [";
		
		List<MembershipResponseMessage> me = this.message.getMemberships();
		for(MembershipResponseMessage memb: me)
			updateData += "(group:"+memb.getGroup()+", role:"+memb.getRole()+")";
		updateData+= "]";
		
		//TODO update attributes defined for update
		return "Updating single user : "+updateData+ " principals: "+RestAuthentication.getBasicInfoPrincipals(principals);
		*/
		return "Updating user with id "+entityId+". New values:  "+message.getUsername();

	}
}
