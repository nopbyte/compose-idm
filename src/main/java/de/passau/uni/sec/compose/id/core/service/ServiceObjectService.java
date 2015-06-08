package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeComponentPrincipal;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.CreateServiceObjectEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetServiceObjectEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateServiceObjectTokenEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.Global;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceObjectRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UniqueRepository;
import de.passau.uni.sec.compose.id.core.service.policy.PolicyManager;
import de.passau.uni.sec.compose.id.core.service.reputation.ReputationManager;
import de.passau.uni.sec.compose.id.core.service.security.Authorization;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectTokenResponseMessage;


@Service
public class ServiceObjectService extends AbstractSecureEntityBasicEntityService implements EntityService 
{

	private static Logger LOG = LoggerFactory.getLogger(ServiceObjectService.class);

	@Autowired
	ServiceObjectRepository serviceObjectRepository;
	
	@Autowired 
	RestAuthentication authentication;
	
	@Autowired
	Authorization authz;
	
	@Autowired
	ReputationManager rep;
	
	@Autowired
	PolicyManager policyManager;
	
	@Autowired
	Random random;
		
	@Autowired
	UniqueValidation check;
	
	
	@Autowired
	UniqueRepository uniqueRepository;
	
	@Override
	protected EntityResponseMessage postACCreateEntity(Event event)
			throws IdManagementException {
			
			//After this call we are sure there is a user, otherwise an exception would have been thrown
			ServiceObjectCreateMessage message = ((CreateServiceObjectEvent) event).getMessage();
			
			check.verifyUnique(message.getId());
			
			if(serviceObjectRepository.exists(message.getId()))
				throw new IdManagementException("ServiceObject already exists",null,LOG,"Conflict while attempting to crete a service objcet: "+event.getLoggingDetails(),Level.ERROR,409);
			
			User u = authentication.getUserFromEvent(event);
			ServiceObject so = new ServiceObject();
			if(message.isRequires_token())
				so.setApiToken(random.getRandomToken());
			
			so.setId(message.getId());
			so.setCollectProvenance(message.isData_provenance_collection());
			so.setPayment(message.isPayment());
			so.setOwner(u);
			so.setReputation(rep.getReputationValueForNewServiceObject(u.getId()));
			so.setCollectProvenance(true);
			so = serviceObjectRepository.save(so);
			check.insertUnique(message.getId(),check.SERVICE_OBJECT);
			List<Map<String, Object>> policy = policyManager.getPolicyForNewServiceObject(u.getId(), so);
			//in this case the policy needs to be included in the ServiceObject response in order for the Service Object registry to keep a copy of it.
			ServiceObjectResponseMessage res = new ServiceObjectResponseMessage (so,policy);
			return res;	
	}


	@Override
	protected EntityResponseMessage postACGetEntity(Event event)
			throws IdManagementException {
		
		GetServiceObjectEvent get = ((GetServiceObjectEvent ) event);
		ServiceObject so = serviceObjectRepository.getOne(get.getId());
		if(so == null)
			throw new IdManagementException("Entity not found",null,LOG,"Entity not found, event :"+get.getLoggingDetails(),Level.DEBUG,404);
		
		Collection<IPrincipal> principals = event.getPrincipals();
		//Whenever a GET call is executed it shouldn't contain the policy of the SO...
		ServiceObjectResponseMessage res = new ServiceObjectResponseMessage (so, null);
		
		
		//Only give API_TOKEN to ComposeComponents or when the owner is querying the API
		if( (principals.size()==1 && principals.iterator().next() instanceof ComposeComponentPrincipal) ||
			(principals.size()==1 && principals.iterator().next() instanceof ComposeUserPrincipal && ((ComposeUserPrincipal)principals.iterator().next()).getOpenId().getUser_id().equals(so.getOwner().getId()))
		  ){
		}
		else 
			res.setApi_token(null);
		
		return res;
	}

	protected   void verifyAccessControlUpdateEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		ServiceObject so = serviceObjectRepository.findOne(event.getEntityId());
		if(so !=null)
			authz.authorizeIfOwner(event.getPrincipals(), so);
		else
			throw new IdManagementException("Service Object not found",null, LOG," Attempting to update a non-existent service object"+event.getEntityId(),Level.ERROR, 404);
		
	}
	
	@Override
	protected EntityResponseMessage postACUpdateEntity(DetailsIdEvent event, IEntity previous)
			throws IdManagementException {
		
		//AC is done in the locally defined method instead of the AbstractSecure class...
		Collection<IPrincipal> principals = event.getPrincipals();
		//trying to update a SO token
		if(event instanceof UpdateServiceObjectTokenEvent)
		{
			UpdateServiceObjectTokenEvent tokenUpdate = (UpdateServiceObjectTokenEvent) event;
			ServiceObject so = serviceObjectRepository.findOne(event.getEntityId());
			if(so.getApiToken().equals(tokenUpdate.getMessage().getOld_api_token()))
			{
				String newToken = random.getRandomToken();
				so.setApiToken(newToken);
				so.UpdateLastModifiedToNow();
				serviceObjectRepository.save(so);
				List<Map<String, Object>> policy = policyManager.getPolicyExistingServiceObject(so);
				//keep the policy in the response since this call could be issued by Servioticy
				return new ServiceObjectResponseMessage(so, policy);
			}
			else
				//Although one should not leak which error happen. It is important to help developers to get what they are doing wrong in this case, since there are two authentication credentials at the same time (the API token, and the user token provided as authentication header)
				throw new IdManagementException("Forbidden action. Wrong API TOKEN",null, LOG," Wrong API token for Service Object: "+tokenUpdate.getEntityId(),Level.ERROR, 401);
				
		}
		// 403 Forbidden, 304 Not modified, or 409 not modified (conflict), 
		//TODO verify ownership, or propper permissions
		
		return null;
	}

	
	
	@Override
	protected void verifyAccessControlCreateEntity(Event event)
			throws IdManagementException {
		
		this.authz.authorizeIfAnyComponentWithAnyUser(event.getPrincipals());
		
	}
	
	@Override
	protected IEntity getEntityById(String entityId) {
	
		return serviceObjectRepository.getOne(entityId);
	}
	
	
	@Override
	protected Logger getLogger() 
	{
		return LOG;
	}
	
	@Override
	protected void postACDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		ServiceObject sc = serviceObjectRepository.getOne(event.getEntityId());
		serviceObjectRepository.delete(sc);
		Global entity = uniqueRepository.findOne(event.getEntityId());
		uniqueRepository.delete(entity);
	}

	@Override
	protected void verifyAccessControlDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		ServiceObject sc = serviceObjectRepository.getOne(event.getEntityId());
		//ensure there is at least one component  (not only one user) behind the call
		authz.authorizeIfAnyComponentWithAnyUser(event.getPrincipals());
		authz.authorizeIfOwner(event.getPrincipals(), sc);
		
	}
}
