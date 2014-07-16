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
import de.passau.uni.sec.compose.id.core.event.ApproveMembershipEvent;
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;
import de.passau.uni.sec.compose.id.core.event.CreateMembershipEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetUserEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateUserEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.Membership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Role;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.MembershipRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.RoleRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.core.service.reputation.ReputationManager;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.core.service.security.uaa.OpenIdUserData;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAUserRequest;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAUserRequestName;
import de.passau.uni.sec.compose.id.rest.controller.UserCommandsController;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.MembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.PendingUserMembershipMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;


@Service
public class UserMembershipService extends AbstractBasicListEntityService implements EntityService 
{

	private static Logger LOG = LoggerFactory.getLogger(UserMembershipService.class);

	@Autowired
    UserRepository userRepository;
	
	@Autowired
    GroupRepository groupRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	MembershipRepository membershipRepository;
	
	@Autowired
	UsersAuthzAndAuthClient uaa;
	
	@Autowired
	ReputationManager rep;

	/**
	 * Method that initializes roles when database is empty. This is done, so this application can be deployed without requiring creation of any additional entries in the database.
	 */
	private void initializeRoles() {
		
		Role role = null;
		String value = null;
		role = new Role();

		
		value = Role.ADMIN;
		role.setId(value);
		role.setName(value);
		roleRepository.save(role);
		
		value = Role.DEVELOPER;
		role.setId(value);
		role.setName(value);
		roleRepository.save(role);
		
		value = Role.OBJECT_PROVIDER;
		role.setId(value);
		role.setName(value);
		roleRepository.save(role);
		
		value = Role.SERVICE_PROVIDER;
		role.setId(value);
		role.setName(value);
		roleRepository.save(role);
		
	}

	
		@Override
	protected EntityResponseMessage postACCreateEntity(Event event)
			throws IdManagementException {
		
		CreateMembershipEvent membEvent = ((CreateMembershipEvent)event);
		Membership m = new Membership();
		List<Role> all = roleRepository.findAll();
		if(all.isEmpty())
			initializeRoles();
		List<Role> roles = roleRepository.findByName(membEvent.getMessage().getRole());
		if(roles.isEmpty())
			throw new IdManagementException("Role unexistent: There is no such role \""+membEvent.getMessage().getRole()+"\"",null, LOG,"Role not found with id ==\""+membEvent.getMessage().getRole()+"\" either the request is wrong, or the table Role doesn't contain the right roles, Principals: "+RestAuthentication.getBasicInfoPrincipals(membEvent.getPrincipals()),Level.ERROR, 403);
		
		Role role = roles.get(0);
		User u = userRepository.getOne(membEvent.getUserId());
		Group group = groupRepository.getOne(membEvent.getMessage().getGroup_id());
		
		for(Membership um: u.getMemberships())
			if(um.getRole().equals(role) && um.getGroup().equals(group))
			{
				if(um.isApprovedByGroupOwner()&&um.isApprovedByUser())
					throw new IdManagementException("User with id:"+u.getId()+" has already an approved membership with the same role and group.",null, LOG,"User with id:"+u.getId()+" has already an approved membership with the same role and group. Principals: "+RestAuthentication.getBasicInfoPrincipals(membEvent.getPrincipals()),Level.INFO, 409);
				else
					throw new IdManagementException("User with id:"+u.getId()+" has already a membership with the same role and group. It is waiting for approval from "+(um.isApprovedByGroupOwner()?" the user ":" the group owner/admin"),null, LOG,
							"User with id:"+u.getId()+" has already a membership with the same role and group. It is waiting for approval from "+(um.isApprovedByGroupOwner()?" the user ":" the group owner/admin")
							+"Principals: "+RestAuthentication.getBasicInfoPrincipals(membEvent.getPrincipals()),Level.INFO, 409);
			}
		
		m.setRole(role);		
		m.setApprovedByGroupOwner(membEvent.isExecutedByGroupOwner() || membEvent.isExecutedByGroupAdmin());
		m.setApprovedByUser(membEvent.isExecutedByUser());
		m.setGroup(group);
		m.setUser(u);
		m.setId(UUID.randomUUID().toString());
		membershipRepository.save(m);
		
		return new MembershipResponseMessage(m);
	}


	@Override
	protected EntityResponseMessage postACGetEntity(Event event)
			throws IdManagementException {
		
		GetUserEvent get = ((GetUserEvent) event);
		User u = userRepository.getOne(get.getId());
		UserResponseMessage res = new UserResponseMessage(u);
		return res;
		

	}

