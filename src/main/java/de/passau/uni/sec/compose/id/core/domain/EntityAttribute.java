package de.passau.uni.sec.compose.id.core.domain;

/**
 * 
 * The class implementing this interface converts ServiceAttributes,ApplicationAttributes,... *Attributes into an instance of the class implementing the interface
 *
 */
public interface EntityAttribute
{
	
	public String getName();
	/**
	 * 
	 * @return Id of the Attribute in the database
	 */
	public String getId();
	/**
	 * 
	 * @return type field of the attribute in the database
	 */
	public String getType();
	/**
	 * 
	 * @return resolves the reference from the attribute table (looks into type table - if it IS a table- and brings the associated entity)
	 */
	
	public Object value();
	/**
	 * 
	 * @return the object that owns the attribute. This object will be a Service in the case of a ServiceAttribute, but it can also be a User... etc
	 */
	public Entity getEntity();
	/**
	 * 
	 * @return Authority for the attribute
	 */
	public String getAuthority();
	
	public boolean getApproved();
	//TODO add all shared getters (all columns) and setters ammong UserAttributes, ServiceAttributes,... *Attrubutes
	//TODO add setters too

	
	
}