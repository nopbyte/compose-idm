package de.passau.uni.sec.compose.id.core.persistence.entities;

/**
 * This interface is used to represent the main entities managed by IDM (ServiceObjects, Services, Applications, Service Instances, Service Compositions)
 * @author dp
 *
 */
public interface CoreEntity 
{
	public User getOwner();
	
	public String getId();
}
