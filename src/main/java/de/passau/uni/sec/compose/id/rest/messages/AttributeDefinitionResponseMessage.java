package de.passau.uni.sec.compose.id.rest.messages;

import de.passau.uni.sec.compose.id.core.persistence.entities.AttributeDefinition;

public class AttributeDefinitionResponseMessage extends AbstractEnityResponse implements EntityResponseMessage
{
	private String id;
	
	private String group_id;
	
	private String name;
		
	private String type;

	public AttributeDefinitionResponseMessage(AttributeDefinition def)
	{
		this.id = def.getId();
		this.group_id = def.getGroup().getId();
		this.name = def.getName();
		this.type = def.getType();
		this.owner_id = def.getGroup().getOwner().getId();
		this.lastModified = def.getLastModified();
	}
	
	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

		public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
