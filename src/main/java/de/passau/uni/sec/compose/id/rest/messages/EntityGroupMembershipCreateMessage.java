package de.passau.uni.sec.compose.id.rest.messages;

import javax.validation.constraints.NotNull;


public class EntityGroupMembershipCreateMessage 
{

	@NotNull
	private String group_id;
	

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	
}
