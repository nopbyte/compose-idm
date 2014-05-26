package de.passau.uni.sec.compose.id.rest.messages;

import java.util.Date;

import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;

public class EntityGroupMembershipResponseMessage implements EntityResponseMessage
{
	private String id;
	
	private String entity_id;
	
	private String entity_type;
	
	private String group_id;
	
	private Date lastModified;

	public EntityGroupMembershipResponseMessage(EntityGroupMembership mem)
	{
		id = mem.getId();
		group_id = mem.getGroup().getId();
		entity_id = mem.getEnityId();
		entity_type= mem.getEntityType();
		lastModified = mem.getLastModified();		
	}
	
		public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEntity_id() {
		return entity_id;
	}

	public void setEntity_id(String entity_id) {
		this.entity_id = entity_id;
	}

	public String getEntity_type() {
		return entity_type;
	}

	public void setEntity_type(String entity_type) {
		this.entity_type = entity_type;
	}
	
}
