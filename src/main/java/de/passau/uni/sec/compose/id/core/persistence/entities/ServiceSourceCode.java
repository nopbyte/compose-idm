package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.*;

import java.util.Collection;
import java.util.LinkedList;

@Entity
public class ServiceSourceCode extends AbstractEntity implements CoreEntity{

    @Column
    private String name;

    @Column
    private String version;

    @ManyToOne
    @JoinColumn(name = "developer_fk")
    private User developer;

    @OneToMany(mappedBy = "serviceSourceCode", cascade = CascadeType.ALL)
    Collection<EntityGroupMembership> groups = new LinkedList<>();

    
    @Column
    private int reputation;

    @Column
    private boolean payment;

    @Column
    private boolean visible;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public User getDeveloper() {
        return developer;
    }

    public void setDeveloper(User developer) {
        this.developer = developer;
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

    public boolean isPayment() {
        return payment;
    }

    public void setPayment(boolean payment) {
        this.payment = payment;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

	@Override
	public User getOwner() {
		return developer;
	}
}
