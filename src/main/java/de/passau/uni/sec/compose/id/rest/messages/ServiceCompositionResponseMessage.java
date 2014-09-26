package de.passau.uni.sec.compose.id.rest.messages;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceComposition;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceCompositionResponseMessage extends AbstractCoreEnityResponse implements EntityResponseMessage
{
	
	
	
	public ServiceCompositionResponseMessage(ServiceComposition si)
	{
		this.id = si.getId();
		this.owner_id = si.getOwner().getId();
		this.lastModified = si.getLastModified();
		this.groups = si.getApprovedGroups(si.getGroups());
		attributeValues = si.getApprovedAttributeValues(si.getAttributes());
		
	}

	public ServiceCompositionResponseMessage() 
	{

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
