package de.passau.uni.sec.compose.id.core.persistence.entities;


import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;

@Entity
@Table(name = "\"Group\"")
public class Group extends AbstractEntity implements CoreEntity{

    @ManyToOne
    @JoinColumn(name = "owner_fk")
    private User owner;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private Collection<GroupAttributes> groupAttributes = new LinkedList<>();

    @Column
    private String name;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Collection<GroupAttributes> getGroupAttributes() {
        return groupAttributes;
    }

    public void setGroupAttributes(Collection<GroupAttributes> groupAttributes) {
        this.groupAttributes = groupAttributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
