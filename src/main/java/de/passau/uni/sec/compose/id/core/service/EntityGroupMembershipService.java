package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sun.security.acl.GroupImpl;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeComponentPrincipal;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.ApproveEntityGroupMembershipEvent;
import de.passau.uni.sec.compose.id.core.event.ApproveMembershipEvent;
import de.passau.uni.sec.compose.id.core.event.CreateEntityGroupMembershipEvent;
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;
import de.passau.uni.sec.compose.id.core.event.CreateMembershipEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetUserEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateUserEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.CoreEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.Membership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Role;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceComposition;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.ApplicationRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.EntityGroupMembershipRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.MembershipRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.RoleRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceCompositionRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceInstanceRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceObjectRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceSourceCodeRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.core.service.reputation.ReputationManager;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.core.service.security.uaa.OpenIdUserData;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAUserRequest;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAUserRequestName;
import de.passau.uni.sec.compose.id.rest.controller.UserCommandsController;
import de.passau.uni.sec.compose.id.rest.messages.EntityGroupMembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.MembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.PendingUserMembershipMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;


@Service
public class EntityGroupMembershipService extends AbstractListEntityService implements EntityService 
{

	private static Logger LOG = LoggerFactory.getLogger(EntityGroupMembershipService.class);

	
	@Autowired
    GroupRepository groupRepository;
	
	@Autowired
	EntityGroupMembershipRepository membershipRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ApplicationRepository appRepository;
	
	@Autowired
	ServiceObjectRepository serviceObjectRepository;
	
	@Autowired
	ServiceInstanceRepository serviceInstanceRepository;
	
	@Autowired
	ServiceCompositionRepository serviceCompositionRepository;
	
	@Autowired
	ServiceSourceCodeRepository serviceSourceCodeRepository;
	
	@Autowired 
	RestAuthentication authentication;

	private EntityGroupMembership buildEntityGroupMembership(User u, CreateEntityGroupMembershipEvent event) throws IdManagementException {
	
		CoreEntity entity = getEntity(event);
		EntityGroupMembership memb = new EntityGroupMembership();
		if(entity instanceof Application)
			memb.setApplication(appRepository.getOne(event.getEntityId()));
		else if(entity instanceof ServiceComposition)
			memb.setServiceComposition(serviceCompositionRepository.getOne(event.getEntityId()));
		else if(entity instanceof ServiceInstance)
			memb.setServiceInstance(serviceInstanceRepository.getOne(event.getEntityId()));
		else if(entity instanceof ServiceObject)
			memb.setServiceObject(serviceObjectRepository.getOne(event.getEntityId()));
		else if(entity instanceof ServiceSourceCode)
			memb.setServiceSourceCode(serviceSourceCodeRepository.getOne(event.getEntityId()));
		else 
			throw new IdManagementException("The entity type is not valid",null, LOG,"Principal attempting to create a entity group membership with a wrong entity type"+event.getEntityType()+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.ERROR, 404);
		return memb;
	}
	/**
	 * 
	 * @param event Event for creation of the entity
	 * @return The entity corresponding to the event
	 * @throws IdManagementException exception if the type of entity is not found, or if the proper repository failed to find the identity for the given id.
	 */
	private CoreEntity getEntity(CreateEntityGroupMembershipEvent event) throws IdManagementException
	{
		CoreEntity ret = null;
		if(event.getEntityType().equals(EntityGroupMembership.APPLICATION))
			ret = appRepository.getOne(event.getEntityId());
		else if(event.getEntityType().equals(EntityGroupMembership.SERVICECOMPOSITION))
			ret = serviceCompositionRepository.getOne(event.getEntityId());
		else if(event.getEntityType().equals(EntityGroupMembership.SERVICEINSTANCE))
			ret = serviceInstanceRepository.getOne(event.getEntityId());
		else if(event.getEntityType().equals(EntityGroupMembership.SERVICEOBJECT))
			ret =serviceObjectRepository.getOne(event.getEntityId());
		else if(event.getEntityType().equals(EntityGroupMembership.SERVICESOURCECODE))
			ret =serviceSourceCodeRepository.getOne(event.getEntityId());
		else 
			throw new IdManagementException("The entity type is not valid",null, LOG,"Principal attempting to create a entity group membership with a wrong entity type"+event.getEntityType()+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.ERROR, 404);
		return ret;
	}
	
