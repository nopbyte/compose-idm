package de.passau.uni.sec.compose.id.rest.messages;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.passau.uni.sec.compose.id.core.persistence.entities.User;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserResponseMessage implements EntityResponseMessage
{
	private String id;
	
	private String username;
	
	private Date lastModified;
	
	private int reputation;
	/**
	 * @return This attribute should only display memberships that have been approved in the past
	 */
	private List<MembershipResponseMessage> approvedGroupMemberships;
	
	private List<AttributeValueResponseMessage> approvedAttributes;

	private String random_auth_token;
	
	public UserResponseMessage(User u)
	{
		username = (u.getUsername());
		id = u.getId();
		approvedGroupMemberships =  u.getApprovedMemberships();
		approvedAttributes = u.getApprovedAttributeValues();
		lastModified = u.getLastModified();
		reputation = u.getReputation();
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

	public String getRandom_auth_token() {
		return random_auth_token;
	}

	public void setRandom_auth_token(String random_auth_token) {
		this.random_auth_token = random_auth_token;
	}

	public int getReputation()
	{
		return reputation;
	}

	public void setReputation(int reputation)
	{
		this.reputation = reputation;
	}
	
	
	
}
