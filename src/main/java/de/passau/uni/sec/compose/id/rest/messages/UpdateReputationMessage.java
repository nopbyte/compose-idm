package de.passau.uni.sec.compose.id.rest.messages;

import java.util.Date;

/**
 * Generic class used to represent the minimum information for entities.
 * @author dp
 *
 */
public class UpdateReputationMessage {

	protected String id;

	protected String entity_type;
	
	protected int reputation;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEntity_type()
	{
		return entity_type;
	}

	public void setEntity_type(String entity_type)
	{
		this.entity_type = entity_type;
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
