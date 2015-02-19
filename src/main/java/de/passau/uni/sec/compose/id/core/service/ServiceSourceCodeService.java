package de.passau.uni.sec.compose.id.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.event.CreateServiceSourceCodeEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetServiceSourceCodeEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceSourceCodeRepository;
import de.passau.uni.sec.compose.id.core.service.reputation.ReputationManager;
import de.passau.uni.sec.compose.id.core.service.security.Authorization;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeResponseMessage;


@Service
public class ServiceSourceCodeService extends AbstractSecureEntityBasicEntityService implements EntityService 
{

	private static Logger LOG = LoggerFactory.getLogger(ServiceSourceCodeService.class);

	@Autowired
	ServiceSourceCodeRepository serviceSourceCodeRepository;
	
	@Autowired 
	RestAuthentication authentication;
	
	@Autowired
	Authorization authz;
	
	@Autowired
	ReputationManager rep;
	
	@Autowired
	UniqueValidation check;
	
	
	@Override
	protected void verifyAccessControlCreateEntity(Event event)
			throws IdManagementException {
		
		this.authz.authorizeIfAnyComponentWithAnyUser(event.getPrincipals());
		
	}
	
	@Override
	protected EntityResponseMessage postACCreateEntity(Event event)
			throws IdManagementException {
			
			//After this call we are sure there is a user, otherwise an exception would have been thrown
			ServiceSourceCodeCreateMessage message = ((CreateServiceSourceCodeEvent) event).getMessage();
			
			check.verifyUnique(message.getId());
					
			if(serviceSourceCodeRepository.exists(message.getId()))
				throw new IdManagementException("Service source code already exists",null,LOG,"Conflict while attempting to create a service source code: "+event.getLoggingDetails(),Level.ERROR,409);
			
			User u = authentication.getUserFromEvent(event);
			ServiceSourceCode sc = new ServiceSourceCode();
			sc.setId(message.getId());
			sc.setDeveloper(u);
			sc.setReputation(rep.getReputationValueForNewSourceCode(u.getId()));
			sc.setVersion(message.getVersion());
			sc.setName(message.getName());
			sc = serviceSourceCodeRepository.save(sc);
			EntityResponseMessage res = new ServiceSourceCodeResponseMessage(sc);
			check.insertUnique(message.getId(), check.SERVICE_SOURCE);
			return res;	
	}

	@Override
	protected EntityResponseMessage postACGetEntity(Event event)
			throws IdManagementException {
		
		GetServiceSourceCodeEvent get = ((GetServiceSourceCodeEvent ) event);
		ServiceSourceCode sc = serviceSourceCodeRepository.getOne(get.getId());
		if(sc == null)
			throw new IdManagementException("Entity not found",null,LOG,"Entity not found, event :"+get.getLoggingDetails(),Level.DEBUG,404);
		
		EntityResponseMessage res = new ServiceSourceCodeResponseMessage(sc);
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
	
		return serviceSourceCodeRepository.getOne(entityId);
	}
	
	
	@Override
	protected Logger getLogger() 
	{
		return LOG;
	}
	
	@Override
	protected void postACDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		ServiceSourceCode sc = serviceSourceCodeRepository.getOne(event.getEntityId());
		serviceSourceCodeRepository.delete(sc);
	}

	@Override
	protected void verifyAccessControlDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		ServiceSourceCode  sc = serviceSourceCodeRepository.getOne(event.getEntityId());
		authz.authorizeIfOwner(event.getPrincipals(), sc);
		
	}
}
