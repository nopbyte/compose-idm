package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class EntityGroupMembership extends AbstractMultiInstanceRelationship {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7383106498525678464L;

	/**
     * Group for the entity
     */
    @ManyToOne
    @JoinColumn(name = "group_fk")
    private Group group;

    /**
     * Approval from group owner
     */
    @Column
    private boolean approvedByGroupOwner;

    /**
     * Approval from the user
     */
    @Column
    private boolean approvedBySelfOwner;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean isApprovedByGroupOwner() {
        return approvedByGroupOwner;
    }

    public void setApprovedByGroupOwner(boolean approvedByGroupOwner) {
        this.approvedByGroupOwner = approvedByGroupOwner;
    }
    
    public boolean isApprovedBySelfOwner() {
		return approvedBySelfOwner;
	}

	public void setApprovedBySelfOwner(boolean approvedBySelfOwner) {
		this.approvedBySelfOwner = approvedBySelfOwner;
	}

    
    
}
