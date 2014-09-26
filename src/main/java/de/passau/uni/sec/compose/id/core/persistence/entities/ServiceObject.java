package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Entity
public class ServiceObject extends AbstractEntity implements CoreEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8739068447032818805L;

	@ManyToOne
    @JoinColumn(name = "owner_fk")
    private User owner;


    @OneToMany(mappedBy = "serviceObject", cascade = CascadeType.ALL)
    Collection<EntityGroupMembership> groups = new LinkedList<>();
    
    @OneToMany(mappedBy = "serviceObject", cascade = CascadeType.ALL)
    Collection<AttributeValue> attributes = new LinkedList<>();

    @Column
    private int reputation;

    @Column
    private boolean collectProvenance;

    @Column
    private boolean payment;

    @Column
    private String apiToken;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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

	
    
}
