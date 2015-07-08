package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.*;

import de.passau.uni.sec.compose.id.rest.messages.AttributeValueResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.MembershipResponseMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "\"User\"")
public class User extends AbstractEntity implements CoreEntity{

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Collection<Group> groups = new LinkedList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Collection<Membership> memberships = new LinkedList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Collection<AttributeValue> attributeValues = new LinkedList<>();
    
    @Column(nullable = false)
    private int reputation = 0;

    @Column
    private String username;
    
    @Column
    private String random_auth_token;

    public Collection<Group> getGroups() {
        return groups;
    }

    public void setGroups(Collection<Group> groups) {
        this.groups = groups;
    }

    public Collection<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(Collection<Membership> memberships) {
        this.memberships = memberships;
    }


    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    
    public Collection<AttributeValue> getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(Collection<AttributeValue> attributeValues) {
		this.attributeValues = attributeValues;
	}

	public List<MembershipResponseMessage> getApprovedMemberships()
    {
    	List <MembershipResponseMessage> ret = new LinkedList<>();
    	for(Membership memb: memberships)
    	{
    		if(memb.isApprovedByGroupOwner() && memb.isApprovedByUser())
    		{
    			ret.add(new MembershipResponseMessage(memb));
    		}    		
    	}
    	return ret;
    }

	@Override
	public User getOwner() {
		return this;
	}
	
	public boolean isGroupAdmin(String group_id)
	{
		for(Membership principalMembership: memberships)
		{
			if(principalMembership.getGroup().getId().equals(group_id)
					&& principalMembership.getRole().getName().equals(Role.ADMIN)
					&& principalMembership.isApprovedByGroupOwner()
					&& principalMembership.isApprovedByUser()
			 )
			 {
				return true;
				
			 }
		}
		return false;		
	}

	public List<AttributeValueResponseMessage> getApprovedAttributeValues() {
		
		List<AttributeValueResponseMessage> ret = new LinkedList<>();
		for(AttributeValue v: attributeValues)
		{
			if(v.isApproved())
				ret.add(new AttributeValueResponseMessage(v));
		}
		return ret;
	}

	public String getRandom_auth_token() {
		return random_auth_token;
	}

	public void setRandom_auth_token(String random_auth_token) {
		this.random_auth_token = random_auth_token;
	}
	
    
}
