package de.passau.uni.sec.compose.id.rest.messages;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceInstanceResponseMessage extends AbstractCoreEnityResponse implements EntityResponseMessage
{
	
	
	private String URI;
	
	private String source_code_id;
	
	private int reputation;
	
	private boolean data_provenance_collection;
	
	private boolean payment;
	
	
	public ServiceInstanceResponseMessage(ServiceInstance si)
	{
		this.id = si.getId();
		this.owner_id = si.getOwner().getId();
		this.lastModified = si.getLastModified();
		this.groups = si.getApprovedGroups(si.getGroups());
		
		URI = si.getURI();
		source_code_id = si.getServiceSourceCode().getId();
		reputation = si.getReputation();
		data_provenance_collection = si.isCollectProvenance();
		payment = si.isPayment();
		attributeValues = si.getApprovedAttributeValues(si.getAttributes());
	}

	public ServiceInstanceResponseMessage() 
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

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

	public String getSource_code_id() {
		return source_code_id;
	}

	public void setSource_code_id(String source_code_id) {
		this.source_code_id = source_code_id;
	}

	public int getReputation() {
		return reputation;
	}

	public void setReputation(int reputation) {
		this.reputation = reputation;
	}

	public boolean isData_provenance_collection() {
		return data_provenance_collection;
	}

	public void setData_provenance_collection(boolean data_provenance_collection) {
		this.data_provenance_collection = data_provenance_collection;
	}

	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}
	
	
	
}
