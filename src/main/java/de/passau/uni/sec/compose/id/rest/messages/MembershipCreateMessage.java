package de.passau.uni.sec.compose.id.rest.messages;

import javax.validation.constraints.NotNull;


public class MembershipCreateMessage 
{

	@NotNull
	private String group_id;
	
	@NotNull
	private String role;

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	
	
	
	

}
