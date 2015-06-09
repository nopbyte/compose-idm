package de.passau.uni.sec.compose.id.core.persistence.entities;

import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ServiceComposition extends AbstractEntity implements CoreEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 7548296346930503413L;

	@ManyToOne
    @JoinColumn(name = "owner_fk")
    private User owner;

    @OneToMany(mappedBy = "serviceComposition", cascade = CascadeType.ALL)
    Collection<EntityGroupMembership> groups = new LinkedList<>();
    
    @OneToMany(mappedBy = "serviceComposition", cascade = CascadeType.ALL)
    Collection<AttributeValue> attributes= new LinkedList<>();

    @Column
    private String authenticationCode=null;
    
    
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

	public Collection<EntityGroupMembership> getGroups() {
		return groups;
	}

	public void setGroups(Collection<EntityGroupMembership> groups) {
		this.groups = groups;
	}

	public Collection<AttributeValue> getAttributes() {
		return attributes;
	}

	public void setAttributes(Collection<AttributeValue> attributes) {
		this.attributes = attributes;
	}

	public String getAuthenticationCode()
	{
		return authenticationCode;
	}

	public void setAuthenticationCode(String authenticationCode)
	{
		this.authenticationCode = authenticationCode;
	}
	
	
    
}
