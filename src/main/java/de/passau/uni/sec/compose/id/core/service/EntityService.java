package de.passau.uni.sec.compose.id.core.service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetUserEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateUserEvent;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;

public interface EntityService 
{
	public EntityResponseMessage createEntity(Event event) throws IdManagementException;

	public EntityResponseMessage getEntity(Event event) throws IdManagementException;
	
	public EntityResponseMessage updateEntity(DetailsIdEvent event) throws IdManagementException;
	
	public void deleteEntity(DetailsIdEvent event) throws IdManagementException;

}
