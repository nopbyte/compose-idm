package de.passau.uni.sec.compose.id.core.service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;

/**
 * This class forces the classes extending it to implement only the code required for handling the 
 * business logic, and the access control mechanisms
 * @author dp
 *
 */
public abstract class AbstractBasicEntityService 
{
	private Logger LOG = LoggerFactory.getLogger(AbstractBasicEntityService.class);
	
	public final EntityResponseMessage createEntity(Event event) throws IdManagementException
	{		
		try{
			  verifyAccessControlCreateEntity(event);
			  EntityResponseMessage res = postACCreateEntity(event);
			  getLogger().info(event.getLoggingDetails());
			  return res;
		}catch(IdManagementException e)
		{
			throw e;//Forward IdManagementException... nothing to do here.
		}
		catch(PersistenceException ex)
		{
			//TODO differentiate repeated element, against other exceptions. return IdManagementException when entity is duplicated
			throw new IdManagementException("An error ocurred while creating the entity",ex,LOG,"A persistenceException occurred while creating an entity: "+event.getLoggingDetails(),Level.ERROR,500);
		}
		catch(Exception ex)
		{
			throw new IdManagementException("An error ocurred while creating the entity",ex,LOG,"An unexpected exception occurred while creating an entity: "+event.getLoggingDetails(),Level.ERROR,500);
		}
		
	}

	public final EntityResponseMessage getEntity(Event event) throws IdManagementException
	{
		try{
				verifyAccessControlGetEntity(event);
				EntityResponseMessage res = postACGetEntity(event);
				getLogger().info(event.getLoggingDetails());
				return res;
		}catch(IdManagementException e)
		{
			throw e;//Forward IdManagementException... nothing to do here.
		}
		catch(EntityNotFoundException ex)
		{
			throw new IdManagementException("Entity not found",null,LOG,"Entity not found, event :"+event.getLoggingDetails(),Level.DEBUG,404);
		}
		catch(PersistenceException ex)
		{
			//TODO differentiate repeated element, against other exceptions. return IdManagementException when entity is duplicated
			throw new IdManagementException("An error ocurred while getting the entity",ex,LOG,"A persistenceException occurred while getting an entity: "+event.getLoggingDetails(),Level.ERROR,500);
		}
		catch(Exception ex)
		{
			throw new IdManagementException("An error ocurred while getting the entity",ex,LOG,"An unexpected exception occurred while getting an entity: "+event.getLoggingDetails(),Level.ERROR,500);
		}
		
	}
	
	public final EntityResponseMessage updateEntity(DetailsIdEvent event) throws IdManagementException
	{
		try{
			verifyAccessControlUpdateEntity(event);
			//To avoid concurrent modifications of data.
			IEntity previous = getEntityById(event.getEntityId());
			verifyUpdateTimestamps(event, previous);
			EntityResponseMessage res = postACUpdateEntity(event, previous);
			getLogger().info(event.getLoggingDetails());
			return res;
		}catch(IdManagementException e)
		{
			throw e;//Forward IdManagementException... nothing to do here.
		}
		catch(EntityNotFoundException ex)
		{
			throw new IdManagementException("Entity not found",null,LOG,"Entity not found, event :"+event.getLoggingDetails(),Level.DEBUG,404);
		}
		catch(PersistenceException ex)
		{
			//TODO differentiate repeated element, against other exceptions. return IdManagementException when entity is duplicated
			throw new IdManagementException("An error ocurred while updating the entity",ex,LOG,"A persistenceException occurred while updating  an entity: "+event.getLoggingDetails(),Level.ERROR,500);
		}
		catch(Exception ex)
		{
			throw new IdManagementException("An error ocurred while updating  the entity",ex,LOG,"An unexpected exception occurred while updating  an entity: "+event.getLoggingDetails(),Level.ERROR,500);
		}
	}
	
	public final void deleteEntity(DetailsIdEvent event) throws IdManagementException
	{		
		try{
			  IEntity entity = getEntityById(event.getEntityId());
			  verifyUpdateTimestamps(event, entity);
			  verifyAccessControlDeleteEntity(event);
			  try{
				  postACDeleteEntity(event);
			  }
			  catch(DataIntegrityViolationException de)
			  {
				  throw new IdManagementException("The entity being deleted has entities associated with it.",null,LOG,"Data integrity violation (it seems this entity still has references). logging details: "+event.getLoggingDetails(),Level.DEBUG,409);
			  }
			  getLogger().info(event.getLoggingDetails());
			  
		}catch(IdManagementException e)
		{
			throw e;//Forward IdManagementException... nothing to do here.
		}
		catch(EntityNotFoundException ex)
		{
			throw new IdManagementException("Entity not found",null,LOG,"Entity not found, event :"+event.getLoggingDetails(),Level.DEBUG,404);
		}
		catch(PersistenceException ex)
		{
			//TODO differentiate repeated element, against other exceptions. return IdManagementException when entity is duplicated
			throw new IdManagementException("An error ocurred while deleting the entity",ex,LOG,"A persistenceException occurred while creating an entity: "+event.getLoggingDetails(),Level.INFO,500);
		}
		catch(Exception ex)
		{
			throw new IdManagementException("An error ocurred while deleting the entity",ex,LOG,"An unexpected exception occurred while creating an entity: "+event.getLoggingDetails(),Level.ERROR,500);
		}
		
	}
	protected abstract IEntity getEntityById(String entityId);

	
	protected void verifyUpdateTimestamps(DetailsIdEvent event, IEntity entity) throws IdManagementException{
		if(!event.getLastModifiedKnown().equals(entity.getLastModified()))
			throw new IdManagementException("Conclicting update: Entity has been updated between read and write requests, try again including the current value for LastModified from the entity",
					null, LOG," Attempt to Update entity: "+event.getLoggingDetails()+" without providing the propper timestamp(conflicting change)",Level.DEBUG, 412);
	}
	protected abstract EntityResponseMessage postACCreateEntity(Event event) throws IdManagementException;
	
	protected abstract EntityResponseMessage postACGetEntity(Event event) throws IdManagementException;
	
	protected  abstract EntityResponseMessage postACUpdateEntity(DetailsIdEvent event, IEntity previous) throws IdManagementException;
	
	protected abstract void verifyAccessControlCreateEntity(Event event) throws IdManagementException;
	
	protected  abstract void verifyAccessControlUpdateEntity(DetailsIdEvent event) throws IdManagementException;
	
	protected abstract void verifyAccessControlGetEntity(Event event) throws IdManagementException;
	
	protected  abstract void postACDeleteEntity(DetailsIdEvent event) throws IdManagementException;

	protected abstract void verifyAccessControlDeleteEntity(DetailsIdEvent event) throws IdManagementException;

	protected abstract Logger getLogger();
}
