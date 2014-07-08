package de.passau.uni.sec.compose.id.rest.messages;

public class ExtraAttributeMessage
{
	private String name;
	private String type;
	private String value;
	private boolean approved;
	//TODO define how the mapping to a group needs to be done 
	
	public String getName() {
		return name;
	}
	public boolean isApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
