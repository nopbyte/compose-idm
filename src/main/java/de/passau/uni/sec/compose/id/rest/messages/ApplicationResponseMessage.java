package de.passau.uni.sec.compose.id.rest.messages;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.passau.uni.sec.compose.id.core.persistence.entities.Application;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApplicationResponseMessage extends AbstractCoreEnityResponse implements EntityResponseMessage
{

	private String name;

	public ApplicationResponseMessage(Application app)
	{
		 name = app.getName();
		 id = app.getId();
		 owner_id = app.getOwner().getId();
		 groups = app.getApprovedGroups(app.getGroups());
		 lastModified = app.getLastModified();
		 //Do this for every entity. And test!
		 attributeValues = app.getApprovedAttributeValues(app.getAttributes());
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwner_id() {
		return owner_id;
	}
	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}
	
	
	
}
