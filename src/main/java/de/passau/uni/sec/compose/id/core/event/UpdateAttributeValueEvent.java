package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.AttributeValueUpdateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ExtraAttributeMessage;
import de.passau.uni.sec.compose.id.rest.messages.MembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserUpdateMessage;

public class UpdateAttributeValueEvent extends AbstractUpdateEvent implements Event
{
	private AttributeValueUpdateMessage message;
	
	/**
	 * Latest modification as epoch
	 */

	
	private static Logger LOG = LoggerFactory.getLogger(UpdateAttributeValueEvent.class);
	

	public AttributeValueUpdateMessage getMessage() {
		return message;
	}

	public void setMessage(AttributeValueUpdateMessage message) {
		this.message = message;
	}

	
		public UpdateAttributeValueEvent(String id, AttributeValueUpdateMessage messate, Collection<IPrincipal> principals, long lastKnownUpdate)
	{
		super.entityId = id;
		super.setLastModifiedKnown(lastKnownUpdate);
		this.message = messate;
		this.principals = principals;
		
	}
	
	
	@Override
	public String getLoggingDetails() {
	
		return "Updating Attribute Value with Id: "+entityId+" with the following values: "+message.getValue()+" , principals"+RestAuthentication.getBasicInfoPrincipals(super.principals);
	
	}
}
