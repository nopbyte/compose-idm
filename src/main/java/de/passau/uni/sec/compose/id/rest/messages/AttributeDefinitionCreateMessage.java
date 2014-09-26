package de.passau.uni.sec.compose.id.rest.messages;

import javax.validation.constraints.NotNull;


public class AttributeDefinitionCreateMessage 
{

	
	@NotNull
	private String name;
	
	@NotNull
	private String type;

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
