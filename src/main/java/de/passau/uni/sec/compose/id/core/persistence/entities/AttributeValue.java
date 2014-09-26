package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "\"AttributeValue\"")
public class AttributeValue extends AbstractMultiInstanceRelationship implements IAttributeValue {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4395801897630647400L;
	
	/**
	 * Map to entity type. See AbstractMultiInstanceRelationship too.
	 */
	public static final String USER = "user";
	

	@ManyToOne
    @JoinColumn(name = "user_fk")
	private User user;
	
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "attributeDefinition_fk")
	private AttributeDefinition definition;
	
	@NotNull
	private String value;

	private boolean approved;
	
	private String approvedBy;
	
	public AttributeDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(AttributeDefinition definition) {
		this.definition = definition;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String getEnityId()
	{
		if(user != null)
			return user.getId();
		return super.getEnityId();
	}
	
	@Override
	public String getEntityType()
	{
		if(user != null)
			return USER;
		return super.getEntityType();
	}
	
	@Override
	public CoreEntity getEntity()
	{
		if(user != null)
			return user;
		return super.getEntity();
	}
   
}
