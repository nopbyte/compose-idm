package de.passau.uni.sec.compose.id.rest.messages;

import javax.validation.constraints.NotNull;


public class AttributeValueUpdateMessage 
{
	
	@NotNull
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
}
