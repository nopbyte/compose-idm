package de.passau.uni.sec.compose.id.rest.messages;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.passau.uni.sec.compose.id.core.persistence.entities.AttributeValue;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AttributeValueResponseMessage extends AbstractEnityResponse implements EntityResponseMessage
{

	private String entity_id;
	
	private String entity_type;
	
	private String attribute_definition_id;
	
	private String value ;
	
	private String attribute_definition_name;
	
	private boolean approved = false;

	private String group_id;
	
	public String getAttribute_definition_id() {
		return attribute_definition_id;
	}

	public void setAttribute_definition_id(String attribute_definition_id) {
		this.attribute_definition_id = attribute_definition_id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public AttributeValueResponseMessage(AttributeValue value)
	{
		 this.id = value.getId();
		 this.value = value.getValue();
		 this.entity_id = value.getEnityId();
		 this.entity_type = value.getEntityType();
		 this.lastModified = value.getLastModified();
		 this.approved = value.isApproved();
		 this.attribute_definition_id = value.getDefinition().getId();
		 this.group_id = value.getDefinition().getGroup().getId();
		 this.attribute_definition_name = value.getDefinition().getName();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwner_id() {
		return owner_id;
	}
	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public String getEntity_id() {
		return entity_id;
	}

	public void setEntity_id(String entity_id) {
		this.entity_id = entity_id;
	}

	public String getEntity_type() {
		return entity_type;
	}

	public void setEntity_type(String entity_type) {
		this.entity_type = entity_type;
	}

	public String getGroup_id()
	{
		return group_id;
	}

	public void setGroup_id(String group_id)
	{
		this.group_id = group_id;
	}

	public String getAttribute_definition_name()
	{
		return attribute_definition_name;
	}

	public void setAttribute_definition_name(String attribute_definition_name)
	{
		this.attribute_definition_name = attribute_definition_name;
	}
	
	
	
}
