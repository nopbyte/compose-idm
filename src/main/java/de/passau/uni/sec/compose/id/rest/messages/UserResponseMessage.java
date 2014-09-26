package de.passau.uni.sec.compose.id.rest.messages;

import java.util.Date;
import java.util.List;

import de.passau.uni.sec.compose.id.core.persistence.entities.User;

public class UserResponseMessage implements EntityResponseMessage
{
	private String id;
	
	private String username;
	
	private Date lastModified;
	/**
	 * @return This attribute should only display memberships that have been approved in the past
	 */
	private List<MembershipResponseMessage> approvedGroupMemberships;
	
	private List<AttributeValueResponseMessage> approvedAttributes;

	
	public UserResponseMessage(User u)
	{
		username = (u.getUsername());
		id = u.getId();
		approvedGroupMemberships =  u.getApprovedMemberships();
		approvedAttributes = u.getApprovedAttributeValues();
		lastModified = u.getLastModified();
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public List<MembershipResponseMessage> getApprovedMemberships() {
		return approvedGroupMemberships;
	}

	public void setApprovedMemberships(List<MembershipResponseMessage> approvedMemberships) {
		this.approvedGroupMemberships = approvedMemberships;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public List<AttributeValueResponseMessage> getApprovedAttributes() {
		return approvedAttributes;
	}

	public void setApprovedAttributes(
			List<AttributeValueResponseMessage> approvedAttributes) {
		this.approvedAttributes = approvedAttributes;
	}
	
	
	
}
