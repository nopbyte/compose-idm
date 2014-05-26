package de.passau.uni.sec.compose.id.rest.messages;

import java.util.List;


public class UserUpdateMessage 
{

	
	private List<MembershipResponseMessage> memberships; 
	
	private List<ExtraAttributeMessage> extraAttributes;
	
	public List<MembershipResponseMessage> getMemberships() {
		return memberships;
	}
	public void setMemberships(List<MembershipResponseMessage> memberships) {
		this.memberships = memberships;
	}
	public List<ExtraAttributeMessage> getExtraAttributes() {
		return extraAttributes;
	}
	public void setExtraAttributes(List<ExtraAttributeMessage> extraAttributes) {
		this.extraAttributes = extraAttributes;
	}
	
	
	

}
