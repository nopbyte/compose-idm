package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.rest.messages.AttributeDefinitionCreateMessage;

public class CreateAttributeDefinitionEvent extends AbstractEvent implements Event
{	
	
	private AttributeDefinitionCreateMessage message;
	
	private String group_id;
	
	public CreateAttributeDefinitionEvent(AttributeDefinitionCreateMessage message2, String gid,
			Collection<IPrincipal> principals) 
	{
		message = message2;
		this.group_id = gid;
		this.principals = principals;
	}

	public AttributeDefinitionCreateMessage getMessage() {
		return message;
	}

	public void setMessage(AttributeDefinitionCreateMessage message) {
		this.message = message;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	@Override
	public String getLoggingDetails() 
	{
		String ret = "Creating attribute definition for group with id:"+getGroup_id()+" with type: "+message.getType()+" and name: "+message.getName();
		return ret;
	}

	
}
