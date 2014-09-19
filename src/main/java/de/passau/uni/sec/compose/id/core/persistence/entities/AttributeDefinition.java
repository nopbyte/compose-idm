package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import de.passau.uni.sec.compose.id.rest.messages.MembershipResponseMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "\"AttributeDefinition\"")
public class AttributeDefinition extends AbstractEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4395801897630647400L;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "group_fk")
	private Group group;
	
	@NotNull
	private String name;
	
	@NotNull
	private String type;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
    
}
