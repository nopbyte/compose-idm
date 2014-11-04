package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.AbstractGetEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.ListApplicationsOwnedByUserEvent;
import de.passau.uni.sec.compose.id.core.event.ListEntitiesInGroupEvent;
import de.passau.uni.sec.compose.id.core.event.ListServiceCompositionsOwnedByUserEvent;
import de.passau.uni.sec.compose.id.core.event.ListServiceInstancesOwnedByUserEvent;
import de.passau.uni.sec.compose.id.core.event.ListServiceObjectsOwnedByUserEvent;
import de.passau.uni.sec.compose.id.core.event.ListServiceSourceCodesOwnedByUserEvent;
import de.passau.uni.sec.compose.id.core.event.ListUsersInGroupEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.Membership;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceComposition;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.ApplicationRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.EntityGroupMembershipRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.MembershipRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceCompositionRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceInstanceRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceObjectRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceSourceCodeRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.core.service.security.Authorization;
import de.passau.uni.sec.compose.id.rest.messages.ApplicationResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityGroupMembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntitySetResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.MembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceCompositionResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceInstanceResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UsersInGroupMessage;


@Service
public class ListEntitiesOwnedByUserService extends AbstractListEntityService  
{

	private static Logger LOG = LoggerFactory.getLogger(ListEntitiesOwnedByUserService.class);

	
	@Autowired
    GroupRepository groupRepository;
	
	
	@Autowired
    ServiceObjectRepository soRepo;
	
	@Autowired
    ServiceInstanceRepository serviceInstanceRepo;
	
	@Autowired
    ServiceSourceCodeRepository serviceSourceRepo;

	@Autowired
    ServiceCompositionRepository serviceCompRepo;
	
	@Autowired
	ApplicationRepository appRepo;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	Authorization authz;
	
	@Override
	public Object postACListAllEntities(Event event)
			throws IdManagementException {
		
		AbstractGetEvent getEvent = (AbstractGetEvent)event;
		User owner = userRepository.getOne(getEvent.getId());
		List<Object> list = new LinkedList<Object>();
		if(event instanceof ListApplicationsOwnedByUserEvent)
		{
			List<Application> ret = appRepo.findByOwner(owner);
			for(Application app: ret)
				list.add(new ApplicationResponseMessage(app));
		}
		else if (event instanceof ListServiceCompositionsOwnedByUserEvent)
		{
			List<ServiceComposition> ret = serviceCompRepo.findByOwner(owner);
			for(ServiceComposition sc: ret)
				list.add(new ServiceCompositionResponseMessage(sc));
		}
		else if (event instanceof ListServiceInstancesOwnedByUserEvent)
		{
			List<ServiceInstance> ret = serviceInstanceRepo.findByOwner(owner);
			for(ServiceInstance si: ret)
				list.add(new ServiceInstanceResponseMessage(si));
		}
		else if(event instanceof ListServiceSourceCodesOwnedByUserEvent)
		{
			List<ServiceSourceCode> ret = serviceSourceRepo.findByDeveloper(owner);
			for(ServiceSourceCode sc: ret)
				list.add(new ServiceSourceCodeResponseMessage(sc));
		}
		else if(event instanceof ListServiceObjectsOwnedByUserEvent)
		{
			List<ServiceObject> ret = soRepo.findByOwner(owner);
			for(ServiceObject so: ret)
			{
				ServiceObjectResponseMessage m = new ServiceObjectResponseMessage(so,null);
				m.setApi_token(null);
				list.add(m);	
			}
		}
		EntitySetResponseMessage res = new EntitySetResponseMessage();
		res.setEntities(list);
		return res;		
	}

	@Override
	public void verifyACListAllEntities(Event event)
			throws IdManagementException {
		
		Collection<IPrincipal> p = event.getPrincipals();
		if(p == null || p.size()==0)
			throw new IdManagementException("Authentication required.",null, LOG," Attempt to access GET without providing credentials",Level.DEBUG, 401);

		
	}

	
}
