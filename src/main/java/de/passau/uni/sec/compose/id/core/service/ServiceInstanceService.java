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
import de.passau.uni.sec.compose.id.core.event.CreateServiceInstanceEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetServiceInstanceEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.Global;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceInstanceRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceSourceCodeRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UniqueRepository;
import de.passau.uni.sec.compose.id.core.service.reputation.ReputationManager;
import de.passau.uni.sec.compose.id.core.service.security.Authorization;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceInstanceCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceInstanceResponseMessage;


@Service
public class ServiceInstanceService extends AbstractSecureEntityBasicEntityService implements EntityService 
{

	private static Logger LOG = LoggerFactory.getLogger(ServiceInstanceService.class);

	@Autowired
	ServiceInstanceRepository serviceInstanceRepository;
	
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
	
	@Autowired
	UniqueRepository uniqueRepository;
	
	
	@Override
	protected EntityResponseMessage postACCreateEntity(Event event)
			throws IdManagementException {
			
			//After this call we are sure there is a user, otherwise an exception would have been thrown
			ServiceInstanceCreateMessage message = ((CreateServiceInstanceEvent) event).getMessage();
			
			check.verifyUnique(message.getId());
			
			if(serviceInstanceRepository.exists(message.getId()))
				throw new IdManagementException("Service Instance already exists",null,LOG,"Conflict while attempting to create a Service Instance: "+event.getLoggingDetails(),Level.ERROR,409);
			
			if(!serviceSourceCodeRepository.exists(message.getSource_code_id()))
				throw new IdManagementException("Service source code not found",null,LOG,"Attempting to create a Service Instance with an unexisting source code with id:"+message.getSource_code_id()+": "+event.getLoggingDetails(),Level.DEBUG,404);
			ServiceSourceCode code = serviceSourceCodeRepository.getOne(message.getSource_code_id());
			User u = authentication.getUserFromEvent(event);
			ServiceInstance si = new ServiceInstance();
			si.setServiceSourceCode(code);
			si.setId(message.getId());
			si.setCollectProvenance(message.isData_provenance_collection());
			si.setPayment(message.isPayment());
			si.setReputation(rep.getReputationValueForNewServiceInstance(u.getId()));
			si.setOwner(u);
			si.setURI(message.getUri());
			si = serviceInstanceRepository.save(si);
			check.insertUnique(message.getId(),check.SERVICE_INSTANCE);
			EntityResponseMessage res = new ServiceInstanceResponseMessage(si);
			return res;	
	}

	@Override
	protected EntityResponseMessage postACGetEntity(Event event)
			throws IdManagementException {
		
		GetServiceInstanceEvent get = ((GetServiceInstanceEvent ) event);
		ServiceInstance si = serviceInstanceRepository.getOne(get.getId());
		if(si == null)
			throw new IdManagementException("Entity not found",null,LOG,"Entity not found, event :"+get.getLoggingDetails(),Level.DEBUG,404);
		Collection<IPrincipal> principals = event.getPrincipals();
		//Only give code to ComposeComponents or when the owner is querying the API
		if( (principals.size()==1 && principals.iterator().next() instanceof ComposeComponentPrincipal) ||
					(principals.size()==1 && principals.iterator().next() instanceof ComposeUserPrincipal && ((ComposeUserPrincipal)principals.iterator().next()).getOpenId().getUser_id().equals(si.getOwner().getId()))
			 ){
		}
		else
			si.setAuthenticationCode(null);
		EntityResponseMessage res = new ServiceInstanceResponseMessage(si);
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
	protected void verifyAccessControlCreateEntity(Event event)
			throws IdManagementException {
		
		this.authz.authorizeIfAnyComponentWithAnyUser(event.getPrincipals());
	}

	@Override
	protected IEntity getEntityById(String entityId) {
	
		return serviceInstanceRepository.getOne(entityId);
	}
	
	
	@Override
	protected Logger getLogger() 
	{
		return LOG;
	}
	
	@Override
	protected void postACDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		ServiceInstance sc = serviceInstanceRepository.getOne(event.getEntityId());
		serviceInstanceRepository.delete(sc);
		Global entity = uniqueRepository.findOne(event.getEntityId());
		uniqueRepository.delete(entity);
	}

	@Override
	protected void verifyAccessControlDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		ServiceInstance sc = serviceInstanceRepository.getOne(event.getEntityId());
		//ensure there is at least one component  (not only one user) behind the call
		authz.authorizeIfAnyComponentWithAnyUser(event.getPrincipals());
		//ensure only owner is deleting		
		authz.authorizeIfOwner(event.getPrincipals(), sc);
		
	}
}
