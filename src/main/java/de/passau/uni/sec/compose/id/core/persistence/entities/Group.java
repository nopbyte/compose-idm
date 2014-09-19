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

    
    @Column
    private String name;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
