package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeComponentPrincipal;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.CreateApplicationEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetApplicationEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.Global;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.ApplicationRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UniqueRepository;
import de.passau.uni.sec.compose.id.core.service.reputation.ReputationManager;
import de.passau.uni.sec.compose.id.core.service.security.Authorization;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.ApplicationCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ApplicationResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;


@Service
public class ApplicationService extends AbstractSecureEntityBasicEntityService implements EntityService 
{

	private static Logger LOG = LoggerFactory.getLogger(ApplicationService.class);

	@Autowired
	ApplicationRepository applicationRepository;
	
	@Autowired 
	RestAuthentication authentication;
	
	@Autowired
	Authorization authz;
	
	@Autowired
	UniqueValidation check;
	
	@Autowired
	ReputationManager rep;
	
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
			ApplicationCreateMessage message = ((CreateApplicationEvent) event).getMessage();
			
			check.verifyUnique(message.getId());
			
			if(applicationRepository.exists(message.getId()))
				throw new IdManagementException("Application already exists",null,LOG,"Conflict while attempting to create an  Application: "+event.getLoggingDetails(),Level.ERROR,409);
			
			User u = authentication.getUserFromEvent(event);
			Application app  = new Application();
			app.setId(message.getId());
			app.setName(message.getName());
			app.setOwner(u);
			app.setReputation(rep.getReputationValueForNewApplication(u.getId()));
			app = applicationRepository.save(app);
			check.insertUnique(message.getId(),check.APPLICATION);
			EntityResponseMessage res = new ApplicationResponseMessage(app);
			return res;	
	}

	@Override
	protected EntityResponseMessage postACGetEntity(Event event)
			throws IdManagementException {
		
		GetApplicationEvent get = ((GetApplicationEvent ) event);
		Application app = applicationRepository.getOne(get.getId());
		if(app == null)
			throw new IdManagementException("Entity not found",null,LOG,"Entity not found, event :"+get.getLoggingDetails(),Level.DEBUG,404);
		
		Collection<IPrincipal> principals = event.getPrincipals();
		//Only give code to ComposeComponents or when the owner is querying the API
		if( (principals.size()==1 && principals.iterator().next() instanceof ComposeComponentPrincipal) ||
					(principals.size()==1 && principals.iterator().next() instanceof ComposeUserPrincipal && ((ComposeUserPrincipal)principals.iterator().next()).getOpenId().getUser_id().equals(app.getOwner().getId()))
			 ){
		}
		else
			app.setAuthenticationCode(null);
		
		ApplicationResponseMessage res = new ApplicationResponseMessage(app);
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
	
		return applicationRepository.getOne(entityId);
	}
	
	@Override
	protected Logger getLogger() 
	{
		return LOG;
	}


	@Override
	protected void postACDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		Application app = applicationRepository.getOne(event.getEntityId());
		applicationRepository.delete(app);
		Global entity = uniqueRepository.findOne(event.getEntityId());
		uniqueRepository.delete(entity);
	}

	@Override
	protected void verifyAccessControlDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		Application  app = applicationRepository.getOne(event.getEntityId());
		authz.authorizeIfOwner(event.getPrincipals(), app);
		
	}
	
	
}
