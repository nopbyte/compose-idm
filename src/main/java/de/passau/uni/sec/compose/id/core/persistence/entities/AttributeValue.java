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

	@NotNull
	@ManyToOne
	@JoinColumn(name = "attributeDefinition_fk")
	private AttributeDefinition definition;
	
	@NotNull
	private String value;

	@NotNull
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

	

	
		
	
	
	
    
}