	private  EntityGroupMembership getMembershipForEntityInGroup(CoreEntity entity, Group group)
	{
		List<?> ret = null;
		if(entity instanceof Application)
			ret = membershipRepository.findByApplicationAndGroup((Application) entity, group);
		else if(entity instanceof ServiceComposition)
			ret = membershipRepository.findByServiceCompositionAndGroup((ServiceComposition) entity, group);
		else if(entity instanceof ServiceInstance)
			ret = membershipRepository.findByServiceInstanceAndGroup((ServiceInstance) entity, group);
		else if(entity instanceof ServiceObject)
			ret = membershipRepository.findByServiceObjectAndGroup((ServiceObject) entity, group);
		else if(entity instanceof ServiceSourceCode)
			ret = membershipRepository.findByServiceSourceCodeAndGroup((ServiceSourceCode) entity, group);
		if( ret!=null&&ret.size()>0)
			return (EntityGroupMembership) ret.iterator().next();
		
		return null;
	}
	@Override
	protected void verifyAccessControlCreateEntity(Event event)
			throws IdManagementException {

		CoreEntity entity = getEntity((CreateEntityGroupMembershipEvent) event);
		CreateEntityGroupMembershipEvent membEvent = ((CreateEntityGroupMembershipEvent)event);
		Collection<IPrincipal> principals = event.getPrincipals();
		for(IPrincipal p: principals)
		{
			if(p instanceof ComposeUserPrincipal)
			{
				ComposeUserPrincipal user = ((ComposeUserPrincipal)p);
				OpenIdUserData userData = user.getOpenId();
				if(entity.getOwner().getId().equals(user.getOpenId().getUser_id()))
					membEvent.setExecutedByEntityOwner(true);
				User u = userRepository.getOne(userData.getUser_id());
				if(u.isGroupAdmin(membEvent.getMessage().getGroup_id()))
					membEvent.setExecutedByGroupAdmin(true);
				Group g = groupRepository.getOne(membEvent.getMessage().getGroup_id());
				if(g.getOwner().getId().equals(u.getId()))
					membEvent.setExecutedByGroupOwner(true);
			}
		}
		if(!membEvent.isExecutedByGroupAdmin() && !membEvent.isExecutedByGroupOwner() && !membEvent.isExecutedByEntityOwner())
			 throw new IdManagementException("Not sufficient permissions for the action requred : Attempt to add an entity in a group where you are not admin nor the owner",null, LOG,"Attempt to add another user in a group where the principal is not admin nor owner. The entities authenticated for the request do not have sufficient permissions to execute it, principals "+RestAuthentication.getBasicInfoPrincipals(principals),Level.ERROR, 403);
		
	}
	
	
		@Override
	protected EntityResponseMessage postACCreateEntity(Event event)
			throws IdManagementException {
		
		
		CreateEntityGroupMembershipEvent membEvent = ((CreateEntityGroupMembershipEvent)event);
		
		User u = authentication.getUserFromEvent(event);
		Group group =groupRepository.getOne(membEvent.getMessage().getGroup_id());
		
		EntityGroupMembership m = buildEntityGroupMembership(u,membEvent);
		EntityGroupMembership um = getMembershipForEntityInGroup(m.getEntity(),group);
		if(um!=null)
			throw new IdManagementException("Entity with id:"+membEvent.getEntityId()+" with type:"+membEvent.getEntityType()+" has already a membership for the same group. It is waiting for approval from "+(um.isApprovedByGroupOwner()?" the owner of the entity":" the group owner/admin"),null, LOG,
					"Entity with id:"+membEvent.getEntityId()+" with type:"
					+membEvent.getEntityType()+" has already a membership for the same group. It is waiting for approval from "
					+(um.isApprovedByGroupOwner()?" the owner of the entity ":" the group owner/admin")
					+"Principals: "+RestAuthentication.getBasicInfoPrincipals(membEvent.getPrincipals()),Level.INFO, 409);
		
		m.setApprovedByGroupOwner(membEvent.isExecutedByGroupOwner()||membEvent.isExecutedByGroupAdmin());
		m.setApprovedBySelfOwner(membEvent.isExecutedByEntityOwner());
		m.setGroup(group);
		m.setId(UUID.randomUUID().toString());
		membershipRepository.save(m);
		
		return new EntityGroupMembershipResponseMessage(m);
	}

