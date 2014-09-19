package de.passau.uni.sec.compose.id.core.persistence.entities;

import java.io.Serializable;

public interface IAttributeValue extends Serializable {
    
	public String getId();
	
	public AttributeDefinition getDefinition();
	
	public String getValue();
	
	public boolean isApproved();
	
	public String getApprovedBy();
	
	

}
