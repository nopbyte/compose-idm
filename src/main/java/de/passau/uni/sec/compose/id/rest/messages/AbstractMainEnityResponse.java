package de.passau.uni.sec.compose.id.rest.messages;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AbstractMainEnityResponse {

	protected String id;
	
	protected Date lastModified;
	
	protected String owner_id;
	/**
	 * groups that the entity belongs to
	 */
	protected List<EntityGroupMembershipResponseMessage> groups;
	
	
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

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public List<EntityGroupMembershipResponseMessage> getGroups() {
		return groups;
	}

	public void setGroups(List<EntityGroupMembershipResponseMessage> groups) {
		this.groups = groups;
	}
	
	
	
}
