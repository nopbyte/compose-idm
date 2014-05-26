package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class UserRole extends AbstractEntity {

    @OneToOne
    private User userId;

    @OneToOne
    private Role roleId;

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Role getRoleId() {
        return roleId;
    }

    public void setRoleId(Role roleId) {
        this.roleId = roleId;
    }
}
