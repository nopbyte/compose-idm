package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.*;

import java.util.Collection;
import java.util.LinkedList;

@Entity
public class ServiceInstance extends AbstractEntity implements CoreEntity{

    @Column
    private String URI;

    @ManyToOne
    @JoinColumn(name = "owner_fk")
    private User owner;

    @OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL)
    Collection<EntityGroupMembership> groups = new LinkedList<>();

    @OneToOne
    private ServiceSourceCode serviceSourceCode;

    @Column
    private int reputation;

    @Column
    private boolean collectProvenance;

    @Column
    private boolean payment;

    public String getURI() {
        return URI;
    }

    public void setURI(String uRI) {
        URI = uRI;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public ServiceSourceCode getServiceSourceCode() {
        return serviceSourceCode;
    }

    public void setServiceSourceCode(ServiceSourceCode serviceSourceCode) {
        this.serviceSourceCode = serviceSourceCode;
    }

    public Collection<EntityGroupMembership> getGroups() {
		return groups;
	}

	public void setGroups(Collection<EntityGroupMembership> groups) {
		this.groups = groups;
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
}
