package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Membership extends AbstractEntity {

    /**
     * User in the membership.
     * This entity should be retrieved automatically when the user is fetched from the database.
     */
    @ManyToOne
    @JoinColumn(name = "user_fk")
    private User user;

    /**
     * Role for the entity
     */
    @ManyToOne
    @JoinColumn(name = "role_fk")
    private Role role;

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
    private boolean approvedByUser;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

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

    public boolean isApprovedByUser() {
        return approvedByUser;
    }

    public void setApprovedByUser(boolean approvedByUser) {
        this.approvedByUser = approvedByUser;
    }
}
