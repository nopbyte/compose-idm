package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

@Entity
public class EntityGroupMembership extends AbstractEntity {

	public static String APPLICATION = "application";
	public static String SERVICEINSTANCE = "serviceinstance";
	public static String SERVICEOBJECT = "serviceobject";
	public static String SERVICECOMPOSITION = "servicecomposition";
	public static String SERVICESOURCECODE = "servicesourcecode";
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 8248637158514995041L;


	/**
     * Application membership.
     */
    @ManyToOne
    @JoinColumn(name = "application_fk")
    private Application application;

	/**
     * Service instance membership.
     */
    @ManyToOne
    @JoinColumn(name = "serviceinstance_fk")
    private ServiceInstance serviceInstance;
    
    
    /**
     * Service object membership.
     */
    @ManyToOne
    @JoinColumn(name = "serviceobject_fk")
    private ServiceObject serviceObject;
    
    
    /**
     * Service composition membership.
     */
    @ManyToOne
    @JoinColumn(name = "servicecomposition_fk")
    private ServiceComposition serviceComposition;
    
    
    /**
     * Service souce membership.
     */
    @ManyToOne
    @JoinColumn(name = "servicesourcecode_fk")
    private ServiceSourceCode serviceSourceCode;
    
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

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public ServiceObject getServiceObject() {
		return serviceObject;
	}

	public void setServiceObject(ServiceObject serviceObject) {
		this.serviceObject = serviceObject;
	}
	
	public ServiceComposition getServiceComposition() {
		return serviceComposition;
	}

	public void setServiceComposition(ServiceComposition serviceComposition) {
		this.serviceComposition = serviceComposition;
	}

	public ServiceSourceCode getServiceSourceCode() {
		return serviceSourceCode;
	}

	public void setServiceSourceCode(ServiceSourceCode serviceSourceCode) {
		this.serviceSourceCode = serviceSourceCode;
	}

	public String getEnityId()
	{
		if(application!=null)
			return application.getId();
		if(serviceInstance!=null)
			return serviceInstance.getId();
		if(serviceObject !=null)
			return serviceObject.getId();
		if(serviceComposition !=null)
			return serviceComposition.getId();
		if(serviceSourceCode != null)
			return serviceSourceCode.getId();
		return "";
	}
	public String getEntityType()
	{
		if(application!=null)
			return APPLICATION;
		if(serviceInstance!=null)
			return SERVICEINSTANCE;
		if(serviceObject !=null)
			return SERVICEOBJECT;
		if(serviceComposition !=null)
			return SERVICECOMPOSITION;
		if(serviceSourceCode != null)
			return SERVICESOURCECODE;
		return null;
	}
	public CoreEntity getEntity()
	{
		if(application!=null)
			return application;
		if(serviceInstance!=null)
			return serviceInstance;
		if(serviceObject !=null)
			return serviceObject;
		if(serviceComposition !=null)
			return serviceComposition;
		if(serviceSourceCode != null)
			return serviceSourceCode;
		return null;
	}
	
    
    
}
