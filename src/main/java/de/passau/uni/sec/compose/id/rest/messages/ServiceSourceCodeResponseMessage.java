package de.passau.uni.sec.compose.id.rest.messages;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceSourceCodeResponseMessage extends AbstractCoreEnityResponse implements EntityResponseMessage
{
	
	
	private String name;
	
	private String version;
	
	private int reputation;
	
	private boolean payment;
	
	
	public ServiceSourceCodeResponseMessage(ServiceSourceCode sc)
	{
		this.id = sc.getId();
		this.owner_id = sc.getDeveloper().getId();
		this.lastModified = sc.getLastModified();
		this.groups = sc.getApprovedGroups(sc.getGroups());
		
		this.name = sc.getName();
		this.version = sc.getVersion();
		this.reputation = sc.getReputation();
		this.payment = sc.isPayment();
		this.attributeValues = sc.getApprovedAttributeValues(sc.getAttributes());

	}

	public ServiceSourceCodeResponseMessage() 
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getReputation() {
		return reputation;
	}

	public void setReputation(int reputation) {
		this.reputation = reputation;
	}

	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}

	
}