		@Override
		protected void verifyAccessControlGetEntity(Event event)
				throws IdManagementException {
			//TODO
		}

	
	@Override
	protected EntityResponseMessage postACGetEntity(Event event)
			throws IdManagementException {
		//TODO 
		return null;
		

	}
	
	@Override
	protected void verifyAccessControlUpdateEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		Collection<IPrincipal> principals = event.getPrincipals();
		if(principals == null || principals.size()==0)
			throw new IdManagementException("Authentication required.",null, LOG," Attempt to list unapproved memberships without providing credentials",Level.DEBUG, 401);
		
		if(principals.size()!=1)
			throw new IdManagementException("Only one user principal should call this API endpoint.",null, LOG,"There is more than one principal for getting pending user approvals ",Level.DEBUG, 401);
	
		//The rest of checks need to be done with the update since it must be specifically noticed whether the update is being done by the user, or the group owner/admin... see postACUPdate method...
	}


	@Override
	protected EntityResponseMessage postACUpdateEntity(DetailsIdEvent event, IEntity previous)
			throws IdManagementException {
		
		Collection<IPrincipal> principals = event.getPrincipals();
		ApproveEntityGroupMembershipEvent approve = ((ApproveEntityGroupMembershipEvent ) event);
		EntityGroupMembership attemptedMembership = membershipRepository.getOne(approve.getEntityId());
		IPrincipal prin = principals.iterator().next();
		boolean updated = false;
		if(prin instanceof ComposeUserPrincipal)
		{
			ComposeUserPrincipal userPrincipal = ((ComposeUserPrincipal)prin);
			CoreEntity entityInMembership = attemptedMembership.getEntity();
			//it is a membership that needs authorization of the owner of the entity
			if(entityInMembership.getOwner().getId().equals(userPrincipal.getOpenId().getUser_id()) 
					&& !attemptedMembership.isApprovedBySelfOwner())
			{
				updated = true;
				attemptedMembership.setApprovedBySelfOwner(true);
			}
			//owner of the group
			if(needsApprovalByUserAsOwner(attemptedMembership,userPrincipal))
			{
				attemptedMembership.setApprovedByGroupOwner(true);
				updated=true;
			}
			if(needsApprovalByUserAsGroupAdmin(attemptedMembership, userPrincipal))
			{
				attemptedMembership.setApprovedByGroupOwner(true);
				updated=true;
			}
				
			//the principal is admin in the group -approved- and the attempted membership requires that group admin or owners approve it
			if(!updated)
				throw new IdManagementException("There was no pending approval from the principal calling the API",null, LOG,"Principal attempting to approve a entity group membership that he didn't have to approve. Membership group"+attemptedMembership.getGroup().getId()
						+"Entity involved in membership id: "+entityInMembership.getId()+", Entity id : "+attemptedMembership.getEnityId()+" Entity type:"+attemptedMembership.getEntityType()+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 403);
		}
		else
			throw new IdManagementException("To approve a membership, the principal must be a user",null, LOG,"Principal attempting to approve a membership is not a user. princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 401);
	
		attemptedMembership.setLastModified(new Date( (new Date().getTime()/1000)*1000 ));
		attemptedMembership = membershipRepository.save(attemptedMembership);
		return new EntityGroupMembershipResponseMessage(attemptedMembership);
		
	}


	@Override
	protected Logger getLogger() 
	{
		return LOG;
	}

	@Override
	protected IEntity getEntityById(String entityId) {
		return membershipRepository.getOne(entityId);
	}

	
	@Override
	public Object postACListAllEntities(Event event)
			throws IdManagementException {
		
		
		//this one is a map to prevent returning repeated approvals that may appear if a user is agroup owner and an admin at the same time
		HashMap<String, EntityGroupMembershipResponseMessage>   groupOwner = new HashMap<String, EntityGroupMembershipResponseMessage>()  ;
		List<EntityGroupMembershipResponseMessage> selfApprovals = new LinkedList<EntityGroupMembershipResponseMessage>();
		User u = authentication.getUserFromEvent(event);
		Collection<Group> groups = u.getGroups();
		//entity memberships for groups owned by the user
		for(Group group: groups)
			for(EntityGroupMembership m: membershipRepository.findByGroup(group))
				if(!m.isApprovedByGroupOwner())
					groupOwner.put(m.getId(),new EntityGroupMembershipResponseMessage(m));
		//entity memberships for groups where the user is admin
		for(Membership principaMembership: u.getMemberships())
		{
			if(u.isGroupAdmin(principaMembership.getGroup().getId()))
			{	
				List<EntityGroupMembership> pendingAdmin = membershipRepository.findByGroup(principaMembership.getGroup());
				for(EntityGroupMembership pending: pendingAdmin)
					if(!pending.isApprovedByGroupOwner())
						groupOwner.put(pending.getId(), new EntityGroupMembershipResponseMessage(pending));	
			}
		}
		List<EntityGroupMembershipResponseMessage> group = new LinkedList<EntityGroupMembershipResponseMessage>();
		for(String key: groupOwner.keySet())
			group.add(groupOwner.get(key));
		
		selfApprovals = getEntityGroupMembershipsNonApprovedAsOwner(u);
		return new PendingUserMembershipMessage(group, selfApprovals);
	}

	private List<EntityGroupMembershipResponseMessage> getEntityGroupMembershipsNonApprovedAsOwner(
			User user) {
		
		List<EntityGroupMembership> membs= new LinkedList<>();
		List<Application> apps=appRepository.findByOwner(user);
		List<ServiceInstance> sis=serviceInstanceRepository.findByOwner(user);
		List<ServiceObject> sos=serviceObjectRepository.findByOwner(user);
		List<ServiceComposition> scs=serviceCompositionRepository.findByOwner(user);
		List<ServiceSourceCode> sources=serviceSourceCodeRepository.findByDeveloper(user);
		for(Application app: apps)
			membs.addAll(membershipRepository.findByApplicationAndApprovedBySelfOwner(app, false));
		for(ServiceInstance si: sis)
			membs.addAll(membershipRepository.findByServiceInstanceAndApprovedBySelfOwner(si, false));
		for(ServiceObject so: sos)
			membs.addAll(membershipRepository.findByServiceObjectAndApprovedBySelfOwner(so, false));
		for(ServiceComposition sc: scs)
			membs.addAll(membershipRepository.findByServiceCompositionAndApprovedBySelfOwner(sc, false));
		for(ServiceSourceCode source: sources)
			membs.addAll(membershipRepository.findByServiceSourceCodeAndApprovedBySelfOwner(source, false));
		List<EntityGroupMembershipResponseMessage> ret = new LinkedList<>();
		for(EntityGroupMembership membership: membs)
			ret.add(new EntityGroupMembershipResponseMessage(membership));
		return ret;
	}
	@Override
	public void verifyACListAllEntities(Event event)
			throws IdManagementException {
		//Just check that there is only one principal 
		Collection<IPrincipal> principals = event.getPrincipals();
		
		if(principals == null || principals.size()==0)
			throw new IdManagementException("Authentication required.",null, LOG," Attempt to list unapproved memberships without providing credentials",Level.DEBUG, 401);
		
		if(principals.size()!=1)
			throw new IdManagementException("Only one user principal should call this API endpoint.",null, LOG,"There is more than one principal for getting pending user approvals ",Level.DEBUG, 401);
		
	}

	@Override
	protected void postACDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		EntityGroupMembership attempted = membershipRepository.getOne(event.getEntityId());
		membershipRepository.delete(attempted);
	}

	@Override
	protected void verifyAccessControlDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		Collection<IPrincipal> principals = event.getPrincipals();
		if(principals == null || principals.size()==0)
			throw new IdManagementException("Authentication required.",null, LOG," Attempt to list unapproved memberships without providing credentials",Level.DEBUG, 401);
		
		if(principals.size()!=1)
			throw new IdManagementException("Only one user principal should call this API endpoint.",null, LOG,"There is more than one principal while trying to remove a user approval",Level.DEBUG, 401);
		
		EntityGroupMembership  memb = membershipRepository.getOne(event.getEntityId());
		IPrincipal prin = principals.iterator().next();
		if(prin instanceof ComposeUserPrincipal)
		{
			if(!userCanDelete(memb, (ComposeUserPrincipal) prin))
				throw new IdManagementException("User does not have sufficient permissions to delete the membesrhip ",null, LOG,"Principal attempting to delete a Entity group membership without sufficient permissions. Membership group"+memb.getGroup().getId()
						+" entity id: "+memb.getEnityId()+", entity type: "+memb.getEntityType()+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 403);
		}
		else
			throw new IdManagementException("To approve a membership, the principal must be a user",null, LOG,"Principal attempting to approve a membership is not a user. princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 401);
	
	}

	/**
	 * 
	 * @param attemptedMembership membership that is attempting to be updated 
	 * @param userPrincipal principal executing the action 
	 * @return true if the membership requires the approval of the user as owner of the entity being added
	 */
	private boolean needsApprovalByUserAsOwner(EntityGroupMembership attemptedMembership, ComposeUserPrincipal userPrincipal)
	{
		boolean cond = ( userPrincipal.getOpenId().getUser_id().equals(attemptedMembership.getGroup().getOwner().getId()) 
				&& !attemptedMembership.isApprovedByGroupOwner());
		
		return cond;
	}
	/**
	 * 
	 * @param attemptedMembership membership that is attempting to be updated 
	 * @param userPrincipal principal executing the action
	 * @return true if the membership requires the approval of the user as owner of the entity being added
	 */
	private boolean needsApprovalByUserAsGroupAdmin(EntityGroupMembership attemptedMembership, ComposeUserPrincipal userPrincipal)
	{
		User u = userRepository.getOne(userPrincipal.getOpenId().getUser_id());
		return u.isGroupAdmin(attemptedMembership.getGroup().getId()) && !attemptedMembership.isApprovedByGroupOwner();
	}
	
	private boolean userCanDelete(EntityGroupMembership  membership, ComposeUserPrincipal user)
	{
		//check if the user is admin of the group of the membership
		User u = userRepository.getOne(user.getOpenId().getUser_id());
		if(u.isGroupAdmin(membership.getGroup().getId()))
			return true;
		// if it was not an admin of the group, then verify if it is the group owner 
		if ( user.getOpenId().getUser_id().equals(membership.getGroup().getOwner().getId()) )
			return true;
		//if it was not any of the previous conditions, the last chance it is that the user owns the entity
		return membership.getEntity().getOwner().getId().equals(user);
		
	}
}
