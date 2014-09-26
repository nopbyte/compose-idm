package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.AttributeValueCreateMessage;

public class CreateAttributeValueEvent extends AbstractEvent implements Event
{	
	private String entityId; 
	
	private String entityType;
	
	private AttributeValueCreateMessage message;
	

	public CreateAttributeValueEvent(String entityId,String type,AttributeValueCreateMessage message2,
			Collection<IPrincipal> principals) 
	{
		message = message2;
		this.principals = principals;
		this.entityId= entityId;
		this.entityType = type;
	}

	public AttributeValueCreateMessage getMessage() {
		return message;
	}

	public void setMessage(AttributeValueCreateMessage message) {
		this.message = message;
	}

		public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
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
		String ret = "Creating attribute value for entity of type:"+entityType+" with id: "+entityType+", principals"+RestAuthentication.getBasicInfoPrincipals(principals);
		return ret;
	}

	
}
