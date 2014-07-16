package de.passau.uni.sec.compose.id.core.service;

import java.util.List;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;

public abstract class AbstractBasicListEntityService extends AbstractBasicEntityService
{
	private Logger LOG = LoggerFactory.getLogger(AbstractBasicEntityService.class);
	
	public final Object listAllEntities(Event event) throws IdManagementException
	{		
		try{
			  verifyACListAllEntities(event);
			  Object res = postACListAllEntities(event);
			  getLogger().info(event.getLoggingDetails());
			  return res;
		}catch(IdManagementException e)
		{
			throw e;//Forward IdManagementException... nothing to do here.
		}
		catch(PersistenceException ex)
		{
			//TODO differentiate repeated element, against other exceptions. return IdManagementException when entity is duplicated
			throw new IdManagementException("An error ocurred while listing all the entities",ex,LOG,"A persistenceException occurred while listing all the entities: "+event.getLoggingDetails(),Level.ERROR,500);
		}
		catch(Exception ex)
		{
			throw new IdManagementException("An error ocurred while listing all the entities",ex,LOG,"An unexpected exception occurred while listing all the entities: "+event.getLoggingDetails(),Level.ERROR,500);
		}
		
	}

	public abstract Object postACListAllEntities(Event event) throws IdManagementException;
	
	public abstract void verifyACListAllEntities(Event event) throws IdManagementException; 
}
