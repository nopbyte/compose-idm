package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeComponentPrincipal;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetUserEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateUserEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.core.service.reputation.ReputationManager;
import de.passau.uni.sec.compose.id.core.service.security.Authorization;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAUserRequest;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAUserRequestName;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;


@Service
public class UserService extends AbstractSecureEntityBasicEntityService implements EntityService 
{

	private static Logger LOG = LoggerFactory.getLogger(UserService.class);

	/**
	 * Should be passed to ComposeRepository to manage exceptions... never access it directly
	 */
	@Autowired
    UserRepository userRepository;
	
	@Autowired
	UsersAuthzAndAuthClient uaa;
	
	@Autowired
	Authorization authz;
	
	@Autowired
	ReputationManager rep;
	
	@Autowired
	RestAuthentication auth;
	
	@Autowired
	Random random;
	
	
	
	@Override
	protected EntityResponseMessage postACCreateEntity(Event event)
			throws IdManagementException {
		
		
		CreateUserEvent create = ((CreateUserEvent) event);
		
		// Build the UAA request for creating a user.
		UAAUserRequest uaaData = new UAAUserRequest();
		uaaData.setUsername(create.getUserMessage().getUsername());
		uaaData.setPassword(create.getUserMessage().getPassword());
		uaaData.addEmail(create.getUserMessage().getUsername()+"@compose.com");
		
		UAAUserRequestName name = new UAAUserRequestName();
		name.setFamilyName("compose");
		name.setGivenName(create.getUserMessage().getUsername());
		name.setFormatted(" ");
		uaaData.setName(name);
		
		//create user in the UAA
		Map<String,Object> UAAResponse = uaa.createUser(uaaData);
		String id = (String) UAAResponse.get("id");
		if(id == null)
			throw new IdManagementException("Internal communication error",null,LOG,"UAA Creation of user doesn't have an id"+UAAResponse,Level.ERROR,500);
		
		//create user in the local database
		User u = new User();
		u.setId(id);
		u.setReputation(rep.getReputationValueforNewUser());
		u.setUsername(create.getUserMessage().getUsername());
		//u.setLastModified(new Date(System.currentTimeMillis()));
		u.setRandom_auth_token(random.getRandomToken());
		u = userRepository.save(u);		
		
		UserResponseMessage res = new UserResponseMessage(u);
		return res;
	}

	@Override
	protected EntityResponseMessage postACGetEntity(Event event)
			throws IdManagementException {
		User u = null;
		if(event instanceof GetUserEvent)
		{
		   GetUserEvent get = ((GetUserEvent) event);
		   u = userRepository.getOne(get.getId());
		}
		else{
			ComposeUserPrincipal comp = auth.getComposeUser(event.getPrincipals());
			u = userRepository.getOne(comp.getOpenId().getUser_id());
			
		}
		UserResponseMessage res = new UserResponseMessage(u);
		try{
		     authz.authorizeIfOwnerOrComponent(event.getPrincipals(), u);
		 	res.setRandom_auth_token(u.getRandom_auth_token());
			
		}catch(IdManagementException ex)
		{
			//Its ok, he is not asking data about himself...
		}
		return res;
		

	}

	@Override
	protected EntityResponseMessage postACUpdateEntity(DetailsIdEvent event, IEntity previous)
			throws IdManagementException {
		
		UpdateUserEvent update = ((UpdateUserEvent) event);
		User u = new User();
		UserResponseMessage message = new UserResponseMessage(u);
		//String epoch = update.getLastModifiedKnown();
		//TODO verify that the last modification of the user coincides with the timestamp in the event.
		// 403 Forbidden, 304 Not modified, or 409 not modified (conflict), 
		
		
		return message;
	}

	@Override
	protected void verifyAccessControlCreateEntity(Event event)
			throws IdManagementException {

		boolean ok = false;
		Collection<IPrincipal> principals = event.getPrincipals();
		for(IPrincipal p: principals)
		{
			if(p instanceof ComposeComponentPrincipal)
			{
			    //TODO Add additional AC rules if required
				//if(((ComposeComponentPrincipal)p).getComposeComponentName().equals("sdk"))
					ok = true;
					LOG.debug("Compose Principal "+((ComposeComponentPrincipal)p).getComposeComponentName()+" executing User creation: ");
			}
		}
		if(!ok)
			throw new IdManagementException("Not sufficient permissions for the action requred ",null, LOG,"The entities authenticated for the request do not have sufficient permissions to execute it, principals "+RestAuthentication.getBasicInfoPrincipals(principals),Level.ERROR, 403);
		
	}


	@Override
	protected void verifyAccessControlUpdateEntity(DetailsIdEvent event)
			throws IdManagementException {
		// TODO Ensure that it is the same user
		
	}

	@Override
	protected Logger getLogger() 
	{
		return LOG;
	}

	@Override
	protected IEntity getEntityById(String entityId) {
		return userRepository.getOne(entityId);
	}

	@Override
	protected void postACDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		User sc = userRepository.getOne(event.getEntityId());
		//the user repository will throw exception if the user still has any associations (groups...etc), so we do it first
		userRepository.delete(sc);
		uaa.deleteUser(sc.getId());
		
	}

	@Override
	protected void verifyAccessControlDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		User sc = userRepository.getOne(event.getEntityId());
		authz.authorizeIfOwnerOrComponent(event.getPrincipals(), sc);
		
		
	}
	
	
	
	
	
}
