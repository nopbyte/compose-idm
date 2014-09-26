package de.passau.uni.sec.compose.id.rest.messages;

import java.util.Date;

/**
 * Generic class used to represent the minimum information for entities.
 * @author dp
 *
 */
public class AbstractEnityResponse {

	protected String id;
	
	protected Date lastModified;
	
	protected String owner_id;
	
	
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

	
}
