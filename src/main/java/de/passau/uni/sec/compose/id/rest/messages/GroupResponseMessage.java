package de.passau.uni.sec.compose.id.rest.messages;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.passau.uni.sec.compose.id.core.persistence.entities.Group;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GroupResponseMessage extends AbstractMainEnityResponse implements EntityResponseMessage
{

	private String name;


	public GroupResponseMessage()
	{
		
	}
	
	public GroupResponseMessage(Group g)
	{
		this.id = g.getId();
		this.name = g.getName();
		this.lastModified = g.getLastModified();
		this.owner_id = g.getOwner().getId();
	}
	
		public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}