	@Override
	protected EntityResponseMessage postACUpdateEntity(DetailsIdEvent event, IEntity previous)
			throws IdManagementException {
		
		Collection<IPrincipal> principals = event.getPrincipals();
		ApproveMembershipEvent approve = ((ApproveMembershipEvent ) event);
		Membership attemptedMembership = membershipRepository.getOne(approve.getEntityId());
		IPrincipal prin = principals.iterator().next();
		boolean updated = false;
		if(prin instanceof ComposeUserPrincipal)
		{
			ComposeUserPrincipal userPrincipal = ((ComposeUserPrincipal)prin);
			//it is a membership that needs authorization from the principal 
			if(attemptedMembership.getUser().getId().equals(userPrincipal.getOpenId().getUser_id()) 
					&& !attemptedMembership.isApprovedByUser())
			{
				updated = true;
				attemptedMembership.setApprovedByUser(true);
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
				throw new IdManagementException("There was no pending approval from the principal calling the API",null, LOG,"Principal attempting to approve a membership that he didn't have to approve. Membership group"+attemptedMembership.getGroup().getId()
						+"Attempted membership role: "+attemptedMembership.getRole().getName()+", Attempted membership user: "+attemptedMembership.getUser().getId()+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 403);
		}
		else
			throw new IdManagementException("To approve a membership, the principal must be a user",null, LOG,"Principal attempting to approve a membership is not a user. princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 401);
	
		attemptedMembership.setLastModified(new Date( (new Date().getTime()/1000)*1000 ));
		attemptedMembership = membershipRepository.save(attemptedMembership);
		return new MembershipResponseMessage(attemptedMembership);
		
	}

	@Override
	protected void verifyAccessControlCreateEntity(Event event)
			throws IdManagementException {

		CreateMembershipEvent membEvent = ((CreateMembershipEvent)event);
		Collection<IPrincipal> principals = event.getPrincipals();
		for(IPrincipal p: principals)
		{
			if(p instanceof ComposeUserPrincipal)
			{
				ComposeUserPrincipal user = ((ComposeUserPrincipal)p);
				OpenIdUserData userData = user.getOpenId();
				if(userData.getUser_id().equals(membEvent.getUserId()))
					membEvent.setExecutedByUser(true);
				User u = userRepository.getOne(userData.getUser_id());
				for(Group g: u.getGroups())
					if(g.getId().equals(membEvent.getMessage().getGroup_id()))
							membEvent.setExecutedByGroupOwner(true);
				for(Membership m: u.getMemberships())
					if(m.getGroup().getId().equals(membEvent.getMessage().getGroup_id()) && m.getRole().getName().equals(Role.ADMIN))
							membEvent.setExecutedByGroupAdmin(true);
			}
		}
		if(!membEvent.isExecutedByGroupAdmin() && !membEvent.isExecutedByGroupOwner() && !membEvent.isExecutedByUser())
			throw new IdManagementException("Not sufficient permissions for the action requred : Attempt to add another user in a group where you are not admin nor the owner",null, LOG,"Attempt to add another user in a group where the principal is not admin nor owner. The entities authenticated for the request do not have sufficient permissions to execute it, principals "+RestAuthentication.getBasicInfoPrincipals(principals),Level.ERROR, 403);
		
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
	protected Logger getLogger() 
	{
		return LOG;
	}

	@Override
	protected IEntity getEntityById(String entityId) {
		return membershipRepository.getOne(entityId);
	}

	@Override
	protected void verifyAccessControlGetEntity(Event event)
			throws IdManagementException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object postACListAllEntities(Event event)
			throws IdManagementException {
		
		
		//this one is a map to prevent returning repeated approvals that may appear if a user is agroup owner and an admin at the same time
		HashMap<String, MembershipResponseMessage>   groupOwner = new HashMap<String, MembershipResponseMessage>()  ;
		List<MembershipResponseMessage> selfApprovals = new LinkedList<MembershipResponseMessage>();
		Collection<IPrincipal> principals = event.getPrincipals();
		
		for(IPrincipal p: principals)
		{
			if(p instanceof ComposeUserPrincipal)
			{
				ComposeUserPrincipal user = ((ComposeUserPrincipal)p);
				OpenIdUserData userData = user.getOpenId();
				User u = userRepository.getOne(userData.getUser_id());
				Collection<Group> groups = u.getGroups();
				
				for(Group group: groups)
					for(Membership m: membershipRepository.findByGroup(group))
						if(!m.isApprovedByGroupOwner())
							groupOwner.put(m.getId(),new MembershipResponseMessage(m));
				
				for(Membership principaMembership: u.getMemberships())
				{
					if(!principaMembership.isApprovedByUser())
						selfApprovals.add(new MembershipResponseMessage(principaMembership));
					
					if(principaMembership.isApprovedByGroupOwner() && principaMembership.isApprovedByUser() && principaMembership.getRole().getName().equals(Role.ADMIN))
					{	
						List<Membership> pendingAdmin = membershipRepository.findByGroup(principaMembership.getGroup());
						for(Membership pending: pendingAdmin)
							if(!pending.isApprovedByGroupOwner())
								groupOwner.put(pending.getId(), new MembershipResponseMessage(pending));	
					}
				}
					
			}
		}
		List<MembershipResponseMessage> group = new LinkedList<MembershipResponseMessage>();
		for(String key: groupOwner.keySet())
			group.add(groupOwner.get(key));
		
		return new PendingUserMembershipMessage(group, selfApprovals);
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
		
		Membership attempted = membershipRepository.getOne(event.getEntityId());
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
		
		Membership memb = membershipRepository.getOne(event.getEntityId());
		IPrincipal prin = principals.iterator().next();
		if(prin instanceof ComposeUserPrincipal)
		{
			if(!userCanDelete(memb, (ComposeUserPrincipal) prin))
				throw new IdManagementException("User does not have sufficient permissions to delete the membesrhip ",null, LOG,"Principal attempting to delete a membership without sufficient permissions. Membership group"+memb.getGroup().getId()
						+"membership role: "+memb.getRole().getName()+", membership user: "+memb.getUser().getId()+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 403);
		}
		else
			throw new IdManagementException("To approve a membership, the principal must be a user",null, LOG,"Principal attempting to approve a membership is not a user. princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 401);
	
		
		
		/*Collection<IPrincipal> principals = event.getPrincipals();
		Membership attempted = membershipRepository.getOne(event.getEntityId());
		boolean userThere = false;
		for(IPrincipal p: principals)
		{
			if(p instanceof ComposeUserPrincipal)
			{
				userThere = true;
				ComposeUserPrincipal user = ((ComposeUserPrincipal)p);
				OpenIdUserData userData = user.getOpenId();
				User u = userRepository.getOne(userData.getUser_id());
				
				if(attempted.getGroup().getOwner().getId().equals(u.getId()))
							return;//the principal owns the group where the membership is
				
				for(Membership principaMembership: u.getMemberships())
				{
					//it is a membership from the principal executing the action
					if(principaMembership.equals(attempted))
						return;
					
					if(principaMembership.isApprovedByGroupOwner() && principaMembership.isApprovedByUser() && principaMembership.getRole().getName().equals(Role.ADMIN))
					    if(attempted.getGroup().equals(principaMembership.getGroup()))
							return;// the membership coressponds to a group where the principal is admin	
				}
			}
		}
		if(!userThere)
			throw new IdManagementException("Authentication required. A user must execute this action",null, LOG," Attempt to delete a membership without authentication of a user first",Level.DEBUG, 401);
		throw new IdManagementException("Not sufficient permissions for the action requred : Attempt to delete a usermembership without sufficient permissions",null, LOG,"Attempt to delete a user membership where the user is neither admin of the group, nor owner, nor the user associated with the membership. groupId: "+attempted.getGroup().getId()+" user associated with the membership: "+attempted.getUser().getId()+" role for the membership: "+attempted.getRole().getName()+", principals "+RestAuthentication.getBasicInfoPrincipals(principals),Level.ERROR, 403);*/
	}

	/**
	 * 
	 * @param attemptedMembership membership that is attempting to be updated 
	 * @param userPrincipal principal executing the action 
	 * @return true if the membership requires the approval of the user as owner of the entity being added
	 */
	private boolean needsApprovalByUserAsOwner(Membership attemptedMembership, ComposeUserPrincipal userPrincipal)
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
	private boolean needsApprovalByUserAsGroupAdmin(Membership attemptedMembership, ComposeUserPrincipal userPrincipal)
	{
		User u = userRepository.getOne(userPrincipal.getOpenId().getUser_id());
		for(Membership principalMembership: u.getMemberships())
		{
			if(principalMembership.getGroup().getId().equals(attemptedMembership.getGroup().getId())
					&& principalMembership.getRole().getName().equals(Role.ADMIN)
					&& principalMembership.isApprovedByGroupOwner()
					&& principalMembership.isApprovedByUser()
					&& !attemptedMembership.isApprovedByGroupOwner()
			 )
			 {
				return true;
				
			 }
		}
		return false;		
	}
	
	private boolean userCanDelete(Membership membership, ComposeUserPrincipal user)
	{
		if(membership.getUser().getId().equals(user.getOpenId().getUser_id()))
			return true;
		//check if the user is admin of the group of the membership
		User u = userRepository.getOne(user.getOpenId().getUser_id());
		for(Membership principalMembership: u.getMemberships())
		{
			if(principalMembership.getGroup().getId().equals(membership.getGroup().getId())
					&& principalMembership.getRole().getName().equals(Role.ADMIN)
					&& principalMembership.isApprovedByGroupOwner()
					&& principalMembership.isApprovedByUser()
					
			 )
			 {
				return true;
				
			 }
		}
		// if it was not the user associated with the membership, and his is not an admin of the group, then verify if it is the group owner 
		return ( user.getOpenId().getUser_id().equals(membership.getGroup().getOwner().getId()) );
		
	}
}
