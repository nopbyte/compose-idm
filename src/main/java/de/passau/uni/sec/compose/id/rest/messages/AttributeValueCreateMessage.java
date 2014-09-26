package de.passau.uni.sec.compose.id.rest.messages;

import javax.validation.constraints.NotNull;


public class AttributeValueCreateMessage 
{
	
	@NotNull
	private String attribute_definition_id;
	
	@NotNull
	private String value;

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
	
	
}
