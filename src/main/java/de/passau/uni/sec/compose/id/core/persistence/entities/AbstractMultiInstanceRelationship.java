package de.passau.uni.sec.compose.id.core.persistence.entities;


import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractMultiInstanceRelationship extends AbstractEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3030781148890334296L;
	
	public static String APPLICATION = "application";
	public static String SERVICEINSTANCE = "serviceinstance";
	public static String SERVICEOBJECT = "serviceobject";
	public static String SERVICECOMPOSITION = "servicecomposition";
	public static String SERVICESOURCECODE = "servicesourcecode";
	
    	/**
     * Application membership.
     */
    @ManyToOne
    @JoinColumn(name = "application_fk")
    protected Application application;

	/**
     * Service instance membership.
     */
    @ManyToOne
    @JoinColumn(name = "serviceinstance_fk")
    protected ServiceInstance serviceInstance;
    
    
    /**
     * Service object membership.
     */
    @ManyToOne
    @JoinColumn(name = "serviceobject_fk")
    protected ServiceObject serviceObject;
    
    
    /**
     * Service composition membership.
     */
    @ManyToOne
    @JoinColumn(name = "servicecomposition_fk")
    protected ServiceComposition serviceComposition;
    
    
    /**
     * Service souce membership.
     */
    @ManyToOne
    @JoinColumn(name = "servicesourcecode_fk")
    protected ServiceSourceCode serviceSourceCode;
    
    
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
