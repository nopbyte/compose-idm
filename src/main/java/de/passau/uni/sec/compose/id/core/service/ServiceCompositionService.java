package de.passau.uni.sec.compose.id.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.event.CreateServiceCompositionEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetServiceCompositionEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.Global;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceComposition;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceCompositionRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UniqueRepository;
import de.passau.uni.sec.compose.id.core.service.reputation.ReputationManager;
import de.passau.uni.sec.compose.id.core.service.security.Authorization;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceCompositionCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceCompositionResponseMessage;


@Service
public class ServiceCompositionService extends AbstractSecureEntityBasicEntityService implements EntityService 
{

	private static Logger LOG = LoggerFactory.getLogger(ServiceCompositionService.class);

	@Autowired
	ServiceCompositionRepository serviceCompositionRepository;
	
	@Autowired 
	RestAuthentication authentication;
	
	@Autowired
	Authorization authz;
	
	@Autowired
	ReputationManager rep;
	
	@Autowired
	UniqueValidation check;
	
	@Autowired
	UniqueRepository uniqueRepository;
	
	
	@Override
	protected void verifyAccessControlCreateEntity(Event event)
			throws IdManagementException {
		
		this.authz.authorizeIfAnyComponentWithAnyUser(event.getPrincipals());
		
	}
	
	@Override
	protected EntityResponseMessage postACCreateEntity(Event event)
			throws IdManagementException {
			
			//After this call we are sure there is a user, otherwise an exception would have been thrown
			ServiceCompositionCreateMessage message = ((CreateServiceCompositionEvent) event).getMessage();
			
			check.verifyUnique(message.getId());
			
			if(serviceCompositionRepository.exists(message.getId()))
				throw new IdManagementException("Service composition already exists",null,LOG,"Conflict while attempting to create a service composition: "+event.getLoggingDetails(),Level.ERROR,409);
			
			User u = authentication.getUserFromEvent(event);
			ServiceComposition sc = new ServiceComposition();
			sc.setId(message.getId());
			sc.setOwner(u);
			sc = serviceCompositionRepository.save(sc);
			check.insertUnique(message.getId(),check.SERVICE_COMPOSITION);
			EntityResponseMessage res = new ServiceCompositionResponseMessage(sc);
			return res;	
	}

	@Override
	protected EntityResponseMessage postACGetEntity(Event event)
			throws IdManagementException {
		
		GetServiceCompositionEvent get = ((GetServiceCompositionEvent ) event);
		ServiceComposition sc = serviceCompositionRepository.getOne(get.getId());
		if(sc == null)
			throw new IdManagementException("Entity not found",null,LOG,"Entity not found, event :"+get.getLoggingDetails(),Level.DEBUG,404);
		
		EntityResponseMessage res = new ServiceCompositionResponseMessage(sc);
		return res;
	}

	@Override
	protected EntityResponseMessage postACUpdateEntity(DetailsIdEvent event, IEntity previous)
			throws IdManagementException {
		
		// 403 Forbidden, 304 Not modified, or 409 not modified (conflict), 
		//TODO verify ownership, or propper permissions
		
		return null;
	}

	@Override
	protected IEntity getEntityById(String entityId) {
	
		return serviceCompositionRepository.getOne(entityId);
	}
	
	@Override
	protected Logger getLogger() 
	{
		return LOG;
	}

	@Override
	protected void postACDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		ServiceComposition sc = serviceCompositionRepository.getOne(event.getEntityId());
		serviceCompositionRepository.delete(sc);
		Global entity = uniqueRepository.findOne(event.getEntityId());
		uniqueRepository.delete(entity);
	}

	@Override
	protected void verifyAccessControlDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		ServiceComposition sc = serviceCompositionRepository.getOne(event.getEntityId());
		authz.authorizeIfOwner(event.getPrincipals(), sc);
		
	}

	
	
}
