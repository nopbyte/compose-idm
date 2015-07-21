package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.CreateGroupEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetGroupEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateUserEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.Membership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Role;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.MembershipRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.RoleRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.core.service.security.Authorization;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.GroupResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;


@Service
public class GroupService extends AbstractSecureEntityBasicEntityService implements EntityService 
{

	private static Logger LOG = LoggerFactory.getLogger(GroupService.class);

	
	@Autowired
	GroupRepository groupRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired 
	RoleRepository roleRepository;
	
	
	@Autowired 
	RestAuthentication authentication;
	
	@Autowired
	Authorization authz;
	
	@Autowired
	MembershipRepository membershipRepository;
	
	@Override
	protected EntityResponseMessage postACCreateEntity(Event event)
			throws IdManagementException {
			
		
		
		// To be moved something else...
					/*Role r = new Role();
					r.setName("DEVELOPER");
					r.setId("DEVELOPER");
					roleRepository.save(r);
					*/
					//end 
			
			//After this call we are sure there is a user, otherwise an exception would have been thrown
			CreateGroupEvent group = (CreateGroupEvent) event;
			User u = authentication.getUserFromEvent(event);
			
			Collection<Group> userGroups = u.getGroups();
			for(Group i: userGroups)
				if(i.getName().equals(group.getMessage().getName()))
						throw new IdManagementException("Entity already exists",null,LOG,"There is a group with the same name for the user already"+group.getLoggingDetails(),Level.DEBUG,409);
			
			GroupCreateMessage message = group.getMessage();
			Group g = new Group();
			g.setId(UUID.randomUUID().toString());
			g.setName(message.getName());
			g.setOwner(u);
			groupRepository.save(g);
			//So that the user has memberships of ADMIN for the groups he owns
			createGroupMembershipAdmin(g,u);			
			GroupResponseMessage res = new GroupResponseMessage(g);
			return res;	
	}

	private void createGroupMembershipAdmin(Group g, User u) 
	{
		Role r = roleRepository.findOne(Role.ADMIN);
		if(r!=null)
		{
			Membership memb = new Membership();
			memb.setApprovedByGroupOwner(true);
			memb.setApprovedByUser(true);
			memb.setGroup(g);
			memb.setId(UUID.randomUUID().toString());
			memb.setRole(r);
			memb.setUser(u);
			membershipRepository.save(memb);
		}		

	}

	@Override
	protected EntityResponseMessage postACGetEntity(Event event)
			throws IdManagementException {
		
		GetGroupEvent get = ((GetGroupEvent) event);
		Group g = groupRepository.getOne(get.getId());
		if(g == null)
			throw new IdManagementException("Entity not found",null,LOG,"Entity not found, event :"+get.getLoggingDetails(),Level.DEBUG,404);
		
		GroupResponseMessage res = new GroupResponseMessage(g);
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
	protected Logger getLogger() 
	{
		return LOG;
	}

	@Override
	protected IEntity getEntityById(String entityId) {
	
		return groupRepository.getOne(entityId);
	}

	
	@Override
	protected void postACDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		Group g = groupRepository.getOne(event.getEntityId());
		List<Membership> memb = membershipRepository.findByGroup(g);
		String userIdFromMembership = memb.get(0).getUser().getId();
		User userFromEvent = authentication.getUserFromEvent(event);
		if( memb.size() == 0 ||  
			(memb.size() == 1 && userFromEvent.getId().equals(userIdFromMembership))
		   ) 
		{
			membershipRepository.delete(memb.get(0));
			groupRepository.delete(g);	
		}
		else
			throw new IdManagementException("Conflict: group has memberships",null,LOG,"group has memberships: group id"+event.getEntityId(),Level.DEBUG,409);
		
	}

	@Override
	protected void verifyAccessControlDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		Group   g = groupRepository.getOne(event.getEntityId());
		authz.authorizeIfOwner(event.getPrincipals(), g);
		
	}
	
	

	
	
	
	
	
}
