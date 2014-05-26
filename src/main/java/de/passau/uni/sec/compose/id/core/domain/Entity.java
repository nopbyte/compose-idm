package de.passau.uni.sec.compose.id.core.domain;
/**
 * 
 * This interface must be implemented by all entities (user, service, service object, applications...)
 *
 */
public interface Entity 
{
	public static enum entityType {USER,SERVICE,APPLICATION,SERVICE_OBJECT,GROUP,SERVICE_COMPOSITION,SOURCE_CODE};
	/**
	 * 
	 * @return owner if the entity. Can be null in the case of a user
	 */
	public Object getOwner();
	/**
	 * 
	 * @return Id in the database for the entity
	 */
	public String getId();
	/**
	 * 
	 * @return entity name taken from the enum of this class
	 */
	public String getEnityName();
}
