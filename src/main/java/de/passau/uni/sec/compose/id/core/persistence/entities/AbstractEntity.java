package de.passau.uni.sec.compose.id.core.persistence.entities;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.passau.uni.sec.compose.id.rest.messages.EntityGroupMembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ExtraAttributeMessage;

@MappedSuperclass
public abstract class AbstractEntity implements IEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2373541806067471037L;

	@Id
    @Column(updatable = false, nullable = false, unique = true, length = 255)
    protected String id;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModified = new Date( (new Date().getTime()/1000)*1000 );

    public boolean equals(Object o) {
        return o instanceof AbstractEntity && ((AbstractEntity) o).getId().equals(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    /**
     * List all the memeberships as messages
     * @param memberships all the memberships that need to be converted
     * @return List of EntityGroupMembershipMessage corresponding to the parameter memberships.
     */
    public List<EntityGroupMembershipResponseMessage> getApprovedGroups(Collection<EntityGroupMembership> memberships)
    {
    	List<EntityGroupMembershipResponseMessage> ret = new LinkedList<>();
    	for(EntityGroupMembership memb: memberships)
    	{
    		if(memb.isApprovedByGroupOwner() && memb.isApprovedBySelfOwner())
    				ret.add(new EntityGroupMembershipResponseMessage( memb ) );
    	}
    	return ret;
    }

    public void UpdateLastModifiedToNow()
    {
    	 this.lastModified = new Date( (new Date().getTime()/1000)*1000 );
    }
}
