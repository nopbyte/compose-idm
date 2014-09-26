package de.passau.uni.sec.compose.id.rest.messages;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceObjectResponseMessage extends AbstractCoreEnityResponse implements EntityResponseMessage
{
	private String api_token;
	
	//New stuff
	private List<Map<String, Object>> policy;
	 
	private int reputation;
	   
	private boolean data_provenance_collection;
	
	private boolean payment;
	    
	    
	 
	//end new stuff
	public ServiceObjectResponseMessage(ServiceObject so, List<Map<String, Object>> policy2)
	{
		this.id = so.getId();
		this.api_token = so.getApiToken();
		this.owner_id = so.getOwner().getId();
		this.lastModified = so.getLastModified();
		this.groups = (so.getApprovedGroups(so.getGroups()));
		this.attributeValues = so.getApprovedAttributeValues(so.getAttributes());
		
		//new stuff
		this.policy = policy2;
		this.reputation = so.getReputation();
		this.data_provenance_collection = so.isCollectProvenance();
		this.payment = so.isPayment();
		
	}

	public ServiceObjectResponseMessage() 
	{

	}

	public String getApi_token() {
		return api_token;
	}

	public void setApi_token(String api_token) {
		this.api_token = api_token;
	}

	public List<Map<String, Object>> getPolicy() {
		return policy;
	}

	public void setPolicy(List<Map<String, Object>> policy) {
		this.policy = policy;
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
