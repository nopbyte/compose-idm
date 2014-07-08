package de.passau.uni.sec.compose.id.rest.messages;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObjectAttributes;

public class ServiceObjectAttributeMessage{
	
	
    private List<ServiceObjectAttributes> serviceObjectAttributes = new LinkedList<>();

    private int reputation;
   
    private boolean collectProvenance;

    private boolean payment;
    
    private String apiToken;
    
    private String owner_id;
    
    private Map<String, Object> policy;

	public ServiceObjectAttributeMessage(ServiceObject so)
	{
		reputation = so.getReputation();
		collectProvenance = so.isCollectProvenance();
		payment = so.isPayment();
		apiToken = so.getApiToken();
		owner_id = so.getOwner().getId();
	}

	
	public List<ServiceObjectAttributes> getServiceObjectAttributes() {
		return serviceObjectAttributes;
	}

	public void setServiceObjectAttributes(
			List<ServiceObjectAttributes> serviceObjectAttributes) {
		this.serviceObjectAttributes = serviceObjectAttributes;
	}

	public int getReputation() {
		return reputation;
	}

	public void setReputation(int reputation) {
		this.reputation = reputation;
	}

	public boolean isCollectProvenance() {
		return collectProvenance;
	}

	public void setCollectProvenance(boolean collectProvenance) {
		this.collectProvenance = collectProvenance;
	}

	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}
	
	
}