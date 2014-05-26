package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.*;

import java.util.Collection;
import java.util.LinkedList;

@Entity
public class Application extends AbstractEntity implements CoreEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 7285545921282723216L;

	@ManyToOne
    @JoinColumn(name = "owner_fk")
    private User owner;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    Collection<EntityGroupMembership> groups = new LinkedList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    private Collection<ApplicationAttributes> applicationAttributes = new LinkedList<>();

    @Column
    private String name;
    
    @Column
    private int reputation;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

        public Collection<ApplicationAttributes> getApplicationAttributes() {
        return applicationAttributes;
    }

    public void setApplicationAttributes(Collection<ApplicationAttributes> applicationAttributes) {
        this.applicationAttributes = applicationAttributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    
	
    
}
